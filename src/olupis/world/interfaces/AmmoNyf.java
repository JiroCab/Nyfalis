package olupis.world.interfaces;

import arc.util.*;
import mindustry.gen.*;

public interface AmmoNyf extends Entityc{
    public float currentAmmo();

    public float ammoCapacity();

    public void setAmmo(float ammo);

    public void fillAmmo();

    public float ammof();

    public boolean isDepleted();

    public boolean ammoBounded();

    public boolean shouldRetreat();

    public @Nullable Teamc parent();

    public void setParent(Teamc parent);

}
