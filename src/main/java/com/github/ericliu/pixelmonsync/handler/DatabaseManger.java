package com.github.ericliu.pixelmonsync.handler;

import com.github.ericliu.pixelmonsync.config.ConfigLoader;
import com.github.ericliu.pixelmonsync.config.PConfig;
import com.github.ericliu.pixelmonsync.data.DataPack;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManger {

    public static DatabaseManger instance;

    private static SqlService service;
    private DataSource dataSource;
    private final PConfig config;

    public DatabaseManger(){
        instance = this;
        config = ConfigLoader.configLoader.getConfig();
        this.setService();
        this.setDataSource();
        this.createDatabase();
        this.createTable();
    }

    private void setService(){
        service = Sponge.getServiceManager().provideUnchecked(SqlService.class);
    }

    private void setDataSource() {
        if (service == null) setService();
        try {
            dataSource = service.getDataSource(
                    "jdbc:mysql://"
                            + config.host + ":" + config.port
                            + "/?user=" + config.user
                            + "&password=" + config.passwd
                            + "&useSSL=false"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDatabase(){
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "create database if not exists " + config.database
                )
                ){
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable(){
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "create table if not exists " + config.database + "." + config.table +
                                "(id int not null auto_increment," +
                                "uuid varchar (40) not null ," +
                                "player_name varchar (40) not null ," +
                                "player_data longblob not null ," +
                                "time_point timestamp not null  default current_timestamp ," +
                                "server_unique_name varchar (40)," +
                                "index(uuid)," +
                                "index(player_name)," +
                                "index(time_point)," +
                                "primary key (id))Engine=InnoDB default charset=utf8mb4"
                )
        ){
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save(Player player, DataPack dataPack){
        delete(player);
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "insert into " + config.database + "." + config.table + " " +
                                "(uuid, player_name, player_data, server_unique_name) " +
                                "values (?,?,?,?)"
                )
                ){
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            statement.setBlob(3, dataPack.toStream());
            statement.setString(4, config.serverName);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DataPack load(Player player){
        DataPack pack = new DataPack();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "select t1.player_data from " +
                                config.database + "." + config.table
                                + " t1 where t1.time_point = (select max(t2.time_point) from " +
                                config.database + "." + config.table
                                + " t2 where t2.uuid = ?)"
                )
                ){
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                InputStream stream = resultSet.getBlob(1).getBinaryStream();
                if (stream != null){
                    pack = DataPack.toDataPack(stream);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pack;
    }

    public void delete(Player player){
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("delete from " +
                        config.database + "." + config.table +
                        " where uuid = ?")
                ){
            statement.setString(1, player.getUniqueId().toString());
            statement.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
