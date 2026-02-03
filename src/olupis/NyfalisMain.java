package olupis;

import arc.*;
import arc.math.*;
import arc.scene.style.*;
import arc.struct.*;
import arc.util.*;
import arc.util.Time;
import mindustry.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.world.*;
import olupis.content.*;
import olupis.input.*;
import olupis.input.ui.*;
import olupis.world.*;
import olupis.world.ai.*;
import olupis.world.blocks.unit.*;
import olupis.world.logic.*;
import olupis.world.planets.*;

import java.util.*;

import static mindustry.Vars.*;
import static olupis.NyfalisVars.*;
import static olupis.content.NyfalisBlocks.*;
import static olupis.content.NyfalisPlanets.*;

public class NyfalisMain extends Mod{


    @Override
    public void loadContent(){
        NyfalisVars.incompatible = !(Version.number == 7 && Objects.equals(Version.type, "official"));

        NyfUnitTeamMapper.load();
        NyfalisShaders.LoadShaders();
        NyfalisShaders.LoadCacheLayer(); //idk when to load this so it 1st -Rushie
        NyfalisItemsLiquid.LoadItems();
        NyfalisStatusEffects.loadStatusEffects();
        NyfalisItemsLiquid.LoadLiquids();
        NyfalisUnitCommands.loadUnitCommands();
        NyfalisColors.load();
        ControlPathfinder.costTypes.addAll(NyfalisPathfind.nyfCostTypes);
        NyfalisUnits.LoadUnits();
        NyfalisBlocks.LoadWorldTiles();
        NyfalisBlocks.LoadBlocks();
        NyfalisSchematic.LoadSchematics();
        NyfalisAttributeWeather.loadWeather();
        NyfalisPlanets.LoadPlanets();
        NyfalisSectors.LoadSectors();
        NyfalisPackets.LoadPackets();

        NyfalisPlanets.PostLoadPlanet();
        NyfalisTechTree.load();
        NyfalisAttributeWeather.AddAttributes();
        NyfalisUnits.PostLoadUnits();

        LogicIO.allStatements.addAll(NyfLStatements.allNyf);

        Log.info("OwO, Nyfalis (Olupis) content Loaded! Hope you enjoy nya~");
    }

