package olupis.world.entities.weapons;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.weapons.RepairBeamWeapon;
import olupis.world.entities.units.AmmoLifeTimeUnitType;

public class LimitedRepairBeamWeapon extends RepairBeamWeapon {
    public TextureRegion ammoRegion;

    public  LimitedRepairBeamWeapon(String name){
        super(name);
    }
    public LimitedRepairBeamWeapon(){
        super();
    }


    @Override
    public void load(){
        super.load();
        ammoRegion = Core.atlas.find(name + "-ammo");
    }

    @Override
    public void draw(Unit unit, WeaponMount mount){
        super.draw(unit, mount);
        if(ammoRegion.found()){

            float  rotation = unit.rotation - 90,
                    realRecoil = Mathf.pow(mount.recoil, recoilPow) * recoil,
                    weaponRotation  = rotation + (rotate ? mount.rotation : baseRotation),
                    wx = unit.x + Angles.trnsx(rotation, x, y) + Angles.trnsx(weaponRotation, 0, -realRecoil),
                    wy = unit.y + Angles.trnsy(rotation, x, y) + Angles.trnsy(weaponRotation, 0, -realRecoil);

            if(unit.type instanceof AmmoLifeTimeUnitType)Draw.color(((AmmoLifeTimeUnitType) unit.type).ammoColor(unit));
            Draw.rect(ammoRegion, wx, wy, weaponRotation);
            Draw.color();
        }
    }
}
