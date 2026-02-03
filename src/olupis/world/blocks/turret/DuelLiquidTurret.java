package olupis.world.blocks.turret;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;

public class DuelLiquidTurret extends NyfalisLiquidTurret {
    public DuelLiquidTurret(String name){
        super(name);
    }

    @Override
    public void setBars(){
        super.setBars();
        barMap.remove("liquid");
        addLiquidBarF(build -> build.getLiquid());
        addLiquidBarFalt(build -> build.getLiquidAlt());
    }

    //Very janky i know
    public class DuelLiquidTurretBuild extends LiquidTurretBuild{

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            if(ammoTypes.get(liquid) == null) return false;
            if(getLiquid() == liquid) return true;
            if(getLiquidAlt() == liquid)return true;

            return ((liquids.get(liquid) <= 1f / ammoTypes.get(liquid).ammoMultiplier + 0.001f))
            ;
        }

        public Seq<Liquid> filterLiquid(){
            Seq<Liquid> a = new Seq<>();
            for(Entry<Liquid, BulletType> ammoType : ammoTypes) a.add(ammoType.key);
            a.removeAll(l -> liquids.get(l) <= 1f / ammoTypes.get(l).ammoMultiplier + 0.001f);
            return  a;
        }

        public Liquid getLiquid(){
            return filterLiquid().size >= 1 ? filterLiquid().first() : null;
        }

        public Liquid getLiquidAlt(){
            return filterLiquid().size >= 2 ? filterLiquid().get(1) : null;
        }

        public Liquid getLiquidAltDraw(){
            return filterLiquid().size >= 2 ? filterLiquid().get(1) : getLiquid();
        }

        @Override
        public BulletType useAmmo(){
            if(cheating()) return ammoTypes.get(getLiquid());
            BulletType type = ammoTypes.get(getLiquid());
            liquids.remove(getLiquid(), 1f / type.ammoMultiplier);

            if(getLiquidAlt() != null) liquids.remove(getLiquidAlt(), 1f / ammoTypes.get(getLiquidAlt()).ammoMultiplier);
            return type;
        }

        @Override
        public BulletType peekAmmo(){
            return getLiquid() != null ? ammoTypes.get(getLiquid()) : null;
        }

        public BulletType peekAmmoAlt(){
            return getLiquidAlt() != null ? ammoTypes.get(getLiquidAlt()) : null;
        }

        @Override
        public boolean hasAmmo(){
            return getLiquid() != null &&ammoTypes.get(getLiquid()) != null && liquids.get(getLiquid()) >= 1f / ammoTypes.get(getLiquid()).ammoMultiplier;
        }

        public boolean hasAmmoAlt(){
            return getLiquidAlt() != null && ammoTypes.get(getLiquidAlt()) != null && liquids.get(getLiquidAlt()) >= 1f / ammoTypes.get(getLiquidAlt()).ammoMultiplier;
        }

        @Override
        protected float ammoReloadMultiplier(){
            return hasAmmo() ? hasAmmoAlt() ? (peekAmmo().reloadMultiplier * ammoTypes.get(getLiquidAlt()).reloadMultiplier)/2f : peekAmmo().reloadMultiplier : 1f;
        }

        @Override
        protected void updateShooting(){

            if(reloadCounter >= reload && !charging() && shootWarmup >= minWarmup){
                BulletType type = peekAmmo();

                shoot(type);
                if(hasAmmoAlt()) shoot(peekAmmoAlt());

                reloadCounter %= reload;
            }
        }

        @Override
        public float range(){
            float change = 0f;
            if(peekAmmo() != null) Math.max(peekAmmo().rangeChange, change);
            if(peekAmmoAlt() != null) Math.max(peekAmmoAlt().rangeChange, change);
            return range + change;
        }

        @Override
        public void drawSelect(){
            if(drawMinRange){
                Drawf.dashCircle(x, y, minRange(), team.color);
            }

            if(peekAmmo() == null) return;
            if(peekAmmoAlt() == null || peekAmmo().rangeChange == peekAmmoAlt().rangeChange){
                Drawf.dashCircle(x, y, range + peekAmmo().rangeChange, team.color);
                return;
            }

            boolean alt = peekAmmo().rangeChange < peekAmmoAlt().rangeChange;
            Tmp.c1.set(team.color).a(0.6f);
            Drawf.dashCircle(x, y, range + peekAmmo().rangeChange, !alt ? team.color : Tmp.c1);
            Drawf.dashCircle(x, y, range + peekAmmoAlt().rangeChange, alt ? team.color : Tmp.c1);

            Draw.reset();
        }

    }

    /** Adds a liquid bar that dynamically displays a liquid type. */
    public <T extends DuelLiquidTurretBuild> void addLiquidBarF(Func<T, Liquid> current){
        addBar("liquid", entity -> new Bar(
        () -> current.get((T)entity) == null || entity.liquids.get(current.get((T)entity)) <= 0.001f ? Core.bundle.get("bar.liquid") : current.get((T)entity).localizedName,
        () -> current.get((T)entity) == null ? Color.clear : current.get((T)entity).barColor(),
        () -> current.get((T)entity) == null ? 0f : entity.liquids.get(current.get((T)entity)) / liquidCapacity)
        );
    }

    /** Adds a liquid bar that dynamically displays a liquid type. */
    public <T extends DuelLiquidTurretBuild> void addLiquidBarFalt(Func<T, Liquid> current){
        addBar("liquid-nyf", entity -> new Bar(
        () -> current.get((T)entity) == null || entity.liquids.get(current.get((T)entity)) <= 0.001f ? Core.bundle.get("bar.liquid") : current.get((T)entity).localizedName,
        () -> current.get((T)entity) == null ? Color.clear : current.get((T)entity).barColor(),
        () -> current.get((T)entity) == null ? 0f : entity.liquids.get(current.get((T)entity)) / liquidCapacity)
        );
    }

}
