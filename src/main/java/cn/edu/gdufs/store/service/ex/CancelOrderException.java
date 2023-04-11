package cn.edu.gdufs.store.service.ex;

/**
 * Description:删除数据失败的异常
 */
public class CancelOrderException extends ServiceException {
    public CancelOrderException() {
        super();
    }

    public CancelOrderException(String message) {
        super(message);
    }

    public CancelOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public CancelOrderException(Throwable cause) {
        super(cause);
    }

    protected CancelOrderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
