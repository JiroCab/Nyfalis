package olupis.world.blocks.unit;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;
import olupis.content.*;

import java.util.*;

import static mindustry.Vars.*;

public class Replicator extends PayloadBlock {
    public float maxDelay = 300f, speedScl, time;
    public Interp riseInterp = Interp.circle;
    public float delay = maxDelay;
    public Seq<UnitType> spawnableUnits = new Seq<>();
    public Block replacement = NyfalisBlocks.rustyScrapWall;

    public Replicator(String name){
        super(name);

        //size = 4;
        update = outputsPayload = rotate = noUpdateDisabled = clearOnDoubleTap = teamPassable = commandable = configurable = solid = true;
        hasPower = quickRotate = destructible =  targetable = false;
        //make sure to display large units.

        clipSize = 120;
        regionRotated1 = 1;
        selectionRows = selectionColumns = 8;

        group = BlockGroup.units;
        spawnableUnits.addAll(content.units().select(Replicator.this::canProduce).as());

        config(UnitType.class, (ReplicatorBuild build, UnitType unit) -> build.config= unit);

        config(Float.class,(ReplicatorBuild build,Float f) -> build.dynamicDelay = f);

        configClear((ReplicatorBuild build) -> {
            build.config = null;
            build.dynamicDelay = delay;
        });
    }

    public boolean accessible(){
        return state.rules.editor || state.playtestingMap != null || state.rules.infiniteResources;
    }

    @Override
    public boolean canBreak(Tile tile){
        return accessible();
    }

    @Override
    public TextureRegion[] icons(){
        if(topRegion.found()) return new TextureRegion[]{region, outRegion, topRegion};
        return new TextureRegion[]{region, outRegion};
    }

    public void setBars() {
        super.setBars();
        removeBar("units");

        addBar("bar.progress", (ReplicatorBuild entity) -> new Bar("bar.progress", Pal.ammo,() -> entity.dynamicDelay / entity.delayTimer));
        addBar("units", (ReplicatorBuild e) -> {
            if(e.config == null) return null ;
            UnitType unit = e.config;
            if (unit == null) return null;
            return new Bar(() -> Core.bundle.format("bar.unitcap",
                    !Objects.equals(Fonts.getUnicodeStr(unit.localizedName), "") ? Fonts.getUnicodeStr(unit.localizedName) : Iconc.units,
                    e.team.data().countType(unit),
                    Units.getStringCap(e.team)
            ), () -> Pal.accent,
                    () -> (float) e.team.data().countType(unit) / Units.getCap(e.team));
        });
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        if (!accessible())return;
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        if(topRegion.found())Draw.rect(topRegion, plan.drawx(), plan.drawy());
    }

    public boolean canProduce(UnitType t){
        return !t.isHidden() && !t.isBanned() && t.supportsEnv(state.rules.env);
    }

    public class ReplicatorBuild extends PayloadBlockBuild<Payload>{
        public @Nullable Vec2 commandPos;
        public float dynamicDelay = delay * 60,
                delayTimer = delay * 60;
        public @Nullable UnitType config = null;
        public float scl;

        @Override
        public Vec2 getCommandPosition(){
            return commandPos;
        }

        @Override
        public void onCommand(Vec2 target){
            commandPos = target;
        }

