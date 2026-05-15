package olupis.world.blocks.drawers;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import olupis.world.blocks.processing.BurstPump.*;

public class DrawImpactPumpArrow extends DrawBlock{
    public TextureRegion arrowRegion;
    public  TextureRegion arrowBlurRegion;
    public Color arrowColor = Color.valueOf("feb380"), baseArrowColor = Color.valueOf("6e7080");
    public float arrowSpacing = 4f, arrowOffset = 0f;
    public int arrows = 3;

    @Override
    public void draw(Building build){
        if(!(build instanceof BurstPumpBuild pump) || pump.liquidDrop == null) return;

        float fract = pump.smoothProgress;
        Draw.color(arrowColor);

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < arrows; j++){
                float arrowFract = (arrows - 1 - j);
                float a = Mathf.clamp(fract * arrows - arrowFract);
                Tmp.v1.trns(i * 90 + 45, j * arrowSpacing + arrowOffset);

                //TODO maybe just use arrow alpha and draw gray on the base?
                Draw.z(Layer.block);
                Draw.color(baseArrowColor, arrowColor, a);
                Draw.rect(arrowRegion, pump.x + Tmp.v1.x, pump.y + Tmp.v1.y, i * 90);

                Draw.color(arrowColor);

                if(arrowBlurRegion.found()){
                    Draw.z(Layer.blockAdditive);
                    Draw.blend(Blending.additive);
                    Draw.alpha(Mathf.pow(a, 10f));
                    Draw.rect(arrowBlurRegion, pump.x + Tmp.v1.x, pump.y + Tmp.v1.y, i * 90);
                    Draw.blend();
                }
            }
        }
        Draw.color();
        Draw.reset();
    }

    @Override
    public void load(Block block){
        arrowRegion = Core.atlas.find(block.name + "-arrow");
        arrowBlurRegion = Core.atlas.find(block.name + "-arrow-blur");
    }
}
