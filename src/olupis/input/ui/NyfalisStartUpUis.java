package olupis.input.ui;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.editor.*;
import mindustry.game.*;
import mindustry.game.Rules.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.ui.fragments.*;
import olupis.*;
import olupis.content.*;
import olupis.world.entities.packets.*;

import java.util.*;

import static mindustry.Vars.*;

public class NyfalisStartUpUis {
    public static Table debugTable = new Table();

    public static void  disclaimerDialog(){
        BaseDialog dialog = new BaseDialog("@nyfalis-disclaimer.name");
        dialog.centerWindow();

        dialog.cont.setOrigin(Align.center);
        dialog.cont.table(t -> {
            t.defaults().growY().growX().center();

            Label header = new Label("@nyfalis-disclaimer.header");
            Label body = new Label("@nyfalis-disclaimer.body");
            Label funny = new Label("@nyfalis-disclaimer.funny");
            header.setAlignment(Align.center);
            header.setWrap(true);
            body.setWrap(true);
            body.sizeBy(10f);
            body.setAlignment(Align.center);

            t.add(header).fontScale(1.2f).row();

            if(Mathf.random(1, 200) == 1 || (Core.input.keyDown(KeyCode.altLeft) && Core.input.keyDown(KeyCode.shiftLeft))){
                TextureRegion icon = NyfalisUnits.gnat.uiIcon;
                t.table(a ->{
                    a.image(icon).scaling(Scaling.bounded).row();
                    a.add(funny).center().growX().row();
                }).maxSize(700).margin(14).pad(3).center().row();
            } else {
                /*Very convoluted way to load the mod icon, because I'm not bright to think of any other way*/
                @Nullable TextureRegion icon = new TextureRegion(mods.list().find(a -> Objects.equals(a.name, "olupis")).iconTexture);
                t.table(a -> a.image(icon).scaling(Scaling.bounded).row()).tooltip("Art By RushieWashie").maxSize(700).margin(14).pad(3).center().row();
            }
            t.add(body).row();


        }).growX().growY().center().top().row();

        dialog.cont.button("@back", Icon.left, dialog::hide).padTop(-1f).size(220f, 55f).bottom();
        dialog.closeOnBack();
        dialog.show();
    }

    public static void  saveDisclaimerDialog(){
        //This is here bc in the beta versions, placeholder was int (`1`) and would crash with the new check
        @Nullable float lVer = Float.parseFloat(Core.settings.get("nyf-lastver", 0).toString());
        if((lVer >= NyfalisSectors.sectorVersion)){
            Log.info("Nyf data: @ >= @ ", lVer, NyfalisSectors.sectorVersion);
            return;
        }
        if(lVer == 0){
            Log.info("no nyf data detected! (0)");
            return;
        }

        boolean hasSave = false;
        for (Planet p : content.planets()) {
            if (!p.name.contains("olupis-")) continue;
            if(hasSave) break;
            for (Sector s : p.sectors) {
                if(s.hasSave()) hasSave = true;
                break;
            }
        }
        if(!hasSave)return;
        showSaveDisclaimerDialog();
    }

    public static void showSaveDisclaimerDialog(){
        float lVer = Float.parseFloat(Core.settings.get("nyf-lastver", 0).toString());
        BaseDialog dialog = new BaseDialog("@nyfalis-disclaimer.name");
        dialog.centerWindow();

        dialog.cont.setOrigin(Align.center);
        dialog.cont.table(t -> {
            t.defaults().growY().growX().center();

            Label body = new Label(Core.bundle.format("nyfalis-disclaimer.save", lVer, NyfalisSectors.sectorVersion));
            body.setWrap(true);
            body.setAlignment(Align.center);
            t.add(body).row();

            if(lVer <= 1.1f ){ //v7
                Label seven = new Label(Core.bundle.get("nyfalis-disclaimer.youretoooldman"));
                seven.setWrap(true);
                seven.setAlignment(Align.center);
                t.add(seven).fontScale(1.25f).padTop(0).row();
            }


        }).growX().growY().center().top().row();

        dialog.cont.button("@back", Icon.left, () -> {
            dialog.hide();
            Core.settings.put("nyf-lastver", NyfalisSectors.sectorVersion);
        }).padTop(-1f).size(220f, 55f).bottom();
        dialog.closeOnBack( () -> {
            Core.settings.put("nyf-lastver", NyfalisSectors.sectorVersion);
        });
        dialog.show();
    }


