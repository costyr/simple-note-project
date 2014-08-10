package simplenote.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import simplenote.activity.R;
import simplenote.db.Note;

/**
 * This custom adapter allows to me to display title and opens more customization possibilities.
 */
public class NotesAdapter extends ArrayAdapter<Note> {

    public NotesAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        Note p = getItem(position);
        if ((p!= null) && p.getTitle().isEmpty())
           return 0;
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        Note p = getItem(position);

        int viewType = ((p != null) && !p.getTitle().isEmpty()) ? R.layout.list_item : R.layout.list_item_notitle;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(viewType, null);

        }

        if (p != null) {

            TextView titleView = (TextView) v.findViewById(R.id.list_item_title);
            TextView textView = (TextView) v.findViewById(R.id.list_item_text);

            if (titleView != null) {
                titleView.setText(p.getTitle());
            }
            if (textView != null) {
                textView.setText(p.toString());
            }
        }

        return v;

    }
}
