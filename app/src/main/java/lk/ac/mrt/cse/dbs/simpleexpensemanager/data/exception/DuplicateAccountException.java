package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception;

/**
 * Created by Charith on 11/20/2016.
 */

public class DuplicateAccountException extends Exception{
    public DuplicateAccountException(String detailMessage) {
        super(detailMessage);
    }

    public DuplicateAccountException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
