package ru.geekbrains.android3_5;

import android.os.Environment;

import com.activeandroid.ActiveAndroid;

import java.io.File;

import io.paperdb.Paper;
import io.realm.Realm;

/**
 * Created by stanislav on 3/15/2018.
 */

public class App extends com.activeandroid.app.Application
{
    private static App instance;

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
        Paper.init(this);

        Realm.init(this);
    }

    public static App getInstance()
    {
        return instance;
    }
}
