package olupis.world.blocks.unit;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.UnitAssembler.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import olupis.content.*;
import olupis.world.blocks.*;
import olupis.world.blocks.defence.Articulator.*;
import olupis.world.consumer.*;

import static mindustry.Vars.*;

public class Fabricator extends Reconstructor {
    public Liquid baseLube = Liquids.oil;
    public float lubeMultiplier = 3f;
    public @Nullable ConsumeLiquidBase lubrication;
    protected @Nullable ConsumePayloadDynamic consPayload;
    public static Seq<PayloadStack>[] savedRequirements;
    public Seq<UnlockableContent> requirementList = new Seq<>();
    public Block statArticulator;

    public Fabricator(String name){
        super(name);
    }

    @Override
    public void init() {
        consume(consPayload = new ConsumePayloadDynamic(FabricatorBuild::currentRequirements));
        consumeBuilder.each(c -> c.multiplier = b -> state.rules.unitCost(b.team));

        super.init();
        if (lubrication == null) {
            lubrication = findConsumer(c -> c instanceof ConsumeLubricant);
        }

        savedRequirements = new Seq[upgrades.size];
        for(int i = 0; i < savedRequirements.length; i++){
            if(upgrades.get(i).length >= 3){
                savedRequirements[i] = new Seq<>();
                requirementList.add(upgrades.get(i)[0]); //blacklist
                for(int j = 2; j < upgrades.get(i).length ; j++){
                    savedRequirements[i].add(PayloadStack.with(upgrades.get(i)[j], 1));

                }
            } else savedRequirements[i] = PayloadStack.list();
        }

    }

    @Override
    public void setStats(){
        super.setStats();

        stats.remove(Stat.output);
        stats.add(Stat.output, table -> {
            table.row();
            Seq<UnitType[]> base = new Seq<>(), alt = new Seq<>();

            for(var upgrade : upgrades){
                if(upgrade.length > 2) alt.add(upgrade);
                else base.add(upgrade);
            }
            for(int i = 0; i < 2; i++){
                Seq<UnitType[]> in  = i == 1 ? alt : base;
                int finalI = i;
                table.table(tab ->{
                    for(var upgrade : in){
                        if(upgrade[0].unlockedNow() && upgrade[1].unlockedNow()){
                            tab.table(Styles.grayPanel, t -> {
                                t.left();

                                t.image(upgrade[0].uiIcon).size(40).pad(10f).left().scaling(Scaling.fit).with(im -> StatValues.withTooltip(im, upgrade[0]));
                                t.table(info -> {
                                    info.add(upgrade[0].localizedName).left();
                                    info.row();
                                }).pad(10).padBottom(0).left();
                                if(upgrade.length > 2){
                                    t.row();
                                    t.table(Styles.grayPanelDark, info -> {
                                        int[] count = {0};
                                        for(int j = 2; j < upgrade.length; j++){
                                            int finalJ = j;
                                            info.image(upgrade[j].uiIcon).size(30).pad(10f).center().scaling(Scaling.fit).with(im -> StatValues.withTooltip(im, upgrade[finalJ], true));
                                            count[0]++;
                                            if(count[0] >= 3){
                                                count[0] = 0;
                                                info.row();
                                            }
                                        }
                                    }).fillX().pad(10).padTop(0).left();
                                }

                            }).fill().padTop(5).padBottom(5);

                            tab.table(Styles.grayPanel, t -> {

                                t.image(Icon.right).color(Pal.darkishGray).size(40).pad(10f);
                            }).fill().padTop(5).padBottom(5);

                            tab.table(Styles.grayPanel, t -> {
                                t.left();

                                t.image(upgrade[1].uiIcon).size(40).pad(10f).right().scaling(Scaling.fit).with(im -> StatValues.withTooltip(im, upgrade[1]));
                                t.table(info -> {
                                    info.add(upgrade[1].localizedName).right();
                                    info.row();
                                }).pad(10).right();
                            }).fill().padTop(5).padBottom(5);

                            tab.row();
                        }
                    }
                }).fill().row();
                if(i == 0){
                    table.image().color(Pal.accent).height(3.0F).fill().padTop(5).padBottom(5).row();
                    table.add(new Table(NyfalisColors.infoPanel, r ->{
                        r.add(new Table(c ->{
                            c.add(new Table(o -> {
                                o.add(new Image(statArticulator.uiIcon)).size(32f).scaling(Scaling.fit);
                            })).left().pad(10f);
                            c.table(info -> {
                                info.add(statArticulator.localizedName).left();
                                if (Core.settings.getBool("console")) {
                                    info.row();
                                    info.add(statArticulator.name).left().color(Color.lightGray);
                                }
                            });
                            c.button("?", Styles.flatBordert, () -> ui.content.show(statArticulator)).size(40f).pad(10).right().grow().visible(statArticulator::unlockedNow);
                        })).row();
                        //todo
//                        r.add(new Table(i -> {
//                            i.button(Icon.upOpen, Styles.emptyi, () -> show[1] = !show[1]).update(iu -> iu.getStyle().imageUp = (!show[1] ? Icon.upOpen : Icon.downOpen)).pad(10).padRight(4).left();
//                            for (ItemStack stack : requiredAlternate) {
//                                i.table(z -> { // BE/v8 removed itemsDisplay & Rushie cant be bother to set up compiling equivalent so this is why this exists
//                                    z.add(new Table(o -> {
//                                        o.left();
//                                        o.add(new Image(stack.item.uiIcon)).size(32f).scaling(Scaling.fit);
//                                    }));
//                                    z.add(new Table(t -> {
//                                        t.left().bottom();
//                                        t.add(stack.amount >= 1000 ? UI.formatAmount(stack.amount) : stack.amount + "").style(Styles.outlineLabel);
//                                        t.pack();
//                                    }));
//                                });
//                            }
//                        }));
                    })).fill();


                    table.row();
                }
            }

        });
    }

