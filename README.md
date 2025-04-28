# Nyfalis (Olupis)
A [Mindustry](https://github.com/Anuken/Mindustry) Java mod that aims to add a new planet that offers a bit of new experience
With slight lean on RTS Elements and providing pain from the base game has to offer

# Misc Info
### [Consider getting the music companion mod here!](https://github.com/JiroCab/Nyfalis-Music)

[Releases](https://github.com/JiroCab/Olupis/releases) for all release versions automatically created from commits to `main`. 
otherwise use [GitHub Actions](https://github.com/JiroCab/Olupis/actions) for ready to download jars per commit on any branch.
See [change logs here](https://github.com/JiroCab/Olupis/blob/main/changelog.txt).

Building Locally works the same as most other mods,  use `./gradlew jar` or `./gradlew deploy`.
alternatively you may use [Toxopid](https://github.com/Xpdustry/Toxopid) with `./gradlew runMindustryClient ` , `./gradlew runMindustryServer`.
you can figure out the rest, you got this, you're a smart cookie

[Discord server](https://discord.gg/S8wuZAF4ZZ) to talk and complain about the mod.

# Contributing
With the way the repository is structured, please refer to the [Experimental branch](https://github.com/JiroCab/Olupis/tree/experimental) on basing changes to avoid future conflict.
And refer to the [Experimental branch changelog.txt](https://github.com/JiroCab/Olupis/blob/experimental/changelog.txt) in case of any similar changes are made

## Credits:
* [RushieWashie](https://github.com/JiroCab) - Code, Sprites, Maps
* [NightmarishWolf](https://github.com/NightmarishWolf) - Sprites, Ideas, Organization, Maps
* [Ethanol10](https://github.com/SuperEthanol10) - Sprites
* [Siede](https://github.com/siede2010) - Code, Sprites
* [WMF Industries](https://github.com/WMF-Industries) - Code
* MrApple - Maps
* [Kapzduke](https://github.com/kapzduke) - Sprites
* [Otamamori](https://github.com/Otamamori917) - Sprites
* [Catana](https://github.com/Catana791) - Sprites
* [ItsKirby69](https://github.com/ItsKirby69) - Sprites


 # Mindusty v7 / Nyfalis v1.7.x Migration guide
With the migration to v8 and the rework of content needed, bundles names are now its internal name (ex: porter -> sentry)
Thus the following content will no longer load properly, migration will require you to replace the following
- t2+ Cores (vestige and above)
- Porter tree  (to Sentry and respective upgrades)
