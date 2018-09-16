package gnc.alarmapp2;

import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class RingtoneHelper {
    public static Map<String, String> getRingToneList(Context context) {
        RingtoneManager manager = new RingtoneManager(context);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();

        Map<String, String> list = new HashMap<>();
        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String notificationUri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);

            list.put(notificationTitle, notificationUri);
        }

        return list;
    }
    public static Field[] getListOfRawFiles(){
        Field[] fields=R.raw.class.getFields();
//        for(int count=0; count < fields.length; count++){
//            Log.i("Raw Asset: ", fields[count].getName());
//        }
        return fields;
    }
}
