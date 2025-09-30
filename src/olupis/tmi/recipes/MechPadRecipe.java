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

    @Override
    public Seq<Recipe> parse(MechPad factory){
        Recipe recipe = new Recipe(RecipeType.factory, getWrap(factory), 1);
        recipe.addProductionInteger(getWrap(factory.type), 1);
        return Seq.with(recipe);
    }
}
