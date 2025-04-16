package olupis;

import arc.*;
import arc.scene.style.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.game.*;
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
import olupis.world.blocks.unit.*;
import olupis.world.entities.packets.*;
import olupis.world.planets.*;

import java.util.*;

import static mindustry.Vars.*;
import static olupis.content.NyfalisBlocks.*;
import static olupis.content.NyfalisPlanets.*;

public class NyfalisMain extends Mod{
    public static NyfalisSounds soundHandler = new NyfalisSounds();
    public static LimitedLauncherSelect sectorSelect;
    public static NyfalisLogicDialog logicDialog;
    public NyfalisSettingsDialog nyfalisSettings;
    public static boolean shownWarning = false, incompatible = false;

    @Override
    public void loadContent(){
        incompatible = !(Version.number == 7 && Objects.equals(Version.type, "official"));

        NyfUnitMapper.load();
        NyfalisShaders.LoadShaders();
        NyfalisShaders.LoadCacheLayer(); //idk when to load this so it 1st -Rushie
        NyfalisItemsLiquid.LoadItems();
        NyfalisStatusEffects.loadStatusEffects();
        NyfalisItemsLiquid.LoadLiquids();
        NyfalisUnitCommands.loadUnitCommands();
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

        Log.info("OwO, Nyfalis (Olupis) content Loaded! Hope you enjoy nya~");
    }

    public NyfalisMain(){
        Core.settings.put("extremity-unitdex-olupis", "olupis-germanica=olupis-supella:olupis-acerodon=olupis-pteropus:olupis-striker=olupis-aero:olupis-serpent=olupis-venom:olupis-blitz=olupis-bay:olupis-essex=olupis-porter:olupis-pedicia=olupis-gnat:olupis-phorid=olupis-pedicia:olupis-diptera=olupis-phorid");

        EnvUpdater.load();

        //Load sounds once they're added to the file tree
        Events.on(FileTreeInitEvent.class, e -> Core.app.post(NyfalisSounds::LoadSounds));

        Events.on(EventType.WorldLoadBeginEvent.class, I -> {
            if(net.server() || !net.active()){
                NyfalisTurrets.cascadeAlt = Core.settings.getBool("nyfalis-bread-gun");
                NyfalisTurrets.dynamicTurretContent();

                NyfalisSyncOtherSettingsPacket packet = new NyfalisSyncOtherSettingsPacket();
                packet.cascadeBread = NyfalisTurrets.cascadeAlt;
                Vars.net.send(packet, true);
            }if(net.client())Call.serverPacketReliable("olupis-getsettings", "");

        });

        Events.on(EventType.WorldLoadEvent.class, l ->{
            /*Delayed since custom games, for some reason needs it*/
            Time.run(0.5f * Time.toSeconds, NyfalisMain::sandBoxCheck);

            //Clean up of the old system of banning stuff
            NyfalisPlanets.unlockPlanets();

            if(state.isCampaign() && NyfalisPlanets.isNyfalianPlanet(state.getPlanet())){
                if(state.rules.blockWhitelist) state.rules.blockWhitelist = false;
            }
            Events.on(EventType.SectorLaunchEvent.class, e -> {
                //When launching, prevents exporting to items to where you launched from if it's out of range
                if(NyfalisPlanets.isNyfalianPlanet(e.sector.planet) && !e.sector.near().contains(e.sector.info.destination)) e.sector.info.destination = e.sector;
            });
            if(headless)return;
            NyfalisStartUpUis.rebuildDebugTable();

            Events.on(EventType.TurnEvent.class, e -> {
                sectorPostTurn();
            });
            //debug and if someone needs to convert a map and said map does not have the Nyfalis Block set / testing
            if( Core.settings.getBool("nyfalis-debug")) NyfalisStartUpUis.buildDebugUI(Vars.ui.hudGroup);
            soundHandler.replaceSoundHandler();
        });

        if(headless)return;
        Events.on(EventType.SectorCaptureEvent.class, event -> {
            for (Building b : Groups.build)
                if (b instanceof Replicator.ReplicatorBuild r) {
                    Tile tile = r.tile;
                    tile.setNet(r.getReplacement());
                    r.remove();
                    NyfalisFxs.replicatorDie.at(r.x, r.y, 0, b.team.color, r.getReplacement());
                }
        });
        Events.on(EventType.UnlockEvent.class, event -> unlockPlanets());
        Events.on(EventType.SectorCaptureEvent.class, event -> unlockPlanets());

        Events.on(ClientLoadEvent.class, e -> {
            globalLoadEvent();
            NyfalisSettingsDialog.AddNyfalisSoundSettings();
            if(Core.settings.getBool("nyfalis-disclaimer"))NyfalisStartUpUis.disclaimerDialog();
            NyfalisStartUpUis.saveDisclaimerDialog();

            Vars.ui.planet.shown(() -> {
                if(Core.settings.getBool("nyfalis-space-sfx")) Core.audio.play(NyfalisSounds.spaces.random(), Core.settings.getInt("ambientvol", 100) / 100f, 1, 0, false);
            });

            arthin.uiIcon = bush.fullIcon;
            nyfalis.uiIcon = redSandBoulder.fullIcon;
            spelta.uiIcon = pinkTree.fullIcon;
            system.uiIcon = Icon.planet.getRegion();
            if(Core.settings.getBool("nyfalis-debug")){
                Log.debug("Nfyalis Debug is on! Nya~");
                //Vars.renderer.maxZoom  = 100; //just going to leave this here so aligning, screenshot are easier
                //if(control.saves.getSaveSlots().first() != null) ui.load.runLoadSave(control.saves.getSaveSlots().first());
                //ui.planet.debugSelect = true;
                //ui.content.show(NyfalisUnits.resolute);
            }

            /*For those people who don't like the name/icon or overwrites in general*/
            if(Core.settings.getBool("nyfalis-green-icon")) Team.green.emoji = "\uf7a6";
            if(Core.settings.getBool("nyfalis-green-name")) Team.green.name = "nyfalis-green";
            /* uncomment when name/icon is final
            if(Core.settings.getBool("nyfalis-blue-icon")) Team.green.name = "";
            if(Core.settings.getBool("nyfalis-blue-name")) Team.green.name = "nyfalis-blue";*/
        });

        Events.on(ServerLoadEvent.class, e-> globalLoadEvent());
    }


