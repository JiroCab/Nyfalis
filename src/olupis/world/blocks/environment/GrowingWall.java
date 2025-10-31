package olupis.world.blocks.environment;

import arc.math.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import olupis.*;

import static mindustry.Vars.*;
import static olupis.NyfalisVars.*;
import static olupis.world.EnvUpdater.*;

public class GrowingWall extends StaticWall implements UpdatingEnvironment{
    public static final int arrayID = 2;

    /** The amount of times the chance must be rolled */
    public int growTries = 3;
    /** Base chance for the tile to try to grow, updated every second */
    public double growChance = 0.02;
    /** An effect spawned on this block when it grows into the next */
    public Effect growEffect = Fx.none;
    /** Block this will grow into */
    public Block next = null;
    /** Default block to replace this with when removed */
    public Block replacement = Blocks.stoneWall;

    public GrowingWall(String name, int variants){
        super(name);
        this.variants = variants;
    }

    public void updateEnv(Tile tile, int key){
        if(net.client()) return;
        if(!calyxSpreading) return;

        if(Mathf.chance(growChance * calyxSpreadingFactor))
            ++data[key][arrayID];

        if(data[key][arrayID] >= growTries){
            data[key][arrayID] = 0;

            if(next != null){
                if(growEffect != null){
                    tasks.post(() -> {
                        growEffect.at(tile.worldx(), tile.worldy(), 0f, tile.block().mapColor, tile.block());
                        Call.effect(growEffect, tile.worldx(), tile.worldy(), 0f, tile.block().mapColor, tile.block());
                    });
                }

                queue[next.id][arrayID].add(tile.pos());
            }
        }
    }

    public boolean isValid(Tile tile){
        return true;
    }

    public Block replacement(){
        return replacement;
    }
}
