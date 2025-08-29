package olupis.world.entities.abilities;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import olupis.*;
import olupis.input.ui.*;
import olupis.world.*;

import static olupis.input.ui.NyfalisSettingsDialog.*;

public class PointDefenceIndicatorAbility  extends Ability{
    public float range = 5 * Vars.tilesize;
    public int type = 1;

    public PointDefenceIndicatorAbility(float range, int type){
        this.range = range;
        this.type = type;
    }

    public PointDefenceIndicatorAbility(){

    }


    @Override
    public void draw(Unit unit){
        super.draw(unit);
        float alpha = pdlStatusGiverTrans;
        if(alpha <= 0)  return;
        if(unit.team != Vars.player.team() && pdlStatusGiverAnyTeam) return;

        if(pdlStatusGiverRange < 51 && !NyfWorldFuckingHelper.withinMouseOrUnitRange(unit, pdlStatusGiverRange * Vars.tilesize)) return;

        Color c = new Color().set(unit.team.color).lerp(Color.black, 0.1f).a(alpha);
        Draw.draw(Layer.blockUnder, () -> {
            Lines.stroke(2f, c);
            Lines.circle(unit.x, unit.y, range);
            if(!pdlStatusGiverSimple){
                Lines.stroke(7f, c);
                if(type == 2){
                    NyfWorldFuckingHelper.spikesTri(unit.x, unit.y, range * 0.25f ,range * 0.25f ,16 , 0, range / 32f);
                    NyfWorldFuckingHelper.spikesTri(unit.x, unit.y, range * 0.50f ,range * 0.25f ,16 , 0, range / 32f);
                }else if(type == 1) NyfWorldFuckingHelper.spikesTri(unit.x, unit.y, range * 0.25f ,range * 0.5f ,16 , 0, range / 32f);
            }
        });
        Draw.reset();
    }
}
