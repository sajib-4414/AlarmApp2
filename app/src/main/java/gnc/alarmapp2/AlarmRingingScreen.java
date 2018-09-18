package gnc.alarmapp2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AlarmRingingScreen extends AppCompatActivity {

    private static AlarmRingingScreen inst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing_screen);
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }
}
