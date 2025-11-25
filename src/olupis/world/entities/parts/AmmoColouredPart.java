package olupis.world.entities.parts;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.part.RegionPart;
import mindustry.game.Team;

import static olupis.world.NyfPartParms.nyfparams;

public class AmmoColouredPart extends RegionPart {
    public  float threshold = 1;
    public  boolean blinks = false;

    public AmmoColouredPart(String region){
        super(region);
    }
    public AmmoColouredPart(String region, boolean blink, float thres){
        super(region);
        this.threshold = thres;
        this.blinks = blink;
    }

    public AmmoColouredPart(String region, float thres){
        super(region);
        this.threshold = thres;
    }

    public AmmoColouredPart(String region, Blending blending, Color color){
        super(region, blending, color);
    }

    public AmmoColouredPart(){}


    @Override
    public void draw(PartParams params){
        updateTeamColor();
        super.draw(params);
    }

    public void updateTeamColor(){
        float f = Mathf.clamp(nyfparams.ammo), b = blinks ? Mathf.absin(Time.time, Math.max(f * 2.5f, 1f), 1f - f) : 0;
        if(threshold < 1) f = Mathf.lerp(0, 1, Mathf.clamp(f - threshold));
        color = Tmp.c1.set(Color.black).lerp(Team.get(nyfparams.team).color, f + b);
    }




}
