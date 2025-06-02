package olupis.world.planets;

import mindustry.game.*;
import mindustry.type.*;

public class NyfCampaignRules extends CampaignRules{
    public boolean fogStatic = false;

    @Override
    public void apply(Planet planet, Rules rules){
        super.apply(planet, rules);

        rules.fog = fog;
        rules.staticFog = fogStatic;
        if(fogStatic && !fog) rules.fog = true;

    }
}
