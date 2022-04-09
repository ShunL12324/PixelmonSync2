# PixelmonOverlay-Forge 

A sponge plugin that can transfer player & pixelmon data between servers via database.

Database details can be specified in config files

## Dependencies

1. Pixelmon 8.3.1 +
2. sponge 7.4.0 +
3. forge-1.12.2-14.23.5.2860+

## Commands 

* `psycn save [<player>]`

Save a player's data to database that is specified in config. 

Leave `[<player>]` blank to specify command user.

* `psycn load [<player>]`

Load a player's data from database

* `psycn server`

Save a server's data to database(?)


* `psycn migrate`

load a server's data from database(?)

## Permissions 

`pixelmonsync.admin.save`

`pixelmonsync.admin.load`

`pixelmonsync.admin.server`

`pixelmonsync.admin.migrate`
