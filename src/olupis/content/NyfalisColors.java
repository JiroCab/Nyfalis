package olupis.content;

import arc.*;
import arc.graphics.*;
import arc.scene.style.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import java.util.*;

public class NyfalisColors{
    public static Color
        contentOutline = Color.valueOf("371404"),

        lightTone = Color.valueOf("#989aa4"),
        midTone = Color.valueOf("#6e7080"),
        darkTone = Color.valueOf("#4a4b53"),
        darkerTone = Color.valueOf("#292a2c"),

        glowPlantLight =  Color.valueOf("A0A54D").a(0.5f),
        glowPlantLightSofter =  Color.valueOf("A0A54D").a(0.20f),

        rustyBullet = Color.valueOf("#a3897d"),
        rustyBulletBack = Color.valueOf("#9f6e46"),
        ironBullet = new Color().set(Pal.metalGrayDark),
        ironBulletBack = new Color().set(Pal.darkestMetal),
        alcoBullet = Color.valueOf("#7e87a9"),
        alcoBulletBack = Color.valueOf("#7d7a8e"),

        floodLightColor = new Color().set(Color.white).a(0.2f),
        turretLightColor = new Color().set(Color.white).a(0.35f),

        acidRainColour = Color.valueOf("50766A").a(0.7f),
        altStatColour = Color.valueOf("63553B"),

        supportGreen = Color.valueOf("3ED09A"),
        supportGreenDarker = Color.valueOf("37946E")
    ;

    public  static  Color[]
        aeroLaserColours = new Color[]{Pal.regen.cpy().a(.2f), Pal.regen.cpy().a(.5f), Pal.regen.cpy().mul(1.2f), Color.white};

    public static TextureRegionDrawable infoPanel = (TextureRegionDrawable) Tex.whiteui;

    public static void load(){
        if(Core.settings.getBool("nyfalis-debug")) Log.info("nyf colours loaded idk");
    }

    public static Color ammoColour(String in){
        if(in == null || in.isEmpty()) return Pal.ammo;
        else if(Objects.equals(in, NyfUnitTeamMapper.ammoCarrier)) return rustyBulletBack;
        else if(Objects.equals(in, NyfUnitTeamMapper.ammoSupport)) return rustyBulletBack;
        return Pal.ammo;
    }
}