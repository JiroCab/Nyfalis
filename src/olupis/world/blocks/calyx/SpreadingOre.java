package olupis.world.blocks.calyx;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

import static mindustry.Vars.net;
import static olupis.NyfalisVars.nyfRule;
import static olupis.world.EnvUpdater.*;

/** This class as a whole is now only for auto-generation */
public class SpreadingOre extends OreBlock implements UpdatingEnvironment{
    public SpreadingOverlay parent;
    public Block next;
    public Block set;
    public TextureRegion[] overlayRegions;

    public SpreadingOre(String name){
        super(name);
        inEditor = false;
        buildVisibility = BuildVisibility.hidden;
    }

    @Override
    public void drawBase(Tile tile){
        Draw.rect(variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))], tile.worldx(), tile.worldy());
        if(overlayRegions.length >= 1)
            Draw.rect(overlayRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, overlayRegions.length - 1))], tile.worldx(), tile.worldy());
    }

    public void updateEnv(Tile tile, EnvStruct i){
        if(tile.block() instanceof Drill)
            tasks.post(() -> tile.build.applySlowdown(parent.drillEfficiency, 30f));

        if(net.client()) return;

        if(Mathf.chance(parent.spawnChance * nyfRule.calyxSpreadingFactor) && i.getIncrementOverlay() >= parent.spreadTries){
            i.clearOverlayVal();

            if(next != null){
                if(parent.upgradeEffect != null){
                    tasks.post(() -> {
                        parent.upgradeEffect.at(tile.worldx(), tile.worldy(), 0f, parent.upgradeColor);
                        Call.effect(parent.upgradeEffect, tile.worldx(), tile.worldy(), 0, parent.upgradeColor);
                    });
                }

                if(parent.spreadSound != null)
                    tasks.post(() -> Call.soundAt(parent.spreadSound, tile.worldx(), tile.worldy(), parent.spreadVolume, 1f));

                queue(next).add(tile.pos());

                if(parent.oresSpawnProps && parent.props.size > 0 && canSpawn(id, parent.propLimit, parent.dynamicLimit) && Mathf.chance(parent.spawnChance * nyfRule.calyxSpreadingFactor)){
                    addProp(id);
                    queue(parent.props.random()).add(tile.pos());
                }
            }

            if(parent.spread && nyfRule.calyxSpreading){
                for(int it = 0; it < 4; it++){
                    Tile near = tile.nearby(it);
                    if(near == null)
                        continue;

                    EnvStruct nearby = instances[near.array()];
                    if(parent.replaces(near, nearby) || !parent.canSpread(near))
                        continue;

                    if(nearby.canWriteOverlay())
                        nearby.setOverlayIndex(near.overlay());
                    queue(parent).add(near.pos());

                    if(parent.spreadEffect != null){
                        tasks.post(() -> {
                            parent.spreadEffect.at(near.worldx(), near.worldy(), near.floor().mapColor);
                            Call.effect(parent.spreadEffect, near.worldx(), near.worldy(), 0, near.floor().mapColor);
                        });
                    }
                }
            }
        }
    }

    public boolean isValid(Tile tile){
        return parent.isValid(tile);
    }

    public Block replacement(){
        return parent.replacement;
    }
}
