package olupis.world.entities.bullets;

import arc.func.*;
import arc.math.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class ConditionalIntervalBulletType extends BasicBulletType{
    public Boolf<Bullet> intervalCheck = (b) -> true;

    public ConditionalIntervalBulletType(float speed, float damage, String bulletSprite) {
        super(speed, damage, bulletSprite);
    }
    public ConditionalIntervalBulletType(float speed, float damage) {
        super(speed, damage);
    }

    @Override
    public void updateBulletInterval(Bullet b) {
        if(!intervalCheck.get(b)) return;

        if (this.intervalBullet != null && b.time >= this.intervalDelay && b.timer.get(2, this.bulletInterval)) {
            float ang = b.rotation();

            for(int i = 0; i < this.intervalBullets; ++i) {
                this.intervalBullet.create(b, b.x, b.y, ang + Mathf.range(this.intervalRandomSpread) + this.intervalAngle + ((float)i - ((float)this.intervalBullets - 1.0F) / 2.0F) * this.intervalSpread);
            }
        }
    }
}
