package olupis.world.blocks.environment;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

public class SprigProp extends Prop{
    public Block replacement = Blocks.air;

    public SprigProp(String name){
        super(name);
    }


    @Override
    public void drawBase(Tile tile){
        if(!rotate){
            super.drawBase(tile);
            return;
        }
        Draw.z(layer);
        Draw.rect(variants > 0 ? variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))] : region, tile.worldx(), tile.worldy(), Mathf.randomSeed(tile.pos(), 0, 4) * 90);
    }

}
