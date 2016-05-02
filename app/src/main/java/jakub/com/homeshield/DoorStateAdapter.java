package jakub.com.homeshield;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;

import jakub.com.homeshield.model.DoorState;

/**
 * Created by jakub on 1/25/16.
 */
public class DoorStateAdapter extends ArrayAdapter<DoorState> {

    // View lookup cache
    private static class ViewHolder {
        TextView state;
        TextView timestamp;
    }

    public DoorStateAdapter(Context context, ArrayList<DoorState> doorStates) {
        super(context, R.layout.item_doorstate, doorStates);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DoorState ds = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_doorstate, parent, false);
            viewHolder.state = (TextView) convertView.findViewById(R.id.tvmsg);
            viewHolder.timestamp = (TextView) convertView.findViewById(R.id.tvtimestamp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.state.setText(ds.state);
        String state_timestamp = DateFormat.getDateTimeInstance().format(ds.timestamp);
        viewHolder.timestamp.setText(state_timestamp);

        // Return the completed view to render on screen
        return convertView;
    }
}
