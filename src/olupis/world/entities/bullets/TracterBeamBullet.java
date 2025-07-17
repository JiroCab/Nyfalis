package olupis.world.entities.bullets;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import olupis.world.entities.status.*;

public class TracterBeamBullet extends ContinuousLaserBulletType {
    public TextureRegion laserEndSprite;
    public TextureRegion laserStartSprite;
    public TextureRegion laserSprite;
    public String laserEnd = "olupis-tractor-end";
    public String laserStart = laserEnd;
    public String laser = "olupis-tractor-laser";
    public boolean statusOnOwner = false;
    public StatusEffect ownerStatus = status;
    public float ownerStatusDuration = statusDuration;
    public float absScl = 4f, absMag = 0.6f;

    public TracterBeamBullet(float damage){
        this.damage = damage;
    }

    public TracterBeamBullet(){

    }


    @Override
    public void draw(Bullet b){
        float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);

        if(b.data instanceof  Position data){
            Tmp.v2.set(data);
            for(int i = 0; i < colors.length; i++){
                Draw.color(Tmp.c1.set(colors[i]));

                Draw.mixcol(colors[i], Mathf.absin(absScl, absMag));
                float stroke = (width) * fout ;
                Lines.stroke(stroke);

                Drawf.laser(laserSprite, laserStartSprite, laserEndSprite,
                        b.x, b.y, Tmp.v2.x,  Tmp.v2.y,
                        stroke);
                Draw.rect(laserEndSprite, Tmp.v2.x,  Tmp.v2.y, laserEndSprite.width * stroke * laserEndSprite.scl(), laserEndSprite.height * stroke * laserEndSprite.scl(), b.rotation() + 180);
            }
            Draw.reset();
            Drawf.light(b.x, b.y, Tmp.v2.x,  Tmp.v2.y, lightStroke, lightColor, 0.7f);

        }
        Draw.reset();
    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), length);
        boolean check = target instanceof  Hitboxc || target instanceof  Building;
        b.data = check ? target : new Vec2().trns(b.rotation(), length).add(b.x, b.y);
    }



    @Override
    public void load() {
        super.load();
        laserEndSprite = Core.atlas.find(laserEnd);
        laserStartSprite = Core.atlas.find(laserStart);
        laserSprite = Core.atlas.find(laser);
    }

    @Override
    public void applyDamage(Bullet b){
        if(status instanceof TrackedSatusEffect ts && b.owner instanceof Unit own && b.data instanceof Unit tar){
            ObjectFloatMap<Unit> ref = ts.valueMap.get(tar, ObjectFloatMap::new); // though wait let me check something in my older code
            if(ref.get(own, -1) <= 0) ref.put(own, Time.time + statusDuration);
        }

        if(b.data instanceof Hitboxc hit){
            hit.collision(b, hit.x(), hit.y());
            b.collision(hit, hit.x(), hit.y());

            if(statusOnOwner && b.owner instanceof Statusc s) s.apply(ownerStatus, ownerStatusDuration); //only apply status on hit
        }else if(b.data instanceof Building tile){
            if(tile.collide(b)){
                tile.collision(b);
                hit(b, tile.x, tile.y);

                if(statusOnOwner && b.owner instanceof Statusc s) s.apply(ownerStatus, ownerStatusDuration); //only apply status on hit
            }
        }
    }
}
