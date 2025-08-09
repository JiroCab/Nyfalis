package olupis.world.entities.parts;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.part.RegionPart;
import mindustry.game.Team;

import static olupis.world.NyfPartParms.nyfparams;

public class CellPart extends RegionPart {

    public CellPart(String region){
        super(region);
    }

    public CellPart(String region, Blending blending, Color color){
        super(region, blending, color);
    }

    public CellPart(){}


    @Override
    public void draw(PartParams params){
        updateTeamColor();
        super.draw(params);
    }

    public void updateTeamColor(){
        float f = Mathf.clamp(nyfparams.health);
        color = Tmp.c1.set(Color.black).lerp(Team.get(nyfparams.team).color, f + Mathf.absin(Time.time, Math.max(f * 5f, 1f), 1f - f));
    }




}
