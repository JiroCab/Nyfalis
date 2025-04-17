package olupis.world.entities.units;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.meta.*;
import olupis.content.*;
import olupis.world.ai.*;
import olupis.world.entities.packets.*;
import olupis.world.entities.weapons.*;

import static mindustry.Vars.*;

/*Unit that dies when it runs out of ammo, ammo Depletes over time*/
public class AmmoLifeTimeUnitType extends  AmmoEnabledUnitType {
    /*Custom logic to remove ammo over time*/
    public  boolean ammoDepletesOverTime = true, ammoDepletesInRange = false;
    /*Custom logic to kill unit on no ammo*/
    public  boolean killOnAmmoDepletion = true;
    /*Amount to deplete per tick*/
    public float ammoDepletionAmount = 0.2f;
    public float passiveAmmoDepletion = ammoDepletionAmount;
    /*Ammo amount that will trigger death*/
    public float deathThreshold = 0.1f;
    /*mining depletes ammo*/
    public boolean miningDepletesAmmo = false;
    /*Time before depleting ammo*/
    public float ammoDepletionOffset = Time.toMinutes;
    float startTime;
    /*Being player controlled depletes ammo*/
    public boolean depleteOnInteraction = true, depleteOnInteractionUsesPassive = false;
    /*Deplete Ammo when over unit cap, Assumes ammoDepletesOverTime = true */
    public boolean overCapacityPenalty = false;
    /*Anti-spam to hard, aka setting a diminishing return for the sake of frames */
    public float penaltyMultiplier = 2f;
    /*Time out params */
    public boolean inoperable = false, inoperableDepletes = true;
    public Sound timedOutSound = Sounds.explosion;
    public Effect timedOutFx = NyfalisFxs.unitBreakdown;
    public float timedOutSoundPitch = 1f, timedOutSoundVolume = 0.4f, maxRange = -1;
    public Vec2 startPos;


    //TODO: Range limit them, deplete ammo when N tiles away from X & Y

    public AmmoLifeTimeUnitType(String name){
        /*let's just hope that ammo is never removed at least not removed internally */
        super(name);
        envDisabled = Env.none;
    }

    public void drawItems(Unit unit){
        if(drawAmmo){
            //Jank otherwise it draw under the ammo
            float z = !unit.isAdded() ? Draw.z() : unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);
            Draw.z(z + 0.001f);
        }
        super.drawItems(unit);
    }


    @Override
    public void buildBars(Table bars, Unit unit){
        bars.defaults().growX().height(20f).pad(4);

        bars.add(new Bar("stat.health", Pal.health, unit::healthf).blink(Color.white));
        bars.row();

        if(state.rules.unitAmmo || killOnAmmoDepletion){
            bars.add(new Bar(ammoType.icon() + " " + Core.bundle.get("stat.ammo"), ammoType.barColor(), () -> (unit.ammo - deathThreshold ) / (ammoCapacity - deathThreshold) ));
            bars.row();
        }

        for(Ability ability : unit.abilities){
            ability.displayBars(unit, bars);
        }

        if(payloadCapacity > 0 && unit instanceof Payloadc payload){
            bars.add(new Bar("stat.payloadcapacity", Pal.items, () -> payload.payloadUsed() / unit.type().payloadCapacity));
            bars.row();

            var count = new float[]{-1};
            bars.table().update(t -> {
                if(count[0] != payload.payloadUsed()){
                    payload.contentInfo(t, 8 * 2, 270);
                    count[0] = payload.payloadUsed();
                }
            }).growX().left().height(0f).pad(0f);
        }
    }

    @Override
    public Color ammoColor(Unit unit){
        float f = Mathf.clamp(unit.ammof());
        if(ammoDepletesInRange && !inRange(unit)) return Color.black;
        return Tmp.c1.set(Color.black).lerp(unit.team.color, f + Mathf.absin(Time.time, Math.max(f * 2.5f, 1f), 1));
    }

    @Override
    public void update(Unit unit){
        if (unit.ammo <= deathThreshold && killOnAmmoDepletion){
            for(WeaponMount mount : unit.mounts){
                if(mount.weapon instanceof  NyfalisWeapon w && w.fireOnTimeOut ){
                    mount.shoot = true;
                    mount.weapon.update(unit, mount);
                }
            }
            callTimeOut(unit);
        }

        inoperable = false;
        boolean multiplier =((unit.count() > unit.cap() && unit.type.useUnitCap));
        if(inoperableDepletes) inoperable = ((unit.controller() instanceof NyfalisMiningAi ai  && (ai.targetItem == null || unit.closestCore() == null || ai.targetItem == null || ai.inoperable) )
                            || !unit.moving() && (unit.hasWeapons() && !unit.isShooting || !unit.activelyBuilding()))
                            || (unit.controller() instanceof SearchAndDestroyFlyingAi ai && ai.inoperable);

        boolean shouldDeplete = ( (startTime+ ammoDepletionOffset) <= Time.time) || (ammoDepletesInRange && !inRange(unit));
        if(inoperable || (ammoDepletesOverTime && shouldDeplete && (!overCapacityPenalty || (unit.count() > unit.cap())))){
            unit.ammo  -= ((depleteOnInteractionUsesPassive ? passiveAmmoDepletion : ammoDepletionAmount) * (multiplier || inoperable ? penaltyMultiplier : 1f));
        }

        if(miningDepletesAmmo && unit.mining()){
            unit.ammo = unit.ammo - (ammoDepletionAmount * (multiplier ? penaltyMultiplier : 1f));
        }

        if(unit.isPlayer() && depleteOnInteraction && unit.ammo >= deathThreshold +0.05f ){
            unit.ammo = unit.ammo - (ammoDepletionAmount * (multiplier ? penaltyMultiplier : 1f));
        }

        super.update(unit);
    }

    @Override
    public Unit create(Team team){
        Unit unit = super.create(team);

        unit.ammo(ammoCapacity);
        startTime = Time.time;
        startPos = new Vec2(unit.x /8f, unit.y /8f);
        return unit;
    }

    public Unit create(Team team, float unitRange, float startX, float startY ){
        Unit unit = super.create(team);
        this.maxRange = unitRange;
        startPos = new Vec2(startX /8f, startY /8f);

        startTime = Time.time;
        unit.apply(spawnStatus, spawnStatusDuration);
        return unit;
    }

    public boolean inRange(Unit unit){
        if(unit.type.aiController instanceof AgressiveFlyingAi ai && ai.hasParent && ai.parent != null) return unit.within(ai.parent.vel, maxRange);
        if(startPos == null || maxRange == -1) return true;
        return unit.within(startPos.x * 8, startPos.y * 8, maxRange);
    }

    public void callTimeOut(Unit unit){
        if (!net.active() || Vars.net.server()) {
            NyfalisUnitTimedOutPacket packet = new NyfalisUnitTimedOutPacket();
            packet.unit = unit;
            Vars.net.send(packet, true);
            timedOut(unit);
        }

    }

    public void timedOut(Unit unit){
        timedOutFx.at(unit.x, unit.y, unit.rotation, unit);
        timedOutSound.at(unit.x, unit.y, timedOutSoundPitch, timedOutSoundVolume);
        unit.remove();
    }


    @Override
    public float partAmmo(Unit unit){
        return (unit.ammo - deathThreshold ) / (ammoCapacity - deathThreshold);
    }

}
