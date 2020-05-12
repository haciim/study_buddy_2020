package studyBuddy;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.studdybuddy.R;

public class TestActivity extends AppCompatActivity
    implements View.OnClickListener {
    private TextView stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        stop = findViewById(R.id.stop_session);
        stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stop_session:
                this.finish();
                break;
        }
    }
}
