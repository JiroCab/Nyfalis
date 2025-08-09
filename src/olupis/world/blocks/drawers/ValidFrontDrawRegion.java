package olupis.world.blocks.drawers;

import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.draw.*;
import olupis.world.blocks.defence.ItemUnitTurret.*;

public class ValidFrontDrawRegion extends DrawRegion{

    public ValidFrontDrawRegion(String suffix){
        this.suffix = suffix;
    }

    @Override
    public void draw(Building build){
        if(build.front() == null) return;
        if(build.front() instanceof ItemUnitTurretBuild){
            float z = Draw.z();
            if(layer > 0) Draw.z(layer);
            if(spinSprite){
                Drawf.spinSprite(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
            }else{
                Draw.rect(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
            }
            Draw.z(z);
        }
    }



}
