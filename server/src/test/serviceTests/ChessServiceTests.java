package serviceTests;

import dataAccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ChessService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChessServiceTests {
    static final ChessService service = new ChessService();

    @BeforeEach
    void clear() throws DataAccessException{
        service.clear();
    }






    //POSITIVE CLEAR
    @Test
    void clearCheck()throws DataAccessException{
        String myAuth = service.registerUser("Rhodric","password","email@rhodric.click");
        service.clear();

    }


    //positive test for register user
    @Test
    void registerUserTest()throws DataAccessException{
        String myAuth;
        assertDoesNotThrow(()->{
            service.registerUser("Rhodric","password","email@rhodric.click");
        });
    }

    //NEGATIVE REGISTER
    @Test
    void userExists() throws DataAccessException{
        service.registerUser("Rhodric","password","email@rhodric.click");
        assertThrows(DataAccessException.class, ()->service.registerUser("Rhodric","password","email@rhodric.click"));
    }
}
