package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class MySQLGameDAO implements GameDAO {
    private final Gson serializer = new Gson();

    public MySQLGameDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException ex) {
            System.out.print("couldnt configure gameDAO");
        }
    }


    public HashMap<Integer, GameData> getGames() throws DataAccessException {
        var result = new HashMap<Integer, GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUser, blackUser, gameName, chessGameJson FROM gamesDB";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        var gameID = rs.getInt("gameID");
                        var gameName = rs.getString("gameName");
                        var whiteUser = rs.getString("whiteUser");
                        var blackUser = rs.getString("blackUser");
                        var chessGameJson = rs.getString("chessGameJson");
                        var deserializedGame = serializer.fromJson(chessGameJson, ChessGame.class);
                        result.put(gameID, new GameData(gameID, whiteUser, blackUser, gameName, deserializedGame));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }
        return result;
    }


    //CHECK ME LATER
    public Integer addGame(String name) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO gamesDB (gameName, chessGameJson) values (?,?)", Statement.RETURN_GENERATED_KEYS)) {
                var json = new Gson().toJson(new ChessGame());
                preparedStatement.setString(1,name);
                preparedStatement.setString(2,json);
                preparedStatement.executeUpdate();
                var resultSet = preparedStatement.getGeneratedKeys();

                var ID = 0;
                if (resultSet.next()) {
                    ID = resultSet.getInt(1);
                }

                return ID;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Wrong", 500);
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUser, blackUser, gameName, chessGameJson FROM gamesDB WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1,gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var gameName = rs.getString("gameName");
                        var whiteUser = rs.getString("whiteUser");
                        var blackUser = rs.getString("blackUser");
                        var chessGameJson = rs.getString("chessGameJson");
                        var deserializedGame = serializer.fromJson(chessGameJson, ChessGame.class);
                        return new GameData(gameID, whiteUser, blackUser, gameName, deserializedGame);
                    }
                    else{
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), 500);
        }
    }

    public void addPlayer(String userName, String teamColor, int gameID) throws DataAccessException {
        String statement = "";
        switch(teamColor){
            case "WHITE":
                statement = "UPDATE gamesDB SET whiteUser=? WHERE gameID = ?";
            case "BLACK":
                statement = "UPDATE gamesDB SET blackUser=? WHERE gameID = ?";
        }
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1,userName);
                preparedStatement.setInt(2, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("weird error in authorization", 500);
        }


    }

    public void addObserver(String userName, int gameId) throws DataAccessException {

    }

    public void clearGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE gamesDB")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("no working",500);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  gamesDB (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `gameName` varchar(256) NOT NULL,
              `whiteUser` TEXT DEFAULT NULL,
              `blackUser` TEXT DEFAULT NULL,
              `chessGameJson` TEXT NOT NULL,
              `observersJson` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()), ex.getErrorCode());
        }
    }
}
