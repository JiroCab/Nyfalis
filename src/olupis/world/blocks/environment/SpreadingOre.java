package olupis.world.blocks.environment;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;
import static olupis.world.EnvUpdater.*;

/** This class as a whole is now only for auto-generation */
public class SpreadingOre extends OreBlock implements UpdatingEnvironment{
    public static final int arrayID = 1;

    public SpreadingOverlay parent;
    public Block next = null;
    public Block set = null;
    public int overlayVariants = 0;
    public TextureRegion[] overlayRegions = new TextureRegion[overlayVariants];

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

    public void updateEnv(Tile tile, int key){
        if(tile.block() instanceof Drill)
            tasks.post(() -> tile.build.applySlowdown(parent.drillEfficiency, 30f));

        if(net.client()) return;

        if(Mathf.chance(parent.spreadChance))
            ++data[key][arrayID];

        if(data[key][arrayID] >= parent.spreadTries){
            data[key][arrayID] = 0;

            if(next != null){
                if(parent.upgradeEffect != null){
                    tasks.post(() -> {
                        parent.upgradeEffect.at(tile.worldx(), tile.worldy(), 0f, tile.floor().mapColor, tile.floor());
                        Call.effect(parent.upgradeEffect, tile.worldx(), tile.worldy(), 0, tile.floor().mapColor, tile.floor());
                    });
                }

                if(parent.spreadSound != null)
                    tasks.post(() -> Call.soundAt(parent.spreadSound, tile.worldx(), tile.worldy(), 1f, 1f));

                queue[next.id][arrayID].add(tile.pos());

                if(parent.oresSpawnsProps && parent.props.size > 0 && canSpawn(id, parent.propLimit, parent.dynamicLimit) && Mathf.chance(parent.spawnChance)){
                    addProp(id);
                    queue[parent.props.random().id][2].add(tile.pos());
                }
            }

            if(parent.spread){
                for(int i = 0; i < 4; i++){
                    Tile near = tile.nearby(i);
                    if(near == null)
                        continue;

                    if(parent.replaces(near)) continue;

                    if(parent.canSpread(near)){
                        if(replacementMap[near.array()][arrayID] <= -1)
                            replacementMap[near.array()][arrayID] = near.overlayID();
                        queue[parent.id][arrayID].add(near.pos());

                        if(parent.spreadEffect != null){
                            tasks.post(() ->{
                                parent.spreadEffect.at(tile.worldx(), tile.worldy(), near.floor().mapColor);
                                Call.effect(parent.spreadEffect, near.worldx(), near.worldy(), 0, near.floor().mapColor);
                            });
                        }
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
