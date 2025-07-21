package olupis.world;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.async.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;

import static mindustry.Vars.*;

/** Yes, this class has race conditions and possibly memory leaks, cry about it */
public class EnvUpdater implements AsyncProcess{
    public static ObjectMap<String, ObjectSet<Block>> blacklists = new ObjectMap<>();

    public static byte[][] data = new byte[][]{};
    public static TaskQueue tasks = new TaskQueue();

    // array noting whether the tile is an instance of UpdatingEnvironment
    public static boolean[] infested = new boolean[]{};
    // array of original tile IDs
    public static short[][] replacementMap = new short[][]{};
    // list of tiles to set blocks on, per block type
    public static IntSeq[] queue = new IntSeq[]{};
    // block layer cache
    public static byte[] layer = new byte[]{};
    // prop count for various floors
    private static short[] props = new short[]{};

    // cache
    static Tile lookup;
    static boolean state;
    static int index;

    static final int csize = 3;
    static int space;
    int wsize;

    public static void load(){
        Events.on(EventType.ContentInitEvent.class, e ->
            Core.app.post(() -> {
                queue = new IntSeq[content.blocks().size];
                layer = new byte[content.blocks().size];

                for(int i = 0; i < queue.length; i++){
                    queue[i] = new IntSeq();

                    Block b = content.block(i);
                    layer[i] = (byte) (
                        b.isOverlay() ? 1
                        : b.isFloor() ? 0
                        : 2
                    );
                }
            })
        );
    }

    @Override
    public void init(){
        wsize = world.width() * world.height();

        props = new short[content.blocks().size];
        data = new byte[wsize][csize];
        infested = new boolean[wsize];
        replacementMap = new short[wsize][csize];

        for(int i = 0; i < wsize; i++){
            Tile tile = world.tiles.geti(i);

            // cleanup & setup
            if(tile.floor() instanceof UpdatingEnvironment e){
                if(!e.isValid(tile)){
                    tile.setFloorNet(e.replacement(), tile.overlay());
                    return;
                }

                replacementMap[i][0] = e.replacement().id;
            }else replacementMap[i][0] = -1;

            if(tile.overlay() instanceof UpdatingEnvironment e){
                if(!e.isValid(tile)){
                    tile.setOverlayNet(e.replacement());
                    return;
                }

                replacementMap[i][1] = e.replacement().id;
            }else replacementMap[i][1] = -1;

            if(tile.block() instanceof UpdatingEnvironment e){
                if(!e.isValid(tile)){
                    tile.setNet(e.replacement());
                    return;
                }

                replacementMap[i][2] = e.replacement().id;
            }else replacementMap[i][2] = -1;

            if(!tile.block().isStatic())
                space++;
        }

        for(int i = 0; i < queue.length; i++)
            queue[i].clear();
    }

    @Override
    public void process(){
        for(int i = 0; i < wsize; i++){
            lookup = world.tiles.geti(i);
            state = false;

            if(lookup.floor() instanceof UpdatingEnvironment e){
                e.updateEnv(lookup, i);
                state = true;
            }

            if(lookup.overlay() instanceof UpdatingEnvironment e){
                e.updateEnv(lookup, i);
                state = true;
            }

            if(lookup.block() instanceof UpdatingEnvironment e){
                e.updateEnv(lookup, i);
                state = true;
            }

            infested[i] = state;
        }
    }

    @Override
    public void end(){
        tasks.run();

        for(int i = 0; i < queue.length; i++){
            if(queue[i].size <= 0) continue;

            index = i;
            queue[index].chunked(200, t -> {
                switch(layer[index]){
                    case 0 -> Call.setTileFloors(content.block(index), t);
                    case 1 -> Call.setTileOverlays(content.block(index), t);
                    case 2 -> Call.setTileBlocks(content.block(index), Team.derelict, t);
                }
            });

            queue[i].clear();
        }
    }

    public static void addProp(int id){
        ++props[id];
    }

    public static boolean canSpawn(int id, int limit, float scaling){
        return props[id] < limit + (scaling * space);
    }

    public static Tile closestInfested(int x, int y, int radius, int height, int width){
        for(int dx = Math.max(x - radius, 0); dx <= Math.min(x + radius, width - 1); dx++)
            for(int dy = Math.max(y - radius, 0); dy <= Math.min(y + radius, height - 1); dy++)
                if(Mathf.within(dx, dy, x, y, radius) && infested[dx + dy * width])
                    return world.tile(dx, dy);
        return null;
    }

    //TODO: possibly broken, fix later on
    public static void resetTile(Tile tile){
        if(tile == null) return;

        int idx = tile.array(), pos = tile.pos();
        for(int i = 0; i < csize; i++){
            int id = replacementMap[idx][i];
            if(id >= 0)
                queue[id].addUnique(pos);
        }
    }

    public static ObjectSet<Block> blacklist(String key){
        return blacklists.get(key, ObjectSet::new);
    }

    public interface UpdatingEnvironment{
        void updateEnv(Tile tile, int i);

        boolean isValid(Tile tile);

        Block replacement();
    }
}
