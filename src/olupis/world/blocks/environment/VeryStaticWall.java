package olupis.world.blocks.environment;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.world;

public class VeryStaticWall extends StaticWall{
    public TextureRegion[] larges;
    public TextureRegion[][][] largeSplits;
    public int largeVariants = 1;
    public float threshold = 0.4f;

    public VeryStaticWall(String name){
        super(name);
    }

    @Override
    public void drawBase(Tile tile){
        int rx = tile.x / 2 * 2;
        int ry = tile.y / 2 * 2;

        if(largeVariants > 0 && Mathf.randomSeed(Point2.pack(rx, ry)) < threshold && eq(rx, ry)){
            int sV = Mathf.randomSeed(Point2.pack(rx, ry), 0, Math.max(0, larges.length - 1));
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
    }

    boolean eq(int rx, int ry){
        return rx < world.width() - 1 && ry < world.height() - 1
        && world.tile(rx + 1, ry).block() == this
        && world.tile(rx, ry + 1).block() == this
        && world.tile(rx, ry).block() == this
        && world.tile(rx + 1, ry + 1).block() == this;
    }
}
