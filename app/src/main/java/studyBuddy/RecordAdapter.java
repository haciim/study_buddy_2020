package studyBuddy;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import java.time.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studdybuddy.R;

import studyBuddy.timemanagement.SessionRecord;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {
    private SessionRecord[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class RecordViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView date;
        RecordViewHolder(TextView v) {
            super(v);
            this.date = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    RecordAdapter(SessionRecord[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecordAdapter.RecordViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_view_test, parent, false);
        RecordViewHolder vh = new RecordViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        SessionRecord record = this.mDataset[position];
        Month month = Month.of(record.start.getMonth() + 1);
        String text = "" + month + " " + record.start.getDate()
                + ": " + record.getDurationMins();
        holder.date.setText(text);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
