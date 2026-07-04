package olupis.world.interfaces;

import arc.util.*;
import mindustry.gen.*;

public interface AmmoNyf extends Entityc{
    public int currentAmmo();

    public int ammoCapacity();

    public void setAmmo(int ammo);

    public void resupply(int mult);

    public void resupplyAdd(int add);

    public void fillAmmo();

    public float ammof();

    public boolean isDepleted();

    public boolean ammoBounded();

    public String ammoType();

    public boolean shouldRetreat();

    public @Nullable Teamc parent();

    public void setParent(Teamc parent);

    public Unit unit();

    public void callTimeOut();

    public void setAmmoTime(int time);

    public int ammoTime();

    public int ammoTimeOffset();

}
