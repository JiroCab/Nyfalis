package olupis.world.blocks.defence;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.units.*;
import mindustry.world.meta.*;
import olupis.content.*;

public class UnitRailingRepairTurret extends RepairTurret {
    /*Yes, we *rail* units in order to repair them ;3 */
    static final Rect rect = new Rect();

    public TextureRegion heatRegion;
    public Color heatColor = Pal.turretHeat;
    /** ticks to cool down the heat region */
    public float cooldownTime = 20f;

    public float reload = 100f, statusDuration = 60f * 6f, shootX = 0f, shootY = 0f;
    public Effect fireFx = NyfalisFxs.shootRepairPin, lineFx = NyfalisFxs.shootRepairPinBeam, hitFx = NyfalisFxs.hitRepairPinSpark;
    public StatusEffect healStatus = StatusEffects.none;


    public UnitRailingRepairTurret(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.repairSpeed);
        stats.add(Stat.repairSpeed,60f / (repairSpeed * (60f / (reload))), StatUnit.perShot);
    }


    @Override
    public void init(){
        if(cooldownTime < 0f) cooldownTime = reload * 0.5f;

        super.init();
    }
    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find(minfo.mod.name + "-iron-block-" + size);
        heatRegion = Core.atlas.find(name + "-heat");
    }


    public class UnitRailingTurretBuild extends RepairTurret.RepairPointBuild {
        public float heat;
        public float reloadTimer;
        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);

            Draw.z(Layer.turret);
            Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);
            Draw.rect(region, x, y, rotation - 90);

            if(heatRegion.found() && heat > 0){
                Draw.color(heatColor, heat);
                Draw.blend(Blending.additive);
                Draw.rect(heatRegion, x, y, rotation - 90);
                Draw.blend();
                Draw.color();
            }
        }


        @Override
        public void updateTile(){
            float multiplier = 1f;
            if(acceptCoolant){
                multiplier = 1f + liquids.current().heatCapacity * coolantMultiplier * optionalEfficiency;
            }

            if(target != null && (target.dead() || target.dst(this) - target.hitSize/2f > repairRadius || target.health() >= target.maxHealth())){
                target = null;
            }

            if(target == null){
                offset.setZero();
            }

            if(target != null && efficiency > 0){
                float angle = Angles.angle(x, y, target.x + offset.x, target.y + offset.y);
                if(Angles.angleDist(angle, rotation) < (target.hitSize() * 0.9f) && (reloadTimer += (Time.delta * multiplier)) >= reload){
                    shoot();
                }
                rotation = Mathf.slerpDelta(rotation, angle, 0.5f * efficiency * timeScale);
            }

            if(timer(timerTarget, 20)){
                rect.setSize(repairRadius * 2).setCenter(x, y);
                target = Units.closest(team, x, y, repairRadius, Unit::damaged);
            }
            heat = Mathf.approachDelta(heat, 0, 1 / cooldownTime);
        }

        public void shoot(){
            target.heal(repairSpeed * edelta());
            reloadTimer = 0f;
            heat = 1f;

            float xf = x + Angles.trnsx(rotation - 90, shootX, shootY),
                    yf = y + Angles.trnsy(rotation - 90, shootX, shootY);
            boolean onTop = !target.within(x, y, size);

            if(onTop)fireFx.at(xf, yf, rotation, Pal.heal);
            else fireFx.at(target.x, target.y, rotation, Pal.heal);

            if(lineFx != Fx.none && onTop) lineFx.at(xf, yf, rotation, Pal.heal, new Vec2().set(target));
            if(hitFx != Fx.none) hitFx.at(target.x, target.y,  target.rotation, Pal.heal);

            if(healStatus != StatusEffects.none) target.apply(healStatus, statusDuration);
        }
    }
}