        @Override
        public void buildConfiguration(Table table){
            if(!accessible()){
                //go away
                deselect();
                return;
            }
            ItemSelection.buildTable(Replicator.this,
                    table,
                    spawnableUnits,
                    () -> config != null ? config : null,
                    this::configure,
                    selectionRows, selectionColumns);
            table.row();
            Cell<Label> delayDisplay = table.add("Delay: " + dynamicDelay + " sec");
            table.row();
            table.slider(1,maxDelay,0.5f,dynamicDelay, true,(f) -> {
                configure(f);
                delayTimer = dynamicDelay * 60;
                delayDisplay.get().setText("Delay: " + dynamicDelay + " sec");
            }).growX();
            if(Core.settings.getBool("nyfalis-debug")){
                table.row();
                table.table().update(t -> {
                    t.clear();
                    t.add(Math.round(delayTimer/60) + "s ").row();
                    t.add(delayTimer + "t");
                }).growX();


            }
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return false;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            //Just annoying if damaged somehow like sector lost and annoy block healers
            if(damaged())heal(maxHealth);
            if(config == null) return;

            delayTimer = Mathf.approachDelta(delayTimer,0,1);
            speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
            time += edelta() * speedScl * Vars.state.rules.unitBuildSpeed(team);

            if (delayTimer <= 0) {
                if (team == state.rules.defaultTeam && unlockedNowHost() && state.isCampaign()) return;
                if (payload == null) {
                    delayTimer = dynamicDelay * 60;
                    scl = 0f;
                    if (config != null) {
                        payload = new UnitPayload(config.create(team));
                        Unit p = ((UnitPayload) payload).unit;
                        if (commandPos != null && p.isCommandable()) {
                            p.command().commandPosition(commandPos);
                        }
                    }
                    payVector.setZero();
                    payRotation = rotdeg();
                }
            }
            scl = Mathf.lerpDelta(scl, 1f, 0.1f);
            moveOutPayload();
        }

        @Override
        public Object config(){
            return  config + ";" + dynamicDelay;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());

            if(config != null && !inFogTo(Vars.player.team())){
                UnitType unit = config;
                Draw.draw(Layer.blockOver, () ->{
                    Shaders.build.region = unit.fullIcon;
                    Shaders.build.progress = Mathf.clamp(1 - riseInterp.apply(delayTimer / (dynamicDelay * 60)));
                    Shaders.build.color.set(config.outlineColor);
                    Shaders.build.color.a =riseInterp.apply((delayTimer / (dynamicDelay * 60))) ;
                    Shaders.build.time = riseInterp.apply(delayTimer / (dynamicDelay * 60)) * 10;

                    Draw.shader(Shaders.build);
                    Draw.rect(unit.fullIcon, x, y, rotdeg() - 90f);
                    Draw.shader();
                    Draw.color();
                    Draw.reset();
                } );
            }

            if(topRegion.found())Draw.rect(topRegion, x, y, rotdeg());

            Draw.scl(scl);
            drawPayload();
            Draw.reset();
        }

        public void drawPayload(){
          if(!headless && this.inFogTo(player.team()))super.drawPayload();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.str(config != null ? config.name : "");
            write.f(dynamicDelay);
            write.f(delayTimer);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision <= 1){
                int r = read.i();
                if(r >= 0)config = spawnableUnits.get(r);
            }
            else config = Vars.content.getByName(ContentType.unit, read.str());

            dynamicDelay = read.f();
            if(revision>= 1) delayTimer = read.f();
        }

        public byte version() {
            return 2;
        }

        @Override
        public boolean canPickup(){
            return false;
        }

        @Override
        public void display(Table table){
            super.display(table);

            TextureRegionDrawable reg = new TextureRegionDrawable();

            table.row();
            table.table(t -> {
                t.left();
                t.image().update(i -> {
                    i.setDrawable(config == null ? Icon.cancel : reg.set(config.uiIcon));
                    i.setScaling(Scaling.fit);
                    i.setColor(config == null  ? Color.lightGray : Color.white);
                }).size(32).padBottom(-4).padRight(2);
                t.label(() -> config == null  ? "@none" : config.localizedName).wrap().width(230f).color(Color.lightGray);
            }).left();
        }

        @Override
        public void damage(float damage){
            if(privileged){
                super.damage(damage);
            }
        }

        //editor-only processors cannot be damaged or destroyed
        @Override
        public boolean collide(Bullet other){
            return privileged;
        }

        public Block getReplacement(){
            return replacement;
        }
    }
}