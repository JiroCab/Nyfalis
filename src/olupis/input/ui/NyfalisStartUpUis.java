package olupis.input.ui;

import arc.*;
import arc.audio.*;
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
import mindustry.input.*;
import mindustry.mod.Mod;
import mindustry.mod.Mods;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.ui.fragments.*;
import olupis.*;
import olupis.content.*;
import olupis.input.*;
import olupis.world.*;
import olupis.world.entities.packets.*;

import java.util.*;

import static mindustry.Vars.*;

public class NyfalisStartUpUis {
    public static Table debugTable = new Table();

    public static void  debugDialog(){
        //debuging DONOT in full release
        BaseDialog dialog = new BaseDialog("@nyfalis-disclaimer.name");
        dialog.centerWindow();

        //NyfalisStartUpUis.debugDialog()
        dialog.cont.setOrigin(Align.center);
        dialog.cont.table(t -> {
            t.defaults().center().scaling(Scaling.bounded);

            int[] row = {0, 0};
            for(int i = 0; i < 204; i++){
                int fi = i;

                t.button(Icon.play, () -> {
                    Sounds.getSound(row[1]).stop();
                    Sounds.getSound(fi).play();
                    row[1] = fi;
                }).tooltip(i +  "");
                row[0]++;
                if(row[0] >= 10){
                    row[0] = 0;
                    t.row();
                }
            }

        }).center().top().row();

        dialog.cont.button("@back", Icon.left, dialog::hide).padTop(-1f).size(220f, 55f).bottom();
        dialog.closeOnBack();
        dialog.show();
    }

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
                Mods.LoadedMod olupisMod = mods.list().find(a -> Objects.equals(a.name, "olupis"));
                if(olupisMod != null && olupisMod.iconTexture != null){
                    TextureRegion icon = new TextureRegion(olupisMod.iconTexture);
                    t.table(a -> a.image(icon).scaling(Scaling.bounded).row()).tooltip("Art By RushieWashie").maxSize(700).margin(14).pad(3).center().row();
                }
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
        debugTable.table(Tex.pane, z -> {

            ImageButton button = z.button(Tex.whiteui, Styles.clearNoneTogglei, 35, () -> updateTeam( NyfUnitTeamMapper.verdentTeam))
            .width(77.5f).height(40f).get();
            button.getImageCell().grow().margin(0).width(70);
            button.getStyle().imageUpColor = NyfUnitTeamMapper.verdentTeam.color;
            button.update(() -> button.setChecked(player.team() ==  NyfUnitTeamMapper.verdentTeam));

            ImageButton button2 = z.button(Tex.whiteui, Styles.clearNoneTogglei, 35, () -> updateTeam( NyfUnitTeamMapper.calyxTeam))
            .width(77.5f).height(40f).get();
            button2.getImageCell().grow().margin(0).width(70);
            button2.getStyle().imageUpColor = NyfUnitTeamMapper.calyxTeam.color;
            button2.update(() -> button2.setChecked(player.team() ==  NyfUnitTeamMapper.calyxTeam));
        }).width(155f).margin(12).marginBottom(0).marginTop(0).checked(false).visible( () -> Core.settings.getBool("nyfalis-debug") || state.isEditor());

