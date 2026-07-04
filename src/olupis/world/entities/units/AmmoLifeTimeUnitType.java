package olupis.world.entities.units;

import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.meta.*;
import olupis.content.*;
import olupis.world.*;
import olupis.world.ai.*;
import olupis.world.entities.weapons.*;
import olupis.world.interfaces.*;

import java.util.*;

/*Unit that dies when it runs out of ammo, ammo Depletes over time*/
public class AmmoLifeTimeUnitType extends  AmmoEnabledUnitType {

    public  boolean
        /*Custom logic to remove ammo over time*/
        ammoDepletesOverTime = true,
        ammoDepletesInRange = false,
        /*Custom logic to kill unit on no ammo*/
        killOnAmmoDepletion = true,
        /*mining depletes ammo*/
        miningDepletesAmmo = false,
        /*Being player controlled depletes ammo*/
        depleteOnInteraction = true, depleteOnInteractionUsesPassive = false,
        /*Deplete Ammo when over unit cap, Assumes ammoDepletesOverTime = true */
        overCapacityPenalty = false,
        /*Time out params */
        inoperableDepletes = false, lookForParent = false
    ;
    public int
        /*Amount to deplete per tick*/
        ammoDepletionAmount = 1,
        passiveAmmoDepletion = ammoDepletionAmount,
        /*Ammo amount that will trigger death*/
        deathThreshold = 1,
        /*How many ticks before ammo is spoils/depleted*/
        ammoDepletionOffset = Math.round(Time.toMinutes)
    ;
    public float
        /*Anti-spam to hard, aka setting a diminishing return for the sake of frames */
        penaltyMultiplier = 2f
    ;


    public Sound timedOutSound = Sounds.explosion;
    public Effect timedOutFx = NyfalisFxs.explosionUnitDepleted;
    public float timedOutSoundPitch = 1f, timedOutSoundVolume = 0.4f, maxRange = -1;


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
    public Color ammoColor(Unit unit){

        float
            a = unit instanceof AmmoNyf na ? na.currentAmmo() : 0,
            c = unit instanceof AmmoNyf na ? na.ammoCapacity() : 0,
            f = Mathf.clamp((a - deathThreshold) / (c - deathThreshold));
        if(ammoDepletesInRange && !inRange(unit)) return Color.black;
        return Tmp.c1.set(Color.black).lerp(unit.team.color, f + Mathf.absin(Time.time, Math.max(f * 2.5f, 1f), 1f - f));
    }

    public boolean operational(AmmoNyf ammo){
        boolean out =((ammo.unit().count() > ammo.unit().cap() && ammo.unit().type.useUnitCap)), op = false;
        if(inoperableDepletes) op = (( ammo.currentAmmo() >= deathThreshold && ammo.unit().controller() instanceof NyfalisMiningAi ai  && (ai.targetItem == null || ammo.unit().closestCore() == null || ai.inoperable) )
        || !ammo.unit().moving() && (ammo.unit().hasWeapons() && !ammo.unit().isShooting || !ammo.unit().activelyBuilding())) //TODO: keep track of building prog and dont dep when no progress
        || (ammo.unit().controller() instanceof InoperableAi ai && ai.inoperable());

        boolean shouldDeplete = ( ammo.ammoTime() <= 0) || (ammoDepletesInRange && !inRange(ammo.unit()));
        if(op || (ammoDepletesOverTime && shouldDeplete && (!overCapacityPenalty || (ammo.unit().count() > ammo.unit().cap())))){
            ammo.resupplyAdd(-Math.round(((depleteOnInteractionUsesPassive ? passiveAmmoDepletion : ammoDepletionAmount) * (out || op ? penaltyMultiplier : 1f))));
        }
        return out;
    }

    @Override
    public void update(Unit unit){
        if(!(unit instanceof  AmmoNyf ae)) return;

        boolean works = operational(ae);
        float multiplier =works ? penaltyMultiplier : 1f;

        ae.setAmmoTime(ae.ammoTime() -1);

        if(miningDepletesAmmo && unit.mining()){

            ae.resupplyAdd(-Math.round((ammoDepletionAmount * multiplier)));
            if(ae.shouldRetreat()){
                unit.mineTile = null;
            }
        }

        if(unit.isPlayer() && depleteOnInteraction && ae.currentAmmo() >= deathThreshold + 1 ){
            ae.resupply( Math.round(ae.currentAmmo() - (ammoDepletionAmount * multiplier)));
        }

        if (ae.currentAmmo() <= deathThreshold && killOnAmmoDepletion){
            for(WeaponMount mount : unit.mounts){
                if(mount.weapon instanceof  NyfalisWeapon w && w.fireOnTimeOut ){
                    mount.shoot = true;
                    mount.weapon.update(unit, mount);
                }
            }
            NyfWorldFuckingHelper.callTimeOut(unit);
        }

        super.update(unit);
    }

    public boolean inRange(Unit unit){
        if(unit instanceof  AmmoNyf ai && ai.parent() != null) return unit.within(ai.parent(), maxRange);
        return true;
    }

    public void timedOutTyped(Unit unit){
        timedOutFx.at(unit.x, unit.y, unit.rotation, unit);
        timedOutSound.at(unit.x, unit.y, timedOutSoundPitch, timedOutSoundVolume);
        unit.remove();
    }


    @Override
    public float partAmmo(Unit unit){
        if(!( unit instanceof  AmmoNyf na)) return 0;
        return ((float)(na.currentAmmo() - deathThreshold) / (na.ammoCapacity() - deathThreshold));
    }

}
