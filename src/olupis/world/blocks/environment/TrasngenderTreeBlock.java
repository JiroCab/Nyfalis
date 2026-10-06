package olupis.world.blocks.environment;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import olupis.*;
import olupis.world.*;

import static olupis.NyfalisVars.treeTransgenderRange;


public class TrasngenderTreeBlock extends TreeBlock{
    public TextureRegion log;
    public TextureRegion[] featureRegions, branchRegions, calyxiedRegions, calyxiestRegions;
    public boolean leaf = true , infestable = true;
    public Seq<Color> flavours = Seq.with();
    public int featureVariants = -1, calyxiedVariants =1, calyxiestVariants = -1, branchVariants = -1;
    public @Nullable Block parent;

    public TrasngenderTreeBlock(String name){
        super(name);
    }

/*      quick tldr on how this layers work, top to bottom
            - feature
            - calyxiestRegions / calyxiedRegions
            - region / variantRegions (coloured)
            - branchRegions regions
            - log
            - shadow
*/

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

        if(calyxiestVariants >= 1){
            calyxiestRegions= new TextureRegion[calyxiestVariants];

            for(int i = 0; i < calyxiestVariants; i++) calyxiestRegions[i] = Core.atlas.find(name + "-calyxiest" + (i + 1));
        }

        if(calyxiedVariants >= 1){
            calyxiedRegions= new TextureRegion[calyxiedVariants];

            for(int i = 0; i < calyxiedVariants; i++) calyxiedRegions[i] = Core.atlas.find(name + "-calyxied" + (i + 1));
        }

        if(featureVariants >= 1){
            featureRegions = new TextureRegion[featureVariants];

            for(int i = 0; i < featureVariants; i++) featureRegions[i] = Core.atlas.find(name + "-feature" + (i + 1));
        }

        if(branchVariants >= 1){
            branchRegions = new TextureRegion[branchVariants];

            for(int i = 0; i < branchVariants; i++) branchRegions[i] = Core.atlas.find(name + "-branches" + (i + 1));
        }
    }

    @Override
    public void createIcons(MultiPacker packer){
        super.createIcons(packer);

        Pixmap base;

        if(branchRegions != null){
            base = Core.atlas.getPixmap(branchRegions[0]).crop();
            base.draw(Core.atlas.getPixmap(region), true);
        } else base =  Core.atlas.getPixmap(region).crop();

        if(featureRegions != null)base.draw(Core.atlas.getPixmap(featureRegions[0]), true);

        packer.add(PageType.main, "block-" + name + "-full", base);
        base.dispose();
    }

    @Override
    public void drawOverlay(float x, float y, int rotation){
        //do not
    }

    @Override
    public void drawBase(Tile tile){

        float alpha = 1f;
        if(treeTransgenderRange > 0 && treeTransgenderRange < 51 * 8f ){
            alpha = NyfWorldFuckingHelper.withinMouseOrUnitRangeF(tile, treeTransgenderRange);
        } else if(treeTransgenderRange >= 51 * 8f ) alpha = 0;

        float
        x = tile.worldx(), y = tile.worldy(),
        rot = Mathf.randomSeed(tile.pos(), 0, 4) * 90 + Mathf.sin(Time.time + x, 50f, 0.5f) + Mathf.sin(Time.time - y, 65f, 0.9f) + Mathf.sin(Time.time + y - x, 85f, 0.9f),
        w = region.width * region.scl(), h = region.height * region.scl(),
        scl = 30f, mag = 0.2f;

        TextureRegion reg = variants == 0 ? customShadowRegion : variantShadowRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantShadowRegions.length - 1))];
        if(reg.found()){
            Draw.z(Layer.power - 1);
            Draw.rect(reg, tile.worldx() + shadowOffset, tile.worldy() + shadowOffset, rot);
        }

        if(log.found()) Draw.rect(log, tile.worldx(), tile.worldy(), rot);

        Draw.alpha(alpha);
        Draw.z(Layer.power + 1);

        if(branchRegions != null &&  branchRegions.length >= 1){
            reg = branchRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, branchRegions.length - 1))];
            commonRectV(reg, x , y, w, h, rot, scl, mag);
        }

        reg = variants == 0 ? region : variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))];
        if(!flavours.isEmpty()) Draw.color(treeFlavor(tile), alpha);

        commonRectV(reg, x , y, w, h, rot, scl, mag);

        if( featureRegions != null && featureRegions.length >= 1){
            Draw.color();
            Draw.alpha(alpha);
            reg = featureRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, featureRegions.length - 1))];
            commonRectV(reg, x , y, w, h, rot, scl, mag);
        }


        if(!infestable){
            //Todo: actual overlays, skip the 1st stage, as calyx is eating the tree's inside and anything more it has finished eating it and in a spreading/blooming stage
        }
        Draw.reset();
    }

    void commonRectV(TextureRegion fet, float x, float y, float w, float h, float rot, float scl, float mag){
        Draw.rectv(fet, x, y, w, h, rot, vec -> vec.add(
        Mathf.sin(vec.y*3 + Time.time, scl, mag) + Mathf.sin(vec.x*3 - Time.time, 70, 0.8f),
        Mathf.cos(vec.x*3 + Time.time + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y*3 - Time.time, 50, 0.2f)
        ));
    }

    public Color treeFlavor(Tile tile){
        if(flavours.isEmpty()) return Color.white;
        return new Color().set(Draw.getColor()).lerp(flavours.get(Mathf.randomSeed(tile.pos(), 0, flavours.size -1)), Mathf.randomSeedRange(tile.pos(), 1f));
    }

}
