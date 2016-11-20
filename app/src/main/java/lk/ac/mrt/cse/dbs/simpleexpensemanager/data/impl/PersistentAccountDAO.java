package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.BalanceNegativeException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.DuplicateAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by Charith on 11/19/2016.
 *
 */

public class PersistentAccountDAO implements AccountDAO {

    private final ExpenseManagerDbHelper expenseManagerDbHelper;
    private final Context mContext;

    public PersistentAccountDAO(Context context) {
        this.mContext = context;
        expenseManagerDbHelper = ExpenseManagerDbHelper.getHelper(context);
    }

    /***
     * Get a list of account numbers.
     *
     * @return - list of account numbers as String
     */
    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = expenseManagerDbHelper.getReadableDatabase();
        List<String> acc_no_list = new ArrayList<>();
        Cursor cursor = db.query(ExpenseManagerContract.AccountEntry.TABLE_NAME,
                new String[]{ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO},null, null, null, null, null);

        if(cursor.moveToFirst()){
            do {
                acc_no_list.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }
        db.close();
        //Log.d(TAG, "getAccountNumbersList: ");
        return acc_no_list;
    }

    /***
     * Get a list of accounts.
     *
     * @return - list of Account objects.
     */
    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = expenseManagerDbHelper.getReadableDatabase();
        List<Account> acc_list = new ArrayList<>();
        Cursor cursor = db.query(ExpenseManagerContract.AccountEntry.TABLE_NAME,
                new String[]{ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO,
                        ExpenseManagerContract.AccountEntry.COLUMN_BANK_NAME,
                        ExpenseManagerContract.AccountEntry.COLUMN_ACC_HOLDER_NAME,
                        ExpenseManagerContract.AccountEntry.COLUMN_BALANCE },null, null, null, null, null);

        if(cursor.moveToFirst()){
            do {
                Account account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3));
                acc_list.add(account);
            }while(cursor.moveToNext());
        }
        db.close();
        return acc_list;
    }

    /***
     * Get the account given the account number.
     *
     * @param accountNo as String
     * @return - the corresponding Account
     * @throws InvalidAccountException - if the account number is invalid
     */
    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = expenseManagerDbHelper.getReadableDatabase();
        Cursor cursor = db.query(ExpenseManagerContract.AccountEntry.TABLE_NAME,
                new String[]{ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO,
                        ExpenseManagerContract.AccountEntry.COLUMN_BANK_NAME,
                        ExpenseManagerContract.AccountEntry.COLUMN_ACC_HOLDER_NAME,
                        ExpenseManagerContract.AccountEntry.COLUMN_BALANCE },
                ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO + "=?",
                new String[]{accountNo}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        else throw new InvalidAccountException(accountNo);
        Account account = new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
        return account;
    }

    /***
     * Add an account to the accounts collection.
     *
     * @param account - the account to be added.
     */
    @Override
    public void addAccount(Account account) throws DuplicateAccountException {
        SQLiteDatabase db = expenseManagerDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO, account.getAccountNo());
        values.put(ExpenseManagerContract.AccountEntry.COLUMN_ACC_HOLDER_NAME, account.getAccountHolderName());
        values.put(ExpenseManagerContract.AccountEntry.COLUMN_BANK_NAME, account.getBankName());
        values.put(ExpenseManagerContract.AccountEntry.COLUMN_BALANCE, account.getBalance());

        if (db.insert(ExpenseManagerContract.AccountEntry.TABLE_NAME, null, values) <= 0){
            throw new DuplicateAccountException("Account already exists for account no : "+account.getAccountNo());
        }
        db.close();
    }

    /***
     * Remove an account from the accounts collection.
     *
     * @param accountNo - of the account to be removed.
     * @throws InvalidAccountException - if the account number is invalid
     */
    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = expenseManagerDbHelper.getWritableDatabase();

        if (db.delete(ExpenseManagerContract.AccountEntry.TABLE_NAME, ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO+"=?",
                new String[]{accountNo}) <= 0){
            throw new InvalidAccountException("Error : "+accountNo);
        }
        db.close();
    }

    /***
     * Update the balance of the given account. The type of the expense is specified in order to determine which
     * action to be performed.
     * <p/>
     * The implementation has the flexibility to figure out how the updating operation is committed based on the type
     * of the transaction.
     *
     * @param accountNo   - account number of the respective account
     * @param expenseType - the type of the transaction
     * @param amount      - amount involved
     * @throws InvalidAccountException - if the account number is invalid
     */
    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException, BalanceNegativeException {
        Account account = getAccount(accountNo);
        if(expenseType == ExpenseType.INCOME){
            amount = account.getBalance() + amount;
        }else{
            amount = account.getBalance() - amount;
        }

        if(amount >= 0){
            account.setBalance(amount);
            SQLiteDatabase db = expenseManagerDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(ExpenseManagerContract.AccountEntry.COLUMN_BALANCE, account.getBalance());
            db.update(ExpenseManagerContract.AccountEntry.TABLE_NAME, values,
                    ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO+" =? ", new String[]{account.getAccountNo()});
        }else{
            throw new BalanceNegativeException("Balance after transaction : "+amount);
        }
    }
}
