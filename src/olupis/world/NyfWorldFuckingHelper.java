package olupis.world;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.style.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.Units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.weather.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import olupis.*;
import olupis.content.*;
import olupis.world.EnvUpdater.*;
import olupis.world.blocks.calyx.*;
import olupis.world.blocks.environment.*;

import static arc.Core.camera;
import static mindustry.Vars.*;
import static olupis.NyfalisVars.*;
import static olupis.content.NyfalisBlocks.*;
import static olupis.content.NyfalisSectors.*;

public class NyfWorldFuckingHelper{
    public static final ObjectIntMap<Block> spreadLevels = new ObjectIntMap<>();

    //region == Gameplay Helpers ==
    /** returns whether any raycasted tiles match the filter */
    public static boolean rayCheck(Healthc build, Posc entity, Func<Block, Boolean> filter){
        Seq<Block> set = new Seq<>(false);

        World.raycastEach(build.tileX(), build.tileY(), entity.tileX(), entity.tileY(), (x, y) -> { 
            Tile tile = Vars.world.tile(x, y);

            if(tile != null && tile.build != build && !tile.within(build, (build.blockOn().size) * Vars.tilesize))
                set.add(tile.block());

            return false;
        });

        return set.contains(filter::get);
    }

    public static float withinMouseOrUnitRangeF(Position tar, float range){
        float out = 1;
        if(Vars.player.unit() != null && Vars.player.unit().within(tar, range)) out = Mathf.lerp(0.1f, 1, tar.dst(Vars.player.unit()) / range);

        Vec2 mouse = Core.input.mouseWorld(Core.input.mouseX(), Core.input.mouseY());
        Tile t = Vars.world.tileWorld(mouse.x, mouse.y);

        if(t != null && t.within(tar, range)) out = Math.min(out, Mathf.lerp(0.1f, 1, tar.dst(t) / range));

        return out;
    }

    public static boolean withinMouseOrUnitRange(Position tar, float range){
        return withinMouseOrUnitRangeF(tar, range) > 0;
    }

    public static Building nearby( Building build, int rotation) {
        switch (rotation) {
            case 0:
                build = Vars.world.build(build.tileX() + build.block.size, build.tileY());
                break;
            case 1:
                build = Vars.world.build(build.tileX(), build.tileY() + build.block.size);
                break;
            case 2:
                build = Vars.world.build(build.tileX() - build.block.size, build.tileY());
                break;
            case 3:
                build = Vars.world.build(build.tileX(), build.tileY() - build.block.size );
                break;
            default:
                build = null;
        }

        return build;
    }

    //Yes all this just to remove `inFogTo()`
    public static Unit bestEnemyFog(Team team, float x, float y, float range, Boolf<Unit> predicate, Sortf sort){
        if(team == Team.derelict) return null;

        Unit[] result = {null};
        float[] in = {0f, -99999f};

        Units.nearbyEnemies(team, x - range, y - range, range*2f, range*2f, e -> {
            if(e.dead() || !predicate.get(e) || e.team == Team.derelict || !e.within(x, y, range + e.hitSize/2f) || !e.targetable(team)) return;

            float cost = sort.cost(e, x, y);
            if((result == null || cost < in[0] || e.type.targetPriority > in[1]) && e.type.targetPriority >= in[1]){
                result[0] = e;
                in[0] = cost;
                in[1] = e.type.targetPriority;
            }
        });

        return result[0];
    }

    public static void youPickedTheWrongCoreLmao(){
        if(!NyfalisPlanets.planetList.contains(state.rules.planet)) return;

        Building build = state.rules.defaultTeam.core();
        if(build != null && !NyfalisBlocks.nyfalisCores.contains(build.block)){;
            build.tile.setNet(coreRemnant, state.rules.defaultTeam, 0);
            //idk i cant get it to work so todo
            Time.run( 20f, () -> {
                Building build2 = state.rules.defaultTeam.core();
                Call.effect(Fx.impactReactorExplosion, build.tile.centerX(), build.tile.centerY(), 0, Color.white);

                //skil issh
                build2.items.clear();
            });

        }

    }
    //endregion
    // region == Weather helpers

