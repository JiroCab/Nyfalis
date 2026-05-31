package olupis.world.blocks.defence;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.ObjectMap.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import olupis.content.*;
import olupis.world.blocks.defence.Articulator.*;
import olupis.world.blocks.turret.*;
import olupis.world.entities.*;
import olupis.world.entities.bullets.*;
import olupis.world.entities.entities.*;
import olupis.world.entities.packets.*;
import olupis.world.entities.units.*;
import olupis.world.interfaces.*;

import java.util.*;

import static mindustry.Vars.*;
import static olupis.world.NyfWorldFuckingHelper.renderConfigIndicator;

/*The cross bread of a Turret and Unit factory, for the sake of being different
Now with hints of UnitAssembler for extra spice
This whole thing is a overcomplicated mess, thank you and why rushie*/
public class ItemUnitTurret extends NyfalisItemTurret{
    /*common required items for all unit types*/
    public ItemStack[] requiredItems = ItemStack.with(Items.copper, 20);
    public ItemStack[] requiredAlternate = ItemStack.with(NyfalisItemsLiquid.aluminum, 30, Items.copper, 60);
    public int alternateCapacity = itemCapacity * 2;
    /*Parameters when failing to make a unit*/
    public Sound failedMakeSound = Sounds.explosionDull;
    public float failedMakeSoundPitch = 0.7f, getFailedMakeSoundVolume = 0.6f;
    public Effect failedMakeFx = NyfalisFxs.explosionFailedMake;
    public TextureRegion bottomRegion, rotatorRegion, radarRegion;
    /*Hovering Shows the unit creation*/
    public boolean hoverShowsSpawn = true, payloadExitShow = true, drawOnTarget = false, arrowShootPos = true, unitFactory = false;
    /*Aim at the rally point*/
    public boolean rallyAim = true;
    /*Aim for closest liquid*/
    public boolean liquidAim = false;
    public boolean setDynamicConsumer = true;
    //Module pareameters
    public Block statArticulator;
    public boolean hasAlternate = true, boosterAlternate = false;
    public int minAltTier = Integer.MIN_VALUE, maxAltTier = 1;
    public @Nullable String boosterDesc;

    //For Shooting whatever is in payload as a bullet
    public float payloadSpeed = 0.7f, payloadRotateSpeed = 5f;

    /*Todo:  tier/unit switch when a component block is attached (t4/5 erekir) */

    public ItemUnitTurret(String name){
        super(name);
        commandable = configurable = outputsPayload = clearOnDoubleTap = true;
        playerControllable = removeItemsBars =  false;
        shootSound = NyfalisSounds.shootSpawn;
        drawer = new DrawDefault();
        fogRadius = -1;
        range = 0f;
        selectionColumns = 5;
        config(UnitCommand.class, (ItemUnitTurretBuild build, UnitCommand command) -> build.command = command);
        config(Integer.class, (ItemUnitTurretBuild build, Integer direction) -> build.direction = direction);
        config(Item.class, (ItemUnitTurretBuild build, Item cheat) -> build.cheatConfig = cheat);

        configClear((ItemUnitTurretBuild build) ->{
            build.command = null;
            build.direction = -1;
        });

        boosterDesc = Core.bundle.getOrNull(getContentType() + "." + this.name + ".asBoost");
    }


    public void setBars(){
        super.setBars();
        if(hasAlternate ){
            addBar("modules", (ItemUnitTurretBuild entity) -> entity.modules.size <= 0 ? null : new Bar("bar.power", Pal.ammo,() -> Mathf.clamp(entity.moduleEfficiency() / entity.modules.size)));
        }

        addBar("bar.progress", (ItemUnitTurretBuild entity) -> new Bar("bar.progress", Pal.ammo,() -> entity.reloadCounter / reload));

        addBar("units", (ItemUnitTurretBuild e) -> e.peekAmmo() == null || e.getUnit() != null &&  !e.getUnit().useUnitCap ? null : new Bar(() ->
                e.getUnit() == null ? "[lightgray]" + Iconc.cancel :
                        Core.bundle.format("bar.unitcap",
                                !Objects.equals(Fonts.getUnicodeStr(e.getUnit().name), "") ? Fonts.getUnicodeStr(e.getUnit().name) : Iconc.units,
                                e.team.data().countType(e.getUnit()),
                                Units.getStringCap(e.team)
                        ),
                () -> Pal.power,
                () -> e.peekAmmo() == null ? 0f : e.getUnit() == null ? 0f : (float) e.team.data().countType(e.getUnit()) / Units.getCap(e.team)
        ));

    }

