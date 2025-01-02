package olupis.world.blocks.defence;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.Damage;
import mindustry.entities.Lightning;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;

import java.util.HashMap;

public class CorrodingWall extends Wall {

    HashMap<Building, Float> currentCorrosion = new HashMap<>();
    public TextureRegion corrosion;
    public float damageMultiplier = 1.25f, timeToFullCorro = 400;

    public CorrodingWall(String name) {
        super(name);
    }

    public class CorrodingWallBuild extends WallBuild {
        float counter, corrodeRate;

        @Override
        public void updateTile(){
            super.updateTile();
            if(!currentCorrosion.containsKey(this)){
                currentCorrosion.put(this,0f);
            }
            if(currentCorrosion.get(this) < 100){
                counter += edelta();
                corrodeRate = timeToFullCorro / 100;
                if (counter >= corrodeRate){
                    counter = 0;
                    currentCorrosion.replace(this,currentCorrosion.get(this)+1);
                }
            }


        }
        @Override
        public boolean collision(Bullet bullet){
            float weaknessMaths = (damageMultiplier - 1) / 100;
            float weakness = (weaknessMaths * currentCorrosion.get(this)) + 1;
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
            if (CorrodingWall.this.lightningChance > 0.0F && Mathf.chance((double)CorrodingWall.this.lightningChance)) {
                Lightning.create(this.team, CorrodingWall.this.lightningColor, CorrodingWall.this.lightningDamage, this.x, this.y, bullet.rotation() + 180.0F, CorrodingWall.this.lightningLength);
                CorrodingWall.this.lightningSound.at(this.tile, Mathf.random(0.9F, 1.1F));
            }

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

        @Override
        public void draw() {
            Draw.rect(block.region, x, y, drawrot());
            if(currentCorrosion.get(this) != null) {
                Drawf.additive(corrosion, Color.white.write(Tmp.c1).a(currentCorrosion.get(this) / 100), x, y, drawrot(), 50.1F);
            }else{
                Drawf.additive(corrosion, Color.white.write(Tmp.c1).a(0), x, y, drawrot(), 50.1F);
            }
        }

        public void load(Block block){
            corrosion = Core.atlas.find(block.name + "-corrosion");
        }

    }
}