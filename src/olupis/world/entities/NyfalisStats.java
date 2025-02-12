package olupis.world.entities;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.weapons.*;
import mindustry.ui.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;
import olupis.world.entities.bullets.*;
import olupis.world.entities.units.*;
import olupis.world.entities.weapons.*;

import java.util.*;

import static mindustry.Vars.*;

public class NyfalisStats extends StatValues {

    public static <T extends UnlockableContent> StatValue ammoWithInfo(ObjectMap<T, BulletType> map, UnlockableContent parent){
        return ammoWithInfo(map, 0, false, parent != null ? parent.name : null, null);
    }

    public static <T extends UnlockableContent> StatValue ammoWithInfoSortable(ObjectMap<T, BulletType> map, UnlockableContent parent, Floatf<BulletType> comparator){
        return ammoWithInfo(map, 0, false, parent != null ? parent.name : null, comparator);
    }

    public static <T extends UnlockableContent> StatValue ammoBlocksOnly(ObjectMap<T, BulletType> map, UnlockableContent parent){
        ObjectMap<T, BulletType> children = new OrderedMap<>();
        for (ObjectMap.Entry<T, BulletType> b : map) {
            children.put(b.key, checkChildren(b.value));
        }

        return ammoWithInfo(children, parent);
    }

    public static BulletType checkChildren(BulletType type){
        if(type instanceof MineBulletType){
            return type;
        }
        if(type.fragBullet != null) return checkChildren(type.fragBullet);
        return type;
    }

