package olupis.world.entities.entities;

import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import olupis.content.*;
import olupis.world.entities.units.*;
import olupis.world.interfaces.*;

public class AmmoEnabledUnitClass extends UnitEntity implements AmmoNyf{

    @Override
    public int classId(){
        return NyfUnitTeamMapper.ammoEnbaled;
    }


    public static AmmoEnabledUnitClass create() {
        return new AmmoEnabledUnitClass();
    }

    protected AmmoEnabledUnitClass(){
        super();
    }

    public float ammo;
    public int prevParentId;
    public @Nullable Teamc parent;

    @Override
    public void write(Writes write){
        super.write(write);
        write.f(ammo);
        write.i(parent == null ?  -1 :parent.id());
    }

    @Override
    public void read(Reads read){
        super.read(read);
        ammo = read.f();
        prevParentId = read.i();
    }

    @Override
    public void afterReadAll(){
        super.afterReadAll();
        if(prevParentId != -1 )parent = Groups.unit.getByID(prevParentId);
    }

    @Override
    public void setAmmo(float ammo){
        this.ammo = ammo;
    }

    @Override
    public void fillAmmo(){
        if(ammoCapacity() > 0) this.ammo = ammoCapacity();
    }

    @Override
    public float currentAmmo(){
        if(ammoCapacity() == -1) return 0;
        return ammo;
    }

    @Override
    public float ammoCapacity(){
        if(!(type instanceof AmmoEnabledUnitType ny)) return -1;
        return ny.ammoCapacity;
    }

    public float ammof(){
        return  currentAmmo() / ammoCapacity();
    }

    @Override
    public boolean isDepleted(){
        return ammoCapacity() > 0 && ammo <= ammoCapacity();
    }

    @Override
    public boolean shouldRetreat(){
        return type instanceof AmmoEnabledUnitType ny &&(ny.setRetreat ? ny.minRetreatAmmo : ammoCapacity() * ny.minRetreatAmmo) >= ammo;
    }

    @Override
    public boolean ammoBounded(){
        return type instanceof AmmoLifeTimeUnitType na && na.killOnAmmoDepletion;
    }

    @Override
    public Teamc parent(){
        return parent;
    }

    @Override
    public void setParent(Teamc parent){
        this.parent = parent;
    }
}
