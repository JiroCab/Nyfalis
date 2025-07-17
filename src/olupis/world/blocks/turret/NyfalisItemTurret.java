package olupis.world.blocks.turret;

import arc.math.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;
import olupis.world.entities.*;

public class NyfalisItemTurret extends ItemTurret {
    public boolean statsBlocksOnly = false, angleCheck = false;
    public float illuminateTime = 30f;

    public  NyfalisItemTurret(String name){
        super(name);
    }

    public void limitRangeI(float margin){
        for(var entry : ammoTypes.entries()){
            limitRange(entry.value, margin);
            if(entry.value.intervalBullet != null)limitRange(entry.value.intervalBullet, margin);
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.ammo);
        if(statsBlocksOnly) stats.add(Stat.ammo, NyfalisStats.ammoBlocksOnly(ammoTypes, this));
        else stats.add(Stat.ammo, NyfalisStats.ammoWithInfo(ammoTypes, this));
    }

    public class NyfalisItemTurretBuild extends ItemTurretBuild{
        public float progressLight;

        @Override
        public void drawLight() {
            boolean check = (!hasPower || power.status >= 0.5f) && (hasAmmo());
            if(emitLight){
                progressLight = Mathf.lerpDelta(progressLight, check ? lightRadius : 0, this.delta() / illuminateTime);
                if(progressLight >= 0)Drawf.light(x, y, progressLight, lightColor, lightColor.a);
            }
            super.drawLight();
        }


        @Override
        protected Posc findEnemy(float range){
            if(!angleCheck) return super.findEnemy(range);

            if(targetAir && !targetGround){
                return Units.bestEnemy(team, x, y, range, e -> !e.dead() && !e.isGrounded() &&  unitFilter.get(e) && rayCheck(e), unitSort);
            }else{
                var ammo = peekAmmo();
                boolean buildings = targetGround && targetBlocks && (ammo == null || ammo.targetBlocks), missiles = ammo == null || ammo.targetMissiles;
                return Units.bestTarget(team, x, y, range,
                e -> rayCheck(e) && !e.dead() && unitFilter.get(e) && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround) && (missiles || !(e instanceof TimedKillc)),
                b -> buildings && buildingFilter.get(b), unitSort);
            }
        }

        public boolean rayCheck(Unit e ){
            //todo
            return true;
        }
    }

}
