package olupis.world.blocks.misc;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Gamemode;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.*;

public class Replicator extends PayloadBlock {
    public float maxDelay = 30f, speedScl, time;
    public Interp riseInterp = Interp.circleOut;
    public float delay = 1;
    public Seq<UnitType> spawnableUnits = new Seq<>();

    public Replicator(String name){
        super(name);

        //size = 4;
        update = outputsPayload = rotate = noUpdateDisabled = clearOnDoubleTap = teamPassable = commandable = configurable = solid = privileged = true;
        hasPower = quickRotate = destructible =  targetable = false;
        //make sure to display large units.

        clipSize = 120;
        regionRotated1 = 1;
        selectionRows = selectionColumns = 8;

        group = BlockGroup.units;
        spawnableUnits.addAll(content.units().select(Replicator.this::canProduce).as());

        config(Integer.class, (ReplicatorBuild build, Integer unit) -> build.selectedUnit = unit);

        config(Float.class,(ReplicatorBuild build,Float f) -> build.dynamicDelay = f);

        config(String.class,(ReplicatorBuild build,String s) -> {
            build.selectedUnit = Integer.parseInt(s.split(";")[0]);
            build.dynamicDelay = Float.parseFloat(s.split(";")[1]);
        });

        configClear((ReplicatorBuild build) -> {
            build.selectedUnit = -1;
            build.dynamicDelay = delay;
        });
    }

    public boolean accessible(){
        return !privileged || state.rules.editor || state.playtestingMap != null || state.rules.mode() == Gamemode.sandbox;
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
        public float dynamicDelay = delay,
                delayTimer = 0;
        public int selectedUnit = -1;
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
                    () -> selectedUnit == -1 ? null : spawnableUnits.get(selectedUnit) ,
                    (i) -> configure(spawnableUnits.indexOf(i)),
                    selectionRows, selectionColumns);
            table.row();
            Cell<Label> delayDisplay = table.add("Delay: " + dynamicDelay + " sec");
            table.row();
            table.slider(1,maxDelay,0.5f,dynamicDelay, true,(f) -> {
                configure(f);
                delayTimer = 0;
                delayDisplay.get().setText("Delay: " + dynamicDelay + " sec");
            });
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return false;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            delayTimer = Mathf.approachDelta(delayTimer,0,1);
            speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
            time += edelta() * speedScl * Vars.state.rules.unitBuildSpeed(team);

            if (delayTimer <= 0) {
                delayTimer = dynamicDelay * 60;
                if (team == state.rules.defaultTeam && unlockedNowHost() && state.isCampaign()) return;
                if (payload == null) {
                    scl = 0f;
                    if (selectedUnit != -1) {
                        payload = new UnitPayload(spawnableUnits.get(selectedUnit).create(team));
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
            return  selectedUnit + ";" + dynamicDelay;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());

            if(selectedUnit != -1 && !inFogTo(Vars.player.team())){
                Draw.draw(Layer.blockOver, () ->{
                   //Drawf.construct(this, spawnableUnits.get(selectedUnit), rotdeg() - 90f, 1 - (delayTimer / (dynamicDelay * 60)) , scl, time )
                    Draw.alpha(1 - (delayTimer / (dynamicDelay * 60)));
                    float f = Mathf.clamp(1 - riseInterp.apply(delayTimer / (dynamicDelay * 60)));
                    Draw.color(Tmp.c1.set(spawnableUnits.get(selectedUnit).outlineColor).lerp(Color.white, f + Mathf.absin(Time.time, Math.max(f * 3f, 0.9f), 1f - f)));
                    Draw.rect(spawnableUnits.get(selectedUnit).fullIcon, x, y, rotdeg() - 90f);
                    Draw.color();
                    Draw.reset();
                } );
            }

            if(topRegion.found())Draw.rect(topRegion, x, y, rotdeg());

            Draw.scl(scl);
            drawPayload();
            Draw.reset();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(selectedUnit);
            write.f(dynamicDelay);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            selectedUnit = read.i();
            dynamicDelay = read.f();
        }
        @Override
        public boolean canPickup(){
            return false;
        }

        @Override
        public void damage(float damage){
            if(!privileged){
                super.damage(damage);
            }
        }

        //editor-only processors cannot be damaged or destroyed
        @Override
        public boolean collide(Bullet other){
            return !privileged;
        }
    }
}