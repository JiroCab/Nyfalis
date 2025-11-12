package olupis.world.ai;

import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.types.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;

public class DumbMissileAI extends MissileAI{
    @Nullable Teamc target = null;
    @Override
    public void updateMovement(){
        unloadPayloads();
        @Nullable  Weapon base = unit.hasWeapons() ?  unit.type.weapons.get(0) : null;

        if(target != null){
            if(base != null && base.bullet.collidesGround){
                float rot = Angles.moveToward(unit.rotation(), unit.angleTo(target),  base.bullet.homingPower) ;

                unit.lookAt(unit.x + Angles.trnsx(rot, 10), unit.y + Angles.trnsy(rot, 10));
            }
        } else if(shooter != null && !shooter.dead() && base != null){
            Tile aimTile = Vars.world.tile((int)(shooter.aimX / 8), (int)(shooter.aimY / 8));
            if(aimTile != null && aimTile.build != null && aimTile.build.team != unit.team && base.bullet.collidesGround){
                target = aimTile.build;
            } else{
                target = Units.closestTarget(unit.team, shooter.aimX, shooter.aimY, base.bullet.homingRange,
                e -> e != null && e.checkTarget(base.bullet.collidesAir, base.bullet.collidesGround),
                t -> t != null && base.bullet.collidesGround);
            }
        }

        float time = unit instanceof TimedKillc t ? t.time() : 1000000f;

        //move forward forever
        unit.moveAt(vec.trns(unit.rotation, unit.type.missileAccelTime <= 0f ? unit.speed() : Mathf.pow(Math.min(time / unit.type.missileAccelTime, 1f), 2f) * unit.speed()));

        var build = unit.buildOn();

        //kill instantly on enemy building contact
        if(build != null && build.team != unit.team && (build == target || !build.block.underBullets)){
            unit.kill();
        }
    }
}
