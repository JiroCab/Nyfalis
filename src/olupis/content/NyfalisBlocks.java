package olupis.content;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.EnumSet;
import arc.struct.ObjectSet;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.RegionPart;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.ShockMine;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.legacy.LegacyBlock;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import olupis.input.NyfalisShaders;
import olupis.world.blocks.defence.*;
import olupis.world.blocks.distribution.*;
import olupis.world.blocks.environment.*;
import olupis.world.blocks.misc.*;
import olupis.world.blocks.power.*;
import olupis.world.blocks.processing.*;
import olupis.world.blocks.turret.UnstablePowerTurret;
import olupis.world.blocks.unit.*;
import olupis.world.consumer.ConsumeLubricant;
import olupis.world.entities.bullets.*;
import olupis.world.entities.parts.*;
import olupis.world.entities.pattern.ShootAlternateAlt;

import java.util.Objects;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.oil;
import static mindustry.type.ItemStack.with;
import static olupis.content.NyfalisAttributeWeather.*;
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
        redSand, riverSand, lumaGrass, yellowGrass, pinkGrass, mossyDirt,  hardenMud, mossyhardenMud, muddyGrass,
        frozenGrass, frozenDirt, frozenMud, crackedIce, redSandSnow, snowySand, frozenTar, frozenSlop,
        cinderBloomGrass, cinderBloomy, cinderBloomier, cinderBloomiest, mossyStone, mossStone, mossierStone, mossiestStone,
        grassyVent, mossyVent, stoneVent, basaltVent, hardenMuddyVent, dirtVent,
        redSandVent, snowVent, mycelium, yourcelium, ourcelium, theircelium,
        beachSandFloor, gypsumFloor, pumiceFloor, galenaFLoor, rustyFloor, rustFloor, forestGrass,

        /*Liquid floors*/
        redSandWater, lumaGrassWater, brimstoneSlag, algaeWater, algaeWaterDeep, pinkGrassWater, yellowMossyWater, coralReef, slop, slopDeep, lubricantPool,

        /*props*/
        yellowBush, lumaFlora, bush, mossyBoulder, mossBoulder, infernalBloom, redSandBoulder, glowBloom, luminiteBoulder, deadBush, glowLilly,
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

        heavyMine,fragMine,glitchMine,mossMine,
        corroder, dissolver, shredder, hive, escalation, shatter, avenger, aegis, obliterator, slash, laceration,strata,porcupine, cutboi, superextendocutboi,

        rustyDrill, steamDrill, hydroElectricDrill, steamAgitator, garden, fortifiedRadiator,

        rustyIronConveyor, ironConveyor, cobaltConveyor, ironRouter, ironDistributor ,ironJunction, ironBridge, ironOverflow, ironUnderflow, ironUnloader, rustedBridge,

        leadPipe, ironPipe, pipeRouter, pipeJunction, pipeBridge, displacementPump, massDisplacementPump, ironPump, rustyPump, fortifiedTank, fortifiedCanister,
        steamBoiler, broiler, oilSeparator, lubricantMixer, demulsifier,

        wire, wireBridge, superConductors, windMills, hydroMill, hydroElectricGenerator, quartzBattery, mirror, solarTower, steamTurbine,

        rustyWall, rustyWallLarge, rustyWallHuge, rustyWallGigantic, ironWall, ironWallLarge, rustyScrapWall, rustyScrapWallLarge, rustyScrapWallHuge, rustyScrapWallGigantic, rustyScrapWallHumongous, quartzWall, quartzWallLarge, cobaltWall, cobaltWallLarge,

        bioMatterPress, rustElectrolyzer, hydrochloricGraphitePress, ironSieve, siliconArcSmelter, rustEngraver, pulverPress, discardDriver, siliconKiln, inductionSmelter,

        construct, arialConstruct, groundConstruct, navalConstruct, alternateArticulator, adaptiveFabricator,ultimateAssembler, fortifiedPayloadConveyor, fortifiedPayloadRouter, repairPin, scoutPad, blackHoleContainer,

        coreRemnant, coreVestige, coreRelic, coreShrine, coreTemple, fortifiedVault, fortifiedContainer, deliveryCannon, deliveryTerminal, deliveryAccelerator,
        mendFieldProjector, taurus, ladar,

        fortifiedMessageBlock, mechanicalProcessor, analogProcessor, mechanicalSwitch, mechanicalRegistry,

        /*special*/
        scarabRadar, floodDisruptor
    ; //endregion
    public static UnstablePowerTurret cascade;
    public static Replicator unitReplicator, unitReplicatorSmall;

    public static Color nyfalisBlockOutlineColour = NyfalisColors.contentOutline;;
    public static ObjectSet<Block>
            nyfalisBuildBlockSet = new ObjectSet<>(), sandBoxBlocks = new ObjectSet<>(), nyfalisCores = new ObjectSet<>(), allNyfalisBlocks = new ObjectSet<>(), hiddenNyfalisBlocks = new ObjectSet<>(),
            rainRegrowables = new ObjectSet<>();

    public static void LoadWorldTiles(){
        //region Ores / Overlays
        oreIron = new OreBlock("ore-iron", rustyIron);

        oreIronWall = new OreBlock("ore-iron-wall", rustyIron){{
            wallOre = true;
        }};

        oreCobalt = new OreBlock("ore-cobalt", cobalt);

        /*uses ore's item as a name block in editor*/
        oreOxidizedCopper = new OreBlock("ore-oxidized-copper", copper);
        oreOxidizedLead = new OreBlock("ore-oxidized-lead", lead);

        oreQuartz = new OreBlock("ore-quartz", quartz){{
            variants = 3;
        }};

        oreAlco = new OreBlock("ore-alco", alcoAlloy){{
            variants = 2;
        }};

        glowSprouts = new OverlayFloor("glow-sprout"){{
            emitLight = true;
            needsSurface = false;
            variants = 1;
            lightRadius = 10f;
            //is sad that BlockRenderer.java does not render light from overlays, but ill keep it incase TnT
        }};

        lumaSprouts = new OverlayFloor("luma-sprout"){{
            variants = 1;
            needsSurface = false;
        }};

        redCorals = new OverlayFloor("red-corals"){{
            variants = 1;
            needsSurface = false;
        }};

        blueCorals = new OverlayFloor("blue-corals"){{
            variants = 1;
            needsSurface = false;
        }};

        greenCorals = new OverlayFloor("green-corals"){{
            variants = 1;
            needsSurface = false;
        }};

        kelp = new OverlayFloor("kelp"){{
            variants = 1;
            needsSurface = false;
        }};

        //endregion
        // region Floors
        beachSandFloor = new Floor("beach-sand-floor"){{
            itemDrop = Items.sand;
            playerUnmineable = true;
            variants = 4;
            attributes.set(Attribute.oil, 0.75f);
        }};
        gypsumFloor = new Floor("gypsum-floor"){{
            variants = 4;
        }};
        galenaFLoor = new Floor("galena-floor"){{
            variants = 4;
        }};
        pumiceFloor = new Floor("pumice-floor"){{
            variants = 4;
        }};
        rustyFloor = new Floor("rusty-floor"){{
            variants = 4;
            blendGroup = metalFloor;
        }};
        rustFloor = new Floor("rust-floor"){{
            variants = 4;
        }};
        redSand = new Floor("red-sand-floor"){{
            itemDrop = Items.sand;
            playerUnmineable = true;
            attributes.set(Attribute.oil, 1.5f);
        }};
		riverSand = new Floor("river-sand"){{
            itemDrop = Items.sand;
            attributes.set(Attribute.oil, 1.2f);
        }};

        redSandSnow = new Floor("red-sand-snow"){{
            itemDrop = Items.sand;
            playerUnmineable = true;
            attributes.set(Attribute.oil, 1.5f);
        }};

        lumaGrass = new Floor("luma-grass"){{
            variants = 3;
            attributes.set(bio, 0.08f);
            attributes.set(Attribute.water, 0.15f);
        }};

        yellowGrass = new Floor("yellow-grass"){{
            variants = 4;
            attributes.set(bio, 0.08f);
            attributes.set(Attribute.water, 0.15f);
        }};

        pinkGrass = new Floor("pink-grass"){{
            variants = 4;
            attributes.set(bio, 0.08f);
            attributes.set(Attribute.water, 0.15f);
        }};

        frozenGrass = new Floor("frozen-grass"){{
            attributes.set(Attribute.water, 0.15f);
            attributes.set(bio, 0.08f);
            wall = shrubs;
        }};

        cinderBloomy = new Floor("cinder-bloomy"){{
            variants = 3;
            attributes.set(bio, 0.03f);
            attributes.set(Attribute.water, -0.15f);
        }};

        cinderBloomier = new Floor("cinder-bloomier"){{
            variants = 3;
            attributes.set(bio, 0.02f);
            attributes.set(Attribute.water, -0.05f);
        }};

        cinderBloomiest = new Floor("cinder-bloomiest"){{
            variants = 3;
            attributes.set(bio, 0.01f);
        }};

        cinderBloomGrass = new Floor("cinder-bloom"){{
            variants = 3;
            attributes.set(bio, 0.06f);
            attributes.set(Attribute.water, 0.25f);
        }};

        mossyStone = new RotatingFloor("mossy-stone"){{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.1f);
        }}  ;

        mossierStone = new RotatingFloor("mossier-stone"){{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.1f);
        }};

        mossiestStone = new RotatingFloor("mossiest-stone"){{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.1f);
        }};

        mossStone = new Floor("moss-stone"){{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.1f);
        }};

        mossyDirt = new RotatingFloor("mossy-dirt"){{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.1f);
        }};

        frozenDirt = new Floor("frozen-dirt"){{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.3f);
        }};

        frozenMud = new Floor("frozen-mud"){{
            attributes.set(bio, 0.1f);
            attributes.set(Attribute.water, 0.3f);
        }};

        hardenMud = new Floor("harden-mud"){{
            attributes.set(Attribute.water, 0.1f);
            variants = 3;
        }};

        frozenTar = new Floor("frozen-tar"){{
            attributes.set(Attribute.water, 0.3f);
            attributes.set(Attribute.oil, 1.2f);
            variants = 3;
        }};

        frozenSlop = new Floor("frozen-slop"){{
            attributes.set(Attribute.water, 0.6f);
            attributes.set(Attribute.oil, 0.6f);
            variants = 3;
        }};

        snowySand = new Floor("snowy-sand"){{
            attributes.set(Attribute.water, 0.1f);
            variants = 3;
        }};

        crackedIce = new Floor("cracked-ice"){{
            attributes.set(Attribute.water, 0.3f);
            variants = 3;
        }};
        forestGrass = new Floor("forest-grass"){{
            attributes.set(Attribute.water, 0.1f);
            variants = 3;
        }};

        mossyhardenMud = new RotatingFloor("mossy-harden-mud"){{
            attributes.set(bio, 0.2f);
            attributes.set(Attribute.water, 0.1f);
        }};

        grassyVent = new SteamVent("grassy-vent"){{
            effectColor = Color.white;
            parent = blendGroup = grass;
            attributes.set(Attribute.steam, 1f);
        }};

        mossyVent = new SteamVent("mossy-vent"){{
            effectColor = Color.white;
            parent = blendGroup = mossStone;
            attributes.set(Attribute.steam, 1f);
        }};

        stoneVent = new SteamVent("stone-vent"){{
            effectColor = Color.white;
            parent = blendGroup = stone;
            attributes.set(Attribute.steam, 1f);
        }};

        hardenMuddyVent = new SteamVent("harden-muddy-vent"){{
            effectColor = Color.white;
            parent = blendGroup = hardenMud;
            attributes.set(Attribute.steam, 1f);
        }};

        basaltVent = new SteamVent("basalt-vent"){{
            effectColor = Color.white;
            parent = blendGroup = basalt;
            attributes.set(Attribute.steam, 1f);
        }};

        snowVent = new SteamVent("snow-vent"){{
            effectColor = Color.white;
            parent = blendGroup = snow;
            attributes.set(Attribute.steam, 1f);
        }};

        redSandVent = new SteamVent("red-sand-vent"){{
            effectColor = Color.white;
            parent = blendGroup = redSand;
            attributes.set(Attribute.steam, 1f);
        }};

        dirtVent = new SteamVent("dirt-vent"){{
            variants = 3;
            effectColor = Color.white;
            parent = blendGroup = dirt;
            attributes.set(Attribute.steam, 1f);
        }};

        //endregion
        //region Liquid floor

        redSandWater = new Floor("red-sand-water"){{
            isLiquid = supportsOverlay = true;

            variants = 0;
            albedo = 0.9f;
            statusDuration = 50f;
            speedMultiplier = 0.8f;
            liquidDrop = Liquids.water;
            status = StatusEffects.wet;
            cacheLayer = CacheLayer.water;
        }};

        lumaGrassWater = new Floor("luma-grass-water"){{
            isLiquid = supportsOverlay = true;
            liquidDrop = Liquids.water;
            status = StatusEffects.wet;
            statusDuration = 50f;
            speedMultiplier = 0.8f;
            cacheLayer = CacheLayer.water;
            variants = 0;
            albedo = 0.9f;
        }};

        brimstoneSlag = new Floor("brimstone-slag"){{
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

        algaeWater = new Floor("mossy-water"){{
            isLiquid = supportsOverlay = true;

            variants = 3;
            albedo = 0.9f;
            statusDuration = 50f;
            speedMultiplier = 0.8f;
            status = StatusEffects.wet;
            liquidDrop = Liquids.water;
            cacheLayer = CacheLayer.water;
        }};

        algaeWaterDeep = new Floor("mossy-water-deep"){{ //Remind rushie to update the bundles thx -past rushie
            variants = 3;
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

        pinkGrassWater = new Floor("pink-grass-water"){{
            isLiquid = supportsOverlay = true;

            statusDuration = 50f;
            speedMultiplier = 0.8f;
            variants = 0;
            albedo = 0.9f;
            liquidDrop = Liquids.water;
            status = StatusEffects.wet;
            cacheLayer = CacheLayer.water;
        }};

        yellowMossyWater = new Floor("yellow-mossy-water"){{
            isLiquid = supportsOverlay = true;

            variants = 0;
            albedo = 0.9f;
            statusDuration = 50f;
            speedMultiplier = 0.8f;
            status = StatusEffects.wet;
            liquidDrop = Liquids.water;
            cacheLayer = CacheLayer.water;
        }};

        coralReef = new Floor("coral-reef"){{
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

        slop = new Floor("slop"){{
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

        slopDeep = new Floor("slop-deep"){{
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

        lubricantPool = new Floor("lubricant-pool"){{
            drownTime = 180f;
            status = NyfalisStatusEffects.lubed;
            statusDuration = 120f;
            speedMultiplier = 1.05f;
            variants = 0;
            liquidDrop = lubricant;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
        }};

        //endregion
        //region Props
        yellowBush = new Prop("yellow-bush"){{
            variants = 2;
            breakSound = Sounds.plantBreak;
            frozenGrass.asFloor().decoration = this;
            yellowGrass.asFloor().decoration = this;
        }};

        lumaFlora = new Prop("luma-flora"){{
            variants = 2;
            breakSound = Sounds.plantBreak;
            lumaGrass.asFloor().decoration = this;
            pinkGrass.asFloor().decoration = this;
        }};

        bush = new Prop("bush"){{
            variants = 2;
            breakSound = Sounds.plantBreak; // Buildable via planty mush
        }};

        mossyBoulder = new Prop("mossy-boulder"){{
            variants = 2;
            frozenGrass.asFloor().decoration = this;
            mossierStone.asFloor().decoration = this;
        }};
        mossBoulder = new Prop("moss-boulder"){{
            variants = 2;
            mossStone.asFloor().decoration = this;
            mossiestStone.asFloor().decoration = this;
        }};
        beachSandBoulder = new Prop("beach-sand-boulder"){{
            beachSandFloor.asFloor().decoration = this;
            variants = 2;
        }};
        gypsumBoulder = new Prop("gypsum-boulder"){{
            gypsumFloor.asFloor().decoration = this;
            variants = 2;
        }};
        galenaBoulder = new Prop("galena-boulder"){{
            galenaFLoor.asFloor().decoration = this;
            variants = 2;
        }};
        pumiceBoulder = new Prop("pumice-boulder"){{
            pumiceFloor.asFloor().decoration = this;
            variants = 2;
        }};
        rustyBoulder = new Prop("rusty-boulder"){{
            rustyFloor.asFloor().decoration = this;
            rustFloor.asFloor().decoration = this;
            variants = 2;
        }};
        infernalBloom = new RotatingProp("infernal-bloom"){{
            variants = 3;
            breakSound = Sounds.plantBreak;
            cinderBloomGrass.asFloor().decoration = this;
            cinderBloomier.asFloor().decoration = this;
            cinderBloomiest.asFloor().decoration = this;
        }};

        redSandBoulder = new Prop("red-sand-boulder"){{
            variants = 2;
            redSand.asFloor().decoration = this;
        }};

        glowBloom = new RotatingProp("glow-bloom"){{
            variants = 3;
            lightRadius = 10f;
            emitLight = true;
            lightColor = NyfalisColors.glowPlantLight;
            breakSound = Sounds.plantBreak;
        }};

        deadBush = new RotatingProp("dead-bush"){{
            hasShadow = false;
            variants = 3;
            breakSound = Sounds.plantBreak;
        }};

        glowLilly = new RotatingProp("glow-lilly"){{
            variants = 1;
            lightRadius = 8.5f;
            hasShadow = false;
            lightColor = NyfalisColors.glowPlantLight;
            floating = placeableLiquid = emitLight = true;
            breakSound = Sounds.plantBreak;
        }};

        //endregion
        //region Walls
        beachSandWall = new StaticWall("beach-wall"){{
            variants = 3;
        }};
        gypsumWall = new StaticWall("gypsum-wall"){{
            variants = 3;
        }};
        gypsumRubble = new TallBlock("gypsum-rubble"){{
            variants = 2;
        }};
        galenaWall = new StaticWall("galena-wall"){{
            variants = 3;
        }};
        pumiceWall = new StaticWall("pumice-wall"){{
            variants = 3;
        }};
        pumiceRubble = new StaticTree("pumice-rubble"){{
            variants = 2;
        }};
        redDune = new StaticWall("red-dune-wall"){{
            redSand.asFloor().wall = this;
            attributes.set(Attribute.sand, 2f);
        }};

        pinkShrubs = new StaticWall("pink-shrubs"){{
            variants = 2;
        }};

        lumaWall = new StaticTree("luma-wall"){{
            variants = 2;
        }};

        rustedMetal = new StaticWall("rusted-metal");

        greenShrubsIrregular = new TallBlock("green-shrubs-irregular"){{
            variants = 2;
            clipSize = 128f;
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
        }};

        yellowShrubsCrooked = new TallBlock("yellow-shrubs-crooked"){{
            variants = 2;
            clipSize = 128f;
            layer = Layer.power + 0.9f;
        }};

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
        nyfalisTree = new TreeBlock("olupis-tree"){{
            variants = 2;
        }};
        mossTree = new TreeBlock("moss-tree"){{
            variants = 2;
        }};
        pinkTree = new TreeBlock("pink-tree"){{
            variants = 2;
        }};
        yellowTree = new TreeBlock("yellow-tree");
        yellowTreeBlooming = new TreeBlock("yellow-tree-blooming"){{
            variants = 2;
        }};
        infernalMegaBloom = new TreeBlock("infernal-megabloom"){{
            variants = 4;
            clipSize = 128f;
        }};
        orangeTree = new TreeBlock("orange-tree"){{
            variants = 3;
        }};
        deadTree = new TreeBlock("dead-tree"){{
            variants = 2;
        }};
        mossDeadTree = new TreeBlock("moss-dead-tree"){{
            variants = 2;
        }};


        //endregion
        //region Spreading & related floor

        /* Note: The last stage of anything from the spreading types should go FIRST,
        last stage -> middle stage -> first stage, otherwise stuff WILL break */

        // TODO: Add Fx (spreadEffect [floors, ores], upgradeEffect [floors], growEffect [walls])
        growingWall = new GrowingWall("walltest", 0){{
            inEditor = false;

            growTries = 11;
            growChance = 0.04d;
            next = mossiestStoneWall;
        }};
        theircelium = new Floor("moss", 3){{
            mapColor = Color.valueOf("#1e2f0a");
            inEditor = false;
        }};

        ourcelium = new SpreadingFloor("mossiest-overlay", 3){{
            // this doesn't spread, but growth is affected by these settings too
            overlay = true;
            inEditor = false;

            spreadTries = 6;
            spreadChance = 0.0095d;

            next = theircelium;

            mapColor = Color.valueOf("#3c5e14");
            spreadSound = NyfalisSounds.mossSpread;
            blacklist.addAll(coreZone); //doesnt work
        }};
        yourcelium = new SpreadingFloor("mossier-overlay", 3){{
            // this doesn't spread, but growth is affected by these settings too
            overlay = true;
            inEditor = false;

            spreadTries = 4;
            spreadChance = 0.013d;

            next = ourcelium;

            mapColor = Color.valueOf("#5a8d1d");
            spreadSound = NyfalisSounds.mossSpread;
        }};
        mycelium = new SpreadingFloor("mossy-overlay", 3){{
            growSpread = true;
            overlay = true;

            spreadTries = 3;
            spreadChance = 0.021d;
            drillEfficiency = 0.66f;

            replacements.putAll(
                stoneWall, growingWall
            );
            blacklist.addAll(coreZone, theircelium);

            next = yourcelium;

            mapColor = Color.valueOf("#78bc27");
            spreadSound = NyfalisSounds.mossSpread;
        }};

        //endregion
    }

    public static void LoadBlocks(){
        //region Distribution
        rustyIronConveyor = new Conveyor("rusty-iron-conveyor"){{
            health = 45;
            speed = 0.025f;
            displayedSpeed = 3.8f;
            buildCostMultiplier = 1.5f;
            researchCost = with(rustyIron, 25);
            requirements(Category.distribution, with(rustyIron, 1));
        }};

        ironConveyor = new PowerConveyor("iron-conveyor"){{
            hasPower = conductivePower = consumesPower = noUpdateDisabled = true;

            health = 70;
            speed = 0.03f;
            itemCapacity = 1;
            displayedSpeed = 4.8f;
            poweredSpeed = 0.05f;
            powerRequired = 15f/60f;
            buildCostMultiplier = 0.45f;
            unpoweredSpeed = 0.025f;
            displayedSpeedPowered = 7f;

            researchCost = with(iron, 10, rustyIron, 20);
            consumePower (2f/60).boost();
            requirements(Category.distribution, with(iron, 1, rustyIron, 5 ));
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
            researchCost = with(cobalt, 500, lead, 500);
            requirements(Category.distribution, with(cobalt, 1, lead, 5 ));
        }};

        ironRouter = new Router("iron-router"){{
            buildCostMultiplier = 1.5f;
            speed = 16;

            researchCost = with(rustyIron, 10, lead, 10);
            requirements(Category.distribution, with(rustyIron, 3, lead, 1));
        }};

        ironDistributor = new Router("iron-distributor"){{
            size = 2;
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

            speed = 4f;
            health = 120;
            regionRotated1 = 1;
            buildCostMultiplier = 0.3f;
            researchCost = with(lead, 500, graphite, 100, iron, 100);
            requirements(Category.distribution, with(iron, 15, graphite, 15, lead, 30));
        }};

        //endregion
        //region Drills / crafting
        rustyDrill = new BoostableBurstDrill("rusty-drill"){{
            hasPower = true;
            squareSprite = false;
            itemCapacity = 25;

            tier = 1;
            size = 3;
            drillTime = 60f * 8.5f;
            variants = topVariant = 3;

            drillEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, Fx.mineImpactWave.wrap(Pal.redLight, 40f));
            researchCost = with(rustyIron,50);
            consumePower(10f/60f);
            consumeLiquid(Liquids.water, 0.05f).boost();
            requirements(Category.production, with(rustyIron, 15    ));
        }};

        steamDrill = new Drill("steam-drill"){{
            squareSprite = false;
            hasPower = true;
            tier = 2;
            size = 3;
            drillTime = 60f * 5f;


            envEnabled ^= Env.space;
            consumePower(50f/60f);
            consumeLiquid(NyfalisItemsLiquid.steam, 0.05f);
            researchCost = with(iron, 300, lead, 700);
            consumeLiquid(Liquids.slag, 0.06f).boost();
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
            hasLiquids = hasPower = hasItems = legacyReadWarmup = true;
            size = 3;
            craftTime = 185f;
            maxBoost = 2.5f;
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
            consumePower(40f / 60f);
            consumeLiquid(Liquids.water, 18f / 60f);
            researchCost = with(iron, 700, lead, 1500, rustyIron, 1500);
            outputItem = new ItemStack(condensedBiomatter, 1);
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
            liquidCapacity = 10f;
            pumpAmount = 0.06f;
            buildCostMultiplier = 1.5f;
            researchCost = with(lead, 20, rustyIron, 20);
            requirements(Category.liquid, with(rustyIron, 1, lead, 3));
        }};

        ironPump = new Pump("iron-pump"){{
            squareSprite = false;
            size = 2;
            liquidCapacity = 20f;
            pumpAmount = 0.08f;
            buildCostMultiplier = 2.1f;
            researchCost = with(lead, 500, iron, 100, copper, 500);
            requirements(Category.liquid, with(iron, 20, lead, 20, copper, 20));
        }};

        displacementPump = new BurstPump("displacement-pump"){{
            squareSprite = false;

            size = 3;
            pumpTime = 310;
            dumpScale = 1.3f;
            leakAmount = 0.02f;
            pumpAmount = 140f;
            liquidCapacity = 300f;
            consumePower(25f/60f);
            researchCost = with(iron, 250, lead, 800, graphite, 250, rustyIron, 800);
            requirements(Category.liquid, with(iron, 15, graphite, 15, lead, 30, rustyIron, 30));
        }};

        massDisplacementPump = new BurstPump("mass-displacement-pump"){{
            size = 4;
            leakAmount = 0.1f;
            pumpTime = 320;
            pumpAmount = 200f;
            liquidCapacity = 400f;
            consumePower(0.6f);
            researchCost = with(iron, 500, lead, 1000, graphite, 250, silicon, 250);
            requirements(Category.liquid, with(iron, 30, graphite, 30, lead, 75, silicon, 30));
        }};

        leadPipe = new Conduit("lead-pipe"){{
            leaks = underBullets = true;

            health = 70;
            liquidCapacity = 2f;
            liquidPressure = 1.05f;
            researchCostMultiplier = 0.5f;
            botColor = Color.valueOf("37323C");
            researchCost = with(rustyIron, 10, lead, 10);
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
        //TODO: test
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

        broiler = new BoostableGenericCrafter("broiler"){{
            hasLiquids = hasPower =  outputsLiquid =  consumesPower = true;

            size = 2;
            health = 600;
            craftTime = 10f;

            consumePower(1f);
            consumeItem(rustyIron, 2);
            consumeItem(scrap, 1).boost();
            outputLiquid = new LiquidStack(Liquids.slag, 12f / 60f);
            drawer = new DrawMulti(new DrawDefault(), new DrawLiquidRegion());
            requirements(Category.liquid, with(graphite, 25, iron, 20, lead, 40, silicon, 10));
        }};

        lubricantMixer = new GenericCrafter("lubricant-mixer"){{
            hasLiquids = hasPower =  outputsLiquid =  consumesPower = true;

            size = 3;

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
            craftTime = 2f / 60f;
            consumePower(1f);
            liquidOutputDirections = new int[]{1, 3};
            consumeLiquid(emulsiveSlop, 15f/ 60f);
            researchCost = with(iron, 500, lead, 800, copper, 800, rustyIron, 800);
            outputLiquids = LiquidStack.with(Liquids.water, 13f / 60f, Liquids.oil, 10f / 60f);
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
            consumeLiquid(Liquids.water, 24f / 60f);
            consumeItems(with(lead, 2, rustyIron, 2));
            researchCost = with(rustyIron, 50, lead, 50);
            requirements(Category.crafting, with(rustyIron, 15, lead, 30));
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.water), new DrawDefault());
        }};

        rustEngraver = new BoostableGenericCrafter("rust-engraver"){{
            hasPower = hasItems = hasLiquids = solid  = true;
            rotate = false;

            size = 4;
            craftTime = 250;
            envEnabled = Env.any;
            buildCostMultiplier = 0.4f;

            lightLiquid = Liquids.cryofluid;
            consumePower(200f / 60f);
            craftEffect = Fx.pulverizeMedium;
            outputItem = new ItemStack(iron, 2);
            consumeLiquid(Liquids.water, 38f / 60f).boost();
            consumeItems(with(quartz, 1, rustyIron, 6));
            researchCost = with(iron, 2000, lead, 2000, rustyIron, 2000, quartz, 1000, cobalt, 500);
            requirements(Category.crafting, with(iron, 25, lead, 30, rustyIron, 30, quartz, 25, cobalt, 25));
        }};

        hydrochloricGraphitePress  = new GenericCrafter("hydro-graphite-press"){{
            hasItems = hasLiquids = hasPower = true;

            size = 3;
            craftTime = 50f;
            itemCapacity = 20;
            buildCostMultiplier = 0.5f;
            craftEffect = Fx.pulverizeMedium;

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(NyfalisItemsLiquid.steam),
                    new DrawLiquidTile(Liquids.oil, 10f),
                    new DrawDefault()
            );
            consumePower(30f/60f);
            outputItem = new ItemStack(Items.graphite, 1);
            researchCost = with(lead, 650,  iron, 250, rustyIron, 650);
            requirements(Category.crafting, with(iron, 10, lead, 50, rustyIron, 40));
            consumeLiquids(LiquidStack.with(Liquids.oil, 10f / 60f, NyfalisItemsLiquid.steam, 10f/60f));
        }};

        siliconKiln = new GenericCrafter("silicon-kiln"){{
            size = 2;
            craftTime = 40f;
            liquidCapacity = 30f;
            ambientSoundVolume = 0.07f;
            consumeItem(quartz, 4);
            consumeLiquid(oil, 25f/60f);
            consumePower(0.50f);
            hasPower = hasLiquids = true;
            craftEffect = Fx.smeltsmoke;
            ambientSound = Sounds.smelter;
            outputItem = new ItemStack(Items.silicon, 1);
            requirements(Category.crafting, with(Items.copper, 30, Items.lead, 30, iron, 20));
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffef99")));
        }};

        siliconArcSmelter = new GenericCrafter("silicon-arc-smelters") {{
            hasPower= hasItems = true;
            size = 4;
            craftTime = 30f;
            itemCapacity = 30;
            liquidCapacity = 15f;

            consumePower(70f/60f);
            outputItem = new ItemStack(silicon, 3);
            consumeItems(with(quartz, 2, graphite, 1));
            requirements(Category.crafting, with(lead, 65, iron, 65, graphite, 15, silicon, 15, cobalt, 15));
        }};

        inductionSmelter = new SeparatorWithLiquidOutput("induction-smelter"){{
            size = 3;
            craftTime = 80f;
            liquidCapacity = 30;
            results = with(
                aluminum, 3,
                cobalt, 1F
            );
            consumeItem(alcoAlloy);
            consumePower(80f /60f);
            liquidOutputDirections = new int[]{4};
            liquidOutputs = LiquidStack.with(Liquids.slag, 4.5f / 60f);
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

        bioMatterPress = new GenericCrafter("biomatter-press"){{
            hasLiquids = hasPower = true;
            size = 2;
            health = 320;
            craftTime = 20f;

            craftEffect = Fx.none;
            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawPistons(){{
                        sinMag = 1f;
                    }},
                    new DrawDefault(),
                    new DrawLiquidRegion(),
                    new DrawRegion("-top")
            );
            consumePower(35f /60f);
            consumeItem(condensedBiomatter, 1);
            researchCost = with(iron, 250, lead, 500, copper, 250);
            outputLiquid = new LiquidStack(oil, 20f / 60f);
            requirements(Category.crafting, with(iron, 25, lead, 25, copper, 25));
        }};

        ironSieve  = new Separator("iron-sieve"){{
            //not to be confused with iron shiv
            hasPower = hasItems = true;
            hasLiquids = false;

            size = 2;
            craftTime = 30f;
            itemCapacity = 20;

            results = with(
                rustyIron, 3,
                quartz, 1
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
            requiredItems = with(lead, 20, copper, 20);
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
            requiredItems = with(copper, 40);
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
                }},
                graphite, new SpawnHelperBulletType(){{
                    shootEffect = Fx.smeltsmoke;
                    ammoMultiplier = 2f;
                    spawnUnit = venom;
                        alternateType = new SpawnHelperBulletType(){{
                            shootEffect = Fx.shootBig;
                            ammoMultiplier = 2f;
                            reloadMultiplier = 0.50f;
                            spawnUnit = serpent;
                        }};
                }}
            );
            requiredItems = with(copper, 40);
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
                    spawnUnit = porter;
                    alternateType = new SpawnHelperBulletType(){{
                        shootEffect = Fx.shootBig;
                        ammoMultiplier = 2f;
                        reloadMultiplier = 0.50f;
                        spawnUnit = essex;
                    }};
                }}
            );
            requiredItems = with(copper, 40);
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
            consumePower(2.55f);
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
            consumePower(5f);
            consumeItems(with(aluminum, 50, Items.silicon, 50, copper, 100));
            consume(new ConsumeLubricant(45f / 60f));

            upgrades.addAll(
                    new UnitType[]{serpent, reaper},
                    new UnitType[]{essex, lexington},
                    new UnitType[]{blitz, crusader},
                    new UnitType[]{striker, falcon},
                    new UnitType[]{germanica, luridiblatta},
                    new UnitType[]{acerodon, nyctalus}
            );

            constructTime = 60f * 60f;
            hasPower = consumesPower = conductivePower = true;
            requirements(Category.units, with(aluminum, 200, iron, 200, copper, 200, cobalt, 150));
        }};

        //Unit Tree: t1 = construct
        // T2 = construct + Articulator
        // t3 = t1 + reconstructor
        // t4 = t2 + t3 reconstructor + Articulator
        // t5 = t1-t4 at assembler

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
            requirements(Category.effect, ItemStack.with(Items.lead, 25, iron, 12));
            hasShadow = false;
            size = 1;
            health = 50;
            damage = tileDamage = tendrils = length = 0;
            shots = 1;
            rebuildable = false;
            bullet = new ExplosionBulletType(200, 20*8){{
                trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                killShooter = collidesAir = true;
                fragBullets = 8;
                fragRandomSpread = 0;
                fragSpread = 45;
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
            requirements(Category.effect, ItemStack.with(new Object[]{Items.lead, 25, cobalt, 12}));
            hasShadow = false;
            size = 1;
            health = 30;
            damage = tileDamage = tendrils = length = 0;
            shots = 1;
            rebuildable = false;
            bullet = new ExplosionBulletType(80, 5*8){{
                trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                killShooter = collidesAir = true;
                fragBullets = 8;
                fragRandomSpread = 0;
                fragSpread = 45;
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
            requirements(Category.effect, ItemStack.with(new Object[]{Items.lead, 25, quartz, 12}));
            hasShadow = false;
            size = 1;
            health = 40;
            damage = tileDamage = tendrils = length = 0;
            shots = 1;
            rebuildable = false;
            bullet = new ExplosionBulletType(100, 10*8){{
                trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                killShooter = collidesAir = true;
                fragBullets = 8;
                fragRandomSpread = 0;
                fragSpread = 45;
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
            requirements(Category.effect, ItemStack.with(new Object[]{Items.lead, 25, condensedBiomatter, 12}));
            hasShadow = false;
            size = 1;
            health = 20;
            damage = tileDamage = tendrils = length = 0;
            shots = 1;
            rebuildable = false;
            bullet = new ExplosionBulletType(50, 15*8){{
                trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
                killShooter = collidesAir = true;
                fragBullets = 8;
                fragRandomSpread = 0;
                fragSpread = 45;
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
            floating = placeableLiquid = consumesPower = outputsPower = true;
            solid = false;
            armor = 1f;
            health = 55;
            baseExplosiveness = 0.5f;
            //TODO: if possible 1 capacity & power usage, this only does usage
            consume(new ConsumePower(1/60f, 1f, false));
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
            boosterMultiplier = 3.8f;/* rationed for press + 2 gardens*/
            powerProduction = 20f/60f;
            attribute = Attribute.steam;
            consumeLiquid(oil, 10f / 60f).boost();
            researchCost = with(rustyIron, 20, Items.lead, 2);
            requirements(Category.power, with(rustyIron, 35, Items.lead, 1));
        }};

        hydroMill = new ThermalGeneratorNoLight("hydro-mill"){{
                floating = true;

                size = 3;
                powerProduction = 20f/60f;
                ambientSoundVolume = 0.06f;

                attribute = hydro;
                ambientSound = Sounds.hum;
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
            powerProduction = 23f/60f;
            ambientSoundVolume = 0.06f;

            attribute = hydro;
            generateEffect = Fx.steam;
            ambientSound = Sounds.hum;
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
            requirements(Category.power, with(quartz, 50, lead, 50, silicon, 50));
        }};

        steamTurbine = new ConsumeGenerator("steam-turbine"){{
            size = 6;
            powerProduction = 145f/60f;

            consumeLiquid(NyfalisItemsLiquid.steam, 24f/60f);
            consumeLiquid(oil, 20f / 60f).boost();
            requirements(Category.power, with(iron, 50, silicon, 50, lead, 100, cobalt, 50));
        }};
        //TODO: Solar receiver & Mirror -> Super structure `Mirror(s)->Redirector->Solar tower+water=steam->steam turbine(s)`
        // Mirror -> SolarTower -> Heat + water-> SteamTurbine -> power

        //endregion
        //region Wall
        rustyWall = new Wall("rusty-wall"){{
            floating = true;
            size = 1;
            health =  350;
            buildCostMultiplier = 0.8f;
            researchCost = with(rustyIron, 50);
            requirements(Category.defense,with(rustyIron, 6));
        }};

        rustyWallLarge = new Wall("rusty-wall-large"){{
            floating = true;
            size = 2;
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
            conductivePower = consumesPower = update = true;
            vanillaLightning = false;
            size = 1;
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


        rustyScrapWall = new Wall("rusty-scrap-wall"){{
            size = 1;
            variants = 1;
            health = 240;
            requirements(Category.defense, BuildVisibility.editorOnly, with(rustyIron, 6, scrap, 3));
        }};

        rustyScrapWallLarge = new Wall("rusty-scrap-wall-large"){{
            size = 2;
            variants = 3;
            health = 960;
            requirements(Category.defense, BuildVisibility.editorOnly, ItemStack.mult(rustyScrapWall.requirements, 4));
        }};

        rustyScrapWallHuge = new Wall("rusty-scrap-wall-huge"){{
            size = 3;
            variants  = 2;
            health = 2160;
            requirements(Category.defense, BuildVisibility.editorOnly, ItemStack.mult(rustyScrapWall.requirements, 9));
        }};

        rustyScrapWallGigantic = new Wall("rusty-scrap-wall-gigantic"){{
            size = 4;
            health = 3840;
            requirements(Category.defense, BuildVisibility.editorOnly, ItemStack.mult(rustyScrapWall.requirements, 16));
        }};

        rustyScrapWallHumongous = new Wall("rusty-scrap-wall-humongous"){{
            size = 5;
            health = 6000;
            requirements(Category.defense, BuildVisibility.editorOnly, ItemStack.mult(rustyScrapWall.requirements, 16));
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
                shootSound = Sounds.sap;
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


        ladar = new Ladar("ladar"){{
            emitLight = true;
            size = 2;
            fogRadius = 64;
            lightRadius = 200;
            rotateSpeed = 20f;
            glowMag = glowScl = 0f;
            discoveryTime = 60f * 80f;
            consumePower(240f/60f);
            glowColor = Color.valueOf("00000000");
            requirements(Category.effect, with(Items.lead, 60, Items.graphite, 50, iron, 10));
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

        coreVestige = new PropellerCoreBlock("core-vestige"){{

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

        coreRelic = new PropellerCoreTurret("core-relic"){{
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
            shootSound = Sounds.bigshot;
            requirements(Category.effect, with(rustyIron, 3000, lead, 3000, iron, 1500, graphite, 500));
            shootType = new ArtilleryBulletType(3f, 10){{
                lifetime = 80f;
                knockback = 0.8f;
                homingRange = 50f;
                width = height = 9f;
                splashDamage = 10f;
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

        coreShrine = new PropellerCoreTurret("core-shrine"){{
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
            shootSound = Sounds.bigshot;
            requirements(Category.effect, with(rustyIron, 3400, lead, 4000, iron, 3500, silicon, 2500, graphite, 2500, quartz, 2500));
            shootType = new SapBulletType(){{
                damage = 25f;
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

        coreTemple = new PropellerCoreTurret("core-temple"){{
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
            shootSound = Sounds.bigshot;
            targetGround = targetHealing = targetAir = true;
            requirements(Category.effect, with(rustyIron, 6000, lead, 6000, iron, 4500, silicon, 4500, graphite, 4500, quartz, 2500, cobalt, 2500));
            shootType = new RailBulletType(){{
                length = 255f;
                damage = 48f;
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

        deliveryCannon = new LimitedLaunchPad("delivery-cannon"){{
            size = 4;
            itemCapacity = 100;
            launchTime = 60f * 20;
            buildCostMultiplier = 0.5f;
            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawPistons()
            );
            requirements(Category.effect, BuildVisibility.campaignOnly, with(rustyIron, 75, iron, 50, silicon, 50, cobalt, 10));
        }};

        deliveryTerminal = new DeliveryTerminal("delivery-terminal"){{
            size = 2;
            buildCostMultiplier = 0.5f;
            toggleEffect = Fx.flakExplosionBig;
            requirements(Category.effect, BuildVisibility.campaignOnly, with(rustyIron, 150, iron, 100, lead, 150));
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
            ambientSound = Sounds.pulse;
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
            health = 10;
            size = 1;
            discoveryTime = 3400;
            glowColor = Color.valueOf("3ed09a");
            outlineColor = NyfalisColors.contentOutline;
            requirements(Category.effect, BuildVisibility.sandboxOnly, ItemStack.with(new Object[]{Items.silicon, 1}));
            fogRadius = 20;
        }};

    }

    public static void NyfalisBlocksPlacementFix(){
        nyfalisBuildBlockSet.clear();

        sandBoxBlocks.addAll(
                /*just to make it easier for testing and/or sandbox*/
                itemSource, itemVoid, liquidSource, liquidVoid, payloadSource, payloadVoid, powerSource, powerVoid, heatSource,
                worldProcessor, worldMessage, boomPuffActive, boomPuffPassive
        );

        Vars.content.blocks().each(b->{
            if(b.name.startsWith("olupis-")){
                if(b.isVisible() || b.buildVisibility == BuildVisibility.fogOnly) nyfalisBuildBlockSet.add(b);
                allNyfalisBlocks.add(b);
                b.envEnabled = NyfalisAttributeWeather.nyfalian;
            }
        });

        nyfalisCores.addAll(coreRemnant, coreRelic, coreShrine, coreTemple, coreVestige);

        for (Planet p : Vars.content.planets()) {
            if (Objects.equals(p.name, "serpulo")) p.techTree.each(n -> {
                if (n.content instanceof Block b && !sandBoxBlocks.contains(b)) hiddenNyfalisBlocks.add(b);
            });
        }

        rainRegrowables.addAll(grass, moss, mossierStone, mossyStone, yellowGrass, cinderBloomGrass, cinderBloomiest, cinderBloomiest, mossyDirt, frozenDirt, frozenGrass, frozenSlop);


        unitReplicator.replacement = NyfalisBlocks.rustyScrapWallHumongous;
        unitReplicatorSmall.replacement = NyfalisBlocks.rustyScrapWallGigantic;

        if(headless) return;
        cinderBloomy.mapColor = new Color().set(cinderBloomGrass.mapColor).lerp(basalt.mapColor, 0.75f);
        cinderBloomier.mapColor = new Color().set(cinderBloomGrass.mapColor).lerp(basalt.mapColor, 0.5f);
        cinderBloomiest.mapColor = new Color().set(cinderBloomGrass.mapColor).lerp(basalt.mapColor, 0.25f);
        mossyStone.mapColor = new Color().set(mossStone.mapColor).lerp(stone.mapColor, 0.75f);
        mossierStone.mapColor = new Color().set(mossStone.mapColor).lerp(stone.mapColor, 0.5f);
        mossiestStone.mapColor = new Color().set(mossStone.mapColor).lerp(stone.mapColor, 0.25f);
        mossyDirt.mapColor = new Color().set(mossyStone.mapColor).lerp(dirt.mapColor, 0.5f);
        frozenDirt.mapColor = new Color().set(ice.mapColor).lerp(dirt.mapColor, 0.5f);
        coralReef.mapColor = deepwater.mapColor;
    }
}
