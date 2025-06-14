package olupis.world.entities.abilities;

import mindustry.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.type.*;

public class DeathStatusAbility extends Ability{
    public float range = 50 * Vars.tilesize, effectDuration = 60f * 10f;
    public StatusEffect effect;

    public DeathStatusAbility(){

    }

    public DeathStatusAbility(StatusEffect effect){
        this.effect = effect;
    }

    public DeathStatusAbility(float range, StatusEffect effect, float effectDuration){
        this.range = range;
        this.effect = effect;
        this.effectDuration = effectDuration;
    }


    @Override
    public void death(Unit unit){
        for(Unit t : Groups.unit){
            if(t == unit)continue;
            if(!t.within(unit, range))continue;

            t.apply(effect, effectDuration);

        }
    }


}
