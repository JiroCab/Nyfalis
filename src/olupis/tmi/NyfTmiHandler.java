package olupis.tmi;

import arc.util.*;
import olupis.tmi.recipes.*;
import tmi.*;

public class NyfTmiHandler implements RecipeEntry{

    @Override
    public void init(){
        TooManyItems.recipesManager.registerParser(new MechPadRecipe());
        TooManyItems.recipesManager.registerParser(new ConstuctRecipes());
        TooManyItems.recipesManager.registerParser(new HeadAcheRecipes());
        Log.info("Nyfalis & Tmi has been loaded");
    }

    @Override
    public void afterInit(){
        Log.info("Nyfalis Recipes loaded owo");
    }
}
