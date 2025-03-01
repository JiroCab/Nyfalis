package olupis.world.entities.bullets;

import arc.util.noise.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.*;

public class FirePuddleBulletType extends MultiShockBulletType{
    boolean removeInstead = false;

    public FirePuddleBulletType(float splashDamage, float radius) {
        super(splashDamage, radius);
        applySound = Sounds.none;
        particleEffect = Fx.none;
        drawBlast = false;
    }

    public FirePuddleBulletType(float splashDamage, float radius, boolean removeInstead) {
        super(splashDamage, radius);
        this.removeInstead = removeInstead;

    }

    public void update(Bullet b) {
        super.update(b);
        int tx = b.tileX();
        int ty = b.tileY();
        int rad = Math.max((int) (splashDamageRadius / 8.0F * 1.75), 1);
        float realNoise = splashDamageRadius / 5;

        for (int x = -rad; x <= rad; ++x) {
            for (int y = -rad; y <= rad; ++y) {
                if ((float) (x * x + y * y) <= (float) (rad * rad) - Simplex.noise2d(0, 2.0, 0.5, (double) (1.0F / 5), (double) (x + tx), (double) (y + ty)) * realNoise * realNoise) {
                    Tile tile = Vars.world.tile(tx + x, ty + y);
                    if (tile != null) {
                        if(removeInstead) Fires.extinguish(tile, 100f);
                        else Fires.create(tile);
                    }
                }
            }
        }
    }
}
