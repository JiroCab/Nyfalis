package olupis.world.entities.entities;

import arc.math.geom.*;
import mindustry.gen.*;
import olupis.content.*;

public class SnekUnitClass extends CrawlUnit{

    @Override
    public int classId(){
        return NyfUnitMapper.tonkNaval;
    }

    @Override
    public void hitbox(Rect rect){
        super.hitbox(rect);
    }

    @Override
    public boolean collides(Hitboxc other){
        return super.collides(other);
    }

    @Override
    public void collision(Hitboxc other, float x, float y){
        super.collision(other, x, y);
    }
}
