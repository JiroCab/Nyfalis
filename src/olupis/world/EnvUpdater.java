package olupis.world;

import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.async.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import java.io.*;
import java.util.Arrays;

import static mindustry.Vars.*;

/** Yes, this class has race conditions and possibly memory leaks, cry about it */
public class EnvUpdater implements AsyncProcess{
    public static ObjectMap<String, ObjectSet<Block>> blacklists = new ObjectMap<>();
    public static EnvStruct[] instances;

    // a queue for stuff that has to be done on the main thread
    public static TaskQueue tasks = new TaskQueue();
    // list of tiles to set blocks on, per block type & layer
    public static IntSeq[][] queue = new IntSeq[][]{};
    // prop count for various floors
    private static short[] props = new short[]{};

    static boolean ready;
    static int space, wsize;
    static float wwidth, wheight;

    // amount of layers to keep track of, 3 for vanilla (floor, overlay, block)
    static final int blockLayers = 3;


    public static void load(){
        Events.on(EventType.ContentInitEvent.class, e ->
            Core.app.post(() -> {
                queue = new IntSeq[content.blocks().size][blockLayers];

                for(int id = 0; id < queue.length; id++)
                    for(int i = 0; i < blockLayers; i ++)
                        queue[id][i] = new IntSeq();
            })
        );

        SaveVersion.addCustomChunk("nyf-envstruct-io", new EnvUpdaterIO());
    }

    @Override
    public void init(){
        ready = false;
        if(Vars.state.isEditor()) return;

        wsize = world.width() * world.height();
        wwidth = (world.width() - 1) * tilesize;
        wheight = (world.height() - 1) * tilesize;

        instances = new EnvStruct[wsize];
        props = new short[content.blocks().size];

        for(int i = 0; i < wsize; i++){
            EnvStruct struct = instances[i] = new EnvStruct();

            Tile lookup = world.tiles.geti(i);
            // avoid casting multiple times
            Floor floor = lookup.floor();
            Floor overlay = lookup.overlay();

            // cleanup & setup
            if(floor instanceof UpdatingEnvironment e){
                if(!e.isValid(lookup)){
                    lookup.setFloorNet(e.replacement(), overlay);
                    continue;
                }

                struct.setFloorIndex(e.replacement());
            }

            if(overlay instanceof UpdatingEnvironment e){
                if(!e.isValid(lookup)){
                    lookup.setOverlayNet(e.replacement());
                    continue;
                }

                struct.setOverlayIndex(e.replacement());
            }

            if(lookup.block() instanceof UpdatingEnvironment e){
                if(!e.isValid(lookup)){
                    lookup.setNet(e.replacement());
                    continue;
                }

                struct.setBlockIndex(e.replacement());
            }

            if(!lookup.block().isStatic())
                space++;
        }

        for(int i = 0; i < queue.length; i++)
            for(int idx = 0; idx < blockLayers; idx++)
                queue[i][idx].clear();

        ready = true;
    }

    @Override
    public void process(){
        for(int i = 0; i < wsize; i++){
            Tile lookup = world.tiles.geti(i);
            EnvStruct instance = instances[i];

            boolean state = false;
            if(lookup.floor() instanceof UpdatingEnvironment e){
                e.updateEnv(lookup, instance);
                state = true;
            }

            if(lookup.overlay() instanceof UpdatingEnvironment e){
                e.updateEnv(lookup, instance);
                state = true;
            }

            if(lookup.block() instanceof UpdatingEnvironment e){
                e.updateEnv(lookup, instance);
                state = true;
            }

            instance.infested = state;
        }
    }

    @Override
    public void end(){
        if(!ready) return;

        tasks.run();
        for(int i = 0; i < queue.length; i++){
            int index = i;

            if(queue[i][0].size > 0){
                queue[i][0].chunked(200, tiles -> Call.setTileFloors(content.block(index), tiles));
                queue[i][0].clear();
            }

            if(queue[i][1].size > 0){
                queue[i][1].chunked(200, tiles -> Call.setTileOverlays(content.block(index), tiles));
                queue[i][1].clear();
            }

            if(queue[i][2].size > 0){
                queue[i][2].chunked(200, tiles -> Call.setTileBlocks(content.block(index), Team.derelict, tiles));
                queue[i][2].clear();
            }
        }
    }

    @Override
    public void reset(){
        ready = false;
    }

    @Override
    public boolean shouldProcess(){
        return ready;
    }

    public static void addProp(int id){
        ++props[id];
    }

    public static boolean canSpawn(int id, int limit, float scaling){
        return props[id] < limit + (scaling * space);
    }

    /** Gets an infested tile within the specified radius, if one exists <br/>All variables are in world units */
    public static Tile getInfested(float x, float y, float radius){
        return getInfested(x, y, radius, 0f);
    }

    /** Gets an infested tile that's within the specified radius but not within the offset, if one exists <br/>All variables are in world units */
    public static Tile getInfested(float x, float y, float radius, float offset){
        for(float dx = Math.max(x - radius, 0); dx <= Math.min(x + radius, wwidth); dx += tilesize){
            for(float dy = Math.max(y - radius, 0); dy <= Math.min(y + radius, wheight); dy += tilesize){
                Tile ret = world.tileWorld(dx, dy);
                if(ret.build == null && ret.within(x, y, radius) && (offset <= 0f || !ret.within(x, y, offset)) && instances[ret.array()].infested)
                    return ret;
            }
        }

        return null;
    }

