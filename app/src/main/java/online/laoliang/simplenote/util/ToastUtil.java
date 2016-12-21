package online.laoliang.simplenote.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 自定义Toast弹出消息，完美实现快速切换
 * Created by liang on 12/18.
 */
public class ToastUtil {

    private static Toast toast;

    public static void showToast(Context context, String msg, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, duration);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void showToast(Context context, int resId, int duration) {
        showToast(context, context.getString(resId), duration);
    }

}