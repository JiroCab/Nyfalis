package olupis.world.entities.bullets;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.*;

import java.util.*;

public class BarrelBulletType extends RollBulletType{
    public int maxBounces = 10;
    public float max = 10;
    HashMap<Integer, Integer> bounces = new HashMap<>();
    HashMap<Integer, Float> currentHp = new HashMap<>();
    HashMap<Integer, Boolean> justBounced = new HashMap<>();
    public float bounceDelay = 20;
    public boolean bounceOnWalls = true, bounceOnEnemyWalls = false, hitFires = false, hitProjectiles = true;
    public  String teamSprite = "";
    public  String liquidSprite = "";
    public TextureRegion teamRegion;
    public TextureRegion liquidRegion;
    public @Nullable Color liquidColour;

    public BarrelBulletType(float speed, float damage, String bulletSprite){
        super(speed, damage);
        sprite = bulletSprite;
        collidesAir = false;
        collides = collidesGround = collidesTiles = true;
        layer = Layer.legUnit +1f;
        shrinkY = 0.0F;
    }

    public BarrelBulletType(float speed, float damage){
        this(speed, damage, "olupis-barrel");
        backSprite = "olupis-barrel-back";
        teamSprite  = "olupis-barrel-team";
        liquidSprite  = "olupis-barrel-liquid";
    }

    public void update(Bullet b){

        if (!bounces.containsKey(b.id)) bounces.put(b.id, 1);
        if (!justBounced.containsKey(b.id)) justBounced.put(b.id, false);
        if (!currentHp.containsKey(b.id)) currentHp.put(b.id, max);

        if(hitFires && Fires.has(b.tileX(), b.tileY() )){
            b.remove();
        }

        if (bounceOnWalls&&b.time >= bounceDelay && !justBounced.get(b.id)) {
            if(b.x <= -1  || b.y <= -1 || b.x > Vars.world.unitWidth() ||  b.y > Vars.world.unitHeight()) {
                if (bounces.get(b.id) < maxBounces) {
                    b.vel.setAngle( b.rotation() + 180 + Mathf.random(-50,50));
                    bounces.replace(b.id, bounces.get(b.id) + 1);
                    justBounced.replace(b.id, true);
                } else  b.remove();
            }else Units.nearbyBuildings(b.x,b.y,b.hitSize*3,bl -> {
                if(hitFires && Fires.has(bl.tileOn().x, bl.tileOn().y)){
                    b.remove();
                }else if (bl.block.solid) {
                    if (bl.team == b.team) {
                        if (bounces.get(b.id) < maxBounces) {
                            b.vel.setAngle((b.angleTo(bl) + 180) + Mathf.random(-50,50));
                            if (b.type.heals() && !(bl.block instanceof ConstructBlock)) {
                                b.type.healEffect.at(bl.x, bl.y, 0.0F, b.type.healColor, bl.block);
                                bl.heal((b.type.healPercent - b.type.healPercent / (maxBounces * bounces.get(b.id))) / 100.0F * bl.maxHealth + (b.type.healAmount - b.type.healAmount / (maxBounces * bounces.get(b.id))));
                            }
                        } else {
                            b.remove();
                        }
                    } else if (bounceOnEnemyWalls && bounces.get(b.id) < maxBounces) {
                        b.vel.setAngle((b.angleTo(bl) + 180) + Mathf.random(-50,50));
                        if (b.type.pierceArmor) {
                            bl.damagePierce(b.damage - b.damage / (maxBounces * bounces.get(b.id)));
                        } else {
                            bl.damage(b.damage - b.damage / (maxBounces * bounces.get(b.id)));
                        }

                    }
                    bounces.replace(b.id, bounces.get(b.id) + 1);
                    justBounced.replace(b.id, true);
                }
            });
        } else justBounced.replace(b.id, false);

        float[] tarSize ={b.hitSize};
        Teamc tar = findTarget(b, tarSize);

        updateCollision(b, tar, tarSize[0]);
        updateTrail(b);
        updateHoming(b, tar);
        updateWeaving(b);
        updateTrailEffects(b);
        updateBulletInterval(b);

        if(hitProjectiles){
            Groups.bullet.each(bb -> {
                if (bb.team != b.team) {
                    float d = Mathf.dst(bb.x, bb.y, b.x, b.y);
                    if (d < b.hitSize*3) {
                        currentHp.replace(b.id,currentHp.get(b.id) - bb.damage);
                        bb.remove();
                    }
                    if (currentHp.get(b.id) <= 0){
                        b.remove();
                    }
                }
            });
        }


        if(artilleryTrail) updateArtilleryTrail(b);
    }


    @Override
    public void updateCollision(Bullet b, Teamc target, float tarSize){
        /*If someone finds a better way to do this, please let us know -RushieWsahie*/
        boolean within = target != null && b.within(target.x(), target.y(), Math.max(tarSize,  b.hitSize)),
                onOwner = b.owner instanceof Building d && !b.within(d.x(), d.y, d.hitSize()) || b.owner instanceof Hitboxc c && !b.within(c.x(), c.y(), c.hitSize());

        /*Feature/bug: ignore one tile blocks beside the owner except  when shot in corner angle*/
        if(b.tileOn() == null || within || !onOwner) return;
        if(!b.tileOn().solid()) return;

        if(!bounceOnWalls || bounces.get(b.id) >= maxBounces) b.remove();

        else{
            if(!justBounced.get(b.id)){
                b.vel.setAngle((b.angleTo(b.tileOn()) + 180) + Mathf.random(-50,50));
                justBounced.replace(b.id,true);
            }else{
                justBounced.replace(b.id,false);
            }
        }
    }

    @Override
    public void load(){
        super.load();
        liquidRegion = Core.atlas.find(liquidSprite);
        teamRegion  = Core.atlas.find(teamSprite);
    }

    @Override
    public void draw(Bullet b){
        super.draw(b);

        float shrink = shrinkInterp.apply(b.fout());
        float height = this.height * ((1f - shrinkY) + shrinkY * shrink);
        float width = this.width * ((1f - shrinkX) + shrinkX * shrink);
        float offset = -90 + (spin != 0 ? Mathf.randomSeed(b.id, 360f) + b.time * spin : 0f) + rotationOffset;

        if(liquidRegion.found() && liquidColour != null){
            Draw.color(liquidColour);
            Draw.rect(liquidRegion, b.x, b.y, width, height, b.rotation() + offset);
        }

        if(teamRegion.found()){
            Draw.color(b.team.color);
            Draw.rect(teamRegion, b.x, b.y, width, height, b.rotation() + offset);
        }

        Draw.reset();

    }
}