    public static void buildDebugUI(Group group){

        group.fill(t -> {
            t.name = "nyfalis-debug-cont";
            t.visible(() -> Vars.ui.hudfrag.shown);
            t.bottom().left();
            rebuildDebugTable();
            t.table(tab -> tab.add(debugTable)).row();
        });
    }

    public static void rebuildDebugTable(){
        debugTable.reset();
        debugTable.clear();

        debugTable.visibility = () -> !state.isEditor() ||  !Core.settings.getBool("editor-blocks-shown");
        if(state.isEditor()){
            debugTable.table(Tex.pane, z -> {
                ImageButton button = z.button(Tex.whiteui, Styles.clearNoneTogglei, 33f, () -> Call.setPlayerTeamEditor(player,  NyfUnitTeamMapper.verdentTeam))
                .size(45f).margin(2f).get();
                button.getImageCell().grow();
                button.getStyle().imageUpColor = NyfUnitTeamMapper.verdentTeam.color;
                button.update(() -> button.setChecked(player.team() ==  NyfUnitTeamMapper.verdentTeam));
            });

            debugTable.row();
        }
        //debug and if someone needs to convert a map and said map does not have the Nyfalis Block set / testing

        if( Core.settings.getBool("nyfalis-debug")){
            CustomRulesDialog ruleInfo = Reflect.get(ui.paused, "rulesDialog");
            WaveInfoDialog waveInfo = new WaveInfoDialog();

            debugTable.table( z -> {
                z.button("E", Icon.export, Styles.squareTogglet, () -> {
                    if(player.admin && net.active() && net.client()){
                        NyfalisDebugPackets packet = new NyfalisDebugPackets();
                        packet.type = 1;
                        Vars.net.send(packet, true);
                    } else {
                        state.rules.blockWhitelist = true;
                        NyfalisPlanets.nyfalis.applyRules(state.rules);
                        NyfalisMain.sandBoxCheck();
                        ui.paused.show();
                    }
                }).width(77.5f).height(40f).checked(false).tooltip("Apply Nyfalis Settings/Env to in.current game");
                z.button("C", Icon.down, Styles.squareTogglet, () -> {
                    if(state.isCampaign()) Logic.sectorCapture();
                    state.wave += 100;
                    for (Item i : content.items()){
                        if(Core.input.keyDown(Binding.boost)) player.team().core().items.add(i, player.team().core().storageCapacity);
                        else if (i.unlocked())player.team().core().items.add(i, player.team().core().storageCapacity);
                    }

                }).width(77.5f).height(40f).checked(false).tooltip("Capture Sector & fill core with Items");
                z.row();
            }).width(155f).growY().margin(12f).checked(false).row();
            debugTable.button("@editor.rules", Icon.list, Styles.squareTogglet, ()->{
                ruleInfo.show(Vars.state.rules, () -> Vars.state.rules = new Rules());
            }).width(155f).height(40f).margin(12f).checked(false).row();
            debugTable.button("@editor.waves", Icon.waves, Styles.squareTogglet, waveInfo::show).width(155f).height(40f).margin(12f).checked(false);
            if(mobile || testMobile){
                debugTable.row();
                debugTable.add(new Element()).width(155f).height(50f).margin(12f).touchable( Touchable.disabled);
            }
        }
        debugTable.marginBottom(200f);
    }

