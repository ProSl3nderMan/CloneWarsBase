package me.prosl3nderman.clonewarsbase.Internal.Storage.Database;

import me.prosl3nderman.clonewarsbase.Internal.Battalions.Battalion;
import me.prosl3nderman.clonewarsbase.Internal.Battalions.BattalionHandler;
import me.prosl3nderman.clonewarsbase.CloneWarsBase;
import me.prosl3nderman.clonewarsbase.Internal.Exceptions.NotAsynchronousException;
import me.prosl3nderman.clonewarsbase.Internal.Player.Player;
import org.bukkit.Bukkit;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MySQLDatabase {

    private CloneWarsBase plugin;
    private Provider<BattalionHandler> battalionHandler;
    private MySQLDatabaseDefaults mySQLDatabaseDefaults;

    @Inject
    public MySQLDatabase(CloneWarsBase plugin, Provider<BattalionHandler> battalionHandler, MySQLDatabaseDefaults mySQLDatabaseDefaults) {
        this.plugin = plugin;
        this.battalionHandler = battalionHandler;
        this.mySQLDatabaseDefaults = mySQLDatabaseDefaults;
    }

    private String host, database, username, password;
    private int port;

    public void loadDefaultValues() {
        String path = "MySQLDatabase.";
        host = plugin.getConfig().getString(path + "host");
        port = plugin.getConfig().getInt(path + "port");
        database = plugin.getConfig().getString(path + "database");
        username = plugin.getConfig().getString(path + "username");
        password = plugin.getConfig().getString(path + "password");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    mySQLDatabaseDefaults.createDefaultTablesAndProceduresIfNotExist(openConnection());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Connection openConnection() throws SQLException, ClassNotFoundException {
        synchronized (this) {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?useSSL=false&allowMultiQueries=true", this.username, this.password);

        }
    }

    private void checkIfBeingRanOnMainThread() {
        if (Bukkit.isPrimaryThread())
            throw new NotAsynchronousException("Error! This must be ran in an asynchronous thread, but was ran in the main thread.");
    }

    public Battalion getPlayersBattalion(Player player) {
        checkIfBeingRanOnMainThread();
        try (Connection connection = openConnection()) {
            String getPlayerBattalionQuery = "{CALL getPlayersBattalion(?)}";
            try (CallableStatement preparedStatement = connection.prepareCall(getPlayerBattalionQuery)) {
                preparedStatement.setString(1, player.getPlayerName());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next())
                        return battalionHandler.get().getBattalion(resultSet.getString("Name"));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return battalionHandler.get().getBattalion(player.getRank());
    }

    public void addPlayerToBattalion(Player player, String battalionName) {
        checkIfBeingRanOnMainThread();
        try (Connection connection = openConnection()) {
            String addPlayerToBattalionCall = "{CALL addPlayerToBattalion(?,?)}";
            try (CallableStatement callableStatement = connection.prepareCall(addPlayerToBattalionCall)) {
                callableStatement.setString(1,player.getPlayerUUID().toString());
                callableStatement.setString(2,player.getPlayerName());
                callableStatement.setString(3, battalionName);
                callableStatement.execute();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void removePlayerFromBattalion(String playerName, String battalionName) {
        checkIfBeingRanOnMainThread();
        try (Connection connection = openConnection()) {
            String removePlayerFromBattalionCall = "{CALL removePlayerFromBattalion(?,?)}";
            try (CallableStatement callableStatement = connection.prepareCall(removePlayerFromBattalionCall)) {
                callableStatement.setString(1,playerName);
                callableStatement.setString(2,battalionName);
                callableStatement.execute();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Integer updatePlayerInformationAndGetPlayerID(Player player) {
        checkIfBeingRanOnMainThread();
        try (Connection connection = openConnection()) {
            String updatePlayerInformationQuery = "{CALL updatePlayerIGNAndReturnID(?,?)}";
            try (CallableStatement callableStatement = connection.prepareCall(updatePlayerInformationQuery)) {
                callableStatement.setString(1, player.getPlayerUUID().toString());
                callableStatement.setString(2, player.getPlayerName());

                try (ResultSet resultSet = callableStatement.executeQuery()) {
                    if (resultSet.next())
                        return resultSet.getInt("ID");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 999999999;
    }

    public void createBattalion(String battalionName) {
        checkIfBeingRanOnMainThread();
        try (Connection connection = openConnection()) {
            String createBattalionCall = "{CALL " + database + ".createBattalion(?)}";
            try (CallableStatement callableStatement = connection.prepareCall(createBattalionCall)) {
                callableStatement.setString(1, battalionName);
                callableStatement.execute();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void deleteBattalion(String battalionName) {
        checkIfBeingRanOnMainThread();
        try (Connection connection = openConnection()) {
            String deleteBattalion = "{CALL deleteBattalion(?)}";
            try (CallableStatement callableStatement = connection.prepareCall(deleteBattalion)) {
                callableStatement.setString(1,battalionName);
                callableStatement.execute();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllBattalionNames() {
        checkIfBeingRanOnMainThread();
        List<String> battalionNamesList = new ArrayList<>();
        try (Connection connection = openConnection()) {
            String getAllBattalionsQuery = "{CALL getListOfBattalions()}";
            try (CallableStatement callableStatement = connection.prepareCall(getAllBattalionsQuery)) {
                try (ResultSet resultSet = callableStatement.executeQuery()) {
                    while (resultSet.next())
                        battalionNamesList.add(resultSet.getString("Name"));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return battalionNamesList;
    }
}
