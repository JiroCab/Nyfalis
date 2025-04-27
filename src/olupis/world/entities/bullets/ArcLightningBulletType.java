package olupis.world.entities.bullets;

import arc.struct.*;
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
                            failLightnighBullet =  false
    ;
    public Effect chainEffect = NyfalisFxs.chainLightningAlt;

    public ArcLightningBulletType(){
        damage = 1f;
        speed = 0f;
        lifetime = 1;
        despawnEffect = Fx.none;
        hitEffect = Fx.hitLancer;
        keepVelocity = false;
        hittable = false;
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
            if(other.checkTarget(hitAir, hitGround) && other.targetable(b.team) && (other.team !=b.team)){
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
            if(failLightnighBullet){
                lightningType.create(b.owner, b.team, b.x, b.y, 0);
            }
            return;
        };

        for(int i = 0; i < maxTargets; i++){
            Healthc tar = all.random();
            if(tar == null) break;

            tar.damage(damage);
            if(tar instanceof Statusc s)s.apply(status, statusDuration);
            chainEffect.at(b.x , b.y, b.rotation(), lightningColor, tar);
            all.remove(tar);
        }
    }
}

