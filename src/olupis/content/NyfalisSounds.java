package olupis.content;

import arc.Core;
import arc.audio.Sound;
import arc.struct.Seq;
import mindustry.content.Planets;
import mindustry.type.Planet;
import mindustry.world.Block;

import java.util.concurrent.atomic.AtomicBoolean;

import static mindustry.Vars.*;
import static olupis.content.NyfalisPlanets.*;

public class NyfalisSounds {
    /*Music has been moved to https://github.com/JiroCab/Nyfalis-Music*/
    public static Sound
        as2ArmorBreak = new Sound(),
        as2PlasmaShot = new Sound(),
        shootPVC1 = new Sound(),
        shootPVC2 = new Sound(),
        shootCncBattleMaster = new Sound(),
        shootCncOsprey = new Sound(),
        shootCncQuad = new Sound(),
        shootCncV4MissileLand = new Sound(),
        shootCncAvengerPdl = new Sound(),
        shootBarrel = new Sound(),
        shootMicrowave = new Sound(),
        shootSnip = new Sound(),
        shootSpawn = new Sound(),
        sawActiveLoop = new Sound(),
        sawCollision = new Sound(),

        cascadeDangerWarning = new Sound(),

        space = new Sound(),
        space2 = new Sound(),
        rainbow1 = new Sound(),
        rainbow2 = new Sound(),

        mossSpread = new Sound()
    ;
    public static Seq<Sound> spaces;

    public static void  LoadSounds(){
        //Note: Vars.tree.loadSound only works with .mp3 and .ogg
        as2PlasmaShot = tree.loadSound("as2-plasma-shot");
        as2ArmorBreak = tree.loadSound("as2-broke-armor");
        shootCncBattleMaster = tree.loadSound("cnc-zh-battlemaster-weapon");
        shootCncAvengerPdl = tree.loadSound("cnc-zh-avenger-pdl");
        sawActiveLoop = tree.loadSound("sawblade-active-loop");
        sawCollision = tree.loadSound("sawblade-collision");
        shootSnip = tree.loadSound("shootSnip");
        shootBarrel = tree.loadSound("barrel-launch");
        cascadeDangerWarning = tree.loadSound("cascade-danger-warning");
        rainbow1 = tree.loadSound("rainbow-stat-music1");
        rainbow2 = tree.loadSound("rainbow-stat-music2");
        space = tree.loadSound("space");
        space2 = tree.loadSound("space2");
        mossSpread = tree.loadSound("moss-spread");
        shootCncOsprey = tree.loadSound("cnc-ra2-destoryer-osprey");
        shootCncQuad = tree.loadSound("cnc-zh-quad-pew");
        shootCncV4MissileLand = tree.loadSound("cnc-ra3-v4missland4");

        shootPVC1 = tree.loadSound("pvc-rocket1");
        shootPVC2 = tree.loadSound("pvc-rocket2");
        shootMicrowave =  tree.loadSound("microwave-beep");
        shootSpawn =  tree.loadSound("legacy-respawn");

        spaces = Seq.with(space, space2);
    }


    /*least invasive approach, hopefully a mod that changes music still has the Seqs public
    Music mod checks for this boolean, bc im lazy to move this to the music mod*/
    public void replaceSoundHandler(){
        Core.settings.put("nyfalis-replacemusic", shouldReplaceMusic());
    }


    public boolean shouldReplaceMusic(){
        if (Core.settings.getBool("nyfalis-music-only")) return true;
        if (Core.settings.getBool("nyfalis-music") && state.isCampaign()){
            Planet sector = state.getSector().planet;
            if(sector == seredris) return true;
            if(sector == vorgin) return true;
            return sector == nyfalis;
        }
        if(state.rules.env == defaultEnv && state.getPlanet() == Planets.sun) return false;

        if (Core.settings.getBool("nyfalis-music-custom-game") && !state.isCampaign()){
            int env = state.rules.env;
            /*Somewhat prevents rare cases with other (modded) planets with same env as nyfalis*/
            AtomicBoolean hasCore = new AtomicBoolean(false);
            for (Block c : NyfalisBlocks.nyfalisCores) {
                if (indexer.isBlockPresent(c)) {
                    hasCore.set(true);
                    break;
                }
            }

            return (env == nyfalis.defaultEnv && hasCore.get());
        }
        return false;
    }
}
