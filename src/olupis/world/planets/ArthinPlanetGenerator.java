package olupis.world.planets;

import arc.graphics.Color;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mindustry.ai.Astar;
import mindustry.content.*;
import mindustry.ctype.UnlockableContent;
import mindustry.game.*;
import mindustry.maps.generators.BaseGenerator;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;
import olupis.content.*;

import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;
import static olupis.content.NyfalisBlocks.*;
import static olupis.content.NyfalisSectors.*;

public class ArthinPlanetGenerator extends PlanetGenerator{
    //alternate, less direct generation (wip)
    public static boolean alt = false;

    BaseGenerator basegen = new BaseGenerator();
    float scl = 1.7f;
    float waterOffset = 0.085f;
    float extraMaxCoreZonesScale = 0.25f;
    public Seq<Integer> lakesSeq = Seq.with(18, 19, 24, 26, 25);

    Block[][] arr =
            {
                    {ice, dirt, stone, deepwater, redSandSnow , yellowGrass,},
                    { stone, yellowGrass , mossierStone, deepwater, ice, hardenMud},
                    { mossierStone, stone, dirt, deepwater, yellowGrass, mossierStone},
                    { yellowGrass, dirt, redSand, deepwater, redSand, mossierStone },
                    {redSandSnow, redSand, mossyStone, deepwater, ice, deepwater},
                    {redSandSnow, mossierStone, stone, dirt, ice, redSandSnow},
            };

    ObjectMap<Block, Block> dec = ObjectMap.of(
            grass, bush,
            grass, boulder,
            mossStone, mossyBoulder,
            mossiestStone, mossyBoulder,
            mossierStone, mossyBoulder,
            mossStone, mossyBoulder
    );

    ObjectMap<Block, Block> deserts = ObjectMap.of(
            mud, yellowGrass,
            dirt, hardenMud
    );

    {
        baseSeed = 69;
        defaultLoadout = NyfalisSchematic.basicRemnant;
    }

    @Override
    public int getSectorSize(Sector sector){
        int res = (int)(sector.rect.radius * 800);
        return res % 2 == 0 ? res : res + 1;
    }

    public void addWeather(Sector sector, Rules rules){

        //apply weather based on terrain
        ObjectIntMap<Block> floorc = new ObjectIntMap<>();
        ObjectSet<UnlockableContent> content = new ObjectSet<>();

        for(Tile tile : world.tiles){
            if(tile == null )continue;
            
            if(world.getDarkness(tile.x, tile.y) >= 3){
                continue;
            }

            Liquid liquid = tile.floor().liquidDrop;
            if(tile.floor().itemDrop != null) content.add(tile.floor().itemDrop);
            if(tile.overlay().itemDrop != null) content.add(tile.overlay().itemDrop);
            if(liquid != null) content.add(liquid);

            if(!tile.block().isStatic()){
                floorc.increment(tile.floor());
                if(tile.overlay() != air){
                    floorc.increment(tile.overlay());
                }
            }
        }

        //sort counts in descending order
        Seq<ObjectIntMap.Entry<Block>> entries = floorc.entries().toArray();
        entries.sort(e -> -e.value);
        //remove all blocks occurring < 30 times - unimportant
        entries.removeAll(e -> e.value < 30);

        Block[] floors = new Block[entries.size];
        for(int i = 0; i < entries.size; i++){
            floors[i] = entries.get(i).key;
        }

        //bad contains() code, but will likely never be fixed
        boolean hasSnow = floors.length > 0 && (floors[0].name.contains("ice") || floors[0].name.contains("snow"));
        boolean hasRain = floors.length > 0 && !hasSnow && content.contains(Liquids.water) && !floors[0].name.contains("sand");
        boolean hasDesert = floors.length > 0 && !hasSnow && !hasRain && floors[0] == sand;
        boolean hasMoss =  floors[0].name.contains("moss") || floors[0].name.contains("slop");

        if(hasSnow){
            rules.weather.add(new Weather.WeatherEntry(Weathers.snow));
        }

        if(hasRain){
            rules.weather.add(new Weather.WeatherEntry(Weathers.rain));
            rules.weather.add(new Weather.WeatherEntry(Weathers.fog));
        }

        if(hasDesert){
            rules.weather.add(new Weather.WeatherEntry(Weathers.sandstorm));
        }

        if(hasMoss){
            rules.weather.add(new Weather.WeatherEntry(NyfalisAttributeWeather.mossMist));
        }
        rules.weather.add(new Weather.WeatherEntry(NyfalisAttributeWeather.cloudShadow));
    }

