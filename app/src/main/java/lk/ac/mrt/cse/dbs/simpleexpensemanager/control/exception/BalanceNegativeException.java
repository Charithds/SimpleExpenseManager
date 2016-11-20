package lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception;

/**
 * Created by Charith on 11/20/2016.
 */

public class BalanceNegativeException extends Exception {
    public BalanceNegativeException(String detailMessage){
        super(detailMessage);
    }
    public BalanceNegativeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
