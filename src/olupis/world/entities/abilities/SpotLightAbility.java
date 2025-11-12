package olupis.world.entities.abilities;

import arc.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.meta.*;
import olupis.world.*;

import static mindustry.Vars.tilesize;

public class SpotLightAbility extends Ability{
    @Nullable public static Teamc target;
    protected float timer;
    public float checkTarget;

    public float spotRange = Vars.tilesize * 8, spottedDuration = 4f;
    public StatusEffect spotted = StatusEffects.none;
    public boolean spotBoost = true, spotGrounded = true;

    @Override
    public void addStats(Table t){
        t.add(Core.bundle.format("bullet.range", Strings.autoFixed(spotRange / tilesize, 2)));
        t.row();
        t.add((spotted.hasEmoji() ? spotted.emoji() : "") + "[stat]" + spotted.localizedName).get().clicked( () -> {
            if(!spotted.isHidden())Vars.ui.content.show(spotted);
        });
    }

    @Override
    public void update(Unit unit){
        if((unit.isFlying() && !spotBoost) || (unit.isGrounded() && !spotGrounded)){
            if(target != null) target = null;
            return;
        }
        if((timer += Time.delta) >= checkTarget){
            target = NyfWorldFuckingHelper.bestEnemyFog(unit.team, unit.x, unit.y, spotRange * Vars.tilesize, u -> !u.dead && (!u.hasEffect(spotted) || u.getDuration(spotted) <= spottedDuration * 0.5f), UnitSorts.strongest);
        }

        if(target != null){
            if(!target.within(unit, spotRange * Vars.tilesize)){
                target = null;
            }else {
                if(spotted != StatusEffects.none && target instanceof  Statusc st) st.apply(spotted, spottedDuration);
            }
        }

    }

}
