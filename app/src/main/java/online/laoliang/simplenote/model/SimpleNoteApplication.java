package online.laoliang.simplenote.model;

import android.app.Application;

import im.fir.sdk.FIR;

/**
 * Created by liang on 12/21.
 */
public class SimpleNoteApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FIR.init(this);
    }
}
