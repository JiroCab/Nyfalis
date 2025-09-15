package olupis.world.blocks.processing;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.*;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import olupis.world.blocks.turret.UnstablePowerTurret;
import olupis.world.entities.parts.*;

import java.util.Iterator;

public class UnstableRegionPart extends UnstableDrawPart{
    public TextureRegion overheatHeat;
    public TextureRegion lights;

    public Color coolColor = new Color(1, 1, 1, 0f);
    public Color hotColor = Color.red;
    public Color flashColor1 = Color.red, flashColor2 = Color.yellow;
    protected UnstableDrawPart.UnstablePartParams childParam = new UnstableDrawPart.UnstablePartParams();
    public String suffix = "";
    @Nullable
    public String name;
    public TextureRegion heat;
    public TextureRegion[] regions = new TextureRegion[0];
    public TextureRegion[] outlines = new TextureRegion[0];
    public boolean mirror = false;
    public boolean outline = true;
    public boolean drawRegion = true;
    public boolean heatLight = false;
    public Boolf<Building> cond = b -> false;
    public UnstablePartProgress progress;
    public UnstablePartProgress growProgress;
    public UnstablePartProgress heatProgress;
    public Blending blending;
    public float layer;
    public float layerOffset;
    public float heatLayerOffset;
    public float turretHeatLayer;
    public float outlineLayerOffset;
    public float x;
    public float y;
    public float xScl;
    public float yScl;
    public float rotation;
    public float moveX;
    public float moveY;
    public float growX;
    public float growY;
    public float moveRot;
    public float heatLightOpacity;
    @Nullable
    public Color color;
    @Nullable
    public Color colorTo;
    @Nullable
    public Color mixColor;
    @Nullable
    public Color mixColorTo;
    public Color heatColor;
    public Seq<UnstableDrawPart> children;
    public Seq<UnstableDrawPart.PartMove> moves;


    public UnstableRegionPart(String region) {
        this.progress = UnstablePartProgress.warmup;
        this.growProgress = UnstablePartProgress.warmup;
        this.heatProgress = UnstablePartProgress.heat;
        this.blending = Blending.normal;
        this.layer = -1.0F;
        this.layerOffset = 0.0F;
        this.heatLayerOffset = 1.0F;
        this.turretHeatLayer = 50.1F;
        this.outlineLayerOffset = -0.001F;
        this.xScl = 1.0F;
        this.yScl = 1.0F;
        this.heatLightOpacity = 0.3F;
        this.heatColor = Pal.turretHeat.cpy();
        this.children = new Seq();
        this.moves = new Seq();
        this.suffix = region;
    }

    public UnstableRegionPart(String region, Boolf<Building> cond) {
        this.progress = UnstablePartProgress.warmup;
        this.growProgress = UnstablePartProgress.warmup;
        this.heatProgress = UnstablePartProgress.heat;
        this.blending = Blending.normal;
        this.layer = -1.0F;
        this.layerOffset = 0.0F;
        this.heatLayerOffset = 1.0F;
        this.turretHeatLayer = 50.1F;
        this.outlineLayerOffset = -0.001F;
        this.xScl = 1.0F;
        this.yScl = 1.0F;
        this.heatLightOpacity = 0.3F;
        this.heatColor = Pal.turretHeat.cpy();
        this.children = new Seq();
        this.moves = new Seq();
        this.suffix = region;
        this.cond = cond;
    }

    public UnstableRegionPart(String region, Blending blending, Color color) {
        this.progress = UnstablePartProgress.warmup;
        this.growProgress = UnstablePartProgress.warmup;
        this.heatProgress = UnstablePartProgress.heat;
        this.blending = Blending.normal;
        this.layer = -1.0F;
        this.layerOffset = 0.0F;
        this.heatLayerOffset = 1.0F;
        this.turretHeatLayer = 50.1F;
        this.outlineLayerOffset = -0.001F;
        this.xScl = 1.0F;
        this.yScl = 1.0F;
        this.heatLightOpacity = 0.3F;
        this.heatColor = Pal.turretHeat.cpy();
        this.children = new Seq();
        this.moves = new Seq();
        this.suffix = region;
        this.blending = blending;
        this.color = color;
        this.outline = false;
    }

