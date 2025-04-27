package olupis.world;

import arc.struct.*;
import arc.util.*;
import mindustry.io.SaveFileReader.*;
import mindustry.world.*;

import java.io.*;

import static mindustry.Vars.*;

public class EnvSaveIO implements CustomChunk{
    @Override
    public void write(DataOutput stream) throws IOException{
        for(Tile t : world.tiles){
            for(int i = 0; i < EnvUpdater.iterations; i++){
                var data = EnvUpdater.data.get(t);
                stream.writeShort(data == null ? 0 : data.get(i, 0));
                var replaced = EnvUpdater.replaced.get(t);
                stream.writeShort(replaced == null ? -1 : replaced.get(i, -1));
            }
        }
    }

    @Override
    public void read(DataInput stream) throws IOException{
        Log.info("Updating created snapshot with save data");

        for(Tile t : world.tiles){
            ObjectIntMap<Integer> data = new ObjectIntMap<>(EnvUpdater.iterations, 1), replaced = new ObjectIntMap<>(EnvUpdater.iterations, 1);

            for(int i = 0; i < EnvUpdater.iterations; i++){
                data.put(i, stream.readShort());
                replaced.put(i, stream.readShort());
            }

            EnvUpdater.data.put(t, data);
            EnvUpdater.replaced.put(t, replaced);
        }
    }

    @Override
    public boolean writeNet(){
        return false;
    }
}