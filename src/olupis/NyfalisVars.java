package olupis;

import arc.graphics.*;
import arc.struct.*;
import arc.util.*;
import mindustry.type.*;
import olupis.content.*;
import olupis.input.ui.*;

import java.util.*;

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

    //env visuals
    public static @Nullable Texture cloudNoise;
    public static float  floodPlaneLevel = 0.30f;

    //Settings vars
    public static boolean shownWarning = false, incompatible = false, nyfalianPlanet = false;
    public static boolean musicModPresent = false;
    public static float pdlStatusGiverTrans, pdlStatusGiverRange, treeTransgenderRange;
    public static boolean pdlStatusGiverAnyTeam, pdlStatusGiverSimple;

    public static NyfRules nyfRule = new NyfRules();


}
