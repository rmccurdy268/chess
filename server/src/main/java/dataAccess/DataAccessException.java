package dataAccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    final private int statusCode;
    public DataAccessException(String message, int statusCode) {
        super(message);
        this.statusCode= statusCode;
    }

    public int StatusCode() {
        return statusCode;
    }

    public static class UnauthorizedException extends DataAccessException{

        public UnauthorizedException() {
            super("Error: unauthorized", 401);
        }
    }

    public static class AlreadyTakenException extends DataAccessException{

        public AlreadyTakenException() {
            super("Error: already taken",403);
        }
    }
    public static class BadRequestException extends DataAccessException{

        public BadRequestException(){
            super("Error: bad request", 400);
        }
    }
}
