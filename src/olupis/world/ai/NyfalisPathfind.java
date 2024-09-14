package olupis.world.ai;

import mindustry.ai.Pathfinder;
import mindustry.gen.PathTile;

public class NyfalisPathfind {
    static final int impassable = -1;
    private static final int wallImpassableCap = 1_000_000;

    public static final Pathfinder.PathCost
        costLeggedNaval =(team, tile) ->
            PathTile.legSolid(tile) ? impassable : 1 +
            (PathTile.nearLegSolid(tile) ? 3 : 0),

        costPreferNaval =(team, tile) ->
            //impassable same-team neutral block
            (PathTile.solid(tile) && ((PathTile.team(tile) == team && !PathTile.teamPassable(tile)) || PathTile.team(tile) == 0)) ? impassable :
            //impassable synthetic enemy block
            1 +
            ((PathTile.team(tile) != team && PathTile.team(tile) != 0) && PathTile.solid(tile) ? wallImpassableCap : 0) +
            (!PathTile.liquid(tile) ? 2 : 0) +
            (!PathTile.nearLiquid(tile) ? 1 : 0) +
            (PathTile.nearGround(tile) || PathTile.nearSolid(tile) ? 6 : 0);


}
