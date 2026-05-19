package olupis.world.entities.units;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.struct.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.type.*;

public class BatHelperUnitType extends NyfalisUnitType {
    public UnitType main = UnitTypes.alpha;
    public Seq<StatusEffect> blacklist = Seq.with(StatusEffects.unmoving, StatusEffects.disarmed, StatusEffects.invincible);
    public TextureRegion overlayRegion;

    public BatHelperUnitType(String name){
        super(name);
        constructor = main.constructor;
        flying = true;
    }

    public BatHelperUnitType(UnitType main){
        this(main.name + "-air");
        this.main = main;
    }


    public BatHelperUnitType(String name, UnitType main){
        this(name);
        this.main = main;
    }

    @Override
    public void load() {
        super.load();
        region = Core.atlas.find(main.name);
        overlayRegion = Core.atlas.find("olupis-bat-helper");
        localizedName = main.localizedName + " " + Core.bundle.get("olupis.air.name");
    }

        @Override
    public void update(Unit unit){
        super.update(unit);

        Unit u = this.main.create(unit.team);
        u.stack = unit.stack;

        for (StatusEffect e : Vars.content.statusEffects()) {
            if(unit.hasEffect(e)){
                boolean enemy = !blacklist.contains(e) && (unit.team == Vars.state.rules.waveTeam);
                u.apply(e, enemy  ? 999999f : u.getDuration(e));
            };
        }

        u.set(unit.x, unit.y);
        if(!Vars.net.client()){
            u.add();
        }
        unit.remove();
    }

    @Override
    public void createIcons(MultiPacker packer){
        super.createIcons(packer);

        Pixmap base = Core.atlas.getPixmap(region).crop();
        base.draw(Core.atlas.getPixmap(overlayRegion), true);
        packer.add(PageType.main, "unit-" + name + "-full", base);
        base.dispose();
    }

}
