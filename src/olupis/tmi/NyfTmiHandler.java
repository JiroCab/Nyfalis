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
        TooManyItems.recipesManager.registerParser(new UnitFabricatorRecipes());
        TooManyItems.recipesManager.registerParser(new PropellerCoreRecipes());
        Log.info("Nyfalis filled TMI with their recipes ;3c");
    }

    @Override
    public void afterInit(){

    }
}
