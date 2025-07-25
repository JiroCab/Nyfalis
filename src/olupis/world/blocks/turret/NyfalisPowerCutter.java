package olupis.world.blocks.turret;

import arc.math.*;
import arc.util.*;
import mindustry.*;
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
                Tile t = closestInfes();
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

        //Debuging
        public @Nullable Tile closestInfes(){
            return closestInfested(tileX(), tileY(), (int) range / tilesize, world.width(), world.height(), (int)(minRange / tilesize));
        }

        @Override
        protected void shoot(BulletType type){
            Tile t = Vars.world.tiles.getc(Math.round((targetPos.x + snipSize) /8), Math.round(targetPos.y /8));
            if(t != null && t.within(this, Mathf.round(range + 8, tilesize)) && !t.within(this, Mathf.round(minRange + 8, tilesize))){
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
