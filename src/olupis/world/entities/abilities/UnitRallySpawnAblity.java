package olupis.world.entities.abilities;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.meta.*;
import olupis.input.*;

import java.util.*;

import static mindustry.Vars.*;

public class UnitRallySpawnAblity extends UnitSpawnAbility {

    public UnitRallySpawnAblity(UnitType unit, float spawnTime, float spawnX, float spawnY){
        super(unit, spawnTime, spawnX, spawnY);
    }
    public UnitRallySpawnAblity(){
        super();
    }

    @Override
    public void update(Unit unit){
        timer += Time.delta * state.rules.unitBuildSpeed(unit.team);

        if(timer >= spawnTime && Units.canCreate(unit.team, this.unit)){
            float x = unit.x + Angles.trnsx(unit.rotation, spawnY, spawnX), y = unit.y + Angles.trnsy(unit.rotation, spawnY, spawnX);
            spawnEffect.at(x, y, 0f, parentizeEffects ? unit : null);
            Unit u = this.unit.create(unit.team);
            u.set(x, y);
            u.rotation = unit.rotation;
            if (unit.isCommandable() && u.isCommandable() && unit.command() != null){
                u.command().commandPosition(unit.command().targetPos);
                if(unit.isCommandable() && unit.command().command == NyfalisUnitCommands.nyfalisDeployCommand) u.command().command(UnitCommand.moveCommand);
            }
            Events.fire(new EventType.UnitCreateEvent(u, null, unit));
            if(!Vars.net.client()){
                u.add();
            }

            timer = 0f;
        }
    }

    @Override
    public void draw(Unit unit){
        Draw.draw(Draw.z(), () -> {
            float x = unit.x + Angles.trnsx(unit.rotation, spawnY, spawnX), y = unit.y + Angles.trnsy(unit.rotation, spawnY, spawnX);
            if(Units.canCreate(unit.team, this.unit))Drawf.construct(x, y, this.unit.fullIcon, unit.rotation - 90, timer / spawnTime, 1f, timer);
            else Draw.rect(this.unit.fullIcon, x, y, unit.rotation - 90);
        });
    }

    @Override
    public void addStats(Table t){
        t.add("[lightgray]" + Stat.buildTime.localized() + ": [white]" + Strings.autoFixed(spawnTime / 60f, 2) + " " + StatUnit.seconds.localized());
        t.row();
        t.table( u -> {
            u.image(unit.uiIcon).scaling(Scaling.fit).left();
            u.table(in -> {
                in.add(unit.localizedName).row();
                if (Core.settings.getBool("console")) in.add("[lightgray]" +unit.name + "[]");
            }).center().pad(10f);
            u.button("?", Styles.flatBordert, () -> ui.content.show(unit)).right().growY().visible(unit::unlockedNow).size(40f);
        });
    }

    @Override
    public void displayBars(Unit unit, Table bars){

        bars.add(new Bar("bar.progress", Pal.ammo,() -> Mathf.clamp(timer / spawnTime))).row();

        if(this.unit.useUnitCap){
            bars.add(new Bar(() ->
            Core.bundle.format("bar.unitcap",
            !Objects.equals(Fonts.getUnicodeStr(this.unit.name), "") ? Fonts.getUnicodeStr(this.unit.name) : Iconc.units,
            unit.team.data().countType(this.unit),
            Units.getStringCap(unit.team)
            ),
            () -> Pal.power,
            () -> Mathf.clamp((float)unit.team.data().countType(this.unit) / Units.getCap(unit.team))
            ));
        }

    }


}
