package olupis.tmi.recipes;

import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.consumers.*;
import olupis.world.blocks.unit.*;
import org.jetbrains.annotations.*;
import tmi.recipe.*;
import tmi.recipe.parser.*;

public class UnitFabricatorRecipes extends RecipeParser<Fabricator>{


    @Override
    public boolean isTarget(@NotNull Block block){
        return block instanceof Fabricator;
    }

    @Override
    public void init(){
        getExcludes().add(ReconstructorParser.class);

        super.init();
    }

    @NotNull
    @Override
    public Seq<Recipe> parse(Fabricator content){

        Seq<Recipe> res = new Seq<>();
        for (UnitType[] upgrade : content.upgrades) {
            Recipe recipe = new Recipe(RecipeType.factory, getWrap(content), content.constructTime);

            recipe.addMaterialInteger(getWrap(upgrade[0]), 1);
            recipe.addProductionInteger(getWrap(upgrade[1]), 1);

            if(upgrade.length >= 3){
                for(int i = 2; i < upgrade.length; i++) recipe.addMaterialInteger(getWrap(upgrade[i]), 1);
            }

            nyfConsHelper(recipe,  content.consumers);

            res.add(recipe);
        }

        return res;
    }


    public void nyfConsHelper(Recipe recipe, Consume[] cons){
        for(Consume con : cons){
            if(con instanceof  ConsumeItems ci){
                for(ItemStack itemStack : ci.items) recipe.addMaterialInteger(getWrap(itemStack.item), itemStack.amount);
            }
            if(con instanceof  ConsumeItemFilter cf){
                for(Item item : Vars.content.items()) if(cf.filter.get(item)) recipe.addMaterialInteger(getWrap(item), 1);
            }
            if(con instanceof ConsumeLiquid cl) recipe.addMaterialFloat(getWrap(cl.liquid), cl.amount);
            if(con instanceof  ConsumeLiquidFilter cf) for(Liquid liquid : Vars.content.liquids()){
                if(cf.filter.get(liquid)) recipe.addMaterialFloat(getWrap(liquid), cf.amount);
            }


        }

    }

}
