package olupis;

import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import olupis.content.*;
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
        if(cly() == null || cly().module() == null) return null;
        if(cly().module() != null) return cly().module().graph;
        return null;
    }

    public static void gphh(){
        if(gph() == null) return;
        for(Building b : gph().all){
            NyfalisFxs.acidRainDamage.at(b.x, b.y, 0, NyfalisColors.acidRainColour, b.block);
        }
    }

    public static void gg(){
        Log.err("===========");
        int[] umu = {0};
        Groups.all.each( e -> {
            if(e instanceof CalyxGraphUpdater ee){
                Log.err("- " + ee.id + " = " + ee.graph.getID() + " | " + ee.graph.all.size);
                umu[0]++;
            }
        });

        Log.err("==========="  + umu[0]);

    }

    public static Seq<CalyxGraph> ggg(){
        Seq<CalyxGraph> owo = new Seq<>();
        Groups.all.each( e -> {
            if(e instanceof CalyxGraphUpdater ee){
                owo.add(ee.graph);
            }
        });

        return owo;
    }

    public static void ggg(int index){

        for(Building b : ggg().get(index).all){
            NyfalisFxs.acidRainDamage.at(b.x, b.y, 0, NyfalisColors.acidRainColour, b.block);
        }
    }



}
