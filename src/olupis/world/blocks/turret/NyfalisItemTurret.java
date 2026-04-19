package olupis.world.blocks.turret;

import arc.math.*;
import arc.scene.ui.layout.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;
import olupis.world.*;
import olupis.world.entities.*;

public class NyfalisItemTurret extends ItemTurret {
    public boolean statsBlocksOnly = false, removeItemsBars = true;
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

    @Override
    public void setBars(){
        super.setBars();
        if(removeItemsBars)removeBar("items");
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
    }

}
