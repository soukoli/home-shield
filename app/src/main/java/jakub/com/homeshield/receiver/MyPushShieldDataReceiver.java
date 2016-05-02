package jakub.com.homeshield.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import jakub.com.homeshield.MainActivity;
import jakub.com.homeshield.R;
import jakub.com.homeshield.helper.NotificationUtils;

public class MyPushShieldDataReceiver extends ParsePushBroadcastReceiver {

    public static final String PARSE_DATA_KEY = "com.parse.Data";
    private static final String TAG = MyPushShieldDataReceiver.class.getSimpleName();;

    private Intent parseIntent;

    private NotificationUtils notificationUtils;

    public MyPushShieldDataReceiver() {
        super();
    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        // deactivate standard notification
        return null;
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        //super.onPushReceive(context, intent);

        if (intent == null)
            return ;

        String jsonData = intent.getExtras().getString(PARSE_DATA_KEY);
        parseIntent = intent;
        JSONObject data = getData(jsonData);

        String state = null;
        if (data != null) {
            if (data.has("state")) {
                try {


                    state =  data.getString("state");

                } catch (JSONException e) {
                    Log.e(TAG, "Push message json exception: " + e.getMessage());
                }
                if (state.equals("open")) {

                    Intent resultIntent = new Intent(context, MainActivity.class);

                    //intent.putExtras(parseIntent.getExtras());

                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    notificationUtils = new NotificationUtils(context);
                    notificationUtils.showNotificationMessage("Home Shield", "Main door open for more than 10 sec", "open", resultIntent);

                    //pushOpenNotification(context);
                } else if (state.equals("closed")) {
                    Intent resultIntent = new Intent(context, MainActivity.class);
                    notificationUtils = new NotificationUtils(context);
                    notificationUtils.showNotificationMessage("Home Shield", "Main door open for more than 10 sec", "closed", resultIntent);

                }
            }
        }
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }




    private void pushOpenNotification(Context context) {
        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Add custom intent
        Intent cIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, cIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create custom notification
        NotificationCompat.Builder  builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText("Main door open for more than 2 min !!!")
                .setContentTitle("Home Shield")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // OPTIONAL create soundUri and set sound:
        builder.setSound(soundUri);
        builder.setVibrate(new long[] { 0, 100, 1000, 300});

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify("MyTag", 0, builder.build());
    }

    private JSONObject getData(String jsonData) {
        // Parse JSON Data
        try {
            Log.e(TAG, "Push received: " + jsonData);
            JSONObject obj = new JSONObject(jsonData);

            return obj.getJSONObject("alert");
        }
        catch(JSONException jse) {
            jse.printStackTrace();
        }

        return null;
    }

}