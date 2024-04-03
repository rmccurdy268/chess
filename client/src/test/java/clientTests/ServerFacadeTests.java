package clientTests;

import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.LoginInfo;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(String.format("http://localhost:%d", port));
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    //Positive register test
    @Test
    public void registerTest() throws Exception{
        UserData user = new UserData("player1", "password", "p1@email.com");
        int myAuth = facade.addUser(user);
        assertTrue(myAuth < 10);
    }

    //Negative register test
    @Test
    public void doubleRegister(){
        try {
            UserData user = new UserData("player1", "password", "p1@email.com");
            int myAuth = facade.addUser(user);
            int secondAuth = facade.addUser(user);
        }
        catch(Exception e){
            assertTrue(true);
        }
    }

    @Test
    public void successfulLogin()throws Exception{
        UserData user = new UserData("player1", "password", "p1@email.com");
        int myAuth = facade.addUser(user);
        assertEquals(1, myAuth);
        LoginInfo myInfo = new LoginInfo(user.username(), user.password());
        myAuth = facade.login(myInfo);
        assertEquals(2, myAuth);
    }

    @Test
    public void unsuccessfulLogin(){
        try{
            UserData user = new UserData("player1", "password", "p1@email.com");
            int myAuth = facade.addUser(user);
            assertEquals(1, myAuth);
            LoginInfo myInfo = new LoginInfo(user.username(), "differentPassword");
            myAuth = facade.login(myInfo);
        }
        catch(Exception e){
            assertTrue(true);
        }
    }
    @Test
    public void successfulLogout()throws Exception{
        UserData user = new UserData("player1", "password", "p1@email.com");
        int myAuth = facade.addUser(user);
        assertEquals(1, myAuth);
        assertDoesNotThrow(()->facade.logout());
    }

    @Test
    public void unsuccessfulLogout(){
        assertThrows(exception.ResponseException.class, ()->facade.logout());
    }

    @Test
    public void successfulGameCreation()throws Exception{
        UserData user = new UserData("player1", "password", "p1@email.com");
        int myAuth = facade.addUser(user);
        assertEquals(1,facade.createGame("myNewGame"));
    }

    @Test
    public void createGameNotLoggedIn(){
        assertThrows(exception.ResponseException.class, ()->facade.createGame("newGame"));
    }

    @Test
    public void listGamesSuccess()throws Exception{
        UserData user = new UserData("player1", "password", "p1@email.com");
        int myAuth = facade.addUser(user);
        int myGame = facade.createGame("myNewGame");
        assertDoesNotThrow(()->facade.listGames());
    }

    @Test
    public void listGamesNotLoggedIn(){
        assertThrows(exception.ResponseException.class, ()->facade.listGames());
    }

    @Test
    public void joinAsPlayerSuccess()throws Exception{
        UserData user = new UserData("player1", "password", "p1@email.com");
        int myAuth = facade.addUser(user);
        assertEquals(1,facade.createGame("myNewGame"));
        assertDoesNotThrow(()->facade.joinAsPlayer(1, "white"));
    }

    @Test
    public void joinAsPlayerOccupied()throws Exception{
        UserData user = new UserData("player1", "password", "p1@email.com");
        facade.addUser(user);
        assertEquals(1,facade.createGame("myNewGame"));
        assertDoesNotThrow(()->facade.joinAsPlayer(1, "white"));
        facade.logout();
        UserData newUser = new UserData("player2", "password", "p1@email.com");
        facade.addUser(newUser);
        assertThrows(exception.ResponseException.class, ()->facade.joinAsPlayer(1,"white"));
    }

    @Test
    public void joinAsObserverSuccess()throws Exception{
        UserData user = new UserData("player1", "password", "p1@email.com");
        int myAuth = facade.addUser(user);
        assertEquals(1,facade.createGame("myNewGame"));
        assertDoesNotThrow(()->facade.joinAsObserver(1));
    }

    @Test
    public void joinObserverGameDoesNotExist()throws Exception{
        UserData user = new UserData("player1", "password", "p1@email.com");
        int myAuth = facade.addUser(user);
        assertEquals(1,facade.createGame("myNewGame"));
        assertThrows(exception.ResponseException.class, ()->facade.joinAsObserver(2));
    }

    @Test
    public void testClear()throws Exception{
        UserData user = new UserData("player1", "password", "p1@email.com");
        int myAuth = facade.addUser(user);
        int gameID = facade.createGame("newGame");
        facade.clear();
        assertEquals(1,facade.addUser(user));
        assertEquals("",facade.listGames());
    }
}