    @Override
    public void load(){
        bottomRegion = Core.atlas.find(name + "-bottom", "olupis-construct-bottom");
        rotatorRegion = Core.atlas.find(name + "-rotator", "olupis-construct-rotator");
        radarRegion = Core.atlas.find(name + "-radar", "olupis-hive-radar");
        super.load();
    }

    @Override
    public TextureRegion[] icons(){
        if(!(drawer instanceof  DrawDefault))return drawer.finalIcons(this);
        else return new TextureRegion[]{bottomRegion, region, rotatorRegion, teamRegions[Team.sharded.id]};
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out){
        if(!(drawer instanceof DrawDefault))drawer.getRegionsToOutline(this, out);
        else {generatedIcons = null;}
    }

    @Override
    public void setStats(){ //TODO, the build time stat might still be off, but this is good enough
        super.setStats();
        stats.remove(Stat.ammo);
        stats.remove(Stat.reload);
        stats.remove(Stat.targetsAir);
        stats.remove(Stat.inaccuracy);
        stats.remove(Stat.targetsGround);
        stats.remove(Stat.input);

        if(range <= 1)stats.remove(Stat.shootRange);
        stats.add(Stat.output, table ->{
            HashMap<Item, BulletType> alts = new HashMap<>(), nor = new HashMap<>();
            this.ammoTypes.each((item, bul) -> {
                if(bul instanceof SpawnHelperBulletType spw){
                    if(spw.alternateType != null) alts.put(item, spw.alternateType);
                    nor.put(item, spw);
                }
            });

            boolean[] show = {true, true};
            //Normal items
            if((requiredItems.length > 0)){
                table.row();

                table.add(new Table(NyfalisColors.infoPanel , b ->{
                    b.button(Icon.upOpen, Styles.emptyi, () -> show[0] = !show[0]).update(i -> i.getStyle().imageUp = (!show[0] ? Icon.upOpen : Icon.downOpen)).pad(10).padRight(4).left();
                    b.table( bt -> {
                        for(ItemStack stack : requiredItems) bt.add(StatValues.displayItem(stack.item, stack.amount, false)).pad(5f).padRight(10f).padLeft(10f);
                    }).growX().center();

                })).growX().pad(5);
                table.row();
            }

            //Normal Units
            statsUnitLists(table, () -> show[0], nor);

            //Alternate banned check
            if(statArticulator != null && hasAlternate && (!statArticulator.unlockedNow() || !statArticulator.isVisible())){
                table.row();
                table.table(Styles.grayPanel, b -> {
                    if(!statArticulator.unlockedNow()) b.image(Icon.tree.getRegion()).size(30).pad(10f).left().scaling(Scaling.fit).tooltip(statArticulator.localizedName);
                    else b.image(Icon.cancel.getRegion()).color(Pal.remove).size(30).pad(10f).left().scaling(Scaling.fit).tooltip(statArticulator.localizedName);
                    b.add(new Table(o -> o.add(new Image(statArticulator.uiIcon)).size(30f).scaling(Scaling.fit).tooltip(statArticulator.localizedName))).left().pad(10f);
                }).growX().pad(5);
                return;
            }

            //Divider
            if(statArticulator != null)table.image().color(Pal.accent).height(3.0F).pad(3.0F).growX().row();

            //Alternate Items
            if(statArticulator != null && statArticulator.unlockedNow() && statArticulator.isVisible() ){
                table.add(new Table(NyfalisColors.infoPanel, r ->{
                    r.button(Icon.upOpen, Styles.emptyi, () -> show[1] = !show[1]).update(iu -> iu.getStyle().imageUp = (!show[1] ? Icon.upOpen : Icon.downOpen)).pad(10).padRight(4).left();
                    r.table(in -> {
                        in.add(new Table(c ->{
                            c.add(new Table(o -> {
                                o.add(new Image(statArticulator.uiIcon)).size(32f).scaling(Scaling.fit).with(owo -> owo.clicked(() -> ui.content.show(statArticulator)));
                            })).left().pad(10f);
                            c.table(info -> {
                                info.add(statArticulator.localizedName).left();
                                if (Core.settings.getBool("console")) {
                                    info.row();
                                    info.add(statArticulator.name).left().color(Color.lightGray);
                                }
                            });
                            c.button("?", Styles.flatBordert, () -> ui.content.show(statArticulator)).size(40f).pad(10).right().grow().visible(statArticulator::unlockedNow).row();
                        })).row();
                        in.add(new Table(i -> {
                            for (ItemStack stack : requiredAlternate) {
                                i.add(StatValues.displayItem(stack.item, stack.amount, false)).pad(5f).padRight(10f).padLeft(10f).center();
                            }
                        })).growX();
                    });
                })).growX().pad(5);
                table.row();
            }

            //Alternate Units
            statsUnitLists(table, () -> show[1], alts);
        });
        if(boosterAlternate){
            stats.add(Stat.boostEffect, NyfalisStats.modulesBoosters(minAltTier, maxAltTier, this));
        }

        if(heatRequirement > 0) stats.add(Stat.input, heatRequirement, StatUnit.heatUnits);
    }

