package olupis.world.blocks.calyx.ineternal;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.tilesize;
import static olupis.NyfalisVars.nyfRule;

public class GrownFloor extends Floor{
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

    public boolean isAlive(Tile tile){
        return heart(tile ) != null;
    }

    public @Nullable Building heart(Tile tile){
        if(nyfRule.calyxTeam == null) {
            return null;
        }

        return Groups.build.find( b -> b != null && !b.dead &&b.within(tile.x * tilesize, tile.y * tilesize, connectionRange) && b instanceof  Calyxian c && c.getHeart() != null && !c.getHeart().dead);

    }
}