    public static void loadHints(){
        ui.hints.hints.addAll(new HintsFragment.Hint() {
            @Override
            public String name() {return "hint.nyflais-command";}

            @Override
            public String text() {return Core.bundle.get("hint.nyflais-command.text");}

            @Override
            public boolean complete() {return Binding.commandMode.value.key != Binding.boost.value.key;}

            @Override
            public boolean show() {return Binding.commandMode.value.key == Binding.boost.value.key;}

            @Override
            public int order() {return 5;}

            @Override
            public boolean valid() {return !Vars.mobile ||  control.input instanceof DesktopInput;}
        },new HintsFragment.Hint() {
            @Override
            public String name() {return "hint.nyflais-end-of-content";}

            @Override
            public String text() {return Core.bundle.get("hint.nyflais-end-of-content.text");}

            @Override
            public boolean complete() {return state.isCampaign() &&  (state.getPlanet() == NyfalisPlanets.nyfalis || state.getPlanet() == NyfalisPlanets.vorgin);}

            @Override
            public boolean show() {return state.isCampaign() && (state.getPlanet() == NyfalisPlanets.nyfalis || state.getPlanet() == NyfalisPlanets.vorgin);}

            @Override
            public int order() {return 6;}

            @Override
            public boolean valid() {return true;}
        }, new HintsFragment.Hint() {
            @Override
            public String name() {return "hint.nyflais-optional-sectors";}

            @Override
            public String text() {return Core.bundle.get("hint.nyflais-optional-sectors.text");}

            @Override
            public boolean complete() {return state.isCampaign() && NyfalisPlanets.isNyfalianPlanet(state.getPlanet()) && state.getSector().preset == null && state.getSector().isCaptured() ;}

            @Override
            public boolean show() {return state.isCampaign() &&  NyfalisPlanets.isNyfalianPlanet(state.getPlanet()) && state.getSector().preset == null;}

            @Override
            public int order() {return 7;}

            @Override
            public boolean valid() {return true;}
        });
    }

