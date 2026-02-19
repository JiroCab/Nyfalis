package olupis.world.blocks.calyx.ineternal;

import arc.math.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.world.modules.*;
import olupis.*;
import olupis.world.*;

public class CalyxModule extends BlockModule{
    public CalyxGraph graph;
    public boolean init;
    public IntSeq links = new IntSeq();
    public int species = 0;

    public CalyxModule(int sp){
        graph = new CalyxGraph(sp);
        species = sp;
    }

    public CalyxModule(){
        graph = new CalyxGraph();
    }

    @Override
    public void write(Writes write){
        write.s(links.size);
        for(int i = 0; i < links.size; i++){
            write.i(links.get(i));
        }
        species = graph.species;
        write.i(species);
    }

    @Override
    public void read(Reads read){
        links.clear();
        short amount = read.s();
        for(int i = 0; i < amount; i++){
            links.add(read.i());
        }
        species = Mathf.clamp(read.i(), 0, NyfalisVars.calyxSpecies);
    }

}
