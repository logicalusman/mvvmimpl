package com.le.mvvmimpl;


import android.app.Application;
import android.util.Log;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author Usman
 */
public class MVVMImpl extends Application {
    private String TAG = "MVVMImpl";

    @Override
    public void onCreate() {
        Log.i(TAG, "App started");
        super.onCreate();
        initRealm();


    }

    /**
     * Inits Realm
     */
    private void initRealm() {

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("mvvmimpl.realm").build();
        Realm.setDefaultConfiguration(realmConfig);
    }

}
