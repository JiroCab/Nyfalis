package olupis.world.entities.parts;

import arc.graphics.*;
import mindustry.*;
import mindustry.entities.part.*;
import mindustry.entities.part.DrawPart.*;

public class BladeRegionPart extends RegionPart{
    public float rotSpeed = 10, minSpeed = 0;

    public BladeRegionPart(String region){
        this.suffix = region;
    }

    public BladeRegionPart(String region, Blending blending, Color color){
        this.suffix = region;
        this.blending = blending;
        this.color = color;
        outline = false;
    }

    public BladeRegionPart(){
    }

    @Override
    public void draw(PartParams params){

        if(!Vars.state.isPaused()){
            this.rotation += (rotSpeed * progress.get(params)) + minSpeed;
        }

        super.draw(params);

}
}
