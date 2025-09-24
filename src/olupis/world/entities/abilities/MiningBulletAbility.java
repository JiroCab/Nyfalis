package olupis.world.entities.abilities;

import arc.struct.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.ItemTurret.*;

public class MiningBulletAbility extends Ability{
    public float hardnessDamgeMul = 5f;
    public Seq<ItemEntry> ammo = new Seq<>();


    public ItemEntry FirstAndRemove(){
        return ammo.first();
    }

    @Override
    public void init(UnitType type){
        super.init(type);

    }
}
