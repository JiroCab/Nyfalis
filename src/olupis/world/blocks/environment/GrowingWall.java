package olupis.world.blocks.environment;

import arc.graphics.*;
import arc.math.Mathf;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.net;
import static olupis.world.EnvUpdater.*;

public class GrowingWall extends StaticWall implements UpdatingEnvironment{
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

        if(Mathf.chance(growChance))
            ++data[key][0];

        if(data[key][0] >= growTries){
            data[key][0] = 0;

            if(next != null){
                if(growEffect != null){
                    tasks.post(() ->
                        Call.effect(growEffect, tile.worldx(), tile.worldy(), 0, Color.clear)
                    );
                }

                tasks.post(() ->
                    queue[next.id].add(tile.pos())
                );
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
