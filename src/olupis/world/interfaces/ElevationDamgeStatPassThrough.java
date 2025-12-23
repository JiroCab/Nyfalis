package olupis.world.interfaces;

import mindustry.content.*;
import mindustry.type.*;

public interface ElevationDamgeStatPassThrough{
    //just used by NyfalisStats to pass through


    default float groundDamage(){
        return -1;
    }


    default float groundDamageMultiplier (){
        return -1f;
    }

    default float groundDamageSplashMultiplier(){
        return -1f;
    }

    default StatusEffect airStatusEffect(){
        return StatusEffects.none;
    }

    default StatusEffect groundStatusEffect(){
        return StatusEffects.none;
    }





}