    public void statsUnitLists(Table table, Boolp show, HashMap<Item, BulletType> types){
        table.collapser(nu -> {
            for(Map.Entry<Item, BulletType> entry : types.entrySet()){
                Item item = entry.getKey();
                BulletType bul = entry.getValue();
                UnitType displayUnit = bul.spawnUnit;
                if(displayUnit == null) continue;

                nu.row();
                nu.table(Styles.grayPanel, b -> {
                    if(displayUnit.isBanned()){
                        b.image(Icon.cancel.getRegion()).color(Pal.remove).size(30).pad(10f).left().scaling(Scaling.fit);
                        b.image(displayUnit.uiIcon).size(30).pad(10f).left().scaling(Scaling.fit);
                    }
                    else if(!displayUnit.unlockedNowHost()) b.image(Icon.lock).color(Pal.darkerGray).size(30).pad(10f).left().scaling(Scaling.fit);
                    else{
                        b.add(new Image(displayUnit.fullIcon)).with(owo -> owo.clicked(() -> ui.content.show(displayUnit))).size(40).padLeft(10f).scaling(Scaling.fit);
                        b.table(info -> {
                            if(item != null) info.add(StatValues.displayItem(item, 0, true)).left().row();
                            info.add(displayUnit.localizedName).left().row();
                            float mul = 1 + Math.abs(1 - bul.reloadMultiplier) * 2;
                            info.add("[lightgray]" + Math.round(mul * reload / 60) + " " + StatUnit.seconds.localized()).left().row();
                            if(Core.settings.getBool("console")) info.add(displayUnit.name).left().color(Color.lightGray);
                        });
                        b.button("?", Styles.flatBordert, () -> ui.content.show(displayUnit)).size(40f).pad(10).right().grow().visible(displayUnit::unlockedNow);
                    }
                }).growX().pad(5);
                }
        }, show).growX();
        table.row();
    }

    @Override
    public void init(){
        if(setDynamicConsumer) consume(new ConsumeItemDynamic((ItemUnitTurretBuild e) -> (e.useAlternate && hasAlternate) ? requiredAlternate : requiredItems));

        consumeBuilder.each(c -> c.multiplier = b -> unitFactory ? state.rules.unitCost(b.team) : 1f);
        super.init();
    }

    public Seq<UnitType> allUnitTypes(){
        Seq<UnitType> out = new Seq<>();
        out.add(possibleUnitTypes(false)).add(possibleUnitTypes(true));
        return out;
    }

    public Seq<UnitType> possibleUnitTypes(){
        return possibleUnitTypes(false);
    }
    public Seq<UnitType> possibleUnitTypes(boolean alts){
        Seq<UnitType> out = new Seq<>();
        for(Entry<Item, BulletType> b : ammoTypes){
            if(b.value instanceof SpawnHelperBulletType s){
                if(!alts && s.spawnUnit != null) out.add(s.spawnUnit);
                else if(alts && s.alternateType != null && s.alternateType.spawnUnit != null) out.add(s.alternateType.spawnUnit);
            }
        }
        return out;
    }

    public Item unitTypeToAmmo(UnitType t){
        for(Entry<Item, BulletType> b : ammoTypes){
            if(b.value instanceof SpawnHelperBulletType s){
                if(s.spawnUnit == t) return b.key;
                if(s.alternateType != null && s.alternateType.spawnUnit == t) return b.key;
            }
        }
        return NyfalisItemsLiquid.powerAmmoItem;
    }

    public class ItemUnitTurretBuild<T extends UnitPayload> extends NyfalisItemTurretBuild implements Moduleable{
        public @Nullable Vec2 commandPos;
        public float time, speedScl;
        public int direction = -1, readUnitId = -1;
        public @Nullable Item cheatConfig = null;
        public @Nullable UnitPayload payload;
        public Vec2 payVector = new Vec2();
        public @Nullable UnitCommand command;
        public Seq<Articulator.ArticulatorBuild> modules = new Seq<>();
        public boolean useAlternate = false;
        public @Nullable Unit child = null;

        @Override
        public Seq<ArticulatorBuild> getModules(){
            return modules;
        }

        @Override
        public int maxTier(){
            return maxAltTier;
        }

