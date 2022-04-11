package com.github.ericliu.pixelmonsync.command;

import com.github.ericliu.pixelmonsync.Pixelmonsync;
import com.github.ericliu.pixelmonsync.handler.BCHandler;
import com.github.ericliu.pixelmonsync.handler.MigrateHandler;
import com.github.ericliu.pixelmonsync.handler.SyncHandler;
import com.github.ericliu.pixelmonsync.pref.Reference;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class Commands {

    private final static CommandSpec LOAD =
            CommandSpec.builder()
                    .executor(((src, args) -> {
                        if (args.hasAny("player") && args.<Player>getOne("player").isPresent()){
                            Player player = args.<Player>getOne("player").get();
                            SyncHandler.instance.load(player);
                        }else if (src instanceof Player){
                            Player player = ((Player) src);
                            SyncHandler.instance.load(player);
                        }
                        return CommandResult.success();
                    }))
                    .arguments(
                            GenericArguments.optional(
                                    GenericArguments.player(Text.of("player"))
                            )
                    )
                    .permission(Reference.PERM_NODE_LOAD)
                    .build();

    private final static CommandSpec SAVE =
            CommandSpec.builder()
                    .executor(((src, args) -> {
                        if (args.hasAny("player") && args.<Player>getOne("player").isPresent()){
                            Player player = args.<Player>getOne("player").get();
                            SyncHandler.instance.save(player);
                        }else if (src instanceof Player){
                            Player player = ((Player) src);
                            SyncHandler.instance.save(player);
                        }
                        return CommandResult.success();
                    }))
                    .arguments(
                            GenericArguments.optional(
                                    GenericArguments.player(Text.of("player"))
                            )
                    )
                    .permission(Reference.PERM_NODE_SAVE)
                    .build();

    private final static CommandSpec SERVER =
            CommandSpec.builder()
                    .executor(((src, args) -> {
                        if (src instanceof Player && args.<String>getOne("server").isPresent()) {
                            String serverName = args.<String>getOne("server").get();
                            SyncHandler.instance.save(((Player) src));
                            BCHandler.instance.connectToServer(((Player) src), serverName);
                        }
                        return CommandResult.success();
                    }))
                    .arguments(
                            GenericArguments.string(Text.of("server"))
                    )
                    .permission(Reference.PERM_NODE_SERVER)
                    .build();

    private final static CommandSpec MIGRATE =
            CommandSpec.builder()
            .executor(((src, args) -> {
                String database = args.<String>getOne(Text.of("database")).get();
                String table = "pixelmonsync";
                MigrateHandler migrateHandler = new MigrateHandler(database, table);
                migrateHandler.migrate();
                return CommandResult.success();
            }))
            .arguments(
                    GenericArguments.seq(
                            GenericArguments.string(Text.of("database"))
                    )
            )
            .permission(Reference.PERM_NODE_MIGRATE)
            .build();


    private final static CommandSpec BASE =
            CommandSpec.builder()
                    .executor(((src, args) -> {
                        return CommandResult.success();
                    }))
                    .child(LOAD, "load")
                    .child(SAVE, "save")
                    .child(SERVER, "server")
                    .child(MIGRATE, "migrate")
                    .build();

    public Commands(){
        Sponge.getCommandManager().register(Pixelmonsync.instance, BASE, "psync");
    }

}
