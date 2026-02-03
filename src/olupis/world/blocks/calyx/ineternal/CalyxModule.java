package olupis.world.blocks.calyx.ineternal;

import arc.struct.*;
import arc.util.io.*;
import mindustry.world.modules.*;

public class CalyxModule extends BlockModule{
    public CalyxGraph graph = new CalyxGraph();
    public boolean init;
    public IntSeq links = new IntSeq();
    public int species = 0;

    @Override
    public void write(Writes write){
        write.s(links.size);
        for(int i = 0; i < links.size; i++){
            write.i(links.get(i));
        }
        write.i(0);
    }

    @Override
    public void read(Reads read){
        links.clear();
        short amount = read.s();
        for(int i = 0; i < amount; i++){
            links.add(read.i());
        }
        species = read.i();
        if(species < 0 || Float.isInfinite(species)) species = 0;
    }

}
