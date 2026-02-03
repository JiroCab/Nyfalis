package olupis;

import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import olupis.world.blocks.calyx.ineternal.*;

public class NYF{
    //Just a quick console helper class for debugging and development.bc rushie lazy

    public static @Nullable Building bu(){
        return Vars.player.buildOn();
    }

    public static @Nullable Floor fl(){
        return Vars.player.floorOn();
    }
    public static @Nullable Block bl(){
        return Vars.player.blockOn();
    }
    public static @Nullable Block ov(){
        return Vars.player.tileOn().overlay();
    }

    public static @Nullable Calyxian cly(){
        if(Vars.player.buildOn() instanceof Calyxian c) return c;
        else return null;
    }

    public static @Nullable CalyxGraph gph(){
        if(cly().module() != null) return cly().module().graph;
        return null;
    }



}
