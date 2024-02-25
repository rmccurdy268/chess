package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
public class MemoryUserDAO implements UserDAO{

    private HashMap<String, UserData> userMap;
    private HashMap<String, AuthData> authMap;

    private Integer ogAuth = 0;

    public MemoryUserDAO(){
        userMap = new HashMap<String, UserData>();
        authMap = new HashMap<String, AuthData>();
    }

    public void createUser(String username, String password, String email){
        userMap.put(username, new UserData(username,password,email));
    }
    public String createAuth(String username){
        String newAuth = String.valueOf(ogAuth++);
        authMap.put(newAuth, new AuthData(String.valueOf(newAuth), username));
        return newAuth;
    }
    public boolean checkCredentials(String username, String password){
        UserData myUserData = getUser(username);
        return password.equals(myUserData.password());
    }

    public AuthData checkAuth(String auth) {
        return authMap.get(auth);
    }

    //getUser throws data exception when the user already exists
    public UserData getUser(String username){
        return userMap.get(username);
    }

    public String getAuthToken(String username){
        for(AuthData each: authMap.values()){
            if(each.username().equals(username)){
                return each.authToken();
            }
        }
        return null;
    }

    public AuthData getAuthData(String authToken){
        return authMap.get(authToken);
    }

    public void deleteAuth(String authToken){
        authMap.remove(authToken);
    }

    public void clearUsers(){
        userMap.clear();
    }

    public void clearAuth(){
        authMap.clear();
    }
}
