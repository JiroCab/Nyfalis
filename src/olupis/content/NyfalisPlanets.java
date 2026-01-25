package olupis.content;

import arc.*;
import arc.graphics.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.maps.planet.*;
import mindustry.type.*;
import mindustry.world.meta.*;
import olupis.world.planets.*;

public class NyfalisPlanets {
    public static NyfPlanet nyfalis, seredris, vorgin, system;
    private static final Seq<Sector> systemSector = new Seq<>();
    public static final Seq<Planet> planetList = new Seq<>();

    public  static void LoadPlanets(){
        /*I Exist so Tech Tree's Item pool is shared among the 3 planets*/
        system = new NyfPlanet("system", Planets.sun, 0.4f){{
            accessible = visible = unlocked = hasAtmosphere = updateLighting = drawOrbit = false;
            hideDetails = alwaysUnlocked = true;

            clipRadius = 2f;
            minZoom = 0.7f;
            camRadius = 0.68f * 3;
            defaultEnv = Env.space;
            icon = "rename";

            sectors.set(systemSector);
            generator = new AsteroidGenerator();
            meshLoader = () -> new HexMesh(this, 3);
            campaignRuleDefaults = new NyfCampaignRules();
            campaignRules = new NyfCampaignRules();
        }};

        nyfalis = new NyfPlanet("nyfalis", Planets.sun, 1.1f, 3){{
            allowSectorInvasion = allowLaunchLoadout = false;
            allowWaves = enemyCoreSpawnReplace  = prebuildBase = hasAtmosphere = true;

            totalRadius = 2.7f;
            atmosphereRadIn = 0.01f;
            atmosphereRadOut = 0.05f;
            startSector = 0;
            sectorSeed = 2;
            launchCapacityMultiplier = 0.4f;

            setDefRules(this);
            systemSector.add(sectors);
            system.position = this.position;
            defaultCore = NyfalisBlocks.coreRemnant;
            generator = new NyfalisPlanetGenerator() ;
            defaultEnv = Env.oxygen | NyfalisAttributeWeather.nyfalian;
            iconColor = NyfalisBlocks.redSand.mapColor;
            atmosphereColor = Color.valueOf("87CEEB");
            landCloudColor = new Color().set(Color.valueOf("C7E7F1").cpy().lerp(Color.valueOf("D7F5DC"), 0.55f));
            unlockedOnLand.addAll(NyfalisBlocks.coreRemnant, NyfalisItemsLiquid.rustyIron);
            meshLoader = () -> new HexMesh(this, 7);
            cloudMeshLoader = () -> new MultiMesh(
                new HexSkyMesh(this, 11, 0.7f, 0.06f, 5, new Color().set(Color.valueOf("C7E7F1")).mul(0.9f).a(0.55f), 2, 0.45f, 0.4f, 0.38f),
                new HexSkyMesh(this, 1, 0.2f, 0.08f, 5, Pal.regen.cpy().lerp(Color.valueOf("D7F5DC"), 0.55f).a(0.55f), 2, 0.45f, 0.5f, 0.41f)
            );
        }};

        //1st moon
        seredris = new NyfPlanet("seredris", NyfalisPlanets.nyfalis, 1.1f, 1){{
            accessible = alwaysUnlocked = clearSectorOnLose = allowSectorInvasion = updateLighting = allowLaunchSchematics = true;

            startSector = 2;
            lightDstTo = 0.8f;
            lightDstFrom = 0f;
            enemyBuildSpeedMultiplier = 0.4f;
            icon = "effect";
            setDefRules(this);
            systemSector.add(sectors);
            defaultCore = NyfalisBlocks.coreRemnant;
            generator = new SeredrisPlanetGenerator();
            defaultEnv = Env.oxygen | NyfalisAttributeWeather.nyfalian;
            iconColor = NyfalisItemsLiquid.condensedBiomatter.color;
            meshLoader = () -> new HexMesh(this, 5);

            cloudMeshLoader = () -> new MultiMesh(
                new HexSkyMesh(this, 11, 0.7f, 0.13f, 5, new Color().set(Color.valueOf("C7E7F1")).mul(0.9f).a(0.55f), 2, 0.45f, 0.9f, 0.38f),
                new HexSkyMesh(this, 1, 0.2f, 0.16f, 5, Pal.regen.cpy().lerp(Color.valueOf("D7F5DC"), 0.55f).a(0.55f), 2, 0.45f, 1f, 0.41f)
            );
        }};

        vorgin = new NyfPlanet("vorgin", NyfalisPlanets.nyfalis, 0.9f, 2){{
            //TODO: planet gimmick: mostly attack sectors + you can place a core in any spot
            clearSectorOnLose = allowSectorInvasion = updateLighting = accessible = allowCampaignRules = true;

            startSector = 1;
            enemyBuildSpeedMultiplier = 0.4f;
            icon = "effect";
            setDefRules(this);
            systemSector.add(sectors);
            generator = new VorginPlanetGenerator();
            defaultCore = NyfalisBlocks.coreRemnant;
            iconColor = NyfalisBlocks.pinkTree.mapColor;
            defaultEnv = Env.oxygen | NyfalisAttributeWeather.nyfalian;
            meshLoader = () -> new HexMesh(this, 5);
        }};

        //TODO: rework the planets generators
        //TODO: LUMA THEMED ASTEROID
    }

