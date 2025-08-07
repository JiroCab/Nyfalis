package olupis.world.blocks.misc;

import arc.util.io.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.legacy.*;

public class NyfLegacyBlock extends LegacyBlock{
    public Block replacement = Blocks.air;
    public int typeRead = 0;

    public NyfLegacyBlock(String name){
        super(name);
    }

    public NyfLegacyBlock(String name, Block bLock){
        super(name);
        replacement = bLock;
    }

    @Override
    public void removeSelf(Tile tile){
        tile.setBlock(replacement, Team.derelict);
    }


    public class NyfLegacyBuild extends Building{


        @Override
        public void read(Reads read, byte revision){

            switch(typeRead){
                //generic crafter
                case 0 -> {
                    read.f();  //progress
                    read.f();  //warmup
                }
                //generic crafter w/ legacyReadWarmup
                case 1 -> {
                    read.f();  //progress
                    read.f();  //warmup
                    read.f();  //legacyReadWarmup
                }
            }


        }
    }



}