    // no longer assumes the solids do not exist
    public static void growSprigs(Tile t){
        if(t == null || (t.build != null) ||(t.block() != Blocks.air && (!t.block().alwaysReplace))) return;

        if(t.block() instanceof RotatingProp sp && sp.replacement != null && sp.replacement != Blocks.air){
            t.setNet(sp.replacement);
            Fx.breakProp.at(t);
            return;
        }

        Floor fl = t.floor();
        t.setNet(
            fl.liquidDrop == Liquids.water ? glowSprig :
            mossGreenAll.contains(fl) ? mossSprig :
            mossYellow.contains(fl) ? yellowSprig :
            grasses.contains(fl) ? grassSprig :
            luma.contains(fl) ? lumaSprig :
            deadBush
        );
    }


    /** Gets the generated ore variant from the given SpreadingFloor
     * @return The ore variant if found, the base ore if missing */
    public static Block spreadingOreVariant(Block floor, OreBlock ore){
        if(floor instanceof SpreadingOverlay f)
            return f.replacements.get(ore, ore);
        return ore;
    }

    //endregion
    // region == Planet gen helpers==
    public static void  noiseColourRaw(float noise, Block block, Color out){
        if(mossGreenAll.contains(block)) noiseColour(noise, mossGreen, out, block);
        else if(grassesAll.contains(block)) noiseColour(noise, grasses, out, block);
        else if(waters.contains(block)) noiseColour(noise, waters, out, block);
        else if(soils.contains(block)) noiseColour(noise, soils, out, block);
        else out.set(block.mapColor).a(1f - block.albedo);
    }

    public static void noiseColour(float noise, Seq<Block> in, Color out, Block block){
        int c = (int)Mathf.lerp(0, in.size, noise);
        Block bl = in.get(c);

        if(soils.contains(bl)) out.set(bl.mapColor).lerp(mossGreen.random().mapColor, 0.5f).a(1f - bl.albedo);
        else out.set(bl.mapColor).a(1f - block.albedo);
    }

    public static Block getVentEqv(Block blk){
        if(blk instanceof SteamVent) return blk;
        if(mossGreenAll.contains(blk)) return mossyVent;
        if(blk == Blocks.dirt) return dirtVent;
        if(blk == hardenMud) return hardenMuddyVent;
        if(blk == Blocks.grass) return grassyVent;
        if(blk == Blocks.snow || blk == Blocks.iceSnow) return snowVent;
        if(blk == redSand || blk == redSandSnow) return redSandVent;

        return Blocks.arkyicVent;
    }

    //endregion
    // region  == Rendering helpers ==
    private static Vec2 vector = new Vec2();

    public static void spikesTri(float x, float y, float radius, float length, int spikes, float rot, float width){
        spikesTri(x, y, radius, length, spikes, rot, width, 0);
    }

    public static void spikesTri(float x, float y, float radius, float length, int spikes, float rot, float width, float size){
        vector.set(0, 1);
        float step = (360f / spikes) + size;

        for(int i = 0; i < spikes; i++){
            vector.trns(i * step + rot, radius);
            float x1 = vector.x, y1 = vector.y;
            vector.setLength(radius + length);

            Drawf.tri(x + x1, y + y1, width, length, Angles.angle(x, y, x + x1, y + y1));
        }
    }

