package olupis.world.entities.parts;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;
import olupis.world.entities.parts.NyfPartParms.*;

public class FloaterTreadsPart extends RegionPart {
	//todo: actually make the treads rects
    public PartProgress alphaProgress =  NyfPartParms.NyfPartProgress.floatingP, treadProgress = NyfPartProgress.treadsP;
    public float minAlpha = 0.1f, maxAlpha = 1f;

    public boolean drawAnimatedTreads = true, mirrorTreads = false;
    /** number of frames of movement in a tread */
    public int treadFrames = 18;
    /** list of treads as rectangles in IMAGE COORDINATES, relative to the center. these are mirrored. */
    public Rect[] treadRects = {};
    /** how much of a top part of a tread sprite is "cut off" relative to the pattern; this is corrected for */
    public int treadPullOffset = 0;
    public float animatedTreadZ = 0;
    public TextureRegion[][] treadRegions;
    public TextureRegion treadRegion;

    public  FloaterTreadsPart(String region){super(region);}
    public FloaterTreadsPart(String region, Blending blending, Color color){
        super(region, blending, color);
    }
    public FloaterTreadsPart(){}

    @Override
    public void load(String name){
        super.load(name);
        String realName = this.name == null ? name + suffix : this.name;
        treadRegion = Core.atlas.find(name + "-treads",  realName);

        if(treadRegion.found()){
            treadRegions = new TextureRegion[treadRects.length][treadFrames];
            for(int r = 0; r < treadRects.length; r++){
                for(int i = 0; i < treadFrames; i++){
                    var reg = Core.atlas.find(name + "-treads" + r + "-" + i, name + r + "-" + i);
                    treadRegions[r][i] = reg;
                }
            }
        }

        if(!drawRegion && drawAnimatedTreads){
            outlines = new TextureRegion[]{treadRegion};
        }
    }

    @Override
    public void getOutlines(Seq<TextureRegion> out){
        super.getOutlines(out);
        out.addAll(treadRegion, treadRegions[0][0]);
    }

    @Override
    public void draw(PartParams params){
        //TODO: maybe redo this whole spaget
        float alp = Mathf.lerp(minAlpha, maxAlpha, alphaProgress.getClamp(params));

        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        //TODO 'under' should not be special cased like this...
        if(under && turretShading) Draw.z(z - 0.0001f);
        Draw.z(Draw.z() + layerOffset);

        float prevZ = Draw.z();
        float prog = progress.getClamp(params), sclProg = growProgress.getClamp(params);
        float mx = moveX * prog, my = moveY * prog, mr = moveRot * prog + rotation,
                gx = growX * sclProg, gy = growY * sclProg;

        if(moves.size > 0){
            for(int i = 0; i < moves.size; i++){
                var move = moves.get(i);
                float p = move.progress.getClamp(params);
                mx += move.x * p;
                my += move.y * p;
                mr += move.rot * p;
                gx += move.gx * p;
                gy += move.gy * p;
            }
        }

        int len = mirror && params.sideOverride == -1 ? 2 : 1;
        float preXscl = Draw.xscl, preYscl = Draw.yscl;
        Draw.xscl *= xScl + gx;
        Draw.yscl *= yScl + gy;

        for(int s = 0; s < len; s++){
            //use specific side if necessary
            int i = params.sideOverride == -1 ? s : params.sideOverride;

            //can be null
            var region = drawRegion ? regions[Math.min(i, regions.length - 1)] : null;
            float sign = (i == 0 ? 1 : -1) * params.sideMultiplier;
            Tmp.v1.set((x + mx) * sign, y + my).rotateRadExact((params.rotation - 90) * Mathf.degRad);

            float
                    rx = params.x + Tmp.v1.x,
                    ry = params.y + Tmp.v1.y,
                    rot = mr * sign + params.rotation - 90;

            Draw.xscl *= sign;

            if(outline && (drawRegion || drawAnimatedTreads)){
                Draw.z(prevZ + outlineLayerOffset);
                Draw.alpha(alp);
                Draw.rect(outlines[Math.min(i, Math.max(regions.length - 1, 0))], rx, ry, rot);
                Draw.z(prevZ);
            }

            if(drawRegion && region.found()){
                if(color != null && colorTo != null) Draw.color(color, colorTo, prog);
                else if(color != null) Draw.color(color, alp);

                if(mixColor != null && mixColorTo != null) Draw.mixcol(mixColor, mixColorTo, prog);
                else if(mixColor != null) Draw.mixcol(mixColor, mixColor.a);

                Draw.blend(blending);
                Draw.alpha(alp);
                Draw.rect(region, rx, ry, rot);
                Draw.blend();
                if(color != null) Draw.color();
            }

            if(drawAnimatedTreads && treadRegion.found()){
                if(color != null && colorTo != null) Draw.color(color, colorTo, prog);
                else if(color != null) Draw.color(color, alp);

                if(mixColor != null && mixColorTo != null) Draw.mixcol(mixColor, mixColorTo, prog);
                else if(mixColor != null) Draw.mixcol(mixColor, mixColor.a);

                Draw.z(prevZ + animatedTreadZ);
                Draw.blend(blending);
                Draw.alpha(alp);
                int frame = (int)(treadProgress.get(params)) % treadFrames;
                for(int t = 0; t < treadRects.length; t ++){
                    var tregion = treadRegions[t][frame];
                    var treadRect = treadRects[t];
                    float xOffset = -(treadRect.x + treadRect.width/2f);
                    float yOffset = -(treadRect.y + treadRect.height/2f);

                    int side[] = mirrorTreads ? Mathf.signs : new int[]{1};
                    for(int k : side){
                        Tmp.v1.set(xOffset * k, yOffset).rotate(rot);
                        Draw.rect(tregion, rx + Tmp.v1.x / 4f, ry + Tmp.v1.y / 4f, treadRect.width / 4f, tregion.height * tregion.scale / 4f, rot);
                    }
                }
                Draw.blend();
                Draw.z(prevZ);
                if(color != null) Draw.color();
            }



            if(heat.found()){
                float hprog = heatProgress.getClamp(params);
                heatColor.write(Tmp.c1).a(hprog * heatColor.a);
                Drawf.additive(heat, Tmp.c1, rx, ry, rot, turretShading ? turretHeatLayer : Draw.z() + heatLayerOffset);
                if(heatLight) Drawf.light(rx, ry, heat, rot, Tmp.c1, heatLightOpacity * hprog);
            }

            Draw.xscl *= sign;
        }

        Draw.color();
        Draw.mixcol();

        Draw.z(z);

        //draw child, if applicable - only at the end
        //TODO lots of copy-paste here
        if(children.size > 0){
            for(int s = 0; s < len; s++){
                int i = (params.sideOverride == -1 ? s : params.sideOverride);
                float sign = (i == 1 ? -1 : 1) * params.sideMultiplier;
                Tmp.v1.set((x + mx) * sign, y + my).rotateRadExact((params.rotation - 90) * Mathf.degRad);

                childParam.set(params.warmup, params.reload, params.smoothReload, params.heat, params.recoil, params.charge, params.x + Tmp.v1.x, params.y + Tmp.v1.y, i * sign + mr * sign + params.rotation);
                childParam.sideMultiplier = params.sideMultiplier;
                childParam.life = params.life;
                childParam.sideOverride = i;
                for(var child : children){
                    child.draw(childParam);
                }
            }
        }

        Draw.scl(preXscl, preYscl);
    }

}