    float water = 1.8f / arr[0].length;

    float rawHeight(Vec3 position){
        position = Tmp.v33.set(position).scl(scl);
        return (Mathf.pow(Simplex.noise3d(seed, 7, 0.5f, 1f/3f, position.x, position.y, position.z), 2.3f) + waterOffset) / (1f + waterOffset);
    }

    @Override
    public void generateSector(Sector sector){
        //No generating sectors, everything is hand made
    }

    @Override
    public float getHeight(Vec3 position){
        float height = rawHeight(position);
        return Math.max(height, water);
    }

    @Override
    public Color getColor(Vec3 position){
        Block block = getBlock(position);
        //replace salt with sand color
        if(block == salt) return sand.mapColor;
        return Tmp.c1.set(block.mapColor).a(1f - block.albedo);
    }

    @Override
    public void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 22) > 0.31){
            tile.block = air;
        }
    }

    Block getBlock(Vec3 position){
        float height = rawHeight(position);
        Tmp.v31.set(position);
        position = Tmp.v33.set(position).scl(scl);
        float rad = scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2f) / (rad));
        float tnoise = Simplex.noise3d(seed, 7, 0.56, 1f/3f, position.x, position.y + 999f, position.z);
        temp = Mathf.lerp(temp, tnoise, 0.5f);
        height *= 1.2f;
        height = Mathf.clamp(height);

        float tar = Simplex.noise3d(seed, 4, 0.55f, 1f/2f, position.x, position.y + 999f, position.z) * 0.3f + Tmp.v31.dst(0, 0, 1f) * 0.2f;

        Block res = arr[Mathf.clamp((int)(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp((int)(height * arr[0].length), 0, arr[0].length - 1)];
        if(tar > 0.7f){
            return deserts.get(res, res);
        }else{
            return res;
        }
    }

    @Override
    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag){
        Vec3 v = sector.rect.project(x, y).scl(5f);
        return Simplex.noise3d(seed, octaves, falloff, 1f / scl, v.x, v.y, v.z) * (float)mag;
    }

    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag, int subSeed, float scl2){
        Vec3 v = sector.rect.project(x, y).scl(scl2);
        return Simplex.noise3d(subSeed, octaves, falloff, 1f / scl, v.x, v.y, v.z) * (float)mag;
    }

    @Override
    protected void generate(){

        class Room{
            final int x;
            final int y;
            final int radius;
            final ObjectSet<Room> connected = new ObjectSet<>();

            Room(int x, int y, int radius){
                this.x = x;
                this.y = y;
                this.radius = radius;
                connected.add(this);
            }

            void join(int x1, int y1, int x2, int y2){
                float nscl = rand.random(100f, 140f) * 6f;
                int stroke = rand.random(3, 9);
                brush(pathfind(x1, y1, x2, y2, tile -> (tile.solid() ? 50f : 0f) + noise(tile.x, tile.y, 2, 0.4f, 1f / nscl) * 500, Astar.manhattan), stroke);
            }

            void connect(Room to){
                if(!connected.add(to) || to == this) return;

                Vec2 midpoint = Tmp.v1.set(to.x, to.y).add(x, y).scl(0.5f);
                rand.nextFloat();

                if(alt){
                    midpoint.add(Tmp.v2.set(1, 0f).setAngle(Angles.angle(to.x, to.y, x, y) + 90f * (rand.chance(0.5) ? 1f : -1f)).scl(Tmp.v1.dst(x, y) * 2f));
                }else{
                    //add randomized offset to avoid straight lines
                    midpoint.add(Tmp.v2.setToRandomDirection(rand).scl(Tmp.v1.dst(x, y)));
                }

                midpoint.sub(width/2f, height/2f).limit(width / 2f / Mathf.sqrt3).add(width/2f, height/2f);

                int mx = (int)midpoint.x, my = (int)midpoint.y;

                join(x, y, mx, my);
                join(mx, my, to.x, to.y);
            }

            void joinLiquid(int x1, int y1, int x2, int y2){
                float nscl = rand.random(100f, 140f) * 6f;
                int rad = rand.random(7, 11);
                int avoid = 2 + rad;
                var path = pathfind(x1, y1, x2, y2, tile -> (tile.solid() || !tile.floor().isLiquid ? 70f : 0f) + noise(tile.x, tile.y, 2, 0.4f, 1f / nscl) * 500, Astar.manhattan);
                path.each(t -> {
                    //don't place liquid paths near the core
                    if(Mathf.dst2(t.x, t.y, x2, y2) <= avoid * avoid){
                        return;
                    }

                    for(int x = -rad; x <= rad; x++){
                        for(int y = -rad; y <= rad; y++){
                            int wx = t.x + x, wy = t.y + y;
                            if(Structs.inBounds(wx, wy, width, height) && Mathf.within(x, y, rad)){
                                Tile other = tiles.getn(wx, wy);
                                other.setBlock(air);
                                if(Mathf.within(x, y, rad - 1) && !other.floor().isLiquid){
                                    Floor floor = other.floor();
                                    //TODO does not respect tainted floors
                                    other.setFloor(waterCheck(floor, floor.isLiquid, true));
                                }
                            }
                        }
                    }
                });
            }

            void connectLiquid(Room to){
                if(to == this) return;

                Vec2 midpoint = Tmp.v1.set(to.x, to.y).add(x, y).scl(0.5f);
                rand.nextFloat();

                //add randomized offset to avoid straight lines
                midpoint.add(Tmp.v2.setToRandomDirection(rand).scl(Tmp.v1.dst(x, y)));
                midpoint.sub(width/2f, height/2f).limit(width / 2f / Mathf.sqrt3).add(width/2f, height/2f);

                int mx = (int)midpoint.x, my = (int)midpoint.y;

                joinLiquid(x, y, mx, my);
                joinLiquid(mx, my, to.x, to.y);
            }
        }

        cells(4);
        distort(10f, 12f);

        float constraint = 1.3f;
        float radius = width / 2f / Mathf.sqrt3;
        int rooms = rand.random(2, 5);
        Seq<Room> roomseq = new Seq<>();

        for(int i = 0; i < rooms; i++){
            Tmp.v1.trns(rand.random(360f), rand.random(radius / constraint));
            float rx = (width/2f + Tmp.v1.x);
            float ry = (height/2f + Tmp.v1.y);
            float maxrad = radius - Tmp.v1.len();
            float rrad = Math.min(rand.random(9f, maxrad / 2f), 30f);
            roomseq.add(new Room((int)rx, (int)ry, (int)rrad));
        }

        //check positions on the map to place the player spawn. this needs to be in the corner of the map
        Room spawn = null;

        Seq<Room> enemies = new Seq<>();
        int enemySpawns = rand.random(1, Math.max((int)(sector.threat * 4), 1));

        int offset = rand.nextInt(360);
        float length = width/2.55f - rand.random(13, 23);
        int angleStep = 5;
        int waterCheckRad = 5;

        for(int i = 0; i < 360; i+= angleStep){
            int angle = offset + i;
            int cx = (int)(width/2 + Angles.trnsx(angle, length));
            int cy = (int)(height/2 + Angles.trnsy(angle, length));

            int waterTiles = 0;

            //check for water presence
            for(int rx = -waterCheckRad; rx <= waterCheckRad; rx++){
                for(int ry = -waterCheckRad; ry <= waterCheckRad; ry++){
                    Tile tile = tiles.get(cx + rx, cy + ry);
                    if(tile == null || tile.floor().liquidDrop != null){
                        waterTiles ++;
                    }
                }
            }

            if(waterTiles <= 4 || (i + angleStep >= 360)){
                roomseq.add(spawn = new Room(cx, cy, rand.random(8, 15)));

                for(int j = 0; j < enemySpawns; j++){
                    float enemyOffset = rand.range(60f);
                    Tmp.v1.set(cx - width/2f, cy - height/2f).rotate(180f + enemyOffset).add(width/2f, height/2f);
                    Room espawn = new Room((int)Tmp.v1.x, (int)Tmp.v1.y, rand.random(8, 16));
                    roomseq.add(espawn);
                    enemies.add(espawn);
                }

                break;
            }
        }

        //clear radius around each room
        for(Room room : roomseq){
            erase(room.x, room.y, room.radius);
        }

        //randomly connect rooms together
        int connections = rand.random(Math.max(rooms - 1, 1), rooms + 3);
        for(int i = 0; i < connections; i++){
            roomseq.random(rand).connect(roomseq.random(rand));
        }

        for(Room room : roomseq){
            spawn.connect(room);
        }

        Room fspawn = spawn;

        cells(1);

        int tlen = tiles.width * tiles.height;
        int total = 0, waters = 0;

        for(int i = 0; i < tlen; i++){
            Tile tile = tiles.geti(i);
            if(tile.block() == air){
                total ++;
                if(tile.floor().liquidDrop == Liquids.water){
                    waters ++;
                }
            }
        }

        boolean naval = (float)waters / total >= 0.19f;

        //create water pathway if the map is flooded
        if(naval){
            for(Room room : enemies){
                room.connectLiquid(spawn);
            }
        }

        distort(250f, 5f);

        int secIdDigit = sector.id <= 9 ? sector.id : sector.id  <= 19 ? sector.id %10 : sector.id %100;
        boolean genLake = (secIdDigit  % 2) != 0;
        if(lakesSeq.contains(sector.id)) genLake = true;
        boolean finalGenLake = genLake;

        //shoreline setup & lakes
        if(genLake) {
            int lakeSize = Math.max(sector.id, 4);

            pass((x, y) -> {
                Tile tile = tiles.get(x, y);
                if(world.getWallDarkness(tile)  > 0){
                    floor = waterCheck(floor, false);
                } else {

                    for (int yi = 0; yi < lakeSize; yi++) {
                        for (int xi = 0; xi < lakeSize; xi++) {
                            for (int i = 0; i < 4; i++) {
                                Tile near = world.tile(x + Geometry.d4[i].x + xi, y + Geometry.d4[i].y + yi);
                                if (near != null && near.floor().isLiquid) {
                                    floor = waterCheckShallow(floor, false);
                                }
                            }
                        }
                    }

                }
            });
        }

        //rivers
        pass((x, y) -> {
            if(block.solid) return;

            Vec3 v = sector.rect.project(x, y);

            float rr = Simplex.noise2d(sector.id, (float)2, 0.6f, 1f / 5f, x, y) * 0.1f;
            float value = Ridged.noise3d(2, v.x, v.y, 1, 1, 1f / 55f) + rr - rawHeight(v) * 0f;
            int rrmul = finalGenLake ? 66 : 44;
            float rrscl = rr * rrmul - 2;

            if((value) > 0.17f && !Mathf.within(x, y, fspawn.x, fspawn.y, 12 + rrscl)){
                boolean deep = value > 0.17f + 0.1f && !Mathf.within(x, y, fspawn.x, fspawn.y, 15 + rrscl);
                //do not place rivers on ice, they're frozen
                //ignore pre-existing liquids
                if(!(floor == ice || floor == iceSnow || floor == snow || floor.asFloor().isLiquid)){
                    floor = waterCheck(floor, deep, true);
                }
            }
        });

        blendf(redSand, redSandWater, 2, Seq.with(Blocks.water), true);
        blendf(yellowGrass, yellowMossyWater, 2, Seq.with(Blocks.water), true);
        blendf(deepwater, algaeWaterDeep, 3, Seq.with(algaeWater), true);

        if(naval){
            int deepRadius = 2;

            //TODO code is very similar, but annoying to extract into a separate function
            pass((x, y) -> {
                if(floor.asFloor().isLiquid && !floor.asFloor().isDeep() && !floor.asFloor().shallow){

                    for(int cx = -deepRadius; cx <= deepRadius; cx++){
                        for(int cy = -deepRadius; cy <= deepRadius; cy++){
                            if((cx) * (cx) + (cy) * (cy) <= deepRadius * deepRadius){
                                int wx = cx + x, wy = cy + y;

                                Tile tile = tiles.get(wx, wy);
                                if(tile != null && (tile.floor().shallow || !tile.floor().isLiquid)){
                                    //found something shallow, skip replacing anything
                                    return;
                                }
                            }
                        }
                    }

                    floor = floor == Blocks.water ? deepwater : Blocks.water;
                }
            });
        }

        trimDark();

        median(2);

        inverseFloodFill(tiles.getn(spawn.x, spawn.y));


        Seq<Block> oresSeq = Seq.with(oreIron, oreLead);
        Seq<Integer> quartzBan = Seq.with(17, 18, 28, 26, 27, 19, 24, 23, 22, 20, 1, 22, 30, 4);
        Seq<Integer> copperBan = Seq.with(18, 26, 27, 28, 17, 22, 30, 4, 14);
        if(!quartzBan.contains(sector.id))oresSeq.add(oreQuartz);
        if(!copperBan.contains(sector.id))oresSeq.add(oreCopper);
        if(genLake)oresSeq.addAll(kelp, blueCorals);


        float poles = Math.abs(sector.tile.v.y);
        FloatSeq frequencies = new FloatSeq();
        for(int i = 0; i < oresSeq.size; i++){
            frequencies.add(rand.random(-0.1f, 0.01f) - i * 0.01f + (poles) * 0.04f);
        }

        pass((x, y) -> {
            int offsetX = x - 4, offsetY = y + 23;
//            Seq<String> logs = new Seq<>();
            for(int i = oresSeq.size - 1; i >= 0; i--){
                Block entry = oresSeq.get(i);
                int freq = Mathf.round(frequencies.get(i));
                float nos = noise(offsetX, offsetY - i*999, 50 + (2 * freq) , 0.3, 69,69, freq, 2f );
                //float nos = noise(offsetX, offsetY - i*999, 1.7, 0.3, 69,86, freq );
                if(entry == kelp || entry == blueCorals) nos += 10;
                if(nos > 58 ){
                    if(entry != blueCorals)floor = solidCheck(floor);
                    else floor = waterCheck(floor, true, true);  // random chasms in the water

                    if (entry == kelp || entry == blueCorals) block = air; //just add extra island if the sector is flooded
                    else ore = entry;

                    break;
                }
            }
//            if(!logs.isEmpty())Log.err(logs + "");
        });

        //make "ore island" be more natural
        blendOreIsland(2,true);

        //replace sandwall to dirt walls
        pass((x, y) -> {
            if(tiles.get(x, y).block() == sandWall) block = (tiles.get(x, y).floor() != deepwater ) ? dirtWall : air;
        });

        float difficulty = sector.threat;
        int zoneCap = 1 + Math.round(extraMaxCoreZonesScale * difficulty);

        //decorations
        pass((x, y) -> {
            Tile tile = tiles.get(x, y);
            //random grass
            if(floor == grass){
                if(Math.abs(0.5f - noise(x - 90, y, 4, 0.8, 65)) > 0.02){
                    floor = grass;
                }
            }

            //tar
            if(floor == darksand){
                if(Math.abs(0.5f - noise(x - 40, y, 2, 0.7, 80)) > 0.25f &&
                        Math.abs(0.5f - noise(x, y + sector.id*10, 1, 1, 60)) > 0.41f && !(roomseq.contains(r -> Mathf.within(x, y, r.x, r.y, 15)))){
                    floor = tar;
                }
            }

            //hotrock tweaks
            if(floor == hotrock){
                if(Math.abs(0.5f - noise(x - 90, y, 4, 0.8, 80)) > 0.035){
                    floor = basalt;
                }else{
                    ore = air;
                    boolean all = true;
                    for(Point2 p : Geometry.d4){
                        Tile other = tiles.get(x + p.x, y + p.y);
                        if(other == null || (other.floor() != hotrock && other.floor() != magmarock)){
                            all = false;
                        }
                    }
                    if(all){
                        floor = magmarock;
                    }
                }
            }

            //Shrubs
            if(block.solid && rand.chance(tile.floor().isLiquid ? 0.0015 : 0.015)){
                if(solidMossYellow.contains(block)) block = solidMossYellow.random();
                else if(block == shrubs) block = solidShurbsGrass.random();
            }

            //Trees
            if(block.solid && rand.chance(tile.floor().isLiquid ? 0.0015 : 0.015)){
                if(solidMossYellow.contains(block)){
                    block = treesYellow.random();
                } else  block = treesGreen.random();
            }

            //water features
            if(tile.floor().isLiquid && tile.overlay() == air && tile.block() == air && rand.chance(0.02)) {
                if(floor == deepwater && rand.chance(0.5)){
                    floor = coralReef;
                } else ore = waterFeatures.random();
            }
            if(tile.floor().isLiquid && tile.overlay() == air && tile.block() == air && rand.chance(0.01)) {
                block = glowLilly;
            }

            //Ore Randomness TODO: spreading moss addition
            if(tile.overlay() == oreCopper){
               if(rand.chance(0.45)) ore = oreOxidizedCopper;
//               else if(rand.chance(0.25)) ore = mossyCopper;
            }
            if(tile.overlay() == oreLead){
                if(rand.chance(0.45)) ore = oreOxidizedLead;
//               else if(rand.chance(0.25)) ore = mossyLead;
            }

            //random stuff
            dec: {
                for(int i = 0; i < 4; i++){
                    Tile near = world.tile(x + Geometry.d4[i].x, y + Geometry.d4[i].y);
                    if(near != null && near.block() != air){
                        break dec;
                    }
                }

                if(rand.chance(0.03) && floor.asFloor().hasSurface() && block == air){
                    if(floor.asFloor().decoration != air){
                        if((floor == grass || mossGreen.contains(floor) && rand.chance(0.2))) block =  Seq.with(bush, yellowBush, glowBloom).random();
                        else if (floor == redSand & rand.chance(0.4)) block = deadBush;
                        else block = dec.get(floor, floor.asFloor().decoration);
                    } else if(floor == mud || floor == dirt){
                         block = deadBush;
                    }
                }
            }

        });


        for(Tile tile : tiles){
            if(tile.overlay().needsSurface && !tile.floor().hasSurface()){
                tile.setOverlay(air);
            }
        }

        //ehh to lazy
        tiles.get(spawn.x, spawn.y).setFloor(coreZone.asFloor());
        tiles.get(spawn.x +1, spawn.y).setFloor(coreZone.asFloor());
        tiles.get(spawn.x +1, spawn.y +1).setFloor(coreZone.asFloor());
        tiles.get(spawn.x, spawn.y + 1).setFloor(coreZone.asFloor());
        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        Seq<Room> zRoom = roomseq;
        zRoom.remove(spawn);
        for(int j = 0; j < zoneCap; j++){
            Math.abs(Mathf.randomSeed(zRoom.size,0,  zRoom.size));
            Room zone = roomseq.random(rand);
            tiles.getn(zone.x, zone.y).setFloor(coreZone.asFloor());
            tiles.getn(zone.x +1, zone.y).setFloor(coreZone.asFloor());
            tiles.getn(zone.x +1, zone.y +1).setFloor(coreZone.asFloor());
            tiles.getn(zone.x, zone.y + 1).setFloor(coreZone.asFloor());
            zRoom.remove(zone);
        }

        for(Room espawn : enemies){
            tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn);
        }

        if(sector.hasEnemyBase()){
            basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)), tiles.get(spawn.x, spawn.y), state.rules.waveTeam, sector, difficulty);

            state.rules.attackMode = sector.info.attack = true;
        }else{
            state.rules.winWave = sector.info.winWave = 10 + 5 * (int)Math.max(difficulty * 10, 1) +Mathf.randomSeed(sector.id, 0 ,10);
        }

        float waveTimeDec = 0.4f;

        state.rules.waveSpacing = Mathf.lerp(60 * 65 * 2, 60f * 60f * 1f, Math.max(difficulty - waveTimeDec, 0f));
        state.rules.waves = sector.info.waves = true;
        state.rules.loadout.clear().add(new ItemStack(NyfalisItemsLiquid.rustyIron, 100 * Math.round(sector.threat)));
        state.rules.enemyCoreBuildRadius = 600f;

        //spawn air only when spawn is blocked
        state.rules.spawns = NyfalisWaves.generate(difficulty, new Rand(sector.id), state.rules.attackMode, state.rules.attackMode && spawner.countGroundSpawns() == 0, naval);
    }

    public Floor waterCheck(Block floor, boolean deep){
        return waterCheck(floor, deep, false);
    }

    public Floor waterCheckShallow (Block floor, boolean deep){
        Block out = waterCheck(floor, deep);
        if(out == deepwater) out = Blocks.water;
        else if(out == algaeWaterDeep) out = algaeWater;

        return out.asFloor();
    }

    public Floor waterCheck(Block floor, boolean deep, boolean requireWater){
        Block out;
        if(Seq.with(dirt, hardenMud, frozenMud, mossyDirt, mud).contains(floor)) out = (deep ? deepwater : requireWater ? Blocks.water : mud);
        else if (mossGreenAll.contains(floor) || floor == algaeWater) out = (deep ? algaeWaterDeep : algaeWater);
        else if (mossYellow.contains(floor) || floor == yellowMossyWater) out = (deep ? deepwater : yellowMossyWater);
        else if (Seq.with(redSand, redSandWater, redSandSnow).contains(floor)) out = (deep ? deepwater : redSandWater);
        else if (floor.asFloor().isLiquid) out = deep ? deepwater : floor;
        else out = (deep ? Blocks.deepwater : Blocks.water);
        return out.asFloor();
    }

    public Floor solidCheck(Block floor){
        Floor out = floor.asFloor();
        if(floor == algaeWater || floor == algaeWaterDeep) out = mossGreen.random(rand).asFloor();
        else if(floor == yellowMossyWater) out = mossYellow.random(rand).asFloor();
        else if(floor == redSandWater) out = redSand.asFloor();
        else if(floor == darksandWater) out = darksand.asFloor();
        else if(floor.asFloor().isLiquid) out = mud.asFloor();
        return out;
    }

    @Override
    public void postGenerate(Tiles tiles){
        if(sector.hasEnemyBase()){
            basegen.postGenerate();
        }
    }

    public void blendf(Block floor, Block around, float radius, Seq<Block> filter, boolean whitelist) {
        float r2 = radius * radius;
        int cap = Mathf.ceil(radius);
        int max = this.tiles.width * this.tiles.height;
        Floor dest = around.asFloor();

        for(int i = 0; i < max; ++i) {
            Tile tile = this.tiles.geti(i);
            if (tile.floor() == floor || tile.block() == floor) {
                for(int cx = -cap; cx <= cap; ++cx) {
                    for(int cy = -cap; cy <= cap; ++cy) {
                        if ((float)(cx * cx + cy * cy) <= r2) {
                            Tile other = this.tiles.get(tile.x + cx, tile.y + cy);
                            if (other != null && other.floor() != floor) {
                                if(whitelist && filter.contains(other.floor()) || !whitelist && !filter.contains(other.floor())){
                                    other.setFloor(dest);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void blendOreIsland( float radius, boolean solid) {
        float r2 = radius * radius;
        int cap = Mathf.ceil(radius);
        int max = this.tiles.width * this.tiles.height;

        for(int i = 0; i < max; ++i) {
            Tile tile = this.tiles.geti(i);;
            if (tile.overlay().itemDrop != null) {
                for(int cx = -cap; cx <= cap; ++cx) {
                    for(int cy = -cap; cy <= cap; ++cy) {
                        if ((float)(cx * cx + cy * cy) <= r2) {
                            Tile other = this.tiles.get(tile.x + cx, tile.y + cy);
                            if (other != null && other.overlay().itemDrop == null) {
                                other.setFloor( solid ? solidCheck(tile.floor()) : waterCheck(tile.floor(), false));
                            }
                        }
                    }
                }
            }
        }
    }

}
