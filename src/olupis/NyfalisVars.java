package olupis;

import arc.graphics.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.type.*;
import olupis.content.*;
import olupis.input.ui.*;

import java.util.*;

public class NyfalisVars{
    public static NyfalisSounds soundHandler = new NyfalisSounds();
    public static LimitedLauncherSelect sectorSelect;
    public static NyfalisLogicDialog logicDialog;
    public static NyfalisSettingsDialog nyfalisSettings;

    //gaymeplay vars
    public static HashMap<UnitType, Weapon[]> payloadWeaponIndex;

    //env visuals
    public static @Nullable Texture cloudNoise;
    public static float  floodPlaneLevel = 0.30f;

    //Settings vars
    public static boolean shownWarning = false, incompatible = false, nyfalianPlanet = false;
    public static boolean musicModPresent = false;
    public static float pdlStatusGiverTrans, pdlStatusGiverRange, treeTransgenderRange;
    public static boolean pdlStatusGiverAnyTeam, pdlStatusGiverSimple;

    //Extended Rules - todo  save/load in env updater chunk
    /*whether  calyx is allowed to spread at all*/
    public static boolean calyxSpreading = true;
    /*How much Calyx tiles want to spread*/
    public static float calyxSpreadingFactor = 1;
    /*How much Calyx will grow "buildings" on it*/
    public static float calyxBuildingFactor = 1;
    /*which team the Calyx "building" team is on*/
    public static Team calyxTeam = NyfUnitTeamMapper.verdentTeam;

    public static boolean damagingWeather = true;






}
