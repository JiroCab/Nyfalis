package olupis;

import arc.Core;
import arc.Events;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.game.EventType;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.FileTreeInitEvent;
import mindustry.game.Team;
import mindustry.gen.Icon;
import mindustry.mod.Mod;
import mindustry.net.Net;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.world.Block;
import olupis.content.*;
import olupis.input.*;
import olupis.world.entities.packets.NyfalisUnitTimedOutPacket;
import olupis.world.planets.NyfalisTechTree;

import static mindustry.Vars.*;
import static olupis.content.NyfalisBlocks.*;
import static olupis.content.NyfalisPlanets.*;

public class NyfalisMain extends Mod{

    public static NyfalisSounds soundHandler = new NyfalisSounds();
    public static NyfalisUnitTimedOutPacket unitTimedOut = new NyfalisUnitTimedOutPacket();
    public static NyfalisLogicDialog logicDialog;
    public NyfalisSettingsDialog nyfalisSettings;

    @Override
    public void loadContent(){
        NyfalisShaders.LoadShaders();
        NyfalisShaders.LoadCacheLayer(); //idk when to load this so it 1st -Rushie
        NyfalisItemsLiquid.LoadItems();
        NyfalisStatusEffects.loadStatusEffects();
        NyfalisItemsLiquid.LoadLiquids();
        NyfalisUnits.LoadUnits();
        NyfalisBlocks.LoadWorldTiles();
        NyfalisBlocks.LoadBlocks();
        NyfalisSchematic.LoadSchematics();
        NyfalisAttributeWeather.loadWeather();
        NyfalisPlanets.LoadPlanets();
        NyfalisSectors.LoadSectors();
        Net.registerPacket(NyfalisUnitTimedOutPacket::new); //If new packets are needed, turn this into a dedicated class

        NyfalisPlanets.PostLoadPlanet();
        NyfalisTechTree.load();
        NyfalisAttributeWeather.AddAttributes();
        NyfalisUnits.PostLoadUnits();

        Log.info("OwO, Nyfalis (Olupis) content Loaded! Hope you enjoy nya~");
    }

    public NyfalisMain(){
        //Load sounds once they're added to the file tree
        Events.on(FileTreeInitEvent.class, e -> Core.app.post(() -> {
            NyfalisSounds.LoadSounds();
        }));

        Events.on(EventType.WorldLoadEvent.class, l ->{
            /*Delayed since custom games, for some reason needs it*/
            Time.run(0.5f * Time.toSeconds, this::sandBoxCheck);

            unlockPlanets();
            NyfalisStartUpUis.rebuildDebugTable();
            //Clean up of the old system of banning stuff

            if(state.isCampaign() && NyfalisPlanets.isNyfalianPlanet(state.getPlanet()) && state.rules.blockWhitelist) state.rules.blockWhitelist = false;
            if(headless)return;

            //debug and if someone needs to convert a map and said map does not have the Nyfalis Block set / testing
            if( Core.settings.getBool("nyfalis-debug")) NyfalisStartUpUis.buildDebugUI(Vars.ui.hudGroup);
            soundHandler.replaceSoundHandler();
        });

        if(headless)return;
        Events.on(EventType.UnlockEvent.class, event -> unlockPlanets());
        Events.on(EventType.SectorCaptureEvent.class, event -> unlockPlanets());

        Events.on(ClientLoadEvent.class, e -> {
            NyfalisBlocks.NyfalisBlocksPlacementFix();
            NyfalisSettingsDialog.AddNyfalisSoundSettings();
            NyfalisStartUpUis.saveDisclaimerDialog();
            if(Core.settings.getBool("nyfalis-disclaimer"))NyfalisStartUpUis.disclaimerDialog();

            Vars.ui.planet.shown(() -> {
                if(Core.settings.getBool("nyfalis-space-sfx")) Core.audio.play(NyfalisSounds.space, Core.settings.getInt("ambientvol", 100) / 100f, 0, 0, false);
            });

            arthin.uiIcon = bush.fullIcon;
            nyfalis.uiIcon = redSandBoulder.fullIcon;
            spelta.uiIcon = pinkTree.fullIcon;
            system.uiIcon = Icon.planet.getRegion();
            Vars.renderer.maxZoom  = 100; //just going to leave this here so aligning, screenshot are easier

            /*For those people who don't like the name/icon or overwrites in general*/
            if(Core.settings.getBool("nyfalis-green-icon")) Team.green.emoji = "\uf7a6";
            if(Core.settings.getBool("nyfalis-green-name")) Team.green.name = "nyfalis-green";
            /* uncomment when name/icon is final
            if(Core.settings.getBool("nyfalis-blue-icon")) Team.green.name = "";
            if(Core.settings.getBool("nyfalis-blue-name")) Team.green.name = "nyfalis-blue";*/
        });
    }

    public void sandBoxCheck(){ //for any sandbox maps
        if(net.client())return;
        if(!Core.settings.getBool("nyfalis-auto-ban")) return;
        if(state.isCampaign()){ Planet sector = state.getSector().planet;
            if(sector == arthin || sector == spelta || sector == nyfalis) state.rules.env = state.rules.env | NyfalisAttributeWeather.nyfalian;
        }
        if(state.rules.env == defaultEnv && state.getPlanet() == Planets.sun) state.rules.env = state.rules.env | NyfalisAttributeWeather.nyfalian;
        for (Block c : NyfalisBlocks.nyfalisCores) {
            if (indexer.isBlockPresent(c)) {
                state.rules.env |= NyfalisAttributeWeather.nyfalian;
                break;
            }
        }
    }

    @Override
    public void init() {
        nyfalisSettings = new NyfalisSettingsDialog();
        logicDialog = new NyfalisLogicDialog();
        unlockPlanets();
    }

    public void unlockPlanets(){
        if(nyfalis.unlocked() && spelta.unlocked()) return;
        if(nyfalis.unlocked()){
            for (Sector s : nyfalis.sectors) {
                if (s.unlocked() || (s.preset != null && s.preset.unlocked())) {
                    nyfalis.quietUnlock();
                    nyfalis.alwaysUnlocked = true;
                    break;
                }
            }
        }
        if(spelta.unlocked()){
            for (Sector s : spelta.sectors) {
                if (s.unlocked() || (s.preset != null && s.preset.unlocked())) {
                    nyfalis.quietUnlock();
                    nyfalis.alwaysUnlocked = true;
                    break;
                }
            }
        }
    }

}
