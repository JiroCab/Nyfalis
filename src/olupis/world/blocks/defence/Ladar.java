package olupis.world.blocks.defence;

import arc.math.Mathf;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.Radar;

public class Ladar extends Radar {

   public Ladar(String name){
       super(name);
   }

    public void setBars() {
        super.setBars();
        addBar("bar.progress", (RadarBuild entity) -> new Bar("bar.loadprogress", Pal.ammo, () -> entity.progress));
    }

    public class LadarBuild extends RadarBuild{
        @Override
        public void updateTile(){
           super.updateTile(); //cant be bothered
        }

        @Override
        public void drawLight() {
            if(emitLight) Drawf.light(x, y, Mathf.lerp(0, lightRadius, progress), lightColor, lightColor.a);
            super.drawLight();
        }
    }

}
