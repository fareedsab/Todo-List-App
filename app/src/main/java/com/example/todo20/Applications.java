package com.example.todo20;

import android.app.Application;
import android.content.Context;

public class Applications extends Application {
    /***********************************************************************************************
     * properties
     **********************************************************************************************/
    private static Applications mInstanceApplication;
    public static Context mContextApplication;
    public  static Boolean isActive=false;
    /***********************************************************************************************
     * methods
     **********************************************************************************************/

    public void onCreate() {
        super.onCreate();
        // set app context
        mContextApplication = getApplicationContext();
    }

    /**
     * retrieve application context
     *
     * @return Context
     */
    public static Context getAppContext() {
        return mContextApplication;
    }


}