        //debug and if someone needs to convert a map and said map does not have the Nyfalis Block set / testing
        if( Core.settings.getBool("nyfalis-debug")){
            debugTable.row();
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
                    if(Core.input.keyDown(Binding.boost)){
                        if(state.isCampaign()) Logic.sectorCapture();
                        state.wave += 100;
                        for (Item i : content.items()){
                            if(Core.input.keyDown(Binding.commandMode)) player.team().core().items.add(i, player.team().core().storageCapacity);
                            else if (i.unlocked())player.team().core().items.add(i, player.team().core().storageCapacity);
                        }
                    }

                }).width(77.5f).height(40f).checked(false).tooltip("Capture Sector & fill core with Items");

                z.row();
                z.button("R", Icon.list, Styles.squareTogglet, () -> {
                    ruleInfo.show(Vars.state.rules, () -> Vars.state.rules = new Rules());
                }).width(77.5f).height(40f).checked(false).tooltip("@editor.rules");

                z.button("W", Icon.waves, Styles.squareTogglet, waveInfo::show).width(77.5f).height(40f).checked(false).tooltip("@editor.waves");
                z.row();
                z.button("G", Icon.grid, Styles.squareTogglet, NYF::gphh).width(77.5f).height(40f).checked(false).tooltip("highlight graph of calyx network under player");
                z.button("S", Icon.commandAttack, Styles.squareTogglet, () -> {
                    if(Core.input.keyDown(Binding.boost) && Core.input.keyDown(Binding.control)) EnvUpdater.spearDebugCalc();
                    NYF.sss();
                }).width(77.5f).height(40f).checked(false).tooltip("highlight spear tiles");

            }).width(155f).growY().margin(12f).marginBottom(0).marginTop(0).checked(false).row();
            if(mobile || testMobile){
                debugTable.row();
                debugTable.add(new Element()).width(155f).height(50f).margin(12f).touchable( Touchable.disabled);
            }
        }
        debugTable.marginBottom(200f);
    }

    public static void updateTeam(Team t ){
        if( state.isEditor()){
            Call.setPlayerTeamEditor(player,  t);
        }else if(Core.settings.getBool("nyfalis-debug")){
            player.team(t);
        }

    }


    public static void loadHints(){
        ui.hints.hints.addAll(
            new HintsFragment.Hint() {
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
            }, new HintsFragment.Hint() {
                int prev = -1;
                @Override
                public String name() {return "hint.nyflais-command-append";}

                @Override
                public String text() {return Core.bundle.get("hint.nyflais-command-append.text");}

                public boolean yes(){
                    prev = control.input.selectedUnits.size;
                    return control.input.commandMode && control.input.selectedUnits.size > 0 && control.input.selectedUnits.contains( u -> NyfalisUnitCommands.hasAppend.contains(u.command().command));
                }

                @Override
                public boolean complete() {return  yes() && Core.input.keyDown(Binding.boost) && prev != control.input.selectedUnits.size;}

                @Override
                public boolean show() {
                    return yes();
                }

                @Override
                public int order() {return 8;}

                @Override
                public boolean valid() {return true;}
            }
        );
    }
    public static void nyfAdditionalRules(CustomRulesDialog in){
        NyfRules nyfRule= new NyfRules();
        Seq<Runnable> additionalSetup = Reflect.get(in, "additionalSetup");
        additionalSetup.add(() -> {

            Rules rules = Reflect.get(in, "rules");
            in.category("nyfalis");

            in.check("@rules.nyf-env", b ->{
                if (b) rules.env |= NyfalisAttributeWeather.nyfalian;
                else rules.env = ~NyfalisAttributeWeather.nyfalian;
            }, () -> rules.hasEnv(NyfalisAttributeWeather.nyfalian));

            tagCheck(in, rules, "@rules.nyf-damagingweather", b -> nyfRule.damagingWeather = b, () -> nyfRule.damagingWeather);

            tagCheck(in, rules, "@rules.nyf-calyxspreading", b -> nyfRule.calyxSpreading = b, () -> nyfRule.calyxSpreading);
            tagNumber(in, rules, "@rules.nyf-calyxspreadingfactor", f -> nyfRule.calyxSpreadingFactor = f, () -> nyfRule.calyxSpreadingFactor );
            tagNumber(in, rules, "@rules.nyf-calyxspearfactor", f -> nyfRule.calyxSpearFactor = f, () -> nyfRule.calyxSpearFactor );
            tagNumber(in, rules, "@rules.nyf-calyxgrowthfactor", f -> nyfRule.calyxGrowthFactor = f, () -> nyfRule.calyxGrowthFactor );
            tagNumber(in, rules, "@rules.nyf-calyxsproutfactor", f -> nyfRule.calyxSproutFactor = f, () -> nyfRule.calyxSproutFactor );

            //todo: the other rules

            if(Core.bundle.get("nyf-teams").toLowerCase().contains(in.ruleSearch)){
                Seq<Cons<Team>>cons = Seq.with(te -> rules.waveTeam = te, te -> rules.defaultTeam = te, te -> nyfRule.calyxTeam = te);
                Seq<Prov<Team>> provs = Seq.with(() -> rules.waveTeam, () -> rules.defaultTeam, () -> nyfRule.calyxTeam);
                String[] names = new String[]{"@rules.enemyteam", "@rules.playerteam", "@rules.nyf-calyxteam"};
                for(int ti = 0; ti < 3; ti++){
                    int finalTi = ti;
                    in.current.table( t -> {
                        t.left();
                        t.add(names[finalTi]).left().padRight(5);

                        Team[] teams;
                        if(finalTi == 2){
                            teams = new  Team[NyfUnitTeamMapper.nyfTeams.length -1 + Team.baseTeams.length];
                            for(int j = 0; j < teams.length; j++){
                                if(j < Team.baseTeams.length -1) teams[j] = Team.baseTeams[j +1];
                                else teams[j] = NyfUnitTeamMapper.nyfTeams[j -  Team.baseTeams.length +1];
                            }
                        }else teams = NyfUnitTeamMapper.nyfTeams;


                        t.table(tam -> {
                            for(int j = 0; j < teams.length; j++){
                                Team team = teams[j];
                                tam.button(Tex.whiteui, Styles.squareTogglei, 38f, () -> cons.get(finalTi).get(team)).pad(1f).checked(b -> provs.get(finalTi).get() == team).size(60f).tooltip(team.coloredName()).with(i -> i.getStyle().imageUpColor = team.color);
                                if(finalTi == 2 && j ==( teams.length /2) -1)tam.row();
                            }
                        });

                    }).padTop(0).row();
                }
            }


            Team[] nyfTeams = NyfUnitTeamMapper.nyfTeams;
            for(Team team : nyfTeams){
                Table teamRules = new Table();
                Table wasCurrent = in.current;
                boolean[] shown = {false};
                teamRules.button(team.coloredName(), Icon.downOpen, Styles.togglet, () -> {
                    shown[0] = !shown[0];
                }).marginLeft(14f).width(260f).height(55f).update(t -> {
                    ((Image)t.getChildren().get(1)).setDrawable(shown[0] ? Icon.upOpen : Icon.downOpen);
                    t.setChecked(shown[0]);
                }).left().padBottom(2f).row();

                teamRules.collapser(c -> {
                    c.left().defaults().fillX().left().pad(5);
                    in.current = c;
                    TeamRule teams = rules.teams.get(team);

                    in.number("@rules.blockhealthmultiplier", f -> teams.blockHealthMultiplier = f, () -> teams.blockHealthMultiplier);
                    in.number("@rules.blockdamagemultiplier", f -> teams.blockDamageMultiplier = f, () -> teams.blockDamageMultiplier);

                    in.check("@rules.rtsai", b -> teams.rtsAi = b, () -> teams.rtsAi, () -> team != rules.defaultTeam);
                    in.numberi("@rules.rtsminsquadsize", f -> teams.rtsMinSquad = f, () -> teams.rtsMinSquad, () -> teams.rtsAi, 0, 100);
                    in.numberi("@rules.rtsmaxsquadsize", f -> teams.rtsMaxSquad = f, () -> teams.rtsMaxSquad, () -> teams.rtsAi, 1, 1000);
                    in.number("@rules.rtsminattackweight", f -> teams.rtsMinWeight = f, () -> teams.rtsMinWeight, () -> teams.rtsAi);

                    //disallow on Erekir (this is broken for mods I'm sure, but whatever)
                    in.check("@rules.buildai", b -> teams.buildAi = b, () -> teams.buildAi, () -> team != rules.defaultTeam && rules.env != Planets.erekir.defaultEnv && !rules.pvp);
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
                    in.current = wasCurrent;
                }, () -> shown[0]).left().growX().row();
            }
        });
    }

    public static void tagCheck(CustomRulesDialog dia, Rules r,  String text, Boolc cons, Boolp prov){
        tagCheck(dia, r, text, cons, prov, () -> true);
    }

    public static void tagCheck(CustomRulesDialog dia, Rules r, String text, Boolc cons, Boolp prov,  Boolp condition){
        if(!Core.bundle.get(text.substring(1)).toLowerCase().contains(dia.ruleSearch)) return;

        String tag = text.replace("@rules.", "");

        Boolp provf;
        if(r.tags.containsKey(tag)) provf = () -> r.tags.getBool(tag);
        else provf = prov;

        Boolc conf =  b-> {
            cons.get(b);
            r.tags.put(tag, String.valueOf(b));
            NyfalisVars.nyfRule.load(r.tags);
        };

        dia.check(text, conf, provf, condition);
    }

    public static void tagNumber (CustomRulesDialog dia, Rules r, String text, Floatc cons, Floatp prov){
        tagNumber(dia, r, text, false, cons, prov, () -> true, 0, Float.MAX_VALUE);
    }

    public static void tagNumber (CustomRulesDialog dia, Rules r, String text, Floatc cons, Floatp prov, Boolp condition, float min, float max){
        tagNumber(dia, r, text, false, cons, prov, condition, min, max);
    }

    public static void tagNumber (CustomRulesDialog dia, Rules r, String text, boolean integer, Floatc cons, Floatp prov){
        tagNumber(dia, r, text, integer, cons, prov, () -> true, 0, Float.MAX_VALUE);
    }

    public static void tagNumber (CustomRulesDialog dia, Rules r, String text, boolean integer, Floatc cons, Floatp prov, Boolp condition, float min, float max){

        if(!Core.bundle.get(text.substring(1)).toLowerCase().contains(dia.ruleSearch)) return;
        String tag = text.replace("@rules.", "");

        Floatp provf;
        if(r.tags.containsKey(tag)){
            if(integer) provf = () -> r.tags.getInt(tag);
            else provf = () -> r.tags.getFloat(tag);
        }else provf = prov;

        Floatc conf =  b-> {
            cons.get(b);
            r.tags.put(tag, String.valueOf(b));
            NyfalisVars.nyfRule.load(r.tags);
        };

        dia.number(text, integer, conf, provf, condition, min, max);
    }
}
