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
        try{
            stream.writeShort(EnvUpdater.propCount.size);
            for(var entry : EnvUpdater.propCount){
                stream.writeShort(entry.key.id);
                stream.writeShort(entry.value);
            }

            for(Tile t : world.tiles){
                for(int i = 0; i < EnvUpdater.iterations; i++){
                    stream.writeShort(EnvUpdater.fetch(EnvUpdater.data, t, i));
                    stream.writeShort(EnvUpdater.fetch(EnvUpdater.replaced, t, i));
                }
            }
        }catch(Exception e){
            Log.warn("Couldn't save snapshot data for the EnvUpdater, skipping...");
            Log.err(e);
        }
    }

    @Override
    public void read(DataInput stream) throws IOException{
        TaskQueue save = new TaskQueue();

        Log.info("Updating created snapshot with save data");
        try{
            int pr = stream.readShort();
            if(pr >= 1){

            
            for(int i = 0; i < pr; i++){
                Block b = content.locks().get(stream.readShort());
                int val = stream.readShort();
                if(b != null || val > 0)
                    save.post(() -> EnvUpdater.propCount.put(b, val));
            }
        }

            for(Tile t : world.tiles){
                int[] dsave = new int[4], rsave = new int[4];
                for(int i = 0; i < EnvUpdater.iterations; i++){
                    dsave[i] = stream.readShort();
                    rsave[i] = stream.readShort();
                }

                save.post(() ->{
                    EnvUpdater.data.put(t, dsave);
                    EnvUpdater.replaced.put(t, rsave);
                });
            }
        }catch(Exception e){
            Log.warn("Possibly corrupted or unsupported EnvUpdater save detected, skipping...");
            save.clear();
            return;
        }

        // do not write anything until we're sure that the data is intact
        save.run(
            
        );
    }

    @Override
    public boolean writeNet(){
        return false;
    }
}