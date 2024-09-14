package olupis.world.blocks.environment;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class RotatingFloor extends Floor {

    public RotatingFloor (String name){
        super(name);
    }

    @Override
    public void drawBase(Tile tile){
        //delegates to entity unless it is null
        Mathf.rand.setSeed(tile.pos());
        Draw.rect(variants == 0 ? region :
                variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))],
                tile.worldx(), tile.worldy(), Math.round(Mathf.randomSeed(tile.pos(),0, 3)) * 90f )
        ;

        Draw.alpha(1f);
        drawEdges(tile);
        drawOverlay(tile);
    }
}
