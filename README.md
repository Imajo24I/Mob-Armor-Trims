[![GitHub](https://github.com/intergrav/devins-badges/raw/2dc967fc44dc73850eee42c133a55c8ffc5e30cb/assets/cozy/available/github_vector.svg)](https://github.com/Imajo24I/Mob-Armor-Trims)
[![Modrinth](https://github.com/intergrav/devins-badges/raw/2dc967fc44dc73850eee42c133a55c8ffc5e30cb/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/mob-armor-trims)
[![CurseForge](https://github.com/intergrav/devins-badges/raw/2dc967fc44dc73850eee42c133a55c8ffc5e30cb/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/mob-armor-trims)

# Naturally Trimmed

This mod applies armor trims to naturally generated armor and equipment from mobs, loot tables and trades,
resulting in a more complete gameplay experience

## Features

- **Applies armor trims to mobs spawning with armor.** By default, the trims are chosen randomly with consideration of
  the
  previous trim applied to the armor set
- **Applies random armor trims to armor from loot tables.** This includes chests from all structures (for example,
  bastions
  or end cities), piglin bartering and fishing.
- **Applies random armor trims to armor from villager trades.** Note: By default, only trades above villager trading level 3 have a
  chance of being trimmed


- Also applies armor trims to equipment like pickaxes or swords if
  either [Trimmable Tools](https://modrinth.com/datapack/trimmable-tools)
  or [Tool Trims Mod](https://modrinth.com/mod/tool-trims-mod) is installed.
  Note, the [Tools Trims Datapack](https://modrinth.com/mod/tool-trims-mod) is **not** supported
- Almost all features are highly configurable. See the [Configuration section](#configuration)

## Configuration

Almost all features of this mod are highly configurable.  
You can configure them through either the in-game config screen (This requires other mods, see
the [Dependencies section](#dependencies))
or through the config file, which can be found under `.minecraft/config/naturally_trimmed.json5`.

For explanation on individual settings, see the comments attached to the settings.  
Note, the settings for the custom trim combinations system are only available in the config file.

## Dependencies

This mod requires [Fabric API](https://modrinth.com/mod/fabric-api) when used with Fabric.  
Optionally,[Yet Another Config Lib](https://modrinth.com/mod/yacl) and [Mod Menu](https://modrinth.com/mod/modmenu)
can be used to access the config screen.

## Issues

If you have found any incompatible mod, please report the
incompatibility [here](https://github.com/Imajo24I/Mob-Armor-Trims/issues/new?assignees=&labels=incompatibility&projects=&template=incompatibility.yml). <br>
If you have found any other issue, please report the
issue [here](https://github.com/Imajo24I/Mob-Armor-Trims/issues/new?assignees=&labels=bug&projects=&template=bug_report.yml).
