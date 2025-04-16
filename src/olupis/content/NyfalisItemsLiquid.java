package olupis.content;

import arc.graphics.*;
import arc.struct.*;
import mindustry.content.*;
import mindustry.type.*;

import static mindustry.content.Items.*;

public class NyfalisItemsLiquid {

    public static Item condensedBiomatter, rustyIron, iron, cobalt, quartz, alcoAlloy, aluminum, cryoRods, steel, silicatePowder, powerAmmoItem;
    public static final Seq<Item> nyfalisOnlyItems = new Seq<>(), nyfalisItems = new Seq<>();
    public static Liquid heavyOil, lightOil, steam, lubricant, emulsiveSlop;

    public static  void LoadItems(){
        //region Items
        /*Texture is from Tech reborn: https://github.com/TechReborn/TechReborn/blob/1.19/src/main/resources/assets/techreborn/textures/item/part/compressed_plantball.png*/
        condensedBiomatter = new Item("condensed-biomatter", Color.valueOf("5a9e70")) {{
            buildable = false;

            flammability = 1.2f;
        }};
        rustyIron = new Item("rusty-iron", Color.valueOf("471B12")) {{
            hardness = 1;
        }};
        iron = new Item("iron", Color.valueOf("989AA4")) {{
            hardness = 2;
            healthScaling = 0.25f;
        }};
        cobalt = new Item("cobalt", Color.valueOf("5D5D74")) {{
            hardness = 2;
            charge = 0.5f;
            healthScaling = 0.25f;
        }};
        quartz = new Item("quartz", Color.valueOf("BAC1C4")){{
            hardness = 2;
        }};

        aluminum = new Item("aluminum", Color.valueOf("6D6D6D")){{
            hardness = 2;
        }};

        alcoAlloy = new Item("alco-alloy", Color.valueOf("546295")){{
            hardness = 2;
        }};

        powerAmmoItem = new Item("power-ammo-item", Color.valueOf("f3e979")){{
            //I only exits so constructs can have a lightning bolt as icon when making spirits
            hidden = true;
            charge = 1f;
        }};


        nyfalisOnlyItems.addAll(rustyIron,iron,condensedBiomatter,cobalt, quartz, alcoAlloy, aluminum);
        nyfalisItems.add(nyfalisOnlyItems);
        nyfalisItems.addAll(copper, lead, silicon, graphite, sand, scrap);
    }

    public static void LoadLiquids(){
        heavyOil = lightOil  = Liquids.oil;

        steam = new Liquid("steam", Color.valueOf("E0DAE9")){{
            /*hacky way so it acts like a liquid in NoBoilLiquidBulletType  but a gas outside of thar*/
            gas = coolant = false;

            heatCapacity = 0.35f;
            boilPoint = 0f;
            effect = StatusEffects.corroded;
        }};

        lubricant = new Liquid("lubricant", Color.valueOf("C4AA90")){{
            viscosity = 0.7f;
            flammability = 1.2f;
            explosiveness = 1.3f;
            heatCapacity = 0.86f;
            barColor = Color.valueOf("C4AA90");
            effect = NyfalisStatusEffects.lubed;
            boilPoint = 0.65f;
            gasColor = Color.grays(0.4f);
            canStayOn.addAll(Liquids.water, Liquids.oil);
        }};

        emulsiveSlop = new Liquid("emulsive-slop", Color.valueOf("39436D")){{
            viscosity = 0.33f;
            flammability = 0.6f;
            explosiveness = 0.7f;
            heatCapacity = 0.63f;
            barColor = Color.valueOf("6b675f");
            effect = NyfalisStatusEffects.lubed;
            boilPoint = 0.65f;
            gasColor = Color.grays(0.4f);
            canStayOn.addAll(Liquids.water, Liquids.oil);
        }};

        Liquids.water.canStayOn.add(lubricant);
        Liquids.oil.canStayOn.add(lubricant);
        Liquids.water.canStayOn.add(emulsiveSlop);
        Liquids.oil.canStayOn.add(emulsiveSlop);
    }
}
