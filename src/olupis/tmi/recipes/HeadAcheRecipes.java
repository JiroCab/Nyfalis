package olupis.tmi.recipes;

import arc.struct.*;
import mindustry.type.*;
import mindustry.world.*;
import olupis.content.*;
import olupis.world.blocks.processing.*;
import org.jetbrains.annotations.*;
import tmi.recipe.*;

public class HeadAcheRecipes extends RecipeParser<HeadacheCrafter>{


    @Override
    public boolean isTarget(@NotNull Block block){
        return block instanceof HeadacheCrafter;
    }


    @Override
    public Seq<Recipe> parse(HeadacheCrafter factory){
        Seq<Recipe> out = new Seq<>();
        for(FactoryPlan plan : factory.plans){
            Recipe recipe = new Recipe(RecipeType.factory, getWrap(factory), plan.time);

            if(plan.input != null)for(ItemStack st :  plan.input ) recipe.addMaterial(getWrap(st.item), st.amount);
            if(plan.inputLiquid != null)for(LiquidStack st :  plan.inputLiquid ) recipe.addMaterial(getWrap(st.liquid), st.amount);

            if(plan.output != null)for(ItemStack st :  plan.output ) recipe.addProduction(getWrap(st.item), st.amount);
            if(plan.inputLiquid != null)for(LiquidStack st :  plan.outputLiquid ) recipe.addMaterial(getWrap(st.liquid), st.amount);

            if(plan.powerOut >0) recipe.addProduction(getWrap(NyfalisItemsLiquid.powerAmmoItem), plan.powerOut * 60);
            if(plan.powerIn >0) recipe.addMaterial(getWrap(NyfalisItemsLiquid.powerAmmoItem), plan.powerIn * 60f);

            out.add(recipe);
        }

        return out;
    }
}
