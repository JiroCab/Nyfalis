package olupis.content;

import arc.graphics.*;
import arc.scene.style.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static olupis.content.NyfalisItemsLiquid.*;

public class NyfalisColors{
	public static Color
		contentOutline = Color.valueOf("371404"),

		lightTone = Color.valueOf("#989aa4"),
		midTone = Color.valueOf("#6e7080"),
		darkTone = Color.valueOf("#4a4b53"),
		darkerTone = Color.valueOf("#292a2c"),

		glowPlantLight =  Color.valueOf("A0A54D").a(0.5f),
		glowPlantLightSofter =  Color.valueOf("A0A54D").a(0.20f),

		rustyBullet = new Color().set(rustyIron.color).lerp(Pal.bulletYellow, 0.5f).a(1),
		rustyBulletBack = new Color().set(rustyIron.color).lerp(Pal.bulletYellowBack, 0.5f).a(1),
		ironBullet = new Color().set(iron.color).lerp(Pal.bulletYellow, 0.25f).a(1),
		ironBulletBack = new Color().set(iron.color).lerp(Pal.bulletYellowBack,  0.25f).a(1),
		alcoBullet = new Color().set(alcoAlloy.color).lerp(Pal.bulletYellow, 0.25f).a(1),
		alcoBulletBack = new Color().set(alcoAlloy.color).lerp(Pal.bulletYellowBack, 0.25f).a(1),

		floodLightColor = new Color().set(Color.white).a(0.2f),
		turretLightColor = new Color().set(Color.white).a(0.35f),

        acidRainColour = Color.valueOf("50766A").a(0.7f),
        altStatColour = Color.valueOf("63553B")
	 ;

    public  static  Color[]
        aeroLaserColours = new Color[]{Pal.regen.cpy().a(.2f), Pal.regen.cpy().a(.5f), Pal.regen.cpy().mul(1.2f), Color.white};

    public static TextureRegionDrawable infoPanel = (TextureRegionDrawable) Tex.whiteui;
}