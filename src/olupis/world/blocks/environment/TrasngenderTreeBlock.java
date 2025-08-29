package olupis.world.blocks.environment;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import olupis.world.*;

public class TrasngenderTreeBlock extends TreeBlock{
    public float transRange = Vars.tilesize * 5;
    public TextureRegion log;

    public TrasngenderTreeBlock(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        log = Core.atlas.find( name + "-log", "olupis-treelog");
    }

    @Override
    public void drawBase(Tile tile){


        float alpha = 1f;
        if(Core.settings.getBool("nyfalis-qolTreeTrans")){
            alpha = 1f - NyfWorldFuckingHelper.withinMouseOrUnitRangeF(tile, transRange);
        }

        float
        x = tile.worldx(), y = tile.worldy(),
        rot = Mathf.randomSeed(tile.pos(), 0, 4) * 90 + Mathf.sin(Time.time + x, 50f, 0.5f) + Mathf.sin(Time.time - y, 65f, 0.9f) + Mathf.sin(Time.time + y - x, 85f, 0.9f),
        w = region.width * region.scl(), h = region.height * region.scl(),
        scl = 30f, mag = 0.2f;

        TextureRegion shad = variants == 0 ? customShadowRegion : variantShadowRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantShadowRegions.length - 1))];



        if(shad.found()){
            Draw.z(Layer.power - 1);
            Draw.rect(shad, tile.worldx() + shadowOffset, tile.worldy() + shadowOffset, rot);
        }

        if(log.found()){
            Draw.rect(log, tile.worldx(), tile.worldy(), rot);
        }
        Draw.alpha(alpha);

        TextureRegion reg = variants == 0 ? region : variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))];

        Draw.z(Layer.power + 1);
        Draw.rectv(reg, x, y, w, h, rot, vec -> vec.add(
        Mathf.sin(vec.y*3 + Time.time, scl, mag) + Mathf.sin(vec.x*3 - Time.time, 70, 0.8f),
        Mathf.cos(vec.x*3 + Time.time + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y*3 - Time.time, 50, 0.2f)
        ));
        Draw.reset();
    }


}
