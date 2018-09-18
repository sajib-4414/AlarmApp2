package gnc.alarmapp2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AlarmListMainActivity extends AppCompatActivity implements AlarmListRecyclerAdapter.ItemClickListener {

    Button btnAddAlarm;
    AlarmListRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list_main);
        btnAddAlarm = findViewById(R.id.btnAddAlarm);
        btnAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmListMainActivity.this, AlarmAddActivity.class);
                startActivity(intent);
            }
        });


        // set up the RecyclerView
//        RecyclerView recyclerView = findViewById(R.id.rvAlarmList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new AlarmListRecyclerAdapter(this, animalNames);
//        adapter.setClickListener(this);
//        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
