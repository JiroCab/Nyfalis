package olupis.tmi.recipes;

import arc.struct.*;
import mindustry.world.*;
import olupis.world.blocks.defence.*;
import org.jetbrains.annotations.*;
import tmi.recipe.*;
import tmi.recipe.types.*;

public class PropellerCoreRecipes extends RecipeParser<PropellerCoreBlock>{

    @Override
    public boolean isTarget(@NotNull Block block){
        return block instanceof   PropellerCoreBlock;
    }

    @NotNull
    @Override
    public Seq<Recipe> parse(@NotNull PropellerCoreBlock factory){
        if(factory.spawns == null) return new Seq<>();
        Recipe recipe = new Recipe(RecipeType.factory, getWrap(factory), factory.unitTimer);
        recipe.addProductionInteger(getWrap(factory.spawns), factory.size);
        recipe.addMaterialFloat(PowerMark.INSTANCE, factory.unitPowerCost * 60);

        return Seq.with(recipe);
    }
}
