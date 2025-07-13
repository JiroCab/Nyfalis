package olupis.world.entities.units;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;

/*ehehe snek*/
public class SnekUnitType extends NyfalisUnitType{
    /*Affects how the Status cell is renders, replaces the two `for` statements*/
    public int cellSegmentParent = segments -1, cellSegmentParentRot = segments + 4;
    public boolean customShadow = true;
    public TextureRegion[] cellSegments;

    public SnekUnitType(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        cellSegments = new TextureRegion[segments];
        for(int i = 0; i < segments; i++){
            TextureRegion tr = Core.atlas.find(name + "-cell-" + i);
            if (tr.found()) cellSegments[i] = tr;
        }

        if(customShadow) softShadowRegion = Core.atlas.find("olupis-shadow-long");
    }

    @Override
    public void drawCrawl(Crawlc crawl){
        super.drawCrawl(crawl);
        //Dont feel like boder copy pasting the draw() to remove the if for crawlc
        drawCrawlCell(crawl);
    }

    public void drawCrawlCell(Crawlc crawl){
        if (crawl instanceof Unit unit) {
            /*Tbh I don't know what half this does since I just copied and pasted it, just manually tune the numbers*/
            if(drawCell){
                float trns = Mathf.sin(crawl.crawlTime() + cellSegmentParent * segmentPhase, segmentScl, segmentMag),
                        rot = Mathf.slerp(crawl.segmentRot(), unit.rotation, (float) cellSegmentParentRot  /( segments - 1f)),
                        tx = Angles.trnsx(rot, trns), ty = Angles.trnsy(rot, trns);

                applyColor(unit);
                Draw.color(cellColor(unit));
                Draw.rect(cellRegion, unit.x + tx, unit.y + ty, rot - 90);
                Draw.reset();
            }

            if(cellSegments.length >= 1f) for(int i = 0; i < segments; i++){
                if(cellSegments[i] == null || !cellSegments[i].found()) continue;
                float trns = Mathf.sin(crawl.crawlTime() + i * segmentPhase, segmentScl, segmentMag),
                        rot = Mathf.slerp(crawl.segmentRot(), unit.rotation, (float) i / (segments - 1f)),

                        tx = Angles.trnsx(rot, trns), ty = Angles.trnsy(rot, trns);

                applyColor(unit);
                Draw.color(cellColor(unit));
                Draw.rect(cellSegments[i], unit.x + tx, unit.y + ty, rot - 90);
                Draw.reset();
            }

        }else {
            Log.err(name + " is not a Crawlc! >:( not snek");
        }

    }
}

