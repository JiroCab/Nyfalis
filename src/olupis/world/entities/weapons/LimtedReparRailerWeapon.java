package olupis.world.entities.weapons;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import olupis.content.*;

public class LimtedReparRailerWeapon extends NyfalisWeapon{

    public  LimtedReparRailerWeapon(String name){
        super(name);
    }
    public LimtedReparRailerWeapon(){
        super();
    }
    public boolean targetBuildings = true, targetUnits = true;

    @Override
    public void update(Unit unit, WeaponMount mount){
        super.update(unit, mount);
    }

    public void rail(Unit unit, WeaponMount mount){
        if(!targetBuildings && mount.target instanceof Building) return;
        if(!targetUnits && mount.target instanceof Unit) return;

        if(mount.target instanceof Healthc target){
            target.heal((bullet.healPercent / 100 * unit.maxHealth) + bullet.healAmount);

            mount.heat = 1f;

            float
                xf = mountX(unit),
                yf = mountY(unit);
            boolean onTop = !target.within(unit.x, unit.y, unit.hitSize);

            //this is prob not set up properlly, let future rushie or someone else figure it out 3:
            if(onTop) bullet.shootEffect.at(xf, yf, mount.rotation - 90, unit.team.color);
            else bullet.shootEffect.at(target.x(), target.y(), mount.rotation - 90, unit.team.color);

            if(bullet.smokeEffect != Fx.none && onTop) bullet.smokeEffect.at(xf, yf, Angles.angle(xf, yf, target.x(), target.y()), unit.team.color, new Vec2().set(target));
            if(bullet.hitEffect != Fx.none) bullet.hitEffect.at(target.x(), target.y(), Angles.angle(xf, yf, target.x(), target.y()), unit.team.color);

            if(bullet.status != StatusEffects.none && target instanceof Statusc sc ) sc.apply(bullet.status, bullet.statusDuration);
        }

    }

    @Override
    protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground){
        var out = targetUnits ? Units.closest(unit.team, x, y, range, u -> u != unit && u.damaged()) :  null;
        if(out != null || !targetBuildings) return out;
        return Units.findAllyTile(unit.team, x, y, range, Building::damaged);
    }

    @Override
    protected boolean checkTarget(Unit unit, Teamc target, float x, float y, float range){
        return !(target.within(unit, range + unit.hitSize/2f) && target.team() == unit.team && target instanceof Healthc u && u.damaged() && u.isValid());
    }

    @Override
    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation){
        //does nothing, shooting is handled in update()

        if(mount.target instanceof Healthc unt){
            float angle = Angles.angle(unit.x + x, unit.y + y, unt.x(), unt.y());

            boolean inRange = Angles.within(rotate ? mount.rotation : unit.rotation + baseRotation, angle, shootCone);
            if(inRange ){
                rail(unit, mount);
            }
            mount.rotation = Mathf.slerpDelta(mount.rotation, angle, 0.5f);
        }

    }

}
