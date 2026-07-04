package olupis.world.entities.entities;

import arc.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import olupis.content.*;
import olupis.world.*;
import olupis.world.entities.units.*;
import olupis.world.interfaces.*;

public class AmmoEnabledUnitClass extends UnitEntity implements AmmoNyf{

    @Override
    public int classId(){
        return NyfUnitTeamMapper.ammoEnabled;
    }


    public static AmmoEnabledUnitClass create() {
        return new AmmoEnabledUnitClass();
    }

    protected AmmoEnabledUnitClass(){
        super();
    }

    public int ammo, ammoTime;
    public int prevParentId;
    public @Nullable Teamc parent;

    @Override
    public void write(Writes write){
        super.write(write);
        write.i(ammo);
        write.i(parent == null ?  -1 :parent.id());
        write.i(ammoTime);
    }

    @Override
    public void read(Reads read){
        super.read(read);
        ammo = read.i();
        prevParentId = read.i();
        if(Core.settings.getBool("nyf-debug-read-float")) ammoTime = Math.round(read.f());
        else ammoTime = ammoTimeOffset();
    }

    @Override
    public void afterReadAll(){
        super.afterReadAll();
        if(prevParentId != -1 )parent = Groups.unit.getByID(prevParentId);
    }

    @Override
    public void setAmmo(int ammo){
        this.ammo = ammo;
    }

    @Override
    public void resupply(int mult){
        setAmmo(Mathf.clamp(currentAmmo() + (mult * ammoCapacity()), 0,ammoCapacity()));
    }

    @Override
    public void resupplyAdd(int add){
        setAmmo(Mathf.clamp(currentAmmo() + add,0, ammoCapacity()));
    }

    @Override
    public void fillAmmo(){
        if(ammoCapacity() > 0) this.ammo = ammoCapacity();
    }

    @Override
    public int currentAmmo(){
        if(ammoCapacity() == -1) return 0;
        return ammo;
    }

    @Override
    public int ammoCapacity(){
        if(!(type instanceof AmmoEnabledUnitType ny)) return -1;
        return ny.ammoCapacity;
    }

    public float ammof(){
        return Mathf.clamp((float)currentAmmo() / ammoCapacity());
    }

    @Override
    public boolean isDepleted(){
        return ammoCapacity() > 0 && ammo <= ammoCapacity();
    }

    @Override
    public boolean shouldRetreat(){
        return type instanceof AmmoEnabledUnitType ny && ny.shouldRetreat(currentAmmo());
    }

    @Override
    public boolean ammoBounded(){
        return type instanceof AmmoLifeTimeUnitType na && na.killOnAmmoDepletion;
    }

    @Override
    public String ammoType() {
        return type instanceof AmmoEnabledUnitType na ? na.ammoType : "";
    }

    @Override
    public Teamc parent(){
        return parent;
    }

    @Override
    public void setParent(Teamc parent){
        this.parent = parent;
    }

    @Override
    public Unit unit(){
        return this;
    }

    @Override
    public void callTimeOut(){
        NyfWorldFuckingHelper.callTimeOut(this);
    }

    @Override
    public void setAmmoTime(int time){ ammoTime = time;}

    @Override
    public int ammoTime(){return ammoTime;}

    @Override
    public int ammoTimeOffset(){ return type instanceof  AmmoLifeTimeUnitType atl ? atl.ammoDepletionOffset:  -1;}


}
