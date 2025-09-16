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

import java.io.*;
import java.util.Arrays;

import static mindustry.Vars.*;

/** Yes, this class has race conditions and possibly memory leaks, cry about it */
public class EnvUpdater implements AsyncProcess{
    public static ObjectMap<String, ObjectSet<Block>> blacklists = new ObjectMap<>();
    public static Seq<Block> env = new Seq<>();
    public static byte[][] data = new byte[][]{};

    // a queue for stuff that has to be done on the main thread
    public static TaskQueue tasks = new TaskQueue();
    // array noting whether the tile is an instance of UpdatingEnvironment
    public static boolean[] infested = new boolean[]{};
    // array of original tile IDs
    public static short[][] replacementMap = new short[][]{};
    // list of tiles to set blocks on, per block type & layer
    public static IntSeq[][] queue = new IntSeq[][]{};
    // prop count for various floors
    private static short[] props = new short[]{};

    // cache
    static Tile lookup, ret;
    static boolean state, ready;
    static int space, wsize;
    static float wwidth, wheight;

    static final int csize = 3;


    public static void load(){
        Events.on(EventType.ContentInitEvent.class, e ->
            Core.app.post(() -> {
                queue = new IntSeq[content.blocks().size][csize];

                for(int id = 0; id < queue.length; id++)
                    for(int i = 0; i < csize; i ++)
                        queue[id][i] = new IntSeq();
            })
        );

        SaveVersion.addCustomChunk("nyf-env-io", new EnvUpdaterIO());
    }

    @Override
    public void init(){
        ready = false;
        if(Vars.state.isEditor()) return;

        wsize = world.width() * world.height();

        wwidth = (world.width() - 1) * tilesize;
        wheight = (world.height() - 1) * tilesize;

        props = new short[content.blocks().size];
        data = new byte[wsize][csize];
        infested = new boolean[wsize];
        replacementMap = new short[wsize][csize];

        for(int i = 0; i < wsize; i++){
            Arrays.fill(replacementMap[i], Short.MIN_VALUE);
            lookup = world.tiles.geti(i);

            env.addUnique(lookup.floor());
            env.addUnique(lookup.overlay());
            env.addUnique(lookup.block());

            // cleanup & setup
            if(lookup.floor() instanceof UpdatingEnvironment e){
                if(!e.isValid(lookup)){
                    lookup.setFloorNet(e.replacement(), lookup.overlay());
                    continue;
                }

                replacementMap[i][0] = e.replacement().id;
            }

            if(lookup.overlay() instanceof UpdatingEnvironment e){
                if(!e.isValid(lookup)){
                    lookup.setOverlayNet(e.replacement());
                    continue;
                }

                replacementMap[i][1] = e.replacement().id;
            }

            if(lookup.block() instanceof UpdatingEnvironment e){
                if(!e.isValid(lookup)){
                    lookup.setNet(e.replacement());
                    continue;
                }

                replacementMap[i][2] = e.replacement().id;
            }

            if(!lookup.block().isStatic())
                space++;
        }

        for(int i = 0; i < queue.length; i++)
            for(int idx = 0; idx < csize; idx++)
                queue[i][idx].clear();

        ready = true;
    }

