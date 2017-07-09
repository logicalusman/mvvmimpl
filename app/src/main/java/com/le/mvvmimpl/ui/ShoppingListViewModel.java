package com.le.mvvmimpl.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.util.Log;

import com.le.mvvmimpl.db.DBUtils;
import com.le.mvvmimpl.db.ShoppingItem;

import io.realm.Realm;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.List;

public class ShoppingListViewModel extends AndroidViewModel {

    private String TAG = "ShoppingListViewModel";
    private Realm mRealm;
    private PublishSubject<List<ShoppingItem>> mShoppingListUpdates;

    public ShoppingListViewModel(Application application) {
        super(application);
        mRealm = Realm.getDefaultInstance();
        DBUtils.mayInsertDummyShoppingItems();
        mShoppingListUpdates = PublishSubject.create();
    }

    public Observable<List<ShoppingItem>> shoppingListUpdates() {
        return mShoppingListUpdates;
    }


    public List<ShoppingItem> getShoppingItems() {
        return mRealm.where(ShoppingItem.class).findAllSorted(ShoppingItem.COLUMN_DATE_TIME);
    }

    public void deleteShoppingItem(ShoppingItem item) {
        mRealm.beginTransaction();
        mRealm.where(ShoppingItem.class).equalTo(ShoppingItem.COLUMN_ID, item.getId()).findFirst().deleteFromRealm();
        mRealm.commitTransaction();
        mShoppingListUpdates.onNext(getShoppingItems());
    }


    @Override
    protected void onCleared() {
        Log.i(TAG, "onCleared");
        super.onCleared();
        mRealm.close();
        mRealm = null;
    }
}
