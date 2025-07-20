package olupis.world;

import arc.func.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.world.*;

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
}
