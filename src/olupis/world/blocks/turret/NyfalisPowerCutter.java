package olupis.world.blocks.turret;

import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.bullet.*;
import mindustry.world.*;
import olupis.world.*;

public class NyfalisPowerCutter extends NyfalisPowerTurret{
    public int snipSize = 0;

    public NyfalisPowerCutter(String name){
        super(name);
    }


    public class NyfalisPowerCutterBuild extends NyfalisPowerTurretBuild{

        @Override
        public void updateTile(){
            super.updateTile();

            if(target == null){
                Tile t = EnvUpdater.closestSpread(x, y, range /8);
                if(t == null) return;

                targetPos.set(t);
                float targetRot = angleTo(t);

                Log.err(range +  " "  + t.x + " "+ t.y);

                if(shouldTurn() && !isControlled() && !logicControlled()){
                    turnToTarget(targetRot);
                }

                if(Angles.angleDist(rotation, targetRot) < shootCone){
                    wasShooting = true;
                    updateReload();
                    updateShooting();
                }
            }
        }

        @Override
        protected void shoot(BulletType type){
            Tile t = Vars.world.tile(Math.round(targetPos.x /8) , Math.round(targetPos.y /8));
            if(t != null && t.within(this, range + 8)){
                EnvUpdater.restoreTile(t, snipSize);
            }

            super.shoot(type);
        }

        @Override
        public boolean isActive(){
            return (target != null || wasShooting || targetPos != null) && enabled;
        }

        @Override
        public boolean isShooting(){
            return alwaysShooting || (isControlled() ? unit.isShooting() : logicControlled() ? logicShooting : target != null) || targetPos != null;
        }
    }




}
