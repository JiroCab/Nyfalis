package olupis.tmi.recipes;

import arc.struct.*;
import arc.util.*;
import mindustry.type.*;
import mindustry.world.*;
import olupis.content.*;
import olupis.world.blocks.processing.*;
import org.jetbrains.annotations.*;
import tmi.*;
import tmi.recipe.*;
import tmi.recipe.parser.*;
import tmi.recipe.types.*;

import java.util.*;

public class HeadAcheRecipes extends RecipeParser<HeadacheCrafter>{

    @Override
    public boolean isTarget(@NotNull Block block){
        return block instanceof HeadacheCrafter;
    }

    @Override
    public void init(){
        getExcludes().add(GenericCrafterParser.class);

        super.init();
    }

    @NotNull
    @Override
    public Seq<Recipe> parse(HeadacheCrafter factory){
        Seq<Recipe> out = new Seq<>();
        for(FactoryPlan plan : factory.plans){
            Recipe recipe = new Recipe(RecipeType.factory, getWrap(factory), plan.time);

            if(plan.input != null)for(ItemStack st :  plan.input ) recipe.addMaterialInteger(getWrap(st.item), st.amount);
            if(plan.inputLiquid != null)for(LiquidStack st :  plan.inputLiquid ) recipe.addMaterialFloat(getWrap(st.liquid), st.amount);

            if(plan.output != null)for(ItemStack st :  plan.output ) recipe.addProductionInteger(getWrap(st.item), st.amount);
            if(plan.inputLiquid != null)for(LiquidStack st :  plan.outputLiquid ) recipe.addMaterialFloat(getWrap(st.liquid), st.amount);

            if(plan.powerIn >0) recipe.addMaterialFloat(PowerMark.INSTANCE, plan.powerIn * 60f);
            if(plan.powerOut >0)recipe.addProductionFloat(PowerMark.INSTANCE, plan.powerIn * 60f);

            out.add(recipe);
        }

        return out;
    }
}
