package olupis.content;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.type.weather.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import olupis.world.*;
import olupis.world.blocks.environment.*;

import javax.swing.GroupLayout.*;
import java.util.*;

import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;
import static olupis.content.NyfalisBlocks.*;

public class NyfalisAttributeWeather {
    public static final int nyfalian = 1 << 910; //this is a random number from rushie keyboard smashing


    public static final Attribute
        /*Used by the biomatter compressor */
        bio = Attribute.add("bio"),
        /*Used by hydroMill yield*/
        hydro = Attribute.add("hydro"),
        /*Water extra output during rain in flood pains*/
        flood = Attribute.add("flood")
    ;
    public static Weather acidRain, mossMist, cloudShadow;

    public static void AddAttributes(){
        ice.attributes.set(bio, 0.01f);
        grass.attributes.set(bio, 0.1f);
        dirt.attributes.set(bio, 0.03f);
        mud.attributes.set(bio, 0.03f);
        stone.attributes.set(bio, 0.03f);
        charr.attributes.set(bio, 0.03f);
        snow.attributes.set(bio, 0.01f);
        craters.attributes.set(bio, 0.5f);

        water.attributes.set(hydro, 0.3f);
        deepwater.attributes.set(hydro, 0.5f);
        sandWater.attributes.set(hydro, 0.3f);
        taintedWater.attributes.set(hydro, 0.3f);
        darksandWater.attributes.set(hydro, 0.3f);
        deepTaintedWater.attributes.set(hydro, 0.3f);
        darksandTaintedWater.attributes.set(hydro, 0.3f);

        algaeWater.attributes.set(hydro, 0.3f);
        redSandWater.attributes.set(hydro, 0.3f);
        pinkGrassWater.attributes.set(hydro, 0.3f);
        lumaGrassWater.attributes.set(hydro, 0.3f);
        yellowMossyWater.attributes.set(hydro, 0.3f);
        algaeWaterDeep.attributes.set(hydro, 0.5f);
        coralReef.attributes.set(hydro, 0.5f);
        slop.attributes.set(hydro, 0.08f);

        mossyStone.asFloor().decoration = bush;
        mossyStone.asFloor().decoration = boulder;
        cinderBloomy.asFloor().decoration = basaltBoulder;
    }

    public static void loadWeather(){

        acidRain = new AcidRainWeather("acidrain"){{
            attrs.set(bio, +0.1f);
            attrs.set(Attribute.light, -0.3f);
            attrs.set(Attribute.water, 0.3f);

            sound = Sounds.rain;
            status = StatusEffects.wet;
            color = NyfalisColors.acidRainColour;

            yspeed = 10f;
            sizeMin = 10f;
            density = 1500f;
            soundVol = 0.2f;
            splashTimeScale = 25f;
        }};

        mossMist = new DamgingParticleWeather("mossmist"){{
            attrs.set(bio, +0.2f);
            attrs.set(Attribute.light, -0.35f);

            noisePath = "clouds";
            status = StatusEffects.none;
            color = noiseColor =  Color.valueOf("50766A");

            drawNoise = true;
            useWindVector = false;

            soundVol = 0.2f;
            damageUnits = 0f;
            damageBlock = 2.5f;
            damageDelay = 2f * Time.toMinutes;

            noiseLayers = 3;
            noiseLayerSclM = 0.6f;
            noiseLayerSpeedM = 2f;
            noiseLayerAlphaM = 0.7f;
            opacityMultiplier = 0.35f;

            sizeMax = 4f;
            sizeMin = 1.4f;
            minAlpha = 0.5f;
            maxAlpha = 1f;
            density = 10000f;
            baseSpeed = 0.03f;
        }};

//        //Just left here to be removed
//        cloudShadow = new ParticleWeather("cloud-shadow"){
//            @Override
//            public void updateEffect(WeatherState state){
//                super.updateEffect(state);
//                for(WeatherEntry w : Vars.state.rules.weather) if(w.weather.type == cloudShadow) Vars.state.rules.weather.remove(w);
//                for(WeatherState w : Groups.weather) if(w.weather.type == cloudShadow) Groups.weather.remove(w);
//            }
//        };
    }

    public static class AcidRainWeather extends RainWeather{
        public float damageDelay = 1.5f * Time.toMinutes, regrowDelay = 3f * Time.toMinutes, damageBlock = 1f, damageUnits = 5f,
                            regrowPercent = 0.005f;
        Interval delayer = new Interval(2);

        public AcidRainWeather(String name){
            super(name);
        }

        @Override
        public void update(WeatherState state){
            if(delayer.get(regrowDelay))
                regrow();
            if(delayer.get(1, damageDelay))
                applyDamage();
        }

        public void regrow(){
            boolean grow;

            for(int i = 0; i < world.width() * world.height(); i++){
                grow = Mathf.randomBoolean(regrowPercent);

                if(grow) NyfWorldFuckingHelper.growSprigs(world.tiles.geti(i));
            }
        }

        public void applyDamage(){
            if(damageBlock > 0){
                Groups.build.each(b -> b.team != Team.derelict, b -> {
                    b.damage(damageBlock);
                    NyfalisFxs.hitAcidRain.at(b.x, b.y, 0, NyfalisColors.acidRainColour, b.block);
                });
            }

            /*Using corroded is too much & annoying, use a custom effect if we made one instead of this*/
            if(damageUnits > 0)
                Groups.unit.each(u ->{
                    u.damage(damageUnits);
                    NyfalisFxs.hitAcidRain.at(u.x, u.y, u.rotation, NyfalisColors.acidRainColour, u.type);
                });
        }
    }

    public static class DamgingParticleWeather extends ParticleWeather{
        public float damageDelay = 1.5f * Time.toMinutes, damageBlock = 1f, damageUnits = 5f;
        Interval delayer = new Interval();

        public DamgingParticleWeather(String name){
            super(name);
        }

        @Override
        public void update(WeatherState state){
            super.update(state);

            if(delayer.get(damageDelay)){
                if(damageBlock > 0)
                    Groups.build.each(b -> b.team == Team.derelict, b -> b.damage(damageBlock));

                /*Using corroded is too much & annoying, use a custom effect if we made one instead of this*/
                if(damageUnits > 0)
                    Groups.unit.each(u -> u.damage(damageUnits));
            }
        }
    }

}
