package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.provider.BaseColumns;

/**
 * Created by Charith on 11/19/2016.
 *
 * This class specifies the column names and table names used.
 * Using this class makes it easier to change column names
 */

public final class ExpenseManagerContract {

    public static class AccountEntry implements BaseColumns{
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_ACCOUNT_NO = "account_no";
        public static final String COLUMN_BANK_NAME = "bank_name";
        public static final String COLUMN_ACC_HOLDER_NAME = "acc_holder_name";
        public static final String COLUMN_BALANCE = "balance";
    }

    public static class TransactionEntry implements BaseColumns{
        public static final String TABLE_NAME = "acc_transaction";
        public static final String COLUMN_DATE = "transaction_date";
        public static final String COLUMN_ACC_NO = "account_no";
        public static final String COLUMN_EXPENSE_TYPE = "expense_type";
        public static final String COLUMN_AMOUNT = "amount";
    }
}
