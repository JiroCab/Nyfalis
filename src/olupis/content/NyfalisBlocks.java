package olupis.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.legacy.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import olupis.input.*;
import olupis.world.*;
import olupis.world.NyfPartParms.*;
import olupis.world.blocks.defence.*;
import olupis.world.blocks.distribution.*;
import olupis.world.blocks.drawers.*;
import olupis.world.blocks.environment.*;
import olupis.world.blocks.misc.*;
import olupis.world.blocks.power.*;
import olupis.world.blocks.processing.*;
import olupis.world.blocks.turret.*;
import olupis.world.blocks.unit.*;
import olupis.world.consumer.*;
import olupis.world.entities.bullets.*;
import olupis.world.entities.parts.*;
import olupis.world.entities.pattern.*;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.oil;
import static mindustry.type.ItemStack.with;
import static olupis.content.NyfalisAttributeWeather.*;
import static olupis.content.NyfalisColors.turretLightColor;
import static olupis.content.NyfalisItemsLiquid.*;
import static olupis.content.NyfalisUnits.*;

public class NyfalisBlocks {
    //region Blocks Variables
    public static Block
        //environment
        /*Ores / SpreadingOres / Overlays */
        oreIron, oreIronWall, oreCobalt, oreOxidizedCopper, oreOxidizedLead, oreQuartz, oreAlco,
        mossyCopper, mossyOxidizedCopper, mossyLead, mossyOxidizedLead, mossyScrap, mossyCoal, mossyIron,
        glowSprouts, lumaSprouts, redCorals, blueCorals, greenCorals, kelp,

        /*Floors*/
        redSand, riverSand, lumaGrass, yellowGrass, pinkGrass, mossierDirt, mossyDirt,  hardenMud, mossyhardenMud, muddyGrass,
        frozenGrass, frozenDirt, frozenMud, crackedIce, redSandSnow, snowySand, frozenTar, frozenSlop,
        cinderBloomGrass, cinderBloomy, cinderBloomier, cinderBloomiest, mossyStone, mossStone, mossierStone, mossiestStone,
        mudFloodPlane, stoneFloodPlane, mossyFloodPlane, sandyFloodPlane,
        grassyVent, mossyVent, stoneVent, basaltVent, hardenMuddyVent, dirtVent,
        redSandVent, snowVent, mycelium, yourcelium, ourcelium, theircelium,
        beachSandFloor, gypsumFloor, pumiceFloor, galenaFloor, rustyFloor, rustFloor, forestGrass,

        /*Liquid floors*/
        redSandWater, lumaGrassWater, brimstoneSlag, algaeWater, algaeWaterDeep, pinkGrassWater, yellowMossyWater, coralReef, slop, slopDeep, lubricantPool,
        flowWater, flowAlgea,

        /*props*/
        yellowBush, lumaFlora, bush, mossyBoulder, mossBoulder, infernalBloom, redSandBoulder, glowBloom, luminiteBoulder, deadBush, glowLilly, lilypad,
        grassSprig, mossSprig, yellowSprig, glowSprig, lumaSprig,
        beachSandBoulder, gypsumBoulder, pumiceBoulder, galenaBoulder, rustyBoulder,

        /*walls*/
        redDune, pinkShrubs, lightWall, lumaWall, rustedMetal,
        greenShrubsIrregular, greenShrubsCrooked, yellowShrubs, yellowShrubsIrregular, yellowShrubsCrooked,
        mossyStoneWall, mossierStoneWall, mossiestStoneWall, mossStoneWall, growingWall,
        beachSandWall, gypsumWall, gypsumRubble, pumiceWall, pumiceRubble, galenaWall,

        /*Env Hazzard*/
        boomPuffPassive, boomPuffActive,

        /*Trees*/
        nyfalisTree, mossTree, pinkTree, yellowTree, yellowTreeBlooming, infernalMegaBloom, orangeTree, deadTree, mossDeadTree, spruceTree,

        //Buildings, sorted by category

        //turret "trees"
        fracture,  shredder,
        slash, strata, laceration, hive,
        avenger, aegis,
        corroder, porcupine,  duality,
        //leftover legacy
        dissolver, obliterator,

        rustyDrill, steamDrill, hydroElectricDrill, steamAgitator, garden, fortifiedRadiator,

        rustyIronConveyor, ironConveyor, cobaltConveyor, ironRouter, ironDistributor ,ironJunction, ironBridge, ironOverflow, ironUnderflow, ironUnloader, rustedBridge, offloader,

        leadPipe, ironPipe, pipeRouter, pipeJunction, pipeBridge, displacementPump, massDisplacementPump, ironPump, rustyPump, fortifiedTank, fortifiedCanister,
        steamBoiler, Liquifier, oilSeparator, lubricantMixer, demulsifier,

        wire, wireBridge, superConductors, windMills, hydroMill, hydroElectricGenerator, quartzBattery, mirror, solarTower, steamTurbine, biomassGenerator, steamyGenerator,

        rustyWall, rustyWallLarge, rustyWallHuge, rustyWallGigantic, ironWall, ironWallLarge, rustyScrapWall, rustyScrapWallLarge, rustyScrapWallHuge, rustyScrapWallGigantic, rustyScrapWallHumongous, quartzWall, quartzWallLarge, cobaltWall, cobaltWallLarge,

        rustElectrolyzer, ironSieve, rustEngraver, pulverPress, discardDriver, siliconKiln, inductionSmelter, compoundCrucible, componentFabricator,

        construct, arialConstruct, groundConstruct, navalConstruct, alternateArticulator, adaptiveFabricator, alternateAmalgamator,ultimateAssembler, fortifiedPayloadConveyor, fortifiedPayloadRouter, repairPin, scoutPad, blackHoleContainer,

        heavyMine,fragMine,glitchMine,mossMine,
        coreRemnant, coreEmergent, corePrime, coreApex, coreAscendant, coreParagon, fortifiedVault, fortifiedContainer, deliveryCannon, deliveryTerminal, deliveryAccelerator, deliveryReciver,
        mendFieldProjector, taurus, lamp, ladar, search,
        cutboi,

        fortifiedMessageBlock, mechanicalProcessor, analogProcessor, mechanicalSwitch, mechanicalRegistry, nyfalianProcessor,

        /*special*/
        scarabRadar, floodDisruptor
    ; //endregion
    public static UnstablePowerTurret cascade;
    public static Replicator unitReplicator, unitReplicatorSmall;
    public static NyfLegacyBlock  hydrochloricGraphitePress, siliconArcSmelter, mushBlender;
    public static FactoryPlan
        copperWirePlan, ironFramePlan, crudeBatteryPlan, ironPlatePlan, basicRotorPlan,
        graphiteFramePlan, ceramicPlatingPlan, siliconCircuitPlan, electricMotorPlan, graphiteCellPlan,
        graphitePlan, siliconPlan;

    public static Color nyfalisBlockOutlineColour = NyfalisColors.contentOutline;
    public static ObjectSet<Block>
            nyfalisBuildBlockSet = new ObjectSet<>(), sandBoxBlocks = new ObjectSet<>(), nyfalisCores = new ObjectSet<>(), allNyfalisBlocks = new ObjectSet<>(), hiddenNyfalisBlocks = new ObjectSet<>(),
            rainRegrowables = new ObjectSet<>(), spreadingTiles = new ObjectSet<>(), vents = new ObjectSet<>(),
            factoryPlans = new ObjectSet<>(), alternateModules = new ObjectSet<>()
    ;

    public static void LoadWorldTiles() {
        //region Ores / Overlays
        oreIron = new OreBlock("ore-iron", rustyIron);

        oreIronWall = new OreBlock("ore-iron-wall", rustyIron) {{
            wallOre = true;
        }};

        oreCobalt = new OreBlock("ore-cobalt", cobalt);

        /*uses ore's item as a name block in editor*/
        oreOxidizedCopper = new OreBlock("ore-oxidized-copper", copper);
        oreOxidizedLead = new OreBlock("ore-oxidized-lead", lead);

        oreQuartz = new OreBlock("ore-quartz", quartz) {{
            variants = 3;
        }};

        oreAlco = new OreBlock("ore-alco", alcoAlloy) {{
            variants = 2;
        }};

        glowSprouts = new OverlayFloor("glow-sprout") {{
            emitLight = true;
            needsSurface = false;
            variants = 1;
            lightRadius = 10f;
            //is sad that BlockRenderer.java does not render light from overlays, but ill keep it incase TnT
        }};

        lumaSprouts = new OverlayFloor("luma-sprout") {{
            variants = 1;
            needsSurface = false;
        }};

        redCorals = new OverlayFloor("red-corals") {{
            variants = 1;
            needsSurface = false;
        }};

        blueCorals = new OverlayFloor("blue-corals") {{
            variants = 1;
            needsSurface = false;
        }};

        greenCorals = new OverlayFloor("green-corals") {{
            variants = 1;
            needsSurface = false;
        }};

        kelp = new OverlayFloor("kelp") {{
            variants = 1;
            needsSurface = false;
        }};

        //endregion
        // region Floors
        /*In each group it should be sorted in the following:
        * Liquid/wet > normal > vent  || sub-grouped tiles with ther variants */

        // region > watered sands
        snowySand = new Floor("snowy-sand") {{
            attributes.set(Attribute.water, 0.1f);
            variants = 3;
        }};

        beachSandFloor = new Floor("beach-sand-floor") {{
            itemDrop = Items.sand;
            playerUnmineable = true;
            variants = 4;
            attributes.set(Attribute.oil, 0.75f);
        }};

        riverSand = new Floor("river-sand") {{
            itemDrop = Items.sand;
            attributes.set(Attribute.oil, 1.2f);
        }};

        sandyFloodPlane= new Floor("river-sand-plane") {{
            variants = 3;
            itemDrop = Items.sand;
            attributes.set(Attribute.oil, 1.2f);
            attributes.set(Attribute.water, 25f);
            cacheLayer = NyfalisShaders.floodPlaneC;
        }};
        // endregion
        // region > red sand
        redSandWater = new Floor("red-sand-water") {{
            isLiquid = supportsOverlay = true;

            variants = 0;
            albedo = 0.9f;
            statusDuration = 50f;
            speedMultiplier = 0.8f;
            liquidDrop = Liquids.water;
            status = StatusEffects.wet;
            cacheLayer = CacheLayer.water;
        }};

        redSand = new Floor("red-sand-floor") {{
            itemDrop = Items.sand;
            playerUnmineable = true;
            attributes.set(Attribute.oil, 1.5f);
        }};

        redSandSnow = new Floor("red-sand-snow") {{
            itemDrop = Items.sand;
            playerUnmineable = true;
            attributes.set(Attribute.oil, 1.5f);
        }};

        redSandVent = new SteamVent("red-sand-vent") {{
            effectColor = Color.white;
            parent = blendGroup = redSand;
            attributes.set(Attribute.steam, 1f);
        }};
        //endregion
        //region > ore stones
        gypsumFloor = new Floor("gypsum-floor") {{
            variants = 4;
        }};

        galenaFloor = new Floor("galena-floor") {{
            variants = 4;
        }};

        pumiceFloor = new Floor("pumice-floor") {{
            variants = 4;
        }};
        //endregion
        // region > metal & rusts
        rustyFloor = new Floor("rusty-floor") {{
            variants = 4;
            blendGroup = metalFloor;
        }};

        rustFloor = new Floor("rust-floor") {{
            variants = 4;
        }};
        //endregion
        //region > grasses
        grassyVent = new SteamVent("grassy-vent") {{
            effectColor = Color.white;
            parent = blendGroup = grass;
            attributes.set(Attribute.steam, 1f);
        }};

        forestGrass = new RotatingFloor("forest-grass") {{
            attributes.set(Attribute.water, 0.1f);
            variants = 3;
            rotateDraw =false;
        }};

        lumaGrassWater = new Floor("luma-grass-water") {{
            isLiquid = supportsOverlay = true;
            liquidDrop = Liquids.water;
            status = StatusEffects.wet;
            statusDuration = 50f;
            speedMultiplier = 0.8f;
            cacheLayer = CacheLayer.water;
            variants = 0;
            albedo = 0.9f;
        }};

        lumaGrass = new Floor("luma-grass") {{
            variants = 3;
            attributes.set(bio, 0.08f);
            attributes.set(Attribute.water, 0.15f);
        }};

        yellowMossyWater = new Floor("yellow-mossy-water") {{
            isLiquid = supportsOverlay = true;

            variants = 0;
            albedo = 0.9f;
            statusDuration = 50f;
            speedMultiplier = 0.8f;
            status = StatusEffects.wet;
            liquidDrop = Liquids.water;
            cacheLayer = CacheLayer.water;
        }};

        yellowGrass = new Floor("yellow-grass") {{
            variants = 4;
            attributes.set(bio, 0.08f);
            attributes.set(Attribute.water, 0.15f);
        }};

        pinkGrassWater = new Floor("pink-grass-water") {{
            isLiquid = supportsOverlay = true;

            statusDuration = 50f;
            speedMultiplier = 0.8f;
            variants = 0;
            albedo = 0.9f;
            liquidDrop = Liquids.water;
            status = StatusEffects.wet;
            cacheLayer = CacheLayer.water;
        }};

        pinkGrass = new Floor("pink-grass") {{
            variants = 4;
            attributes.set(bio, 0.08f);
            attributes.set(Attribute.water, 0.15f);
        }};

        frozenGrass = new Floor("frozen-grass") {{
            attributes.set(Attribute.water, 0.15f);
            attributes.set(bio, 0.08f);
            wall = shrubs;
        }};
        //endregion
        //region > cinder bloom
        cinderBloomy = new Floor("cinder-bloomy") {{
            variants = 3;
            attributes.set(bio, 0.03f);
            attributes.set(Attribute.water, -0.15f);
        }};

        cinderBloomier = new Floor("cinder-bloomier") {{
            variants = 3;
            attributes.set(bio, 0.02f);
            attributes.set(Attribute.water, -0.05f);
        }};

        cinderBloomiest = new Floor("cinder-bloomiest") {{
            variants = 3;
            attributes.set(bio, 0.01f);
        }};

        cinderBloomGrass = new Floor("cinder-bloom") {{
            variants = 3;
            attributes.set(bio, 0.06f);
            attributes.set(Attribute.water, 0.25f);
        }};
        //endregion
        //region > moss Stone
        algaeWater = new Floor("mossy-water") {{
            isLiquid = supportsOverlay = true;

            variants = 3;
            albedo = 0.9f;
            statusDuration = 50f;
            speedMultiplier = 0.8f;
            status = StatusEffects.wet;
            liquidDrop = Liquids.water;
            cacheLayer = CacheLayer.water; //cacheLayer = NyfalisShaders.algaeC;
            blendGroup = water;
        }};

        algaeWaterDeep = new Floor("mossy-water-deep") {{ //Remind rushie to update the bundles thx -past rushie
            variants = 3;
            albedo = 0.9f;
            drownTime = 200f;
            statusDuration = 120f;
            liquidMultiplier = 1.5f;
            speedMultiplier = 0.2f;
            isLiquid = supportsOverlay = true;
            liquidDrop = Liquids.water;
            status = StatusEffects.wet;
            cacheLayer = CacheLayer.water; //cacheLayer = NyfalisShaders.algaeC;
            blendGroup = water;
        }};

        mossyStone = new RotatingFloor("mossy-stone") {{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.1f);
        }};

