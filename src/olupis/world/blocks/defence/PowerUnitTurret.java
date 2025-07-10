package olupis.world.blocks.defence;

import arc.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import olupis.content.*;
import olupis.world.entities.*;
import olupis.world.entities.bullets.*;

import java.util.*;

import static mindustry.Vars.ui;

/*Im sorry to for whatever curse this class has and may you spared from it*/
public class PowerUnitTurret extends ItemUnitTurret {
    /*Weapon to use when there's no modifier item*/
    public Item internalItem = NyfalisItemsLiquid.powerAmmoItem;

    public PowerUnitTurret(String name){
        super(name);
        hasPower = consumesPower = true;
        setDynamicConsumer = false;

        consume(new ConsumeItemDynamic((PowerUnitTurretBuild e) -> e.regularShoot() ? (hasAlternate && e.useAlternate) ? requiredAlternate : requiredItems : ItemStack.with(Items.copper, 0, Items.lead, 0)));
    }

    public void setBars(){
        super.setBars();
        removeBar("units");

        addBar("units", (PowerUnitTurretBuild e) ->{
            UnitType unit = e.peekAmmo().spawnUnit;
            if(unit == null) return null;
            return new Bar(() -> Core.bundle.format("bar.unitcap",
                !Objects.equals(Fonts.getUnicodeStr(unit.localizedName), "") ? Fonts.getUnicodeStr(unit.localizedName) : Iconc.units,
                e.team.data().countType(unit),
                Units.getStringCap(e.team)
            ),() -> Pal.accent,
            () -> (float) e.team.data().countType(unit) / Units.getCap(e.team));
        });

        removeBar("ammo");
        addBar("ammo", (ItemTurretBuild entity) ->
                new Bar(
                        "stat.ammo",
                        Pal.ammo,
                        () -> ((float)entity.totalAmmo - 1f)/ (maxAmmo -1f) //Hide the internal ammo
                )
        );
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.remove(Stat.output);
        stats.add(Stat.output, table -> { //TODO, the build time stat might still be off
            table.row();
            boolean[] show = {true};

            //power unit
            table.table(Styles.grayPanel, nu ->{
                UnitType displayUnit = ammoTypes.get(internalItem).spawnUnit;
                if (displayUnit == null || internalItem == null) return;
                nu.row();
                nu.table(Styles.grayPanel, b -> {
                    if (!displayUnit.isBanned())
                        b.image(displayUnit.fullIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                    else
                        b.image(Icon.cancel.getRegion()).color(Pal.remove).size(40).pad(10f).left().scaling(Scaling.fit);

                    b.table(info -> {
                        if (internalItem != null) info.table(title -> {
                            title.image(internalItem.fullIcon).size(3 * 8).left().scaling(Scaling.fit).top();
                            title.add(Core.bundle.get("stat.olupis-unitpowercost")).left().top().padLeft(5f);
                        }).left().row();
                        info.add(displayUnit.localizedName).left().row();
                        info.add("[lightgray]"+Math.round(NyfalisStats.unitReloadTime(ammoTypes.get(internalItem).reloadMultiplier) * reload / 60) + " " + StatUnit.seconds.localized()).left().row();
                        if (Core.settings.getBool("console")) info.add(displayUnit.name).left().color(Color.lightGray);
                    });
                    b.button("?", Styles.flatBordert, () -> ui.content.show(displayUnit)).size(40f).pad(10).right().grow().visible(displayUnit::unlockedNow);
                }).growX().pad(5).row();
            }).growX().pad(5).row();

            //Divider
            table.image().color(Pal.accent).height(3.0F).pad(3.0F).growX().row();

            //Items
            table.add(new Table(NyfalisColors.infoPanel, b ->{
                b.button(Icon.upOpen, Styles.emptyi, () -> show[0] = !show[0]).update(i -> i.getStyle().imageUp = (!show[0] ? Icon.upOpen : Icon.downOpen)).pad(10).padRight(4).left();
                for(ItemStack stack : requiredItems){
                    b.table(z -> { //v8 / Be removed itemsdiplay & i cant be bother to set up compiling equivalent so this is why this exists
                        z.add(new Image(stack.item.uiIcon)).size(32f).scaling(Scaling.fit);
                        z.add(new Table(t -> {
                            t.left().bottom();
                            t.add(stack.amount >= 1000 ? UI.formatAmount(stack.amount) : stack.amount + "").style(Styles.outlineLabel);
                            t.pack();
                        }));
                    });
                }
            }).align(Align.center)).growX().pad(5);
            table.row();

            //Units
            table.collapser(nu -> this.ammoTypes.each((item, bul) -> {
                UnitType displayUnit = bul.spawnUnit;
                if (displayUnit == null || item == internalItem) return;
                nu.row();
                nu.table(Styles.grayPanel, b -> {
                    if (!displayUnit.isBanned())
                        b.image(displayUnit.fullIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                    else
                        b.image(Icon.cancel.getRegion()).color(Pal.remove).size(40).pad(10f).left().scaling(Scaling.fit);

                    b.table(info -> {
                        if (item != null) info.table(title -> {
                            title.image(item.fullIcon).size(3 * 8).left().scaling(Scaling.fit).top();
                            title.add(item.localizedName).left().top().padLeft(5f);
                        }).left().row();
                        info.add(displayUnit.localizedName).left().row();
                        float mul  = 1 + Math.abs(1 - bul.reloadMultiplier);
                        info.add("[lightgray]"+Math.round(mul * reload / 60) + " " + StatUnit.seconds.localized()).left().row();
                        if (Core.settings.getBool("console")) info.add(displayUnit.name).left().color(Color.lightGray);
                    });
                    b.button("?", Styles.flatBordert, () -> ui.content.show(displayUnit)).size(40f).pad(10).right().grow().visible(displayUnit::unlockedNow);
                }).growX().pad(5).row();
            }), () -> show[0]).growX();
        });
    }

    public class PowerUnitTurretBuild extends ItemUnitTurretBuild {
        //note: Setting ammo multiplier bellow 1f causes ammo not to be properly added
        @Override
        public void read(Reads read, byte revision){
            if(revision > 1){ //Back when this used to extend powerTurret, prevents reading of item entry
                super.read(read, revision);
            }

            if(revision == 1){
                reloadCounter = read.f();
                rotation = read.f();
            }
        }

        @Override
        public boolean hasAmmo(){
            if(payload != null) return false;
            if(!hasReqItems() && regularShoot()) return false;

            //skip first entry if it has less than the required amount of ammo
            if(ammo.size >= 2 && ammo.peek().amount < ammoPerShot && ammo.get(ammo.size - 2).amount >= ammoPerShot){
                ammo.swap(ammo.size - 1, ammo.size - 2);
            }
            return true;
        }

        @Override
        public boolean hasReqItems() {
            if(!regularShoot()) return true;
            return super.hasReqItems();
        }

        @Override
        protected void shoot(BulletType type){
            boolean creatable = shootCreatable(type);
            if(direction != -1){
                shootPayload(type, true);
            } else{
                if(payload != null) payload = null;
                shootRegular(type, creatable, true);
            }

            if(consumeAmmoOnce && regularShoot()) useAmmo();
        }

        public boolean regularShoot(){
            if(peekAmmo() == null || ammo.isEmpty() || ammo.peek() == null) return false;
            if((ammo.peek() instanceof  ItemEntry i && i.item ==internalItem )) return false;
            return ammo.peek().amount >= ammoPerShot;
        }

        @Override
        public @Nullable BulletType peekAmmo(){
            if (ammo.isEmpty() || ammo.peek() == null || ammo.peek().amount < ammoPerShot) return ammoTypes.get(internalItem);
            return super.peekAmmo();
        }

        @Override
        public UnitType getUnit(){
            if(ammo.size > 0 && peekAmmo().spawnUnit != null){
                if(useAlternate && peekAmmo() instanceof SpawnHelperBulletType bt ){
                    if(bt.alternateType != null) return bt.alternateType.spawnUnit;
                }
                return peekAmmo().spawnUnit;
            }
            return ammoTypes.get(internalItem).spawnUnit;
        }

        @Override
        public void updateTile() {
            //we "handle in" an item for when we're empty, so ItemTurret's consumer is happy and lets us consume power
            if(ammo.isEmpty() || ammo.peek() == null || ammo.peek().amount < ammoPerShot){
                handleItem(this, internalItem);
            }
            super.updateTile();
        }
    }
}
