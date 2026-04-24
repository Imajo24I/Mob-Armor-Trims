[![GitHub](https://github.com/intergrav/devins-badges/raw/2dc967fc44dc73850eee42c133a55c8ffc5e30cb/assets/cozy/available/github_vector.svg)](https://github.com/Imajo24I/Naturally-Trimmed)
[![Modrinth](https://github.com/intergrav/devins-badges/raw/2dc967fc44dc73850eee42c133a55c8ffc5e30cb/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/Naturally-Trimmed)
[![CurseForge](https://github.com/intergrav/devins-badges/raw/2dc967fc44dc73850eee42c133a55c8ffc5e30cb/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/Naturally-Trimmed)

# Naturally Trimmed

This mod applies armor trims to naturally generated armor and equipment from mobs, loot tables and trades,
resulting in a more complete gameplay experience and making armor you find in the wild much more interesting

## Features

- **Applies armor trims to armor from mob spawns.** Both completely random armor trims and predefined armor trims are
  supported.
- **Applies armor trims to armor from loot tables.** This includes chests from all structures (for example,
  bastions or end cities), piglin bartering and fishing.
- **Applies armor trims to armor from villager trades.** Note: By default, only trades above villager trading level 3
  have a chance of being trimmed.
- Also applies armor trims to tools like pickaxes or swords if
  either [Trimmable Tools](https://modrinth.com/datapack/trimmable-tools) or [Tool Trims Mod](https://modrinth.com/mod/tool-trims-mod) is installed.
- Almost all features are highly configurable. See the [Configuration section](#configuration)

## Configuration

Almost all features of this mod are highly configurable.  
You can configure them through either the in-game config screen (This requires other mods, see
the [Dependencies section](#dependencies))
or through the config file, which can be found under `.minecraft/config/naturally_trimmed.json5`.

For explanation on individual settings, see the comments attached to the settings.  
Note, the settings for the predefined trims system are only configurable in the config file.

## Dependencies

This mod requires [Fabric API](https://modrinth.com/mod/fabric-api) when used with Fabric.  
Optionally, [Yet Another Config Lib](https://modrinth.com/mod/yacl) (+ [Mod Menu](https://modrinth.com/mod/modmenu) on  Fabric)
can be used to access the config screen.

## Mod Dependencies

Although this mod should work with almost all mods, there is one long-term issue: Trims with no texture

These trims with no textures most often appear when a modded trim material is combined with another mods trim pattern, and they aren't explicitly compatible with each other.
While missing textures on trims aren't an issue caused by this mod, they are often triggered by it.

To prevent these trims, this mod has several filtering options to filter these trims out.

These are:
- `texture_validation_filtering`: This filters out the trims by directly checking if they have no texture. This is the best filtering option, but only available on the client
- `vanilla_only`: This filters out any trims that aren't vanilla, which should prevent almost all trims with no texture, since they are most often seen from modded trims.
- `material_blacklist` and `pattern_blacklist`: These are more advanced filtering options, which allow you to filter out specific trims by their material or pattern.

For more info about these options specifically, see the comments in the config file or config screen.

If you happen to come across a trim with no texture,
please create an issue report to the [project's issue tracker](https://github.com/Imajo24I/Naturally-Trimmed/issues/new?assignees=&labels=bug&projects=&template=bug_report.yml).

## Issues

If you have found any issue, please report the issue to
the [project's issue tracker](https://github.com/Imajo24I/Naturally-Trimmed/issues/new?assignees=&labels=bug&projects=&template=bug_report.yml).