    public static void nyfAdditionalRules(CustomRulesDialog in){
        Seq<Runnable> additionalSetup = Reflect.get(in, "additionalSetup");
        additionalSetup.add( () -> Time.run(2 * Time.toSeconds, () -> {

            Rules rules = Reflect.get(in, "rules");
            boolean[] shown = {false};
            Table wasCurrent = in.current;

                Table teamRules = new Table(); // just button and collapser in one table
            teamRules.button(NyfUnitTeamMapper.verdentTeam.coloredName(), Icon.downOpen, Styles.togglet, () -> {
                shown[0] = !shown[0];
            }).marginLeft(14f).width(260f).height(55f).update(t -> {
                ((Image)t.getChildren().get(1)).setDrawable(shown[0] ? Icon.upOpen : Icon.downOpen);
                t.setChecked(shown[0]);
            }).left().padBottom(2f).row();


            teamRules.collapser(c -> {
                c.left().defaults().fillX().left().pad(5);
                in.current = c;
                TeamRule teams = rules.teams.get(NyfUnitTeamMapper.verdentTeam);
                in.check("@nyf.envrule", b ->{
                    if (b) rules.env |= NyfalisAttributeWeather.nyfalian;
                    else rules.env = ~NyfalisAttributeWeather.nyfalian;
                }, () -> rules.hasEnv(NyfalisAttributeWeather.nyfalian));

                c.image().color(Pal.accent).height(3f).padBottom(20).fillX().left().pad(5).row();

                in.table(t -> {

                    for(int i = 0; i < 2; i++){
                        String type = i == 1 ? "@rules.enemyteam" : "@rules.playerteam" ;
                        int finalI = i;
                        Cons<Team> cons;
                        if(finalI == 1) cons = te -> rules.waveTeam = te;
                        else cons = te -> rules.defaultTeam = te;

                        if(!Core.bundle.get(type.substring(1)).toLowerCase().contains(in.ruleSearch)) return;
                        in.current.table(ta -> {
                            ta.left();
                            ta.add(type).left().padRight(5).marginRight(10f);

                            ta.button(Tex.whiteui, Styles.squareTogglei, 38f, () -> {
                                cons.get(NyfUnitTeamMapper.verdentTeam);
                            }).pad(1f).checked(b -> (finalI == 1 ? rules.waveTeam : rules.defaultTeam) == NyfUnitTeamMapper.verdentTeam).size(60f).tooltip(NyfUnitTeamMapper.verdentTeam.coloredName()).with(im -> im.getStyle().imageUpColor = NyfUnitTeamMapper.verdentTeam.color);
                        }).row();
                    }
                }).padTop(0).row();

                in.number("@rules.blockhealthmultiplier", f -> teams.blockHealthMultiplier = f, () -> teams.blockHealthMultiplier);
                in.number("@rules.blockdamagemultiplier", f -> teams.blockDamageMultiplier = f, () -> teams.blockDamageMultiplier);

                in.check("@rules.rtsai", b -> teams.rtsAi = b, () -> teams.rtsAi, () -> NyfUnitTeamMapper.verdentTeam != rules.defaultTeam);
                in.numberi("@rules.rtsminsquadsize", f -> teams.rtsMinSquad = f, () -> teams.rtsMinSquad, () -> teams.rtsAi, 0, 100);
                in.numberi("@rules.rtsmaxsquadsize", f -> teams.rtsMaxSquad = f, () -> teams.rtsMaxSquad, () -> teams.rtsAi, 1, 1000);
                in.number("@rules.rtsminattackweight", f -> teams.rtsMinWeight = f, () -> teams.rtsMinWeight, () -> teams.rtsAi);

                //disallow on Erekir (this is broken for mods I'm sure, but whatever)
                in.check("@rules.buildai", b -> teams.buildAi = b, () -> teams.buildAi, () -> NyfUnitTeamMapper.verdentTeam != rules.defaultTeam && rules.env != Planets.erekir.defaultEnv && !rules.pvp);
                in.number("@rules.buildaitier", false, f -> teams.buildAiTier = f, () -> teams.buildAiTier, () -> teams.buildAi && rules.env != Planets.erekir.defaultEnv && !rules.pvp, 0, 1);

                in.number("@rules.extracorebuildradius", f -> teams.extraCoreBuildRadius = f * tilesize, () -> Math.min(teams.extraCoreBuildRadius / tilesize, 200), () -> !rules.polygonCoreProtection);

                in.check("@rules.infiniteresources", b -> teams.infiniteResources = b, () -> teams.infiniteResources);
                in.check("@rules.fillitems", b -> teams.fillItems = b, () -> teams.fillItems);
                in.number("@rules.buildspeedmultiplier", f -> teams.buildSpeedMultiplier = f, () -> teams.buildSpeedMultiplier, 0.001f, 50f);

                in.number("@rules.unitdamagemultiplier", f -> teams.unitDamageMultiplier = f, () -> teams.unitDamageMultiplier);
                in.number("@rules.unitcrashdamagemultiplier", f -> teams.unitCrashDamageMultiplier = f, () -> teams.unitCrashDamageMultiplier);
                in.number("@rules.unitminespeedmultiplier", f -> teams.unitMineSpeedMultiplier = f, () -> teams.unitMineSpeedMultiplier);
                in.number("@rules.unitbuildspeedmultiplier", f -> teams.unitBuildSpeedMultiplier = f, () -> teams.unitBuildSpeedMultiplier, 0.001f, 50f);
                in.number("@rules.unitcostmultiplier", f -> teams.unitCostMultiplier = f, () -> teams.unitCostMultiplier);
                in.number("@rules.unithealthmultiplier", f -> teams.unitHealthMultiplier = f, () -> teams.unitHealthMultiplier);

                if(!in.current.hasChildren()){
                    teamRules.clear();
                }else{
                    wasCurrent.add(teamRules).row();
                }

            }, () -> shown[0]).left().growX().row();
            in.current = wasCurrent;
        }));
    }
}
