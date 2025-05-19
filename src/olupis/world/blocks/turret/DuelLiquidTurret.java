package olupis.world.blocks.turret;

import arc.struct.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.type.*;

public class DuelLiquidTurret extends NyfalisLiquidTurret {
    public DuelLiquidTurret(String name){
        super(name);
    }

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
            ammoTypes.forEach( (l) -> a.add(l.key));
            a.removeAll(l -> liquids.get(l) <= 1f / ammoTypes.get(l).ammoMultiplier + 0.001f);
            return  a;
        }

        public Liquid getLiquid(){
            return filterLiquid().size >= 1 ? filterLiquid().first() : null;
        }

        public Liquid getLiquidAlt(){
            return filterLiquid().size >= 2 ? filterLiquid().get(1) : null;
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
            return getLiquid() != null ? ammoTypes.get(getLiquidAlt()) : null;
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
    }
}
