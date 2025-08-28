package olupis.world.blocks.environment;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import mindustry.graphics.*;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Prop;

public class RotatingProp extends Prop {
    public TextureRegion[] larges, largeShadows;
    public int largeVariants = 0;
    public float threshold = 0.7f;

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

        Draw.rect(out, tile.worldx(), tile.worldy(), Mathf.randomSeed(tile.pos(), 0, 4) * 90);
    }

    @Override
    public void drawShadow(Tile tile){
        Draw.color(0f, 0f, 0f, BlockRenderer.shadowColor.a);
        Draw.rect(
        variants == 0 ? customShadowRegion :
        largeVariants >= 1 && Mathf.randomSeed(tile.pos(), 0f , 1f) >= threshold ? largeShadows[Mathf.randomSeed(tile.pos(), 0, Math.max(0, largeShadows.length - 1))] :
        variantShadowRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantShadowRegions.length - 1))],
        tile.drawx(), tile.drawy(), Mathf.randomSeed(tile.pos(), 0, 4) * 90) ;
        Draw.color();
    }
}
