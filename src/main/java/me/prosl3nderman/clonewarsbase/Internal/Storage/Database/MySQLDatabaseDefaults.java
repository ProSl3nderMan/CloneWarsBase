package me.prosl3nderman.clonewarsbase.Internal.Storage.Database;

import org.bukkit.Bukkit;

import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Singleton
public class MySQLDatabaseDefaults {

    public void createDefaultTablesAndProceduresIfNotExist(Connection connection) {
        List<String> allCreationQueries = new ArrayList<>();
        List<String> allDefaultTableCreationQueries = getAllDefaultTableCreationQueries();
        List<String> allDefaultProcedureCreationQueries = getAllDefaultProcedureCreationQueries();
        allCreationQueries.addAll(allDefaultTableCreationQueries); allCreationQueries.addAll(allDefaultProcedureCreationQueries);

        for (String creationQuery : allCreationQueries) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(creationQuery)) {
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private List<String> getAllDefaultTableCreationQueries() {
        List<String> defaultTableCreationQueries = new ArrayList<>();
        defaultTableCreationQueries.add("CREATE TABLE IF NOT EXISTS Players ( " +
                "ID int NOT NULL AUTO_INCREMENT, " +
                "UUID varchar(64), " +
                "PRIMARY KEY (ID), " +
                "UNIQUE KEY (UUID)" +
                ")");
        defaultTableCreationQueries.add("CREATE TABLE IF NOT EXISTS Battalions ( " +
                "ID int NOT NULL AUTO_INCREMENT, " +
                "Name varchar(32), " +
                "PRIMARY KEY (ID), " +
                "UNIQUE KEY (Name)" +
                ")");
        defaultTableCreationQueries.add("CREATE TABLE IF NOT EXISTS PlayerBattalionRelations ( " +
                "PlayerID_FK int unsigned NOT NULL, " +
                "BattalionID_FK int unsigned NOT NULL, " +
                "PRIMARY KEY (PlayerID_FK, BattalionID_FK)" +
                ")");
        defaultTableCreationQueries.add("CREATE TABLE IF NOT EXISTS PlayerIGNs ( " +
                "ID int, " +
                "IGN varchar(20), " +
                "PRIMARY KEY (ID)" +
                ")");
        return defaultTableCreationQueries;
    }

    private List<String> getAllDefaultProcedureCreationQueries() {
        List<String> defaultProcedureCreationQueries = new ArrayList<>();
        defaultProcedureCreationQueries.add("DROP PROCEDURE IF EXISTS addPlayerToBattalion");
        defaultProcedureCreationQueries.add("CREATE PROCEDURE addPlayerToBattalion(IN pUUID text(64), IN bName varchar(32))" +
                "begin " +
                "replace into PlayerBattalionRelations(PlayerID_FK, BattalionID_FK) " +
                        "values((select ID from Players where UUID=pUUID), (select ID from Battalions where Name=bName)); " +
                "end");
        defaultProcedureCreationQueries.add("DROP PROCEDURE IF EXISTS removePlayerFromBattalion");
        defaultProcedureCreationQueries.add("CREATE PROCEDURE removePlayerFromBattalion(IN pIGN varchar(20))" +
                "begin " +
                "delete from PlayerBattalionRelations " +
                        "where PlayerID_FK = (select ID from PlayerIGNs where IGN=pIGN); " +
                "end");
        defaultProcedureCreationQueries.add("DROP PROCEDURE IF EXISTS createBattalion");
        defaultProcedureCreationQueries.add("CREATE PROCEDURE createBattalion(IN bName varchar(32))" +
                "begin " +
                "if NOT EXISTS(select ID from Battalions where Name=bName) then " +
                        "insert into Battalions(Name) values(bName); " +
                "end if;" +
                "end");
        defaultProcedureCreationQueries.add("DROP PROCEDURE IF EXISTS deleteBattalion");
        defaultProcedureCreationQueries.add("CREATE PROCEDURE deleteBattalion(IN bName varchar(32))" +
                "begin " +
                "delete from PlayerBattalionRelations " +
                        "where BattalionID_FK=(select ID from Battalions where Name=bName); " +
                "delete from Battalions " +
                        "where Name=bName; " +
                "end");
        defaultProcedureCreationQueries.add("DROP PROCEDURE IF EXISTS updatePlayerIGNAndReturnID");
        defaultProcedureCreationQueries.add("CREATE PROCEDURE updatePlayerIGNAndReturnID(IN pUUID text(64), IN pIGN varchar(20))" +
                "begin " +
                "if NOT EXISTS(select ID from Players where UUID=pUUID) then " +
                        "insert into Players(UUID) values(pUUID); " +
                "end if; " +
                "replace into PlayerIGNs(ID,IGN) values((select ID from Players where UUID=pUUID), pIGN); " +
                "select ID from PlayerIGNs where IGN=pIGN; " +
                "end");
        defaultProcedureCreationQueries.add("DROP PROCEDURE IF EXISTS getPlayersBattalion");
        defaultProcedureCreationQueries.add("CREATE PROCEDURE getPlayersBattalion(IN pIGN varchar(20))" +
                "begin " +
                "select Name from Battalions where " +
                        "ID=(select BattalionID_FK from PlayerBattalionRelations where " +
                                "PlayerID_FK=(select ID from PlayerIGNs where IGN=pIGN)); " +
                "end");
        defaultProcedureCreationQueries.add("DROP PROCEDURE IF EXISTS getListOfBattalions");
        defaultProcedureCreationQueries.add("CREATE PROCEDURE getListOfBattalions()" +
                "begin " +
                "select Name from Battalions; " +
                "end");
        defaultProcedureCreationQueries.add("DROP PROCEDURE IF EXISTS getPlayerIGNsOfBattalion");
        defaultProcedureCreationQueries.add("CREATE PROCEDURE getPlayerIGNsOfBattalion(IN bNAme varchar(32))" +
                "begin " +
                "SELECT IGN FROM PlayerIGNs " +
                "INNER JOIN PlayerBattalionRelations ON PlayerIGNs.ID = PlayerBattalionRelations.PlayerID_FK " +
                "AND PlayerBattalionRelations.BattalionID_FK=(SELECT Battalions.ID FROM Battalions WHERE Battalions.Name=bName); " +
                "end");
        defaultProcedureCreationQueries.add("DROP PROCEDURE IF EXISTS getPlayerUUIDsOfBattalion");
        defaultProcedureCreationQueries.add("CREATE PROCEDURE getPlayerUUIDsOfBattalion(IN bNAme varchar(32))" +
                "begin " +
                "SELECT UUID FROM Players " +
                "INNER JOIN PlayerBattalionRelations ON Players.ID = PlayerBattalionRelations.PlayerID_FK " +
                "AND PlayerBattalionRelations.BattalionID_FK=(SELECT Battalions.ID FROM Battalions WHERE Battalions.Name=bName); " +
                "end");
        defaultProcedureCreationQueries.add("DROP PROCEDURE IF EXISTS getPlayerUUIDFromIGN");
        defaultProcedureCreationQueries.add("CREATE PROCEDURE getPlayerUUIDFromIGN(IN pIGN varchar(20))" +
                "begin " +
                "select UUID from Players where ID=(select ID from PlayerIGNs where IGN=pIGN); " +
                "end");
        return defaultProcedureCreationQueries;
    }
}