package olupis.world.entities.parts;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.part.*;
import mindustry.game.*;
import mindustry.graphics.*;

import static olupis.world.entities.parts.NyfPartParms.nyfparams;

public class ThrusterPartOwO extends DrawPart{
    protected PartParams childParam = new PartParams();
    /** If true, parts are mirrored across the turret. Requires -1 and -2 regions. */
    public boolean mirror = false;
    /** Whether to clamp progress to (0-1). If false, allows usage of interps that go past the range, but may have unwanted visual bugs depending on values. */
    public boolean clampProgress = true;
    /** Progress function for determining position/rotation. */
    public PartProgress progress = PartProgress.warmup;
    /** Progress function for scaling. */
    public PartProgress growProgress = PartProgress.warmup;
    /** Progress function for heat alpha. */
    public Blending blending = Blending.normal;
    public float layer = -1, layerOffset = 0f;
    //note that origin DOES NOT AFFECT child parts
    public float x, y, xScl = 1f, yScl = 1f, rotation, originX, originY;
    public float moveX, moveY, growX, growY, moveRot;
    public Seq<DrawPart> children = new Seq<>();
    public Seq<PartMove> moves = new Seq<>();
    public float width = 3f, height = 5f;
    public boolean rotMirror =false, absin = true;

    @Override
    public void draw(PartParams params){
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        //TODO 'under' should not be special cased like this...
        if(under && turretShading) Draw.z(z - 0.0001f);
        Draw.z(Draw.z() + layerOffset);

        float prevZ = Draw.z();
        float prog = progress.getClamp(params, clampProgress), sclProg = growProgress.getClamp(params, clampProgress);
        float mx = moveX * prog, my = moveY * prog, mr = moveRot * prog + rotation, rand = absin ? Mathf.absin(Time.time, 10f, ((width * height) /2)/6) : 0,
        gx = growX * sclProg, gy = growY * sclProg;

        if(moves.size > 0){
            for(int i = 0; i < moves.size; i++){
                var move = moves.get(i);
                float p = move.progress.getClamp(params, clampProgress);
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
            float sign = (i == 0 ? 1 : -1) * params.sideMultiplier;
            Tmp.v1.set((x + mx) * sign, y + my).rotateRadExact((params.rotation - 90) * Mathf.degRad);

            Draw.xscl *= sign;

            if(originX != 0f || originY != 0f){
                //correct for offset caused by origin shift
                Tmp.v1.sub(Tmp.v2.set(-originX * Draw.xscl, -originY * Draw.yscl).rotate(params.rotation - 90f).add(originX * Draw.xscl, originY * Draw.yscl));
            }

            float
            rx = params.x + Tmp.v1.x,
            ry = params.y + Tmp.v1.y,
            rot = mr * (rotMirror ? sign : 1) + params.rotation - 90;

        Draw.color(Team.get(nyfparams.team).color);

        Draw.blend(blending);
        Drawf.tri(rx, ry, width * gx + rand , height * gy + rand, rot);
        Draw.blend();
        Draw.color();

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

                childParam.set(params.warmup, params.reload, params.smoothReload, params.heat, params.recoil, params.charge, params.x + Tmp.v1.x, params.y + Tmp.v1.y, mr * sign + params.rotation);
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
