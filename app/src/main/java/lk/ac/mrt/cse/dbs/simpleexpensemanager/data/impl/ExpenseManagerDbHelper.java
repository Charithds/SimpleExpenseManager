package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;

/**
 * Created by Charith on 11/19/2016.
 *
 * Used to create a database.
 * It is implemented using singleton pattern to optimize the performance.
 */

public class ExpenseManagerDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "ExpenseManager_140601G.db";


    private static final String SQL_CREATE_TABLE_ACCOUNT = "CREATE TABLE "+ExpenseManagerContract.AccountEntry.TABLE_NAME+
            " ( " +
            ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO+" int, " +
            ExpenseManagerContract.AccountEntry.COLUMN_BANK_NAME+" varchar(100), " +
            ExpenseManagerContract.AccountEntry.COLUMN_ACC_HOLDER_NAME+" varchar(100) not null, " +
            ExpenseManagerContract.AccountEntry.COLUMN_BALANCE+" DECIMAL(12,2), " +
            "CONSTRAINT pk_account_id PRIMARY KEY ("+ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO+"), " +
            "CONSTRAINT balance_positive CHECK ("+ExpenseManagerContract.AccountEntry.COLUMN_BALANCE+" >= 0) "+
            " ) ";
    private static final String SQL_CREATE_TABLE_TRANSACTION = "CREATE TABLE "+ExpenseManagerContract.TransactionEntry.TABLE_NAME +
            " ( " +
            ExpenseManagerContract.TransactionEntry.COLUMN_DATE+" date not null, " +
            ExpenseManagerContract.TransactionEntry.COLUMN_ACC_NO+" varchar(20) not null, " +
            ExpenseManagerContract.TransactionEntry.COLUMN_EXPENSE_TYPE+" BOOLEAN not null, " +
            ExpenseManagerContract.TransactionEntry.COLUMN_AMOUNT+" DECIMAL(12,2) not null, " +
            "CONSTRAINT amount_positive CHECK ("+ExpenseManagerContract.TransactionEntry.COLUMN_AMOUNT+" > 0),"+
            "FOREIGN KEY ("+ExpenseManagerContract.TransactionEntry.COLUMN_ACC_NO+") REFERENCES "+ExpenseManagerContract.AccountEntry.TABLE_NAME+"("+ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO+")" +
            " ) ";
    private static final String SQL_DELETE_ACCOUNT_ENTRIES =
            "DROP TABLE IF EXISTS " + ExpenseManagerContract.AccountEntry.TABLE_NAME;
    private static final String SQL_DELETE_TRANSACTION_ENTRIES =
            "DROP TABLE IF EXISTS "+ ExpenseManagerContract.TransactionEntry.TABLE_NAME;

    private static ExpenseManagerDbHelper instance;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * param name    of the database file, or null for an in-memory database
     * param factory to use for creating cursor objects, or null for the default
     * param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    private ExpenseManagerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /* Singleton */
    public static ExpenseManagerDbHelper getHelper(Context context){
        if (instance == null)
            instance = new ExpenseManagerDbHelper(context);
        return instance;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_ACCOUNT);
        db.execSQL(SQL_CREATE_TABLE_TRANSACTION);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ACCOUNT_ENTRIES);
        db.execSQL(SQL_DELETE_TRANSACTION_ENTRIES);
        onCreate(db);
    }
}
