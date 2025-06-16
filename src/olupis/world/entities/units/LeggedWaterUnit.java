package olupis.world.entities.units;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.entities.Leg;
import mindustry.gen.Legsc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

public class LeggedWaterUnit extends  AmmoLifeTimeUnitType  {
    private static final Vec2 legOffset = new Vec2();
    public float navalSpeed = speed, deepSpeed = -1;
    public boolean showLegsOnLiquid = true, showLegsOnDeepLiquid = showLegsOnLiquid, lockLegsOnLiquid = true,
            floaterOnHiddenLegs = false,
            boostUsesNaval, customShadow = false
    ;

    public LeggedWaterUnit(String name){
        super(name);
        speed = navalSpeed;
        canDrown = false;
    }

    @Override
    public void init(){
        super.init();
    }

    @Override
    public void load(){
        super.load();
        if(customShadow) softShadowRegion = Core.atlas.find("olupis-shadow-long");
    }

    @Override
    public <T extends Unit & Legsc> void drawLegs(T unit){
        if(!showLegsOnDeepLiquid && onDeepWater(unit)){
            drawFloaters(unit);
            return;
        }
        if(!showLegsOnLiquid && onWater(unit)){
            drawFloaters(unit);
            return;
        }

        applyColor(unit);
        Tmp.c3.set(Draw.getMixColor());

        Leg[] legs = unit.legs();

        float ssize = footRegion.width * footRegion.scl() * 1.5f;
        float rotation = unit.baseRotation();
        float invDrown = 1f - unit.drownTime;

        if(footRegion.found()){
            for(Leg leg : legs){
                Drawf.shadow(leg.base.x, leg.base.y, ssize, invDrown);
            }
        }

        //legs are drawn front first
        for(int j = legs.length - 1; j >= 0; j--){
            int i = (j % 2 == 0 ? j/2 : legs.length - 1 - j/2);
            Leg leg = legs[i];
            boolean flip = i >= legs.length/2f;
            int flips = Mathf.sign(flip);

            Vec2 position = unit.legOffset(legOffset, i).add(unit);

            Tmp.v1.set(leg.base).sub(leg.joint).inv().setLength(legExtension);

            if(footRegion.found() && leg.moving && shadowElevation > 0){
                float scl = shadowElevation * invDrown;
                float elev = Mathf.slope(1f - leg.stage) * scl;
                Draw.color(Pal.shadow);
                Draw.rect(footRegion, leg.base.x + shadowTX * elev, leg.base.y + shadowTY * elev, position.angleTo(leg.base));
                Draw.color();
            }

            Draw.mixcol(Tmp.c3, Tmp.c3.a);

            if(footRegion.found()){
                Draw.rect(footRegion, leg.base.x, leg.base.y, position.angleTo(leg.base));
            }

            Lines.stroke(legRegion.height * legRegion.scl() * flips);
            Lines.line(legRegion, position.x, position.y, leg.joint.x, leg.joint.y, false);

            Lines.stroke(legBaseRegion.height * legRegion.scl() * flips);
            Lines.line(legBaseRegion, leg.joint.x + Tmp.v1.x, leg.joint.y + Tmp.v1.y, leg.base.x, leg.base.y, false);

            if(jointRegion.found()){
                Draw.rect(jointRegion, leg.joint.x, leg.joint.y);
            }
        }

        //base joints are drawn after everything else
        if(baseJointRegion.found()){
            for(int j = legs.length - 1; j >= 0; j--){
                //TODO does the index / draw order really matter?
                Vec2 position = unit.legOffset(legOffset, (j % 2 == 0 ? j/2 : legs.length - 1 - j/2)).add(unit);
                Draw.rect(baseJointRegion, position.x, position.y, rotation);
            }
        }

        if(baseRegion.found()){
            Draw.rect(baseRegion, unit.x, unit.y, rotation - 90);
        }

        Draw.reset();
    }

    public <T extends Unit & Legsc> void drawFloaters(T unit){
        if(!floaterOnHiddenLegs) return;
        applyColor(unit);
        Draw.rect(treadRegion, unit.x, unit.y, unit.rotation - 90);
        Draw.reset();
    }
}
