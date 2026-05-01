package olupis;

import arc.struct.*;
import mindustry.*;
import mindustry.game.*;
import olupis.content.*;

public class NyfRules{
    /*whether  calyx is allowed to spread at all*/
    public  boolean calyxSpreading = true;
    /*How much Calyx tiles want to spread overall*/
    public  float calyxSpreadingFactor = 1;
    /*How much Calyx tiles on "spear tiles" gets a bonus */
    public  float calyxSpearFactor = 1;
    /*How much Calyx tiles would grow on to it's next stage*/
    public  float calyxGrowthFactor = 1;
    /*How much Calyx will sprout "buildings" on it*/
    public  float calyxSproutFactor = 1;
    /*which team the Calyx "building" team is on*/
    public  Team calyxTeam = NyfUnitTeamMapper.calyxTeam;
    /*Determines how many tiles deep spear tiles will go for peformance reasons*/
    public int calyxSpearDepth = 50;


    public  boolean damagingWeather = true;


    public void load (StringMap tags){
        calyxSpreading = !tags.containsKey("nyf-calyxspreading") || tags.getBool("nyf-calyxspreading");
        calyxSproutFactor = tags.getFloat("nyf-calyxsproutfactor", 1);
        calyxGrowthFactor = tags.getFloat("nyf-calyxgrowthfactor", 1);
        calyxSpreadingFactor = tags.getFloat("nyf-calyxspreadingfactor", 1);
        calyxTeam = tags.containsKey("nyf-calyxteam") ? Team.get(tags.getInt("nyf-calyxteam")) : NyfUnitTeamMapper.calyxTeam;
        calyxSpearFactor = tags.getFloat("nyf-calyxspearfactor", 1);
        calyxSpearDepth = tags.getInt("nyf-calyxspeardepth", 50);
    }
}
