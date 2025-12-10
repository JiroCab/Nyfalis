package olupis.world.blocks.environment;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.world;

public class VeryStaticWall extends StaticWall{
    public TextureRegion[] larges, huge, gigantic;
    public TextureRegion[][][] largeSplits, hugeSplits, giganticSplits;
    public int largeVariants = 1, hugeVariants = 0, giganticVariants = 0;
    public float largeThreshold = 0.4f, hugeThreshold = 0.2f, giganticThreshold = 0.1f ;

    public VeryStaticWall(String name){
        super(name);
    }

    @Override
    public void drawBase(Tile tile){
        //todo: this has a problem that all 3 biggens overlap  with one another
        int lx = tile.x / 2 * 2, hx = tile.x / 3 * 3, gx = tile.x / 4 * 4;
        int ly = tile.y / 2 * 2, hy = tile.y / 3 * 3,  gy = tile.y / 4 * 4;

        if(giganticVariants > 0 && Mathf.randomSeed(Point2.pack(gx, gy)) < giganticThreshold && eq(gx, gy, 3)){
            int sV = Mathf.randomSeed(Point2.pack(gx, gy), 0, Math.max(0, gigantic.length - 1));
            Draw.rect(giganticSplits[sV][tile.x % 4][3 - tile.y % 4], tile.worldx(), tile.worldy());
        }else if(hugeVariants > 0 && Mathf.randomSeed(Point2.pack(hx, hy)) < hugeThreshold && eq(hx, hy, 2)){
            int sV = Mathf.randomSeed(Point2.pack(hx, ly), 0, Math.max(0, huge.length - 1));
            Draw.rect(hugeSplits[sV][tile.x % 3][2 - tile.y % 3], tile.worldx(), tile.worldy());
        }else if(largeVariants > 0 && Mathf.randomSeed(Point2.pack(lx, ly)) < largeThreshold && eq(lx, ly, 1)){
            int sV = Mathf.randomSeed(Point2.pack(lx, ly), 0, Math.max(0, larges.length - 1));
            Draw.rect(largeSplits[sV][tile.x % 2][1 - tile.y % 2], tile.worldx(), tile.worldy());
        }else if(variants > 0){
            Draw.rect(variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))], tile.worldx(), tile.worldy());
        }else{
            Draw.rect(region, tile.worldx(), tile.worldy());
        }

        //draw ore on top
        if(tile.overlay().wallOre){
            tile.overlay().drawBase(tile);
        }
    }

    @Override
    public void load(){
        super.load();

        if(largeVariants >= 1){
            larges = new TextureRegion[largeVariants];
            largeSplits = new TextureRegion[largeVariants][][];
            
            for(int i = 0; i < largeVariants; i++){
                larges[i] = Core.atlas.find(name+"-large" + (i + 1));
                int size = larges[i].width / 2;
                largeSplits[i] = larges[i].split(size, size);
            }
            if(large == null) large = larges[0];
            if(split == null) split = largeSplits[0];
        }
        if(hugeVariants >= 1){
            huge = new TextureRegion[hugeVariants];
            hugeSplits = new TextureRegion[hugeVariants][][];
            
            for(int i = 0; i < hugeVariants; i++){
                huge[i] = Core.atlas.find(name+"-huge" + (i + 1));
                int size = huge[i].width / 3;
                hugeSplits[i] = huge[i].split(size, size);
        }}
        
        if(giganticVariants >= 1){
            gigantic = new TextureRegion[giganticVariants];
            giganticSplits = new TextureRegion[giganticVariants][][];

            for(int i = 0; i < giganticVariants; i++){
                gigantic[i] = Core.atlas.find(name+"-gigantic" + (i + 1));
                int size = gigantic[i].width / 4;
                giganticSplits[i] = gigantic[i].split(size, size);
        }}
    }

    boolean eq(int rx, int ry, int s){
        boolean out = rx < world.width() - 1 && ry < world.height() - 1 && world.tile(rx, ry).block() == this;
        for(int fx = 0; fx < s; fx++){
            for(int fy = 0; fy < s; fy++){
                out = out && world.tile(rx, ry).block() == this;
            }
        }
        return out;
        
    }
}