        @Override
        public int minTier(){
            return minAltTier;
        }

        @Override
        public String boosterDesc(){
            return boosterDesc;
        }

        public void checkTier(){
            if(!hasAlternate && !boosterAlternate) return;
            boolean check =  modules.size > 0;
            if(check != useAlternate) tierChange();
            useAlternate = check;
        }

        public void tierChange(){
            if(reloadCounter > 0){
                failedMakeFx.create(x, y, rotation -90, Pal.plasticSmoke, null);
                failedMakeSound.at(x, y, failedMakeSoundPitch, getFailedMakeSoundVolume);
            }
            reloadCounter = 0;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return ((super.acceptItem(source, item) && !ammoTypes.get(item).spawnUnit.isBanned())
                || (!useAlternate || !hasAlternate) && (Arrays.stream(requiredItems).anyMatch( i -> item == i.item) && items.get(item) < getMaximumAccepted(item)))
                || (useAlternate && hasAlternate) && (Arrays.stream(requiredAlternate).anyMatch( i -> item == i.item) && items.get(item) < alternateCapacity);
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source){
            if(acceptItem(self(), item) && block.hasItems && (source == null || source.team() == team) && !ammoTypes.containsKey(item)){
                return Math.min((useAlternate ? alternateCapacity : getMaximumAccepted(item)) - items.get(item), amount);
            } else return super.acceptStack(item, amount, source);
        }

        @Override
        public void handleItem(Building source, Item item){
            boolean
                inv =( hasAlternate && Arrays.stream(requiredAlternate).anyMatch(i -> item == i.item) && useAlternate && items.get(item) < alternateCapacity )|| (Arrays.stream(requiredItems).anyMatch(i -> item == i.item)  && items.get(item) < getMaximumAccepted(item)),
                amo = ammoTypes.containsKey(item) && totalAmmo + ammoTypes.get(item).ammoMultiplier <= maxAmmo;

            if(inv && amo){
                if(Mathf.randomBoolean()) super.handleItem(source, item);
                else items.add(item, 1);
            }
            else if (amo)super.handleItem(source, item);
            else items.add(item, 1);
        }

        public int getMaximumAccepted(Item item) {
            if(unitFactory){
                if(hasAlternate && useAlternate){
                    if(Arrays.stream(requiredAlternate).anyMatch(i -> item == i.item)) return Math.round(alternateCapacity * state.rules.unitCost(team));
                } else {
                    if(Arrays.stream(requiredItems).anyMatch(i -> item == i.item)) return Math.round(itemCapacity * state.rules.unitCost(team));
                }
            }


            return this.block.itemCapacity;
        }

        @Override
        public void updateTile(){
            speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
            time += edelta() * speedScl * (unitFactory ? Vars.state.rules.unitBuildSpeed(team) : 1f);

            moveOutPayload();
            super.updateTile();

            if(direction == -1 && payload != null){
                BulletType nya = new SpawnHelperBulletType(){{
                    shootEffect = Fx.shootBig;
                    ammoMultiplier = 1f;
                    reloadMultiplier = 0.75f;
                    spawnUnit = payload.unit.type;
                }};
                shootRegular(nya, shootCreatable(nya), false);
                payload = null;
            }

            checkTier();

            if(target == null && peekAmmoAlt() != null && peekAmmoAlt() instanceof  SpawnHelperBulletType s && s.targetsFlames ){
                @Nullable Fire t = Groups.fire.find( f -> f.within(this, range));
                if(t == null) return;

                targetPos.set(t);
                float targetRot = angleTo(t);

                if(shouldTurn() && !isControlled() && !logicControlled()){
                    turnToTarget(targetRot);
                }

                if(Angles.angleDist(rotation, targetRot) < Math.max(shootCone / 2, 4f)){
                    wasShooting = true;
                    updateReload();
                    updateShooting();
                }
            }

            if(readUnitId != -1){
                child = Groups.unit.getByID(readUnitId);

                if(child != null && !net.client()){
                    if(child instanceof AmmoEnabledUnitClass ae && ae.parent == null) ae.setParent(this);
                    readUnitId = -1;
                }
            }

            if(child != null && child.dead){
                if(child instanceof AmmoEnabledUnitClass ae && ae.parent == null) ae.setParent(null);
                child = null;

            }
        }

        public void resupplied(){

        }

        public UnitType checkUnit(Item item){
            if(useAlternate && ammoTypes.get(item) instanceof  SpawnHelperBulletType s ) return s.getUnits(useAlternate);
            return ammoTypes.get(item).spawnUnit;
        }