    // using the queue in this method is unnecessary, all this stuff is done on the main thread anyway
    public static void resetTile(Tile tile){
        if(tile == null) return;

        tile.getLinkedTiles(t -> {
            int ptr = t.array();
            EnvStruct instance = instances[ptr];

            int arr = instance.replacedIndexes[0];
            if(arr > 0) // air is never a good replacement for floors
                t.setFloorNet(content.block(arr), t.overlay());

            arr = instance.replacedIndexes[1];
            if(arr >= 0)
                t.setOverlayNet(content.block(arr));

            arr = instance.replacedIndexes[2];
            if(arr >= 0)
                t.setNet(content.block(arr));

            instances[ptr] = new EnvStruct();
        });
    }

    public static ObjectSet<Block> blacklist(String key){
        return blacklists.get(key, ObjectSet::new);
    }

    // save chunk - only writing the necessary stuff, everything else gets recreated
    public static class EnvUpdaterIO implements SaveFileReader.CustomChunk{
        final short exitVal = Short.MIN_VALUE + 1;
        @Override
        public void write(DataOutput stream) throws IOException{
            stream.writeByte(1);

            stream.writeBoolean(ready);
            if(ready)
                writeOptimizedDataChunk(stream);
        }

        @Override
        public void read(DataInput stream) throws IOException{
            byte version = stream.readByte();

            if(stream.readBoolean())
                readOptimizedDataChunk(stream, version);
        }

        public void writeName(DataOutput stream, short id) throws IOException{
            Block b = content.block(id);
            if(b == null)
                b = Blocks.air;

            stream.writeUTF(b.name);
        }

        public short readName(DataInput stream) throws IOException{
            Block b = content.block(stream.readUTF());
            if(b == null)
                b = Blocks.air;

            return b.id;
        }

        public void writeOptimizedDataChunk(DataOutput stream) throws IOException{
            stream.writeByte(blockLayers);
            for(int i = 0; i < blockLayers; i++){
                int count = 0;
                short index = instances[0].formatID(i);
                for(EnvStruct instance : instances){
                    if(count >= Short.MAX_VALUE || index != instance.formatID(i)){
                        stream.writeShort(count);
                        writeName(stream, index);

                        index = instance.formatID(i);
                        count = 1;
                    }else ++count;
                }

                if(count > 0){
                    stream.writeShort(count);
                    writeName(stream, index);
                }

                stream.writeShort(exitVal);
            }
        }

        public void readOptimizedDataChunk(DataInput stream, int version) throws IOException{
            int layers = stream.readByte();
            for(int i = 0; i < layers; i++){
                int arrayIndex = 0;
                while(true){
                    short count = stream.readShort();
                    if(count == exitVal)
                        break;

                    short id = readName(stream);
                    for(int idx = arrayIndex; idx < (arrayIndex + count); idx++)
                        instances[idx].replacedIndexes[i] = id;

                    arrayIndex += count;
                }
            }
        }

        @Override
        public boolean writeNet(){
            return false;
        }
    }

    /** Class containing all necessary values for environment updates, alongside methods to access and change said values for ease of use and readability */
    public static class EnvStruct{
        public final byte[] tileValues = new byte[blockLayers];
        public final short[] replacedIndexes = new short[blockLayers];
        public boolean infested;

        public short formatID(int layer){
            return replacedIndexes[layer] > 0 ? replacedIndexes[layer] : Blocks.air.id;
        }

        public void setFloorIndex(Block block){
            short value = block.id;
            if(block instanceof UpdatingEnvironment e)
                value = e.replacement().id;

            replacedIndexes[0] = value;
        }

        public int getIncrementFloor(){
            return tileValues[0]++;
        }

        public void clearFloorVal(){
            tileValues[0] = 0;
        }

        public boolean canWriteFloor(){
            return replacedIndexes[0] <= 0;
        }

        public void setOverlayIndex(Block block){
            short value = block.id;
            if(block instanceof UpdatingEnvironment e)
                value = e.replacement().id;

            replacedIndexes[1] = value;
        }

        public int getIncrementOverlay(){
            return tileValues[1]++;
        }

        public void clearOverlayVal(){
            tileValues[1] = 0;
        }

        public boolean canWriteOverlay(){
            return replacedIndexes[1] <= 0;
        }

        public void setBlockIndex(Block block){
            short value = block.id;
            if(block instanceof UpdatingEnvironment e)
                value = e.replacement().id;

            replacedIndexes[2] = value;
        }

        public int getIncrementBlock(){
            return tileValues[2]++;
        }

        public void clearBlockVal(){
            tileValues[2] = 0;
        }

        public boolean canWriteBlock(){
            return replacedIndexes[2] <= 0;
        }
    }

    public interface UpdatingEnvironment{
        void updateEnv(Tile tile, EnvStruct i);

        boolean isValid(Tile tile);

        Block replacement();
    }
}
