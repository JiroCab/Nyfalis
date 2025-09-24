package olupis.world.entities.bullets;

import arc.*;
import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class EffectivenessMissleType extends MissileBulletType {
    static final EventType.UnitDamageEvent bulletDamageEvent = new EventType.UnitDamageEvent();
    public float groundDamageMultiplier = 1f;
    public float groundDamageSplashMultiplier = 1f;
    public boolean flatDamage = false, homingExtends = true, maxRangeLifeScale;
    public float homingExtendedRange = 3f * tilesize;

    private static final Rect rect = new Rect();

    public EffectivenessMissleType(float speed, float damge, String bulletSprite){
        super(speed, damge, bulletSprite);
    }

    public EffectivenessMissleType(float speed, float damge){
        super(speed, damge);
    }

    public EffectivenessMissleType(){
        super();
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        if(homingExtends){
            Teamc target;
            if(b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)){
                target = b.aimTile.build;
            }else{
                target = Units.closestTarget(b.team, b.aimX, b.aimY, homingRange,
                e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                t -> t != null && collidesGround && !b.hasCollided(t.id));
            }
            if(target != null && homingExtendedRange > 0 && target.within(b, homingExtendedRange)){
                b.lifetime += 1;
            }
        }
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health){
        boolean wasDead = entity instanceof Unit u && u.dead;

        if(entity instanceof Healthc h){
            float dmg = entity instanceof  Unit u && u.isGrounded() ? flatDamage ? groundDamageMultiplier :   groundDamageMultiplier * damage : damage;

            if(pierceArmor){
                h.damagePierce(dmg);
            }else{
                h.damage(dmg);
            }
        }

        if(entity instanceof Unit unit){
            Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f);
            if(impact) Tmp.v3.setAngle(b.rotation() + (knockback < 0 ? 180f : 0f));
            unit.impulse(Tmp.v3);
            unit.apply(status, statusDuration);

            Events.fire(bulletDamageEvent.set(unit, b));
        }

        if(!wasDead && entity instanceof Unit unit && unit.dead){
            Events.fire(new EventType.UnitBulletDestroyEvent(unit, b));
        }

        handlePierce(b, health, entity.x(), entity.y());
    }

    @Override
    public void createSplashDamage(Bullet b, float x, float y){
        //All this so splash damge's mul is also applied
        //Damage.damage(b.team, x, y, splashDamageRadius, splashDamage * b.damageMultiplier(), splashDamagePierce, collidesAir, collidesGround, scaledSplashDamage, b);
        if(splashDamageRadius > 0 && !b.absorbed){
            Cons<Unit> cons = unit -> {
                if(unit.team == b.team  || !unit.checkTarget(collidesAir, collidesGround) || !unit.hittable() || !unit.within(x, y, splashDamageRadius + (scaledSplashDamage ? unit.hitSize / 2f : 0f))){
                    return;
                }

                boolean dead = unit.dead, grounded = unit.isGrounded() && collidesGround;

                float amount = calculateDamage(scaledSplashDamage ? Math.max(0, unit.dst(x, y) - unit.type.hitSize/2) : unit.dst(x, y), splashDamageRadius, splashDamage * (grounded  ? groundDamageSplashMultiplier : 1));
                unit.damage(amount);

                if(b != null){
                    Events.fire(bulletDamageEvent.set(unit, b));
                    unit.controller().hit(b);

                    if(!dead && unit.dead){
                        Events.fire(new EventType.UnitBulletDestroyEvent(unit, b));
                    }
                }
                //TODO better velocity displacement
                float dst =  Tmp.v1.set(unit.x - x, unit.y - y).len();
                unit.vel.add( Tmp.v1.setLength((1f - dst / splashDamageRadius) * 2f / unit.mass()));

                if(splashDamagePierce && splashDamage >= 9999999f && unit.isPlayer()){
                    Events.fire(EventType.Trigger.exclusionDeath);
                }
            };

            rect.setSize(splashDamageRadius * 2).setCenter(x, y);
            if(b.team != null){
                Units.nearbyEnemies(b.team, rect, cons);
            }else{
                Units.nearby(rect, cons);
            }

            if(collidesGround){
                if(!splashDamagePierce){
                    Damage.tileDamage(b.team, World.toTile(x), World.toTile(y), splashDamageRadius / tilesize, damage * (b == null ? 1f : b.type.buildingDamageMultiplier), b);
                }else{
                    completeDamage(b.team, x, y, splashDamageRadius, damage);
                }
            }

            if(status != StatusEffects.none){
                Damage.status(b.team, x, y, splashDamageRadius, status, statusDuration, collidesAir, collidesGround);
            }

            if(heals()){
                indexer.eachBlock(b.team, x, y, splashDamageRadius, Building::damaged, other -> {
                    healEffect.at(other.x, other.y, 0f, healColor, other.block);
                    other.heal(healPercent / 100f * other.maxHealth() + healAmount);
                });
            }

            if(makeFire){
                indexer.eachBlock(null, x, y, splashDamageRadius, other -> other.team != b.team, other -> Fires.create(other.tile));
            }
        }
    }

    private static float calculateDamage(float dist, float radius, float damage){
        float falloff = 0.4f;
        float scaled = Mathf.lerp(1f - dist / radius, 1f, falloff);
        return damage * scaled;
    }

    private static void completeDamage(Team team, float x, float y, float radius, float damage){

        int trad = (int)(radius / tilesize);
        for(int dx = -trad; dx <= trad; dx++){
            for(int dy = -trad; dy <= trad; dy++){
                Tile tile = world.tile(Math.round(x / tilesize) + dx, Math.round(y / tilesize) + dy);
                if(tile != null && tile.build != null && (team == null || team != tile.team()) && dx*dx + dy*dy <= trad*trad){
                    tile.build.damage(team, damage);
                }
            }
        }
    }
}
