package olupis.world.ai;

import arc.struct.*;
import mindustry.ai.*;
import mindustry.ai.Pathfinder.*;
import mindustry.gen.*;

public class NyfalisPathfind {
    static final int impassable = -1;
    private static final int wallImpassableCap = 1_000_000;

    public static final Pathfinder.PathCost
        costLeggedNaval =(team, tile) ->
            PathTile.legSolid(tile) ? impassable : 1 +
                PathTile.health(tile) * 5 +
                (PathTile.nearLegSolid(tile) ? 3 : 0),

        costPreferNaval =(team, tile) ->
            //impassable same-team neutral block
            (PathTile.solid(tile) && ((PathTile.team(tile) == team && !PathTile.teamPassable(tile)) || PathTile.team(tile) == 0)) ? impassable :  1 +
            //impassable synthetic enemy block
            ((PathTile.team(tile) != team && PathTile.team(tile) != 0) && PathTile.solid(tile) ? wallImpassableCap : 0) +
                PathTile.health(tile) * 5 +
                (!PathTile.liquid(tile) ? 2 : 0) +
                (!PathTile.nearLiquid(tile) ? 1 : 0) +
                (PathTile.nearGround(tile) || PathTile.nearSolid(tile) ? 6 : 0),

        //Sameish as legged but prefers open blocks & cant walk on terrain
        costPreferTrackedNaval =(team, tile) ->
            //not legged units
            (PathTile.solid(tile) && !PathTile.teamPassable(team)) ? impassable :
            (PathTile.legSolid(tile)) ? impassable :

            (PathTile.solid(tile) && PathTile.team(tile) != team? 0 : 3)+
                PathTile.health(tile) * 5 +
                (!PathTile.solid(tile) ? 3 : 0) +
                (!PathTile.nearSolid(tile) ? 2 : 0) +
                (!PathTile.liquid(tile) ? 2 : 0) +
                (!PathTile.nearLiquid(tile) ? 1 : 0) +
                (PathTile.nearLegSolid(tile) ? 3 : 0),

        costPreferLeggedNaval =(team, tile) ->
            PathTile.legSolid(tile) ? wallImpassableCap : 1 +
                PathTile.health(tile) * 5 +
                (!PathTile.liquid(tile) ? 2 : 0) +
                (!PathTile.nearLiquid(tile) ? 1 : 0) +
                (PathTile.nearLegSolid(tile) ? 3 : 0)
    ;

    public static final Seq<PathCost> nyfCostTypes = Seq.with(
            costLeggedNaval, costPreferLeggedNaval, costPreferNaval, costPreferTrackedNaval
    );




}
