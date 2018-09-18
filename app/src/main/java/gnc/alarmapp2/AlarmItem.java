package gnc.alarmapp2;

import java.util.ArrayList;

public class AlarmItem {
    int id;
    String label;
    ArrayList<String> whichDaysToRepeat;
    Boolean willRepeat;
    Boolean willVibrate;
    int alarmRingToneResouceId;
    String alarmRingToneName;
}
