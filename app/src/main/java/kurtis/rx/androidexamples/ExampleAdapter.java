package kurtis.rx.androidexamples;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for mapping a set of example activities to views.
 */
public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ViewHolder> {

    private Context mContext;
    private List<ExampleActivityAndName> mExamples;

    public ExampleAdapter(Context context, List<ExampleActivityAndName> examples) {
        mContext = context;
        mExamples = examples;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(mContext)
                .inflate(R.layout.example_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mNameDisplay.setText(mExamples.get(position).mExampleName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent exampleIntent = new Intent(mContext, mExamples.get(position).mExampleActivityClass);
                mContext.startActivity(exampleIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mExamples.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mNameDisplay;

        public ViewHolder(View itemView) {
            super(itemView);
            mNameDisplay = (TextView) itemView.findViewById(R.id.name_display);
        }
    }
}