    public NyfalisMain(){
        loadModSupport();
        EnvUpdater.load();

        //Load sounds once they're added to the file tree
        Events.on(FileTreeInitEvent.class, e -> Core.app.post(NyfalisSounds::LoadSounds));

        Events.on(ContentInitEvent.class, e -> NyfWorldFuckingHelper.initLevelMap());

        Events.on(WorldLoadEvent.class, l ->{
            /*Delayed since custom games, for some reason needs it*/
            nyfRule = new NyfRules();
            nyfRule.load(state.rules.tags);
            Time.run(0.5f * Time.toSeconds, NyfalisMain::sandBoxCheck);
            nyfalianPlanet = false;
                if(isNyfalianPlanet(state.getPlanet())) nyfalianPlanet = true;
            else for(Block c : NyfalisBlocks.nyfalisCores){
                if(indexer.isBlockPresent(c)){
                    nyfalianPlanet = true;
                    break;
                }
            }
            NyfWorldFuckingHelper.restUpdaters();

            //Clean up of the old system of banning stuff
            NyfalisPlanets.unlockPlanets();

            if(state.isCampaign() && NyfalisPlanets.isNyfalianPlanet(state.getPlanet())){
                if(state.rules.blockWhitelist) state.rules.blockWhitelist = false;
            }
            if(headless)return;
            NyfalisStartUpUis.rebuildDebugTable();

            soundHandler.replaceSoundHandler();
        });

        Events.on(SectorLaunchEvent.class, e -> {
            //When launching, prevents exporting to items to where you launched from if it's out of range
            if(NyfalisPlanets.isNyfalianPlanet(e.sector.planet) && !e.sector.near().contains(e.sector.info.destination)) e.sector.info.destination = e.sector;
        });
        if(headless)return;
        Events.on(TurnEvent.class, e -> sectorPostTurn());
        Events.on(SectorCaptureEvent.class, event -> {
            for (Building b : Groups.build)
                if (b instanceof Replicator.ReplicatorBuild r) {
                    Tile tile = r.tile;
                    tile.setNet(r.getReplacement());
                    r.remove();
                    NyfalisFxs.replicatorDie.at(r.x, r.y, 0, b.team.color, r.getReplacement());
                }
        });
        Events.on(UnlockEvent.class, event ->{
            unlockPlanets();
        });


        Events.on(SectorCaptureEvent.class, event -> unlockPlanets());

        Events.on(ClientLoadEvent.class, e -> {
            globalLoadEvent();
            NyfalisSettingsDialog.AddNyfalisSoundSettings();
            if(Core.settings.getBool("nyfalis-disclaimer"))NyfalisStartUpUis.disclaimerDialog();
            NyfalisStartUpUis.saveDisclaimerDialog();
            NyfalisStartUpUis.buildDebugUI(Vars.ui.hudGroup);

            //Thank you wmf for telling me this exists
            //Delayed for cases like when mapping utils
            Time.run(0.5f * Time.toSeconds, () -> {
                //pause menu
                NyfalisStartUpUis.nyfAdditionalRules(Reflect.get(ui.paused, "rulesDialog"));
                //main menu custom game
                NyfalisStartUpUis.nyfAdditionalRules(Reflect.get((Object)Reflect.get(ui.custom, "dialog"), "dialog"));
                //editor > custom game
                NyfalisStartUpUis.nyfAdditionalRules(Reflect.get((Object)Reflect.get(ui.editor, "playtestDialog"), "dialog"));
                //editor > info > rules
                NyfalisStartUpUis.nyfAdditionalRules(Reflect.get((Object)Reflect.get(ui.editor, "infoDialog"), "ruleInfo"));
            });


            Vars.ui.planet.shown(() -> {
                if(Core.settings.getBool("nyfalis-space-sfx")) Core.audio.play(NyfalisSounds.spaces.random(), Core.settings.getInt("ambientvol", 100) / 100f, 1, 0, false);
            });

            for(Planet planet : planetList){
                setDefRules(planet, false);
            }

            seredris.uiIcon = bush.fullIcon;
            nyfalis.uiIcon = redSandBoulder.fullIcon;
            vorgin.uiIcon = pinkTree.fullIcon;
            system.uiIcon = Icon.planet.getRegion();
            if(Core.settings.getBool("nyfalis-debug")){
                Log.debug("Nfyalis Debug is on! Nya~");
                //Vars.renderer.maxZoom  = 100; //just going to leave this here so aligning, screenshot are easier
                //if(control.saves.getSaveSlots().first() != null) ui.load.runLoadSave(control.saves.getSaveSlots().first());
                //ui.planet.debugSelect = true;
                //ui.content.show(NyfalisUnits.aero);
                //drawDebugHitboxes = true;
                enableDarkness = false;
            }
            NyfalisClassMap.load(this.getClass().getPackage().getName());

            content.each(c -> {
                if(c.minfo != null && c.minfo.mod != null && Objects.equals(c.minfo.mod.name, "olupis")){
                    if(c instanceof  Planet) return;
                    if( c instanceof UnlockableContent uc && !uc.fullIcon.found()) uc.uiIcon = uc.fullIcon = Core.atlas.find(Mathf.randomBoolean(0.5f) ? "alphaaaa" :  "ranai");
                }
            });
        });

        Events.on(ServerLoadEvent.class, e-> globalLoadEvent());

        Events.on(UnitDamageEvent.class, e -> {
            if(!e.unit.hasEffect(NyfalisStatusEffects.marked)) return;
            if(e.bullet.owner instanceof  Statusc s ){
                s.apply(NyfalisStatusEffects.concentrated);
            }
        });

        Events.run(Trigger.update, () -> {
            NyfalisSettingsDialog.updateSettings();
            NyfWorldFuckingHelper.allUpdaters();
        });

        Events.run(Trigger.draw, () -> {
            NyfWorldFuckingHelper.allDrawers();
        });
    }

    public void loadModSupport(){
        //https://github.com/WMF-Industries/Extremity/
        Core.settings.put("extremity-unitdex-olupis", "olupis-germanica=olupis-supella:olupis-acerodon=olupis-pteropus:olupis-striker=olupis-aero:olupis-serpent=olupis-venom:olupis-blitz=olupis-bay:olupis-warden=olupis-sentry:olupis-pedicia=olupis-gnat:olupis-phorid=olupis-pedicia:olupis-diptera=olupis-phorid");

        //https://github.com/JiroCab/PlanetVPlanetComplablityLayer
        Core.settings.put( "pcl-ores-olupis",
            "olupis-ore-iron," + //any
            "olupis-ore-iron," + //metal floor
            "olupis-ore-iron," +
            "olupis-ore-oxidized-lead," +
            "olupis-ore-oxidized-lead," +
            "olupis-ore-oxidized-lead," +
            "olupis-ore-oxidized-copper," +
            "olupis-ore-oxidized-copper," + // dmg mtl flr
            "olupis-ore-quartz," + // dark panel
            "olupis-ore-quartz," +
            "olupis-ore-alco," +
            "olupis-ore-alco," +
            "olupis-ore-cobalt," +
            "olupis-ore-cobalt," +
            "," + // any wall
            "," + // darkmetal
            "," + // dacite
            "," + // dirt
            "," + // snow
            "," + // salt
            "," + // regolith
            "," + // stone
            ","  // ferric stne
        );

        Core.settings.put("pcl-spread-olupis", "olupis-moss-stone");

        Core.settings.put( "pcl-blacklist-olupis",
        "grassy-vent," +
        "mossy-vent," +
        "harden-muddy-vent," +
        "basalt-vent," +
        "red-sand-vent," +
        "dirt-vent"
        );
    }


