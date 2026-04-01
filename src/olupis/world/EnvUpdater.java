package olupis.world;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.struct.Bits;
import arc.util.*;
import arc.util.TaskQueue;
import mindustry.ai.*;
import mindustry.async.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import olupis.world.blocks.calyx.ineternal.*;

import java.io.*;
import java.util.*;

import static mindustry.Vars.*;

/** Yes, this class has race conditions and possibly memory leaks, cry about it */
public class EnvUpdater implements AsyncProcess{
    public static ObjectMap<String, ObjectSet<Block>> blacklists = new ObjectMap<>();
    public static EnvStruct[] instances;

    // a queue for stuff that has to be done on the main thread
    public static TaskQueue tasks = new TaskQueue();
    // list of tiles to set blocks on, per block type & layer
    public static BlockSet[] queue = new BlockSet[]{};
    // prop count for various floors
    private static short[] props = new short[]{};

    static boolean ready;
    static int space, wsize;
    static float wwidth, wheight;
    static double lastTick;

    // amount of layers to keep track of, 3 for vanilla (floor, overlay, block)
    static final int blockLayers = 3;

    //reduces the cost of checking if a floor is alive by moving into a spread tick + laziness instead of per game tick
    public static Bits aliveOverlays, aliveFloors;
    // Tiles with (0.5 * n)  extra chance, usually the furthest to give vein growth more outward movement
    public static HashMap<Tile, Float> spearTiles; //TODO: maybe something not a hash map
    public static float lazyTickPeriod = 5f;
    static int rushieIsTooLazyToNameThisProperlly;
    public static int spearSpread = 2;


    public static void load(){
        Events.on(EventType.ContentInitEvent.class, e ->
            Core.app.post(() ->
                queue = new BlockSet[content.blocks().size]
            )
        );
        // This is here only to avoid in-editor crashes,
        Events.on(EventType.ResizeEvent.class, e -> {
            EnvStruct[] resized = new EnvStruct[world.width() * world.height()];
            for(int i = 0; i < resized.length; i++)
                resized[i] = new EnvStruct();
            instances = resized;
        });

        SaveVersion.addCustomChunk("nyf-envstruct-io", new EnvUpdaterIO());
    }

    public static EnvStruct getStruct(int index){
        EnvStruct struct = new EnvStruct();

        Tile lookup = world.tiles.geti(index);
        Floor floor = lookup.floor();
        Floor overlay = lookup.overlay();

        // cleanup & setup
        if(floor instanceof UpdatingEnvironment e){
            if(!e.isValid(lookup))
                lookup.setFloorNet(e.replacement(), overlay);
            else struct.setFloorIndex(e.replacement());
        }

        if(overlay instanceof UpdatingEnvironment e){
            if(!e.isValid(lookup))
                lookup.setOverlayNet(e.replacement());
            else struct.setOverlayIndex(e.replacement());
        }

        if(lookup.block() instanceof UpdatingEnvironment e){
            if(!e.isValid(lookup))
                lookup.setNet(e.replacement());
            else struct.setBlockIndex(e.replacement());
        }

        if(!lookup.block().isStatic())
            space++;

        return struct;
    }

    @Override
    public void init(){
        wsize = world.width() * world.height();
        wwidth = (world.width() - 1) * tilesize;
        wheight = (world.height() - 1) * tilesize;

        instances = new EnvStruct[wsize];
        props = new short[content.blocks().size];

        aliveFloors = new Bits(wsize);
        aliveOverlays = new Bits(wsize);
        spearTiles = new HashMap<>();

        rushieIsTooLazyToNameThisProperlly = 0;

        for(int i = 0; i < wsize; i++)
            instances[i] = getStruct(i);

        Core.app.post(
            () -> ready = state.isGame() && !state.isEditor()
        );
    }

