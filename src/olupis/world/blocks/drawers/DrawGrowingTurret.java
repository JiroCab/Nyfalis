package olupis.world.blocks.drawers;

import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.world.draw.*;
import olupis.world.blocks.calyx.ineternal.*;

public class DrawGrowingTurret extends DrawTurret{
    public DrawGrowingTurret(){}

    @Override
    public void draw(Building build){
        if(build instanceof Calyxian grow){
            Draw.color(grow.colourState());
        }
        super.draw(build);
    }

}
