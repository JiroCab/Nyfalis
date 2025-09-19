package olupis.world.blocks.defence;

import arc.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.meta.*;

public class PoweredLightingWall extends CTWall {
    public float lightningChancePowered = lightningChance;
    public boolean vanillaLightning = true;
    public @Nullable BulletType dischargeBullet = null;


    public PoweredLightingWall(String name) {
        super(name);
        hasPower = true;

    }

    @Override
    public void setStats(){
        super.setStats();
        if(lightningChancePowered > 0f) stats.add(new Stat("olupis-lightningchancepowered"), lightningChancePowered * 100f, StatUnit.percent);
    }

    public class PoweredLightingWallBuild extends CTWallBuild {

        public void handleDmg(){
            if(lightningChance > 0f || lightningChancePowered > 0f){
                float chance = power.status > 0 ? Mathf.lerp(lightningChance, lightningChancePowered, power.status) : lightningChance;
                if(Mathf.chance(chance)){
                    if(power.status >= 1) consume();

                    lightningSound.at(tile, Mathf.random(0.9f, 1.1f));
                    if(vanillaLightning){
                        Lightning.create(team, lightningColor, lightningDamage, x, y, Mathf.random(360f), lightningLength);
                    }
                    if(dischargeBullet != null){
                        dischargeBullet.create(this, team, x, y, 0, 1, 1);
                        dischargeBullet.shootEffect.at(x, y, lightColor);
                    }
                }
            }
        }

        @Override
        public void damage(Team source, float damage) {
            //create lightning if necessary, for direct damage. ex: Abilities from aegires & germanica
            handleDmg();

            super.damage(source, damage);
        }

        @Override
        public boolean collision(Bullet bullet){
            boolean wasDead = this.health <= 0.0F;
            float damage = bullet.damage() * bullet.type().buildingDamageMultiplier;
            if (!bullet.type.pierceArmor) damage = Damage.applyArmor(damage, this.block.armor);

            super.damage(bullet.team, damage); //don't call the custom damage, so we don't double lighting
            Events.fire(bulletDamageEvent.set(this, bullet));
            if (this.health <= 0.0F && !wasDead) {
                Events.fire(new EventType.BuildingBulletDestroyEvent(this, bullet));
            }

            hit = 1f;

            //create lightning if necessary
            handleDmg();

            //deflect bullets if necessary
            if(chanceDeflect > 0f){
                //slow bullets are not deflected
                if(bullet.vel.len() <= 0.1f || !bullet.type.reflectable) return true;

                //bullet reflection chance depends on bullet damage
                if(!Mathf.chance(chanceDeflect / bullet.damage())) return true;

                //make sound
                deflectSound.at(tile, Mathf.random(0.9f, 1.1f));

                //translate bullet back to where it was upon collision
                bullet.trns(-bullet.vel.x, -bullet.vel.y);

                float penX = Math.abs(x - bullet.x), penY = Math.abs(y - bullet.y);

                if(penX > penY){
                    bullet.vel.x *= -1;
                }else{
                    bullet.vel.y *= -1;
                }

                bullet.owner = this;
                bullet.team = team;
                bullet.time += 1f;

                //disable bullet collision by returning false
                return false;
            }

            return true;
        }
    }
}
