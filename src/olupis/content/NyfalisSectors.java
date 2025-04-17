package olupis.content;

import arc.func.*;
import arc.struct.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.type.*;
import mindustry.world.*;

import static olupis.content.NyfalisBlocks.*;
import static olupis.content.NyfalisPlanets.*;

public class NyfalisSectors {
    public static final float sectorVersion = 1.2f;

    public static SectorPreset
        /*Arthin / Seredris*/
        sanctuary, dyingForest, terrarootCaves, muddyLakes, ironCurtain, glasierSea, abandonedPayloadTerminal, conciditRuins, coldFlats,
        /*Nyfalis*/
        conservatorium, forestOfSerenity,
        /*Spelta / Vorgin*/
         dormantCell, forestOfHope
    ;

    public static final Seq<Block> //Used by the planet generators, Not in NyfalisBLock so it's not null
            mossGreen = Seq.with(mossStone, mossyStone, mossierStone, mossiestStone),
            mossGreenExtra = Seq.with(mossyDirt, mossyhardenMud, mossyVent),
            mossGreenAll = Seq.with(mossGreen).addAll(mossGreenExtra),
            mossYellow = Seq.with(yellowGrass, yellowMossyWater),
            waterFeatures = Seq.with(kelp, redCorals, blueCorals, greenCorals),
            treesGreen = Seq.with(nyfalisTree, mossTree, mossDeadTree, mossTree),
            treesYellow = Seq.with(yellowTree, yellowTreeBlooming, mossDeadTree, mossTree),
            treesAll = Seq.with(treesGreen).addAll(treesYellow),
            solidMossYellow = Seq.with(yellowShrubs, yellowShrubsCrooked, yellowShrubsIrregular, yellowTree, yellowTreeBlooming),
            solidMossYellowAll = Seq.with(solidMossYellow).addAll(yellowTree),
            solidShurbsGrass = Seq.with(greenShrubsIrregular, greenShrubsCrooked),
            overlayFlowers = Seq.with(glowSprouts, lumaSprouts)
    ;

    public static final Seq<SectorPreset> nyfalisSectorRequirement = Seq.with(ironCurtain, glasierSea, conciditRuins);


