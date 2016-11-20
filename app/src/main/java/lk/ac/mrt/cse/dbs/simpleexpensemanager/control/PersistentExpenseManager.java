package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.os.Parcel;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

/**
 * Created by Charith on 11/19/2016.
 */

public class PersistentExpenseManager extends ExpenseManager {

    Context mContext;
    /***
     * This method should be implemented by the concrete implementation of this class. It will dictate how the DAO
     * objects will be initialized.
     */
    public PersistentExpenseManager(Context context) {
        this.mContext = context;
        setup();
    }

    @Override
    public void setup() {
        TransactionDAO transactionDAO = new PersistentTransactionDAO(mContext);
        setTransactionsDAO(transactionDAO);
        AccountDAO accountDAO = new PersistentAccountDAO(mContext);
        setAccountsDAO(accountDAO);
    }

}
