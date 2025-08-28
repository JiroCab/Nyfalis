package olupis.input.ui;

import arc.*;
import arc.graphics.*;
import arc.input.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.blocks.logic.*;
import olupis.*;
import olupis.content.*;
import olupis.world.entities.packets.*;

import static arc.Core.settings;
import static mindustry.Vars.*;

public class NyfalisSettingsDialog {
    public static String nyfalisDiscordInvite = "https://discord.gg/K5YX2ECjv7";
    public static String nyfalisMusicLink = "https://github.com/JiroCab/Nyfalis-Music";
    public NyfalisSettingsDialog() {
        if(!headless) BuildDialog();
    }
    public static boolean musicModPresent = false;

    public void BuildDialog(){
        musicModPresent =  mods.locateMod("nyfalis-music") != null;

        ui.settings.addCategory("@category.nyfalis.name", Icon.effect, table -> {
            /*Note: please order this from least to most invasive changes, thanks! but sliders are 1st*/
            table.pref(new CollapserSetting("div-audio", 6));

            if(musicModPresent)table.pref(new CollapserSetting("disclaimer-button", 4));
            else{
                table.sliderPref("nyfalis-beep-volume",-1, -1, 100, 1, i -> i == -1 ? "@nyfalis-beep-volume.def" : i + "%");
                table.checkPref("nyfalis-space-sfx", false);
            }
            table.checkPref("nyfalis-rainbow-music", false);

            table.pref(new CollapserSetting("div-visuals", 6));
            table.checkPref("nyfalis-green-icon", true);
            table.checkPref("nyfalis-green-name", true);
            table.checkPref("nyfalis-cloud-shadows", true);
            table.checkPref("nyfalis-qolTreeTrans", true);
            table.sliderPref("nyfalis-pdl-status-trans",25, 0, 100, 1, i -> i == 0 ? "@off" : i + "%");
            table.sliderPref("nyfalis-pdl-status-range",5, 0, 21, 1, i -> i == 0 ? "@off" : i == 21 ? "@yes" : i + "%");
            table.checkPref("nyfalis-pdl-status-anyteam", false);

            table.pref(new CollapserSetting("div-gameplay", 6));
            table.checkPref("nyfalis-sandbox-super-weapon-cap", false);
            //table.checkPref("nyfalis-bread-gun", false); TODO: full removal
            table.checkPref("nyfalis-auto-ban", true);
            table.checkPref("nyfalis-display-bat-helper", false, val -> {
                for(UnitType b : NyfalisUnits.batHelpers){
                    b.hidden = !val;
                }
            });

            table.pref(new CollapserSetting("div-other", 6));
            table.checkPref("nyfalis-disclaimer", true);
            table.pref(new CollapserSetting("nyfalis-debug-button") );


            table.pref(new CollapserSetting("disclaimer-button", 2));
            table.pref(new CollapserSetting("discord-and-music-mod-button", 1) );
            table.pref(new CollapserSetting("div-data", 6));
            table.pref(new CollapserSetting("data-buttons", 3));
        });
    }

    public static void AddNyfalisSoundSettings(){
        Vars.ui.settings.sound.pref(new CollapserSetting("nyfalis-sounds-in-sounds-button", 5));
    }

    public static void nyfalisExternalLinkDialog(String link, int type){
        Dialog dialog = new Dialog("");

        float h = 70f;
        dialog.cont.margin(12f);
        Color color = Color.valueOf("3ED09A");
        if(type == 1) color = Color.valueOf("4A8059");

        Color finalColor = color;
        dialog.cont.table(t -> {
            t.background(Tex.button).margin(0);

            t.table(img -> {
                img.image().height(h - 5).width(40f).color(finalColor);
                img.row();
                img.image().height(5).width(40f).color(finalColor.cpy().mul(0.8f, 0.8f, 0.8f, 1f));
            }).expandY();

            t.table(i -> {
                TextureRegionDrawable icon = Icon.discord;
                if(type == 1 )icon = Icon.star;

                i.image(icon);
            }).size(h).left();

            String longType  =  "nyfalis-discord-long";
            if(type == 1) longType  = "nyfalis-music-mod-long";
            t.add(Core.bundle.format(longType, link.replace("https://", "" ))).color(Pal.accent).growX().padLeft(10f);

        }).size(520f, h).pad(10f);
        if(type == 1){
            dialog.cont.row();
            dialog.cont.button("@mod.import", Icon.download, () -> {
                if(state.isGame()){
                    ui.showCustomConfirm("@warning", "@nyfalis-music.live", "@mod.import", "@ok", () -> importMusicMod(dialog), dialog::hide);
                }else importMusicMod(dialog);

            }).size(170f, 50).row();
        }
        dialog.buttons.defaults().size(170f, 50);

        dialog.buttons.button("@back", Icon.left, dialog::hide);
        dialog.buttons.button("@copylink", Icon.copy, () -> {
            Core.app.setClipboardText(link);
            ui.showInfoFade("@copied");
        });
        dialog.buttons.button("@openlink", Icon.export, () -> {
            if(!Core.app.openURI(link)){
                ui.showErrorMessage("@linkfail");
                Core.app.setClipboardText(link);
            }
        });
        dialog.closeOnBack();
        dialog.show();
    }