    public class FabricatorBuild extends ReconstructorBuild implements Moduleable{
        public boolean constructing;
        public Seq<ArticulatorBuild> modules = new Seq<>();
        public PayloadSeq blocks = new PayloadSeq();

        @Override
        public void display(Table table){
            super.display(table);
            table.row();
        }

        @Override
        public void updateTile(){
            constructing = constructing();
            float lubeMul = updateLube();


            boolean valid = false;

            if(payload != null){
                //check if offloading
                if(!hasUpgrade(payload.unit.type)){
                    moveOutPayload();
                } else if (blocks.contains(payload.unit.type)){
                    if(moveInPayload()){
                        Tmp.v4.set(x, y);
                        Fx.payloadDeposit.at(payload.x(), payload.y(), rotation, new YeetData(Tmp.v4.cpy(), payload.content()));
                    }
                }else{ //update progress
                    if(moveInPayload()){
                        if(efficiency > 0){
                            valid = true;
                            progress += edelta() * state.rules.unitBuildSpeed(team) * lubeMul *   consPayload.efficiency(this);
                        }

                        //upgrade the unit
                        if(progress >= constructTime){
                            payload.unit = upgrade(payload.unit.type).create(payload.unit.team());

                            if(payload.unit.isCommandable()){
                                if(commandPos != null){
                                    payload.unit.command().commandPosition(commandPos);
                                }
                                if(command != null){
                                    //this already checks if it is a valid command for the unit type
                                    payload.unit.command().command(command);
                                }
                            }

                            progress %= 1f;
                            Effect.shake(2f, 3f, this);
                            Fx.producesmoke.at(this);
                            consume();
                            Events.fire(new EventType.UnitCreateEvent(payload.unit, this));
                            blocks.clear();
                        }
                    }
                }
            }

            speedScl = Mathf.lerpDelta(speedScl, Mathf.num(valid), 0.05f);
            time += edelta() * speedScl * state.rules.unitBuildSpeed(team) * lubeMul;
        }

        public float updateLube(){
            float out = 1f;
            if(lubrication == null) return out;
            if(this.liquids == null) return out;
            if(lubrication.efficiency(this) == 0) return out;
            if(efficiency == 0) return out;

            if(lubrication instanceof ConsumeLiquidFilter filter  && filter.getConsumed(this) != baseLube){
//                float capacity = lubrication instanceof ConsumeLiquidFilter filter ? filter.getConsumed(this).heatCapacity : 1f;
//                float amount = lubrication.amount * lubrication.efficiency(this);

                out += edelta() * lubeMultiplier * Math.abs(filter.getConsumed(this).heatCapacity - baseLube.heatCapacity ) * (lubrication.amount * lubrication.efficiency(this));
                //out += amount * edelta() * capacity * lubeMultiplier;

            }
            return out;
        }

        @Override
        public Seq<ArticulatorBuild> getModules(){
            return modules;
        }

        @Override
        public int minTier(){
            return 2;
        }

        @Override
        public void updateEfficiencyMultiplier(){
            super.updateEfficiencyMultiplier();

            if(modules.size > 0){
                efficiency *= moduleEfficiency() / modules.size;
            }
        }

        public UnitType[] currentPlan(){
            if(payload == null) return null;
            return upgrades.find( u -> u[0] == payload.unit.type);
        }

        public Seq<PayloadStack> currentRequirements(){
            if(payload == null || !upgrades.contains(currentPlan())) return PayloadStack.list();
            return savedRequirements[upgrades.indexOf(currentPlan())];
        }

        @Override
        public boolean hasUpgrade(UnitType type){
            UnitType[] p = upgrades.find(u -> u[0] == type);
            if(p != null && p.length > 2 && modules.size <= 0) return false;

            UnitType t = upgrade(type);
            return t != null && (t.unlockedNowHost() || team.isAI()) && !type.isBanned();
        }

        @Override
        public PayloadSeq getPayloads(){
            return blocks;
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            if(currentRequirements().find( b-> b.item == payload.content()) != null){
                blocks.add(payload.content(), 1);
                return;
            }
            super.handlePayload(source, payload);
        }


        @Override
        public boolean acceptPayload(Building source, Payload payload){
            if(modules.size <= 0 && requirementList.contains(payload.content())){
                if(payload.content() != null && payload instanceof UnitPayload p) p.showOverlay(Icon.settings);
                return false;
            }
            if(currentRequirements().find( b-> b.item == payload.content()) != null && !blocks.contains( payload.content())) return true;
            return super.acceptPayload(source, payload);
        }

        @Override
        public boolean constructing(){
            return super.constructing() && consPayload.efficiency(this) > 0 ;
        }

        @Override
        public boolean shouldConsume(){
            return constructing && enabled && consPayload.efficiency(this) > 0 ;
        }
    }
}
