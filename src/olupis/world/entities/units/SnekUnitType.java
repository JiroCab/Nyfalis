package olupis.world.entities.units;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import olupis.world.ai.*;

/*ehehe snek*/
public class SnekUnitType extends NyfalisUnitType{
    public boolean customShadow = true;
    public boolean alternateDraw = false;
    public float sinOffset = 45, crawlTimeMul = 6f;
    @Nullable public int[] sprites;
    @Nullable public float[] spriteOffsets;

    public SnekUnitType(String name){
        super(name);
        pathCost = NyfalisPathfind.costSnek;
    }

    @Override
    public void load(){
        super.load();

        if(alternateDraw){
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
    public void drawCrawl(Crawlc crawl){
        if(!alternateDraw) super.drawCrawl(crawl);
        else {
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
    }

}

