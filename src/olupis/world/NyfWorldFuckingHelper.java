package olupis.world;

import arc.func.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.world.*;
import olupis.world.blocks.environment.*;

import static olupis.content.NyfalisBlocks.*;
import static olupis.content.NyfalisBlocks.deadBush;
import static olupis.content.NyfalisBlocks.glowSprig;
import static olupis.content.NyfalisBlocks.grassSprig;
import static olupis.content.NyfalisBlocks.lumaSprig;
import static olupis.content.NyfalisSectors.*;

public class NyfWorldFuckingHelper{
    /** returns whether any raycasted tiles match the filter */
    public static boolean rayCheck(Healthc build, Posc entity, Func<Block, Boolean> filter){
        Seq<Block> set = new Seq<>(false);

        World.raycastEach(build.tileX(), build.tileY(), entity.tileX(), entity.tileY(), (x, y) -> { 
            Tile tile = Vars.world.tile(x, y);

            if(tile != null && tile.build != build && !tile.within(build, (build.blockOn().size) * Vars.tilesize))
                set.add(tile.block());


            return false;
        });

        Log.err(set.toString());
        return set.contains(filter::get);
    }

    //Assumes that theres no solids
    public  static void placeSprigs(Tile t){
        Block fl = t.floor();
        if(mossGreenAll.contains(fl)) t.setNet(mossSprig);
        else if(mossYellow.contains(fl)) t.setNet(yellowSprig);
        else if(grasses.contains(fl)) t.setNet(grassSprig);
        else if(luma.contains(fl)) t.setNet(lumaSprig);
        else if(t.floor().liquidDrop == Liquids.water) t.setNet(glowSprig);
        else if(Mathf.randomBoolean(0.01f))t.setNet(deadBush);
    }

    public static void growSprigs(Tile t){
        if(t.block() instanceof SprigProp sp ){
            t.setNet(sp.replacement);
            Fx.breakProp.at(t);
        }else Log.err(t + " is not  Sprig(prop)!" );
    }
}
