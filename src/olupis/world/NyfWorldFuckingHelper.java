package olupis.world;

import arc.func.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import olupis.world.blocks.environment.*;

import static olupis.content.NyfalisBlocks.*;
import static olupis.content.NyfalisSectors.*;

public class NyfWorldFuckingHelper{
    /** @return whether any raycasted tiles match the filter */
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

    // no longer assumes the solids do not exist
    public static void placeSprigs(Tile t){
        if(t == null || (t.block() != Blocks.air && !t.block().alwaysReplace))
            return;

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
}
