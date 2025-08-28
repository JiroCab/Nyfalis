package olupis.world.entities.abilities;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import olupis.world.*;

public class PointDefenceIndicatorAbility  extends Ability{
    public float range = 5 * Vars.tilesize;
    public boolean multiHit = false;

    public PointDefenceIndicatorAbility(float range, boolean multiHit){
        this.range = range;
        this.multiHit = multiHit;
    }

    public PointDefenceIndicatorAbility(){

    }


    @Override
    public void draw(Unit unit){
        super.draw(unit);
        float alpha = Core.settings.getInt("nyfalis-pdl-status-trans")  / 100f;
        if(alpha <= 0)  return;
        if(unit.team != Vars.player.team() && !Core.settings.getBool("nyfalis-pdl-status-anyteam")) return;
        int ran =  Core.settings.getInt("nyfalis-pdl-status-range");

        if(ran < 21){
            Vec2 mouse = Core.input.mouseWorld(Core.input.mouseX(), Core.input.mouseY());
            Tile t = Vars.world.tileWorld(mouse.x, mouse.y);
            if(!unit.within(unit, ran * Vars.tilesize) && t != null && !unit.within(t, ran * Vars.tilesize)) return;
        };



        Color c = new Color().set(unit.team.color).lerp(Color.black, 0.1f);
        Draw.draw(Layer.blockUnder, () -> {
            Lines.stroke(0.85f, new Color().set(c).a(alpha));
            Lines.spikes(unit.x, unit.y,30 ,12f ,8 ,unit.rotation);
            Lines.stroke(2f, new Color().set(c).a(alpha));
            Lines.circle(unit.x, unit.y, range);
            Lines.stroke(7f, new Color().set(c).a(alpha));
            NyfWorldFuckingHelper.spikesTri(unit.x, unit.y, range * 0.25f ,range * 0.5f ,16 , 0, range / 32f);
        });
        Draw.reset();
    }
}
