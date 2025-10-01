package olupis.tmi.recipes;

import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.type.*;
import mindustry.world.*;
import olupis.world.blocks.defence.*;
import olupis.world.entities.bullets.*;
import org.jetbrains.annotations.*;
import tmi.recipe.*;
import tmi.recipe.types.*;

import java.util.*;

public class ConstuctRecipes extends RecipeParser<ItemUnitTurret>{

    @Override
    public boolean isTarget(@NotNull Block block){
        return block instanceof ItemUnitTurret;
    }

    @NotNull
    @Override
    public Seq<Recipe> parse(ItemUnitTurret factory){
        Seq <Recipe> out = new Seq<>();
        ObjectMap<Item, BulletType> alts = new ObjectMap<>();

        for(Entry<Item, BulletType> a : factory.ammoTypes){
            if(!( a.value instanceof SpawnHelperBulletType s)){
                Log.err(a.value+ "is not a spawn bullet!");
                continue;
            }

            if(factory.hasAlternate && s.alternateType != null) alts.put(a.key, s.alternateType);
            Recipe recipe = new Recipe(RecipeType.factory, getWrap(factory), factory.reload *  a.value.reloadMultiplier);

            for(ItemStack st :  factory.requiredItems ) recipe.addMaterialInteger(getWrap(st.item), st.amount);
            recipe.addProductionInteger(getWrap(s.spawnUnit), 1);
            if(!(factory instanceof PowerUnitTurret p) || a.key != p.internalItem)recipe.addMaterialInteger(getWrap(a.key), 1);
            if(factory.consumesPower && factory.consPower != null )recipe.addMaterialFloat(PowerMark.INSTANCE, factory.consPower.usage * 60);
            out.add(recipe);
        }

        for(Entry<Item, BulletType> a : alts){
            if(!( a.value instanceof SpawnHelperBulletType s)){
                Log.err(a.value+ "is not a spawn bullet!");
                continue;
            }
            Recipe recipe = new Recipe(RecipeType.factory, getWrap(factory), factory.reload *  a.value.reloadMultiplier);

            for(ItemStack st :  factory.requiredAlternate ) recipe.addMaterialInteger(getWrap(st.item), st.amount);
            recipe.addProductionInteger(getWrap(s.spawnUnit), 1);
            if(!(factory instanceof PowerUnitTurret p) || a.key != p.internalItem)recipe.addMaterialInteger(getWrap(a.key), 1);
            if(factory.consumesPower && factory.consPower != null )recipe.addMaterialFloat(PowerMark.INSTANCE, factory.consPower.usage * 60);
            out.add(recipe);}


        return out;
    }
}