    public static <T extends UnlockableContent> StatValue ammoWithInfo(ObjectMap<T, BulletType> map, int indent, boolean showUnit, String parent, Floatf<BulletType> comparator){
        return table -> {

            table.row();

            var orderedKeys = map.keys().toSeq();
            if(comparator != null) orderedKeys.sort( t -> comparator.get(map.get(t)));
            else  orderedKeys.sort( t -> map.get(t).damage);

            for(T t : orderedKeys) {
                boolean compact = t instanceof UnitType && !showUnit || indent > 0;

                BulletType type = map.get(t);

                if(type instanceof  MineBulletType){
                    table.table(Styles.grayPanel, in -> {
                        in.left().top().defaults().padRight(3).left();

                        MineBulletType mb = (MineBulletType) type;
                        if (mb.mine != null) {
                            in.table(info -> {
                                if(mb.mine.canBeBuilt() && mb.mine.fullIcon != null) info.image(mb.mine.fullIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                                else info.image(Icon.cancel.getRegion()).color(Pal.remove).size(40).pad(10f).left().scaling(Scaling.fit);

                                info.table(bt -> {
                                    bt.left().top().defaults().growX().left();
                                    if(!(t instanceof  UnitType))title(bt, (t.localizedName), t.fullIcon);
                                    sepLeft(bt, (mb.mine.localizedName));
                                    sepLeftWrap(bt, (mb.mine.description));
                                    if(Core.settings.getBool("console"))sepLeft(bt, ("[lightgray]"+ mb.mine.name));
                                    if (mb.createChance) {
                                        float set;
                                        if (mb.createChancePercent > 0.99) {
                                            set = 99;
                                        } else if (mb.createChancePercent < 0.01) {
                                            set = 1;
                                        } else {
                                            set = (mb.createChancePercent * 100);
                                        }
                                        sep(bt, Core.bundle.format("stat.olupis-chancepercent", Strings.autoFixed(set, 2)));
                                    }
                                    if (mb.mine instanceof ShockMine sm) {
                                        float mdmg = (sm.damage * sm.tendrils) + sm.tileDamage;
                                        if (mdmg != 0) {
                                            sep(bt, Core.bundle.format("bullet.damage", (sm.damage * sm.tendrils) + sm.tileDamage));
                                        }
                                        if (sm.bullet != null) {
                                            bt.row();

                                            Table ic = new Table();
                                            ammoWithInfo(ObjectMap.of(t, sm.bullet), indent + 1, false, null).display(ic);
                                            Collapser coll = new Collapser(ic, true);
                                            coll.setDuration(0.1f);

                                            bt.table(it -> {
                                                it.left().defaults().left();

                                                it.add(Core.bundle.format("stat.olupis-bullet", Strings.autoFixed(sm.shots, 2)));
                                                it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                                            });
                                            bt.row();
                                            bt.add(coll);
                                        }
                                    }
                                }).left().growX();
                            }).growX().left();
                            in.button("?", Styles.flatBordert, () -> ui.content.show(mb.mine)).size(40f).right().visible(mb.mine::unlockedNow).row();
                        }
                    }).padLeft(indent * 5).padTop(5).padBottom(compact ? 0 : 5).margin(compact ? 0 : 10);
                    table.row();
                }
                else if (type instanceof SpawnHelperBulletType || type.spawnUnit != null ) { //TODO Icon broken
                    UnitType spawn = type.spawnUnit;
                    table.table(Styles.grayPanel, in -> {
                        in.left().top().defaults().padRight(3).left();

                        in.table(bt -> {
                            boolean show = !spawn.isBanned();
                            if(show)in.image(spawn.fullIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                            else in.image(Icon.cancel.getRegion()).color(Pal.remove).size(40).pad(10f).left().scaling(Scaling.fit);
                            bt.row();

                            bt.table(info -> {
                                if (!spawn.unlocked() && (Vars.state.isCampaign() || !Vars.state.isPlaying())) info.image(Icon.lock.getRegion()).tooltip(spawn.localizedName).size(25).pad(10f).left().scaling(Scaling.fit);
                                else {
                                    info.add(spawn.localizedName).left().row();
                                    if (Core.settings.getBool("console"))
                                        info.add("[lightgray]" + spawn.name).left();
                                }

                                if (type.intervalBullet != null) {
                                    info.row();
                                    int subIndent = indent + 1;
                                    info.table(ib ->{
                                        Table ic = new Table();
                                        ammoWithInfo(ObjectMap.of(t, type.intervalBullet), subIndent + 1, false, null).display(ic);
                                        Collapser coll = new Collapser(ic, true);
                                        coll.setDuration(0.1f);

                                        ib.table(it -> {
                                            it.left().defaults().left();

                                            it.add(Core.bundle.format("bullet.interval", Strings.autoFixed(type.intervalBullets / type.bulletInterval * 60, 2)));
                                            it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                                        });
                                        ib.row();
                                        ib.add(coll);
                                    }).growX().left();
                                }
                            }).growX().left();
                        });
                        in.button("?", Styles.flatBordert, () -> ui.content.show(spawn)).size(40f).pad(10).padLeft(4).right().grow().visible(spawn::unlockedNow);
                    }).padLeft(indent * 5).padTop(5).padBottom(compact ? 0 : 5).growX().margin(compact ? 0 : 10);
                }
                else {
                    table.table(Styles.grayPanel, bt -> {
                        bt.left().top().defaults().padRight(3).left();
                        //no point in displaying unit icon twice

                        if (!compact && !(t instanceof Turret)) {
                            bt.table(title -> {
                                title.image(icon(t)).size(3 * 8).padRight(4).right().scaling(Scaling.fit).top();
                                title.labelWrap(t.localizedName).padRight(10).left().top();
                            });
                            bt.row();
                        }

                        if (parent != null && Core.bundle.has(parent + "." + t.name)) {
                            bt.table(info -> {
                                info.add(Core.bundle.get(parent + "." + t.name)).padRight(10).left().top();
                            }).row();
                        }

                        if (type.damage > 0 && (type.collides || type.splashDamage <= 0)) {
                            if (type.continuousDamage() > 0) {
                                bt.add(Core.bundle.format("bullet.damage", type.continuousDamage()) + StatUnit.perSecond.localized());
                            }else if( type instanceof  DistanceScalingBulletType st){
                                bt.add(Core.bundle.format("bullet.damage", (type.damage * st.minDmgMul) + "-" + type.damage * st.maxDmgMul ));
                            }else {
                                bt.add(Core.bundle.format("bullet.damage", type.damage));
                            }
                        }

                        if (type.buildingDamageMultiplier != 1) {
                            int val = (int) (type.buildingDamageMultiplier * 100 - 100);
                            sep(bt, Core.bundle.format("bullet.buildingdamage", ammoStat(val)));
                        }

                        if (type.rangeChange != 0 && !compact) {
                            sep(bt, Core.bundle.format("bullet.range", ammoStat(type.rangeChange / tilesize)));
                        }

                        if (type.splashDamage > 0) {
                            if( type instanceof  DistanceScalingBulletType st){
                                sep(bt, Core.bundle.format("bullet.splashdamage",  ((type.splashDamage * st.minDmgMul) + "-" + type.splashDamage * st.maxDmgMul ), Strings.fixed(type.splashDamageRadius / tilesize, 1)));
                            }else sep(bt, Core.bundle.format("bullet.splashdamage", (int) type.splashDamage, Strings.fixed(type.splashDamageRadius / tilesize, 1)));
                        }

                        if(type.fragBullet instanceof DistanceScalingBulletType st){
                            sep(bt, Core.bundle.format("bullet.splashdamage",  ((st.splashDamage * st.minDmgMul) + "-" + st.splashDamage * st.maxDmgMul ), Strings.fixed(st.splashDamageRadius / tilesize, 1)));
                        }

                        if (type.splashDamage > 0 && type instanceof EffectivenessMissleType m && m.groundDamageSplashMultiplier != 1f) {
                            float val = (m.flatDamage ? (m.groundDamageMultiplier / m.damage) * 100 -100 : m.groundDamageMultiplier * 100 - 100);
                            sep(bt, Core.bundle.format("stat.olupis-splashpenalty", ammoStat(val), m.flatDamage ? m.groundDamageMultiplier : m.damage * m.groundDamageMultiplier, 2));
                        }


                        if (!compact && !Mathf.equal(type.ammoMultiplier, 1f) && type.displayAmmoMultiplier && (!(t instanceof Turret turret) || turret.displayAmmoMultiplier)) {
                            sep(bt, Core.bundle.format("bullet.multiplier", (int) type.ammoMultiplier));
                        }

                        if (!compact && !Mathf.equal(type.reloadMultiplier, 1f)) {
                            int val = (int) (type.reloadMultiplier * 100 - 100);
                            sep(bt, Core.bundle.format("bullet.reload", ammoStat(val)));
                        }

                        if (type.knockback > 0) {
                            sep(bt, Core.bundle.format("bullet.knockback", Strings.autoFixed(type.knockback, 2)));
                        }

                        if (type.healPercent > 0f) {
                            sep(bt, Core.bundle.format("bullet.healpercent", Strings.autoFixed(type.healPercent, 2)));
                        }

                        if (type.healAmount > 0f) {
                            sep(bt, Core.bundle.format("bullet.healamount", Strings.autoFixed(type.healAmount, 2)));
                        }

                        if (type.pierce || type.pierceCap != -1) {
                            sep(bt, type.pierceCap == -1 ? "@bullet.infinitepierce" : Core.bundle.format("bullet.pierce", type.pierceCap));
                        }

                        if (type.incendAmount > 0) {
                            sep(bt, "@bullet.incendiary");
                        }

                        if (type.homingPower > 0.01f) {
                            if(type instanceof  BarrelBulletType) sep(bt, "@stat.olupis-bouncy");
                            else if(type instanceof  RollBulletType r  && r.ricochetHoming) sep(bt, "@stat.olupis-ricochet");
                            else sep(bt, "@bullet.homing");
                        }

                        if (type.lightning > 0) {
                            sep(bt, Core.bundle.format("bullet.lightning", type.lightning, type.lightningDamage < 0 ? type.damage : type.lightningDamage));
                        }

                        if (type.pierceArmor) {
                            sep(bt, "@bullet.armorpierce");
                        }

                        if (type.suppressionRange > 0) {
                            sep(bt, Core.bundle.format("bullet.suppression", Strings.autoFixed(type.suppressionDuration / 60f, 2), Strings.fixed(type.suppressionRange / tilesize, 1)));
                        }

                        if (type.status != StatusEffects.none) {
                            sep(bt, (type.status.minfo.mod == null ? type.status.emoji() : "") + "[stat]" + type.status.localizedName + (type.status.reactive ? "" : "[lightgray] ~ [stat]" + (Strings.autoFixed(type.statusDuration / 60f, 1)) + "[lightgray] " + Core.bundle.get("unit.seconds")));
                        }

                        if (type instanceof EffectivenessMissleType m && m.groundDamageMultiplier != 1f) {
                            float val = (m.flatDamage ? (m.groundDamageMultiplier / m.damage) * 100 -100 : m.groundDamageMultiplier * 100 - 100);
                            sep(bt, Core.bundle.format("stat.olupis-groundpenalty", ammoStat(val), m.flatDamage ? m.groundDamageMultiplier : m.damage * m.groundDamageMultiplier, 2));
                        }

                        if (t instanceof  NyfalisUnitType nu && nu.weaponsStartEmpty){
                            sep(bt,"@stat.olupis-unloaded");
                        }

                        if (type.intervalBullet != null) {
                            bt.row();

                            Table ic = new Table();
                            ammoWithInfo(ObjectMap.of(t, type.intervalBullet), indent + 1, false, null).display(ic);
                            Collapser coll = new Collapser(ic, true);
                            coll.setDuration(0.1f);

                            bt.table(it -> {
                                it.left().defaults().left();

                                it.add(Core.bundle.format("bullet.interval", Strings.autoFixed(type.intervalBullets / type.bulletInterval * 60, 2)));
                                it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                            });
                            bt.row();
                            bt.add(coll);
                        }

                        if (type.fragBullet != null && !(type.fragBullet instanceof DistanceScalingBulletType)) {
                            bt.row();

                            Table fc = new Table();
                            ammoWithInfo(ObjectMap.of(t, type.fragBullet), indent + 1, false, null).display(fc);
                            Collapser coll = new Collapser(fc, true);
                            coll.setDuration(0.1f);

                            bt.table(ft -> {
                                ft.left().defaults().left();

                                ft.add(Core.bundle.format("bullet.frags", type.fragBullets));
                                ft.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
                            });
                            bt.row();
                            bt.add(coll);
                        }
                    }).padLeft(indent * 5).padTop(5).padBottom(compact ? 0 : 5).growX().margin(compact ? 0 : 10);
                    table.row();
                }
            }
        };
    }

    public static <T extends UnlockableContent> StatValue ammoWithInfo(ObjectMap<T, BulletType> map, int indent, boolean showUnit, String parent){
        return ammoWithInfo(map, indent, showUnit, parent, null);
    }

    public static StatValue weapons(UnitType unit, Seq<Weapon> weapons) {
        return (table) -> {
            table.row();

            for(int i = 0; i < weapons.size; ++i) {
                Weapon weapon = weapons.get(i);
                if (!weapon.flipSprite && weapon.hasStats(unit)) { //haha supella work around
                    TextureRegion preRegion = null;
                    if(!weapon.name.isEmpty()) preRegion = Core.atlas.find(weapon.name + "-preview", weapon.region);
                    else if(weapon instanceof NyfalisWeapon nyft && !Objects.equals(nyft.weaponIconString, "")) {
                        if(nyft.weaponIconUseFullString) preRegion = Core.atlas.find(nyft.weaponIconString);
                        else preRegion = Core.atlas.find(weapon.name + nyft.weaponIconString);
                    }
                    else if(!weapon.parts.isEmpty() && weapon.parts.first() instanceof RegionPart rp) preRegion = rp.regions[0];
                    TextureRegion region = preRegion;


                    table.table(Styles.grayPanel, (w) -> {
                        w.left().top().defaults().padRight(3.0F).left();
                        if (region != null && region.found() && weapon.showStatSprite) w.image(region).size(60.0F).scaling(Scaling.bounded).left().top();
                        if(weapon instanceof PointDefenseWeapon) sep(w, "@stat.olupis-pointdefence");

                        w.row();
                        ammoWithInfo(ObjectMap.of(unit, weapon.bullet), unit).display(w);
                    }).growX().pad(5.0F).margin(10.0F);
                    table.row();
                }
            }

        };
    }

    public static float unitReloadTime(float reloadmul){ //idk
        float mul  = Math.abs(1 - reloadmul);
        return Math.abs((mul > 0  ? -1  : mul < 0  ? 1 : 0) + mul);
    }

    //for AmmoListValue
    private static void sep(Table table, String text){
        table.row();
        table.add(text);
    }

    private static void sepLeft(Table table, String text){
        table.table(te -> {
            te.add(text).growX().left();
        }).row();
    }

    private static void sepLeftWrap(Table table, String text){
        table.table(te -> {
            te.labelWrap(text).growX().left();
        }).growX().row();
    }

    private static void title(Table table, String text, TextureRegion icon){
        table.table(te -> {
            te.image(icon).size(3 * 8).left().scaling(Scaling.fit).top();
            te.add(text).left().top();
        }).left().row();
    }

    private static void title(Table table, String text, TextureRegion icon, Color colour){
        table.table(te -> {
            te.image(icon).size(3 * 8).left().scaling(Scaling.fit).color(colour).top();
            te.add(text).left().top();
        }).left().row();
    }

    //for AmmoListValue
    private static String ammoStat(float val){
        return (val > 0 ? "[stat]+" : "[negstat]") + Strings.autoFixed(val, 1);
    }

    public static StatValue sawBoosters(float reload, float maxUsed, float multiplier, boolean baseReload, Boolf<Liquid> filter) {
        return (table) -> {
            table.row();
            table.table((c) -> {
                Iterator var6 = Vars.content.liquids().iterator();

                while(var6.hasNext()) {
                    Liquid liquid = (Liquid)var6.next();
                    if (filter.get(liquid)) {
                        c.table(Styles.grayPanel, (b) -> {
                            b.image(liquid.uiIcon).size(40.0F).pad(10.0F).left().scaling(Scaling.fit);
                            b.table((info) -> {
                                info.add(liquid.localizedName).left().row();
                                info.add(Strings.autoFixed(maxUsed * 60.0F, 2) + StatUnit.perSecond.localized()).left().color(Color.lightGray);
                            });
                            b.table((bt) -> {
                                bt.right().defaults().padRight(3.0F).left();
                                float reloadRate = (baseReload ? 1.0F : 0.0F) + maxUsed * multiplier * liquid.heatCapacity;
                                float standardReload = baseReload ? reload : reload / (maxUsed * multiplier * 0.4F);
                                float result = standardReload / (reload / reloadRate);
                                bt.add(Core.bundle.format("stat.olupis-spin-speed-bonus", new Object[]{Strings.autoFixed(result * 100.0F, 2)})).pad(5.0F);
                            }).right().grow().pad(10.0F).padRight(15.0F);
                        }).growX().pad(5.0F).row();
                    }
                }

            }).growX().colspan(table.getColumns());
            table.row();
        };
    }

    public static StatValue lubeBoosters(float reload, float maxUsed, float multiplier, float baseHeat, Boolf<Liquid> filter){
        return table -> {
            table.row();
            table.table(c -> {
                for(Liquid liquid : content.liquids()){
                    if(!filter.get(liquid)) continue;

                    c.table(Styles.grayPanel, b -> {
                        b.image(liquid.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                        b.table(info -> {
                            info.add(liquid.localizedName).left().row();
                            info.add(Strings.autoFixed(maxUsed * 60f, 2) + StatUnit.perSecond.localized()).left().color(Color.lightGray);
                        });

                        b.table(bt -> {
                            bt.right().defaults().padRight(3).left();
                                //TODO
                                float out =  (multiplier * Math.abs(liquid.heatCapacity - baseHeat) * reload) * 60f;
                                bt.add(Core.bundle.formatString(
                                        "{0}", out
                                )).pad(5);
                        }).right().grow().pad(10f).padRight(15f);
                    }).growX().pad(5).row();
                }
            }).growX().colspan(table.getColumns());
            table.row();
        };
    }

    private static TextureRegion icon(UnlockableContent t){
        return t.uiIcon;
    }


}
