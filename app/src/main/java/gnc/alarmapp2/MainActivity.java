package gnc.alarmapp2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private static MainActivity inst;
    private TextView alarmTextView;
    private Switch soundSwitch;
    private Ringtone ringtone;

    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmTextView = (TextView) findViewById(R.id.alarmText);
        SwitchCompat alarmToggle = (SwitchCompat) findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        soundSwitch = findViewById(R.id.soundSwitch);
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked == true)
                {
                    if(ringtone !=null)
                    {
                        if(ringtone.isPlaying())
                        {
                            ringtone.stop();
                        }
                    }

                }
            }
        });
        soundSwitch.setVisibility(View.GONE);
    }

    public void onToggleClicked(View view) {
        if (((SwitchCompat) view).isChecked()) {
            Log.d("MyActivity", "Alarm On");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            setAlarmText("", null);
            if(ringtone !=null)
            {
                if(ringtone.isPlaying())
                {
                    ringtone.stop();
                }
            }

            Log.d("MyActivity", "Alarm Off");
        }
    }

    public void setAlarmText(String alarmText, Ringtone ringtone) {
        alarmTextView.setText(alarmText);
        if(alarmText.length()==0)
        {
            soundSwitch.setVisibility(View.GONE);
        }
        else
        {
            soundSwitch.setVisibility(View.VISIBLE);
        }
        if(ringtone !=null){
            this.ringtone = ringtone;
            if(alarmText !=null && alarmText.length()>0)
            {
                soundSwitch.setVisibility(View.VISIBLE);
            }
        }
    }
}