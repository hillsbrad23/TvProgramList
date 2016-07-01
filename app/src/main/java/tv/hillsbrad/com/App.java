package tv.hillsbrad.com;

import android.app.Application;
import android.content.Context;

/**
 * Created by alex on 6/30/16.
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
