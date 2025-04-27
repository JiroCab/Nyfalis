package olupis.world.planets;

import arc.struct.*;
import mindustry.content.*;
import mindustry.game.*;

import static mindustry.content.Items.*;
import static mindustry.content.TechTree.*;
import static olupis.content.NyfalisBlocks.*;
import static olupis.content.NyfalisItemsLiquid.*;
import static olupis.content.NyfalisPlanets.*;
import static olupis.content.NyfalisSectors.*;
import static olupis.content.NyfalisUnits.*;

public class NyfalisTechTree {

    public static void load(){
        nyfalis.techTree = nodeRoot("olupis", coreRemnant, () -> {
            node(system, () -> {
                node(arthin, () ->{
                    node(sanctuary, () -> {
                        node(terrarootCaves,
                                Seq.with(new Objectives.SectorComplete(sanctuary)),
                        () -> {
                            node(dyingForest, Seq.with(new Objectives.SectorComplete(terrarootCaves)), () -> {
                                node(coldFlats, Seq.with(new Objectives.SectorComplete(conciditRuins)),  () -> {
                                    node(glasierSea,Seq.with(new Objectives.SectorComplete(coldFlats), new Objectives.SectorComplete(dyingForest)), () -> {

                                    });
                                });
                                node(muddyLakes, Seq.with(new Objectives.SectorComplete(dyingForest)), () -> {
                                    node(abandonedPayloadTerminal, Seq.with(new Objectives.SectorComplete(muddyLakes)), () -> {
                                        node(ironCurtain, Seq.with(
                                                new Objectives.SectorComplete(abandonedPayloadTerminal),
                                                new Objectives.SectorComplete(glasierSea),
                                                new Objectives.SectorComplete(coldFlats)
                                        ), () -> {

                                        });
                                    });
                                });
                                node(conciditRuins, Seq.with(new Objectives.Research(groundConstruct), new Objectives.SectorComplete(dyingForest)), () -> {

                                });
                            });
                        });
                    });
                });
                node(nyfalis, Seq.with(
                        new Objectives.SectorComplete(ironCurtain),
                        new Objectives.SectorComplete(glasierSea),
                        new Objectives.SectorComplete(coldFlats)
                ),() ->{
                    node(conservatorium, Seq.with( //Just incase the planet node was somehow unlocked prematurely
                            new Objectives.SectorComplete(sanctuary),
                            new Objectives.SectorComplete(ironCurtain),
                            new Objectives.SectorComplete(glasierSea),
                            new Objectives.SectorComplete(coldFlats)
                    ), () ->{
                        node(forestOfSerenity,Seq.with(new Objectives.SectorComplete(conservatorium)), () -> {

                        });
                    });
                });
                node(spelta, Seq.with(
                        new Objectives.SectorComplete(sanctuary), new  Objectives.Research(groundConstruct)
                ), () ->{
                    node(forestOfHope, Seq.with(
                            new Objectives.SectorComplete(conservatorium),
                            new Objectives.Research(groundConstruct)
                    ), () ->{
                        node(dormantCell,  ()-> {

                        });
                    });

                });
            });

            node(gnat, ()->{
                node(spirit, Seq.with(
                new  Objectives.Research(construct)
                ), () ->{
                    node(banshee,  Seq.with(
                    new  Objectives.Produce(quartz)
                    ), () ->{
                        node(phantom, Seq.with(
                        new  Objectives.Produce(graphite)
                        ), () -> {
                            node(revenant, Seq.with(
                            new  Objectives.Produce(silicon)
                            ), () -> {

                            });
                        });
                    });
                });

                node(aero, Seq.with(
                    new  Objectives.Research(arialConstruct)
                ), () -> {
                    node(striker, Seq.with(
                            new  Objectives.Research(alternateArticulator)
                    ), () -> {
                        node(falcon, Seq.with(
                        new  Objectives.Research(adaptiveFabricator)
                        ), () -> {

                        });
                    });
                });
                node(pteropus, Seq.with(
                    new  Objectives.Research(arialConstruct)
                ), () -> {
                    node(acerodon, Seq.with(
                            new  Objectives.Research(alternateArticulator)
                    ), () -> {
                        node(nyctalus, Seq.with(
                        new  Objectives.Research(adaptiveFabricator)
                        ), () -> {

                        });
                    });
                });

                node(venom, Seq.with(
                        new  Objectives.Research(groundConstruct)
                ), () -> {
                    node(serpent, Seq.with(
                            new  Objectives.Research(alternateArticulator)
                    ), () -> {
                        node(reaper, Seq.with(
                        new  Objectives.Research(adaptiveFabricator)
                        ), () -> {

                        });
                    });
                });
                node(supella, Seq.with(
                        new  Objectives.Research(groundConstruct)
                ), () -> {
                    node(germanica, Seq.with(
                            new  Objectives.Research(alternateArticulator)
                    ), () -> {
                        node(luridiblatta, Seq.with(
                        new  Objectives.Research(adaptiveFabricator)
                        ), () -> {

                        });
                    });
                });
                node(sentry, Seq.with(
                        new  Objectives.Research(navalConstruct)
                ), () -> {
                    node(zoner, () -> {

                    });
                    node(warden, Seq.with(
                            new  Objectives.Research(alternateArticulator)
                    ), () -> {
                        node(regioner, () -> {

                        });
                        node(guardian, Seq.with(
                        new  Objectives.Research(adaptiveFabricator)
                        ), () -> {
                            node(district, () -> {

                            });
                        });
                    });
                });
                node(bay, Seq.with(
                    new  Objectives.Research(navalConstruct)
                ), () -> {
                    node(blitz, Seq.with(
                            new  Objectives.Research(alternateArticulator)
                    ), () -> {
                        node(crusader, Seq.with(
                        new  Objectives.Research(adaptiveFabricator)
                        ), () -> {

                        });
                    });
                });
                node(pedicia, Seq.with(new Objectives.Research(coreVestige)), () ->{
                    node(phorid, Seq.with(
                            new Objectives.SectorComplete(sanctuary), new  Objectives.Research(coreRelic)
                    ), () -> {
                        node(diptera, () -> {

                        });
                        node(embryo, () -> {

                        });
                    });
                }) ;
                node(scarab, Seq.with(
                        new  Objectives.Research(scoutPad)
                ), () -> {

                });
            });

            node(mossyBoulder, ()-> {

                node(coreVestige, () ->{
                    node(coreRelic, () -> {
                        node(coreShrine, () -> {
                            node(coreTemple, () -> {

                            });
                        });
                    });
                });

                node(wire, Seq.with(new Objectives.Research(rustyDrill)), () -> {
                    node(windMills, () -> {
                        node(rustElectrolyzer, Seq.with(new Objectives.Research(corroder), new Objectives.SectorComplete(sanctuary)), () -> {
                            node(garden, Seq.with(new Objectives.SectorComplete(dyingForest)),()->{
                                node(biomassGenerator, () ->{

                                });
                            });
                            node(siliconKiln, ()->{
                                node(hydrochloricGraphitePress, Seq.with(new Objectives.OnSector(dyingForest)), ()->{
                                    node(siliconArcSmelter , Seq.with(
                                            new  Objectives.Research(hydrochloricGraphitePress)
                                    ), ()->{

                                    });
                                });
                            });
                            node(inductionSmelter, Seq.with(new Objectives.Research(alcoAlloy)), () -> {
                                node(rustEngraver, () ->{

                                });
                            });
                            node(discardDriver, Seq.with(new Objectives.SectorComplete(glasierSea)),() ->{

                            });
                        });
                        node(hydroMill, ()->{
                            node(hydroElectricGenerator, () ->{

                            });
                        });
                        node(wireBridge, ()-> {
                            node(superConductors, ()->{

                            });
                        });
                        node(taurus ,Seq.with(new Objectives.Research(sanctuary)), ()->{
                            node(ladar, Seq.with(
                                    new  Objectives.Research(hydrochloricGraphitePress)
                            ), () -> {

                            });
                        });
                    });
                });

                node(leadPipe, Seq.with(new Objectives.Research(windMills)), () -> {
                    node(rustyPump, () ->{
                        node(pipeRouter, ()->{
                            node(fortifiedCanister, ()->{
                                node(fortifiedTank,
                                    Seq.with(new Objectives.OnSector(muddyLakes))
                                 , ()->{

                                });
                            });
                            node(pipeJunction, ()->{
                                node(pipeBridge, ()-> {

                                });
                            });
                            node(ironPipe,
                                Seq.with(new Objectives.OnSector(muddyLakes)),
                            ()->{

                            });
                            node(steamBoiler,
                                    Seq.with(new Objectives.OnSector(terrarootCaves)),
                            ()->{
                                node(steamAgitator, Seq.with(new Objectives.Research(steam)),()->{
                                    node(fortifiedRadiator, () ->{

                                    });
                                });
                                node(demulsifier, Seq.with(new Objectives.OnSector(dyingForest), new Objectives.Produce(emulsiveSlop)),()->{

                                });
                                node(broiler, Seq.with(new Objectives.Research(graphite), new Objectives.Research(siliconArcSmelter)),()->{

                                });

                            });
                            node(ironPump,
                                Seq.with(new Objectives.OnSector(muddyLakes)),
                            () -> {
                                node(displacementPump, Seq.with(
                                        new  Objectives.Research(hydrochloricGraphitePress)
                                ), () -> {
                                    node(massDisplacementPump, () -> {

                                    });
                                });
                            });
                        });
                    });
                });

                node(rustyIronConveyor, () ->{
                    node(ironRouter, () ->{
                        node(ironJunction, ()->{
                            node(rustedBridge, () -> {

                            });
                        });
                        node(ironConveyor, ()->{
                            node(cobaltConveyor, ()->{

                            });

                            node(ironOverflow, () ->{
                                node(ironUnderflow, () -> {
                                    node(ironDistributor, () -> {

                                    });
                                });
                            });

                            node(ironBridge, ()->{

                            });
                        });
                    });

                    node(rustyDrill, ()->{
                        node(steamDrill, Seq.with(new Objectives.Produce(steam)), () ->{
                            node(hydroElectricDrill, () ->{

                            });
                        });
                        node(ironSieve, Seq.with(new Objectives.OnSector(dyingForest), new Objectives.Produce(sand)),() ->{

                        });
                    });
                });

                node(ironUnloader, Seq.with(
                        new  Objectives.Research(hydrochloricGraphitePress)
                ), () ->{
                    node(fortifiedContainer, () ->{
                        node(fortifiedVault, () ->{
                            node(deliveryCannon, () ->{
                                node(deliveryTerminal, () ->{

                                });
                            });
                        });
                    });
                });

                node(corroder, Seq.with(new Objectives.Research(rustyPump)), ()-> {
                    node(avenger, () -> {
                        node(strata, ()->{
                            node(mossMine, ()->{
                                node(heavyMine, ()->{
                                    node(fragMine, ()->{
                                        node(glitchMine, ()->{

                                        });
                                    });
                                });
                            });
                        });
                        node(aegis, ()->{
                            node(cascade, ()->{

                            });
                        });
                    });
                    node(slash, ()->{
                        node(shredder, ()->{
                            node(porcupine, ()->{
                                node(hive, ()->{

                                });
                            });
                            node(laceration, () -> {

                            });
                        });
                    });
                    node(rustyWall, () ->{
                        node(rustyWallLarge, ()->{
                            node(rustyWallHuge, ()->{

                            });
                        });
                        node(ironWall, ()->{
                            node(ironWallLarge, ()->{
                                node(quartzWall, () -> {
                                    node(quartzWallLarge, () -> {
                                        node(cobaltWall, () -> {
                                            node(cobaltWallLarge, () -> {

                                            });
                                        });
                                    });
                                });
                            });
                        });
                    });
                });

                node(unitReplicatorSmall, Seq.with(new Objectives.OnSector(terrarootCaves)),()->{
                    node(construct, Seq.with(new Objectives.Research(ironRouter), new Objectives.OnSector(dyingForest)), ()->{
                        node(groundConstruct,
                            Seq.with(new Objectives.Research(iron))
                        , () ->{
                            node(arialConstruct, Seq.with(new Objectives.SectorComplete(dyingForest)), () ->{
                                node(navalConstruct, () ->{

                                });
                            });
                        });
                        node(fortifiedPayloadConveyor, () -> {
                            node(fortifiedPayloadConveyor, () -> {

                            });
                        });
                        node(repairPin, () -> {
                            node(scoutPad,  Seq.with(
                                    new  Objectives.OnSector(glasierSea)
                            ), () -> {

                            });
                            node(alternateArticulator,  Seq.with(
                                    new  Objectives.SectorComplete(abandonedPayloadTerminal)
                            ), () -> {
                                node(adaptiveFabricator,  Seq.with(
                                new  Objectives.SectorComplete(conservatorium)
                                ), () -> {

                                });
                            });
                        });

                        node(unitReplicator,  Seq.with(new Objectives.OnSector(dyingForest)),()->{

                        });
                    });
                });

                node(fortifiedMessageBlock, Seq.with(new Objectives.Research(ironRouter)), ()->{
                    node(mechanicalProcessor, () -> {
                        node(mechanicalSwitch, () -> {

                        });
                        node(mechanicalRegistry, ()->{

                        });
                    });
                });


                nodeProduce(rustyIron, () ->{
                    nodeProduce(copper, () ->{

                    });
                    nodeProduce(lead, () ->{
                        nodeProduce(sand, () ->{
                            nodeProduce(quartz, () ->{

                            });
                        });
                    });
                    nodeProduce(iron, () ->{
                        nodeProduce(alcoAlloy, ()->{
                            nodeProduce(aluminum, () -> {

                            });
                            nodeProduce(cobalt, ()->{

                            });
                            nodeProduce(Liquids.slag, () ->{

                            });
                        });
                        nodeProduce(condensedBiomatter, () ->{

                        });
                    });
                    nodeProduce(Liquids.water, ()->{
                        nodeProduce(steam, () ->{

                        });
                        nodeProduce(emulsiveSlop, () ->{
                            nodeProduce(Liquids.oil,()->{
                                nodeProduce(graphite, ()->{

                                });
                                nodeProduce(silicon, ()->{

                                });
                            });
                        });
                    });
                });

            });

        });
        arthin.techTree = nyfalis.techTree;
        spelta.techTree = nyfalis.techTree;
        system.techTree = nyfalis.techTree;

    }
}