    public static void  setDefRules(Planet planet){
        setDefRules(planet, true);
    }

    public static void  setDefRules(Planet planet, boolean full){
        planet.allowCampaignRules = true;

        planet.ruleSetter = r ->{
            r.unitCrashDamageMultiplier = 0.25f;

            r.bannedBlocks.clear();
            r.waveTeam = NyfUnitTeamMapper.verdentTeam;

            r.placeRangeCheck = r.disableOutsideArea = r.staticFog = r.blockWhitelist = false;
            r.waves = r.showSpawns = r.unitPayloadUpdate = r.coreDestroyClear = r.coreIncinerates = r.fog = r.hideBannedBlocks = true;

            r.env =  Env.oxygen | NyfalisAttributeWeather.nyfalian;
        };

        if(!full)return;
        planet.campaignRuleDefaults = new NyfCampaignRules();
        planet.campaignRules = new NyfCampaignRules();
        
        planet.campaignRuleDefaults.fog = true;
        planet.campaignRuleDefaults.showSpawns= true;
        planet.showRtsAIRule = true;
        planet.campaignRuleDefaults.legacyLaunchPads = true;

    }

    public  static void PostLoadPlanet(){
         Seq<Sector> finalSectors = new Seq<>();
         finalSectors.add(systemSector.find(t -> t.preset == NyfalisSectors.sanctuary)); //prevents launching at sector 0 of nyfalis if you double tap while system is selected
         systemSector.remove(t -> t.preset == NyfalisSectors.sanctuary);
         finalSectors.add(systemSector);
        system.sectors.set(finalSectors);
        planetList.add(nyfalis, seredris, vorgin, system);
    }

    public static boolean isNyfalianPlanet (Planet planet){
        if (planet == null) return false;
        if (planet == seredris) return true;
        if (planet == vorgin) return true;
        return planet == nyfalis;
    }


    public static void unlockPlanets(){
        if(nyfalis.alwaysUnlocked && vorgin.alwaysUnlocked) return;
        nyfalisCheck();
        vorginCheck();
    }

    public static void nyfalisCheck(){
        if(NyfalisSectors.conservatorium.unlocked()){
            Log.info("Nyfalis Check passed!");
            nyfalis.quietUnlock();
            nyfalis.alwaysUnlocked = nyfalis.visible = true;
        }
    }

    public static void vorginCheck(){
        if(NyfalisSectors.forestOfHope.unlocked()){
            Log.info("Vorgin Check passed!");
            vorgin.quietUnlock();
            vorgin.alwaysUnlocked = vorgin.visible = true;
        }
    }

    public static class NyfPlanet extends Planet{

        public NyfPlanet(String name, Planet parent, float radius){
            super(name, parent, radius);
        }

        public NyfPlanet(String name, Planet parent, float radius, int sectorSize){
            super(name,parent,radius,sectorSize);
        }


        @Override
        public void loadRules(){
            campaignRules = Core.settings.getJson(name + "-campaign-rules", CampaignRules.class, () -> campaignRules);

            NyfCampaignRules owo = new NyfCampaignRules();
            owo.fog = campaignRules.fog;
            owo.showSpawns = campaignRules.showSpawns;
            owo.sectorInvasion = campaignRules.sectorInvasion;
            owo.randomWaveAI = campaignRules.randomWaveAI;
            owo.legacyLaunchPads = campaignRules.legacyLaunchPads;
            owo.rtsAI =  campaignRules.rtsAI;
            owo.clearSectorOnLose = campaignRules.clearSectorOnLose;
            owo.difficulty = campaignRules.difficulty;

            campaignRules = owo;
        }
    }
}
