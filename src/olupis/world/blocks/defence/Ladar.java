package olupis.world.blocks.defence;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.meta.*;
import olupis.world.*;

import static mindustry.Vars.*;
import static olupis.world.NyfWorldFuckingHelper.bestEnemyFog;

public class Ladar extends Radar {
    public @Nullable UnitType type = null;

    public  boolean spotlight = false;
    public float
        /*How Big Light is made on the unit*/
        spotRadius  = 100f,
        /*Distance how far the tracking of a unit is*/
        spotRange = fogRadius * 3,
        /*Fog reveal progress required to spot units*/
        minProgress = 0.7f,
        /*Duration of status effect applied on a spotted unit*/
        spottedDuration = 4f,
        /*Minimum efficiency needed to start spotting*/
        minEffReveal = 0.7f,
        /*How long till we start decaing*/
        noBuildDecayMul = 2f

    ;
    public int
        decayDelay = -1,
        minHealth = 1,
        /*Time before we pick a new target*/
        spotRetargeting = Math.round(Time.toSeconds * 2.5f)
    ;
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

    public class LadarBuild extends RadarBuild implements Ranged{
        public @Nullable Posc target;
        public int readUnitId = -1, decayTimer, maxHp, retarget = 0;
        public @Nullable Unit slave;

        @Override
        public void updateTile(){
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
                    //note: ladar helper also tp's to the target to reduce stuttering
                    else if(target != null) slave.set(target);
                    else slave.set(x,y);
                }
            }

            retarget--;
            if(target == null || retarget <= 0){
                target = bestEnemyFog(team, this.x, this.y, spotRange * tilesize, u -> !u.dead && (!u.hasEffect(spotted) || u.getDuration(spotted) <= spottedDuration * 0.5f), UnitSorts.strongest);
                retarget = spotRetargeting;
            }

            if(spotted != StatusEffects.none && target != null && target instanceof Statusc st) st.apply(spotted, spottedDuration);


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
            if(spotlight && target != null && progress >= minProgress){
                Drawf.light(target.x(), target.y(), Mathf.lerp(0, spotRadius, progress), team.color, 0.8f);
                super.drawLight();
            }
        }

        @Override
        public void drawSelect(){
            if(efficiency > minEffReveal)Drawf.dashCircle(x, y, spotRange * tilesize, Color.lightGray);
            if(target != null) Drawf.square(target.x(), target.y(), 3, 45,team.color);
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
            if(slave != null) NyfWorldFuckingHelper.callTimeOut(slave);
            super.onRemoved();
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(slave == null ? -1 : slave.id);
            write.i(decayDelay > 0 ? decayTimer : -1);
        }

        @Override
        public float range(){
            return spotRange;
        }


    }
}
