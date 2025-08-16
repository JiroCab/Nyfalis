package olupis.world.entities.weapons;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.part.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import olupis.world.entities.units.*;

/*Very janky but gets the work done*
I can see why the base game doesnt have this*/
public class SnekWeapon extends NyfalisWeapon {
    /*Sets which segment the weapon is "mounted" to*/
    public int weaponSegmentParent = 3;

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
    public void draw(Unit unit, WeaponMount mount){
        updateParams(unit);

        if(!(unit instanceof Crawlc crawl) || !(unit.type instanceof SnekUnitType type)){
            super.draw(unit, mount);
        }else{

            //apply layer offset, roll it back at the end
            float z = Draw.z();
            Draw.z(z + layerOffset);

            float wr = rotate ? mount.rotation : baseRotation, rotation = unit.rotation - 90,
                    realRecoil = Mathf.pow(mount.recoil, recoilPow) * recoil,
                    weaponRotation = rotation + wr,

                    wx = seg(unit)[0] + unit.x + Angles.trnsx(rotation, x, y) + Angles.trnsx(weaponRotation, 0, -realRecoil),
                    wy = seg(unit)[1] + unit.y +  Angles.trnsy(rotation, x, y) + Angles.trnsy(weaponRotation, 0, -realRecoil);

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
        }

    }

    public float[] seg (Unit unit){
        if (unit instanceof Crawlc crawl && unit.type instanceof  SnekUnitType type) {
            float  off = type.alternateDraw ? type.sinOffset : 0,
            crawlTime = crawl instanceof Segmentc seg && seg.headSegment() instanceof Crawlc head ? head.crawlTime() + seg.segmentIndex() * type.segmentPhase * type.segments* type.crawlTimeMul : crawl.crawlTime() * type.crawlTimeMul,
            trns = Mathf.sin(crawlTime + weaponSegmentParent * type.segmentPhase, type.segmentScl, type.segmentMag),
            rot = Mathf.slerp(crawl.segmentRot(), unit.rotation, weaponSegmentParent / (type.segments - 1f)),
            so = type.alternateDraw ? type.spriteOffsets[weaponSegmentParent] : 0;
            return new float[]{
                Angles.trnsx(rot + off, trns) + Angles.trnsx(rot, so),
                Angles.trnsy(rot + off, trns) + Angles.trnsy(rot, so)
            };
        } return new float[]{0, 0};
    }


    @Override
    public float mountX(Unit unit){
        return super.mountX(unit) + seg(unit)[0];
    }

    @Override
    public float mountY(Unit unit){
        return super.mountY(unit) + seg(unit)[1];
    }
}
