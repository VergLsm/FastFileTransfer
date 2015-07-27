package vision.resourcemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import vision.fastfiletransfer.R;
import vision.fastfiletransfer.ReceiveService;

/**
 * Created by Vision on 15/7/27.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterAP extends BaseAdapter {
    protected LayoutInflater inflater = null;
    private ArrayList<String> mAllAP;
    private Context context;

    public AdapterAP(Context context, ArrayList<String> mAllAP) {
        this.context = context;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mAllAP = mAllAP;
    }

    @Override
    public int getCount() {
        return mAllAP.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.griditem_ap, null);
            holder.layout = (RelativeLayout) convertView
                    .findViewById(R.id.list_item_layout);
            holder.icon = (ImageView) convertView.findViewById(R.id.image);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String ap = this.mAllAP.get(position);
        final String name = ap.substring(5, ap.length() - 6);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
                ((ReceiveService) context).connectAP(ap);
            }
        });
        holder.name.setText(name);
        return convertView;
    }

    private class ViewHolder {
        RelativeLayout layout;
        ImageView icon;
        TextView name;
    }
}
