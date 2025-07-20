package olupis.world.blocks.turret;

import arc.math.Mathf;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.meta.*;
import olupis.world.*;
import olupis.world.entities.*;

public class NyfalisLiquidTurret  extends LiquidTurret {
    public float illuminateTime = 30f;
    public boolean angleCheck = false;

    public NyfalisLiquidTurret(String name){
        super(name);
    }

    /** Limits bullet range to this turret's range value. */
    public void limitRange(float margin){
        for(var entry : ammoTypes.entries()){
            limitRange(entry.value, margin);
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.ammo);
        stats.add(Stat.ammo, NyfalisStats.ammoWithInfo(ammoTypes, this));
    }

    public class NyfalisLiquidTurretBuild extends LiquidTurretBuild{
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
                return Units.bestEnemy(team, x, y, range, e -> !e.dead() && !e.isGrounded() &&  unitFilter.get(e) && !NyfWorldFuckingHelper.rayCheck(this, e, b -> b.solid), unitSort);
            }else{
                var ammo = peekAmmo();
                boolean buildings = targetGround && targetBlocks && (ammo == null || ammo.targetBlocks), missiles = ammo == null || ammo.targetMissiles;
                return Units.bestTarget(team, x, y, range,
                e -> !NyfWorldFuckingHelper.rayCheck(this, e, b -> b.solid) && !e.dead() && unitFilter.get(e) && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround) && (missiles || !(e instanceof TimedKillc)),
                b -> buildings && buildingFilter.get(b), unitSort);
            }
        }

    }

}
