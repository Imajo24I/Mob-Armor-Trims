- Added a new system of how to give mobs armor trims
  - Existing system is now named `Random Trims`, new one `Custom Trims`
  - You can choose between these two systems
  - The Custom Trims System takes a Trim out of the Trims List and applies it to the whole armor of the mob.
  You can configure which trims are in this list  
- Added disabling the mod
  - You can disable the mod using the `Trim System` option
- Overhauled Config File
  - Added Subcategories
  - Added descriptions to options
  - Switched from GSON to Night Config
- Switched from Cloth Config API as a dependency to [Yet Another Config Lib](https://modrinth.com/mod/yacl)
- Removed the Architectury API dependency on Forge and Neoforge
- Added dependencies to modinfo (fabric.mod.json, etc.)
  