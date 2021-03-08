package com.github.ericliu.pixelmonsync.command;

import com.github.ericliu.pixelmonsync.Pixelmonsync;
import com.github.ericliu.pixelmonsync.handler.BCHandler;
import com.github.ericliu.pixelmonsync.handler.MigrateHandler;
import com.github.ericliu.pixelmonsync.handler.SyncHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class Commands {

    private final static CommandSpec load =
            CommandSpec.builder()
                    .executor(((src, args) -> {
                        if (args.hasAny("player")){
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
                    .permission("pixelmonsync.load")
                    .build();

    private final static CommandSpec save =
            CommandSpec.builder()
                    .executor(((src, args) -> {
                        if (args.hasAny("player")){
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
                    .permission("pixelmonsync.save")
                    .build();

    private final static CommandSpec server =
            CommandSpec.builder()
                    .executor(((src, args) -> {
                        if (src instanceof Player) {
                            String serverName = args.<String>getOne("server").get();
                            SyncHandler.instance.save(((Player) src));
                            BCHandler.instance.connectToServer(((Player) src), serverName);
                        }
                        return CommandResult.success();
                    }))
                    .arguments(
                            GenericArguments.string(Text.of("server"))
                    )
                    .permission("pixelmonsync.server")
                    .build();

    private final static CommandSpec migrate =
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
            .permission("pixelmonsync.migrate")
            .build();


    private final static CommandSpec base =
            CommandSpec.builder()
                    .executor(((src, args) -> {
                        return CommandResult.success();
                    }))
                    .child(load, "load")
                    .child(save, "save")
                    .child(server, "server")
                    .child(migrate, "migrate")
                    .build();

    public Commands(){
        Sponge.getCommandManager().register(Pixelmonsync.instance, base, "psync");
    }

}