        @Override
        public BulletType useAmmo(){
            if(cheatingAlt()) return peekAmmo();
            return super.useAmmo();
        }
        @Override
        public boolean hasAmmo(){
            if(payload != null) return false;
            if(!hasReqItems()) return false;
            return super.hasAmmo();
        }

        public UnitType getUnit(){
            if(ammo.size > 0 && peekAmmo().spawnUnit != null){
                if(useAlternate && peekAmmo() instanceof  SpawnHelperBulletType bt ){
                    if(bt.alternateType != null) return bt.alternateType.spawnUnit;
                }
                return peekAmmo().spawnUnit;
            }
            return null;
        }

        public boolean hasReqItems(){
            for (ItemStack req : (hasAlternate && useAlternate) ? requiredAlternate : requiredItems) {
                if(items.get(req.item) >= req.amount *  state.rules.unitCost(team)) continue;
                return false;
            }
            return true;
        }

        public boolean shootCreatable (BulletType type){
            return !type.spawnUnit.isBanned() && (type.spawnUnit.unlockedNowHost() && state.isCampaign() || !state.isCampaign());
        }

        @Override
        protected void updateShooting(){
            if(!isUnitFactory() && child != null) return;
            if(reloadCounter >= reload && !charging() && shootWarmup >= minWarmup){
                BulletType type = peekAmmo();
                if(useAlternate && type instanceof SpawnHelperBulletType spw && spw.alternateType != null) type = spw.alternateType;

                shoot(type);

                reloadCounter %= reload;
            }
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

            if(consumeAmmoOnce) useAmmo();
        }

        protected void shootPayload(BulletType type, boolean consume){
            if(type.spawnUnit == null) return;
            if(payload == null) {
                payload = new UnitPayload(type.spawnUnit.create(team));
                Unit p = (payload).unit;
                if(moduleBoosted())moduleUnitBoosts(p);
                if (commandPos != null && p.isCommandable()) {
                    p.command().commandPosition(commandPos);
                }
                if (p.isCommandable() && command != null) {
                    p.command().command(command);
                }
                Events.fire(new EventType.UnitCreateEvent(p, this));
                payVector.setZero();
                if(consume) consume();
            }
            moveOutPayload();
        }

        protected void shootRegular(BulletType type, boolean creatable, boolean consume){
            boolean spawn =creatable && (state.rules.waveTeam == this.team || (type.spawnUnit.useUnitCap && this.team.data().countType(type.spawnUnit) < this.team.data().unitCap) || !type.spawnUnit.useUnitCap);
            /*don't create the unit if it's banned or at unit cap*/
            if(spawn){
                BulletType t = type;

                if(moduleBoosted() && type instanceof SpawnHelperBulletType spb){
                    spb.handler = this::moduleUnitBoosts;
                    t = spb;
                }

                if(consume)consume();
                float rot = direction == -1 ? rotation - 90 : (direction -1) * 90,
                        bulletX = x + Angles.trnsx(rot, shootX, shootY),
                        bulletY = y + Angles.trnsy(rot, shootX, shootY);

                if (shoot.firstShotDelay > 0) {
                    chargeSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));
                    type.chargeEffect.at(bulletX, bulletY, rot);
                }