    @Override
    public void process(){
        for(int i = 0; i < wsize; i++){
            lookup = world.tiles.geti(i);
            state = false;

            if(lookup.floor() instanceof UpdatingEnvironment e){
                e.updateEnv(lookup, i);
                state = true;
            }

            if(lookup.overlay() instanceof UpdatingEnvironment e){
                e.updateEnv(lookup, i);
                state = true;
            }

            if(lookup.block() instanceof UpdatingEnvironment e){
                e.updateEnv(lookup, i);
                state = true;
            }

            infested[i] = state;
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

    public static short index(Block key){
        short idx = (short) env.indexOf(key);
        if(idx < 0){
            env.add(key);
            idx = (short) env.size;
        }

        return idx;
    }

    /** Gets an infested tile within the specified radius, if one exists <br/>All variables are in world units */
    public static Tile getInfested(float x, float y, float radius){
        return getInfested(x, y, radius, 0f);
    }

    /** Gets an infested tile that's within the specified radius but not within the offset, if one exists <br/>All variables are in world units */
    public static Tile getInfested(float x, float y, float radius, float offset){
        for(float dx = Math.max(x - radius, 0); dx <= Math.min(x + radius, wwidth); dx += tilesize){
            for(float dy = Math.max(y - radius, 0); dy <= Math.min(y + radius, wheight); dy += tilesize){
                ret = world.tileWorld(dx, dy);
                if(ret.build == null && ret.within(x, y, radius) && (offset <= 0f || !ret.within(x, y, offset)) && infested[ret.array()])
                    return ret;
            }
        }

        return null;
    }

    // using the queue in this method is unnecessary, all this stuff is done on the main thread anyway
    public static void resetTile(Tile tile){
        if(tile == null) return;

        tile.getLinkedTiles(t -> {
            int idx = t.array(), arr;

            arr = replacementMap[idx][0];
            if(arr >= 0)
                t.setFloorNet(content.block(arr), t.overlay());

            arr = replacementMap[idx][1];
            if(arr >= 0)
                t.setOverlayNet(content.block(arr));

            arr = replacementMap[idx][2];
            if(arr >= 0)
                t.setNet(content.block(arr));
        });
    }

    public static ObjectSet<Block> blacklist(String key){
        return blacklists.get(key, ObjectSet::new);
    }

    public interface UpdatingEnvironment{
        void updateEnv(Tile tile, int i);

        boolean isValid(Tile tile);

        Block replacement();
    }

    // save chunk - only writing the necessary stuff, everything else gets recreated
    public static class EnvUpdaterIO implements SaveFileReader.CustomChunk{
        @Override
        public void write(DataOutput stream) throws IOException{
            stream.writeByte(2);

            stream.writeBoolean(ready);
            if(ready){
                stream.writeInt(wsize);
                stream.writeByte(csize);

                StringBuilder map = new StringBuilder();
                for(int i = 0; i < env.size; i++)
                    map.append(env.get(i).name).append("=").append(i).append(":");

                map.setLength(map.length() - 1);
                stream.writeUTF(map.toString());

                for(int i = 0; i < wsize; i++)
                    for(int idx = 0; idx < csize; idx++)
                        stream.writeShort(replacementMap[i][idx]);
            }
        }

        @Override
        public void read(DataInput stream) throws IOException{
            byte version = stream.readByte();

            if(stream.readBoolean()){
                int readw = stream.readInt();
                byte readc = stream.readByte();

                // old saves remain readable
                if(version == 1){
                    for(int i = 0; i < readw; i++)
                        for(int idx = 0; idx < readc; idx++)
                            replacementMap[i][idx] = stream.readShort();
                }

                if(version == 2){
                    String[] entries = stream.readUTF().split(":");
                    ObjectMap<Short, Block> map = new ObjectMap<>();

                    for(int i = 0; i < entries.length; i++){
                        String[] entry = entries[i].split("=");
                        if(entry.length == 2){
                            Block b = content.block(entry[0]);
                            if(b == null)
                                b = Blocks.air;

                            short id = (short) Strings.parseInt(entry[1], -1);
                            map.put(id, b);
                        }
                    }

                    for(int i = 0; i < readw; i++)
                        for(int idx = 0; idx < readc; idx++)
                            replacementMap[i][idx] = getID(stream.readShort(), map);
                }
            }
        }

        public short getID(short idx, ObjectMap<Short, Block> map){
            Block b = map.get(idx, Blocks.air);
            return b == Blocks.air ? -1 : b.id;
        }

        @Override
        public boolean writeNet(){
            return false;
        }
    }
}
