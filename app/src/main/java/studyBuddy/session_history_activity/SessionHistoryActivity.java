package studyBuddy.session_history_activity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studdybuddy.R;

import studyBuddy.util.DataManager;
import studyBuddy.util.PrimaryColorPicker;
import studyBuddy.time_management.SessionRecord;


public class SessionHistoryActivity extends AppCompatActivity
        implements View.OnClickListener {
    private RecyclerView records;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SessionRecord[] sessions;
    private ImageButton homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_history_layout);

        this.records = findViewById(R.id.session_record_recycler);

        this.sessions = DataManager.load(this, SessionRecord[].class);
        if (this.sessions != null) {
            Log.i("session_records", "loaded existing records");

            this.layoutManager = new LinearLayoutManager(this);
            this.records.setLayoutManager(this.layoutManager);

            this.mAdapter = new RecordAdapter(this.sessions);
            this.records.setAdapter(this.mAdapter);

            DividerItemDecoration divider
                = new DividerItemDecoration(records.getContext(), LinearLayoutManager.VERTICAL);
            divider.setDrawable(getDrawable(R.drawable.session_record_divider));
            records.addItemDecoration(divider);
        }

        homeButton = findViewById(R.id.session_history_home_button);
        homeButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Window window = this.getWindow();
        window.setStatusBarColor(PrimaryColorPicker.getDayColorInt(this));
        PrimaryColorPicker.setBackgroundFilter(this, findViewById(R.id.session_history_home_button));

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.sessions != null) {
            DataManager.save(this, this.sessions);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.session_history_home_button:
                this.finish();
                break;
        }
    }
}


