package olupis.world.blocks.defence;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.defense.*;

public class Ladar extends Radar {
    public  boolean spotlight = false;
    public float spotRadius  = 100f, minProgress = 0.7f, spottedDuration = 4f;
    public StatusEffect spotted = StatusEffects.none;
    public Ladar(String name){
       super(name);
   }

    public void setBars() {
        super.setBars();
        addBar("bar.progress", (RadarBuild entity) -> new Bar("bar.loadprogress", Pal.ammo, () -> entity.progress));
    }

    public class LadarBuild extends RadarBuild{
        public @Nullable Vec2 tar = new Vec2();

        @Override
        public void updateTile(){
            if(spotlight){
                Unit uf = Units.bestEnemy(team, this.x, this.y, fogRadius * Vars.tilesize, u -> !u.dead, UnitSorts.strongest);
                if(uf != null){
                    tar.set(uf.x, uf.y);
                    if(spotted != StatusEffects.none) uf.apply(spotted, spottedDuration);
                }else tar.set(-1, -1);
            }
            super.updateTile(); //cant be bothered
        }

        @Override
        public void drawLight(){
            if(emitLight) Drawf.light(x, y, Mathf.lerp(0, lightRadius, progress), lightColor, lightColor.a);
            if(spotlight && tar != null && tar.x != -1 && tar.y != -1 && progress >= minProgress){
                Drawf.light(tar.x, tar.y, Mathf.lerp(0, spotRadius, progress), team.color, 0.8f);
                super.drawLight();
            }
        }
    }
}
