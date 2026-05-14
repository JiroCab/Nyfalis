package olupis;

import arc.graphics.*;
import arc.struct.*;
import arc.util.*;
import mindustry.type.*;
import olupis.content.*;
import olupis.input.ui.*;
import olupis.world.*;

import java.util.*;

import static mindustry.Vars.state;

public class NyfalisVars{
    public static NyfalisSounds soundHandler = new NyfalisSounds();
    public static LimitedLauncherSelect sectorSelect;
    public static NyfalisLogicDialog logicDialog;
    public static NyfalisSettingsDialog nyfalisSettings;

    // none, birttleleaf, temp
    public static final int calyxSpecies = 3;

    //gaymeplay vars
    public static HashMap<UnitType, Weapon[]> payloadWeaponIndex;
    public static Seq<String> blacklistedWeaponsTypes = new Seq<>();
    public static int worldFuckeryTimer = 0;

    //env visuals
    public static @Nullable Texture cloudNoise;
    public static float  floodPlaneLevel = 0.5f;

    public static Bits aliveOverlays, aliveFloors;

    //Settings vars
    public static boolean shownWarning = false, incompatible = false, nyfalianPlanet = false;
    public static boolean musicModPresent = false;
    public static float pdlStatusGiverTrans, pdlStatusGiverRange, treeTransgenderRange;
    public static boolean pdlStatusGiverAnyTeam, pdlStatusGiverSimple;
    public static int turretConfigIndicator;

    public static NyfRules nyfRule = new NyfRules();

    public static void reset(){
        nyfRule = new NyfRules();
        nyfRule.load(state.rules.tags);
        load();
    }

    public static void load(){
        aliveFloors = new Bits(EnvUpdater.wsize);
        aliveOverlays = new Bits(EnvUpdater.wsize);

        NyfWorldFuckingHelper.restUpdaters();
    }
}
