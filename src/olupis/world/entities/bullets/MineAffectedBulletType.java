package olupis.world.entities.bullets;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.type.*;

public class MineAffectedBulletType extends BasicBulletType{

    public MineAffectedBulletType(float speed, float damage, String bulletSprite){
        super(speed, damage, bulletSprite);
    }

    public MineAffectedBulletType(float speed, float damage){
        super(speed, damage);
    }

    @Override
    public void draw(Bullet b){

        if(b.data instanceof Item itm){
            float shrink = shrinkInterp.apply(b.fout());
            float height = this.height * ((1f - shrinkY) + shrinkY * shrink);
            float width = this.width * ((1f - shrinkX) + shrinkX * shrink);
            float offset = -90 + (spin != 0 ? Mathf.randomSeed(b.id, 360f) + b.time * spin : 0f) + rotationOffset;

            Color mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin());

            Draw.mixcol(mix, mix.a);

            if(backRegion.found()){
                Draw.color(new Color().set(backColor).lerp(itm.color, 0.6f));
                Draw.rect(backRegion, b.x, b.y, width, height, b.rotation() + offset);
            }
            Draw.color(new Color().set(frontColor).lerp(itm.color, 0.6f));
            Draw.rect(frontRegion, b.x, b.y, width, height, b.rotation() + offset);

            Draw.reset();

            drawTrail(b);
            drawParts(b);
        }else super.draw(b);
    }
}
