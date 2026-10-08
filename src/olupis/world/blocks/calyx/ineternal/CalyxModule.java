package olupis.world.blocks.calyx.ineternal;

import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.world.modules.*;
import olupis.*;
import olupis.world.*;

import static olupis.world.EnvUpdater.calyxSpeciesNames;

public class CalyxModule extends BlockModule{
    public CalyxGraph graph;
    public boolean init;
    public IntSeq links = new IntSeq();
    public String species = "";

    public CalyxModule(int sp){
        graph = new CalyxGraph(calyxSpeciesNames.get(sp));
        species = calyxSpeciesNames.get(sp);
    }

    public CalyxModule(String sp){
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
        //saved as int bc backwards compact :/
        int i = calyxSpeciesNames.indexOf(species);
        write.i(i);
    }

    @Override
    public void read(Reads read){
        links.clear();
        short amount = read.s();
        for(int i = 0; i < amount; i++){
            links.add(read.i());
        }
        species = calyxSpeciesNames.get(Mathf.clamp(read.i(), 0, EnvUpdater.calyxSpeciesNames.size));
    }

    public Color getColour(){
        return NyfWorldFuckingHelper.calyxSpeciesColors(calyxSpeciesNames.indexOf(this.species));
    }

}
