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

		rustyBullet = new Color().set(rustyIron.color).lerp(Pal.bulletYellow, 0.5f),
		rustyBulletBack = new Color().set(rustyIron.color).lerp(Pal.bulletYellowBack, 0.5f),
		ironBullet = new Color().set(iron.color).lerp(Pal.bulletYellow, 1 - 0.25f),
		ironBulletBack = new Color().set(iron.color).lerp(Pal.bulletYellowBack, 1 - 0.25f),

		floodLightColor = new Color().set(Color.white).a(0.2f),
		turretLightColor = new Color().set(Color.white).a(0.35f)
	 ;

    public  static  Color[]
        aeroLaserColours = new Color[]{Pal.regen.cpy().a(.2f), Pal.regen.cpy().a(.5f), Pal.regen.cpy().mul(1.2f), Color.white};

    public static TextureRegionDrawable infoPanel = (TextureRegionDrawable) Tex.whiteui;
}