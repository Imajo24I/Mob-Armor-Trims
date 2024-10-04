[![GitHub](https://github.com/intergrav/devins-badges/raw/2dc967fc44dc73850eee42c133a55c8ffc5e30cb/assets/cozy/available/github_vector.svg)](https://github.com/Imajo24I/Mob-Armor-Trims)
[![Modrinth](https://github.com/intergrav/devins-badges/raw/2dc967fc44dc73850eee42c133a55c8ffc5e30cb/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/mob-armor-trims)
[![CurseForge](https://github.com/intergrav/devins-badges/raw/2dc967fc44dc73850eee42c133a55c8ffc5e30cb/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/mob-armor-trims)

# Mob Armor Trims

This minecraft mod makes mobs be able to spawn with naturally trimmed armor.


When a mob spawns, each of the mob's armor pieces has a chance to have an armor trim applied to it.
By default, the armor trims are randomly chosen, while also taking the previous trim into account,  
resulting in good-looking trim combinations.  
Alternatively, you can configure this mod to use customizable trim combinations instead of random trims.

## Supported Versions/Loaders/Environments

This mod is serverside.

For 1.20.1, this mod supports [Fabric](https://fabricmc.net/) and [Forge](https://files.minecraftforge.net/net/minecraftforge/forge/).<br>
For supported minecraft versions above 1.20.1, this mod supports [Fabric](https://fabricmc.net/) and [Neoforge](https://neoforged.net/).

Supported minecraft versions are:
- 1.20.1
- 1.20.4
- 1.20.5 - 1.20.6
- 1.21-1.21.1

## Dependencies

### Required

This mod has no required dependencies.

### Optional

You can configure this mod inside minecraft using a config screen.  
These are the dependencies for each modloader to be able to do this:

- Fabric: [Mod Menu](https://modrinth.com/mod/modmenu) and [Yet Another Config Lib](https://modrinth.com/mod/yacl)
- Forge/Neoforge: [Yet Another Config Lib](https://modrinth.com/mod/yacl)

## Configure

You can configure this mod through a

- Config Screen: Dependencies for this can be found under `Dependencies` > `Optional`
- Config file: The config file is located under `.minecraft/config/mob_armor_trims.toml`

There are two different systems of how the mod chooses what trims to give the mob.  
Most things about these systems are configurable.  
These two systems are:

- Random Trims - On the first armor piece of the mob, this system chooses a completely random trim to apply to the armor
  piece. The next trims are highly based on the previous trim of the armor.
- Custom Trim Combinations - This system chooses a trim combination out of a custom list of trim combinations and
  applies the combination to the armor. You can configure the trim combinations. Each combinations consists of a trim
  for each of the armor pieces and an armor material, on which the combination will applied on. It won't be applied to
  any armor with another material.

## Issues

If you have found any incompatible mod, please report the incompatibility [here](https://github.com/Imajo24I/Mob-Armor-Trims/issues/new?assignees=&labels=incompatibility&projects=&template=incompatibility.yml). <br>
If you have found any other issue, please report the issue [here](https://github.com/Imajo24I/Mob-Armor-Trims/issues/new?assignees=&labels=bug&projects=&template=bug_report.yml).

### Known Issues:

- Crash when opening the Config Screen:  
  This is a known issue with YACL (Yet Another Config Lib), where it crashes when opening any config screen created with
  YACL on Forge 1.20.1.
  As a workaround, downgrade the version of YACL to `3.4.2`.