    public UnstableRegionPart() {
        this.progress = UnstablePartProgress.warmup;
        this.growProgress = UnstablePartProgress.warmup;
        this.heatProgress = UnstablePartProgress.heat;
        this.blending = Blending.normal;
        this.layer = -1.0F;
        this.layerOffset = 0.0F;
        this.heatLayerOffset = 1.0F;
        this.turretHeatLayer = 50.1F;
        this.outlineLayerOffset = -0.001F;
        this.xScl = 1.0F;
        this.yScl = 1.0F;
        this.heatLightOpacity = 0.3F;
        this.heatColor = Pal.turretHeat.cpy();
        this.children = new Seq();
        this.moves = new Seq();
    }
    public void draw(UnstablePartParams Uparams, Building build) {
        float z = Draw.z();
        if (this.layer > 0.0F) {
            Draw.z(this.layer);
        }

        if (this.under && this.turretShading) {
            Draw.z(z - 1.0E-4F);
        }

        Draw.z(Draw.z() + this.layerOffset);
        float prevZ = Draw.z();
        float prog = this.progress.getClamp(Uparams);
        float sclProg = this.growProgress.getClamp(Uparams);
        float mx = this.moveX * prog;
        float my = this.moveY * prog;
        float mr = this.moveRot * prog + this.rotation;
        float gx = this.growX * sclProg;
        float gy = this.growY * sclProg;
        int len;
        float preYscl;
        if (this.moves.size > 0) {
            for(len = 0; len < this.moves.size; ++len) {
                PartMove move = (PartMove)this.moves.get(len);
                preYscl = move.progress.getClamp(Uparams);
                mx += move.x * preYscl;
                my += move.y * preYscl;
                mr += move.rot * preYscl;
                gx += move.gx * preYscl;
                gy += move.gy * preYscl;
            }
        }

        len = this.mirror && Uparams.sideOverride == -1 ? 2 : 1;
        float preXscl = Draw.xscl;
        preYscl = Draw.yscl;
        Draw.xscl *= this.xScl + gx;
        Draw.yscl *= this.yScl + gy;

        int s;
        int i;
        for(s = 0; s < len; ++s) {
            i = Uparams.sideOverride == -1 ? s : Uparams.sideOverride;
            TextureRegion region = this.drawRegion ? this.regions[Math.min(i, this.regions.length - 1)] : null;
            float sign = (float)((i == 0 ? 1 : -1) * Uparams.sideMultiplier);
            Tmp.v1.set((this.x + mx) * sign, this.y + my).rotateRadExact((Uparams.rotation - 90.0F) * 0.017453292F);
            float rx = Uparams.x + Tmp.v1.x;
            float ry = Uparams.y + Tmp.v1.y;
            float rot = mr * sign + Uparams.rotation - 90.0F;
            Draw.xscl *= sign;
            if (this.outline && this.drawRegion) {
                Draw.z(prevZ + this.outlineLayerOffset);
                Draw.rect(this.outlines[Math.min(i, this.regions.length - 1)], rx, ry, rot);
                Draw.z(prevZ);
            }

            if (this.drawRegion && region.found()) {
                if (this.color != null && this.colorTo != null) {
                    Draw.color(this.color, this.colorTo, prog);
                } else if (this.color != null) {
                    Draw.color(this.color);
                }

                if (this.mixColor != null && this.mixColorTo != null) {
                    Draw.mixcol(this.mixColor, this.mixColorTo, prog);
                } else if (this.mixColor != null) {
                    Draw.mixcol(this.mixColor, this.mixColor.a);
                }

                Draw.blend(this.blending);
                Draw.rect(region, rx, ry, rot);
                Draw.blend();
                if (this.color != null) {
                    Draw.color();
                }
            }

            if (this.heat.found()) {
                float hprog = this.heatProgress.getClamp(Uparams);
                this.heatColor.write(Tmp.c1).a(hprog * this.heatColor.a);
                Drawf.additive(this.heat, Tmp.c1, rx, ry, rot, this.turretShading ? this.turretHeatLayer : Draw.z() + this.heatLayerOffset);
                if (this.heatLight) {
                    Drawf.light(rx, ry, this.heat, rot, Tmp.c1, this.heatLightOpacity * hprog);
                }
            }
            UnstablePowerTurret t = (UnstablePowerTurret)build.block;
            UnstablePowerTurret.UnstablePowerTurretBuild tb = (UnstablePowerTurret.UnstablePowerTurretBuild)build;
            drawUnstable(t,tb,rx,ry,rot);


            Draw.xscl *= sign;
        }

        Draw.color();
        Draw.mixcol();
        Draw.z(z);
        if (this.children.size > 0) {
            for(s = 0; s < len; ++s) {
                i = Uparams.sideOverride == -1 ? s : Uparams.sideOverride;
                float sign = (float)((i == 1 ? -1 : 1) * Uparams.sideMultiplier);
                Tmp.v1.set((this.x + mx) * sign, this.y + my).rotateRadExact((Uparams.rotation - 90.0F) * 0.017453292F);
                this.childParam.set(Uparams.warmup, Uparams.reload, Uparams.smoothReload, Uparams.heat, Uparams.recoil, Uparams.charge, Uparams.x + Tmp.v1.x, Uparams.y + Tmp.v1.y, (float)i * sign + mr * sign + Uparams.rotation);
                this.childParam.sideMultiplier = Uparams.sideMultiplier;
                this.childParam.life = Uparams.life;
                this.childParam.sideOverride = i;
                Iterator var24 = this.children.iterator();

                while(var24.hasNext()) {
                    UnstableDrawPart child = (UnstableDrawPart)var24.next();
                    child.draw(this.childParam,build);
                }
            }
        }

        Draw.scl(preXscl, preYscl);
        Draw.reset();
    }
    @Override
    public void load(String name) {
        String realName = this.name == null ? name + this.suffix : this.name;
        if (this.drawRegion) {
            if (this.mirror && this.turretShading) {
                this.regions = new TextureRegion[]{Core.atlas.find(realName + "-r"), Core.atlas.find(realName + "-l")};
                this.outlines = new TextureRegion[]{Core.atlas.find(realName + "-r-outline"), Core.atlas.find(realName + "-l-outline")};
            } else {
                this.regions = new TextureRegion[]{Core.atlas.find(realName)};
                this.outlines = new TextureRegion[]{Core.atlas.find(realName + "-outline")};
            }
        }

        this.heat = Core.atlas.find(realName + "-heat");
        this.overheatHeat = Core.atlas.find(realName + "-overh-heat");
        this.lights = Core.atlas.find(realName + "-lights");
        Iterator var3 = this.children.iterator();

        while(var3.hasNext()) {
            UnstableDrawPart child = (UnstableDrawPart)var3.next();
            child.turretShading = this.turretShading;
            child.load(name);
        }

    }
    public void drawUnstable(UnstablePowerTurret block, UnstablePowerTurret.UnstablePowerTurretBuild build, float x, float y, float rot){
        if (this.overheatHeat.found()) {
            Draw.color(coolColor, hotColor, build.heatT);
            Draw.rect(overheatHeat, x, y, rot);
        }
        if (this.lights.found()) {
            if(build.heatT > block.flashThreshold){
                build.flash += (1f + ((build.heatT - block.flashThreshold) / (1f - block.flashThreshold)) * 5.4f) * Time.delta;
                Draw.color(flashColor1, flashColor2, Mathf.absin(build.flash, 9f, 1f));
                Draw.alpha(0.3f);
                Draw.rect(lights, x, y, rot);
            }
        }
    }
    public void getOutlines(Seq<TextureRegion> out) {
        if (this.outline && this.drawRegion) {
            out.addAll(this.regions);
        }

        Iterator var2 = this.children.iterator();

        while(var2.hasNext()) {
            UnstableDrawPart child = (UnstableDrawPart)var2.next();
            child.getOutlines(out);
        }

    }
}
