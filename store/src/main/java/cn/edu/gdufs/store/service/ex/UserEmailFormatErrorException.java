package cn.edu.gdufs.store.service.ex;

/**
 * Description:邮箱格式错误的异常
 */
public class UserEmailFormatErrorException extends ServiceException {
    public UserEmailFormatErrorException() {
        super();
    }

    public UserEmailFormatErrorException(String message) {
        super(message);
    }

    public UserEmailFormatErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserEmailFormatErrorException(Throwable cause) {
        super(cause);
    }

    protected UserEmailFormatErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
