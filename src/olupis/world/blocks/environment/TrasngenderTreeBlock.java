package olupis.world.blocks.environment;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import olupis.world.*;

import static olupis.input.ui.NyfalisSettingsDialog.treeTransgenderRange;

public class TrasngenderTreeBlock extends TreeBlock{
    public TextureRegion log;
    public TextureRegion[] featureRegions;
    public boolean leaf = true, flavored = true;
    public Color  flavourTarget = Color.valueOf("d9f54e");
    public int featureVariants = -1;
    public @Nullable Block parent;

    public TrasngenderTreeBlock(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        if(parent != null){
            region = parent.region;
            variantRegions = parent.variantRegions;
            variantShadowRegions = parent.variantShadowRegions;
            variants = parent.variants;
            mapColor = parent.mapColor;
        }
        log = Core.atlas.find( name + "-log", "olupis-treelog");

        if(featureVariants >= 1){
            featureRegions = new TextureRegion[featureVariants];

            for(int i = 0; i < featureVariants; i++){
                featureRegions[i] = Core.atlas.find(name + "-feature"+ (i + 1));
            }

        }
    }

    @Override
    public void createIcons(MultiPacker packer){
        super.createIcons(packer);

        Pixmap base = Core.atlas.getPixmap(region).crop();
        if(featureRegions != null)base.draw(Core.atlas.getPixmap(featureRegions[0]), true);
        packer.add(PageType.main, "block-" + name + "-full", base);
        base.dispose();
    }

    @Override
    public void drawBase(Tile tile){

        float alpha = 1f;
        if(treeTransgenderRange > 0 && treeTransgenderRange < 51 ){
            alpha = NyfWorldFuckingHelper.withinMouseOrUnitRangeF(tile, treeTransgenderRange);
        } else if(treeTransgenderRange >= 51 ) alpha = 0;

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
        if(flavored) Draw.color(new Color().set(Draw.getColor()).lerp(flavourTarget, Mathf.randomSeedRange(tile.pos(), 1f)));
        Draw.z(Layer.power + 1);
        Draw.rectv(reg, x, y, w, h, rot, vec -> vec.add(
        Mathf.sin(vec.y*3 + Time.time, scl, mag) + Mathf.sin(vec.x*3 - Time.time, 70, 0.8f),
        Mathf.cos(vec.x*3 + Time.time + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y*3 - Time.time, 50, 0.2f)
        ));
        if( featureRegions != null && featureRegions.length >= 1){
            Draw.color();
            Draw.alpha(alpha);
            TextureRegion fet = featureRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, featureRegions.length - 1))];
            Draw.rectv(fet, x, y, w, h, rot, vec -> vec.add(
                Mathf.sin(vec.y*3 + Time.time, scl, mag) + Mathf.sin(vec.x*3 - Time.time, 70, 0.8f),
                Mathf.cos(vec.x*3 + Time.time + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y*3 - Time.time, 50, 0.2f)
            ));
        }
        Draw.reset();
    }


}
