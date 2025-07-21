package olupis.world.entities.bullets;

import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

import static olupis.world.EnvUpdater.*;

public class MossRemoverBullet extends BulletType{

    public MossRemoverBullet(){{
        trailEffect = despawnEffect = smokeEffect = shootEffect = hitEffect =  Fx.none;
        killShooter = collidesAir = hittable = collides = keepVelocity = false;
        instantDisappear = true;
        lifetime = 1f;
        speed = 0f;
        rangeOverride = 20f;
    }}

    @Override
    public void update(Bullet b){
        super.update(b);

        resetTile(b.tileOn());
        b.remove();
    }
}
