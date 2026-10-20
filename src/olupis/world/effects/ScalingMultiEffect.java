package olupis.world.effects;

import arc.graphics.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.effect.*;

public class ScalingMultiEffect extends MultiEffect{
    /*Note: negatives are handles as <= otherwise >= is used */
    public Float[] scales = {};

    public ScalingMultiEffect(Effect... eff){
        this.effects = eff;
        scales = new Float[eff.length];
        for(int i = 0; i < eff.length; i++) scales[i] = 0.25f;
    }

    public ScalingMultiEffect(Object... items){
        Seq<Effect>outFx = new Seq<>();
        Seq<Float> outScl = new Seq<>();

        for(int i = 0; i < items.length; i++){
            if(items[i] == null) break;

            if(items[i] instanceof Effect effect){
                outFx.add(effect);

                if(items[i + 1] != null && items[i + 1] instanceof Float fl){
                    outScl.add(fl);
                    i++;
                }
                else outScl.add(0.25f);
            } else Log.err(items[i] + ": is not a valid effect/not formatted correctly :p");

        }

        effects = new Effect[outFx.size];
        for(int i = 0; i < outFx.size; i++){
            effects[i] = outFx.get(i);
        }

        scales = new Float[outScl.size];
        for(int i = 0; i < outScl.size; i++){
            scales[i] = outScl.get(i);
        }
    }

    @Override
    public void create(float x, float y, float rotation, Color color, Object data){
        if(!shouldCreate()) return;

        for(int i = 0; i < effects.length; i++){
            var effect = effects[i];

            if(data instanceof Float f){
                if(scales[i] >= 0){
                    if(f >= scales[i]) effect.create(x, y, rotation, color, data);
                } else if(Math.abs(scales[i]) >= f){
                    effect.create(x, y, rotation, color, (data));
                }
            }
        }
    }



}