        mossierStone = new RotatingFloor("mossier-stone") {{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.1f);
        }};

        mossiestStone = new RotatingFloor("mossiest-stone") {{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.1f);
        }};

        mossStone = new Floor("moss-stone") {{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.1f);
        }};

        mossyDirt = new RotatingFloor("mossy-dirt") {{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.1f);
        }};

        mossierDirt = new RotatingFloor("mossier-dirt") {{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.1f);
        }};

        mossyFloodPlane = new Floor("mossy-plane") {{
            variants = 3;
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.5f);
            cacheLayer = NyfalisShaders.floodPlaneC;
        }};

        mossyhardenMud = new RotatingFloor("mossy-harden-mud") {{
            attributes.set(bio, 0.2f);
            attributes.set(Attribute.water, 0.1f);
        }};

        mossyVent = new SteamVent("mossy-vent") {{
            variants = 3;
            effectColor = Color.white;
            parent = blendGroup = mossStone;
            attributes.set(Attribute.steam, 1f);
        }};

        //endregion
        //region > dirt
        frozenDirt = new Floor("frozen-dirt") {{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.3f);
        }};

        dirtVent = new SteamVent("dirt-vent") {{
            variants = 3;
            effectColor = Color.white;
            parent = blendGroup = dirt;
            attributes.set(Attribute.steam, 1f);
        }};
        //endregion
        //region > muds
        frozenMud = new Floor("frozen-mud") {{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.3f);
        }};

        mudFloodPlane = new Floor("mud-plane") {{
            speedMultiplier = 0.7f;
            variants = 3;
            statusDuration = 15f;
            status = StatusEffects.muddy;
            attributes.set(Attribute.water, 1f);
            cacheLayer = NyfalisShaders.floodPlaneC;
        }};

        hardenMud = new Floor("harden-mud") {{
            attributes.set(Attribute.water, 0.1f);
            variants = 3;
        }};

        hardenMuddyVent = new SteamVent("harden-muddy-vent") {{
            effectColor = Color.white;
            parent = blendGroup = hardenMud;
            attributes.set(Attribute.steam, 1f);
        }};
        //endregion
        //region > stones
        stoneFloodPlane = new Floor("stone-plane") {{
            variants = 3;
            attributes.set(Attribute.water, 0.25f);
            cacheLayer = NyfalisShaders.floodPlaneC;
        }};

        stoneVent = new SteamVent("stone-vent") {{
            effectColor = Color.white;
            parent = blendGroup = stone;
            attributes.set(Attribute.steam, 1f);
        }};

        basaltVent = new SteamVent("basalt-vent") {{
            effectColor = Color.white;
            parent = blendGroup = basalt;
            attributes.set(Attribute.steam, 1f);
        }};
        //endregion
        //region > frozen
        crackedIce = new Floor("cracked-ice") {{
            attributes.set(Attribute.water, 0.3f);
            variants = 3;
        }};

        snowVent = new SteamVent("snow-vent") {{
            effectColor = Color.white;
            parent = blendGroup = snow;
            attributes.set(Attribute.steam, 1f);
        }};

        frozenTar = new Floor("frozen-tar") {{
            attributes.set(Attribute.water, 0.3f);
            attributes.set(Attribute.oil, 1.2f);
            variants = 3;
        }};

        frozenSlop = new Floor("frozen-slop") {{
            attributes.set(Attribute.water, 0.6f);
            attributes.set(Attribute.oil, 0.6f);
            variants = 3;
        }};
        // endregion
        //region > special liquid floors
        slop = new Floor("slop") {{
            isLiquid = supportsOverlay = true;

            variants = 0;
            albedo = 0.9f;
            statusDuration = 50f;
            speedMultiplier = 0.7f;
            liquidDrop = emulsiveSlop;
            cacheLayer = NyfalisShaders.slopC;
            status = NyfalisStatusEffects.sloppy;
            walkEffect = NyfalisFxs.bubbleSlow;
        }};

        slopDeep = new Floor("slop-deep") {{
            isLiquid = supportsOverlay = true;

            variants = 0;
            albedo = 0.9f;
            drownTime = 400f;
            statusDuration = 100f;
            speedMultiplier = 0.35f;
            liquidDrop = emulsiveSlop;
            cacheLayer = NyfalisShaders.slopC;
            status = NyfalisStatusEffects.sloppy;
            walkEffect = NyfalisFxs.bubbleSlow;
        }};

        coralReef = new Floor("coral-reef") {{
            variants = 0;
            albedo = 0.9f;
            drownTime = 200f;
            statusDuration = 120f;
            liquidMultiplier = 1.5f;
            speedMultiplier = 0.2f;
            isLiquid = supportsOverlay = true;
            liquidDrop = Liquids.water;
            status = StatusEffects.wet;
            cacheLayer = CacheLayer.water;
        }};

        brimstoneSlag = new Floor("brimstone-slag") {{
            isLiquid = emitLight = true;

            variants = 0;
            drownTime = 30f;
            lightRadius = 40f;
            statusDuration = 240f;
            speedMultiplier = 0.19f;
            liquidDrop = Liquids.slag;
            damageTaken = 9999999f;
            status = StatusEffects.melting;
            cacheLayer = CacheLayer.slag;
            attributes.set(Attribute.heat, 0.90f);
            lightColor = Color.valueOf("D54B3B").a(0.38f);
        }};

        lubricantPool = new Floor("lubricant-pool") {{
            drownTime = 180f;
            status = NyfalisStatusEffects.lubed;
            statusDuration = 120f;
            speedMultiplier = 1.05f;
            variants = 0;
            liquidDrop = lubricant;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
        }};

        flowWater = new FlowWaterTile("flow-water") {{
            parent = water;
            variants = 0;
            albedo = 0.9f;
            drownTime = 200f;
            statusDuration = 120f;
            liquidMultiplier = 1.5f;
            speedMultiplier = 0.2f;
            isLiquid = supportsOverlay = true;
            blendGroup = water;
            liquidDrop = Liquids.water;
            status = StatusEffects.wet;
            cacheLayer = CacheLayer.water;
        }};

        flowAlgea = new FlowWaterTile("flow-algae") {{
            parent = algaeWater;
            variants = 0;
            albedo = 0.9f;
            drownTime = 200f;
            statusDuration = 120f;
            liquidMultiplier = 1.5f;
            speedMultiplier = 0.2f;
            isLiquid = supportsOverlay = true;
            blendGroup = water;
            liquidDrop = Liquids.water;
            status = StatusEffects.wet;
            cacheLayer = CacheLayer.water;
        }};
        //endregion
        //endregion
        //region Props
        yellowBush = new RotatingProp("yellow-bush") {{
            threshold = -1;
            variants = 2;
            breakSound = Sounds.plantBreak;
            frozenGrass.asFloor().decoration = this;
            yellowGrass.asFloor().decoration = this;
        }};

        lumaFlora = new RotatingProp("luma-flora") {{
            threshold = -1;
            variants = 2;
            breakSound = Sounds.plantBreak;
            lumaGrass.asFloor().decoration = this;
            pinkGrass.asFloor().decoration = this;
        }};

        bush = new RotatingProp("bush") {{
            customShadow = true;
            variants = 2;
            breakSound = Sounds.plantBreak; // Buildable via planty mush
        }};

        mossyBoulder = new Prop("mossy-boulder") {{
            variants = 2;
            frozenGrass.asFloor().decoration = this;
            mossierStone.asFloor().decoration = this;
        }};
        mossBoulder = new Prop("moss-boulder") {{
            variants = 2;
            mossStone.asFloor().decoration = this;
            mossiestStone.asFloor().decoration = this;
        }};
        beachSandBoulder = new Prop("beach-sand-boulder") {{
            beachSandFloor.asFloor().decoration = this;
            variants = 2;
        }};
        gypsumBoulder = new Prop("gypsum-boulder") {{
            gypsumFloor.asFloor().decoration = this;
            variants = 2;
        }};
        galenaBoulder = new Prop("galena-boulder") {{
            galenaFloor.asFloor().decoration = this;
            variants = 2;
        }};
        pumiceBoulder = new Prop("pumice-boulder") {{
            pumiceFloor.asFloor().decoration = this;
            variants = 2;
        }};
        rustyBoulder = new Prop("rusty-boulder") {{
            rustyFloor.asFloor().decoration = this;
            rustFloor.asFloor().decoration = this;
            variants = 2;
        }};
        infernalBloom = new RotatingProp("infernal-bloom") {{
            variants = 3;
            breakSound = Sounds.plantBreak;
            cinderBloomGrass.asFloor().decoration = this;
            cinderBloomier.asFloor().decoration = this;
            cinderBloomiest.asFloor().decoration = this;
        }};

        redSandBoulder = new RotatingProp("red-sand-boulder") {{
            threshold = -1;
            variants = 2;
            redSand.asFloor().decoration = this;
        }};

        glowBloom = new VariantsBush("glow-bloom") {{
            variants = 2;
            lightRadius = 10f;
            lobesMin = 5;
            lobesMax = 6;

            emitLight = true;
            lightColor = NyfalisColors.glowPlantLight;
            breakSound = Sounds.plantBreak;
        }};

        deadBush = new RotatingProp("dead-bush") {{
            hasShadow = false;
            variants = 3;
            breakSound = Sounds.plantBreak;
        }};

        glowLilly = new RotatingProp("glow-lilly") {{
            variants = 1;
            lightRadius = 8.5f;
            hasShadow = false;
            lightColor = NyfalisColors.glowPlantLight;
            floating = placeableLiquid = emitLight = true;
            breakSound = Sounds.plantBreak;
        }};

        lilypad = new LargeProps("lilypad") {{
            variants = 3;
            largeVariants = 6;
            customShadow = floating = placeableLiquid = true;
            breakSound = Sounds.plantBreak;
        }};


        //Rain stage grows of props
        grassSprig = new RotatingProp("grass-sprig") {{
            hasShadow = false;
            replacement = bush;
            breakSound = Sounds.plantBreak;
        }};

        mossSprig = new RotatingProp("moss-sprig") {{
            hasShadow = false;
            replacement = mossBoulder;
            breakSound = Sounds.plantBreak;
        }};

        yellowSprig = new RotatingProp("yellow-sprig") {{
            hasShadow = false;
            replacement = yellowBush;
            breakSound = Sounds.plantBreak;
        }};

        glowSprig = new RotatingProp("glow-sprig") {{
            hasShadow = false;
            lightRadius = 5f;
            floating = placeableLiquid = emitLight = rotate =  true;
            replacement = glowLilly;
            lightColor = NyfalisColors.glowPlantLightSofter;
            breakSound = Sounds.plantBreak;
        }};

        lumaSprig = new RotatingProp("luma-sprig") {{
            hasShadow = false;
            replacement = lumaFlora;
            breakSound = Sounds.plantBreak;
        }};

        //endregion
        //region Walls
        beachSandWall = new StaticWall("beach-wall") {{
            beachSandFloor.asFloor().wall = this;
            variants = 3;
        }};
        gypsumWall = new StaticWall("gypsum-wall") {{
            gypsumFloor.asFloor().wall = this;
            variants = 3;
        }};
        gypsumRubble = new TallBlock("gypsum-rubble") {{
            variants = 2;
        }};
        galenaWall = new StaticWall("galena-wall") {{
            galenaFloor.asFloor().wall = this;
            variants = 3;
        }};
        pumiceWall = new StaticWall("pumice-wall") {{
            pumiceFloor.asFloor().wall = this;
            variants = 3;
        }};
        pumiceRubble = new StaticTree("pumice-rubble") {{
            variants = 2;
        }};
        redDune = new VeryStaticWall("red-dune-wall") {{
            redSand.asFloor().wall = this;
            attributes.set(Attribute.sand, 2f);
            largeVariants = 2;
        }};

        pinkShrubs = new StaticWall("pink-shrubs") {{
            variants = 2;
        }};

        lumaWall = new StaticTree("luma-wall") {{
            lumaGrass.asFloor().wall = this;
            variants = 2;
        }};

        rustedMetal = new StaticWall("rusted-metal") {{
            rustFloor.asFloor().wall = this;
            rustyFloor.asFloor().wall = this;
            variants = 2;
        }};

        greenShrubsIrregular = new TallBlock("green-shrubs-irregular"){{
            variants = 3;
            clipSize = 128f;
            shadowAlpha = 0.4f;
            shadowOffset = -1f;
        }};

        /*Irregular varrients that don't show up on top of tress*/
        greenShrubsCrooked = new StaticTree("green-shrubs-crooked"){{
            variants = 2;
            clipSize = 128f;
        }};

        yellowShrubs = new StaticWall("yellow-shrubs");

        yellowShrubsIrregular = new TallBlock("yellow-shrubs-irregular"){{
            variants = 2;
            clipSize = 128f;
            layer = Layer.power + 0.9f;
            shadowAlpha = 0.4f;
            shadowOffset = -1f;
        }};

        yellowShrubsCrooked = new TallBlock("yellow-shrubs-crooked"){{
            variants = 2;
            clipSize = 128f;
            layer = Layer.power + 0.9f;
        }};

        //todo: fix colours
        mossyStoneWall = new StaticWallTree("mossy-stone-wall"){{
            attributes.set(Attribute.sand, 1f);
            mossierStone.asFloor().wall = this;
        }};

        mossierStoneWall = new StaticWallTree("mossier-stone-wall"){{
            mossierStone.asFloor().wall = this;
            attributes.set(Attribute.sand, 0.8f);
        }};

        mossiestStoneWall = new StaticWallTree("mossiest-stone-wall"){{
            mossiestStone.asFloor().wall = this;
            attributes.set(Attribute.sand, 0.6f);
        }};

        mossStoneWall = new StaticWallTree("moss-stone-wall"){{
            mossStone.asFloor().wall = this;
            attributes.set(Attribute.sand, 0.6f);
            layer = Layer.power + 0.9f;
        }};

        //endregion
        //region Trees
        nyfalisTree = new TrasngenderTreeBlock("olupis-tree"){{
            variants = 2;
        }};
        mossTree = new TrasngenderTreeBlock("moss-tree"){{
            variants = 2;
        }};
        pinkTree = new TrasngenderTreeBlock("pink-tree"){{
            variants = 2;
        }};
        yellowTree = new TrasngenderTreeBlock("yellow-tree"){{
            flavours = Seq.with(Color.valueOf("f5d271"));
        }};
        yellowTreeBlooming = new TrasngenderTreeBlock("yellow-tree-blooming"){{
            flavours = Seq.with(Color.valueOf("f5d271"));
            featureVariants = 3;
            parent = yellowTree;
        }};
        infernalMegaBloom = new TrasngenderTreeBlock("infernal-megabloom"){{
            variants = 4;
            clipSize = 128f;
            flavours = Seq.with();
        }};
        orangeTree = new TrasngenderTreeBlock("orange-tree"){{
            variants = 3;
        }};
        deadTree = new TrasngenderTreeBlock("dead-tree"){{
            variants = 2;
            leaf = false;
        }};
        mossDeadTree = new TrasngenderTreeBlock("moss-dead-tree"){{
            variants = 2;
            leaf = false;
        }};


        //endregion
        //region Spreading & related floor

        growingWall = new GrowingWall("walltest", 0){{
            inEditor = false;

            growTries = 11;
            growChance = 0.04d;
            next = mossiestStoneWall;
        }};

        mycelium = new SpreadingOverlay("mossy-overlay", 3){{
            spread = true;

            spreadTries = 5;
            spreadChance = 0.021d / 30d;
            drillEfficiency = 0.66f;

            replacements.putAll(
                stoneWall, mossStoneWall
            );

            blacklistKey = "calyx";

            upgradeColor = Color.valueOf("#78bc27");
            spreadSound = NyfalisSounds.mossSpread;
        }};

        yourcelium = new SpreadingOverlay("mossier-overlay", 3){{
            inEditor = false;

            // this doesn't spread, but growth is affected by these settings too
            spreadTries = 7;
            spreadChance = 0.013d / 30d;

            ((SpreadingOverlay) mycelium).next = this;

            blacklistKey = "calyx";

            upgradeColor = Color.valueOf("#5a8d1d");
            spreadSound = NyfalisSounds.mossSpread;
        }};

        ourcelium = new SpreadingOverlay("mossiest-overlay", 3){{
            inEditor = false;

            // this doesn't spread, but growth is affected by these settings too
            spreadTries = 10;
            spreadChance = 0.0095d / 30d;

            ((SpreadingOverlay) yourcelium).next = this;

            blacklistKey = "calyx";

            upgradeColor = Color.valueOf("#3c5e14");
            spreadSound = NyfalisSounds.mossSpread;

            status = StatusEffects.corroded;
        }};

        theircelium = new Floor("moss", 3){{
            mapColor = Color.valueOf("#1e2f0a");
            inEditor = false;

            ((SpreadingOverlay) ourcelium).next = this;
            status = StatusEffects.corroded;
        }};

        //endregion
    }

    public static void LoadBlocks(){
        //region Distribution
        rustyIronConveyor = new VaraintConveryor("rusty-iron-conveyor"){{
            variants = 4;
            health = 60;
            armor = 1;
            speed = 0.025f;
            displayedSpeed = 3.8f;
            buildCostMultiplier = 1.5f;
            requirements(Category.distribution, with(rustyIron, 1));
        }};

        ironConveyor = new PowerConveyor("iron-conveyor"){{
            hasPower = conductivePower = consumesPower = noUpdateDisabled = true;
            variants = 1;

            health = 70;
            speed = 0.03f;
            itemCapacity = 1;
            displayedSpeed = 4.8f;
            poweredSpeed = 0.05f;
            powerRequired = 15f/60f;
            buildCostMultiplier = 0.45f;
            unpoweredSpeed = 0.025f;
            displayedSpeedPowered = 7f;

            researchCost = with(iron, 500, rustyIron, 1000, graphite, 200);
            consumePower (2f/60).boost();
            requirements(Category.distribution, with(iron, 2, rustyIron, 5, graphite, 1));
        }};

        cobaltConveyor = new PowerConveyor("cobalt-conveyor"){{
            hasPower = conductivePower = consumesPower = noUpdateDisabled =true;


            health = 70;
            speed = 0.06f;
            itemCapacity = 1;
            displayedSpeed = 0f;
            poweredSpeed = 0.095f;
            unpoweredSpeed = 0.023f;
            displayedSpeedPowered = 9f;
            buildCostMultiplier = 2f;

            consumePower (5f/60);
            researchCost = with(cobalt, 500, lead, 500, aluminum, 500, quartz, 500);
            requirements(Category.distribution, with(cobalt, 1, lead, 5, aluminum, 2, quartz ,1 ));
        }};

        ironRouter = new Router("iron-router"){{
            buildCostMultiplier = 1.5f;

            researchCost = with(rustyIron, 10, lead, 10);
            requirements(Category.distribution, with(rustyIron, 3, lead, 1));
        }};

        ironDistributor = new Router("iron-distributor"){{
            size = 2;
            speed = 16;
            health = 200;
            buildCostMultiplier = 2f;
            hasPower = conductivePower = consumesPower = noUpdateDisabled = true;
            researchCost = with(rustyIron, 300, lead, 300, iron, 10);
            consumePower (6f/60);
            requirements(Category.distribution, with(rustyIron, 3, lead, 3, iron, 1));
        }};

        ironJunction = new Junction("iron-junction"){{
            speed = 45;
            armor = 1f;
            health = 50;
            capacity = 6;
            buildCostMultiplier = 0.5f;

            researchCost = with(rustyIron, 20, lead, 20);
            ((Conveyor)rustyIronConveyor).junctionReplacement = this;
            ((PowerConveyor)ironConveyor).junctionReplacement = this;
            ((PowerConveyor)cobaltConveyor).junctionReplacement = this;
            requirements(Category.distribution, with(lead, 3, rustyIron, 3));
        }};

        rustedBridge = new BufferedItemBridge("rusted-bridge") {{
            /*Same throughput as a rusty conv, slightly slower but insignificant*/
            fadeIn = moveArrows = false;

            armor = 1f;
            health = 50;
            speed = 47.55f;
            itemCapacity = 5;
            arrowSpacing = 5f;
            buildCostMultiplier = 0.3f;
            range = bufferCapacity = 3;

            researchCost = with(lead, 15, rustyIron, 15);
            ((Conveyor)rustyIronConveyor).bridgeReplacement = this;
            /*expensive, to put off bridge waving/stacking*/
            requirements(Category.distribution, with(rustyIron, 6, lead, 6));
        }};

        ironBridge = new PoweredBufferItemBridge("iron-bridge"){{
            /*Same throughput as an iron conv*/
            fadeIn = moveArrows = false;
            hasPower = true;

            range = 6;
            armor = 1f;
            health = 50;
            speed = 67.05f;
            arrowSpacing = 6f;
            itemCapacity = 10;
            bufferCapacity = 8;
            buildCostMultiplier = 0.4f;

            consumePower(10f / 60f); 
            ((PowerConveyor)ironConveyor).bridgeReplacement = this;
            ((PowerConveyor)cobaltConveyor).bridgeReplacement = this;
            researchCost = with(iron, 100, rustyIron, 500, lead, 500);
            requirements(Category.distribution, with(iron, 12, rustyIron, 35, lead, 8));
        }};

        ironOverflow = new OverflowSorter("iron-overflow"){{
            hideDetails = false;
            buildCostMultiplier = 0.5f;
            researchCost = with(lead, 350, iron, 25);
            requirements(Category.distribution, with(iron, 2, lead, 5));
        }};

        ironUnderflow = new OverflowSorter("iron-underflow"){{
            invert = true;
            hideDetails = false;
            buildCostMultiplier = 0.5f;
            researchCost = with(lead, 350, iron, 25);

            requirements(Category.distribution, with(iron, 2, lead, 5));
        }};

        ironUnloader = new DirectionalUnloaderRotatable("iron-unloader"){{
            solid = false;
            allowCoreUnload = true;

            speed = 14f;
            health = 120;
            regionRotated1 = 1;
            buildCostMultiplier = 0.3f;
            researchCost = with(lead, 500, graphite, 100, iron, 100);
            requirements(Category.distribution, with(iron, 15, graphite, 15, lead, 30));
        }};

        offloader = new DirectionalUnloaderRotatable("offloader"){{
            solid = allowCoreUnload = hasPower = consumesPower = true;

            speed = 6f;
            health = 250;
            regionRotated1 = 1;
            consumePower(10f / 60f); 
            buildCostMultiplier = 0.3f;
            //TODO: this should costmore/later
            researchCost = with(cobalt, 500, graphite, 1500, iron, 1500);
            requirements(Category.distribution, with(iron, 30, silicon, 30, cobalt, 30));
        }};

        //endregion
        //region Drills / crafting
        rustyDrill = new BoostableBurstDrill("rusty-drill"){{
            hasPower = true;
            squareSprite = false;
            itemCapacity = 25;

            tier = 1;
            size = 3;
            liquidCapacity  = 10;
            drillTime = 60f * 8.5f;
            variants = topVariant = 3;

            drillEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, Fx.mineImpactWave.wrap(Pal.redLight, 40f));
            consumePower(10f/60f);
            consumeLiquid(Liquids.water, 5f/ 60f).boost();
            requirements(Category.production, with(rustyIron, 15    ));
        }};

        steamDrill = new Drill("steam-drill"){{
            squareSprite = false;
            hasPower = true;
            tier = 2;
            size = 3;
            drillTime = 60f * 4.03f;
            liquidCapacity  = 10;
            liquidBoostIntensity = 1.21f;

            envEnabled ^= Env.space;
            consumePower(50f/60f);
            consumeLiquid(NyfalisItemsLiquid.steam, 0.05f);
            researchCost = with(iron, 300, lead, 700);
            consumeLiquid(Liquids.slag, 5/ 60f).boost();
            requirements(Category.production, with( iron, 40, lead, 20));
        }};

        hydroElectricDrill = new Drill("hydro-electric-drill"){{
            tier = 3;
            size = 4;
            drillTime = 60f * 4.2f;
            liquidBoostIntensity = 1.7f;

            envEnabled ^= Env.space;
            consumeLiquid(NyfalisItemsLiquid.steam, 0.1f);
            consumePower(30f/60f);
            consumeLiquid(Liquids.slag, 0.1f).boost();
            researchCost = with(iron, 1000, graphite, 1000, silicon, 500);
            requirements(Category.production, with(iron, 60, graphite, 70, silicon, 30));
        }};

        garden = new AttributeCrafter("garden"){{
            hasLiquids = hasItems = legacyReadWarmup = true;
            size = 3;
            craftTime = 185f;
            maxBoost = 2.5f;
            liquidCapacity = 25;
            buildCostMultiplier = 0.6f;

            attribute = bio;
            craftEffect = Fx.none;
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawRegion("-middle"),
                    new DrawLiquidTile(Liquids.water){{alpha = 0.5f;}},
                    new DrawDefault(),
                    new DrawRegion("-top")
            );
            consumeLiquid(Liquids.water, 18f / 60f);
            researchCost = with(iron, 600, lead, 1200, rustyIron, 1200);
            outputItem = new ItemStack(condensedBiomatter, 3);
            requirements(Category.production, ItemStack.with(iron, 30, lead, 60, rustyIron, 60));
        }};

        steamAgitator = new AttributeCrafter("steam-agitator"){{
            outputsLiquid = solid = hasLiquids = true;
            displayEfficiency = rotate = squareSprite = false;

            size = 3;
            boostScale = 0.1f;
            craftTime = 150f;
            baseEfficiency = 0f;
            scaledHealth = 50f;
            liquidCapacity = 30f;
            minEfficiency = 9f - 0.0001f;
            envEnabled = Env.any;
            updateEffect = Fx.steam;
            attribute = Attribute.steam;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(NyfalisItemsLiquid.steam, 1f),
                    new DrawDefault()
            );
            researchCost = with(lead, 750, rustyIron, 750, copper, 750);
            outputLiquid = new LiquidStack(NyfalisItemsLiquid.steam, 15/60f);
            requirements(Category.production, with(rustyIron, 30, lead, 30, copper, 30));
        }};

        fortifiedRadiator = new RadiatorCrafter("fortified-radiator"){{
            size = 5;
            liquidCapacity = 50f;
            buildCostMultiplier = 2f;
            passiveOutput = (24f/60f)/ 9f; //really meant for vents that are 3x3, anything more is a boost
            consumePower(1f);
            attribute = Attribute.steam;
            consumeLiquid(NyfalisItemsLiquid.steam, 40/60f);
            outputLiquid = new LiquidStack(Liquids.water, 6/60f);
            researchCost = with(rustyIron, 750, copper, 750, lead, 750);
            requirements(Category.production, with(rustyIron, 150, lead, 150, copper, 150));
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(Liquids.water, 2),
                new DrawDefault()
            );
        }};

        //endregion
        //region Liquid
        rustyPump = new Pump("rusty-pump"){{
            squareSprite = false;
            size = 1;
            liquidCapacity = 15f;
            pumpAmount = 4f / 60f;
            buildCostMultiplier = 1.5f;
            requirements(Category.liquid, with(rustyIron, 1, lead, 3));
        }};

        ironPump = new Pump("iron-pump"){{
            squareSprite = false;
            size = 2;
            liquidCapacity = 40f;
            pumpAmount = (10f / size) / 60f;
            buildCostMultiplier = 2.1f;
            researchCost = with(quartz, 500, iron, 100, copper, 500);
            requirements(Category.liquid, with(iron, 20, quartz, 30, copper, 30));
        }};

        //NyfalisBlocks.displacementPump.pumpTime
        displacementPump = new BurstPump("displacement-pump"){{
            squareSprite = false;

            size = 3;
            pumpTime = 60f* 10f;
            dumpScale = 1.3f;
            leakAmount = 4.5f / 60f;
            pumpAmount = (150f/ size);
            liquidCapacity = 500f;
            consumePower(25f/60f);
            researchCost = with(iron, 250, alcoAlloy, 800, graphite, 250, rustyIron, 800);
            requirements(Category.liquid, with(iron, 15, graphite, 15, alcoAlloy, 30, rustyIron, 30));
        }};

        massDisplacementPump = new BurstPump("mass-displacement-pump"){{
            size = 4;
            leakAmount = 0.1f;
            pumpTime = 320;
            pumpAmount = 200f;
            liquidCapacity = 1000f;
            consumePower(70f/60f);
            researchCost = with(iron, 3000, aluminum, 3000, graphite, 3000, silicon, 3000);
            requirements(Category.liquid, with(iron, 30, graphite, 30, aluminum, 75, silicon, 30));
        }};

        leadPipe = new Conduit("lead-pipe"){{
            leaks = underBullets = true;

            health = 70;
            liquidCapacity = 2f;
            liquidPressure = 1.05f;
            researchCostMultiplier = 0.5f;
            botColor = Color.valueOf("37323C");
            requirements(Category.liquid, with(lead, 1, rustyIron, 1));
        }};

        ironPipe = new ArmoredConduit("iron-pipe"){{
            leaks = underBullets = true;

            liquidCapacity = 6f;
            liquidPressure = 1.25f;
            researchCostMultiplier = 3;
            botColor = Color.valueOf("252731");
            researchCost = with(lead, 300, iron, 50);
            requirements(Category.liquid, with(iron, 2, lead, 5));
        }};

        pipeRouter = new LiquidRouter("pipe-router"){{
            solid = underBullets = true;
            liquidCapacity = 15f;
            liquidPressure = 0.90f; /* Nerfed so you can't bypass lead pipe being terrible */
            researchCost = with(lead, 10, rustyIron, 10);
            requirements(Category.liquid, with(lead, 5, rustyIron, 5));
        }};

        fortifiedCanister = new LiquidRouter("pipe-canister"){{
            squareSprite = false;
            solid = true;
            size = 2;
            liquidPadding = 2f;
            liquidCapacity = 850f;
            liquidPressure = 0.95f;
            researchCost = with(lead, 300, iron, 50);
            requirements(Category.liquid, with(lead, 50, iron, 20));
        }};

        fortifiedTank = new LiquidRouter("pipe-tank"){{
            squareSprite = false;
            solid = true;
            size = 3;
            liquidPadding = 2f;
            liquidCapacity = 2300f;
            researchCost = with(lead, 800, iron, 250);
            requirements(Category.liquid, with(lead, 75, iron, 30));
        }};

        pipeJunction = new LiquidJunction("pipe-junction"){{
            solid = hideDetails = false;
            ((Conduit)ironPipe).junctionReplacement = this;
            ((Conduit)leadPipe).junctionReplacement = this;
            researchCost = with(lead,200, rustyIron,200);
            buildCostMultiplier = 0.5f;

            /*expensive, since you can cheese the terribleness of pipes with this*/
            requirements(Category.liquid, with(rustyIron, 50, lead, 50));
            researchCost = with(lead, 40, rustyIron, 40);
        }};

        pipeBridge = new LiquidBridge("pipe-bridge"){{
            fadeIn = moveArrows = hasPower = false;
            range = 6;
            arrowSpacing = 6f;
            buildCostMultiplier = 0.5f;
            ((Conduit)ironPipe).bridgeReplacement = this;
            ((Conduit)leadPipe).bridgeReplacement = this;
            researchCost = with(iron, 150, lead, 150);
            requirements(Category.liquid, with(iron, 5, lead, 10));
        }};

        oilSeparator = new LegacyBlock("oil-separator"){{

        }};

        steamBoiler = new AttributeCrafter("steam-boiler"){{
            hasPower = hasLiquids = outputsLiquid = solid = true;
            rotate = false;

            size = 2;
            craftTime = 140f;
            boostScale = 0.1f;
            scaledHealth = 50f;
            liquidCapacity = 50f;
            envEnabled = Env.any;
            attribute = Attribute.heat;

            consumePower(1f);
            consumeLiquid(Liquids.water, 20/60f);
            researchCost = with(rustyIron, 50, lead, 50, copper, 50);
            outputLiquid = new LiquidStack(NyfalisItemsLiquid.steam, 12/60f);
            requirements(Category.liquid, with(rustyIron, 10, lead, 10, copper, 10));

            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawLiquidTile(Liquids.water),
                new DrawLiquidTile(NyfalisItemsLiquid.steam){{padding = 6f;}},
                new DrawRegion("-mid"),
                new DrawFlame()
            );
        }};

        Liquifier = new BoostableGenericCrafter("liquifier"){{
            hasLiquids = hasPower =  outputsLiquid =  consumesPower = true;

            size = 2;
            health = 600;
            craftTime = 60f;
            liquidCapacity = 25;

            consumePower(1f);
            consumeItem(rustyIron, 2);
            consumeItem(scrap, 1).boost();
            outputLiquid = new LiquidStack(Liquids.slag, 12f / 60f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawDefault());
            requirements(Category.liquid, with(graphite, 25, iron, 20, lead, 40, silicon, 10));
        }};

        lubricantMixer = new GenericCrafter("lubricant-mixer"){{
            hasLiquids = hasPower =  outputsLiquid =  consumesPower = true;

            size = 3;
            liquidCapacity = 25;
            craftTime = 60f* 2f;

            consumeItem(silicon);
            lightLiquid = lubricant;
            consumePower(1f);
            consumeLiquid(Liquids.oil, 12f / 60f);
            outputLiquid = new LiquidStack(lubricant, 10/60f);
            requirements(Category.liquid, with(quartz, 25, iron, 50, silicon, 40, cobalt, 10));
        }};

        demulsifier = new GenericCrafter("demulsifier"){{
            hasLiquids = hasPower =  outputsLiquid = consumesPower = rotate = true;
            size = 2;

            liquidCapacity =25f;
            craftTime = 2f * 60f;
            consumePower(1f);
            liquidOutputDirections = new int[]{1, 3};
            consumeLiquid(emulsiveSlop, 15f/ 60f);
            researchCost = with(iron, 500, lead, 800, copper, 800, rustyIron, 800);
            outputLiquids = LiquidStack.with(Liquids.water, 18f / 60f, Liquids.oil, 15f / 60f);
            requirements(Category.liquid, with(quartz, 40, iron, 25, lead, 50,copper, 50));
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.water){{
                        padLeft = 7f;
                    }},
                    new DrawLiquidTile(Liquids.oil){{
                        padRight = 7f;
                    }},
                    new DrawDefault()
            );
        }};

        //endregion
        //region Production
        rustElectrolyzer = new GenericCrafter("rust-electrolyzer"){{
            hasPower = hasItems = hasLiquids = solid = true;
            rotate = false;

            size = 2;
            craftTime = 120;
            liquidCapacity = 24f;
            envEnabled = Env.any;
            buildCostMultiplier = 0.4f;

            lightLiquid = Liquids.cryofluid;
            consumePower(1f);
            craftEffect = Fx.pulverizeMedium;
            outputItem = new ItemStack(iron, 2);
            consumeLiquid(Liquids.water, 12f / 60f);
            consumeItems(with(lead, 2, rustyIron, 2));
            researchCost = with(rustyIron, 50, lead, 50);
            requirements(Category.crafting, with(rustyIron, 15, lead, 30));
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.water), new VariantableDrawRegion(4));
        }};

        rustEngraver = new BoostableGenericCrafter("rust-engraver"){{
            hasPower = hasItems = hasLiquids = solid  = true;
            rotate = false;

            size = 4;
            craftTime = 60* 5;
            envEnabled = Env.any;
            buildCostMultiplier = 0.4f;
            liquidCapacity = 40;

            lightLiquid = Liquids.cryofluid;
            consumePower(200f / 60f);
            craftEffect = Fx.pulverizeMedium;
            outputItem = new ItemStack(iron, 3);
            consumeLiquid(Liquids.water, 38f / 60f).boost();
            consumeItems(with(quartz, 1, rustyIron, 6));
            researchCost = with(iron, 2000, lead, 2000, rustyIron, 2000, quartz, 1000, cobalt, 500);
            requirements(Category.crafting, with(iron, 25, lead, 30, rustyIron, 30, quartz, 25, cobalt, 25));
        }};

        //pard of the compoundCrucible
        hydrochloricGraphitePress  = new NyfLegacyBlock("hydro-graphite-press"){{
            hasItems = hasLiquids = hasPower = true;
        }};

        siliconKiln = new GenericCrafter("silicon-kiln"){{
            size = 2;
            craftTime = 3f * 60f;
            liquidCapacity = 30f;
            ambientSoundVolume = 0.07f;
            consumeItem(quartz, 4);
            consumeLiquid(oil, 25f/60f);
            consumePower(0.50f);
            hasPower = hasLiquids = true;
            craftEffect = Fx.smeltsmoke;
            ambientSound = Sounds.loopSmelter;
            outputItem = new ItemStack(Items.silicon, 1);
            requirements(Category.crafting, with(Items.copper, 30, Items.lead, 30, iron, 20));
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffef99")));
        }};

        //pard of the compoundCrucible
        siliconArcSmelter = new NyfLegacyBlock("silicon-arc-smelters"){{
            hasPower= hasItems = true;
        }};

        inductionSmelter = new SeparatorWithLiquidOutput("induction-smelter"){{
            size = 3;
            craftTime = 60* 3f;
            liquidCapacity = 30;
            results = with(
                aluminum, 3,
                cobalt, 1F
            );
            consumeItem(alcoAlloy);
            consumePower(80f /60f);
            liquidOutputDirections = new int[]{4};
            liquidOutputs = LiquidStack.with(Liquids.slag, 2.5f / 60f);
            requirements(Category.crafting, with(iron, 25, lead, 25, copper, 25, alcoAlloy, 20));

            squareSprite = false; //todo, this no work for some reason
            hasLiquids = outputsLiquid = rotate = quickRotate = true;
            drawer = new DrawMulti(
                new DrawRegion("-bottom"),
                new DrawLiquidTile(Liquids.slag, 2f),
                new DrawDefault(),
                new DrawRegion("-top"){{
                    buildingRotate = true;
                }}
            );
        }};

        copperWirePlan = new FactoryPlan("copper-wire-plan", "", 60f* 10f, with(copper, 2), with(copperWire, 1), 1f, 0);
        ironFramePlan = new FactoryPlan("iron-frame-plan", "", 60f* 10f, with(iron, 1, rustyIron, 3), with(ironFrame, 1), 1f, 0);
        crudeBatteryPlan = new FactoryPlan("crude-battery-plan","" ,60f* 10f, with(lead, 2, copper, 2), with(crudeBattery, 1), 1f, 0);
        basicRotorPlan = new FactoryPlan("basic-rotor-plan","" ,60f* 10f, with(iron, 2, copper, 1), with(basicRotor, 1), 1f, 0);
        ironPlatePlan = new FactoryPlan("iron-plate-plan","" ,60f* 10f, with(iron, 2, lead, 1), with(ironPlate, 1), 1f, 0);

        graphiteFramePlan = new FactoryPlan("graphite-frame-plan", "", 60f* 10f, with(ironPlate, 2, graphite, 1), with(graphiteFrame, 1), 200f / 60f, 0);
        siliconCircuitPlan = new FactoryPlan("silicon-circuit-plan", "", 60f* 10f, with(copperWire, 5, silicon, 1), with(siliconCircuit, 2), 200f / 60f, 0);
        ceramicPlatingPlan = new FactoryPlan("ceramic-plate-plan", "", 60f* 10f, with(ironPlate, 2, ash, 3), with(ceramicPlating, 1), 200f / 60f, 0);
        graphiteCellPlan = new FactoryPlan("graphite-cell-plan", "", 60f* 10f, with(crudeBattery, 1, graphite, 3), with(graphiteCell, 1), 200f / 60f, 0);
        electricMotorPlan = new FactoryPlan("electric-motor-plan", "", 60f* 10f, with(basicRotor, 1, iron, 3, graphite, 2 ), with(electricMotor, 1), 200f / 60f, 0);

        //componentFabricator -> used for all things items for unit production
        componentFabricator = new HeadacheCrafter("component-fabricator"){{
            outputsPower = true;
            craftEffect = Fx.steamCoolSmoke;
            size = 3;
            plans = Seq.with(copperWirePlan, ironFramePlan, crudeBatteryPlan, ironPlatePlan, basicRotorPlan, graphiteFramePlan, ceramicPlatingPlan, siliconCircuitPlan, electricMotorPlan, graphiteCellPlan);
            drawer = new DrawMulti(new DrawDefault(),new PlanDrawer(true));
            requirements(Category.crafting, with(iron, 25, lead, 50, copper, 50, rustyIron, 50));
        }};

        //replaced by slop
        mushBlender = new NyfLegacyBlock("mush-blender"){{
            hasLiquids = hasPower = true;
        }};

        siliconPlan = new FactoryPlan("silicon-plan", "-silicon", 60f * 3f, with(ash, 2, quartz, 2), with(silicon, 2), LiquidStack.with(Liquids.oil, 20 / 60f), null);
        graphitePlan = new FactoryPlan("graphite-plan", "-graphite", 60f * 3f, with(ash, 3), with(graphite, 3), LiquidStack.with(Liquids.oil, 25 / 60f), null, 45f / 60f, 0);

        compoundCrucible = new HeadacheCrafter("compound-crucible"){{
            outputsPower = true;
            craftEffect = Fx.pulverizeMedium;
            size = 3;
            plans = Seq.with(graphitePlan, siliconPlan);
            drawer = new DrawMulti(new DrawDefault(),new PlanDrawer(), new DrawFlame(Color.valueOf("ffef99")){{flameRadius = 5f; flameRadiusIn = 3.7f;}});
            requirements(Category.crafting, with(iron, 25, lead, 25, copper, 25, quartz, 50));
        }};


        ironSieve  = new Separator("iron-sieve"){{
            //not to be confused with iron shiv
            hasPower = hasItems = true;
            hasLiquids = false;

            size = 2;
            craftTime = 60f * 2f;
            itemCapacity = 20;

            results = with(
                rustyIron, 8,
                quartz, 3, iron, 1
            );
            consumePower(2f);
            consumeItem(Items.sand, 3);
            consumeItem(scrap, 3).boost();
            researchCost = with(lead, 700, rustyIron, 700);
            requirements(Category.crafting, with(rustyIron, 20, lead, 50));

            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawPistons(){{
                    sides = 1;
                    lenOffset = -2f;
                    sinScl = 2.8f;
                }}
            );
        }};

        discardDriver = new DiscardDriver("discard-driver"){{
            range = itemCapacity = 110;
            size = 1;
            bullet = new BasicBulletType(2.5f, 9){{
                width = 7f;
                height = 9f;
                lifetime = 10f;
                scaleLife = true;
            }};
            requirements(Category.crafting, with(iron, 25, copper, 25));
        }};

        //discardDriver -> mass driver that discards item in a random direction

        //endregion
        //region Units
        construct = new PowerUnitTurret("construct"){{
            size = 4;
            shootY = 0f;
            reload = 1200f;
            maxAmmo = 16;
            itemCapacity = 40;
            alternateCapacity = 40;
            failedMakeSoundPitch = 0.7f;
            boosterAlternate = true;
            hasAlternate = squareSprite = false;
            ammo(
                powerAmmoItem ,new SpawnHelperBulletType(){{
                    shootEffect = Fx.unitLand;
                    ammoMultiplier = 1f;
                    reloadMultiplier = 1.2f;
                    spawnUnit = spirit;
                }},
                quartz, new SpawnHelperBulletType(){{
                    shootEffect = Fx.shootBig;
                    ammoMultiplier = 1f;
                    reloadMultiplier = 0.75f;
                    spawnUnit = banshee;
                }},
                graphite, new SpawnHelperBulletType(){{
                    shootEffect = Fx.shootBig;
                    ammoMultiplier = 1f;
                    reloadMultiplier = 0.65f;
                    spawnUnit = phantom;
                }},
                silicon, new SpawnHelperBulletType(){{
                    shootEffect = Fx.shootBig;
                    ammoMultiplier = 1f;
                    reloadMultiplier = 0.65f;
                    spawnUnit = revenant;
                }}
            );
            alwaysShooting = unitFactory = true;
            consumePower(80f / 60f);
            failedMakeSound = NyfalisSounds.as2ArmorBreak;
            requiredItems = with(ironFrame, 3, copperWire, 3, crudeBattery, 3, basicRotor, 3);
            researchCost = with(lead, 1000, iron, 600, rustyIron, 1000);
            requirements(Category.units, with(iron, 50, lead, 50, rustyIron, 50));
        }};

        // arialConstruct -> offensive air units
        arialConstruct = new ItemUnitTurret("arial-construct"){{
            squareSprite = false;

            size = 4;
            shootY = 0f;
            reload = 1200f;
            maxAmmo = 15;
            itemCapacity = 80;
            alternateCapacity = 120;
            failedMakeSoundPitch = 0.7f;

            ammo(
                lead, new SpawnHelperBulletType(){{
                    shootEffect = Fx.shootBig;
                    ammoMultiplier = 2f;
                    spawnUnit = aero;
                    alternateType = new SpawnHelperBulletType(){{
                        shootEffect = Fx.shootBig;
                        ammoMultiplier = 2f;
                        reloadMultiplier = 0.50f;
                        spawnUnit = striker;
                    }};
                }},
                graphite, new SpawnHelperBulletType(){{
                    shootEffect = Fx.shootBig;
                    ammoMultiplier = 2f;
                    spawnUnit = pteropus;
                    alternateType = new SpawnHelperBulletType(){{
                        shootEffect = Fx.shootBig;
                        ammoMultiplier = 2f;
                        reloadMultiplier = 0.50f;
                        spawnUnit = acerodon;
                    }};
                }}
            );
            alwaysShooting = unitFactory = true;
            requiredItems = with(lead, 10, copper, 10 , basicRotor ,5);
            requiredAlternate = with(ironPlate, 10, copperWire, 10, crudeBattery, 10, basicRotor, 10, ironFrame, 10);
            failedMakeSound = NyfalisSounds.as2ArmorBreak;
            researchCost = with(lead, 800, copper, 800,  iron, 600);
            requirements(Category.units, with(iron, 100, lead, 100, copper, 100));
        }};

        // groundConstruct -> offensive ground units
        groundConstruct = new ItemUnitTurret("ground-construct"){{
            squareSprite = false;

            size = 4;
            reload = 1200f;
            maxAmmo = 15;
            itemCapacity = 80;
            alternateCapacity = 120;
            failedMakeSoundPitch = 0.7f;
            shootY = 3f * Vars.tilesize;

            ammo(
                lead, new SpawnHelperBulletType(){{
                    shootEffect = Fx.smeltsmoke;
                    ammoMultiplier = 2f;
                    spawnUnit = supella;
                    alternateType = new SpawnHelperBulletType(){{
                        shootEffect = Fx.shootBig;
                        ammoMultiplier = 2f;
                        reloadMultiplier = 0.50f;
                        spawnUnit = germanica;
                    }};
                }}
            );
            requiredItems = with(lead, 10, copper, 10, crudeBattery, 5);
            requiredAlternate = with(ironPlate, 10, copperWire, 10, crudeBattery, 10, ironFrame, 10);
            alwaysShooting = hoverShowsSpawn = arrowShootPos = unitFactory = true;
            failedMakeSound = NyfalisSounds.as2ArmorBreak;
            researchCost = with(rustyIron, 500, copper, 500,  iron, 300);
            requirements(Category.units, with(iron, 100, rustyIron, 100, copper, 100));
        }};

        //navalConstruct -> offensive naval units
        navalConstruct = new ItemUnitTurret("naval-construct"){{
            squareSprite = false;

            size = 4;
            reload = 1200f;
            maxAmmo = 15;
            itemCapacity = 80;
            alternateCapacity = 120;
            failedMakeSoundPitch = 0.7f;
            shootY = 2.5f * Vars.tilesize;

            ammo(
                graphite, new SpawnHelperBulletType(){{
                    shootEffect = Fx.smeltsmoke;
                    ammoMultiplier = 2f;
                    spawnUnit = bay;
                    alternateType = new SpawnHelperBulletType(){{
                        shootEffect = Fx.shootBig;
                        ammoMultiplier = 2f;
                        reloadMultiplier = 0.50f;
                        spawnUnit = blitz;
                    }};
                }},
                iron, new SpawnHelperBulletType(){{
                    shootEffect = Fx.smeltsmoke;
                    ammoMultiplier = 2f;
                    spawnUnit = sentry;
                    alternateType = new SpawnHelperBulletType(){{
                        shootEffect = Fx.shootBig;
                        ammoMultiplier = 2f;
                        reloadMultiplier = 0.50f;
                        spawnUnit = warden;
                    }};
                }}
            );
            requiredItems = with(copper, 10, lead, 10, crudeBattery, 3, basicRotor , 3);
            requiredAlternate = with(ironPlate, 10, copperWire, 10, crudeBattery, 10, basicRotor, 10, ironFrame, 10);
            failedMakeSound = NyfalisSounds.as2ArmorBreak;
            alwaysShooting = hoverShowsSpawn = floating = arrowShootPos = unitFactory = true;
            researchCost = with(lead, 1500, graphite, 500,  iron, 800);
            requirements(Category.units, with(iron, 100, lead, 100, graphite, 50));
        }};

        alternateArticulator = new Articulator("alternate-articulator"){{
            size = 3;

            ((ItemUnitTurret) arialConstruct).statArticulator = this;
            ((ItemUnitTurret) navalConstruct).statArticulator = this;
            ((ItemUnitTurret) groundConstruct).statArticulator = this;


            hasPower = consumesPower = conductivePower = true;
            consumePower(160/60f);
            requirements(Category.units, with(aluminum, 100, rustyIron, 150, copper, 150, iron, 100));

            drawer = new DrawMulti(
                new ValidFrontDrawRegion("-bottom"){{
                    buildingRotate = true;
                }},
                new DrawRegion(""),
                new DrawRegion("-top"){{ buildingRotate = true;}}
            );
        }};

        adaptiveFabricator = new Fabricator("adaptive-fabricator"){{
            size = 6;
            liquidCapacity = 90;
            consumePower(5f);
            consumeItems(with(copperWire, 40, ironFrame, 40, ceramicPlating, 20));
            consume(new ConsumeLubricant(45f / 60f));

            upgrades.addAll(
                //T3
                //new UnitType[]{serpent, reaper},
                new UnitType[]{warden, guardian},
                new UnitType[]{blitz, crusader},
                new UnitType[]{striker, falcon},
                new UnitType[]{germanica, luridiblatta},
                new UnitType[]{acerodon, nyctalus},
                //t4
                new UnitType[]{aero, vortex, falcon},
                new UnitType[]{supella, vaga, luridiblatta},
                new UnitType[]{bay, torrent, crusader},
                new UnitType[]{sentry, domination, guardian}

            );

            constructTime = 60f * 60f;
            hasPower = consumesPower = conductivePower = true;
            requirements(Category.units, with(aluminum, 200, iron, 200, copper, 200, cobalt, 150));
        }};

        alternateAmalgamator = new Articulator("alternate-amalgamator"){{
            size = 6;
            tier = 2;

            ((Fabricator) adaptiveFabricator).statArticulator = this;

            hasPower = consumesPower = conductivePower = true;
            consumePower(160/60f);
            requirements(Category.units, with(aluminum, 100, rustyIron, 150, cobalt, 150, iron, 100));

            drawer = new DrawMulti(
            new ValidFrontDrawRegion("-bottom"){{
                buildingRotate = true;
            }},
            new DrawRegion(""),
            new DrawRegion("-top"){{ buildingRotate = true;}}
            );
        }};

        //Unit Tree: t1 = construct
        // T2 = construct + alternateArticulator
        // t3 = t2 + reconstructor
        // t4 = t1 + t3 reconstructor + alternateAmalgamator
        // t5 = t1-t4 at assembler

        //alternateAmalgamator allows scarab to have payloa to fire/update
        scoutPad = new MechPad("scout-pad"){{
            hasPower = consumesPower = solid = true;
            consumePower(100f / 60f);
            researchCost = with(rustyIron, 3000, lead, 3000, copper, 3000, iron, 1500);
            requirements(Category.units, with(lead, 150, iron, 50, rustyIron, 150, copper, 110));
            size = 4;
        }};

        repairPin = new UnitRailingRepairTurret("repair-pin"){{
            //Intrusive bottom thoughts won -Rushie
            size = 3;
            shootY = 0;
            repairSpeed = 10f;
            repairRadius = 110;
            powerUse =100f / 60f;
            liquidCapacity = 40;

            length = 100f;
            acceptCoolant = true;
            outlineColor = NyfalisColors.contentOutline;
            lineFx = NyfalisFxs.repairPinBeam;
            fireFx = NyfalisFxs.repairPinShoot;
            requirements(Category.units, with(iron, 15, Items.lead, 20, copper, 20));
        }};


        fortifiedPayloadConveyor = new PayloadConveyor("fortified-payload-conveyor"){{
            requirements(Category.units, with(Items.graphite, 10 , iron, 5));
            canOverdrive = false;
            payloadLimit = 4f;
            size =4;
        }};

        fortifiedPayloadRouter = new PayloadRouter("fortified-payload-router"){{
            requirements(Category.units, with(Items.graphite, 15, iron, 10));
            canOverdrive = false;
            payloadLimit = 4f;
            size = 4;
        }};


        unitReplicator = new Replicator("unit-replicator"){{
            size = 5;
            delay = 5f;
            this.requirements(Category.units, BuildVisibility.sandboxOnly, ItemStack.with());
        }};

        unitReplicatorSmall = new Replicator("unit-replicator-small"){{
            size = 4;
            delay = 4f;

            this.requirements(Category.units, BuildVisibility.sandboxOnly, ItemStack.with());
        }};

        blackHoleContainer = new PayloadVoid("black-hole-container"){{
            size = 4;
            this.requirements(Category.units, BuildVisibility.sandboxOnly, ItemStack.with());
        }};

        //endregion
        //region Mines
        heavyMine = new ShockMine("heavy-mine"){{
            requirements(Category.effect, ItemStack.with(Items.lead, 25, aluminum, 12));
            hasShadow = rebuildable = false;
            floating = placeableLiquid = alwaysReplace = true;
            instantDeconstruct = true; //statra nerf
            size = 1;
            health = 100;
            armor = 2;
            damage = tileDamage = tendrils = length = 0;
            shots = 1;
            bullet = new ExplosionBulletType(185, 140){{
                trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                killShooter = collidesAir = true;
                fragBullets = 8;
                fragSpread = 45;
                fragRandomSpread = 0;
                buildingDamageMultiplier = 0.005f;
                fragBullet = new BulletType(){{
                    damage = 0;
                    knockback = 2f;
                    speed = 3;
                    lifetime = 20;
                    trailEffect = despawnEffect = smokeEffect = shootEffect = Fx.none;
                    hitEffect =  Fx.none;
                    collidesAir = false;
                    status = StatusEffects.slow;
                    statusDuration = 260;
                }};
            }};
        }};

        glitchMine = new ShockMine("glitch-mine"){{
            requirements(Category.effect, ItemStack.with(Items.lead, 25, cobalt, 12));
            hasShadow = rebuildable = false;
            floating = placeableLiquid = alwaysReplace = true;
            instantDeconstruct = true; //statra nerf
            size = 1;
            health = 60;
            armor = 2;
            damage = tileDamage = tendrils = length = 0;
            shots = 1;
            bullet = new ExplosionBulletType(80, 5*8){{
                trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                killShooter = collidesAir = true;
                fragBullets = 8;
                fragSpread = 45;
                fragRandomSpread = 0;
                buildingDamageMultiplier = 0.005f;
                fragBullet = new BulletType(){{
                    damage = 0;
                    knockback = 2f;
                    speed = 3;
                    lifetime = 10;
                    trailEffect = despawnEffect = smokeEffect = shootEffect = Fx.none;
                    hitEffect =  Fx.none;
                    collidesAir = false;
                    status = NyfalisStatusEffects.glitch;
                    statusDuration = 260;
                }};
            }};
        }};
        fragMine = new ShockMine("frag-mine"){{
            requirements(Category.effect, ItemStack.with(Items.lead, 25, quartz, 12));
            hasShadow = rebuildable = false;
            floating = placeableLiquid = alwaysReplace = true;
            instantDeconstruct = true; //statra nerf
            size = 1;
            shots = 1;
            health = 40;
            damage = tileDamage = tendrils = length = 0;
            bullet = new ExplosionBulletType(100, 10*8){{
                trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                killShooter = collidesAir = true;
                fragBullets = 8;
                fragSpread = 45;
                buildingDamageMultiplier = 0.005f;
                fragRandomSpread = 0;
                fragBullet = new BasicBulletType(3.0F, 10.0F) {{
                    width = 5.0F;
                    height = 12.0F;
                    shrinkY = 1.0F;
                    lifetime = 30.0F;
                    backColor = NyfalisItemsLiquid.quartz.color;
                    frontColor = Color.white;
                    despawnEffect = Fx.none;
                }};
            }};
        }};
        mossMine = new ShockMine("moss-mine"){{
            requirements(Category.effect, ItemStack.with(Items.lead, 25, condensedBiomatter, 12));
            hasShadow = rebuildable = false;
            floating = placeableLiquid = alwaysReplace = true;
            instantDeconstruct = true; //statra nerf
            size = 1;
            health = 20;
            shots = 1;
            damage = tileDamage = tendrils = length = 0;
            bullet = new ExplosionBulletType(50, 15*8){{
                trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                killShooter = collidesAir = true;
                fragBullets = 8;
                fragSpread = 45;
                fragRandomSpread = 0;
                buildingDamageMultiplier = 0.005f;
                fragBullet = new BulletType(){{
                    damage = 0;
                    knockback = 0.2f;
                    speed = 3;
                    lifetime = 10;
                    trailEffect = despawnEffect = smokeEffect = shootEffect = Fx.none;
                    hitEffect =  Fx.none;
                    collidesAir = false;
                    status = NyfalisStatusEffects.mossed;
                    statusDuration = 260;
                }};
            }};
        }};
        //endregion
        NyfalisTurrets.LoadTurrets();
        //region Power
        wire = new Wire("wire"){{
            floating = placeableLiquid = consumesPower = hasPower = conductivePower = true;
            solid = false;
            armor = 1f;
            health = 55;
            baseExplosiveness = 0.5f;
            //TODO: if possible 1 capacity & power usage, this only does usage
            consume(new ConsumePower(1/60f, 1f, true));
            researchCost = with(rustyIron, 20, lead, 20);
            requirements(Category.power, with(rustyIron, 1, lead, 2));
        }};

        superConductors = new Wire("super-conductor"){{
            floating = true;
            solid = false;
            armor = 5;
            health = 150;
            baseExplosiveness = 0.7f;
            researchCost = with(iron, 250, cobalt, 100, copper, 250);
            requirements(Category.power, with(cobalt, 10, iron, 5, copper, 10));
        }};

        wireBridge = new WireBridge("wire-bridge"){{
            consumesPower = outputsPower = floating = true;
            range = 5;
            armor = 3;
            health = 100;
            pulseMag = 0f;
            laserWidth = 0.4f;
            baseExplosiveness = 0.6f;
            buildCostMultiplier = 0.6f;
            consumePower(10f/ 60f);
            laserColor2 = Color.valueOf("65717E");
            laserColor1 = Color.valueOf("ACB5BA");
            researchCost = with(lead, 100, iron, 50);
            requirements(Category.power, with(iron, 10, Items.lead, 5));
        }};

        windMills = new WindMill("wind-mill"){{
            size = 3;
            liquidCapacity = 30;
            powerProduction = 20f/60f;
            attribute = Attribute.steam;
            consume(new ConsumeLubricant(15f / 60f)).boost();
            researchCost = with(rustyIron, 20, Items.lead, 20);
            requirements(Category.power, with(rustyIron, 35, Items.lead, 10));
        }};

        hydroMill = new ThermalGeneratorNoLight("hydro-mill"){{
                floating = true;

                size = 3;
                powerProduction = 20f/60f;
                ambientSoundVolume = 0.06f;

                attribute = hydro;
                ambientSound = Sounds.loopHum;
                researchCost = with(iron, 750, silicon, 500, lead, 1500, cobalt, 500);
                requirements(Category.power, with(iron, 20, silicon, 20, lead, 50, cobalt, 20));
                drawer = new DrawMulti(
                        new DrawRegion("-bottom"),
                        new DrawLiquidTile(Liquids.water, 2f){
                            @Override
                            public void draw(Building build){
                                Liquid drawn = build.floor().isLiquid && build.floor().asFloor().liquidDrop != null ?  build.floor().asFloor().liquidDrop : (drawLiquid != null ? drawLiquid : build.liquids.current());
                                LiquidBlock.drawTiledFrames(build.block.size, build.x, build.y, padLeft, padRight, padTop, padBottom, drawn, alpha);
                            }
                        },
                        new DrawBubbles(){{spread = 9f;}},
                        new DrawDefault(),
                        new DrawBlurSpin("-rotator", 0.45f * 9f){{blurThresh =  0.01f;}}
                );
            }
            @Override
            public void drawPlace(int x, int y, int rotation, boolean valid){
                drawPotentialLinks(x, y);
                drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);

                if(displayEfficiency && sumAttribute(attribute, x, y) != 0){
                    drawPlaceText(Core.bundle.formatFloat("bar.nyfalis-windmill", (sumAttribute(attribute, x, y) * 10) * 2, 0), x, y, valid);
                }
            }
        };

        hydroElectricGenerator = new ThermalGeneratorNoLight("hydro-electric-generator"){{
            placeableLiquid = floating = true;

            size = 5;
            effectChance = 0.011f;
            powerProduction = 35f/60f;
            ambientSoundVolume = 0.06f;

            attribute = hydro;
            generateEffect = Fx.steam;
            ambientSound = Sounds.loopHum;
            researchCost = with(iron, 1500, silicon, 1000, lead, 3000, cobalt, 1000);
            requirements(Category.power, with(iron, 50, silicon, 50, lead, 100, cobalt, 50));
            drawer = new DrawMulti(new DrawDefault(), new DrawBlurSpin("-rotator", 0.6f * 9f){{
                blurThresh = 0.01f;
            }});
        }};

        quartzBattery = new Battery("quartz-battery"){{
            size = 2;
            baseExplosiveness = 1f;
            consumePowerBuffered(1000f);
            researchCost = with(quartz, 500, lead, 500, silicon, 500);
            requirements(Category.power, with(quartz, 100, lead, 100, silicon, 100));
        }};

        steamyGenerator = new AttribueOrConsumeGenrator("steam-generator"){{
            requirements(Category.power, with(Items.copper, 35, rustyIron, 25, Items.lead, 40, iron, 30));
            passiveGenAmmount = 0.083f;
            powerProduction = 5.5f;
            itemDuration = 90f;
            hasLiquids = true;
            size = 3;
            attribute = Attribute.steam;
            generateEffect = Fx.generatespark;
            consumeLiquid(NyfalisItemsLiquid.steam, 20f / 60f);
            ambientSound = Sounds.loopSmelter;
            ambientSoundVolume = 0.06f;
        }};

        biomassGenerator = new GenericCrafterWithPower("biomass-generator"){{
            size = 2;
            powerProduction = 75f/60f;
            powerProductionBoosted = 155f/60f;
            craftTime = 90f;

            outputItem = new ItemStack(ash, 3);

            consumeItem(condensedBiomatter,2);
            consumeLiquid(oil, 20f / 60f).boost();
            researchCost = with(iron, 500, Items.lead, 1000, quartz, 500);
            requirements(Category.power, with(iron, 50, lead, 100, quartz, 50));
        }};

        steamTurbine = new ConsumeGenerator("steam-turbine"){{
            size = 6;
            powerProduction = 300f/60f;
            liquidCapacity = 40;

            consumeLiquid(NyfalisItemsLiquid.steam, 24f/60f);
            consumeLiquid(oil, 20f / 60f).boost();
            requirements(Category.power, with(iron, 50, silicon, 50, lead, 100, cobalt, 50));
        }};

        //endregion
        //region Wall
        rustyWall = new Wall("rusty-wall"){{
            floating = true;
            size = 1;
            variants = 5;
            health =  350;
            buildCostMultiplier = 0.8f;
            researchCost = with(rustyIron, 50);
            requirements(Category.defense,with(rustyIron, 6));
        }};

        rustyWallLarge = new Wall("rusty-wall-large"){{
            floating = true;
            size = 2;
            variants = 6;
            health =  1400;
            buildCostMultiplier = 0.7f;
            researchCost = with(rustyIron, 100);
            requirements(Category.defense,with(rustyIron, 24));
        }};

        rustyWallHuge = new Wall("rusty-wall-huge"){{
            floating = true;
            size = 3;
            health = 2690;
            buildCostMultiplier = 0.7f;
            researchCost = with(rustyIron, 200);
            requirements(Category.defense,with(rustyIron, 54));
        }};

        rustyWallGigantic = new Wall("rusty-wall-gigantic"){{
            size = 4;
            health = 3600;
            researchCost = with(rustyIron,4200, iron, 10);
            requirements(Category.defense, BuildVisibility.editorOnly, with(rustyIron, 1500));
        }};

        ironWall = new Wall("iron-wall"){{
            size = 1;
            health = 600;
            buildCostMultiplier = 0.7f;
            researchCost = with(iron, 500);
            requirements(Category.defense,with(iron, 6));
        }};

        ironWallLarge = new Wall("iron-wall-large"){{
            size = 2;
            health = 600 * 4;
            buildCostMultiplier = 0.7f;
            researchCost = with(iron, 1000);
            requirements(Category.defense,with(iron, 24));
        }};

        quartzWall = new Wall("quartz-wall"){{
            absorbLasers = true;
            size = 1;
            health = 850;
            buildCostMultiplier = 0.7f;
            researchCost = with(quartz, 500, lead, 200);
            requirements(Category.defense,with(quartz, 6, lead, 2));
        }};

        quartzWallLarge = new Wall("quartz-wall-large"){{
            absorbLasers = true;
            size = 2;
            health = 850 * 4;
            buildCostMultiplier = 0.7f;
            researchCost = with(quartz, 1000, lead, 200);
            requirements(Category.defense,with(quartz, 24, lead, 8));
        }};


        BulletType arcLighning =new ArcLightningBulletType(){{
            hitGround = hitAir = hitBuilding = true;
            rangeOverride = range = 150f;
            damage = 10;
            homingPower = 0.1f;

            status = StatusEffects.none;
            hitEffect= Fx.hitLancer;
            lightningColor = hitColor = new Color().set(cobalt.color).lerp(surgeAlloy.color, 0.5f);
        }};

        cobaltWall = new PoweredLightingWall("cobalt-wall"){{
            conductivePower = consumesPower = update = invertedBlending = true;
            vanillaLightning = false;
            size = 1;
            variantsSide = 1;
            health = 1200;
            buildCostMultiplier = 0.7f;
            lightningChancePowered = 0.06f;
            consumePower(5f / 60f);
            researchCost = with(cobalt, 500, copper, 200);
            requirements(Category.defense,with(cobalt, 6, silicon, 2));
            dischargeBullet = arcLighning;
        }};

        cobaltWallLarge = new PoweredLightingWall("cobalt-wall-large"){{
            conductivePower = consumesPower = update = true;
            vanillaLightning = false;

            variantsSide = 1;
            size = 2;
            health = 1200 * 4;
            buildCostMultiplier = 0.7f;
            lightningChancePowered = 0.06f;
            consumePower(20f / 60f);
            researchCost = with(cobalt, 1000, copper, 200);
            requirements(Category.defense,with(cobalt, 24, silicon, 8));
            dischargeBullet = arcLighning;
        }};

        //TODO: late game wall is also a router


        rustyScrapWall = new FunnyWall("rusty-scrap-wall"){{
            size = 1;
            variants = 1;
            armor = -10; //bc fck u
            health = 240;
            buildCostMultiplier = 0.8f;
            floating = true;
            requirements(Category.defense,  with(rustyIron, 15, scrap, 8));
        }};

        rustyScrapWallLarge = new FunnyWall("rusty-scrap-wall-large"){{
            size = 2;
            variants = 3;
            armor = -10; //bc fck u
            health = 960;
            buildCostMultiplier = 0.8f;
            floating = true;
            requirements(Category.defense,  ItemStack.mult(rustyScrapWall.requirements, 4));
        }};

        rustyScrapWallHuge = new FunnyWall("rusty-scrap-wall-huge"){{
            size = 3;
            variants  = 2;
            armor = -10; //bc fck u
            health = 2160;
            buildCostMultiplier = 0.8f;
            floating = true;
            requirements(Category.defense,  ItemStack.mult(rustyScrapWall.requirements, 9));
        }};

        rustyScrapWallGigantic = new FunnyWall("rusty-scrap-wall-gigantic"){{
            size = 4;
            health = 3840;
            armor = -10; //bc fck u
            buildCostMultiplier = 0.8f;
            floating = true;
            requirements(Category.defense,  ItemStack.mult(rustyScrapWall.requirements, 16));
        }};

        rustyScrapWallHumongous = new FunnyWall("rusty-scrap-wall-humongous"){{
            size = 5;
            armor = -10; //bc fck u
            health = 6000;
            buildCostMultiplier = 0.8f;
            floating = placeableLiquid = true;
            requirements(Category.defense, with(rustyIron, 15 * 16 , scrap, 8 * 16, iron, 5 * 16));
        }};

        //endregion
        //region Effect / Storage
        mendFieldProjector = new DirectionalMendProjector("mend-field-projector");

        taurus = new PowerTurret("taurus"){{
            size = 3;
            reload = 100f;
            rotateSpeed = 3;
            inaccuracy = 15f;
            shootCone = 12f;
            coolantMultiplier = 1.6f;
            shootY = (size * tilesize / 2f) -2f;
            liquidCapacity = 50;

            hasPower = targetHealing = true;
            targetAir = targetGround  = false;
            shootType = new HealOnlyBulletType(5.2f, -5, "olupis-diamond-bullet"){{
                collidesTeam = despawnHit = splashDamagePierce = alwaysSplashDamage = despawnHitEffect = true;
                collidesAir = absorbable = false;
                width = 8f;
                height = 13f;
                healPercent = 0.75f;
                splashDamage = 0f;
                /*added slight homing, so it can hit 1x1 blocks better or at all*/
                homingRange = 5f;
                homingPower = 0.2f;
                splashDamageRadius = Vars.tilesize * 2;
                backColor = Pal.heal;
                hitEffect = despawnEffect = NyfalisFxs.taurusHeal;
                frontColor = Color.white;
                shootSound = Sounds.shootSap;
            }};
            shootEffect = NyfalisFxs.shootTaurus;
            smokeEffect = Fx.none;
            group = BlockGroup.projectors;
            outlineColor = nyfalisBlockOutlineColour;
            consumePower(100f / 60f);
            researchCost = with(iron, 100, lead, 200);
            shoot = new ShootAlternateAlt(9f);
            shoot.shots = 4;
            shoot.shotDelay = 11f;

            recoils = 2;
            recoil = 0.5f;
            drawer = new DrawTurret("iron-"){{
                for(int i = 0; i < 2; i ++){
                    int f = i;
                    parts.add(new RegionPart("-barrel-" + (i == 0 ? "l" : "r")){{
                        progress = PartProgress.recoil;
                        recoilIndex = f;
                        under = true;
                        moveY = -3;
                    }});
                }
            }};
            limitRange(-23f);
            flags = EnumSet.of(BlockFlag.repair, BlockFlag.turret);
            coolant = consume(new ConsumeLubricant(45f / 60f));
            requirements(Category.effect, with(iron, 40, Items.lead, 30));
        }};

        cutboi = new NyfalisPowerCutter("shear"){{
            reload = 30;
            recoilTime = 5;
            shootY = 0;
            inaccuracy = 0.5f;
            rotateSpeed = 3f;
            minWarmup = 0.9f;
            minRange = 30f;
            smokeEffect = shootEffect =  Fx.none;
            shootType = new LaserBulletType(){{
                lifetime = 4f;
                length = 80;
                width = 0;
                damage = 20;
                trailEffect = laserEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
            }

                @Override
                public void draw(Bullet b){
                     //dont
                }
            };
            drawer = new DrawTurret("iron-"){{
                targetAir = false;
                emitLight = true;

                size = 2;
                recoil = 0;
                armor = 2f;
                range = 80f;
                health = 3000;
                fogRadius = 13;
                lightRadius = 37;
                shootCone = 180f;
                liquidCapacity = 5f;
                coolantMultiplier = 3f;
                turretLayer = Layer.power +0.3f;
                for(int i = 0; i < 6; i++){
                    int finalI = i;
                    boolean isOdd = finalI % 2 != 0;
                    parts.add(new RegionPart("-truss"){{
                        //todo: make a progress that scales how far the target pos is
                        progress = p -> Mathf.lerp(0, NyfPartProgress.tarDistP.get(p),  PartProgress.warmup.get(p));
                        mirror = true;
                        under = false;
                        y = 5;
                        x = 4;
                        moveY = 8f * finalI;
                        moveRot = isOdd ? -45 : 45;
                        layer = Layer.power + 0.2f;
                    }});
                }

                parts.addAll(
                new RegionPart("-blade"){{
                    mirror = true;
                    under = true;
                    progress = p -> Mathf.lerp(0, NyfPartProgress.tarDistP.get(p),  PartProgress.warmup.get(p));
                    y = 0;
                    moveY = 53;
                    moveRot = -45;
                    moves.add(new PartMove(PartProgress.recoil, 0, 0, 45f));
                }}
                );
            }};
            lightColor = turretLightColor;
            outlineColor = nyfalisBlockOutlineColour;
            shootSound = NyfalisSounds.shootSnip;
            coolant = consume(new ConsumeLubricant(15f / 60f));
            consumePower(30f / 60f);
            researchCost = with(iron, 200, copper, 150);
            requirements(Category.effect, with(iron, 60, copper, 50));

        }};

        lamp = new LightBlock("lamp"){{
            requirements(Category.effect, BuildVisibility.lightingOnly, with(iron, 8, Items.lead, 8, copper, 8));
            brightness = 0.75f;
            radius = 60f;
            consumePower(20f / 60f);
        }};

        ladar = new Ladar("ladar"){{
            emitLight = true;
            size = 2;
            fogRadius = 16;
            lightRadius = 130f;
            rotateSpeed = 10f;
            glowMag = glowScl = 0f;
            discoveryTime = 60f * 40f;
            consumePower(240f/60f);
            type = ladarHelper;
            glowColor = Color.valueOf("00000000");
            requirements(Category.effect, with(Items.lead, 60, Items.graphite, 50, iron, 10));
        }};

        search = new Ladar("search"){{
            emitLight = spotlight = true;
            size = 3;
            fogRadius = 32;
            lightRadius = 200;
            rotateSpeed = 5f;
            glowMag = glowScl = 0f;
            discoveryTime = 60f * 40f;
            spotted = NyfalisStatusEffects.marked;
            consumePower(400/60f);
            type = ladarHelper;
            glowColor = Color.valueOf("00000000");
            requirements(Category.effect, with(Items.lead, 60, cobalt, 50, iron, 10, quartz, 30));
        }};

        //Healing turret that has ammo and water to heal better

        //TODO: Mister -> phase fluid = to give units a temp shied
        //  -> Nanite Fluid = repair

        fortifiedContainer = new StorageBlock("fortified-container"){{
            coreMerge = false;
            size = 2;
            health =  790;
            buildCostMultiplier = 0.7f;
            scaledHealth = itemCapacity = 150;
            researchCost = with(iron, 250, rustyIron, 550);
            requirements(Category.effect, with(rustyIron, 55, iron, 25));
        }};

        fortifiedVault = new StorageBlock("fortified-vault"){{
            coreMerge = false;
            size = 3;
            health =  1500;
            scaledHealth = 120;
            itemCapacity = 950;
            researchCost = with(iron, 550, rustyIron, 800, silicon, 550);
            requirements(Category.effect, with(rustyIron, 75, iron, 50, silicon, 50));
        }};

        int coreBaseHp = 600, coreUnitCap = 4;
        float corePowerScl = 100f /60f;

        coreRemnant = new PropellerCoreBlock("core-remnant"){{
            alwaysUnlocked = isFirstTier = requiresCoreZone = true;
            squareSprite = false;
            unitAmount = size = 2;
            itemCapacity = 1500;
            unitCapModifier  = 8;
            health = coreBaseHp * 3;
            thrusterLength = (14f/2.4f)/4f;
            unitPowerCost = corePowerScl * 1;
            buildCostMultiplier = researchCostMultiplier = 0.5f;

            unitType = gnat;
            requirements(Category.effect, with(rustyIron, 1300, lead, 1300));
        }};

        coreEmergent = new PropellerCoreBlock("core-emergent"){{

            offset = 17.5f;
            itemCapacity = 3000;
            unitAmount = size = 3;
            unitPowerCost = corePowerScl * 2;
            health = Math.round(coreBaseHp * 3.5f);
            buildCostMultiplier = researchCostMultiplier = 0.5f;
            unitCapModifier = (coreRemnant.unitCapModifier + (coreUnitCap));
            researchCost = with(lead, 13500, rustyIron, 13500, iron, 13500, copper, 13500);

            unitType = pedicia;
            requirements(Category.effect, with(rustyIron, 1300, lead, 1300, iron, 1000, copper, 1300));
        }};

        corePrime= new PropellerCoreTurret("core-prime"){{
            offset = 25f;
            reload = 80f;
            itemCapacity = 4500;
            shootX = shootY = 0f;
            unitAmount = size = 4;
            range = 20f * Vars.tilesize;
            unitPowerCost = corePowerScl * 3;
            health = Math.round(coreBaseHp * 5f);
            buildCostMultiplier = researchCostMultiplier = 0.5f;
            unitCapModifier = (coreRemnant.unitCapModifier + (coreUnitCap * 2));

            parts.add(
                        new RegionPart("-door-bl"){{
                            progress = PartProgress.warmup;
                            moveX = moveY = -2.8f;
                            growX = growY = -1f;
                            mirror = outline = false;
                        }},
                        new RegionPart("-door-tl"){{
                            progress = PartProgress.warmup;
                            moveX = -2.8f;
                            moveY = 2.8f;
                            growX = growY = -1f;
                            mirror = outline = false;
                        }},
                        new RegionPart("-door-br"){{
                            progress = PartProgress.warmup;
                            moveX = 2.8f;
                            moveY = -2.8f;
                            growX = growY = -1f;
                            mirror = outline = false;
                        }},
                        new RegionPart("-door-tr"){{
                            progress = PartProgress.warmup;
                            moveX = moveY = 2.8f;
                            growX = growY = -1f;
                            mirror = outline = false;
                        }}
            );
            unitType = phorid;
            shootEffect = Fx.none;
            shootSound = Sounds.massdriver;
            requirements(Category.effect, with(rustyIron, 3000, lead, 3000, iron, 1500, graphite, 500, copper, 1500));
            shootType = new ArtilleryBulletType(3f, 50){{
                lifetime = 80f;
                knockback = 1f;
                homingRange = 50f;
                width = height = 9f;
                splashDamage = 25f;
                homingPower = 0.08f;
                reloadMultiplier = 1.2f;
                buildCostMultiplier = 0.5f;
                splashDamageRadius = 30f;

                collidesTiles = false;
                frontColor = iron.color;
                backColor = rustyIron.color;
                collidesAir = collidesGround;
            }};
        }};

        coreApex = new PropellerCoreTurret("core-apex"){{
            reload = 55f;
            itemCapacity = 6000;
            shootX = shootY = 0f;
            unitAmount = size = 5;
            range = 25f * Vars.tilesize;
            unitPowerCost = corePowerScl * 4;
            health = Math.round(coreBaseHp * 6.5f);
            buildCostMultiplier = researchCostMultiplier = 0.5f;
            unitCapModifier = (coreRemnant.unitCapModifier + (coreUnitCap) * 3);

            unitType = diptera;
            limitRange(0);
            shootEffect = Fx.none;
            shootSound = Sounds.massdriver;
            requirements(Category.effect, with(rustyIron, 3400, lead, 4000, iron, 3500, silicon, 2500, graphite, 2500, quartz, 2500, copper, 2500));
            shootType = new SapBulletType(){{
                damage = 150f;
                width = 0.8f;
                lifetime = 20f;
                sapStrength = 0f;
                length = 25f * Vars.tilesize;
                status = StatusEffects.none;
                despawnEffect = Fx.none;
                color = hitColor = rustyIron.color;
                collidesTiles = false;
                collidesAir = collidesGround = collidesTeam = true;
            }};
        }};

        coreAscendant = new PropellerCoreTurret("core-ascendant"){{
            reload = 35f;
            itemCapacity = 7500;
            shootX = shootY = 0f;
            unitAmount = size = 6;
            range = 35 * Vars.tilesize;
            unitPowerCost = corePowerScl * 5;
            health = Math.round(coreBaseHp * 8f);
            buildCostMultiplier = researchCostMultiplier = 0.5f;
            unitCapModifier = (coreRemnant.unitCapModifier + (coreUnitCap) * 4);

            unitType = diptera;
            shootEffect = Fx.none;
            shootSound = Sounds.massdriver;
            targetGround = targetHealing = targetAir = true;
            requirements(Category.effect, with(rustyIron, 6000, lead, 6000, iron, 4500, silicon, 4500, graphite, 4500, quartz, 2500, cobalt, 2500, copper, 2500));
            shootType = new RailBulletType(){{
                length = 255f;
                damage = 500;
                pierceDamageFactor = 0.5f;
                hitColor = iron.color;
                hitEffect = endEffect = Fx.hitBulletColor.wrap(iron.color);
                lineEffect = new Effect(20f, e -> {
                    if(!(e.data instanceof Vec2 v)) return;
                    color(e.color);
                    stroke(e.fout() * 0.9f + 0.6f);
                    Fx.rand.setSeed(e.id);
                    for(int i = 0; i < 7; i++){
                        Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
                        Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
                    }
                    e.scaled(14f, b -> {
                        stroke(b.fout() * 1.5f);
                        color(e.color);
                        Lines.line(e.x, e.y, v.x, v.y);
                    });
                });
            }};
        }};

        //Todo: paragon, comstant beam style like reaper

        deliveryCannon = new LimitedLaunchPad("delivery-cannon"){{
            size = 4;
            itemCapacity = 100;
            launchTime = 60f * 20;
            buildCostMultiplier = 0.5f;
            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawPistons()
            );
            requirements(Category.effect, BuildVisibility.notLegacyLaunchPadOnly, with(rustyIron, 75, iron, 50, silicon, 50, cobalt, 10));
        }};

        deliveryReciver = new LimitedLandingPad("delivery-receiver"){{
            size = 5;
            itemCapacity = 100;
            cooldownTime = 60f;
            buildCostMultiplier = 0.5f;
            liquidCapacity = 3000f;
            consumeLiquidAmount = 1500f;
            consumeLiquid = NyfalisItemsLiquid.steam;
            coolingEffect = new RadialEffect(Fx.steamCoolSmoke, 4, 90f, 9.5f, 180f);
            requirements(Category.effect, BuildVisibility.notLegacyLaunchPadOnly, with(rustyIron, 75, iron, 50, silicon, 50, cobalt, 10));
        }};

        deliveryTerminal = new DeliveryTerminal("delivery-terminal"){{
            size = 2;
            buildCostMultiplier = 0.5f;
            toggleEffect = Fx.flakExplosionBig;
            requirements(Category.effect, BuildVisibility.notLegacyLaunchPadOnly, with(rustyIron, 150, iron, 100, lead, 150));
        }};

        lightWall = new PrivilegedLightBlock("light-wall"){{
            alwaysUnlocked = true;
            brightness = 0.75f;
            radius = 140f;
            requirements(Category.effect, BuildVisibility.editorOnly, with());
        }};

        //endregion
        //region Logic
        fortifiedMessageBlock = new MessageBlock("fortified-message-block"){{
            health = 100;
            researchCost = with(iron, 500, graphite, 500);
            requirements(Category.logic, with(Items.graphite, 10, iron, 5));
        }};


        nyfalianProcessor = new NyfalisLogicBlock("nyfalian-processor"){{
            requirements(Category.logic, BuildVisibility.worldProcessorOnly, with());
            allStatements.addAll(privStatements);

            canOverdrive = false;
            targetable = false;
            instructionsPerTick = 8;
            forceDark = true;
            privileged = true;
            size = 1;
            maxInstructionsPerTick = 1000;
            range = Float.MAX_VALUE;
        }};

        mechanicalProcessor = new NyfalisLogicBlock("mechanical-processor"){{
            requirements(Category.logic, with(lead, 150, iron, 50, graphite, 30, silicon, 30));
            researchCost = with(lead, 500, iron, 350, graphite, 100, silicon, 100);

            buildCostMultiplier = 0.45f;
            instructionsPerTick = 25;
            range = 10 * 22;
            size = 2;
        }};

        mechanicalSwitch = new SwitchBlock("mechanical-switch"){{
            size = 2;
            requirements(Category.logic, with(Items.graphite, 5, iron, 10));
            researchCost = with(iron, 400, graphite, 200);
        }};

        mechanicalRegistry = new MemoryBlock("mechanical-registry"){{
            size = 2;
            requirements(Category.logic, with(Items.graphite, 10, iron, 10, silicon, 5));
            researchCost = with(iron, 400, graphite, 400, silicon, 200);
        }};

        floodDisruptor = new ImpactReactor("flood-disruptor"){{
            //Flood helper
            requirements(Category.logic, with(rustyIron, 500, Items.silicon, 100, Items.graphite, 250, iron, 250, copper, 300, lead, 300));
            size = 5;
            health = 900;
            powerProduction = 20f/60f;
            itemDuration = 3f * 60f;
            ambientSound = Sounds.loopPulse;
            ambientSoundVolume = 0.07f;
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawDefault(),
                    new DrawPlasma(){
                        @Override
                        public void draw(Building build){
                            Draw.blend(Blending.additive);
                            for(int i = 0; i < regions.length; i++){
                                float r = ((float)regions[i].width * regions[i].scl() - 3f + Mathf.absin(Time.time, 2f + i * 1f, 5f - i * 0.5f));

                                Draw.color(plasma1, plasma2, (float)i / regions.length);
                                Draw.alpha((0.3f + Mathf.absin(Time.time, 2f + i * 2f, 0.3f + i * 0.05f)) * build.warmup());
                                Draw.rect(regions[i], build.x, build.y, r, r); //no spin
                            }
                            Draw.color();
                            Draw.blend();
                        }
                    }
            );

            consumePower(300f/60f);
            consumeItems(new ItemStack(iron, 1), new ItemStack(silicon, 1));
            consumeLiquid(lubricant, 0.25f);
        }};
        //endregion
        //special
        scarabRadar = new Ladar("scarab-block-radar"){{
            underBullets = true;
            health = 100;
            armor = -2;
            size = 1;
            discoveryTime = 3400;
            decayDelay =Mathf.round(Time.toMinutes * 6f);
            glowColor = Color.valueOf("3ed09a");
            outlineColor = NyfalisColors.contentOutline;
            requirements(Category.effect, BuildVisibility.sandboxOnly, ItemStack.with(new Object[]{Items.silicon, 1}));
            fogRadius = 20;
        }};

        if(headless) return;
    }

    public static void NyfalisBlocksPlacementFix(){
        nyfalisBuildBlockSet.clear();

        sandBoxBlocks.addAll(
                /*just to make it easier for testing and/or sandbox*/
                itemSource, itemVoid, liquidSource, liquidVoid, payloadSource, payloadVoid, powerSource, powerVoid, heatSource,
                worldProcessor, worldMessage, boomPuffActive, boomPuffPassive
        );

        Vars.content.blocks().each(b->{
            //Mod support?
            if(b instanceof  FactoryPlan) factoryPlans.add(b);
            if(b instanceof  Articulator) alternateModules.add(b);

            if(b.name.startsWith("olupis-")){
                if(b.isVisible() || b.buildVisibility == BuildVisibility.fogOnly) nyfalisBuildBlockSet.add(b);
                if(b instanceof SteamVent) vents.add(b);
                allNyfalisBlocks.add(b);
                b.envEnabled = NyfalisAttributeWeather.nyfalian;
                b.outlineColor = NyfalisColors.contentOutline;
            }

            if(b.techNode != null && b.techNode.planet == Planets.serpulo || b.isOnPlanet(Planets.serpulo)){
                if (!sandBoxBlocks.contains(b)) hiddenNyfalisBlocks.add(b);
                b.shownPlanets.removeAll(NyfalisPlanets.planetList);

            }
            
        });


        nyfalisCores.addAll(coreRemnant, coreEmergent, corePrime, coreApex, coreAscendant);

        rainRegrowables.addAll(grass, moss, mossierStone, mossyStone, yellowGrass, cinderBloomGrass, cinderBloomiest, cinderBloomiest, mossierDirt, mossyDirt, frozenDirt, frozenGrass, frozenSlop);
        spreadingTiles.addAll(mycelium, yourcelium, ourcelium, theircelium);

        unitReplicator.replacement = NyfalisBlocks.rustyScrapWallHumongous;
        unitReplicatorSmall.replacement = NyfalisBlocks.rustyScrapWallGigantic;
        siliconArcSmelter.replacement = compoundCrucible;
        hydrochloricGraphitePress.replacement = compoundCrucible;
        mushBlender.replacement = rustyScrapWallLarge;

        if(headless)return;

        ((LimitedLandingPad) deliveryReciver).podRegion = ((LimitedLaunchPad) deliveryCannon).podRegion;
        yellowTree.mapColor = yellowTreeBlooming.mapColor = Color.valueOf("A0A54DFF");
        cinderBloomy.mapColor = new Color().set(cinderBloomGrass.mapColor).lerp(basalt.mapColor, 0.65f);
        cinderBloomier.mapColor = new Color().set(cinderBloomGrass.mapColor).lerp(basalt.mapColor, 0.45f);
        cinderBloomiest.mapColor = new Color().set(cinderBloomGrass.mapColor).lerp(basalt.mapColor, 0.15f);
        mossyStoneWall.mapColor = new Color(mossStoneWall.mapColor).lerp(stoneWall.mapColor, 0.65f).lerp(Color.black, 0.1f);
        mossierStoneWall.mapColor = new Color(mossStoneWall.mapColor).lerp(stoneWall.mapColor, 0.45f).lerp(Color.black, 0.1f);
        mossierStoneWall.mapColor = new Color(mossStoneWall.mapColor).lerp(stoneWall.mapColor, 0.15f).lerp(Color.black, 0.1f);
        mossyStone.mapColor = new Color().set(mossStone.mapColor).lerp(stone.mapColor, 0.75f);
        mossierStone.mapColor = new Color().set(mossStone.mapColor).lerp(stone.mapColor, 0.5f);
        mossiestStone.mapColor = new Color().set(mossStone.mapColor).lerp(stone.mapColor, 0.25f);
        mossierDirt.mapColor = new Color().set(mossyStone.mapColor).lerp(dirt.mapColor, 0.5f);
        mossyDirt.mapColor = new Color().set(mossyStone.mapColor).lerp(dirt.mapColor, 0.5f);
        frozenDirt.mapColor = new Color().set(ice.mapColor).lerp(dirt.mapColor, 0.5f);
        coralReef.mapColor = deepwater.mapColor;
    }
}
