package ca.philipyoung.philssampler.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import ca.philipyoung.philssampler.util.DataSamples;

public class GridArrayAdapter extends ArrayAdapter<DataSamples.DataRecord> {
    private static final String TAG = "GridArrayAdapter";

    private Context mContext;

    public GridArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
