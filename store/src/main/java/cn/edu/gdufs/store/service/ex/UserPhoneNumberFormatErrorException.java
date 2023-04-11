package cn.edu.gdufs.store.service.ex;

/**
 * Description手机号格式错误的异常
 */
public class UserPhoneNumberFormatErrorException extends ServiceException{
    public UserPhoneNumberFormatErrorException() {
        super();
    }

    public UserPhoneNumberFormatErrorException(String message) {
        super(message);
    }

    public UserPhoneNumberFormatErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserPhoneNumberFormatErrorException(Throwable cause) {
        super(cause);
    }

    protected UserPhoneNumberFormatErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
