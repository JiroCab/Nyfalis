package olupis.world.blocks.calyx.ineternal;

import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.world.*;

public interface Calyxian{
    Seq<Calyxian> tempBuilds = new Seq<>();

    default @Nullable CalyxModule module(){
        return null;
    }

    default Seq<Calyxian> getCalyxConnections(Seq<Calyxian> out){
        out.clear();
        if(Vars.state.isEditor()) return out;// dont
        if(this.module() == null){
            Log.err("C Module is null!");
            return null;
        }
            if( this.build() == null){
                Log.err("C build is null!");
                return out;
            }

        for(Building bul : this.build().proximity){
            if(!(bul instanceof Calyxian other)) continue;

            if(build().team == bul.team && !module().links.contains(bul.pos())){
                out.add(other);
            }

            for(int i = 0; i < module().links.size; i++){
                Tile link = Vars.world.tile(this.module().links.get(i));
                if(link != null && link.build != null && link.team() == build().team && link instanceof Calyxian c){
                    out.add(c);
                }
            }
        }
        return out;
    }

    default @Nullable Building build(){
        return null;
    }

    @Nullable default Building getHeart(){
        return null;
    }


    default void updateCalyxianModule(){
        tempBuilds.clear();
        for(Calyxian c : getCalyxConnections(tempBuilds)){
            if(c.module() != null){
                c.module().graph.addGraph(module().graph);
            }
        }
    }

    default void removedCalyxianModule(){
        if(module() != null){
            module().graph.remove(build());

            for(int i = 0; i < module().links.size; i++){
                Tile other = Vars.world.tile(module().links.get(i));
                if(other != null && other.build instanceof Calyxian c){
                    c.module().links.removeValue(build().pos());
                }

            }

            module().links.clear();
        }
    }

    //todo handle payload pick up & change team()
}