    @Override
    public void process(){
        boolean
            full = state.tick - lastTick > lazyTickPeriod,
            reCalc = rushieIsTooLazyToNameThisProperlly >= 120
        ;

        if(full){
            lastTick = state.tick;
            rushieIsTooLazyToNameThisProperlly++;
        }

        for(int i = 0; i < wsize; i++){
            Tile lookup = world.tiles.geti(i);
            EnvStruct instance = instances[i];

            boolean state = false;
            if(lookup.floor() instanceof UpdatingEnvironment e){
                if(full) e.lazyEnv(lookup);
                e.updateEnv(lookup, instance);
                state = true;
            }

            if(lookup.overlay() instanceof UpdatingEnvironment e){
                if(full)
                    e.lazyEnv(lookup);
                e.updateEnv(lookup, instance);
                state = true;
            }

            if(lookup.block() instanceof UpdatingEnvironment e){
                e.updateEnv(lookup, instance);
                state = true;
            }

            instance.infested = state;
        }

        if(reCalc){
            //todo: optimize this shit, something something rushie makes thing exist then improve later
            rushieIsTooLazyToNameThisProperlly = 0;
            spearTiles.clear();

            Seq<Building> cores = new Seq<>(), hearts = new Seq<>();
            Groups.build.each( b ->{
                if (b instanceof CoreBuild && !(b instanceof Calyxian)) cores.add(b);
                if(b instanceof Calyxian cal && cal.isHeart()) hearts.add(b);
            });

            Seq<Tile> worldTile = new Seq<>();
            for(Tile tile : world.tiles) worldTile.addUnique(tile);
            worldTile.removeAll(t -> t.solid() || t.floor().hasLiquids);
            Seq<Tile> out= new Seq<>(), outP = new Seq<>();

            for(Building building : hearts){
                out.clear();
                outP.clear();


                //Guaranteed move towards core fuckery
                @Nullable Building  core = cores.random();
                if(core != null && core.tileOn() != null)out.add(core.tileOn());

                //Random aesthetic spreading

                outP= worldTile.copy();
                for(int i = 0; i < 3; i++){
                    Tile t = worldTile.random();
                    out.add(t);
                    outP.remove(t);
                }

                Tile uwu = building.tileOn();

                if(Mathf.randomSeed(uwu.pos(), 0, 1) == 0){
                    int sizes = 2;
                    if(uwu.build != null) sizes += uwu.build.block.size;

                    uwu = world.tiles.get(
                        Mathf.randomSeed(uwu.pos(), -sizes, sizes) + uwu.x,
                        Mathf.randomSeed(uwu.pos(), -sizes, sizes) +  uwu.y
                    );

                    if(uwu == null) uwu = building.tileOn();
                }


                Seq<Tile> owo;
                Seq<Tile> qmq = new Seq<>();
                for(Tile meow : out){
                    owo = Astar.pathfind(uwu, meow, t -> t.solid() ? 100 : 1, t -> !t.floor().isDeep());

                    for(Tile tile : owo){
                        spearTiles.put(tile, spearTiles.getOrDefault(tile, 0f) + 4);
                        qmq.clear();

                        //TODO: is this actually in line for longish small veins
                        for(int iy = -spearSpread; iy < spearSpread; iy++){
                            for(int ix = -spearSpread; ix < spearSpread; ix++){
                                Tile t = world.tiles.get(tile.x + ix, tile.y + iy);
                                if(t != null)qmq.addUnique(t);
                            }
                        }

                        for(Tile idk : qmq){
                            spearTiles.put(idk, spearTiles.getOrDefault(idk, 0f) + 0.15f);
                        }
                    }
                }

            }

//            Log.err(debug.toString());
        }

    }

    @Override
    public void end(){
        if(!ready) return;

        tasks.run();
        if(net.client()) return;

        for(BlockSet set : queue){
            if(set != null && set.positions.size > 0){
                switch(set.layer){
                    case 0 -> set.positions.chunked(200, tiles -> Call.setTileFloors(set.block, tiles));
                    case 1 -> set.positions.chunked(200, tiles -> Call.setTileOverlays(set.block, tiles));
                    case 2 -> set.positions.chunked(200, tiles -> Call.setTileBlocks(set.block, Team.derelict, tiles));
                }

                set.positions.clear();
            }
        }
    }

    public static IntSeq queue(Block block){
        if(queue[block.id] == null)
            queue[block.id] = new BlockSet(block);
        return queue[block.id].positions;
    }

    @Override
    public void reset(){
        // dereference all the garbage
        Arrays.fill(queue, null);
        instances = null;
        props = null;

        aliveFloors = new Bits(wsize);
        aliveOverlays = new Bits(wsize);
        spearTiles = new HashMap<>();

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
            if(!instance.infested) return;

            int arr = instance.replacedIndexes[0];
            if(arr > 0) // air is never a good replacement for floors
                t.setFloorNet(content.block(arr), t.overlay());

            arr = instance.replacedIndexes[1];
            if(arr >= 0)
                t.setOverlayNet(content.block(arr));

            if(t.build == null){
                arr = instance.replacedIndexes[2];
                if(arr >= 0)
                    t.setNet(content.block(arr));
            }

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

        @Override
        public boolean shouldWrite(){
            return ready;
        }

        @Override
        public boolean writeNet(){
            return ready;
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
    }

    /** Class containing the bare minimum required for the Call.set methods */
    public static class BlockSet{
        final IntSeq positions = new IntSeq();
        final Block block;
        final byte layer;

        public BlockSet(Block block){
            this.block = block;
            layer = (byte) (block.isOverlay() ? 1 : block.isFloor() ? 0 : 2);
        }
    }

    /** Class containing all necessary values for environment updates, alongside methods to access and change said values for ease of use and readability */
    public static class EnvStruct{
        public final byte[] tileValues = new byte[blockLayers];
        public final short[] replacedIndexes = new short[blockLayers];
        public boolean infested;

        public EnvStruct(){
            Arrays.fill(replacedIndexes, Short.MIN_VALUE);
        }

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
        default void updateEnv(Tile tile, EnvStruct i){}

        default void lazyEnv(Tile tile){}

        default boolean isValid(Tile tile){ return false; }

        default Block replacement(){ return null; }
    }

    public static double lastTick(){
        //debuging
        return lastTick;
    }

    public static double lazy(){
        //debuging
        return rushieIsTooLazyToNameThisProperlly;
    }
}
