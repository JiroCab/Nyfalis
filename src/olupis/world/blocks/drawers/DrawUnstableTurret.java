package olupis.world.blocks.drawers;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.Rand;
import arc.struct.Seq;
import arc.util.*;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import olupis.world.blocks.turret.UnstablePowerTurret;
import olupis.world.entities.parts.*;

import java.util.Iterator;

public class DrawUnstableTurret extends DrawBlock {
    protected static final Rand rand = new Rand();
    public Seq<UnstableDrawPart> parts = new Seq();
    public String basePrefix = "";
    @Nullable
    public Liquid liquidDraw;
    public TextureRegion base;
    public TextureRegion liquid;
    public TextureRegion top;
    public TextureRegion heat;
    public TextureRegion preview;
    public TextureRegion outline;
    public TextureRegion overheatHeat;
    public TextureRegion lights;

    public DrawUnstableTurret(String basePrefix) {
        this.basePrefix = basePrefix;
    }

    public DrawUnstableTurret() {
    }

    public void getRegionsToOutline(Block block, Seq<TextureRegion> out) {
        Iterator var3 = this.parts.iterator();


        while(var3.hasNext()) {
            UnstableDrawPart part = (UnstableDrawPart)var3.next();
            part.getOutlines(out);
        }

        if (block.region.found() && (block.outlinedIcon <= 0 || block.outlinedIcon >= block.getGeneratedIcons().length || !block.getGeneratedIcons()[block.outlinedIcon].equals(block.region))) {
            out.add(block.region);
        }

        block.resetGeneratedIcons();
    }

    public void draw(Building build) {
        UnstablePowerTurret t = (UnstablePowerTurret)build.block;
        UnstablePowerTurret.UnstablePowerTurretBuild tb = (UnstablePowerTurret.UnstablePowerTurretBuild)build;
        Draw.rect(this.base, build.x, build.y);
        Draw.color();
        Draw.z(49.5F);
        Drawf.shadow(this.preview, build.x + tb.recoilOffset.x - t.elevation, build.y + tb.recoilOffset.y - t.elevation, tb.drawrot());
        Draw.z(50.0F);
        this.drawTurret(t, tb);
        this.drawHeat(t, tb);
        if (this.parts.size > 0) {
            if (this.outline.found()) {
                Draw.z(49.99F);
                Draw.rect(this.outline, build.x + tb.recoilOffset.x, build.y + tb.recoilOffset.y, tb.drawrot());
                Draw.z(50.0F);
            }

            float progress = tb.progress();
            UnstableDrawPart.UnstablePartParams Uparams = UnstableDrawPart.Uparams.set(build.warmup(), 1.0F - progress, 1.0F - progress, tb.heat, tb.curRecoil, tb.charge, tb.x + tb.recoilOffset.x, tb.y + tb.recoilOffset.y, tb.rotation);
            Iterator var6 = this.parts.iterator();

            while(var6.hasNext()) {
                UnstableDrawPart part = (UnstableDrawPart)var6.next();
                Uparams.setRecoil(part.recoilIndex >= 0 && tb.curRecoils != null ? tb.curRecoils[part.recoilIndex] : tb.curRecoil);
                part.draw(Uparams,build);
            }
        }

    }

    public void drawTurret(UnstablePowerTurret block, UnstablePowerTurret.UnstablePowerTurretBuild build) {
        if (block.region.found()) {
            Draw.rect(block.region, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot());
        }

        if (this.liquid.found()) {
            Liquid toDraw = this.liquidDraw == null ? build.liquids.current() : this.liquidDraw;
            Drawf.liquid(this.liquid, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.liquids.get(toDraw) / block.liquidCapacity, toDraw.color.write(Tmp.c1).a(1.0F), build.drawrot());
        }

        if (this.top.found()) {
            Draw.rect(this.top, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot());
        }

    }

    public void drawHeat(UnstablePowerTurret block, UnstablePowerTurret.UnstablePowerTurretBuild build) {
        Draw.color(block.coolColor, block.hotColor, build.heatT);
        Draw.rect(overheatHeat, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot());

        if (!(build.heat <= 1.0E-5F) && this.heat.found()) {
            Drawf.additive(this.heat, block.heatColor.write(Tmp.c1).a(build.heat), build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot(), 50.1F);
        }
        if(build.heatT > block.flashThreshold){
            build.flash += (1f + ((build.heatT - block.flashThreshold) / (1f - block.flashThreshold)) * 5.4f) * Time.delta;
            Draw.color(block.flashColor1, block.flashColor2, Mathf.absin(build.flash, 9f, 1f));
            Draw.alpha(0.3f);
            Draw.rect(lights, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot());
        }
    }

    public void load(Block block) {
        if (!(block instanceof UnstablePowerTurret)) {
            throw new ClassCastException("This drawer can only be used on UnstablePowerTurrets.");
        } else {
            this.preview = Core.atlas.find(block.name + "-preview", block.region);
            this.outline = Core.atlas.find(block.name + "-outline");
            this.liquid = Core.atlas.find(block.name + "-liquid");
            this.top = Core.atlas.find(block.name + "-top");
            this.heat = Core.atlas.find(block.name + "-heat");
            this.overheatHeat = Core.atlas.find(block.name + "-overh-heat");
            this.lights = Core.atlas.find(block.name + "-lights");
            this.base = Core.atlas.find(block.name + "-base");
            Iterator var2 = this.parts.iterator();

            while(var2.hasNext()) {
                UnstableDrawPart part = (UnstableDrawPart)var2.next();
                part.turretShading = true;
                part.load(block.name);
            }

            if (!this.base.found() && block.minfo.mod != null) {
                this.base = Core.atlas.find(block.minfo.mod.name + "-" + this.basePrefix + "block-" + block.size);
            }

            if (!this.base.found()) {
                this.base = Core.atlas.find(this.basePrefix + "block-" + block.size);
            }

        }
    }

    public TextureRegion[] icons(Block block) {
        return this.top.found() ? new TextureRegion[]{this.base, this.preview, this.top} : new TextureRegion[]{this.base, this.preview};
    }
}

