# TG-KITPVP

An intermediate-level KitPvP plugin for Paper servers (Minecraft 1.20-1.21+) that allows players to engage in balanced PvP battles with predefined kits.

## Features

### Core Features
- **5 Pre-configured Kits**: Warrior, Archer, Tank, Mage, and Assassin
- **Kit Selection GUI**: Interactive inventory menu for selecting kits
- **Spawn Management**: Configurable spawn point with automatic teleportation
- **Arena Boundaries**: Define PvP zones with configurable boundaries
- **Kill Tracking**: Comprehensive statistics including kills, deaths, streaks
- **Leaderboards**: Top 10 players ranked by kills
- **Economy Integration**: Optional Vault support for kill rewards
- **Anti-Cheat Basics**: Item drop/pickup prevention, kit cooldowns

### Commands
- `/kitpvp join` - Enter the arena
- `/kitpvp leave` - Leave the arena
- `/kitpvp setspawn` - Set spawn location (admin)
- `/kitpvp reload` - Reload configuration (admin)
- `/kit` or `/kit select <kitname>` - Select a kit (opens GUI if no args)
- `/kit list` - List all available kits
- `/leaderboard` (aliases: `/lb`, `/top`) - View top players

### Permissions
- `kitpvp.use` - Basic KitPVP features (default: true)
- `kitpvp.admin` - Admin commands (default: op)
- `kitpvp.reload` - Reload plugin (default: op)
- `kitpvp.create` - Create custom kits (default: op)
- `kitpvp.bypass.cooldown` - Bypass kit cooldown

## Installation

### Prerequisites
- Java 17 or higher
- Paper server 1.20-1.21+
- Maven 3.6+
- (Optional) Vault plugin for economy features

### Building from Source

1. Clone or download this repository
2. Navigate to the project directory
3. Build with Maven:
```bash
mvn clean package
```
4. The compiled JAR will be in `target/TG-KITPVP-1.0.0.jar`
5. Copy the JAR to your server's `plugins` folder
6. Restart your server

### First-Time Setup

1. Start your server to generate the default configuration
2. Stop the server
3. Edit `plugins/TG-KITPVP/config.yml` to customize settings
4. Start the server again
5. Join the server and run `/kitpvp setspawn` to set the arena spawn point

## Configuration

### Main Settings (config.yml)

```yaml
settings:
  arena-world: "world"              # World where arena is located
  spawn-on-join: true               # Teleport to spawn on join
  spawn-on-death: true              # Teleport to spawn after death
  refill-health-on-kit: true        # Restore health when selecting kit
  refill-hunger-on-kit: true        # Restore hunger when selecting kit
  kit-cooldown: 30                  # Seconds before switching kits
  prevent-item-drop: true           # Disable item dropping in arena
  prevent-item-pickup: true         # Disable item pickup in arena
  auto-respawn-delay: 20            # Ticks before auto-respawn (20 = 1 second)
```

### Arena Boundaries

```yaml
arena:
  enabled: true                     # Enable arena boundaries
  min-x: -100                       # Minimum X coordinate
  min-z: -100                       # Minimum Z coordinate
  max-x: 100                        # Maximum X coordinate
  max-z: 100                        # Maximum Z coordinate
  pvp-only-in-arena: true          # Restrict PvP to arena only
```

### Economy (requires Vault)

```yaml
economy:
  enabled: false                    # Enable economy features
  kill-reward: 10.0                # Base coins per kill
  death-penalty: 5.0               # Coins lost on death
  streak-bonus: 2.0                # Bonus per streak kill
```

### Custom Kits

You can create custom kits in the `config.yml`. Each kit supports:
- Custom armor pieces with enchantments
- Inventory items at specific slots
- Potion effects
- Custom icons and descriptions
- Permission requirements

Example kit structure:
```yaml
kits:
  mykit:
    name: "&b&lCustom Kit"
    description: "&7My custom kit description"
    icon: DIAMOND_SWORD
    permission: "kitpvp.kit.custom"
    items:
      helmet:
        material: DIAMOND_HELMET
        enchantments:
          PROTECTION_ENVIRONMENTAL: 4
      slot-0:
        material: DIAMOND_SWORD
        enchantments:
          DAMAGE_ALL: 5
    effects:
      - SPEED:1:999999
```

## Kit Descriptions

### Warrior (Balanced Fighter)
- Iron armor with Protection II
- Iron Sword (Sharpness II)
- Bow with arrows
- Golden Apples
- Regeneration effect

### Archer (Ranged Combat)
- Light armor with Projectile Protection
- Powerful bow (Power III, Infinity)
- Stone Sword for close combat
- Speed effect

### Tank (Heavy Defense)
- Full diamond armor with Protection III
- Stone Sword
- Shield
- Extra Golden Apples
- Damage Resistance and Slowness

### Mage (Potion Master)
- Gold armor
- Splash potions (Damage, Slowness, Weakness)
- Healing potions
- Regeneration effect

### Assassin (Speed & Stealth)
- Leather armor with Feather Falling
- Diamond Sword (Sharpness III)
- Invisibility potions
- Speed II and Jump Boost

## Data Storage

Player data is stored in `plugins/TG-KITPVP/playerdata.yml` and includes:
- Kills and deaths
- Current kill streak
- Best kill streak
- Last kit change timestamp
- Current kit selection

Data is automatically saved on server shutdown and player quit.

## API Usage

Developers can hook into TG-KITPVP:

```java
TGKitPVP plugin = (TGKitPVP) Bukkit.getPluginManager().getPlugin("TG-KITPVP");

// Get player data
PlayerData data = plugin.getDataManager().getPlayerData(player);
int kills = data.getKills();

// Give a kit
plugin.getKitManager().giveKit(player, "warrior");

// Check if in arena
boolean inArena = plugin.getArenaManager().isInArena(player);
```

## Troubleshooting

### Spawn not working
- Make sure you've set the spawn with `/kitpvp setspawn`
- Check that the world name in config.yml matches your server world

### Kits not loading
- Verify material names are correct (use uppercase, e.g., IRON_SWORD)
- Check console for error messages during startup
- Ensure enchantment names match Bukkit's enum names

### Economy not working
- Install Vault plugin
- Install a compatible economy plugin (e.g., EssentialsX)
- Enable economy in config.yml: `economy.enabled: true`

### PvP not working
- Check arena boundaries in config.yml
- Verify `pvp-only-in-arena` setting
- Ensure players are in the arena (`/kitpvp join`)

## Support

For issues, questions, or feature requests:
1. Check the configuration file
2. Review console logs for errors
3. Verify you're using Paper 1.20+ (not Spigot/Bukkit)

## License

This plugin is provided as-is for educational purposes. Feel free to modify and use it for your server.

## Credits

Developed for TG community servers.
Built with Paper API and optional Vault integration.
