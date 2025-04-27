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
            cncZhBattleMasterWeapon = new Sound(),
            cncZhAvengerPdl = new Sound(),
            sawActiveLoop = new Sound(),
            sawCollision = new Sound(),
            snip = new Sound(),


            barrelLaunch = new Sound(),

            cascadeDangerWarning = new Sound(),

            space = new Sound(),
            space2 = new Sound(),
            rainbow1 = new Sound(),
            rainbow2 = new Sound(),

            mossSpread = new Sound();
    ;
    public static Seq<Sound> spaces;

    public static void  LoadSounds(){
        //Note: Vars.tree.loadSound only works with .mp3 and .ogg
        as2PlasmaShot = tree.loadSound("as2-plasma-shot");
        as2ArmorBreak = tree.loadSound("as2-broke-armor");
        cncZhBattleMasterWeapon = tree.loadSound("cnc-zh-battlemaster-weapon");
        cncZhAvengerPdl = tree.loadSound("cnc-zh-avenger-pdl");
        sawActiveLoop = tree.loadSound("sawblade-active-loop");
        sawCollision = tree.loadSound("sawblade-collision");
        snip = tree.loadSound("snip");
        barrelLaunch = tree.loadSound("barrel-launch");
        cascadeDangerWarning = tree.loadSound("cascade-danger-warning");
        rainbow1 = tree.loadSound("rainbow-stat-music1");
        rainbow2 = tree.loadSound("rainbow-stat-music2");
        space = tree.loadSound("space");
        space2 = tree.loadSound("space2");
        mossSpread = tree.loadSound("moss-spread");

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
            if(sector == arthin) return true;
            if(sector == spelta) return true;
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