    public static void LoadSectors(){

        //region Seredris
        sanctuary = new SectorPreset("sanctuary", arthin, 2){{
            alwaysUnlocked = overrideLaunchDefaults =  true;
            addStartingItems = allowLaunchSchematics = false;

            captureWave = 15;
            difficulty = 1;
            rules = commonRules(captureWave);
        }};

        terrarootCaves = new SectorPreset("terraroot-caves", arthin, 31){{
            overrideLaunchDefaults =  true;
            addStartingItems = allowLaunchLoadout = allowLaunchSchematics = false;

            difficulty = 2;
            captureWave = 13;
            rules = commonRules(captureWave, ItemStack.with(NyfalisItemsLiquid.rustyIron, 300, Items.lead, 200));
        }};

        muddyLakes = new SectorPreset("muddy-lakes", arthin, 21){{
            addStartingItems  = overrideLaunchDefaults =  true;
            allowLaunchLoadout = allowLaunchSchematics = false;

            captureWave = 17;
            difficulty = 4;
            rules = commonRules(captureWave, ItemStack.with(NyfalisItemsLiquid.rustyIron, 500, Items.lead, 500));
        }};

        dyingForest = new SectorPreset("dying-forest", arthin, 10){{
            overrideLaunchDefaults =  true;
            addStartingItems = allowLaunchLoadout = allowLaunchSchematics  = false;

            captureWave = 16;
            difficulty = 3;
            rules = commonRules(captureWave, ItemStack.with(Items.copper, 75, NyfalisItemsLiquid.rustyIron,300, Items.lead, 300, NyfalisItemsLiquid.iron, 250));
        }};

        glasierSea = new SectorPreset("glasier-sea", arthin, 7){{
            addStartingItems = overrideLaunchDefaults =  true;
            allowLaunchLoadout = allowLaunchSchematics =  false;

            difficulty = 5;
            rules = commonRules(captureWave, ItemStack.with(NyfalisItemsLiquid.rustyIron, 1200, Items.lead, 1200, NyfalisItemsLiquid.iron, 200, Items.copper, 300,  Items.graphite, 200));
        }};

        conciditRuins = new SectorPreset("concidit-ruins", arthin, 6){{
            addStartingItems = overrideLaunchDefaults = true;
            allowLaunchLoadout = allowLaunchSchematics =  false;

            difficulty = 3;
            rules = commonRules(captureWave, ItemStack.with(NyfalisItemsLiquid.rustyIron, 200, Items.lead, 200, NyfalisItemsLiquid.iron, 50));
        }};

        coldFlats = new SectorPreset("cold-flats", arthin, 29){{
            addStartingItems = overrideLaunchDefaults =  true;
            allowLaunchLoadout = allowLaunchSchematics =  false;

            difficulty = 5;
            captureWave = 13;
            rules = commonRules(captureWave, ItemStack.with(NyfalisItemsLiquid.rustyIron, 1000, Items.lead, 1000,Items.copper, 500, NyfalisItemsLiquid.iron, 550));
        }};

        ironCurtain = new SectorPreset("iron-curtain", arthin, 11){{
            addStartingItems = overrideLaunchDefaults =  true;
            allowLaunchLoadout = allowLaunchSchematics =  false;

            difficulty = 6;
            captureWave = 21;
            rules = commonRules(captureWave, ItemStack.with(NyfalisItemsLiquid.rustyIron, 100, Items.lead, 100));
        }};

        abandonedPayloadTerminal = new SectorPreset("abandoned-payload-terminal", arthin, 0){{
            addStartingItems = overrideLaunchDefaults =  true;
            allowLaunchLoadout = allowLaunchSchematics =  false;

            difficulty = 5;
            captureWave = 21;
            rules = commonRules(captureWave, ItemStack.with(NyfalisItemsLiquid.rustyIron, 0, Items.lead, 0));
        }};

        //endregion
        //region Nyfalis

        conservatorium = new SectorPreset("conservatorium", nyfalis, 0){{
            captureWave = 20;
            difficulty = 3;
            rules = commonRules(captureWave);
        }};

        forestOfSerenity  = new SectorPreset("forest-of-serenity", nyfalis, 43){{
            difficulty = 4;
            rules = commonRules(captureWave);
        }};

        //endregion
        //region Vorgin
        dormantCell = new SectorPreset("dorment-cell", spelta, 1){{
            /*Yes this map's lore may or may not be a reference to command and conquer*/
            difficulty = 4;
            rules = commonRules(captureWave);
        }};

        forestOfHope = new SectorPreset("forest-of-hope", spelta,  4){{
            difficulty = 2;
            rules = commonRules(captureWave);
        }};
        //endregion
    }

    //moved it here, so players switching planets rule isn't affected & per map dropZonesRadius are possible
    public static void defaultRules(Rules r, float dzRadius, ItemStack[] startItems){
        if(dzRadius <= 0)r.dropZoneRadius = 400f;
        else r.dropZoneRadius = dzRadius;
        r.enemyCoreBuildRadius = 650f;
        r.env = nyfalis.defaultEnv;

        if(startItems.length >= 1)r.loadout.set(startItems);
    }

    public static void defaultRules(Rules r){
        defaultRules(r, -1, ItemStack.with(NyfalisItemsLiquid.rustyIron, 100, Items.lead, 100));
    }
    public static void defaultRules(Rules r, float dzRadius){
        defaultRules(r, dzRadius);
    }

    public static Cons<Rules> commonRules(int captureWave, float dzRadius, ItemStack[] startItems){ return r ->{
        r.winWave = captureWave;
        defaultRules(r, dzRadius, startItems);
    };}

    public static Cons<Rules> commonRules(int captureWave, ItemStack[] startItems){
        return commonRules(captureWave, -1f, startItems);
    }

    public static Cons<Rules> commonRules(int captureWave){
        return commonRules(captureWave, -1f);
    }
    public static Cons<Rules> commonRules(int captureWave, float dzRadius){
        return commonRules(captureWave,  dzRadius, ItemStack.with(NyfalisItemsLiquid.rustyIron, 100, Items.lead, 100));
    }

}
