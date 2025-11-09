package olupis.world.blocks.defence;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.Units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.meta.*;
import olupis.world.entities.units.*;

import static mindustry.Vars.*;

public class Ladar extends Radar {
    public @Nullable UnitType type = null;

    public  boolean spotlight = false;
    public float spotRadius  = 100f, spotRange = fogRadius * 3, minProgress = 0.7f, spottedDuration = 4f, minEffReveal = 0.7f, noBuildDecayMul = 2f;
    public int decayDelay = -1, minHealth = 1;
    public StatusEffect spotted = StatusEffects.none;
    public Ladar(String name){
        super(name);
    }

    public void setBars() {
        super.setBars();
        addBar("bar.progress", (RadarBuild entity) -> new Bar("bar.loadprogress", Pal.ammo, () -> entity.progress));
        if(decayDelay > 0)addBar("bar.nyf-decay", (LadarBuild entity) -> new Bar("objective.timer.name", Color.scarlet, entity::decay));
    }


    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.shootRange, spotRange / tilesize, StatUnit.blocks);
        stats.add(Stat.range, (float)fogRadius / tilesize, StatUnit.blocks);
    }


    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, spotRange * tilesize, Color.lightGray);
    }

    public class LadarBuild extends RadarBuild{
        public @Nullable Vec2 tar = new Vec2();
        public int readUnitId = -1, decayTimer, maxHp;
        public @Nullable Unit slave;

        @Override
        public void updateTile(){
            Unit uf = bestEnemyFog(team, this.x, this.y, spotRange * tilesize, u -> !u.dead, UnitSorts.strongest);
            if(uf != null){
                tar.set(uf.x, uf.y);
                if(spotted != StatusEffects.none) uf.apply(spotted, spottedDuration);
            }else tar.set(-1, -1);

            if(type != null){
                //unit was lost/destroyed
                if(slave != null && (slave.dead || !slave.isAdded())) slave = null;

                if(readUnitId != -1){
                    slave = Groups.unit.getByID(readUnitId);
                    if(slave != null || !net.client()) readUnitId = -1;
                }

                if(slave == null && Units.canCreate(team, type) && efficiency > minEffReveal){
                    if(!net.client()){
                        slave = type.create(team);
                        slave.set(x, y);
                        slave.rotation = 90f;
                        slave.add();
                        readUnitId = slave.id;
                    }
                }

                if(slave != null){
                    if(efficiency <= minEffReveal) slave.kill();
                    else if(tar != null){
                        if(tar.x != -1 && tar.y != -1) slave.set(tar);
                        else if(tar.x == -1 && tar.y == -1) slave.set(x, y);
                    }
                }

            }

            if(decayDelay > 0) {
                if(decayTimer  <= 0 ) kill();
                float mul = Build.validPlace(block, team, tile.x,  tile.y, 0, false, true) ? noBuildDecayMul : 1;
                decayTimer -= Math.round(mul);
                maxHp = Math.round(Mathf.lerp(block.health, minHealth, 1- decay()));
                if(health > maxHp) health = maxHp;
                maxHealth = maxHp;
            }
            super.updateTile(); //cant be bothered
        }

        public float decay(){
            return  ((float) decayTimer / decayDelay);
        }

        @Override
        public float maxHealth(){
            return maxHp;
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation){
            decayTimer = decayDelay;
            return super.init(tile, team, shouldAdd, rotation);

        }
        @Override
        public void drawLight(){
            if(emitLight) Drawf.light(x, y, Mathf.lerp(0, lightRadius, progress), lightColor, lightColor.a);
            if(spotlight && tar != null && tar.x != -1 && tar.y != -1 && progress >= minProgress){
                Drawf.light(tar.x, tar.y, Mathf.lerp(0, spotRadius, progress), team.color, 0.8f);
                super.drawLight();
            }
        }

        @Override
        public void drawSelect(){
            if(efficiency > minEffReveal)Drawf.dashCircle(x, y, spotRange * tilesize, Color.lightGray);
            if(tar != null && tar.x != -1 && tar.y != -1) Drawf.square(tar.x, tar.y, 3, 45,team.color);
            super.drawSelect();
        }

        public byte version() {
            return 2;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if(revision >= 1)readUnitId = read.i();
            if(revision >= 2)decayTimer = read.i();
        }

        @Override
        public void onRemoved(){
            if(slave != null) AmmoLifeTimeUnitType.callTimeOut(slave);
            super.onRemoved();
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(slave == null ? -1 : slave.id);
            write.i(decayDelay > 0 ? decayTimer : -1);
        }
    }


    //Yes all this just to remove `inFogTo()`
    public static Unit bestEnemyFog(Team team, float x, float y, float range, Boolf<Unit> predicate, Sortf sort){
        if(team == Team.derelict) return null;

        Unit[] result = {null};
        float[] in = {0f, -99999f};

        Units.nearbyEnemies(team, x - range, y - range, range*2f, range*2f, e -> {
            if(e.dead() || !predicate.get(e) || e.team == Team.derelict || !e.within(x, y, range + e.hitSize/2f) || !e.targetable(team)) return;

            float cost = sort.cost(e, x, y);
            if((result == null || cost < in[0] || e.type.targetPriority > in[1]) && e.type.targetPriority >= in[1]){
                result[0] = e;
                in[0] = cost;
                in[1] = e.type.targetPriority;
            }
        });

        return result[0];
    }
}
