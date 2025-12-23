package olupis.world.entities.bullets;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import olupis.world.interfaces.*;

//ContinuousLaserBulletType with ShrapnelBulletType style of rendering
public class ContinuousShrapnelBulletType extends ContinuousLaserBulletType implements ElevationDamgeStatPassThrough{
    public int serrations = 7;
    public float
        serrationLenScl = 10f,
        serrationWidth = 2f,
        serrationSpacing = 8f,
        serrationSpaceOffset = 80f,
        serrationFadeOffset = 0.5f,
        serrationAngle = 0,
        serrationFirstOffset = 0,
        serrationAlphaMul = 0.95f,
        serrationLengthMul = 1f,

        groundDamageMultiplier = 1f,
        groundDamageSplashMultiplier = 1f
    ;
    public boolean flatDamage = false;
    public StatusEffect groundStatus = StatusEffects.none, airStatus = StatusEffects.none;
    static final EventType.UnitDamageEvent bulletDamageEvent = new EventType.UnitDamageEvent();

    public ContinuousShrapnelBulletType(float damage){
        this.damage = damage;
    }

    public ContinuousShrapnelBulletType(){}


    @Override
    public void draw(Bullet b){
        float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
        float realLength = Damage.findLength(b, length * fout, laserAbsorb, pierceCap);
        float rot = b.rotation();

        for(int c = 0; c < colors.length; c++){
            Draw.color(Tmp.c1.set(colors[c]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));
            float colorFin = c / (float)(colors.length - 1);
            float baseStroke = Mathf.lerp(strokeFrom, strokeTo, colorFin);
            float stroke = (width + Mathf.absin(Time.time, oscScl, oscMag)) * fout * baseStroke;
            float ellipseLenScl = Mathf.lerp(1 - c / (float)(colors.length), 1f, pointyScaling);

            Draw.alpha(serrationAlphaMul * colors[c].a);
            for(int i = 0; i < serrations; i++){
                Tmp.v1.trns(rot, i * serrationSpacing + serrationFirstOffset);
                float sl = (Mathf.clamp(fout - serrationFadeOffset) * (serrationSpaceOffset - i * serrationLenScl)) * serrationLengthMul * ellipseLenScl;
                Drawf.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, serrationWidth + Mathf.absin(Time.time, oscScl, oscMag), sl, b.rotation() + 90 + serrationAngle);
                Drawf.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, serrationWidth + Mathf.absin(Time.time, oscScl, oscMag), sl, b.rotation() - 90 - serrationAngle);
            }
            Draw.alpha(colors[c].a);
            Lines.stroke(stroke);
            Lines.lineAngle(b.x, b.y, rot, realLength - frontLength, false);

            //back ellipse
            Drawf.flameFront(b.x, b.y, divisions, rot + 180f, backLength, stroke / 2f);

            //front ellipse
            Tmp.v1.trnsExact(rot, realLength - frontLength);
            Drawf.flameFront(b.x + Tmp.v1.x, b.y + Tmp.v1.y, divisions, rot, frontLength * ellipseLenScl, stroke / 2f);

            Draw.reset();
        }

        Tmp.v1.trns(b.rotation(), realLength * 1.1f);

        Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f);
        Draw.reset();
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
            if(groundStatus != StatusEffects.none &&unit.isGrounded()) unit.apply(groundStatus, statusDuration);
            if(airStatus != null && !unit.isGrounded()) unit.apply(airStatus, statusDuration);

            Events.fire(bulletDamageEvent.set(unit, b));
        }

        if(!wasDead && entity instanceof Unit unit && unit.dead){
            Events.fire(new EventType.UnitBulletDestroyEvent(unit, b));
        }

        handlePierce(b, health, entity.x(), entity.y());
    }

    @Override
    public float groundDamage(){
        return (flatDamage ? groundDamageMultiplier :   groundDamageMultiplier * damage) / damageInterval * 60f ;
    }
    @Override
    public float groundDamageMultiplier(){
        return flatDamage ? ((groundDamageMultiplier / damage)) : groundDamageMultiplier;
    }

    @Override
    public float groundDamageSplashMultiplier(){
        return flatDamage ? ((groundDamageSplashMultiplier / splashDamage)) : groundDamageSplashMultiplier;
    }
    @Override
    public StatusEffect groundStatusEffect(){
        return groundStatus;
    }

    @Override
    public StatusEffect airStatusEffect(){
        return airStatus;
    }
}
