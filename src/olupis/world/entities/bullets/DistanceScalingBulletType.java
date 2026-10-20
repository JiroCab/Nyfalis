package olupis.world.entities.bullets;

import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class DistanceScalingBulletType extends ExplosionBulletType{
    //static final UnitDamageEvent bulletDamageEvent = new UnitDamageEvent();

    public float
    /* % min/max dmg depending on range*/
    minDmgMul = 0.25f, maxDmgMul = 1,
    /* clamps for dist */
    maxDst = Float.MAX_VALUE, minDst = Vars.tilesize * 2;

    /* Extra hit effect that has data for  scaling */
    public Effect hitScaledEffect = Fx.none;
    public Effect despawnScaledEffect = Fx.none;



    public DistanceScalingBulletType(float splashDamage, float splashDamageRadius){
        this.splashDamage = splashDamage;
        this.splashDamageRadius = splashDamageRadius;
        rangeOverride = Math.max(rangeOverride, splashDamageRadius * 2f / 3f);
    }

    public DistanceScalingBulletType(){
        super();
    }


    @Override
    public float damageMultiplier(Bullet b){
        float distMul = Mathf.lerp(minDmgMul, maxDmgMul, (getDistMul(b)));


        if(b.owner instanceof Unit u) return u.damageMultiplier() * state.rules.unitDamage(b.team) * distMul;
        if(b.owner instanceof Building) return state.rules.blockDamage(b.team) * distMul;

        return 1f;
    }

    @Override
    public void hit(Bullet b, float x, float y, boolean createFrags){
        hitScaledEffect.at(x, y, b.rotation(), hitColor, getDistMul(b));

        super.hit(b, x,y, createFrags);
    }

    @Override
    public void despawned(Bullet b){
        despawnScaledEffect.at(b.x, b.y, b.rotation(), hitColor, getDistMul(b));

        super.despawned(b);
    }

    public float getDistMul(Bullet b){
        if(b.owner instanceof  Posc p ){
            float dst = Mathf.clamp(b.dst(p), minDst, maxDst) - minDst;
            if(maxDst != Float.MAX_VALUE) dst /= maxDst;
            return dst;
        }
        return minDmgMul;
    }
}
