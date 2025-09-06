package olupis.world;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import olupis.world.blocks.environment.*;

import static olupis.content.NyfalisBlocks.*;
import static olupis.content.NyfalisSectors.*;

public class NyfWorldFuckingHelper{
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

    //endregion
    // region == Weather helpers

    // no longer assumes the solids do not exist
    public static void placeSprigs(Tile t){
        if(t == null || (t.block() != Blocks.air && !t.block().alwaysReplace)) return;

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



    public static void growSprigs(Tile t){
        if(t.block() instanceof SprigProp sp){
            t.setNet(sp.replacement);
            Fx.breakProp.at(t);
        }else Log.err(t + " is not Sprig(prop)!");
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

    public static void spikesTri(float x, float y, float radius, float length, int spikes, float rot, float width, float offset){
        vector.set(0, 1);
        float step = (360f / spikes) + offset;

        for(int i = 0; i < spikes; i++){
            vector.trns(i * step + rot, radius);
            float x1 = vector.x, y1 = vector.y;
            vector.setLength(radius + length);

            Drawf.tri(x + x1, y + y1, width, length, Angles.angle(x, y, x + x1, y + y1));
        }
    }
    //engregion
}
