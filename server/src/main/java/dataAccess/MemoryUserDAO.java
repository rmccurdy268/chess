package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashSet;
import java.util.Objects;


public class MemoryUserDAO implements UserDAO{

    private HashSet<AuthData>authSet;
    private HashSet<UserData>userSet;

    private int ogAuth = 1;
    public String createUser(String username, String password, String email){
        userSet.add(new UserData(username,password,email));
        return createAuth(username);
    }

    public String createAuth(String username){
        ogAuth++;
        authSet.add(new AuthData(String.valueOf(ogAuth),username));
        return String.valueOf(ogAuth);
    }

    public boolean checkAuth(AuthData auth) {
        return authSet.contains(auth);
    }
    public UserData getUser(String username) {
    }

    public UserData checkCredentials(String username, String password){

    }

    public String getUsername(String authToken){
        try{
            for(AuthData each:authSet){
                if(Objects.equals(each.authToken(), authToken)){
                    return each.username();
                }
            }
        }
        catch (DataAccessException("User does not exist in Database")){
            throw new ;
        }
    }

    public void clearUsers(){

    }
}
