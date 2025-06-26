package olupis.world.blocks.turret;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.effect.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;
import olupis.content.*;
import olupis.world.entities.parts.*;

import static mindustry.Vars.world;

public class UnstablePowerTurret extends PowerTurret {

    public Effect explodeEffect = new MultiEffect(NyfalisFxs.cascadeSun, NyfalisFxs.cascadeSmoke);
    public Sound explodeSound = Sounds.boom;
    public Sound warningSound = NyfalisSounds.cascadeDangerWarning;

    public float explosionDamage = 60, explosionRadius = 40;
    public Liquid explosionPuddleLiquid = null;
    public int explosionPuddles = 4;
    public float explosionShake = 3, explosionShakeDuration = 30;
    public float explosionPuddleRange = 80, explosionPuddleAmount = 10;
    public float smokeThreshold = 0.3f, flashThreshold = 0.6f, soundThreshold = 0.4f;
    public float coolantPower = 0.05f;
    public float heatTime = 5f * 60f;
    public float minimumHeatTime = 10;
    public int maxCopies = 3;
    public Color coolColor = new Color(1, 1, 1, 0f);
    public Color hotColor = Color.red;
    public Color flashColor1 = Color.red, flashColor2 = Color.yellow;
    public float minBeepRate = 2f , maxBeepRate = 60f;

    public float illuminateTime = 30f;

    public UnstablePowerTurret(String name){
        super(name);
        liquidCapacity = 30;
        drawer = new DrawUnstableTurret();
        rebuildable = false;
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("heat", (UnstablePowerTurretBuild entity) -> new Bar("bar.heat", Pal.lightOrange, () -> entity.heatT));
    }

    public void setStats() {
        super.setStats();
        this.stats.remove(Stat.ammo);
        this.stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(new Object[]{this, this.shootType})));
        this.stats.remove(Stat.booster);
        this.stats.add(Stat.input, StatValues.boosters(this.reload, this.coolant.amount, this.coolantMultiplier, false, this::consumesLiquid));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x, y, explosionRadius, Color.red);
    }

    public class UnstablePowerTurretBuild extends PowerTurretBuild{
        public float progressLight;
        public float heatT;
        public float beepT;
        public float flash;
        @Override
        public void updateTile(){
            int[] copies= {0};
            Units.nearbyBuildings(this.x,this.y,range,b -> {
                        if (b.block == this.block && b.team == this.team && b != this){
                            if(!Core.settings.getBool("nyfalis-sandbox-super-weapon-cap")){
                                copies[0]++;
                            }
                        };
                    });
            unit.ammo(power.status * (float)unit.type().ammoCapacity);
            super.updateTile();

            if(heatT >= soundThreshold){

                beepT += this.delta() / Mathf.lerp(maxBeepRate, minBeepRate, heatT);
                beepT = Mathf.clamp(beepT);
                if(beepT >= 0.999f){
                    beepT = 0;
                    makeBeep();
                }
            }

            if(isShooting() && power.status > 0){
                heatT = Mathf.clamp(heatT);
                if (heatTime - (60* copies[0]) <= minimumHeatTime){
                    heatT += edelta() / minimumHeatTime;
                }else{
                    heatT += edelta() / (heatTime - (60*copies[0]));
                }
                heatT = Mathf.clamp(heatT);
            }

            //So logic cant cheese it
            if(heatT > 0 &&  liquids.currentAmount() > 0.5f){
                float maxUsed = Math.min(liquids.currentAmount(), heatT / coolantPower);
                if( copies[0] <= maxCopies){
                    //for some reason, heat doesn't accumulate after the water hits zero this way, so I added this if
                    heatT -= maxUsed * coolantPower;
                }
                liquids.remove(liquids.current(), maxUsed + (20* copies[0]));
                if(!isShooting() && liquids.currentAmount() <= 0){
                    heatT = Mathf.clamp(heatT - 0.0005f);
                }
            }

            if(heatT > smokeThreshold){
                float smoke = 1.0f + (heatT - smokeThreshold) / (1f - smokeThreshold); //ranges from 1 to 2.0
                if(Mathf.chance(smoke / 20.0 * delta())){
                    Fx.reactorsmoke.at(x + Mathf.range(size * 8 / 2f),
                            y + Mathf.range(size * 8 / 2f));
                }
            }

            heatT = Mathf.clamp(heatT);

            if(heatT >= 0.999f){
                Events.fire(Trigger.thoriumReactorOverheat);
                createExplosion();
                kill();
            }
        }

        public void makeBeep(){
            float vol = Core.settings.getInt("nyfalis-beep-volume");
            if(vol == -1) vol = Core.settings.getInt("sfxvol");
            if(Core.app.isHeadless()) vol = 0;
            vol *= warningSound.calcFalloff(x, y);
            if(vol > 0.01f){
                warningSound.play(vol / 100, 1, warningSound.calcPan(this.x, this.y));
            }
        }


        public void createExplosion(){
            if(explosionDamage > 0){
                Damage.damage(x, y, explosionRadius * 8, explosionDamage);
            }

            explodeEffect.at(this);
            explodeSound.at(this);

            if(explosionPuddleLiquid != null){
                for(int i = 0; i < explosionPuddles; i++){
                    Tmp.v1.trns(Mathf.random(360f), Mathf.random(explosionPuddleRange));
                    Tile tile = world.tileWorld(x + Tmp.v1.x, y + Tmp.v1.y);
                    Puddles.deposit(tile, explosionPuddleLiquid, explosionPuddleAmount);
                }
            }

            if(explosionShake > 0){
                Effect.shake(explosionShake, explosionShakeDuration, this);
            }
        }

        @Override
        public void drawLight() {
            boolean check = (!hasPower || power.status >= 0.5f) && (hasAmmo());
            if(emitLight){
                progressLight = Mathf.lerpDelta(progressLight, check ? lightRadius : 0, this.delta() / illuminateTime);
                if(progressLight >= 0)Drawf.light(x, y, progressLight, lightColor, lightColor.a);
            }
            super.drawLight();
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            Drawf.dashCircle(x, y, explosionRadius*8, Color.red);

            int[] c = {0};
            Units.nearbyBuildings(this.x,this.y,range,b -> {
                float rad = b.block.size * 6f;
                if (b.block == this.block && b.team == this.team && b != this){
                    if(!Core.settings.getBool("nyfalis-sandbox-super-weapon-cap")){
                        Tmp.c1.set(c[0] < maxCopies ? Pal.placing : Color.red);
                        Fill.lightInner(b.x, b.y, 6,
                            Math.max(0f, rad * 0.8f),
                            rad,
                            0f,
                            Tmp.c3.set(Tmp.c1).a(0f),
                            Tmp.c2.set(Tmp.c1).a(0.7f)
                        );
                        c[0]++;
                        Lines.stroke(1f);
                        Draw.color(Tmp.c1);
                        Lines.poly(b.x, b.y, 6, rad + 0.5f);
                    }
                }
            });
        }


        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.heat) return heatT;
            return super.sense(sensor);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(heatT);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 2){
                heatT = read.f();
            }
        }

        @Override
        public byte version(){
            return 2;
        }
    }
}

