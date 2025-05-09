package olupis.world.blocks.processing;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
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

import static mindustry.Vars.*;


public class HeadacheCrafter  extends GenericCrafter{
    public Seq<FactoryPlan> plans = new Seq<>();
    private static TextField search;
    private static int rowCount;

    public HeadacheCrafter(String name){
        super(name);
        configurable = true;
        acceptsItems = true;

        config(Integer.class, (HeadacheCrafterBuild build, Integer i) -> {
            if(!configurable) return;
            if(i >= plans.size || i <= -1){
                build.planSelected = 0;
                return;
            }
            build.planSelected = i;
        });


        configClear((HeadacheCrafterBuild build) -> build.planSelected = 0);

        consume(new ConsumeItemDynamic((HeadacheCrafterBuild e) -> e.planSelected != -1 ? plans.get(Math.min(e.planSelected, plans.size - 1)).input : ItemStack.empty));
        //consume(new ConsumeLiquidsDynamic((HeadacheCrafterBuild e) -> e.planSelected != -1 ? plans.get(Math.min(e.planSelected, plans.size - 1)).inputLiquid : LiquidStack.empty));
    }


    public class HeadacheCrafterBuild extends GenericCrafterBuild{
        public int planSelected;

        @Override
        public void updateTile(){
            if(plans.size <= 0) return;
            if(planSelected <= -1 || planSelected > plans.size) planSelected = 1;
            FactoryPlan plan = plans.get(planSelected);

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

            dumpOutputs();
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
        public void buildConfiguration(Table table){
            Seq<UnlockableContent> units = Seq.with(plans).map(u -> u);
            if(state.isCampaign()) units.retainAll(y -> y.unlockedNowHost());
            else if(!state.isEditor()) units.retainAll(y -> !state.rules.isBanned((Block)y));

            if(units.size >= 1){
                buildTable(HeadacheCrafter.this,
                table,
                units,
                () -> plans.get(planSelected),
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

        @Override
        public boolean acceptItem(Building source, Item item){
            return planSelected != -1 && items.get(item) < getMaximumAccepted(item) &&
            Structs.contains(plans.get(planSelected).input, stack -> stack.item == item);
        }

        @Override
        public Object config(){
            return plans.get(planSelected);
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

}
