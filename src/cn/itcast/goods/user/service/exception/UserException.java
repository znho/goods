package cn.itcast.goods.user.service.exception;

/**
 * Created by znho_0 on 2017/3/8.
 */
public class UserException extends Exception{
    public UserException() {
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserException(Throwable cause) {
        super(cause);
    }
}
