package olupis.world.entities.bullets;

import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import olupis.content.*;

public class ArcLightningBulletType extends BulletType{
    public int maxTargets = 3;
    public boolean hitAir = true,
                            hitGround = true,
                            hitBuilding = true,
                            failLightningBullet =  false
    ;

    public float minTargetDistance = -1f;
    public Effect chainEffect = NyfalisFxs.shootChainLightning;

    public ArcLightningBulletType(){
        damage = 1f;
        speed = 0f;
        lifetime = 1;
        despawnEffect = Fx.none;
        hitEffect = Fx.hitLancer;
        keepVelocity = false;
        hittable = false;
        fragOnHit = false;
        //for stats
        status = StatusEffects.shocked;
    }

    @Override
    public float estimateDPS(){
        return super.estimateDPS() * maxTargets;
    }

    @Override
    public void draw(Bullet b){
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        Seq<Healthc> out = new Seq<>();
        Units.nearby(null, b.x, b.y, range, other -> {
            //Todo, maybe healing?
            if(other.checkTarget(hitAir, hitGround) && other.targetable(b.team) && (other.team !=b.team) && (minTargetDistance <= -1 || out.allMatch(c -> !c.within(other, minTargetDistance)))){
                out.add(other);
            }
        });

        if(hitBuilding){
            Units.nearbyBuildings(b.x, b.y, range, d -> {
                if((b.team != d.team)){
                    out.add(d);
                }
            });
        }
        handleDamage(out, b);
    }

    public void handleDamage(Seq<Healthc> all, Bullet b){
        if(all.size < 1) {
            if(failLightningBullet){
                lightningType.create(b.owner, b.team, b.x, b.y, 0);
            }
            return;
        };

        if( b.aimX > 0 &&  b.aimY > 0 )all.sort(t -> t.dst(b.aimX, b.aimY));

        for(int i = 0; i < maxTargets; i++){
             @Nullable Healthc tar = all.random();
             //guaranteed to hit aimed target
            if(i == 0 && !all.isEmpty())tar = all.first();

            if(tar == null) continue;

            if(fragBullet != null) createFrags(b, tar.x(), tar.y());
            tar.damage(damage);
            if(tar instanceof Statusc s)s.apply(status, statusDuration);
            chainEffect.at(b.x , b.y, b.rotation(), lightningColor, tar);
            all.remove(tar);
        }
    }


    @Override
    public void despawned(Bullet b){
        if(despawnHit){
            hit(b);
        }else{
            createUnits(b, b.x, b.y);
        }

        despawnEffect.at(b.x, b.y, b.rotation(), hitColor);
        despawnSound.at(b);

        Effect.shake(despawnShake, despawnShake, b);
    }
}

