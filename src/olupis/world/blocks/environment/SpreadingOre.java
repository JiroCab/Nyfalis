package olupis.world.blocks.environment;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;

/** This class as a whole is now only for auto-generation */
public class SpreadingOre extends OreBlock{
    public SpreadingFloor parent;
    public Block next = null;
    public Block set = null;
    public int overlayVariants = 0;
    public TextureRegion[] overlayRegions = new TextureRegion[overlayVariants];
    public StatusEffect statusEffect = StatusEffects.none;

    public SpreadingOre(String name){
        super(name);
        inEditor = false;
        buildVisibility = BuildVisibility.hidden;
    }

    @Override
    public void drawBase(Tile tile) {
        Draw.rect(variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))], tile.worldx(), tile.worldy());
        if(overlayRegions.length >= 1)
            Draw.rect(overlayRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, overlayRegions.length - 1))], tile.worldx(), tile.worldy());
    }
}
