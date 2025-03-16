package olupis.world.entities.packets;

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

public class VariantableDrawRegion extends DrawRegion{
    public TextureRegion[] variantRegions;
    int variants = 1;


    public VariantableDrawRegion(int variants){
        this.variants = variants;
    }

    public VariantableDrawRegion(String suffix){
        this.suffix = suffix;
    }

    public VariantableDrawRegion(int variants, String suffix){
        this.variants = variants;
        this.suffix = suffix;
    }

    public VariantableDrawRegion(String suffix, float rotateSpeed){
        this.suffix = suffix;
        this.rotateSpeed = rotateSpeed;
    }

    public VariantableDrawRegion(int variants, String suffix, float rotateSpeed){
        this(suffix, rotateSpeed);
        this.variants = variants;
    }

    public VariantableDrawRegion(String suffix, float rotateSpeed, boolean spinSprite){
        this.suffix = suffix;
        this.spinSprite = spinSprite;
        this.rotateSpeed = rotateSpeed;
    }

    public VariantableDrawRegion(int variants,String suffix, float rotateSpeed, boolean spinSprite){
        this(suffix, rotateSpeed, spinSprite);
        this.variants = variants;
    }

    public VariantableDrawRegion(){}
        @Override
        public void load(Block block){
            variantRegions = new TextureRegion[variants];

            for(int i = 0; i < variants; i++){
                variantRegions[i] = Core.atlas.find(block.name + suffix + (i + 1));
            }
            region = variantRegions[0];
        }

        @Override
        public void draw(Building build){
            float z = Draw.z();
            if(layer > 0) Draw.z(layer);

            TextureRegion region = variantRegions[Mathf.randomSeed(build.pos(), 0,variants-1)];

            if(spinSprite){
                Drawf.spinSprite(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
            }else{
                Draw.rect(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation + (buildingRotate ? build.rotdeg() : 0));
            }
            Draw.z(z);
        }

        @Override
        public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
            if(!drawPlan) return;

            TextureRegion region = variantRegions[Mathf.randomSeed(Point2.pack(plan.x, plan.y), 0,variants -1)];
            Draw.rect(region, plan.drawx(), plan.drawy(), (buildingRotate ? plan.rotation * 90f : 0));
        }


    }
