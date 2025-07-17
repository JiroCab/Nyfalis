package olupis.world.entities.status;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;

public class TrackedSatusEffect extends StatusEffect{
    public final ObjectMap<Unit, ObjectFloatMap<Unit>> valueMap = new ObjectMap<>();
    public int immobilizedCount = 6;

    public TrackedSatusEffect(String name){
        super(name);

        Events.on(EventType.ResetEvent.class, e -> valueMap.clear());
    }

    @Override
    public void update(Unit unit, float time){
        super.update(unit, time);

        if(valueMap.get(unit, ObjectFloatMap::new).size > 0){
            ObjectFloatMap<Unit> ref = valueMap.get(unit);

            var it = ref.iterator();
            while(it.hasNext()){
                var entry = it.next();
                if(Time.time >= entry.value)
                    it.remove();
            }

            if(ref.size >= immobilizedCount)
                unit.apply(StatusEffects.unmoving, 6f);
            else unit.speedMultiplier(Mathf.clamp(speedMultiplier / ref.size));
        }
    }

    @Override
    public void onRemoved(Unit unit){
        super.onRemoved(unit);

        valueMap.remove(unit);
    }
}
