package olupis.world.entities.bullets;

import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static arc.Core.atlas;

public class ShappedBulletType extends BulletType{
    public float widthIn = 3, heightIn = 3, widthOut = 4, heightOut = 4, rotIn = 45, rotOut = rotIn;
    public boolean drawOut = true;
    public Color colourIn = trailColor, colourOut = trailColor;
    public int shapeIn = 0, shapeOut = 0;

    @Override
    public void draw(Bullet b){
        shape(b, true);
        shape(b,false);
        Draw.reset();
        super.draw(b);
    }

    public void shape(Bullet b, boolean outer){
        if(!drawOut && outer) return;
        float w = outer ? widthOut : widthIn;
        float h = outer ? heightOut : heightIn;
        float r = outer ? rotOut : rotIn;
        int s = outer ? shapeOut : shapeIn;
        Color c = outer ? colourOut : colourIn;


        Lines.stroke(s == 1? h : 1f, c);
        switch(s){
            case 2 -> Drawf.tri(b.x, b.y, w, h, b.rotation() + r);
            case 1 -> Lines.circle(b.x, b.y, w);
            case 0 -> Draw.rect(atlas.white(), b.x, b.y, w, h, b.rotation() + r);
        }


    }
}