    public static void  renderConfigIndicator(Building build, float scaleIn, Seq<TextureRegion> seq){
        if(turretConfigIndicator == 0 || build.team !=  player.team()) return;
        float pz = Draw.z();

        Block block = build.block;
        //yeah yeah tell me this is bad, i just cant be assed, if  you think its that bad make a PR already
        int xm =
            turretConfigIndicator == 1 || turretConfigIndicator == 4 || turretConfigIndicator == 7 ? -1 :
            turretConfigIndicator == 2 || turretConfigIndicator == 5||  turretConfigIndicator == 8 ? 1:
            0;
        int ym =
            turretConfigIndicator >= 1 && turretConfigIndicator <= 3 ? 1 :
            turretConfigIndicator >= 4 && turretConfigIndicator <= 6 ? -1 :
            0;
        float multiplier = build.block.size > 1 ? 1.0F : 0.64F;
        float bs = (block.size * 8) / 2.0F;
        float brcx = build.x + (bs * xm) + ( 8f * (multiplier /2 * -xm));
        float brcy = build.y + (bs * ym) + ( 8f * (multiplier /2* -ym));
        float scale = scaleIn * (  turretConfigScale != 0 ? turretConfigScale * 0.05f : (Vars.mobile || testMobile) ? 0.6f : 0.85f);

        Draw.z(71.0F);
        Draw.color(Color.white);
        Draw.scl(scale);

        for(int i = 0; i < seq.size; i++){
            if(!seq.get(i).found()) continue;
            //((offset * 2 * scale) + 1)
            float xf = ( 8f * (multiplier /2 * Mathf.clamp(-xm, -1, 1)) * i * 2  * scale +1);
            Draw.rect(seq.get(i), brcx + xf , brcy);
        }
        Draw.reset();
        Draw.z(pz);
    }

    public static void renderConfigIndicator(Building build, TextureRegion tex){
        renderConfigIndicator(build, 1, Seq.with(tex));
    }

    public static void renderConfigIndicator(Building build, Seq<TextureRegion> tex){
        renderConfigIndicator(build, 1, tex);
    }

    public static void renderConfigIndicator(Building build, float scl, TextureRegion tex){
        renderConfigIndicator(build, scl, Seq.with(tex));
    }

    //endregion
    //region == Ambience Helper ==

    public static void allUpdaters(){
        if(state.isPaused() || state.isEditor() || ui.editor.isShown())
            return;

        if(worldFuckeryTimer <= 0) worldFuckeryTimer = 240;
        worldFuckeryTimer--;

        transgenderTreeLeaves();
        updateFloodPlane();
        updateFlows();
        updateCalyxTiles();
    }

    public static void restUpdaters(){
        trees = new Seq<>();
        flows = new Seq<>();

        floodPlaneLevel = 0.50f;
    }

    public static void updateFloodPlane(){

        if(Groups.weather.isEmpty()){
            floodPlaneLevel = Mathf.lerpDelta(floodPlaneLevel, 0.0f, 0.0025f);
            return;
        }


        if(Groups.weather.contains(w -> w.weather instanceof RainWeather)){
            int cnt = 0;
            float avrg = 0f;

            for(int i = 0; i < Groups.weather.size(); i++){
                WeatherState w = Groups.weather.index(i);

                if(w.weather instanceof RainWeather){
                    cnt++;
                    avrg += w.intensity;
                }
            }

            floodPlaneLevel = Mathf.lerpDelta(floodPlaneLevel, Math.max(0.0f, avrg/cnt), 0.0025f);
        }
    }

    static Seq<Tile> trees  = new Seq<>();
    public static void transgenderTreeLeaves(){
        if(!renderer.enableEffects || !Mathf.randomBoolean(0.1f)) return;

        if(worldFuckeryTimer % 120 == 0){

            for(int i = 0; i < world.width() * world.height(); i++)
                if(world.tiles.geti(i).block() instanceof TrasngenderTreeBlock tg && tg.leaf && world.tiles.geti(i).staticDarkness() < 5)
                    trees.add(world.tiles.geti(i));
        }

        Tile tree = trees.random();
        if(tree != null){
            if(Mathf.randomBoolean()) NyfalisFxs.envTransgenderTreeLeafEffect.at(tree.x * tilesize, tree.y * tilesize, tree.block().mapColor);
            else NyfalisFxs.envTransgenderTreeLeafEffectUnder.at(tree.x * tilesize, tree.y * tilesize, tree.block().mapColor);
        }
    }

