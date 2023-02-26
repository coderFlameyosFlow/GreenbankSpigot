![features](https://user-images.githubusercontent.com/49708496/213881214-ffce668a-34ec-4c93-9036-d1cd12193b3d.png)
## Features

**[...]** means optional

**%...%** means required

Stuff to know:
- **BoostedYAML**, an open-source YAML handler with more performance and features than Spigot yaml handler.
 
  Check out their SpigotMC thread [here](https://www.spigotmc.org/threads/%E2%9A%A1boostedyaml-feature-rich-library-write-once-run-everywhere-updater-comments-and-more-%E2%9A%A1.545585/)
  
  Check out their GitHub [here](https://github.com/dejvokep/boosted-yaml).
  
  Check out their Discord [here](https://discord.gg/kj7rDDraCr)
- **HikariCP**, an open-source SQL-based database handler faster than **java.sql** implementation, and most connection pools available
  
  Check out their GitHub [here](https://github.com/brettwooldridge/HikariCP).
- **InventoryFramework**, an open-source inventory handler for minecraft, this will be used for bank support and /balancetop
  
  Check out their GitHub [here](https://github.com/stefvanschie/IF).
  
  Check out their Discord [here](https://discord.gg/4KtKJB5eJj)

General:
- Multi-threaded database handling to prevent database reading, writing and vice versa to slow down.
- Stable code to work as fast as possible without affecting performance optimizations
- BoostedYAML implementation for a more performant and feature rich YAML handler.
- HikariCP usage for improved and optimized SQL-based database speed (such as SQLite)

  This adds more options in db.yml files.
- Open source under GPLv3 license waiting for improvements.
- K2 Compiler (Experimental) to speed up Kotlin compilation by an average of 2.24x 
- Full UUID support for economy, never worry about losing progress after changing your name.
  
Databases:
- MongoDB, uses synchronous drivers (although used in a different thread than the main thread)
- SQLite, uses HikariCP (used in a different thread than the main thread)

Administrative:
- Ability to give players money (/eco give %username% %amount%)
- Ability to remove money from players (/eco remove %username% %amount%)
- Ability to set player's money (/eco set %username% %amount%)
- Ability to reset player's money to default player balance (/eco reset %username%) (configurable from config.yml)
  
Messages:
- All messages are FULLY customizable via messages.yml
  If you find a message you can't customize, make an issue on github or report to discord.
  
Economy:
- Ability to pay others money (/pay %username% %amount%)
- Ability to check other player's balance, including yours (/balance [username] [flags])
- Ability to check balancetop **SOON**
- Ability for customizable bank support in GUI **SOON**
  
  Available flags for /balance is: 
  1. "--long" (Display balance in long {9000} numbers instead of short-handed {9K})

Other Commands:
- Ability to reload the plugin (/greenbank reload)
- Ability to check the plugin's version (/greenbank version)