                BulletType finalT = t;
                shoot.shoot(barrelCounter, (xOffset, yOffset, angle, delay, mover) -> {
                    queuedBullets++;
                    if (delay > 0f) {
                        Time.run(delay, () -> bullet(finalT, xOffset, yOffset, angle, mover));
                    } else {
                        bullet(finalT, xOffset, yOffset, angle, mover);
                    }
                }, () -> barrelCounter++);
            }else {
                failedMakeFx.create(x, y, rotation -90, Pal.plasticSmoke, null);
                failedMakeSound.at(x, y, failedMakeSoundPitch, getFailedMakeSoundVolume);
            }
        }

        public void moveOutPayload(){
            if(payload == null) return;

            updatePayload();

            Vec2 dest = Tmp.v1.trns((direction + 1) * 90, size * tilesize / 2f);
//            payloadRotation = Angles.moveToward(payloadRotation, rotdeg(), payloadRotateSpeed * delta());
            payVector.approach(dest, payloadSpeed * delta());

            int trns = this.block.size / 2 + 1;
            Building front = this.nearby(Geometry.d4((direction + 1)).x * trns, Geometry.d4((direction + 1)).y * trns);
            boolean canDump = front == null || !front.tile.solid(),
                        canMove = front != null && (front.block.outputsPayload || front.block.acceptsPayload);

            if(canDump && !canMove) pushOutput(payload, 1f - (payVector.dst(dest) / (size * tilesize / 2f)));

            if(payVector.within(dest, 0.001f)){
                payVector.clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);

                if(canMove){
                    if(movePayload(payload)){
                        payload = null;
                    }
                }else if(canDump) dumpPayload();
            }
        }

        @Override
        public boolean movePayload(Payload todump) {
            int trns = this.block.size / 2 + 1;
            Tile next = this.tile.nearby(Geometry.d4(direction + 1).x * trns, Geometry.d4(direction + 1).y * trns);
            if (next != null && next.build != null && next.build.team == this.team && next.build.acceptPayload(this, todump)) {
                next.build.handlePayload(this, todump);
                return true;
            } else {
                return false;
            }
        }

        public void pushOutput(Payload payload, float progress){
            float thresh = 0.55f;
            if(progress >= thresh){
                boolean legStep = payload instanceof UnitPayload u && u.unit.type.allowLegStep;
                float size = payload.size(), radius = size/2f, x = payload.x(), y = payload.y(), scl = Mathf.clamp(((progress - thresh) / (1f - thresh)) * 1.1f);

                Groups.unit.intersect(x - size/2f, y - size/2f, size, size, u -> {
                    float dst = u.dst(payload);
                    float rs = radius + u.hitSize/2f;
                    if(u.isGrounded() && u.type.allowLegStep == legStep && dst < rs)
                        u.vel.add(Tmp.v1.set(u.x - x, u.y - y).setLength(Math.min(rs - dst, 1f)).scl(scl));
                });
            }
        }

        public void dumpPayload(){
            //translate payload forward slightly
            float tx = Angles.trnsx(payload.rotation(), 0.1f), ty = Angles.trnsy(payload.rotation(), 0.1f);
            payload.set(payload.x() + tx, payload.y() + ty, payload.rotation());

            if(payload.dump()) payload = null;
            else payload.set(payload.x() - tx, payload.y() - ty, payload.rotation());
        }

        public void updatePayload(){
            if(payload != null) payload.set(x + payVector.x, y + payVector.y, (direction + 1) * 90);
        }

        protected void updateReload(){
            float multiplier = 1f;
            if(hasAmmo()){
                if(peekAmmoAlt() != null) multiplier = peekAmmoAlt().reloadMultiplier;
                else multiplier = peekAmmo().reloadMultiplier;
            }
            multiplier *= unitFactory ? state.rules.unitBuildSpeed(team) : 1f;
            reloadCounter += delta() * multiplier * baseReloadSpeed();

            //cap reload for visual reasons
            reloadCounter = Math.min(reloadCounter, reload);
        }

        public boolean moduleBoosted(){
            if(!boosterAlternate) return false;
            return modules.size >= 1;
        }

        public void moduleUnitBoosts(Unit p){
            int lastTier = -1;
            for(ArticulatorBuild m : modules){
                if(m.tier() == lastTier)continue;
                if(m.tier() > lastTier) lastTier = m.tier();

                if(m.tier() == 1) p.apply(StatusEffects.overclock, Float.MAX_VALUE);

            }

        }

        @Override
        public void draw(){
            if(!(drawer instanceof DrawDefault)){
                super.draw();
            }else{
                Draw.z(Layer.block + 0.1f);
                float rot = direction == -1 ? rotation -90: direction * 90;
                Draw.rect(bottomRegion, x, y);
                Draw.rect(rotatorRegion, x, y, rot);

                if(!unitFactory && child != null && !child.dead){
                    Draw.draw(Layer.blockOver, () ->{
                        Draw.rect(radarRegion, x, y, child.angleTo(this) + 90);
                    });
                }
                else if (peekAmmo() != null) {
                    UnitType unt = useAlternate && peekAmmo() instanceof SpawnHelperBulletType spw && spw.alternateType != null ? spw.alternateType.spawnUnit : peekAmmo().spawnUnit;
                    if (unt != null) { Draw.draw(Layer.blockOver, () ->{
                        boolean building = shootCreatable(peekAmmo()) && (this.reloadCounter / reload) < 0.99f;
                        if (building) { Drawf.construct(this, unt.fullIcon != null ? unt.fullIcon : unt.region, rot, this.reloadCounter / reload, speedScl, time);}
                        else {
                            Draw.alpha(reloadCounter / reload);
                            Draw.rect(unt.fullIcon, x, y, rot);

                            Draw.color(Pal.accent);
                            Draw.alpha((reloadCounter / reload) / 1.2f);
                            Lines.lineAngleCenter(this.x + Mathf.sin(this.time, 20f, (this.block.size * tilesize - 4f) / 4f), this.y, 90, this.block.size * tilesize - 4f);
                            Draw.reset();

                            Draw.color(Pal.remove, Math.min(reloadCounter / reload, 0.8f));
                            if(!unt.unlockedNowHost()) Draw.rect(Icon.tree.getRegion(), x, y);
                            else if(unt.isBanned()) Draw.rect(Icon.cancel.getRegion(), x, y);
                            else if(team.data().countType(unt) < team.data().unitCap && unt.useUnitCap)  Draw.rect(Icon.warning.getRegion(), x, y);
                            Draw.reset();
                        }
                    });}
                }
                Draw.z(Layer.blockBuilding + 0.1f);
                if(payload != null){
                    payload.draw();
                }
                Draw.rect(region, x, y);
            }

            if(unitFactory){
                float scl  = 0.75f;
                TextureRegionDrawable[] icons = new TextureRegionDrawable[]{Icon.export, Icon.up, Icon.left, Icon.down, Icon.right};
                Seq<TextureRegion> out = new Seq<>();
                out.add(icons[direction + 1].getRegion());
                out.add(command != null ? command.getIcon().getRegion() : getUnit() != null ? getUnit().defaultCommand.getIcon().getRegion() : Icon.cancel.getRegion());
                out.add( cheatConfig != null ? cheatConfig.uiIcon : Core.atlas.find("error"));
                renderConfigIndicator(this, scl, out);
            } //todo draw to child when not a unit factory

        }

        @Override
        public void drawTeam(){
            Draw.draw(Layer.blockOver +0.1f, () ->{
                Draw.color(this.team.color);
                Draw.rect("block-border", this.x - (float)(this.block.size * 8) / 2.0F + 4.0F, this.y - (float)(this.block.size * 8) / 2.0F + 4.0F);
                Draw.color();
            });
        }

        @Override
        public void drawSelect(){
            /*instead of dealing/trying to make the block itself rotate, we have this*/
            Lines.stroke(1f, team.color);
            Draw.color(team.color, 0.8f);

            float sx = arrowShootPos ? shootX : 0f, sy = arrowShootPos ? shootY : 6f,
                    rot = direction == -1 ? rotation - 90 : direction * 90,
                    squareX = x + Angles.trnsx(rot , sx, sy), squareY = y + Angles.trnsy(rot, sx, sy);
            if(hoverShowsSpawn && direction == -1){
                Lines.square(squareX, squareY + 0.5f, 3.5f, Time.time * 0.5f);
            } else if(payloadExitShow && direction != -1){
                TextureRegion regionArrow = Core.atlas.find("place-arrow");

                Draw.rect(regionArrow, squareX, squareY -1f, (float) regionArrow.width / size, (float) regionArrow.height / size,direction * 90);
            }
            Draw.reset();
            if(drawOnTarget && target != null){
                Drawf.target(target.getX(), target.getY(), 7f * Interp.swingIn.apply(1f), this.team().color);
            }

            super.drawSelect();
        }

        @Override
        public void display(Table table) {
            super.display(table);

            TextureRegionDrawable reg = new TextureRegionDrawable();

            table.row();
            table.collapser(t ->{
                t.left();
                t.image().update(i -> {
                    i.setDrawable(getUnit() == null ? Icon.cancel : reg.set(getUnit().uiIcon));
                    i.setScaling(Scaling.fit);
                    i.setColor(getUnit() == null ? Color.lightGray : Color.white);
                }).size(32).padBottom(-4).padRight(2);
                t.label(() -> getUnit() == null ? "@none" : getUnit().localizedName).wrap().width(230f).color(Color.lightGray);
            }, true, () -> getUnit() != null).left();
        }


        @Override
        public Vec2 getCommandPosition(){return commandPos;}

        @Override
        protected void findTarget(){
            if(rallyAim){
                if(commandPos != null) targetPos = commandPos;
                return;
            }
            if(liquidAim){
                target = indexer.findTile(team, x, y, range, t-> !t.checkSolid() && t.floor().isLiquid, false);
            }

            super.findTarget();
        }

        @Override
        public void onCommand(Vec2 tar){commandPos = tar;}

        @Override
        /*Work around for rally point without fully rewriting updateTile*/
        public boolean logicControlled(){return logicControlTime > 0 || commandPos != null;}

        @Override
        public void buildConfiguration(Table table){
            table.background(Styles.black6);
            table.table(t -> {
                buildIcon(t, -1, Icon.export);
                buildIcon(t, 0, Icon.up);
                buildIcon(t, 1, Icon.left);
                buildIcon(t, 2, Icon.down);
                buildIcon(t, 3, Icon.right);
            }).row();

            table.table(t -> {
                var group = new ButtonGroup<ImageButton>();
                group.setMinCheckCount(0);
                int i = 0, columns = 6;
                if(peekAmmo() != null && peekAmmo().spawnUnit != null && (peekAmmo().spawnUnit instanceof NyfalisUnitType nyf && !nyf.constructHideDefault) && peekAmmo().spawnUnit.commands.size >1 ){
                    var unit = peekAmmo().spawnUnit;
                    var list = unit.commands;
                    t.row();
                    for(var item : list){
                        ImageButton button = t.button(item.getIcon(), Styles.clearNoneTogglei, 40f, () -> {
                            command = item;
                            configure(item);
                            deselect();
                        }).tooltip(item.localized()).group(group).get();

                        button.update(() -> button.setChecked(command == item || (command == null && unit.defaultCommand == item)));

                        if(++i % columns == 0){
                            t.row();
                        }
                    }
                }
            });
            cheatIcons(table);
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(block.clearOnDoubleTap){
                if(self() == other){
                    deselect();
                    direction = -1;
                    Call.tileConfig(Vars.player, this, -1);
                    return false;
                }
                return true;
            }
            return self() != other;
        }

        void buildIcon(Table table, int conf, Drawable icon){
            table.button(icon, Styles.clearNoneTogglei, 40f, () -> {
                direction = conf;
                configure(conf);
                deselect();
            }).checked(direction == conf);
        }

        public void cheatIcons(Table table){
            if(!cheatingAlt()) return;

            if(ammo.size <= 0 || ((ammo.size == 1) && ammo.peek().amount <= 10)){

                table.row();

                table.table(t -> ItemSelection.buildTable(
                    ItemUnitTurret.this, t,
                    ammoTypes.keys().toSeq(),
                    () -> cheatConfig,
                    this::requestCheat,
                    selectionRows, selectionColumns
                )).fillX();
                }

        }

        public void requestCheat(Item c){
            handleCheat(c);

            var p = new ConstructorCheatConfigPacket();
            p.build = this;
            p.conf = c;
            Vars.net.send(p, true);
        }

        public void handleCheat(Item c){
            this.cheatConfig = c;
            configure(c);
            ammo.clear();

            totalAmmo = 0;
            this.handleItem(this, cheatConfig);
            tierChange();
        }

        public boolean cheatingAlt() {
            return this.team.rules().cheat || state.rules.unitCost(team) == 0 ;
        }

        @Override
        public Object config() {
            return direction;
        }

        @Override
        public Object senseObject(LAccess sensor){
            if(sensor == LAccess.config){
                if(privileged) return cheatConfig;
                return null;
            }
            if(sensor == LAccess.rotation) return direction == -1 ? rotation : direction * 90f;
            return super.senseObject(sensor);
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.rotation) return direction == -1 ? rotation : direction * 90f;
            if(sensor == LAccess.itemCapacity) return Mathf.round(itemCapacity * state.rules.unitCost(team));
            return super.sense(sensor);
        }

        @Override
        public boolean isShooting(){
            return super.isShooting() && hasAmmo();
        }

        @Override
        public void updateEfficiencyMultiplier(){
            super.updateEfficiencyMultiplier();

            if(modules.size > 0){
                efficiency *= moduleEfficiency() / modules.size;
            }
        }

        @Override
        protected float baseReloadSpeed(){
            if(!unitFactory && child != null && !child.dead && reloadCounter >= reload / 2f) return 0f;
            return hasReqItems() ? efficiency : 0f;
        }

        public boolean isUnitFactory(){
            return unitFactory;
        }

        @Override
        public void onRemoved(){
            if(child != null) AmmoLifeTimeUnitType.callTimeOut(child);
            super.onRemoved();
        }
        @Override
        public void write(Writes write){
            super.write(write);
            TypeIO.writeVecNullable(write, commandPos);
            write.i(direction);
            Payload.write(payload, write);
            write.i(child != null ? child.id : -1);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 3){ /*necessary, to prevent map/save corruption */
                commandPos = TypeIO.readVecNullable(read);
            }
            if(revision >=5 ){
                direction = read.i();
                payload = Payload.read(read);
            } else  direction = -1;
            if(revision >= 6) readUnitId =  read.i();
        }

        @Override
        public byte version(){
            return 6;
        }

        @Override
        public float estimateDps(){
            if(unitFactory) return 0f;
            return super.estimateDps();
        }

        public @Nullable BulletType peekAmmoAlt(){
            return ammo.size == 0 ? null :
                (hasAlternate && useAlternate)  && ammo.peek().type() instanceof SpawnHelperBulletType s ? s.alternateType
                : ammo.peek().type();
        }
    }

}
