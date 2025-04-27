package olupis.world.blocks.distribution;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.distribution.*;

import static mindustry.Vars.*;

public class VaraintConveryor extends Conveyor{
    TextureRegion[][] sideRegions;

    public VaraintConveryor(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        if(variants >= 1){
            sideRegions = new TextureRegion[5][variants];
            for(int s = 0; s < 5; s++){
                for(int i = 0; i < variants; i++) sideRegions[s][i] = Core.atlas.find(name +"-side-"+ (s) + "-"+ (i + 1));
            }
        }
    }

    @Override
    public TextureRegion[] icons(){
        if(variants >= 1) return new TextureRegion[]{regions[0][0], sideRegions[0][0]};

        return super.icons();
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        int[] bits = getTiling(plan, list);

        if(bits == null) return;

        TextureRegion side = null, region = regions[bits[0]][0];
        if(variants >=1)side = sideRegions[bits[0]][Mathf.randomSeed(plan.tile().pos(), 0 , variants -1)];

        Draw.rect(region, plan.drawx(), plan.drawy(), region.width * bits[1] * region.scl(), region.height * bits[2] * region.scl(), plan.rotation * 90);
        if(variants >=1)Draw.rect(side, plan.drawx(), plan.drawy(), region.width * bits[1] * region.scl(), region.height * bits[2] * region.scl(), plan.rotation * 90);
    }


    public class ConveyorBuild extends Conveyor.ConveyorBuild{
        @Override
        public void draw(){
            if(variants <= 0 ) {
                super.draw();
                return;
            }

            int frame = enabled && clogHeat <= 0.5f ? (int)(((Time.time * speed * 8f * timeScale * efficiency)) % 4) : 0;
            int seed = Mathf.randomSeed(tile.pos(), 0 , variants -1);


            //draw extra conveyors facing this one for non-square tiling purposes
            Draw.z(Layer.blockUnder);
            for(int i = 0; i < 4; i++){
                if((blending & (1 << i)) != 0){
                    int dir = rotation - i;
                    float rot = i == 0 ? rotation * 90 : (dir)*90;

                    Draw.rect(sliced(regions[0][frame], i != 0 ? SliceMode.bottom : SliceMode.top), x + Geometry.d4x(dir) * tilesize*0.75f, y + Geometry.d4y(dir) * tilesize*0.75f, rot);
                    Draw.rect(sliced(sideRegions[0][seed], i != 0 ? SliceMode.bottom : SliceMode.top), x + Geometry.d4x(dir) * tilesize*0.75f, y + Geometry.d4y(dir) * tilesize*0.75f, rot);
                }
            }

            Draw.z(Layer.block - 0.2f);

            Draw.rect(regions[blendbits][frame], x, y, tilesize * blendsclx, tilesize * blendscly, rotation * 90);
            Draw.rect(sideRegions[blendbits][seed], x, y, tilesize * blendsclx, tilesize * blendscly, rotation * 90);

            Draw.z(Layer.block - 0.1f);
            float layer = Layer.block - 0.1f, wwidth = world.unitWidth(), wheight = world.unitHeight(), scaling = 0.01f;

            for(int i = 0; i < len; i++){
                Item item = ids[i];
                Tmp.v1.trns(rotation * 90, tilesize, 0);
                Tmp.v2.trns(rotation * 90, -tilesize / 2f, xs[i] * tilesize / 2f);

                float
                ix = (x + Tmp.v1.x * ys[i] + Tmp.v2.x),
                iy = (y + Tmp.v1.y * ys[i] + Tmp.v2.y);

                //keep draw position deterministic.
                Draw.z(layer + (ix / wwidth + iy / wheight) * scaling);
                Draw.rect(item.fullIcon, ix, iy, itemSize, itemSize);
            }
        }
    }


}
