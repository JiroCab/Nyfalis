package olupis.world.entities.abilities;

import mindustry.entities.abilities.*;

public class CarrierResupplyAbility extends Ability{
    public int tier = 1;

    public CarrierResupplyAbility(){
        //Empty for now
        //TODO: if ammo rule is on, they resupply all units in range
        display = false;
    }

    public CarrierResupplyAbility(int tier){
        this.tier = tier;
        display = false;
    }

}
