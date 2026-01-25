package olupis.world.blocks.processing;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.Color;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.Image;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.*;
import arc.scene.ui.layout.Stack;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import olupis.content.*;
import olupis.world.blocks.drawers.*;

import java.awt.*;
import java.util.*;

import static mindustry.Vars.*;


public class HeadacheCrafter  extends GenericCrafter{
    public Seq<FactoryPlan> plans = new Seq<>();
    private static TextField search;
    private static int rowCount;

    public HeadacheCrafter(String name){
        super(name);
        configurable = hasPower = outputsPower  =  true;
        acceptsItems = true;

        config(Integer.class, (HeadacheCrafterBuild build, Integer i) -> {
            if(!configurable) return;

            if(i >= plans.size || i <= -1){
                build.planSelected = plans.get(0).unlockedNowHost() ? 0 : -1;
                return;
            }
            build.planSelected = plans.get(i).unlockedNowHost() ? i : -1;
        });

        consumePowerDynamic((HeadacheCrafterBuild b) -> !b.invalidPlan()? plans.get(b.planSelected).powerIn * b.efficiency : 0f);

        configClear((HeadacheCrafterBuild build) -> build.planSelected = 0);

        drawer = new DrawMulti(new DrawDefault(),new PlanDrawer());
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.output);
        stats.remove(Stat.productionTime);
        stats.add(Stat.output, table -> {
            table.row();

            float[] widths = new float[] {0, 0, 0};
            Seq<Cell<Table>> inTab = new Seq<>(), arwTab  = new Seq<>(), outTab = new Seq<>();
            table.table(nu -> plans.each(pl -> {
                nu.row();
                nu.table(Styles.grayPanel, b -> {
                        if(pl.isBanned()){
                            b.table(e -> {
                                e.image(Icon.cancel.getRegion()).color(Color.scarlet).scaling(Scaling.bounded).row();
                            }).center();

                            b.table(e -> {
                            if(pl.output != null && pl.output.length >= 1) for(ItemStack stack : pl.output) e.add(StatValues.displayItem(stack.item, 0, false)).pad(5).row();
                            if(pl.outputLiquid != null && pl.outputLiquid.length >= 1) for(LiquidStack stack : pl.outputLiquid) e.add(displayLiquid(stack.liquid, 01f, false)).pad(5).row();
                        }).padLeft(5f);
                    }else if(!pl.unlockedNow()){
                        b.table(e -> {
                            e.image(Icon.tree.getRegion()).color(Color.white).scaling(Scaling.bounded).row();
                        }).center();

                        b.table(e -> {
                            if(pl.output != null && pl.output.length >= 1) for(ItemStack stack : pl.output) e.add(StatValues.displayItem(stack.item, 0, false)).pad(5).row();
                            if(pl.outputLiquid != null && pl.outputLiquid.length >= 1) for(LiquidStack stack : pl.outputLiquid) e.add(displayLiquid(stack.liquid, 01f, false)).pad(5).row();
                        }).padLeft(5f);
                    }else {
                        Cell<Table>  in = b.table( e -> {
                            if(pl.input != null && pl.input.length >= 1) for(ItemStack stack : pl.input) e.add(StatValues.displayItem(stack.item, stack.amount, pl.time, true)).pad(5).left().row();
                            if(pl.inputLiquid != null && pl.inputLiquid.length >= 1) for(LiquidStack stack : pl.inputLiquid) e.add(displayLiquid(stack.liquid, stack.amount, true)).left().pad(5).row();
                            if(pl.powerIn > 0) e.add("[accent]" + Iconc.power + " []" + Mathf.round(pl.powerIn * 60f) + "[lightgray] " + StatUnit.perSecond.localized()).left();
                        }).left().padLeft(5f);
                        inTab.add(in);
                        widths[0] = Math.max(in.minWidth(), widths[0]);

                        Cell<Table> arw = b.table( e -> {
                            e.image(Icon.right.getRegion()).scaling(Scaling.bounded).growX().row();
                            e.add("[lightgray]" + Strings.autoFixed(pl.time /60f, 2) + StatUnit.perSecond.localized());
                        }).center().pad(5f);
                        arwTab.add(arw);
                        widths[1] = Math.max(arw.minWidth(), widths[1]);

                        Cell<Table>  out = b.table( e -> {
                            if(pl.output != null && pl.output.length >= 1) for(ItemStack stack : pl.output) e.add(StatValues.displayItem(stack.item, stack.amount, pl.time, true)).left().pad(5).row();
                            if(pl.outputLiquid != null && pl.outputLiquid.length >= 1) for(LiquidStack stack : pl.outputLiquid) e.add(displayLiquid(stack.liquid, stack.amount, true)).left().pad(5).row();
                            if(pl.powerOut > 0) e.add("[accent]" + Iconc.power + " []" + Mathf.round(pl.powerOut * 60f) + "[lightgray] " + StatUnit.perSecond.localized()).left();
                        }).right().padRight(5f).padLeft(5f);
                        widths[2] = Math.max(out.minWidth(), widths[2]);
                        outTab.add(out);
                    }
                }).growX().pad(5).margin(20).row();
            })).minWidth((widths[0] + widths[1] + widths [2]) * 1.25f).growX();
            for(int i = 0; i < inTab.size; i++) inTab.get(i).width(widths[0]);
            for(int i = 0; i < arwTab.size; i++) arwTab.get(i).width(widths[1]);
            for(int i = 0; i < outTab.size; i++) outTab.get(i).width(widths[2]);
            Log.err(widths[0] + " " + widths[1] + " " + widths[2] );

        });
    }

    @Override
    public void init(){
        consume(new ConsumeItemDynamic((HeadacheCrafterBuild e) -> !e.invalidPlan() && e.getPlanSelected().input != null ? e.getPlanSelected().input : ItemStack.empty));
        consume(new ConsumeLiquidsDynamic((HeadacheCrafterBuild e) -> !e.invalidPlan() && e.getPlanSelected().inputLiquid != null ? e.getPlanSelected().inputLiquid : LiquidStack.empty));

        super.init();

        Seq<ItemStack> outputs = new Seq<>();
        for( FactoryPlan plan : plans ){
            if(plan.inputLiquid != null){
                for(LiquidStack stack : plan.inputLiquid){
                    liquidFilter[stack.liquid.id] = true;
                }
            }
            if(plan.output != null){
                for(ItemStack stack : plan.output){
                        outputs.add(stack.copy());
                }
            }
            plan.displayFactory.add(this);
        }
        outputItems  = new ItemStack[outputs.size];
        for(int is = 0; is < outputs.size; is++) outputItems[is] = outputs.get(is);
    }

    public class HeadacheCrafterBuild extends GenericCrafterBuild{
        public int planSelected = -1;
        public float peekEff;

        @Override
        public void updateTile(){
            dumpOutputs();
            if(plans.size <= 0 || invalidPlan()) return;
            if(planSelected > plans.size || planSelected <= -1) planSelected = 0;

            FactoryPlan plan = plans.get(planSelected);
            if(!plan.unlockedNowHost()) planSelected = -1;
            if(planSelected <= -1) return;


            if(efficiency > 0){
                progress += getProgressIncrease(plan.time);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                //continuously output based on efficiency
                if(plan.inputLiquid != null && this.liquids != null){
                    float inc = getProgressIncrease(1f);
                    for(LiquidStack output : plan.outputLiquid){
                        handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                if(wasVisible && Mathf.chanceDelta(updateEffectChance)){
                    updateEffect.at(x + Mathf.range(size * updateEffectSpread), y + Mathf.range(size * updateEffectSpread));
                }
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            //TODO may look bad, revert to edelta() if so
            totalProgress += warmup * Time.delta;

            if(progress >= 1f){
                craft();
            }
        }

        @Override
        public void craft(){
            consume();
            FactoryPlan plan = plans.get(planSelected);

            if(plan.output != null){
                for(var output : plan.output){
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                }
            }

            if(wasVisible){
                craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        @Override
        public float getPowerProduction(){
            if(!enabled || invalidPlan()) return 0f;

            return plans.get(planSelected).powerOut * efficiency;
        }

        public boolean invalidPlan(){
            return planSelected <= -1 || planSelected >= plans.size || plans.get(planSelected) == null;
        }

        @Override
        public void buildConfiguration(Table table){

            if(getLivePlans().size >= 1){
                buildTable(HeadacheCrafter.this,
                table,
                getLivePlans(),
                () -> invalidPlan() ? null: plans.get(planSelected),
                p -> {
                    int i  = plans.indexOf(f -> f  == p);
                    configure(i);
                    planSelected = i;

                },
                selectionRows,
                selectionColumns);

                table.row();
            }else{
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }
        }

        public Seq<FactoryPlan> getLivePlans(){
            Seq<FactoryPlan> livePlans = Seq.with(plans).map(u -> u);
            if(state.isCampaign()) livePlans.retainAll(UnlockableContent::unlockedNowHost);
            else if(!state.isEditor()) livePlans.retainAll(y -> !state.rules.isBanned(y));
            return livePlans;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return planSelected != -1 && items.get(item) < getMaximumAccepted(item) &&
            Structs.contains(plans.get(planSelected).input, stack -> stack.item == item);
        }

        @Override
        public Object config(){
            return !invalidPlan() ? plans.get(planSelected) : null;
        }

        @Override
        public void placed(){
            super.placed();

            if(getLivePlans().size >= 1) planSelected = plans.indexOf(getLivePlans().first());
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(planSelected);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 2)planSelected =read.i();
        }

        @Override
        public BlockStatus status(){
            if(planSelected <= -1) return BlockStatus.noOutput;
            return super.status();
        }

        public FactoryPlan getPlanSelected(){
            return plans.get(planSelected);
        }
    }


    public static <T extends UnlockableContent> void buildTable(Block block, Table table, Seq<T> items, Prov<T> holder, Cons<T> consumer, int rows, int columns){
        buildTable(block, table, items, holder, consumer, true, rows, columns);
    }

    public static <T extends UnlockableContent> void buildTable(@Nullable Block block, Table table, Seq<T> items, Prov<T> holder, Cons<T> consumer, boolean closeSelect, int rows, int columns){
        ButtonGroup<ImageButton> group = new ButtonGroup<>();
        group.setMinCheckCount(0);
        Table cont = new Table().top();
        cont.defaults().size(40);

        if(search != null) search.clearText();

        Runnable rebuild = () -> {
            group.clear();
            cont.clearChildren();

            var text = search != null ? search.getText() : "";
            int i = 0;
            rowCount = 0;

            Seq<T> list = items.select(u -> (text.isEmpty() || u.localizedName.toLowerCase().contains(text.toLowerCase())));
            for(T item : list){
                //No checks here
                ImageButton button = cont.button(Tex.whiteui, Styles.clearNoneTogglei, Mathf.clamp(item.selectionSize, 0f, 40f), () -> {
                    if(closeSelect) control.input.config.hideConfig();
                }).tooltip(item.localizedName).group(group).get();
                button.changed(() -> consumer.get(button.isChecked() ? item : null));
                button.getStyle().imageUp = new TextureRegionDrawable(item.uiIcon);
                button.update(() -> button.setChecked(holder.get() == item));

                if(i++ % columns == (columns - 1)){
                    cont.row();
                    rowCount++;
                }
            }
        };

        rebuild.run();

        Table main = new Table().background(Styles.black6);
        if(rowCount > rows * 1.5f){
            main.table(s -> {
                s.image(Icon.zoom).padLeft(4f);
                search = s.field(null, text -> rebuild.run()).padBottom(4).left().growX().get();
                search.setMessageText("@players.search");
            }).fillX().row();
        }

        ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
        pane.setScrollingDisabled(true, false);
        pane.exited(() -> {
            if(pane.hasScroll()){
                Core.scene.setScrollFocus(null);
            }
        });

        if(block != null){
            pane.setScrollYForce(block.selectScroll);
            pane.update(() -> {
                block.selectScroll = pane.getScrollY();
            });
        }

        pane.setOverscroll(false, false);
        main.add(pane).maxHeight(40 * rows);
        table.top().add(main);
    }

    public static Table displayLiquid(Liquid liquid, float amount, boolean perSecond){
        Table t = new Table();

        t.add(new Stack(){{
            add(new Image(liquid.uiIcon).setScaling(Scaling.fit));

            if(amount >= 1){
                Table t = new Table().left().bottom();
                t.add(Strings.autoFixed(amount, 2)).style(Styles.outlineLabel);
                add(t);
            }
        }}).size(iconMed).padRight(1  + (amount != 0 ? (Strings.autoFixed(amount, 2).length() - 1) * 2.5f : 0)).with(s -> StatValues.withTooltip(s, liquid, false));

        if(amount <= -1)return t;
        t.table(ta ->{
            ta.add(liquid.localizedName);
            if(perSecond && amount != 0){
                ta.row();
                ta.add(StatUnit.perSecond.localized()).padLeft(2).padRight(5).color(Color.lightGray).style(Styles.outlineLabel);
            }
        });

        return t;
    }
}
