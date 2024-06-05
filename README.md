[<img src="https://github.com/sn0wfrog/modding-badges/blob/main/Available%20on%20Modrinth.png" alt="Available on Modrinth" width="200"/>](https://modrinth.com/mod/mob-armor-trims)

# Mob Armor Trims
This minecraft mod makes mobs be able to spawn with naturally trimmed armor.

## What does the mod exactly do?
If a mob has armor, it also has a configurable chance of having armor trims on its armor piece.  
The armor trims are randomly chosen, while also taking the previous applied trims into account, 
creating good-looking trim combinations.  
This mod is highly configurable. 
Learn more about configuring this mod in the `Configure` part.

## Clientside/Serverside
This mod is serverside.

##  Dependencies

### Required
For 1.20.1, you can use
- [Fabric](https://fabricmc.net/) - When using Fabric, this mod also depends on [Fabric API](https://modrinth.com/mod/fabric-api)
- [Forge](https://files.minecraftforge.net/net/minecraftforge/forge/) - When using Forge, this mod has no dependencies

For 1.20.4, you can use
- [Fabric](https://fabricmc.net/) - When using Fabric, this mod depends on [Fabric API](https://modrinth.com/mod/fabric-api)
- [Neoforge](https://neoforged.net/) - When using Neoforge, this mod has no dependencies

For 1.20.5/1.20.6, you can use 
- [Fabric](https://fabricmc.net/) - When using Fabric, this mod depends on [Fabric API](https://modrinth.com/mod/fabric-api)
- [Neoforge](https://neoforged.net/) - When using Neoforge, this mod has no dependencies

### Optional
You can configure this mod inside minecraft using a Config Screen.  
These are the dependencies for each modloader to be able to do this:
- Fabric:  [Mod Menu](https://modrinth.com/mod/modmenu) and [Yet Another Config Lib](https://modrinth.com/mod/yacl)
- Forge/Neoforge: [Yet Another Config Lib](https://modrinth.com/mod/yacl)

## Configure
You can configure this mod through a
- Config Screen -  Dependencies for this can be found under `Dependencies` > `Optional` in this file
- Config file - The config file is located in `.minecraft/config/mob_armor_trims.json`
 
There are two different systems of how the mod chooses what trims to give the mob.  
Most things about these systems are configurable.  
These two systems are:
- Random Trims - On the first armor piece of the mob, this system chooses a completely random trim to apply to the armor piece. The next trims are highly based on the previous trim of the armor.
- Custom Trims - This system chooses an armor trim from a list of trims and applies it to the whole mob. This list of trims is completely configurable.

## Compatibility
Currently, there are no found incompatible mods. If you find any incompatible mod, please report it [here](https://github.com/Imajo24I/Mob-Armor-Trims/issues/new?assignees=&labels=incompatibility&projects=&template=incompatibility.yml)

## Issues
If you have found any Issues, please report them [here](https://github.com/Imajo24I/Mob-Armor-Trims/issues/new?assignees=&labels=bug&projects=&template=bug_report.yml)  
- Crash when opening a Config Screen while using forge:  
  This is a known issue with YACL, where it crashes when opening any config screen done with YACL on Forge 1.20.1.  
  As a workaround, change the version of YACL from `3.4.4` to `3.4.2`.
