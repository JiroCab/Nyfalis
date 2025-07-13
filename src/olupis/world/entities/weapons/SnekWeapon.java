package olupis.world.entities.weapons;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.part.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

/*Very janky but gets the work done*
I can see why the base game doesnt have this*/
public class SnekWeapon extends NyfalisWeapon {
    /*Sets which segment the weapon is "mounted" to*/
    public float weaponSegmentParent = 3;

    public SnekWeapon(String name){
        super(name);
    }
    public SnekWeapon(String name, boolean boostShoot, boolean groundShoot ){
        super(name, boostShoot, groundShoot);
    }
    public SnekWeapon(){
        super("");
    }

    @Override
    public void init(){
        super.init();
    }

    @Override
    public void update(Unit unit, WeaponMount mount) {

        if ( altWeaponPos && unit instanceof Crawlc crawl) {
            UnitType type = unit.type;
            float trns = Mathf.sin(crawl.crawlTime() + weaponSegmentParent * type.segmentPhase, type.segmentScl, type.segmentMag),
                    rot = Mathf.slerp(crawl.segmentRot(), unit.rotation, weaponSegmentParent / (type.segments - 1f)),
                    tx = Angles.trnsx(rot, trns), ty = Angles.trnsy(rot, trns), rotation = unit.rotation - 90,
                    weaponRotation = rotation + (rotate ? mount.rotation : baseRotation);
            shootXf  = tx + unit.x + Angles.trnsx(rotation, x, y) + Angles.trnsx(weaponRotation, this.shootX, this.shootY);
            shootYf  = ty + unit.y + Angles.trnsy(rotation, x, y) + Angles.trnsy(weaponRotation, this.shootX, this.shootY);
        }
        super.update(unit, mount);
    }

    @Override
    public void draw(Unit unit, WeaponMount mount){
        updateParams(unit);
        if (unit instanceof Crawlc crawl) {
            //apply layer offset, roll it back at the end
            float z = Draw.z();
            Draw.z(z + layerOffset);
            UnitType type = unit.type;

            float wr = rotate ? mount.rotation : baseRotation, rotation = unit.rotation - 90,
                    trns = Mathf.sin(crawl.crawlTime() + weaponSegmentParent * type.segmentPhase, type.segmentScl, type.segmentMag),
                    rot = Mathf.slerp(crawl.segmentRot(), unit.rotation, weaponSegmentParent / (type.segments - 1f)),
                    tx = Angles.trnsx(rot, trns), ty = Angles.trnsy(rot, trns),
                    realRecoil = Mathf.pow(mount.recoil, recoilPow) * recoil,
                    weaponRotation = rotation + wr,
                    wx = tx + unit.x + Angles.trnsx(rotation, x, y) + Angles.trnsx(weaponRotation, 0, -realRecoil),
                    wy = ty + unit.y +  Angles.trnsy(rotation, x, y) + Angles.trnsy(weaponRotation, 0, -realRecoil);

            if (shadow > 0) {
                Drawf.shadow(wx, wy, shadow);
            }

            if (top && outlineRegion.found()){
                Draw.xscl = -Mathf.sign(flipSprite);
                Draw.rect(outlineRegion, wx, wy, weaponRotation);
                Draw.xscl = 1f;
            }

            if (parts.size > 0) {
                DrawPart.params.set(mount.warmup, mount.reload / reload, mount.smoothReload, mount.heat, mount.recoil, mount.charge, wx, wy, weaponRotation + 90);
                DrawPart.params.sideMultiplier = flipSprite ? -1 : 1;

                for (int i = 0; i < parts.size; i++) {
                    var part = parts.get(i);
                    DrawPart.params.setRecoil(part.recoilIndex >= 0 && mount.recoils != null ? mount.recoils[part.recoilIndex] : mount.recoil);
                    if (part.under) {
                        part.draw(DrawPart.params);
                    }
                }
            }

            Draw.xscl = -Mathf.sign(flipSprite);

            //fix color
            unit.type.applyColor(unit);

            if (region.found()) Draw.rect(region, wx, wy, weaponRotation);

            if (cellRegion.found()) {
                Draw.color(unit.type.cellColor(unit));
                Draw.rect(cellRegion, wx, wy, weaponRotation);
                Draw.color();
            }

            if (heatRegion.found() && mount.heat > 0) {
                Draw.color(heatColor, mount.heat);
                Draw.blend(Blending.additive);
                Draw.rect(heatRegion, wx, wy, weaponRotation);
                Draw.blend();
                Draw.color();
            }

            Draw.xscl = 1f;

            if (parts.size > 0) {
                //TODO does it need an outline?
                for (int i = 0; i < parts.size; i++) {
                    var part = parts.get(i);
                    DrawPart.params.setRecoil(part.recoilIndex >= 0 && mount.recoils != null ? mount.recoils[part.recoilIndex] : mount.recoil);
                    if (!part.under) {
                        part.draw(DrawPart.params);
                    }
                }
            }

            Draw.xscl = 1f;

            Draw.z(z);
        }else super.draw(unit, mount);

    }
}
