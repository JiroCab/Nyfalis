package olupis.tmi.recipes;

import arc.struct.*;
import mindustry.world.*;
import olupis.content.*;
import olupis.world.blocks.defence.*;
import olupis.world.blocks.unit.*;
import org.jetbrains.annotations.*;
import tmi.recipe.*;
import tmi.recipe.types.*;
import tmi.util.*;

public class MechPadRecipe extends RecipeParser<MechPad>{

    @Override
    public boolean isTarget(@NotNull Block block){
        return block instanceof MechPad;
    }

    @NotNull
    @Override
    public Seq<Recipe> parse(@NotNull MechPad factory){
        Recipe recipe = new Recipe(RecipeType.factory, getWrap(factory), 0);
        if(factory.consumesPower && factory.consPower != null )recipe.addMaterialFloat(PowerMark.INSTANCE, factory.consPower.usage);
        recipe.addProductionInteger(getWrap(factory.type), 1);
        return Seq.with(recipe);
    }
}
