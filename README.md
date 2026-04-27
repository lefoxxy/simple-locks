# SimpleLocks

Lock your storage with intent.

SimpleLocks is a lightweight Minecraft Forge 1.20.1 mod that gives players a clean, predictable way to protect the containers that matter. No bulky claims system. No extra GUI. No password dance. Just a named key, a right-click, and storage that knows who owns it.

Built for Forge `47.4.20`  
Mod ID: `simplelocks`  
Version: `0.1.0`  
License: All Rights Reserved

Worldloom by GOGLEO (GitHub: lefoxxy)

## Why SimpleLocks?

Minecraft storage is personal. A chest full of diamonds, tools, rare drops, build materials, or half-finished plans is not just a box. It is progress.

SimpleLocks keeps that feeling intact with a system that is easy to understand:

- Rename a key.
- Lock a container.
- Let the owner open it normally.
- Block everyone else.

It does one job and stays out of the way.

## How It Works

1. Rename a tripwire hook to `Storage Key`.
2. Right-click a chest, barrel, or trapped chest to lock it.
3. The owner can open the locked container normally.
4. Non-owners are blocked and see a clear chat message.
5. The owner crouch-right-clicks with a `Storage Key` to unlock it.

Keys are not consumed by default. Server owners can change that in config.

## Supported Containers

Enabled by default:

- Chest
- Trapped chest
- Barrel

Optional:

- Shulker boxes, disabled by default

All vanilla shulker box colors are supported when shulker locking is enabled. Shulker item-form lock persistence is not guaranteed and is out of scope for this release.

## Configuration

SimpleLocks uses a Forge server config so server owners can tune the experience without changing the mod.

| Option | Default | Description |
| --- | --- | --- |
| `keyItem` | `minecraft:tripwire_hook` | Item ID used as the lock key. Invalid IDs fall back to `minecraft:tripwire_hook`. |
| `keyName` | `Storage Key` | Exact custom item name required for the key. |
| `allowCreativeBypass` | `true` | Allows creative players to open locked containers they do not own. |
| `allowOpsBypass` | `true` | Allows operators to open locked containers they do not own. |
| `lockChests` | `true` | Enables chest locking. |
| `lockTrappedChests` | `true` | Enables trapped chest locking. |
| `lockBarrels` | `true` | Enables barrel locking. |
| `lockShulkerBoxes` | `false` | Enables shulker box locking. |
| `consumeKeyOnLock` | `false` | Consumes one key when locking, unless the player is creative. |
| `consumeKeyOnUnlock` | `false` | Consumes one key when unlocking, unless the player is creative. |

## What Players See

SimpleLocks keeps feedback direct:

- `Container locked.`
- `Container unlocked.`
- `This container is locked.`

No spam, no noisy interface, no mystery behavior.

## Limitations

SimpleLocks is intentionally focused. This release does not include:

- GUI screens
- Shared access lists
- Passwords
- Claims integration
- Guaranteed support for modded containers
- Guaranteed shulker item-form persistence

## Release Focus

SimpleLocks is for servers that want straightforward container protection without turning storage into paperwork. It is small, readable, configurable, and Forge-native.

Bring the key. Lock the chest. Keep building.
