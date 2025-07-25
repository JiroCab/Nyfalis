package olupis.world.blocks.turret;

import arc.math.*;
import mindustry.entities.bullet.*;
import mindustry.world.*;

import static mindustry.Vars.*;
import static olupis.world.EnvUpdater.*;

public class NyfalisPowerCutter extends NyfalisPowerTurret{
    //Snip size should be 1 unless you can figure out cutting for tiles w/ even blocks
    public int snipSize = 0, snipRand = 2;

    public NyfalisPowerCutter(String name){
        super(name);
        drawMinRange = true;
    }


    public class NyfalisPowerCutterBuild extends NyfalisPowerTurretBuild{

        @Override
        public void updateTile(){
            super.updateTile();

            if(target == null){
                Tile t = getInfested(x, y, range, minRange);
                if(t == null) return;

                targetPos.set(t);
                float targetRot = angleTo(t);

                if(shouldTurn() && !isControlled() && !logicControlled()){
                    turnToTarget(targetRot);
                }

                if(Angles.angleDist(rotation, targetRot) < Math.max(shootCone /2, 4f)){
                    wasShooting = true;
                    updateReload();
                    updateShooting();
                }
            }
        }

        @Override
        protected void shoot(BulletType type){
            Tile t = world.tileWorld(targetPos.x, targetPos.y);
            if(t != null){
                resetTile(t);
                target = null;
            }

            super.shoot(type);
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
        }

        @Override
        public boolean isActive(){
            return (target != null || wasShooting || targetPos != null) && enabled;
        }

        @Override
        public boolean isShooting(){
            return alwaysShooting || (isControlled() ? unit.isShooting() : logicControlled() ? logicShooting : target != null) || (targetPos != null && wasShooting);
        }
    }
}