    //Foo's workaround
    public static class CollapserSetting extends SettingsMenuDialog.SettingsTable.Setting{
        int type = 0;
        public CollapserSetting (String name){
            super(name);
        }
        public CollapserSetting (String name, int type){
            super(name);
            this.type = type;
        }

        public void add(SettingsMenuDialog.SettingsTable table) {
            if(type == 1) addDiscordButton(table);
            else if(type == 2) addDisclaimerButton(table);
            else if(type == 3)addDataButtons(table);
            else if(type == 4)addSoundButton(table, false);
            else if(type == 5)addSoundButton(table, true);
            else if(type == 6){
                table.table(t -> {
                    t.defaults().center();
                    Label title = new Label(() -> name.replace("div-", "@nyfalis-")) ;
                    title.setAlignment(Align.center);
                    t.add(title).growX().center().color(Pal.stat).align(Align.center).row();
                    t.image().color(Color.lightGray).height(3f).center().color(Pal.stat).growX().row();
                }).padTop(10).padBottom(10f).growX().row();

            }else { //Haha, was the effort Worth it? probably not -Rushie
                settings.defaults("nyfalis-debug", false);
                CheckBox box = new CheckBox("@nyfalis-debug");

                box.update(() -> box.setChecked(settings.getBool("nyfalis-debug")));

                box.changed(() -> {
                    if(Core.settings.getBool("nyfalis-debug"))settings.put("nyfalis-debug", false);
                    else ui.showConfirm("@confirm", "@nyfalis-debug.confirm",() -> settings.put("nyfalis-debug", true));
                });

                box.left();
                ui.addDescTooltip(table.add(box).left().padTop(3f).get(), "@nyfalis-debug.description");
                table.row();

            }

        }

        public void addDiscordButton(SettingsMenuDialog.SettingsTable table) {
            table.table(tab -> {
                tab.button("@nyfalis-discord", Icon.discord, ()->nyfalisExternalLinkDialog(nyfalisDiscordInvite, 0)).margin(14).width(260f).pad(6);
                if(!musicModPresent)tab.button("@nyfalis-music", Icon.star, ()->nyfalisExternalLinkDialog(nyfalisMusicLink, 1)).margin(14).width(260f).pad(6).row();
            }).growX().row();
        }

