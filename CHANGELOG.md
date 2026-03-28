- Add support for 26.1 fabric and neoforge
- Drop support for 1.20.1 and 1.20.4-1.20.6
  - Due to taking way too much effort to continue to actively support and due to relatively low download counts, these versions have received a final update and will, except for maybe hotfix updates, no longer receive any more updates


- Add the vanillaOnly config entry
  - This will make the mod use exclusively (except for tool trims, as the custom trim patterns are necessary there) vanilla trim materials and patterns
  - This is intended as an easy workaround for when trims with no texture appear, as they most of the times are caused by a non-vanilla trim combination
  - This is intended as an easier and quicker alternative to the blacklists
- Add a config screen button in the util section, which opens the config file


- Fix crash when no trims materials or patterns are available
- Fix trims with a missing texture appearing when the mod is combined with tooltrims mod and a mod/datapack adding trim materials
- Rewritten buildscript and lots of other refactoring and cleanup in the mods source code