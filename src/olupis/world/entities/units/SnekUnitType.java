package olupis.world.entities.units;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.gen.*;
import olupis.world.ai.*;

import java.security.*;
import java.util.*;
import java.util.Map.*;

/*ehehe snek*/
public class SnekUnitType extends NyfalisUnitType{
    public boolean customShadow = true;
    public byte drawType = 0;
    public float sinOffset = 45, crawlTimeMul = 6f, lerpFactor = 0.31f;
    @Nullable public int[] sprites;
    @Nullable public float[] spriteOffsets;
    public HashMap<Unit, float[][]> rotationBuffer = new HashMap<>();

    public SnekUnitType(String name){
        super(name);
        pathCost = NyfalisPathfind.costSnek;
    }

    @Override
    public void load(){
        super.load();

        if(drawType == 2){
            if(sprites == null || sprites.length < segments)
                throw new IllegalArgumentException(name + " \"sprites\" isn't set properly!");

            if(spriteOffsets == null || spriteOffsets.length < segments){
                Log.err(name + " no spriteOffsets set, generating....");
                generateOffsets(1f);
            }
        }

        //todo draw the shadow per segment
        if(customShadow) softShadowRegion = Core.atlas.find("olupis-shadow-long");
    }

    public void generateOffsets(float size){
         generateOffsets(size, 0, 0);
    }

    public void generateOffsets(float size, float headOff, float assOff){
        spriteOffsets = new float[segments];
        for(int i = 0; i < spriteOffsets.length; i++){
            spriteOffsets[i] = size * (i - (segments / 2f));
        }
        spriteOffsets[0] += headOff;
        spriteOffsets[spriteOffsets.length -2] += assOff;
    }

    public void spriteBasic(int head, int ass, int body){
        sprites = new int[segments];
        sprites[0] = ass;
        sprites[sprites.length -1] = head;
        for(int i = 1; i < sprites.length -2; i++){
            sprites[i] = body;
        }
    }

    public void spriteBasic(int head, int ass, int[] body){
        sprites = new int[segments];
        sprites[0] = ass;
        sprites[sprites.length -1] = head;
        int count = 0;
        for(int i = 1; i < sprites.length -1; i++){
            sprites[i] = body[count];
            count++;
            if(count >= body.length) count = 0;
        }
    }

    @Override
    public void update(Unit unit){
        super.update(unit);

        if(!rotationBuffer.containsKey(unit)){
            float[][] out = new float[segments][3];

            for(int s = 0; s < segments; s++){
                float tx = Angles.trnsx((360f / segments) * s, hitSize), ty = Angles.trnsy((360f / segments) * s, hitSize);
                out[s] = new float[]{unit.x + tx, unit.y +  ty, s == 0 ? unit.rotation :Angles.angle(unit.x + tx, unit.y +  ty, out[s][0], out[ s][1]) };
            }
            rotationBuffer.put(unit, out);
        }

        if(unit.moving()){
            float[][] in = rotationBuffer.get(unit);
            float[][] out = new float[segments][3];
            float[] up = new float[]{unit.x, unit.y, unit.rotation};
            out[segments - 1] = up;
            //
            for(int s = 1; s < segments; s++){
                for(int d = 0; d < 2; d++){
                    out[segments - s -1][d] = Mathf.lerpDelta(in[segments - s -1][d], out[segments - s][d], lerpFactor);
                }
                out[segments - s - 1][2] =  Angles.angle(in[segments - s - 1][0],in[segments - s - 1][1], in[segments - s][0], in[segments - s][1]);

            }
            rotationBuffer.put(unit, out);
        }
    }

    @Override
    public void drawCrawl(Crawlc crawl){

        if(drawType == 1){
            Unit unit = (Unit)crawl;
            applyColor(unit);


            if(!rotationBuffer.containsKey(unit)) return;
            float[][] in = rotationBuffer.get(unit);
            for(int p = 0; p < 2; p++){
                TextureRegion[] regions = p == 0 ? segmentOutlineRegions : segmentRegions;

                for(int i = 0; i < segments; i++){

                    float rot = in[i][2], tx = in[i][0] , ty = in[i][1] ;
                    Draw.rect(regions[sprites[i]], tx, ty, rot - 90);

                    // Draws the cells
                    if(drawCell && p != 0 && segmentCellRegions[sprites[i]].found()){
                        Draw.color(cellColor(unit));
                        Draw.rect(segmentCellRegions[sprites[i]], tx, ty, rot - 90);
                        Draw.reset();
                    }
                }
            }

        }

        else if (drawType == 2) {
            Unit unit = (Unit)crawl;
            applyColor(unit);


            float crawlTime =
                crawl instanceof Segmentc seg && seg.headSegment() instanceof Crawlc head ? head.crawlTime() + seg.segmentIndex() * segmentPhase * segments * crawlTimeMul :
                crawl.crawlTime() * crawlTimeMul;

            for(int p = 0; p < 2; p++){
                TextureRegion[] regions = p == 0 ? segmentOutlineRegions : segmentRegions;

                for(int i = 0; i < segments; i++){

                    float trns = Mathf.sin(crawlTime + i * segmentPhase, segmentScl, segmentMag),

                    //at segment 0, rotation = segmentRot, but at the last segment it is rotation
                    rot = Mathf.slerp(crawl.segmentRot(), unit.rotation, i / (segments - 1f)),
                    tx = unit.x + Angles.trnsx(rot, spriteOffsets[i]) + Angles.trnsx(rot + sinOffset, trns),
                    ty = unit.y + Angles.trnsy(rot, spriteOffsets[i]) + Angles.trnsy(rot + sinOffset, trns);

                    //todo: make segments point towards the next one
                    Draw.rect(regions[sprites[i]], tx, ty, rot - 90);

                    // Draws the cells
                    if(drawCell && p != 0 && segmentCellRegions[sprites[i]].found()){
                        Draw.color(cellColor(unit));
                        Draw.rect(segmentCellRegions[sprites[i]], tx, ty, rot - 90);
                        Draw.reset();
                    }
                }
            }

            Draw.reset();
        }
        else super.drawCrawl(crawl);
    }

}

