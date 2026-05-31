package olupis.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.ui.*;
import olupis.world.blocks.calyx.ineternal.*;
import olupis.world.entities.entities.*;

public class  NyfUnitTeamMapper{
    public static Team verdentTeam, calyxTeam;
    public static final Team[] nyfTeams = new Team[2];
    public static int LeggedPayload, OnePayloadUnit, tonkNaval, snekUnit, calxyUpdater, ammoEnbaled, ammoLegged;

    public static void load(){
        //Thank you Siede for explaining how to do this!! ^w^
        LeggedPayload = EntityMapping.register("nyf-legged-naval", LeggedPayloadUnitClass::create);
        OnePayloadUnit = EntityMapping.register("nyf-one-payload", OnePayloadUnitClass::create);
        tonkNaval = EntityMapping.register("nyf-tonk-naval", TonkNavalUnitClass::create);
        snekUnit = EntityMapping.register("nyf-snek", SnekUnitClass::create);
        calxyUpdater = EntityMapping.register("nyf-calyx-updater", CalyxGraphUpdater::create);
        ammoEnbaled = EntityMapping.register("nyf-ammo-unit", AmmoEnabledUnitClass::create);
        //ammoLegged = EntityMapping.register("nyf-ammo-legged", CalyxGraphUpdater::create);

    }


    //stolen from: https://github.com/xjamiex/BioTech/blob/master/src/biotech/content/BioTeams.java
    //bc rushie lazy
    public static void loadTeam(){
        verdentTeam = newTeam(60, "nyf-verdent");
        calyxTeam = newTeam(61, "nyf-calyx");

        nyfTeams[0] = verdentTeam;
        nyfTeams[1] = calyxTeam;
    }

    private static Team newTeam(int id, String name) {
        Team team = Team.get(id);
        //to lazy lmao to get the hex or whatever
        Color color = team.color;
        team.name = name;
        team.color.set(color);
        team.palette[0] = color;
        team.palette[1] = color.cpy().mul(0.75f);
        team.palette[2] = color.cpy().mul(0.5f);

        for(int i = 0; i < 3; i++){
            team.palettei[i] = team.palette[i].rgba();
        }

        Seq<Font> fonts = Seq.with(Fonts.def, Fonts.outline);

        var ch = 65000 + id;
        Reflect.<ObjectIntMap<String>>get(Fonts.class, "unicodeIcons").put(name, ch);
        var stringIcons = Reflect.<ObjectMap<String, String>>get(Fonts.class, "stringIcons");
        stringIcons.put(name, ((char)ch) + "");

        int size = (int)(Fonts.def.getData().lineHeight/Fonts.def.getData().scaleY);
        TextureRegion regionR = Core.atlas.find("nyf-team-" + name);
        if(regionR == null) regionR = StatusEffects.corroded.fullIcon;
        TextureRegion region = regionR;
        Vec2 out = Scaling.fit.apply(region.width, region.height, size, size);
        Font.Glyph glyph = new Font.Glyph(){{
            id = ch;
            srcX = 0;
            srcY = 0;
            width = (int)out.x;
            height = (int)out.y;
            u = region.u;
            v = region.v2;
            u2 = region.u2;
            v2 = region.v;
            xoffset = 0;
            yoffset = -size;
            xadvance = size;
            kerning = null;
            fixedWidth = true;
            page = 0;
        }};
        fonts.each(f -> f.getData().setGlyph(ch, glyph));

        team.emoji = Iconc.statusCorroded + "";


        return team;
    }
}
