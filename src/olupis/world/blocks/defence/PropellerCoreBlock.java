package olupis.world.blocks.defence;

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
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;
import olupis.content.*;
import olupis.world.*;
import olupis.world.blocks.defence.PropellerCoreTurret.*;

import java.util.*;

import static mindustry.Vars.*;
import static olupis.content.NyfUnitTeamMapper.nyfAndBaseTeam;

public class PropellerCoreBlock extends CoreBlock  {
    public TextureRegion blur;
    public boolean singleBlade = false;
    public float rotateSpeed = 7f, offset = 10f, unitTimer = 60f * 35, unitPowerCost = 100;
    public Color lightColorAlt = NyfalisColors.floodLightColor;
    public Seq<CoreMode> modes;
    public UnitType spawns = NyfalisUnits.shade;
    public TextureRegionDrawable[] icons;
    public int unitAmount = 1;

    public PropellerCoreBlock(String name){
        super(name);
        clipSize = 500; //floodlight

        configurable = true;
        hasPower = consumesPower = true;
        clearOnDoubleTap = true;

        modes = Seq.with(
            new CoreMode("mode-storage" ,false, false, true ),
            new CoreMode("mode-factory", true, false, true )
        );

        configs();
    }

    public void configs(){
        consumePowerDynamic((PropellerCoreBuild b) -> b.producingUnits() ? unitPowerCost : 0);
        config(CoreMode.class, (PropellerCoreBuild build, CoreMode i) -> {
            if(!configurable) return;


            if(build.currentMode == i) return;
            if(!modes.contains(i))return;
            build.currentMode = i;
        });
        config(UnitCommand.class, (PropellerCoreBuild build, UnitCommand command) -> build.command = command);

        configClear((PropellerCoreBuild build) -> build.currentMode = modes.get(0));
    }

