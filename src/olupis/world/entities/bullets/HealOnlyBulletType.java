package olupis.world.entities.bullets;

import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.world.blocks.*;

public class HealOnlyBulletType extends BasicBulletType {
    public boolean fogVisible = true, alwaysSplashDamage = false, despawnHitEffect = false;

    public HealOnlyBulletType(float speed, float damage, String bulletSprite){
        super(speed, damage);
        this.sprite = bulletSprite;
        this.collidesAir = this.hittable = false;
        this.collidesTeam = true;

    }

    public HealOnlyBulletType(float speed, float damage, String bulletSprite, Boolean fogVisible){
        this(speed, damage, bulletSprite);
        this.fogVisible = fogVisible;
    }

    public HealOnlyBulletType(float speed, float damage){
        this(speed, damage, "bullet");
    }

    @Override
    public void update(Bullet b){;
        float[] hitsize = {b.hitSize};
        Teamc tar = findTarget(b, hitsize);

        updateCollision(b, tar, hitsize[0]);
        updateTrail(b);
        updateHoming(b, tar);
        updateWeaving(b);
        updateTrailEffects(b);
        updateBulletInterval(b);
    }

    public void updateHoming(Bullet b, Teamc target){
        if(homingPower > 0.0001f && b.time >= homingDelay && target != null) {
            b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta * 50f));
        }
    }

    /*Overcomplicated way to say: Hit only Blocks but not units*/
    public void updateCollision(Bullet b, Teamc target, float tarSize){
        /*If someone finds a better way to do this, please let us know -RushieWsahie*/
        if (!heals())return;
        /*heals only the target, passing over any damaged blocks is ignored, debating if this a feature or bug*/
        float splash = Math.max(b.type.splashDamageRadius * 0.8f, 1f);
        if(target == null) this.collides = this.collidesGround = false;
        else this.collides  = this.collidesGround = target.within(b.x(), b.y(), Math.max(tarSize -3.5f, splash));
    }

    public Teamc findTarget(Bullet b, float[] hitsize){
        if(!heals()) return null;
        float realAimX = b.aimX < 0 ? b.x : b.aimX,
                realAimY = b.aimY < 0 ? b.y : b.aimY;
        //Only Home on allies
        return Units.closestTarget(null, realAimX, realAimY, homingRange,
            e -> false /*don't*/,
            t ->{hitsize[0] = t.hitSize();
                return (t.team == b.team && t.damaged()) && !b.hasCollided(t.id);
            }
        );
    }

    /*Don't like how gnats/phorids interval bullets are a dead give away in fog, so only the diamond will be visible*/
    @Override
    public void draw(Bullet b){
        if(checkFog(b)) super.draw(b);
    }

    @Override
    public void despawned(Bullet b){
        if(despawnHit) hit(b);
        else createUnits(b, b.x, b.y);

        if(!fragOnHit) createFrags(b, b.x, b.y);

        if(!checkFog(b)) return;

        despawnEffect.at(b.x, b.y, b.rotation(), hitColor);
        despawnSound.at(b);

        Effect.shake(despawnShake, despawnShake, b);
    }

    @Override
    public void drawTrail(Bullet b){
        if(checkFog(b)) super.drawTrail(b);
    }

    @Override
    public void removed(Bullet b){
        if(checkFog(b)) super.removed(b);
    }

    @Override
    /** If direct is false, this is an indirect hit and the tile was already damaged.
     * TODO this is a mess. */
    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct){
        if(makeFire && build.team != b.team){
            Fires.create(build.tile);
        }

        if(heals() && build.team == b.team && !(build.block instanceof ConstructBlock)){
            healEffect.at(build.x, build.y, 0f, healColor, build.block);
            build.heal(healPercent / 100f * build.maxHealth + healAmount);
            if(alwaysSplashDamage)createSplashDamage(b, x, y);
            if(despawnHitEffect) despawnEffect.at(b.x, b.y, b.rotation(), hitColor);
        }else if(build.team != b.team && direct){
            hit(b);
        }

        handlePierce(b, initialHealth, x, y);
    }

    public boolean checkFog(Bullet b){
        if(!Vars.state.rules.fog) return true;
        if(Vars.headless) return  false; //causes a crash because of the bellow, so this will just be hidden client side
        if(fogVisible) return true;
        return !b.inFogTo(Vars.player.team());
    }
}
