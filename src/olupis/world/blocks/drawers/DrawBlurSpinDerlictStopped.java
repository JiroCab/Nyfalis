package olupis.world.blocks.drawers;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.draw.*;

public class DrawBlurSpinDerlictStopped extends DrawBlurSpin{
    public float derelictSpinMin  = 0.005f, derelictSpinMax  = 0.15f;

    public DrawBlurSpinDerlictStopped(String suffix, float speed){
        super(suffix, speed);
    }


    @Override
    public void draw(Building build){
        if(build.team == Team.derelict){

            if(Mathf.randomSeed(build.pos(), 0, 1) == 1) Draw.rect(region, build.x + x, build.y + y, Mathf.randomSeed(build.pos(), 0,  360) +  build.rotdeg());
            else Drawf.spinSprite(build.warmup() > blurThresh ? blurRegion : region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed * Mathf.randomSeed(build.pos(),derelictSpinMin ,derelictSpinMax));

        } else Drawf.spinSprite(build.warmup() > blurThresh ? blurRegion : region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed);
    }

}