    @Override
    public void load(){
        icons  = new TextureRegionDrawable[]{Icon.home, Icon.units, Icon.turret};
        blur = Core.atlas.find(name + "-blur");
        super.load();
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("bar.progress", (PropellerCoreBuild entity) ->
            entity.currentMode.stats[0] ? new Bar("bar.progress", Pal.ammo,() -> entity.unitProg / unitTimer) :
            entity.currentMode.stats[1] && entity instanceof PropellerCoreTurretBuild tur ? new Bar("stat.reload", Pal.ammo,() -> tur.reloadF() ) :
            //entity.currentMode().stats[3] ? new Bar("bar.progress", Pal.ammo,() -> entity.unitProg / unitTimer) :

            null);
    }


    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.unitType);
        stats.add(Stat.unitType, table -> {
            table.row();
            table.table(Styles.grayPanel, b -> {
                b.image(unitType.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                b.table(info -> {
                    info.add(unitType.localizedName).left();
                    if(Core.settings.getBool("console")){
                        info.row();
                        info.add(unitType.name).left().color(Color.lightGray);
                    }
                });
                b.button("?", Styles.flatBordert, () -> ui.content.show(unitType)).size(40f).pad(10).right().grow().visible(() -> unitType.unlockedNow());
            }).growX().pad(5).padBottom(0).row();
            table.table(Styles.grayPanel, b -> {
                b.image(spawns.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                b.table(info -> {
                    info.add(spawns.localizedName).left();
                    if(Core.settings.getBool("console")){
                        info.row();
                        info.add(spawns.name).left().color(Color.lightGray);
                    }
                });
                b.button("?", Styles.flatBordert, () -> ui.content.show(spawns)).size(40f).pad(10).right().grow().visible(() -> spawns.unlockedNow());
            }).growX().pad(5).row();
        });
    }

    protected void drawProps(float x, float y, float rotation, float frame, float scl){
        if(!blur.found()) return;
        /*Renders spinny propellers in flight*/
        float length = 1- (thrusterLength * (frame - 1f) - 1f/4f);

        for(int i = 0; i < 4; i++){
            float rot =  (i * 90) + rotation % 90f;
            Tmp.v1.trns(rot, (offset + length) * Draw.xscl);

            Drawf.spinSprite(blur,  x + Tmp.v1.x, y+ Tmp.v1.y, Math.max(rotateSpeed, scl * 2 ) *  Time.time);
        }


    }


    public static class CoreMode{
        //Produce units, enable weapon, accept items
        public boolean[] stats = {false, false, true};
        public String name = "";

        public CoreMode(String name, boolean units, boolean weapon, boolean items){
            this.name = name;
            this.stats = new boolean[]{units, weapon, items};
        }
        
        public boolean[]stats(){
            return stats;
        }
        public String String(){
            return name;
        }

        CoreMode(){}
    }

    public class PropellerCoreBuild extends CoreBuild {
        public @Nullable UnitCommand command;
        public @Nullable CoreMode currentMode;
        public float unitProg = 0;

        @Override
        public void drawLanding(float x, float y){
            float fout = renderer.getLandTime() / launchDuration();

            if(renderer.isLaunching()) fout = 1f - fout;
            float fin = 1f - fout;

            float scl = Scl.scl(4f) / renderer.getDisplayScale();
            float shake = 0f;
            float s = region.width * region.scl() * scl * 3.6f * Interp.pow2Out.apply(fout);
            float rotation = Interp.pow2In.apply(fout) * 135f;
            x += Mathf.range(shake);
            y += Mathf.range(shake);
            float thrustOpen = 0.25f;
            float thrusterFrame = fin >= thrustOpen ? 1f : fin / thrustOpen;

            //when launching, thrusters stay out the entire time.
            if(renderer.isLaunching()){
                Interp i = Interp.pow2Out;
                thrusterFrame = i.apply(Mathf.clamp(fout*13f));
            }

            Draw.rect("circle-shadow", x, y, s, s);

            Draw.scl(scl);

            drawLandingThrusters(x, y, rotation, thrusterFrame);

            Drawf.spinSprite(region, x, y, rotation);

            drawLandingThrusters(x, y, rotation, thrusterFrame);
            Draw.alpha(1f);

            if(teamRegions[team.id] == teamRegion) Draw.color(team.color);

            Drawf.spinSprite(teamRegions[team.id], x, y, rotation);


            Draw.color();

            drawProps(x, y, rotation, thrusterFrame, scl);
            Draw.scl();
            Draw.reset();
        }

        @Override
        protected void drawLandingThrusters(float x, float y, float rotation, float frame){
            /*Renders propeller base in flight*/
            float length = thrusterLength * (frame - 1f) - 1f/4f;
            float alpha = Draw.getColor().a;

            //two passes for consistent lighting
            for(int j = 0; j < 2; j++){
                for(int i = 0; i < 4; i++){
                    var reg = i >= 2 ? thruster2 : thruster1;
                    float rot = (i * 90) + rotation % 90f;
                    Tmp.v1.trns(rot, length * Draw.xscl);

                    //second pass applies extra layer of shading
                    if(j == 1){
                        Tmp.v1.rotate(-90f);
                        Draw.alpha((rotation % 90f) / 90f * alpha);
                        rot -= 90f;
                        Draw.rect(reg, x + Tmp.v1.x, y + Tmp.v1.y, rot);
                    }else{
                        Draw.alpha(alpha);
                        Draw.rect(reg, x + Tmp.v1.x, y + Tmp.v1.y, rot);
                    }
                }
            }
            Draw.alpha(1f);
        }

        @Override
        public void updateLaunch(){
            if (renderer.getLandTime() >= 1f) {
                tile.getLinkedTiles(t -> {
                    if (Mathf.chance(0.65f)) {
                        float rotation = Interp.pow2In.apply(renderer.getLandTime() / launchDuration()) * 540f;
                        /*  -45 so it doesn't end at the corner and align with the propellers*/
                        Fx.coreLandDust.at(t.worldx(), t.worldy(), angleTo(t.worldx() + Mathf.range(0.05f), t.worldy() + Mathf.range(0.25f)) + rotation - 45, Tmp.c1.set(t.floor().mapColor).mul(1.5f + Mathf.range(0.15f)));
                    }
                });

                super.updateLaunch();
            }
        }

        @Override
        public void drawThrusters(float frame) {
            float length = thrusterLength * (frame - 1f) - 1f / 8f;
            for (int i = 0; i < 4; i++) {
                var reg = i >= 2 ? thruster2 : thruster1;
                float dx = Geometry.d4x[i] * length, dy = Geometry.d4y[i] * length;
                Draw.rect(reg, x + dx, y + dy, i * 90);
            }
        }

        @Override
        public void drawLight() {
            if(emitLight)Drawf.light(x, y, fogRadius * 8, lightColorAlt, lightColorAlt.a);
            super.drawLight();
        }

        @Override
        public int getMaximumAccepted(Item item) {
            if(!currentMode.stats[2]) return 0;
            return super.getMaximumAccepted(item);
        }
        @Override
        public void updateTile() {
            block.configurable = true;

            if (currentMode == null || !configurable) {
                if(!modes.isEmpty()) currentMode = modes.get(0);
            } else {
                if(currentMode.stats[0]) unitProg = 0;
                    //only check for ban, shades are always accessible regardless of research
                else if(!unitType.isBanned()){
                    unitProg += edelta() * Vars.state.rules.unitBuildSpeed(team);
                    if(unitProg >= unitTimer) {
                        unitProg %= unitTimer;
                        float rot = (360f/unitAmount);

                        for (int i = 1; i < (unitAmount + 1); i++) {
                            float fx = x, fy = y;

                            if(unitAmount >1){
                                Tmp.v1.trns(rot * i, tilesize * size);
                                fx +=Tmp.v1.x;
                                fy += Tmp.v1.y;
                            }

                            if(Units.canCreate(team, spawns) && !net.client()){
                                Unit unit = spawns.spawn(team, fx, fy);
                                unit.rotation = Angles.angle(fx, fy, x, y);
                                if(unit.isCommandable())unit.command().command(command == null && unit.type.defaultCommand != null ? unit.type.defaultCommand : command);
                                Fx.spawn.at(unit);
                                consume();
                                Events.fire(new EventType.UnitCreateEvent(unit, this));
                            }
                        }
                    }
                }
            }

            super.updateTile();
            block.configurable = true; //pains i cant be bothered copy pasting 2 lines
        }


        @Override
        public boolean shouldHideConfigure(Player player){
            return false; //modes
        }

        @Override
        public void created(){
            super.created();
            configurable = true;
            Events.fire(new CoreChangeEvent(this));
        }

        @Override
        public void buildConfiguration(Table table){
            block.configurable = true;
            Log.err("saudigasidgasg");
            table.table(par -> {
                par.table(t -> {
                    t.background(Styles.black6);
                    var group = new ButtonGroup<ImageButton>();
                    group.setMinCheckCount(0);
                    int i = 0, columns = 6;
                    t.row();
                    for(var item : modes){
                        ImageButton button = t.button(icons[modes.indexOf(item)], Styles.clearNoneTogglei, 45f, () -> {
                            configMode(item);
                            deselect();
                        }).group(group).get();

                        button.update(() -> button.setChecked(item == currentMode));

                        if(++i % columns == 0){
                            t.row();
                        }
                    }
                }).row();
                par.table(t -> {
                    t.defaults().center();
                    t.image().color(team.color.cpy().lerp(Color.black, 0.25f).a(0.55f)).height(2f).center().growX().row();
                }).visible(() -> currentMode.stats[0]).growX().row();
                par.collapser(ta -> {
                    ta.table( t ->{
                        ta.background(Styles.black6);
                        var cmd = new ButtonGroup<ImageButton>();
                        cmd.setMinCheckCount(0);
                        int ic = 0, columnsC = 6;
                        t.row();
                        for(var item : spawns.commands){
                            ImageButton button = ta.button(item.getIcon(), Styles.clearNoneTogglei, 45f, () -> {
                                command = item;
                                configure(item);
                                deselect();
                            }).tooltip(item.localized()).group(cmd).get();

                            button.update(() -> button.setChecked(command == item || (command == null && spawns.defaultCommand == item)));

                            if(++ic % columnsC == 0){
                                t.row();
                            }
                        }
                    });
                },() -> currentMode.stats[0]).row();

                par.table(t ->{
                    if(state.rules.coreBuildAndConfig && (team != state.rules.defaultTeam || team.cores().size != 1)){
                        //no longer calls super bc it has deselect() >n>
                        t.defaults().center().growX().pad(5);
                        ButtonGroup<ImageButton> group = new ButtonGroup<>();
                        group.setMinCheckCount(0);
                        Table cont = new Table();
                        cont.defaults().size(32f);

                        int i = 0;
                        for(Team team : nyfAndBaseTeam){
                            ImageButton button = cont.button(Tex.whiteui, Styles.clearTogglei, 24f, () -> {
                            }).group(group).get();
                            button.changed(() -> {
                                if(button.isChecked()){
                                    configure(team.id);
                                }
                            });
                            button.getStyle().imageUpColor = team.color;
                            button.update(() -> button.setChecked(this.team == team));

                            if(i++ % 3 == 2){
                                cont.row();
                            }
                        }

                        ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
                        pane.setScrollingDisabled(false, false);
                        pane.setOverscroll(false, false);
                        t.add(pane).maxHeight(Scl.scl(40f * 3f)).left();
                        t.row();
                    }
                }).growX();

            });
        }

        /*Config calls are now a functions. as game confuse if two configs with ints crashie
        * This allows for Growing core to have configs for species and mode*/
        public void configMode(CoreMode mode) {
            currentMode = mode;
            configure(mode);
        }

        
        public boolean producingUnits(){
            if(currentMode == null) return false;
            return currentMode.stats[0];
        }
        
        void buildIcon(Table table, CoreMode conf, Drawable icon){
            table.button(icon, Styles.clearNoneTogglei, 40f, () -> {
                currentMode = conf;
                configure(conf);
                deselect();
            }).checked(currentMode == conf);
        }

        @Override
        public byte version() {
            return 3;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            if(currentMode == null) currentMode = modes.get(0);
            write.str(currentMode.name);
            write.f(unitProg);
            TypeIO.writeCommand(write, command);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){
                if(revision >= 3 ){
                    currentMode = modes.find( m -> Objects.equals(m.name, read.str()));

                } else currentMode = modes.get(read.i());

                unitProg = read.f();
            }
            if(revision >=2){
                command = TypeIO.readCommand(read);
            }
        }

        @Override
        public Object config() {
            return currentMode;
        }


        @Override
        public BlockStatus status(){
            if (!this.enabled) return BlockStatus.logicDisable;
            else if (!currentMode.stats[0] &&! currentMode.stats[1] && currentMode.stats[2]) return BlockStatus.active;
            else if (!this.shouldConsume()) return BlockStatus.noOutput;
            else if (!(this.efficiency <= 0.0F) && this.productionValid()) return Vars.state.tick / (double)30.0F % (double)1.0F < (double)this.efficiency ? BlockStatus.active : BlockStatus.noInput;
            else return BlockStatus.noInput;
        }

        @Override
        public void draw(){
            super.draw();

            Seq<TextureRegion> out = new Seq<>();
            out.add(icons[modes.indexOf(currentMode)].getRegion());
            out.add(currentMode.stats[0] ? command != null ? command.getIcon().getRegion() : spawns.defaultCommand.getIcon().getRegion() :  Core.atlas.find("error"));
            NyfWorldFuckingHelper.renderConfigIndicator(this, out);
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source){
            boolean incinerate = NyfalisItemsLiquid.nyfalisCompentItems.contains(item);
            int realAmount = incinerate ? 0 : Math.min(amount, storageCapacity - items.get(item));

            if(incinerate){
                Log.err("asidjoasjd");
                if(wasVisible)Fx.fuelburn.at(x, y);
                this.noSleep();
                return;
            }

            super.handleStack(item, realAmount, source);
        }

        @Override
        public void handleItem(Building source, Item item){
            boolean incinerate = NyfalisItemsLiquid.nyfalisCompentItems.contains(item);

            if(!noEffect && incinerate){
                incinerateEffect(this, source);
                noEffect = false;
                return;
            }

            super.handleItem(source, item);
        }
    }
}
