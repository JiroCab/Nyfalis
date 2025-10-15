package olupis.world.blocks.environment;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.*;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.Prop;

public class RotatingProp extends Prop {
    //
    public Block replacement = Blocks.air;
    public TextureRegion[] larges, largeShadows;
    public int largeVariants = 0;
    public float threshold = 0.7f, sclMin = 10f, sclMax = 30f, magMin = 0.05f, magMax = 0.2f, minFaX = 1.25f,maxFaX = 3,minFaY = 1.25f,maxFaY = 3f;
    public boolean windAffected = true;

    public  RotatingProp(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();

        if(largeVariants >= 1){
            larges = new TextureRegion[largeVariants];
            largeShadows = new TextureRegion[largeVariants];

            for(int i = 0; i < largeVariants; i++){
                larges[i] = Core.atlas.find(name+"-large" + (i + 1));
                largeShadows[i] = Core.atlas.find(name+"-large-shadow" + (i + 1));

            }
        }
    }



    @Override
    public void drawBase(Tile tile){
        Draw.z(layer);
        TextureRegion out = variants <= 0 ? region :
            largeVariants >= 1 &&Mathf.randomSeed(tile.pos(), 0f , 1f) >= threshold ? larges[Mathf.randomSeed(tile.pos(), 0, Math.max(0, larges.length - 1))] :
            variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))]
        ;
        int rot =  rotate ? Mathf.randomSeed(tile.pos(), 0, 4) * 90 : 0;
        float mag = Mathf.randomSeed(tile.pos(), magMin, magMax), scl= Mathf.randomSeed(tile.pos(), sclMin, sclMax);

        if(windAffected){
            Draw.rectv(out, tile.worldx(), tile.worldy(), out.width * region.scl(), out.height * region.scl(), rot, vec -> vec.add(
                Mathf.sin(vec.y*3 + Time.time, scl, mag)  + Mathf.sin(vec.x*3 - Time.time, 70, 0.8f),
                Mathf.cos(vec.x*3 + Time.time + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y*3 - Time.time, 50, 0.2f)
            ));
        }else {
            Draw.rect(out, tile.worldx(), tile.worldy(), rot);
        }
    }

    @Override
    public void drawShadow(Tile tile){
        Draw.color(0f, 0f, 0f, BlockRenderer.shadowColor.a);
        TextureRegion draw = variants == 0 ? customShadowRegion :
            largeVariants >= 1 && Mathf.randomSeed(tile.pos(), 0f , 1f) >= threshold ? largeShadows[Mathf.randomSeed(tile.pos(), 0, Math.max(0, largeShadows.length - 1))] :
            variantShadowRegions[Mathf.randomSeed(tile.pos() , 0, Math.max(0, variantShadowRegions.length - 1))];
        int rot =  rotate ? Mathf.randomSeed(tile.pos(), 0, 4) * 90 : 0;
        float
            mag = Mathf.randomSeed(tile.pos(), magMin, magMax),
            scl= Mathf.randomSeed(tile.pos(), sclMin, sclMax),
            xf = Mathf.randomSeed(tile.pos(), minFaX, maxFaX),
            yf= Mathf.randomSeed(tile.pos(), minFaY, maxFaY);

        if(windAffected){
            Draw.rectv(draw, tile.drawx(), tile.drawy(), draw.width * region.scl(), draw.height * region.scl(), rot, vec -> vec.add(
                Mathf.sin(vec.y*xf + Time.time, scl, mag)  + Mathf.sin(vec.x*xf - Time.time, 70, 0.8f),
                Mathf.cos(vec.x*xf + Time.time + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y*yf - Time.time, 50, 0.2f)
            ));
        }else {
            Draw.rect( draw, tile.drawx(), tile.drawy(), rot) ;
        }

        Draw.color();
    }
}