    public static Seq<IntTile> flows = new Seq<>();
    public static void updateFlows(){
        if(!renderer.enableEffects) return;

        if(worldFuckeryTimer % 240 == 0){

            for(int i = 0; i < world.width() * world.height(); i++){
                Tile tile = world.tiles.geti(i);

                if(tile.floor() instanceof FlowWaterTile ft && (tile.block() == Blocks.air || !tile.block().solid) && !IntTile.containsTile(flows, tile))
                    flows.add(new IntTile(tile, ft.effectSpacing));
            }
        }

        flows.each(entry -> {
            if(entry.integer <= 0){
                if(entry.tile.floor() instanceof FlowWaterTile ft){
                    ft.effect.at(entry.tile.x * tilesize, entry.tile.y * tilesize, entry.tile.extraData);
                    entry.integer = ft.effectSpacing;
                }else flows.remove(entry);

            }else entry.integer--;
        });
    }

    public static void  allDrawers(){
        cloudShadowDrawer();
    }

    public  static void cloudShadowDrawer(){
        if( NyfalisVars.nyfalianPlanet){
            if(!Core.settings.getBool("nyfalis-cloud-shadows")) return;
        } else {
            if(!Core.settings.getBool("nyfalis-cloud-shadows-others")) return;
        }

        if(cloudNoise == null){
            cloudNoise = Core.assets.get("sprites/clouds.png", Texture.class);
            cloudNoise.setWrap(Texture.TextureWrap.repeat);
            cloudNoise.setFilter(Texture.TextureFilter.linear);
        }

        final float[] sspeed = {1f}, sscl = { 1f }, salpha = { 1f }, offset = { 0f };
        Color col = Tmp.c1.set(Color.grays(0.1f));
        Draw.z(Layer.weather - 2f);
        for(int i = 0; i < 3; i++){
            Weather.drawNoise(cloudNoise, Color.grays(0.1f), 1100f * sscl[0], salpha[0] * 0.27f, sspeed[0] *  0.035f, 1, 1.1f, 0.5f, offset[0]);
            sspeed[0] *= 2;
            salpha[0] *= 0.4f;
            sscl[0] *= 2;
            offset[0] += 0.29f;
            col.mul(1);
        }
        Draw.reset();
    }

    public static void initLevelMap(){
        Seq<Block> keys = spreadLevels.keys().toArray();
        keys.each(b -> {
            if(b instanceof SpreadingOverlay overlay){
                keys.add(overlay.ores.first());
            }
        });

        for(int i = 0; i < keys.size; i++){
            if(keys.get(i) instanceof SpreadingOverlay entity){
                Block current = entity;
                int level = 1;

                while(current instanceof SpreadingOverlay object){
                    current = object.next;
                    spreadLevels.put(object, level++);
                }

                if(current != null && spreadLevels.get(current, -1) < 0)
                    spreadLevels.put(current, level);
            }

            if(keys.get(i) instanceof SpreadingOre entity){
                Block current = entity;
                int level = 1;

                while(current instanceof SpreadingOre object){
                    current = object.next;
                    spreadLevels.put(object, level++);
                }

                if(current != null && spreadLevels.get(current, -1) < 0)
                    spreadLevels.put(current, level);
            }
        }
    }

    public static class IntTile{
        public final Tile tile;
        public int integer;

        IntTile(Tile tile, int integer){
            this.tile = tile;
            this.integer = integer;
        }

        public static boolean containsTile(Seq<IntTile> data, Tile t){
            for(int i = 0; i < data.size; i++)
                if(data.get(i).tile == t)
                    return true;
            return false;
        }
    }

    public static Color calyxSpeciesColors(int i){
        return   Seq.with(Color.green, Color.pink, Pal.graphiteAmmoBack).get(i);
    }

    public static TextureRegionDrawable calyxSpeciesICon(int i){
        return  Seq.with(Icon.cancel, Icon.tree, Icon.terrain).get(i);
    }

    public static void updateCalyxTiles(){
        if(!(worldFuckeryTimer % 60 == 0))return;
        camera.bounds(Tmp.r1);
        EnvUpdater.eachInfested(Tmp.r1.x,  Tmp.r1.y, Math.max(Tmp.r1.width, Tmp.r1.height), 0, t -> {
            if(t.floor() instanceof UpdatingEnvironment e) e.lazyEnv(t);
            if(t.block() instanceof UpdatingEnvironment e) e.lazyEnv(t);
            if(t.overlay() instanceof UpdatingEnvironment e) e.lazyEnv(t);
        });
    }
    //endregion
}
