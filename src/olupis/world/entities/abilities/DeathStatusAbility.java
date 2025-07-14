package olupis.world.entities.abilities;

import arc.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.type.*;

import static mindustry.Vars.tilesize;

public class DeathStatusAbility extends Ability{
    public float range = 30 * Vars.tilesize, effectDuration = 60f * 10f;
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
    public void addStats(Table t){
        t.add(Core.bundle.format("bullet.range", Strings.autoFixed(range / tilesize, 2)));
        t.row();
        t.add((effect.hasEmoji() ? effect.emoji() : "") + "[stat]" + effect.localizedName);
    }

    @Override
    public void death(Unit unit){
        for(Unit t : Groups.unit){
            if(t == unit)continue;
            if(t.team != unit.team)continue;
            if(!t.within(unit, range))continue;

            t.apply(effect, effectDuration);

        }
    }


}
