package olupis.world.data;

import arc.*;
import arc.struct.*;
import arc.util.*;
import olupis.*;
import olupis.world.blocks.processing.HeadacheCrafter.*;

import java.util.*;

public class CraftingPlansSaveIO{

    public static Seq<FactoryPlan>  allPlans = new Seq<>();


    public static void refreshCraftingPlans(){

        String out = Core.settings.getString("nyfalis-craftingPlansSave");
        if(out == null) return;
        String[] in = out.split(":");
        if(in.length <= 0 ) return;

        NyfalisMain.researchedPlans.clear();
        Log.err("load: " + out);
        for(int i = 0; i < in.length; i++){
            String p = in[i];
            NyfalisMain.researchedPlans = allPlans.copy();
            NyfalisMain.researchedPlans.retainAll(c -> Objects.equals(c.name, p));

        }


    }

    public static void  saveCraftingPlans(){
        //todo make this go through lists from nodes when added to the tree
        StringBuilder out = new StringBuilder();
        for(FactoryPlan plan : NyfalisMain.researchedPlans){
            out.append(plan.name).append(":");
        }
        Log.err("save: " + out);
        Log.err("all: " + allPlans.toString());
        Core.settings.put("nyfalis-craftingPlansSave", out.toString());




    }




}
