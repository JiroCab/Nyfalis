package olupis.world.entities.weapons;

import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.ItemTurret.*;
import olupis.world.entities.abilities.*;

public class MineAblityWeapon extends  NyfalisWeapon{
    public int ablityIndex = 1;

    @Override
    protected void handleBullet(Unit unit, WeaponMount mount, Bullet bullet){
        super.handleBullet(unit, mount, bullet);

        if(unit.abilities[ablityIndex] != null && unit.abilities[ablityIndex] instanceof MiningBulletAbility ab){
            if(ab.ammo.first() != null){
                ItemEntry pew = ab.FirstAndRemove();
                bullet.damage += ab.hardnessDamgeMul * pew.item.hardness;
                bullet.data = pew.item;
            }
        }
    }
}
