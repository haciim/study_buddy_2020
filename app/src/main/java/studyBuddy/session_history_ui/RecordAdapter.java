package studyBuddy.session_history_ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        LinearLayout record;
        TextView date;
        TextView name;
        TextView info;
        RecordViewHolder(LinearLayout v) {
            super(v);
            this.record = v;
            this.date = (TextView) v.getChildAt(0);
            LinearLayout inner = (LinearLayout) v.getChildAt(1);
            this.name = (TextView) inner.getChildAt(0);
            this.info = (TextView) inner.getChildAt(1);
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
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_record_entry, parent, false);
        RecordViewHolder vh = new RecordViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        SessionRecord record = this.mDataset[position];

        String date = record.start.getMonth() + "/" + record.start.getDate();
        holder.date.setText(date);

        holder.name.setText(record.name);

        String info = record.getDurationMins() + " Mins";
        holder.info.setText(info);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
