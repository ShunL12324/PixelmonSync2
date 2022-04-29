# PixelmonOverlay-Forge 

A sponge plugin that can transfer player & pixelmon data between servers via database.

Database details can be specified in config files

## Dependencies

1. Pixelmon 8.3.1 +
2. sponge 7.4.0 +
3. forge-1.12.2-14.23.5.2860+

## Commands 

* `psync save [<player>]`

Save a player's data to database that is specified in config. 

Leave `[<player>]` blank to specify command user.

* `psync load [<player>]`

Load a player's data from database

* `psync server`

Use this command as the alternative of `server` command of BungeeCord, this command make sure the data has been fully saved before connecting the player to another sub-server  


* `psync migrate`

If you are using PixelmonSync 1, you could use this command to migrate player data  

## Permissions 

`pixelmonsync.user.base` (Allow auto load/save when player join/leave)

`pixelmonsync.admin.save`

`pixelmonsync.admin.load`

`pixelmonsync.admin.server`

`pixelmonsync.admin.migrate`