    public static void globalLoadEvent(){
        NyfalisUnits.GenerateWeapons();

        for(SectorPreset sector : content.sectors()){
            if(sector.planet == nyfalis){
                sector.databaseTabs.remove(vorgin);
                sector.databaseTabs.remove(seredris);
            }else if(sector.planet == vorgin){
                sector.databaseTabs.remove(nyfalis);
                sector.databaseTabs.remove(seredris);
            }else if(sector.planet == seredris){
                sector.databaseTabs.remove(nyfalis);
                sector.databaseTabs.remove(vorgin);
            }
        }

        try{
            asyncCore.processes.add(new EnvUpdater());
        }catch(Exception e){
            Log.warn("Failed to initialize EnvUpdater with the following exception:");
            Log.warn(e.getMessage());
        }
    }

    public static void sectorPostTurn(){
        Seq<String> lostSectors = new Seq<>();

//        for (Sector sec : system.sectors) { //Guaranteed lost, if a base is left alone in 3 turns (6 minutes)
//            if(sec.hasBase() && !sec.isBeingPlayed() && !sec.isCaptured()){
//                sec.info.damage = Math.min(sec.info.damage + 0.33f, 1f);
//
//                if(sec.info.damage >= 0.999){
//                    if(sec.info.wave < sec.info.winWave && sec.info.hasCore){
//                        lostSectors.add(sec.name() + "");
//                        Events.fire(new SectorLoseEvent(sec));
//
//                        sec.info.items.clear();
//                        sec.info.damage = 1f;
//                        sec.info.hasCore = false;
//                        sec.info.production.clear();
//                    }
//                }
//            }
//        }
        Time.run(0.5f * Time.toSeconds, () -> abandonedSectorsWarning(lostSectors));
    }

    public static void abandonedSectorsWarning(Seq<String> lostSectors){
        if(lostSectors.size == 0) return;
        if(!shownWarning){
            shownWarning = true;
            String list = String.valueOf(lostSectors).replace("[" , "").replace("]" , "");
            Log.info("(Nyfalis) " + list + " was lost from being left alone for too long!");
            Call.sendChatMessage(Core.bundle.format("nyfalis-sector.warning", list));
        }
        lostSectors.clear();
    }

    public static void sandBoxCheck(){
        sandBoxCheck(true);
    }

    public static void sandBoxCheck(Boolean auto){
        if(incompatible) return;
        if(!state.isPlaying()) return;
        if(net.client())return;

        if(!Core.settings.getBool("nyfalis-auto-ban") && auto) return;
        boolean changed = false, anyPlanet = false;
        int prevEnv = state.rules.env;
        if(state.isCampaign()){ Planet sector = state.getSector().planet;
            if(NyfalisPlanets.isNyfalianPlanet(sector)){
                changed = true;
            }
        }
        if(state.rules.env == defaultEnv && state.getPlanet() == Planets.sun){
            anyPlanet = changed = true;
        }

//        if(state.rules.items.isEmpty()){
//            anyPlanet = changed = true;
//        }

        if(!changed){
            for (Block c : NyfalisBlocks.nyfalisCores) {
                if (indexer.isBlockPresent(c)) {
                    changed = true;
                    break;
                }
            }
        }
        if(changed){
            state.rules.env = prevEnv | NyfalisAttributeWeather.nyfalian;
        }

        if(anyPlanet) return;
        /*this is here so A)Hotkeys aren't broken even if blocks are hidden due to env B)Prevent Serpulo cores to be built here*/
        if(state.rules.hasEnv(NyfalisAttributeWeather.nyfalian) && !state.rules.isBanned(Blocks.coreShard)){
            for (Block b : hiddenNyfalisBlocks) {
                if (state.rules.bannedBlocks.contains(b) && b != Blocks.coreShard) { //shard core shouldn't be built anyway and will be our check
                    state.rules.bannedBlocks.remove(b);
                    continue;
                }
                if(!sandBoxBlocks.contains(b))state.rules.bannedBlocks.add(b);
            }
        }
    }

    @Override
    public void init() {
        NyfalisBlocks.NyfalisBlocksPlacementFix();
        NyfalisVars.nyfalisSettings = new NyfalisSettingsDialog();
        if(!headless){
            NyfUnitTeamMapper.loadTeam();

            NyfalisColors.infoPanel = (TextureRegionDrawable) Tex.whiteui;
            NyfalisColors.infoPanel = (TextureRegionDrawable) NyfalisColors.infoPanel.tint(Pal.darkerGray);

            logicDialog = new NyfalisLogicDialog();
            sectorSelect = new LimitedLauncherSelect();
            unlockPlanets();
            NyfalisStartUpUis.loadHints();
        }

    }


}