        public void addDataButtons(SettingsMenuDialog.SettingsTable table) {
            boolean[] showData = {false, false};
            table.button("@setting.nyfalis-data-category", Icon.trash, Styles.togglet, () ->{
                showData[0] = !showData[0];
                if(Core.input.keyDown(Binding.boost) && Core.input.keyDown(KeyCode.altLeft)) showData[1] = !showData[1];
            }).margin(14f).padLeft(5f).padRight(5f).growX().height(60f).checked(a -> showData[0]).pad(5f).center().row();
            table.collapser(t -> {
                Table subTable = new Table();
                subTable.button("@setting.nyfalis-resetsector.name", Icon.trash, () -> {
                    ui.showConfirm("@confirm", "@setting.nyfalis-resetsector.confirm", () -> {
                        for (Planet p : content.planets()) {
                            if (!p.name.contains("olupis-")) continue;
                            for (Sector s : p.sectors) {
                                s.clearInfo();
                                if (s.save == null) continue;
                                s.save.delete();
                                s.save = null;
                            }
                        }

                        for (Saves.SaveSlot s : control.saves.getSaveSlots())
                            if (s.isSector() && NyfalisPlanets.isNyfalianPlanet(s.getSector().planet)) s.delete();
                    });
                }).margin(14).width(260f).pad(6);
                subTable.button("@setting.nyfalis-resetresearch.name", Icon.trash, () -> {
                    ui.showConfirm("@confirm", "@setting.nyfalis-resetresearch.confirm", () -> {
                        content.each(c -> {
                            if(c instanceof UnlockableContent u && u.name.contains("olupis-")) u.clearUnlock();
                        });

                        for(Planet planet : NyfalisPlanets.planetList ){
                            planet.techTree.reset();
                            planet.techTree.each(TechTree.TechNode::reset);
                        }
                    });
                }).margin(14).width(260f).pad(6).row();
                subTable.button("@setting.nyfalis-resetprogress.name", Icon.trash, () -> {
                    ui.showConfirm("@confirm", "@setting.nyfalis-resetprogress.confirm", () -> {
                        content.each(c -> {
                            if(c instanceof UnlockableContent u && u.name.contains("olupis-")) u.clearUnlock();
                        });
                        for(Planet planet : NyfalisPlanets.planetList ){
                            planet.techTree.reset();
                            planet.techTree.each(TechTree.TechNode::reset);
                        }

                        for (Planet p : content.planets()) {
                            if (!p.name.contains("olupis-")) continue;
                            for (Sector s : p.sectors) {
                                s.clearInfo();
                                if (s.save == null) continue;
                                s.save.delete();
                                s.save = null;
                            }
                        }

                        for (Saves.SaveSlot s : control.saves.getSaveSlots()) {
                            if (s.isSector() && NyfalisPlanets.isNyfalianPlanet(s.getSector().planet)) s.delete();
                        }
                    });
                }).margin(14).width(260f).pad(6);
                subTable.button("@setting.nyfalis-repair.name", Icon.pencil, () -> {
                    if(state.isGame()) {
                        ui.showConfirm("@confirm", "@setting.nyfalis-repair.confirm", () -> {
                            if (player.admin && net.active() && net.client()) {
                                NyfalisDebugPackets packet = new NyfalisDebugPackets();
                                packet.type = 1;
                                Vars.net.send(packet, true);
                            } else {
                                Log.err("fix?");
                                NyfalisMain.sandBoxCheck(false);
                                for (Building b : Groups.build) {
                                    if(b.block.minfo.mod == null) continue;
                                    if(b.block instanceof SwitchBlock) continue; //dont mess with them lol
                                    if (!b.enabled && b.lastDisabler == null && b.block.supportsEnv(state.rules.env) && b.block.minfo.mod.main instanceof NyfalisMain) {
                                        b.enabled = true;
                                    }
                                }
                            }
                        });
                    } else  ui.showInfo("@setting.nyfalis-repair.error");
                }).margin(14).width(260f).pad(6);
                t.add(subTable);
            }, true, () -> showData[0]).growX().center().row();

            table.collapser(t -> {
                Table subTable = new Table();
                subTable.button("sandbox check", () -> {
                    if(player.admin && net.active() && net.client()){
                        NyfalisDebugPackets packet = new NyfalisDebugPackets();
                        Vars.net.send(packet, true);
                    }else NyfalisMain.sandBoxCheck(false);
                }).margin(14).width(260f).pad(6);
                subTable.button("sector turn", NyfalisMain::sectorPostTurn).margin(14).width(260f).pad(6);
                subTable.row();
                subTable.button("test save disclaimer", () -> {
                    if(Core.input.keyDown(KeyCode.altLeft))Core.settings.put("nyf-lastver", 0f);
                    else Core.settings.put("nyf-lastver", 1f);
                    Sounds.respawn.play();
                    float lVer = Float.parseFloat(Core.settings.get("nyf-lastver", 0).toString());
                    Log.info("Nyf last save set: @", lVer);
                }).margin(14).width(260f).pad(6);
                subTable.button("Save Disclaimer", NyfalisStartUpUis::showSaveDisclaimerDialog).margin(14).width(260f).pad(6);
                subTable.row();
                subTable.row();

                t.add(subTable);
            }, true, () -> showData[1]).growX().center().row();
            if(Core.settings.getBool("nyfalis-debug")){
                table.row();
                table.button("extra", () -> showData[1] = !showData[1]).margin(14).width(260f).pad(6);
                table.row();
            }
        }

        public void addDisclaimerButton(SettingsMenuDialog.SettingsTable table){
            table.button("@nyfalis-disclaimer.name", Icon.chat, NyfalisStartUpUis::disclaimerDialog).margin(14).width(260f).pad(6).row();
        }

        public void addSoundButton(SettingsMenuDialog.SettingsTable table, boolean hide){
            if(!Core.settings.getBool("nyfalis-hide-sound") && hide)return;

            boolean[] shown = {false};
            table.button("@setting.nyfalis-sound-category", Icon.effect, Styles.togglet, () -> shown[0] = !shown[0]).margin(14f).growX().height(60f).checked(a -> shown[0]).pad(5f).center().row();

            table.collapser(t -> {
                SettingsMenuDialog.SettingsTable subTable = new SettingsMenuDialog.SettingsTable();
                if (!hide){
                    subTable.checkPref("nyfalis-hide-sound",true);
                } else  t.label(() -> "@settings.nyfalis-music-hint").top().center().padBottom(5f).margin(3).row();

                subTable.sliderPref("nyfalis-beep-volume",-1, -1, 100, 1, i -> i == -1 ? "@nyfalis-beep-volume.def" : i + "%");
                subTable.checkPref("nyfalis-space-sfx",false);
                subTable.checkPref("nyfalis-music",true);
                subTable.checkPref("nyfalis-music-only",false);
                subTable.checkPref("nyfalis-music-add",true, c -> Core.settings.put("nyfalis-replacemusic", false));
                subTable.checkPref("nyfalis-music-custom-game",true);
                t.add(subTable);
            }, () ->shown[0]).growX().center().row();
        }
    }


    public static void importMusicMod(Dialog dialog){
        ui.settings.hide();
        String mod =  nyfalisMusicLink.substring("https://github.com/".length());
        dialog.hide();
        Core.settings.put("lastmod", mod);
        ui.mods.show();//haha so we don't need to manually restart and im too lazy to find out how
        ui.mods.githubImportMod(mod, true, null);
    }

}
