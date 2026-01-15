package olupis.world.entities.abilities;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import mindustry.ai.types.*;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import olupis.content.*;

import static mindustry.Vars.*;

public class MicroWaveFieldAbility extends Ability {
    private static final Seq<Healthc> all = new Seq<>();

    public float damage = 1, reload = 100, range = 60, boostRange = 30, groundRange = 60;
    public Effect healEffect = Fx.heal, hitEffect = Fx.hitFuse, damageEffect = Fx.none, shootEffect = Fx.none;
    public StatusEffect status = StatusEffects.none;
    public Sound shootSound = NyfalisSounds.shootMicrowave;
    public float statusDuration = 60f * 6f;
    public float x, y;
    public boolean targetGround = true, targetAir = true, hitBuildings = true, hitUnits = true;
    public int maxTargetsGround = 25, maxTargetBoost = maxTargetsGround, maxTargets = maxTargetsGround;
    public float healPercent = 3f;
    /** Multiplies healing to units of the same type by this amount. */
    public float sameTypeHealMult = 1f;

    public float layer = Layer.bullet - 0.001f, blinkScl = 20f;
    public float sectorRad = 0.18f, rotateSpeed = 0.23f;
    public int sectors = 10;
    public Color color = Color.valueOf("996E31");
    public boolean useAmmo = true;

    protected float timer, curStroke;
    protected boolean anyNearby = false;

    public boolean boostShoot = true, groundShoot = true, ideRangeDisplay = true;

    public MicroWaveFieldAbility(float damage, float reload, float groundRange, float boostRange){
        this.damage = damage;
        this.reload = reload;
        this.groundRange = this.range = groundRange;
        this.boostRange = boostRange;
    }
    public MicroWaveFieldAbility(){}

    @Override
    public void addStats(Table t){
        t.add(Core.bundle.format("bullet.damage", damage)).row();
        t.add("[lightgray]" + Stat.reload.localized() + ": [white]" + Strings.autoFixed(60f / reload, 2) + " " + StatUnit.perSecond.localized()).row();
        t.add("[lightgray]" + Stat.shootRange.localized() + ": [white]" +  Strings.autoFixed(groundRange / tilesize, 2) + " " + StatUnit.blocks.localized()).row();
        t.add("[lightgray]" + Core.bundle.get("ability.microwavefieldAbility.boostrange") + ": [white]" +  Strings.autoFixed(boostRange / tilesize, 2) + " " + StatUnit.blocks.localized()).row();
        t.add(Core.bundle.format("ability.microwavefieldAbility.maxtargets", maxTargetsGround)).row();
        t.add(Core.bundle.format("ability.microwavefieldAbility.maxtargetsBoost", maxTargetBoost)).row();

        if(status != StatusEffects.none){
            t.row();
            t.add(status.emoji() + " " + status.localizedName);
        }
    }

    @Override
    public void draw(Unit unit){
        super.draw(unit);

        Draw.z(layer);
        Draw.color(color);
        Tmp.v1.trns(unit.rotation - 90, x, y).add(unit.x, unit.y);
        float rx = Tmp.v1.x, ry = Tmp.v1.y;

        Lines.stroke((0.7f + Mathf.absin(blinkScl, 0.7f)), color);

        if(curStroke > 0){
            for(int i = 0; i < sectors; i++){
                float rot = unit.rotation + i * 360f/sectors + Time.time * rotateSpeed;
                Lines.arc(rx, ry, range, sectorRad, rot);
            }
        }
        Drawf.light(rx, ry, range * 1.5f, color, curStroke * 0.8f);

        Draw.reset();
    }

    @Override
    public void update(Unit unit){
        boolean shouldShoot = (boostShoot && unit.isFlying()) || (groundShoot  && unit.isGrounded());
        curStroke = Mathf.lerpDelta(curStroke, anyNearby && shouldShoot ? 1 : 0, 0.15f);
        range = Mathf.lerpDelta(range, unit.isFlying() ? boostRange : groundRange, 0.25f);
        maxTargets = unit.isFlying() ? maxTargetBoost : maxTargetsGround;

        if((timer += Time.delta) >= reload && shouldShoot && (!useAmmo || unit.ammo > 0 || !state.rules.unitAmmo)){
            Tmp.v1.trns(unit.rotation - 90, x, y).add(unit.x, unit.y);
            float rx = Tmp.v1.x, ry = Tmp.v1.y;
            anyNearby = false;

            all.clear();

            //DO NOT THE HEAL
            if(hitUnits){
                Units.nearby(null, rx, ry, range, other -> {
                    if(other != unit && other.checkTarget(targetAir, targetGround) && other.targetable(unit.team) && (other.team != unit.team)){
                        all.add(other);
                    }
                });
            }

            if(hitBuildings && targetGround){
                Units.nearbyBuildings(rx, ry, range, b -> {
                    if((b.team != Team.derelict || state.rules.coreCapture) && (b.team != unit.team)){
                        all.add(b);
                    }
                });
            }


            all.sort(h -> h.dst2(rx, ry) + Mathf.randomSeed( unit.id, 0f, 20f));
            if(unit.controller() instanceof CommandAI c && c.attackTarget instanceof Healthc h && all.contains(h)){
                all.remove(h);
                all.add(h);
            }

            int len = Math.min(all.size, maxTargets);
            for(int i = 0; i < len; i++){
                Healthc other = all.get(i);

                if(((Teamc)other).team() == unit.team){
                    if(!other.damaged()) return;

                    //anyNearby = true;
                    float healMult = (other instanceof Unit u && u.type == unit.type) ? sameTypeHealMult : 1f;
                    other.heal(healPercent / 100f * other.maxHealth() * healMult);
                    healEffect.at(other);
                    damageEffect.at(rx, ry, 0f, color, other);
                    hitEffect.at(rx, ry, unit.angleTo(other), color);

                    if(other instanceof Building b) Fx.healBlockFull.at(b.x, b.y, 0f, color, b.block);
                }else{
                    anyNearby = true;
                    if(other instanceof Building b) b.damage(unit.team, damage * state.rules.unitDamage(unit.team));
                    else other.damage(damage * state.rules.unitDamage(unit.team));
                    if(other instanceof Statusc s) s.apply(status, statusDuration);
                    hitEffect.at(other.x(), other.y(), unit.angleTo(other), color);
                    damageEffect.at(rx, ry, 0f, color, other);
                    shootEffect.at(rx, ry, unit.angleTo(other), color);
                }
            }

            if(anyNearby){
                shootSound.at(unit);

                if(useAmmo && state.rules.unitAmmo) unit.ammo--;
            }

            timer = 0f;
        }
    }


}
