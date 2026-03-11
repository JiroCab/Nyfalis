package olupis.world.blocks.calyx.ineternal;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import olupis.world.EnvUpdater.*;

import static mindustry.Vars.*;
import static olupis.NyfalisVars.*;
import static olupis.world.EnvUpdater.*;

public class GrownFloor extends Floor implements UpdatingEnvironment{
    public int connectionRange = tilesize * 10;
    public Color noHeartColour = Pal.accentBack;

    public GrownFloor(String name){
        super(name);
    }

    public GrownFloor(String name, int variants){
        super(name, variants);
    }

    @Override
    public void drawMain(Tile tile){
        Color prev = Draw.getColor();
        if(!isAlive(tile))Draw.color(noHeartColour);
        super.drawMain(tile);
        Draw.color(prev);
    }

    @Override
    protected void drawEdges(Tile tile){
        Color prev = Draw.getColor();
        if(!isAlive(tile))Draw.color(noHeartColour);
        super.drawEdges(tile);
        Draw.color(prev);
    }

    @Override
    protected void drawBlended(Tile tile, boolean checkId){
        Color prev = Draw.getColor();
        if(!isAlive(tile))
            Draw.color(noHeartColour);
        super.drawBlended(tile, checkId);
        Draw.color(prev);
    }

    public boolean isAlive(Tile tile){
        return aliveFloors.get(tile.array());
    }

    @Override
    public void lazyEnv(Tile tile){
        Building heart = Units.closestBuilding(nyfRule.calyxTeam, tile.x * tilesize , tile.y * tilesize, connectionRange, b -> !b.dead && b instanceof Calyxian c && c.isAlive());
        aliveFloors.set(tile.array(), heart != null);
    }
}
