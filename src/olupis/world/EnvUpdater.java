package olupis.world;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import olupis.world.blocks.environment.*;

import java.util.Arrays;

import static mindustry.Vars.*;

public class EnvUpdater{
    public static class OreUpdateEvent{};

    public static final ObjectSet<Block> generated = new ObjectSet<>();
    public static int completed = 0;

    private static final Seq<Tile> tiles = new Seq<>(), dormantTiles = new Seq<>();
    public static short[][][] data = {}, replaced = {};
    private static Timer.Task validator;
    private static int timer;

    public static void load(){
        Log.info("EnvUpdater loaded");
        SaveVersion.addCustomChunk("envupdater-data", new EnvSaveIO());

        Events.on(OreUpdateEvent.class, e -> {
            var set = content.blocks().select(b -> b instanceof SpreadingFloor);
            if(completed >= set.size)
                set.each(t -> ((SpreadingFloor) t).addGenerated(generated));
        });

        Events.on(EventType.ResetEvent.class, e -> {
            data = null;
            replaced = null;

            dormantTiles.clear();
            tiles.clear();
        });
        Events.on(EventType.WorldLoadEvent.class, e -> {
            if(net.client() || state.isEditor()) return;
            Log.info("Creating world snapshot for EnvUpdater");

            if(data == null || replaced == null){
                data = replaced = new short[world.width()][world.height()][4];
                for(int x = 1; x < world.width(); x++)
                    for(int y = 1; y < world.height(); y++)
                        Arrays.fill(replaced[x][y], (short) -1);
            }

            timer = 0;
            world.tiles.eachTile(t -> {
                var floor = t.floor() instanceof SpreadingFloor f ? f : t.overlay() instanceof SpreadingFloor f ? f : null;
                var ore = t.overlay() instanceof SpreadingOre f ? f : null;
                var wall = t.block() instanceof GrowingWall w ? w : null;

                if(floor != null || ore != null || wall != null){
                    tiles.add(t);

                    if(floor != null && floor.overlay){
                        Seq<Floor> tmp = new Seq<>();
                        for(int i = 0; i <= 3; i++){
                            Tile nearby = t.nearby(i);
                            if(nearby != null && nearby.floor() != null && !(nearby.floor() instanceof SpreadingFloor sf && sf.overlay))
                                tmp.add(nearby.floor());
                        }
                        t.setFloorNet(tmp.isEmpty() ? Blocks.stone : tmp.random(), floor);
                    }
                }
            });

            Log.info("Snapshot created, " + (tiles.size) + " tiles to update");

            if(validator == null || !validator.isScheduled())
                validator = Timer.schedule(() -> {
                    if(!state.isGame() || state.isEditor() || state.isPaused()) return;

                    updateSpread();
                    if(timer++ >= 10){
                        updateDormant();
                        timer = 0;
                    }
                }, 0, 1);
        });
    }

    private static void updateSpread(){
        var it = tiles.iterator();
        while(it.hasNext()){
            Tile tile = it.next();
            if(tile == null) continue;

            int iter = 0, complete = 0;
            var floor = tile.floor() instanceof SpreadingFloor f ? f : null;
            if(updateStatus(floor, tile, iter)) ++complete;

            ++iter;
            var overlay = tile.overlay() instanceof SpreadingFloor f ? f : null;
            if(updateStatus(overlay, tile, iter)) ++complete;

            ++iter;
            var ore = tile.overlay() instanceof SpreadingOre f ? f : null;
            if(ore != null && ((ore.set != null && tile.floor() != ore.set) || ore.next != null || canSpread(tile, ore.parent.spreadOffset, ore.parent.blacklist))){
                if(Mathf.chance(ore.parent.spreadChance)) ++data[tile.x][tile.y][iter];

                if(data[tile.x][tile.y][iter] >= ore.parent.spreadTries){
                    data[tile.x][tile.y][iter] = 0;

                    if(replaced[tile.x][tile.y][iter] <= 0)
                        replaced[tile.x][tile.y][iter] = tile.overlay().id;
                    if(ore.next != null)
                        tile.setFloorNet(tile.floor(), ore.next);
                    if(ore.set != null)
                        tile.setFloorNet(ore.set, ore);

                    Seq<Tile> nearby = getNearby(tile, ore.parent.spreadOffset, ore.parent.blacklist);
                    if(!nearby.isEmpty()){
                        if(ore.parent.fullSpread){
                            for(Tile t : nearby)
                                spreadOre(ore, t, iter);
                        }else spreadOre(ore, nearby.random(), iter);
                    }
                }
            }else ++complete;

            ++iter;
            var wall = tile.block() instanceof GrowingWall w ? w : null;
            if(wall != null){
                if(Mathf.chance(wall.growChance)) ++data[tile.x][tile.y][iter];

                if(data[tile.x][tile.y][iter] >= wall.growTries){
                    data[tile.x][tile.y][iter] = 0;

                    if(wall.growEffect != null)
                        Call.effect(wall.growEffect, tile.worldx(), tile.worldy(), 0, Color.clear);
                    tile.setNet(wall.next);
                }
            }else ++complete;

            if(complete >= 4){
                it.remove();
                dormantTiles.add(tile);
            }
        }
    }

