package olupis.world.blocks.defence;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import olupis.world.*;

import static mindustry.Vars.*;

//Connected Textures
public class CTWall extends  FunnyWall{
    public boolean invertedBlending = false;

    public CTWall(String name){
        super(name);
    }

    public TextureRegion[]sideRegions;
    public int variantsSide = variants;

    @Override
    public void load(){
        super.load();
        if(variantsSide >= 1){
            sideRegions = new TextureRegion[variantsSide];
            for(int i = 0; i < variantsSide; i++) sideRegions[i] = Core.atlas.find(name +"-side"+ (i + 1));
        }
    }


    public class CTWallBuild extends FunnyWallBuild{
        public int blendprox;

        @Override
        public void draw(){
            if (this.block.variants != 0 && this.block.variantRegions != null) Draw.rect(this.block.variantRegions[Mathf.randomSeed((long)this.tile.pos(), 0, Math.max(0, this.block.variantRegions.length - 1))], this.x, this.y, this.drawrot());
            else Draw.rect(this.block.region, this.x, this.y, this.drawrot());

            if(variantsSide >= 1 && sideRegions != null){
                for(int i = 0; i < 4; i++){
                    if((blendprox & (1 << i)) != 0) Draw.rect(sideRegions[(int)Mathf.randomSeed(this.tile.pos())], x, y, (rotation - i) * 90);
                }
            }

            this.drawTeamTop();

            //draw flashing white overlay if enabled
            if(flashHit){
                if(hit < 0.0001f) return;

                Draw.color(flashColor);
                Draw.alpha(hit * 0.5f);
                Draw.blend(Blending.additive);
                Fill.rect(x, y, tilesize * size, tilesize * size);
                Draw.blend();
                Draw.reset();

                if(!state.isPaused()){
                    hit = Mathf.clamp(hit - Time.delta / 10f);
                }
            }
        }


        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            if(!headless){
                blendprox = 0;
                for(int i = 0; i < 4; i++){
                    if(NyfWorldFuckingHelper.nearby(this, Mathf.mod(rotation - i, 4)) instanceof CTWallBuild w && w.block.size == size){
                        blendprox |= (1 << i);
                    }
                }
            }
        }


    }


}