    public static void  globalLoadEvent(){
        NyfalisUnits.GenerateWeapons();
    }

    public static void sectorPostTurn(){
        Seq<String> lostSectors = new Seq<>();

        for (Sector sec : system.sectors) { //Guaranteed lost, if a base is left alone in 3 turns (6 minutes)
            if(sec.hasBase() && !sec.isBeingPlayed() && !sec.isCaptured()){
                sec.info.damage = Math.min(sec.info.damage + 0.33f, 1f);

                if(sec.info.damage >= 0.999){
                    if(sec.info.wave < sec.info.winWave && sec.info.hasCore){
                        lostSectors.add(sec.name() + "");
                        Events.fire(new EventType.SectorLoseEvent(sec));

                        sec.info.items.clear();
                        sec.info.damage = 1f;
                        sec.info.hasCore = false;
                        sec.info.production.clear();
                    }
                }
            }
        }
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
        if(NyfalisMain.incompatible) return;
        if(!state.isPlaying()) return;
        if(net.client())return;
        if(!Core.settings.getBool("nyfalis-auto-ban") && auto) return;
        boolean changed = false, anyPlanet = false;
        int prevEnv = state.rules.env;
        if(state.isCampaign()){ Planet sector = state.getSector().planet;
            if(NyfalisPlanets.isNyfalianPlanet(sector)){
                changed = true;
                if(!state.rules.weather.contains(w -> w.weather ==  NyfalisAttributeWeather.cloudShadow)){
                    state.rules.weather.add(new Weather.WeatherEntry(NyfalisAttributeWeather.cloudShadow));
                    Log.info("(Nyfalis) Cloud shadows weather added!");

                }
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
        nyfalisSettings = new NyfalisSettingsDialog();
        if(!headless){

            NyfalisColors.infoPanel = (TextureRegionDrawable) Tex.whiteui;
            NyfalisColors.infoPanel = (TextureRegionDrawable) NyfalisColors.infoPanel.tint(Pal.darkerGray);

            logicDialog = new NyfalisLogicDialog();
            sectorSelect = new LimitedLauncherSelect();
            unlockPlanets();
            NyfalisStartUpUis.loadHints();
        }

    }


}