    private static void updateDormant(){
        Log.info(Strings.format("Tiles: @ (@ active, @ dormant)", tiles.size + dormantTiles.size, tiles.size, dormantTiles.size));

        var it = dormantTiles.iterator();
        while(it.hasNext()){
            Tile t = it.next();

            var floor = t.floor() instanceof SpreadingFloor f ? f : null;
            var overlay = t.overlay() instanceof SpreadingFloor f ? f : null;
            var ore = t.overlay() instanceof SpreadingOre o ? o : null;

            if(floor == null && overlay == null && ore == null){ // tiles like these do not need re-instancing, so we remove them
                it.remove();
                continue;
            }

            boolean replaced = true;
            if(ore != null){
                if(ore.parent.drillEfficiency < 1 && t.build instanceof Drill.DrillBuild d)
                    d.applySlowdown(ore.parent.drillEfficiency, 660);

                Seq<Tile> check = getNearby(t, ore.parent.spreadOffset, ore.parent.blacklist);

                if(!check.isEmpty()){
                    for(Tile tile : check){
                        if(tile.floor() != ore.set){
                            replaced = false;
                            break;
                        }
                    }
                }
            }

            var op = floor == null ? overlay : floor;
            if(op != null && !getNearby(t, op.spreadOffset, op.blacklist).isEmpty())
                replaced = false;

            if(replaced) continue;

            it.remove();
            tiles.add(t);
        }
    }

    public static void debugUpdateActive(){
        updateSpread();
        updateDormant();
    }

    private static boolean updateStatus(SpreadingFloor var, Tile tile, int iter){
        if(var != null && (canGrow(var, tile) || canSpread(tile, var.spreadOffset, var.blacklist))){
            if(Mathf.chance(var.spreadChance)) ++data[tile.x][tile.y][iter];

            if(data[tile.x][tile.y][iter] >= var.spreadTries){
                data[tile.x][tile.y][iter] = 0;

                if(var.next != null){
                    if(var.upgradeEffect != null)
                        Call.effect(var.upgradeEffect, tile.worldx(), tile.worldy(), 0, Color.clear);

                    var next = var.next instanceof SpreadingFloor s ? s : null;
                    boolean isOverlay = next != null ? next.overlay : var.next.isOverlay();

                    if(isOverlay) tile.setOverlayNet(var.next);
                    else tile.setFloorNet(var.next, tile.overlay());
                }

                if(var.set != null){
                    Seq<Tile> nearby = getNearby(tile, var.spreadOffset, var.blacklist);
                    if(nearby.isEmpty()) return false;

                    if(var.fullSpread){
                        for(Tile t : nearby)
                            spreadFloor(var, t, iter);
                    }else spreadFloor(var, nearby.random(), iter);
                }
            }

            return false;
        }

        return true;
    }

    private static void spreadFloor(SpreadingFloor floor, Tile tile, int iter){
        if(floor.spreadEffect != null)
            Call.effect(floor.spreadEffect, tile.worldx(), tile.worldy(), 0, Color.clear);
        if(floor.spreadSound != null)
            Call.soundAt(floor.spreadSound, tile.worldx(), tile.worldy(), 0.6f, 1f);

        tiles.add(tile);
        if(replaced[tile.x][tile.y][iter] < 0)
            replaced[tile.x][tile.y][iter] = iter == 0 ? tile.floor().id : tile.overlay().id;

        if(iter == 0) tile.setFloorNet(floor.replacements.containsKey(tile.floor()) ? floor.replacements.get(tile.floor()) : floor.set, floor.replacements.containsKey(tile.overlay()) ? floor.replacements.get(tile.overlay()) : tile.overlay());
        else tile.setOverlayNet(floor.replacements.containsKey(tile.overlay()) ? floor.replacements.get(tile.overlay()) : floor.set);
        if(floor.replacements.containsKey(tile.block())){
            if(replaced[tile.x][tile.y][3] < 0)
                replaced[tile.x][tile.y][3] = tile.block().id;
            tile.setNet(floor.replacements.get(tile.block()));
        }
    }

    private static void spreadOre(SpreadingOre ore, Tile tile, int iter){
        if(ore.parent.replacements.containsKey(tile.overlay())){
            if(ore.parent.spreadEffect != null)
                Call.effect(ore.parent.spreadEffect, tile.worldx(), tile.worldy(), 0, Color.clear);
            if(ore.parent.spreadSound != null)
                Call.soundAt(ore.parent.spreadSound, tile.worldx(), tile.worldy(), 0.6f, 1f);

            tiles.add(tile);
            if(replaced[tile.x][tile.y][iter] < 0)
                replaced[tile.x][tile.y][iter] = tile.overlay().id;

            tile.setOverlayNet(ore.parent.replacements.get(tile.overlay()));
            if(ore.parent.replacements.containsKey(tile.block())){
                if(replaced[tile.x][tile.y][3] < 0)
                    replaced[tile.x][tile.y][3] = tile.block().id;
                tile.setNet(ore.parent.replacements.get(tile.block()));
            }
        }else spreadFloor(ore.parent, tile, ore.parent.overlay ? 1 : 0);
    }

    private static boolean canSpread(Tile tile, int radius, ObjectSet<Block> blacklist){
        return !getNearby(tile, radius, blacklist).isEmpty();
    }

    private static boolean canGrow(SpreadingFloor var, Tile tile){
        return var.next != null && (var.next instanceof SpreadingFloor next ? next.overlay ? tile.overlay() != next : tile.floor() != next : var.next.isOverlay() ? tile.overlay() != var.next : tile.floor() != var.next);
    }

    private static Seq<Tile> getNearby(Tile tile, int radius, ObjectSet<Block> blacklist){
        Seq<Tile> tiles = new Seq<>();
        if(tile.block().isStatic())
            return tiles;
        Tile t = null;

        if(radius <= 0)
            for(int i = 0; i <= 3; i++){ // linear
                t = tile.nearby(i);
                if(t != null && !(blacklist.contains(t.floor()) || blacklist.contains(t.overlay())))
                    tiles.add(t);
            }
        else
            tile.circle(radius, tmp -> { // random
                if(tmp != null && !(blacklist.contains(tmp.floor()) || blacklist.contains(tmp.overlay())))
                    tiles.add(tmp);
            });

        return tiles;
    }
}
