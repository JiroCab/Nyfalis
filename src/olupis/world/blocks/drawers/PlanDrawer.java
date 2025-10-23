package olupis.world.blocks.drawers;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import olupis.world.blocks.processing.*;
import olupis.world.blocks.processing.HeadacheCrafter.*;

public class PlanDrawer  extends DrawRegion{
    public TextureRegion[][] variantRegions;
    public String emptyName= "";
    public  boolean placeholder = false;


    public PlanDrawer(){

    }


    public PlanDrawer(boolean placeholer){
        this.placeholder = placeholer;
    }

    @Override
    public void load(Block block){
        if(!(block instanceof HeadacheCrafter hc)) return;

        variantRegions = new TextureRegion[hc.plans.size][1];
        for(int i = 0; i < hc.plans.size; i++){
            FactoryPlan plan = hc.plans.get(i);
            variantRegions[i] = new TextureRegion[plan.variants];
            for(int j = 0; j < plan.variants; j++){
                variantRegions[i][j] = Core.atlas.find(block.name + plan.overlay + (j + 1));
            }
        }


        region = Core.atlas.find(emptyName, block.name + emptyName);
        if(region == null || !region.found()) region = variantRegions[0][0];
    }

    @Override
    public void draw(Building build){
        if(build instanceof HeadacheCrafterBuild hc){
            if(hc.invalidPlan()) return;

            float z = Draw.z();
            if(layer > 0) Draw.z(layer);

            if(placeholder){
                Draw.rect(hc.getPlanSelected().fullIcon, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));

                Draw.z();
                return;
            }

            TextureRegion region = variantRegions[hc.planSelected][Mathf.randomSeed(build.pos(), 0,hc.getPlanSelected().variants-1)];

            if(spinSprite){
                Drawf.spinSprite(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
            }else{
                Draw.rect(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
            }
            Draw.z(z);
        }
    }
}
