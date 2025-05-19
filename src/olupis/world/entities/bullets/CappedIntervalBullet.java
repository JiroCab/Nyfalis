package olupis.world.entities.bullets;

import mindustry.entities.bullet.*;
import mindustry.gen.*;

import java.util.*;

public class CappedIntervalBullet extends BasicBulletType{
     public int IntervalCap = 2;
    HashMap<Integer, Integer> counts = new HashMap<>();

    public CappedIntervalBullet(float speed, float damage, String bulletSprite){
        super(speed, damage, bulletSprite);
    }

    public CappedIntervalBullet(float speed, float damage){
        this(speed, damage, "bullet");
    }

    public CappedIntervalBullet(){
        this(1f, 1f, "bullet");
    }

    @Override
    public void update(Bullet b){
        if (!counts.containsKey(b.id) || counts.get(b.id) == null) counts.put(b.id, 0);
        super.update(b);

    }

    @Override
    public void updateBulletInterval(Bullet b){
        if(counts.get(b.id) >= IntervalCap) return;

        counts.replace(b.id, counts.get(b.id) + 1);
        super.updateBulletInterval(b);

    }
}
