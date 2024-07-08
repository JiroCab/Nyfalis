package olupis.content;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.StatusEffects;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.Weather;
import mindustry.type.weather.ParticleWeather;
import mindustry.type.weather.RainWeather;
import mindustry.world.meta.Attribute;

import static mindustry.content.Blocks.*;
import static olupis.content.NyfalisBlocks.*;

public class NyfalisAttributeWeather {
    public static final int nyfalian = 1 << 910; //this is a random number from rushie keyboard smashing

    /*Used by the biomatter compressor */
    public static final Attribute bio = Attribute.add("bio");
    /*Used by hydroMill yield*/
    public static final Attribute hydro = Attribute.add("hydro");
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
            color = Color.valueOf("50766A");

            yspeed = 6f;
            sizeMin = 10f;
            density = 1000f;
            soundVol = 0.3f;
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

        cloudShadow = new ParticleWeather("cloud-shadow"){{
                duration = 15f * Time.toMinutes;
                noiseLayers = 3;
                noiseLayerAlphaM = 0.4f;
                noiseLayerSpeedM = 2f;
                noiseLayerSclM = 0.6f;
                baseSpeed = 0.035f;
                color = noiseColor = Color.grays(0.1f);
                noiseScale = 1100f;
                noisePath = "clouds";
                drawParticles = false;
                drawNoise = true;
                useWindVector = false;
                xspeed = 1f;
                yspeed = 0.01f;
                opacityMultiplier = 0.47f;
                maxAlpha = 0.8f;
            }
            @Override
            public void drawOver(WeatherState state){
                if(!Core.settings.getBool("nyfalis-cloud-shadows"))return;
                float windx, windy;
                if(useWindVector){
                    float speed = baseSpeed * state.intensity;
                    windx = state.windVector.x * speed;
                    windy = state.windVector.y * speed;
                }else{
                    windx = this.xspeed;
                    windy = this.yspeed;
                }

                if(drawNoise){
                    if(noise == null){
                        noise = Core.assets.get("sprites/" + noisePath + ".png", Texture.class);
                        noise.setWrap(Texture.TextureWrap.repeat);
                        noise.setFilter(Texture.TextureFilter.linear);
                    }

                    float sspeed = 1f, sscl = 1f, salpha = 1f, offset = 0f;
                    Color col = Tmp.c1.set(noiseColor);
                    for(int i = 0; i < noiseLayers; i++){
                        drawNoise(noise, noiseColor, noiseScale * sscl, state.opacity * salpha * opacityMultiplier, sspeed * (useWindVector ? 1f : baseSpeed), state.intensity, windx, windy, offset);
                        sspeed *= noiseLayerSpeedM;
                        salpha *= noiseLayerAlphaM;
                        sscl *= noiseLayerSclM;
                        offset += 0.29f;
                        col.mul(noiseLayerColorM);
                    }
                }

                if(drawParticles){drawParticles(region, color, sizeMin, sizeMax, density, state.intensity, state.opacity, windx, windy, minAlpha, maxAlpha, sinSclMin, sinSclMax, sinMagMin, sinMagMax, randomParticleRotation);}
            }
        };
    }

    public static class AcidRainWeather extends RainWeather{
        public float damageDelay = 1.5f * Time.toMinutes, damageBlock = 1f, damageUnits = 5f,
                          regrowPercent = 0.1f;
        boolean coolDown = false, coolDownRegrow = false;

        public AcidRainWeather(String name){
            super(name);
        }

        @Override
        public void update(WeatherState state){
            if(coolDown) return;
            coolDown = true;
            Time.run(damageDelay, () ->{
                if(damageBlock > 0)Groups.build.each(b ->{
                    if(b.team == Team.derelict) return;
                    b.damage(damageBlock);
                });
                /*Using corroded is too much & annoying, use a custom effect if we made one instead of this*/
                if(damageUnits > 0)Groups.unit.each(u -> u.damage(damageUnits));
//                if(!coolDownRegrow){
//                    int range = Math.round((Vars.world.height() + Vars.world.height()) * regrowPercent);
//                    Log.err("nya");
//                    for(int r = range ; r > 0 ; r-- ){
//                        int x = (int) Mathf.range(0, Vars.world.width()), y = (int) Mathf.range(0, Vars.world.height());
//
//                        Tile t = Vars.world.tiles.get(x, y);
//                        if(t.block() == air && rainRegrowables.contains(t.floor())){
//                            t.setNet(t.floor().decoration);
//                        }
//                    }
//                }
//                coolDownRegrow = !coolDownRegrow;
                coolDown = false;
            });
        }
    }

    public static class  DamgingParticleWeather extends ParticleWeather{
        public float damageDelay = 1.5f * Time.toMinutes, damageBlock = 1f, damageUnits = 5f;
        boolean coolDown = false;

        public DamgingParticleWeather(String name){
            super(name);
        }


        @Override
        public void update(WeatherState state){
            super.update(state);

            if(coolDown) return;
            coolDown = true;
            Time.run(damageDelay, () ->{
                if(damageBlock > 0)Groups.build.each(b ->{
                    if(b.team == Team.derelict) return;
                    b.damage(damageBlock);
                });
                /*Using corroded is too much & annoying, use a custom effect if we made one instead of this*/
                if(damageUnits > 0)Groups.unit.each(u -> u.damage(damageUnits));
                coolDown = false;
            });
        }
    }

}
