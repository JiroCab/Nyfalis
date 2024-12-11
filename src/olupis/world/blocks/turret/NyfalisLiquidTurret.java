package olupis.world.blocks.turret;

import arc.math.Mathf;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.defense.turrets.LiquidTurret;

public class NyfalisLiquidTurret  extends LiquidTurret {
    public float illuminateTime = 30f;

    public NyfalisLiquidTurret(String name){
        super(name);
    }

    /** Limits bullet range to this turret's range value. */
    public void limitRange(float margin){
        for(var entry : ammoTypes.entries()){
            limitRange(entry.value, margin);
        }
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

    }

}
