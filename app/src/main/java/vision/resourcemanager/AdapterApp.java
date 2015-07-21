package vision.resourcemanager;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vis.SelectedFilesQueue;
import vision.fastfiletransfer.R;

/**
 * Created by Vision on 15/7/9.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterApp extends AdapterList {

    private SparseArray<FileApp> apps;
    private SelectedFilesQueue mSelectedList;

    public AdapterApp(Context context, SelectedFilesQueue selectedList) {
        super(context);
        this.mSelectedList = selectedList;
    }

    @Override
    public void setData(SparseArray<?> data) {
        this.apps = (SparseArray<FileApp>) data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == apps) {
            return 0;
        } else {
            return apps.size();
        }
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
            convertView = inflater.inflate(R.layout.griditem_app, null);
            holder.layout = (RelativeLayout) convertView
                    .findViewById(R.id.list_item_layout);
            holder.icon = (ImageView) convertView.findViewById(R.id.image);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.size = (TextView) convertView.findViewById(R.id.tvSize);
            holder.ivCheckBox = (ImageView) convertView.findViewById(R.id.ivCheckBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final FileApp fileApp = this.apps.valueAt(position);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileApp.isSelected) {
                    fileApp.isSelected = false;
                    mSelectedList.remove(fileApp);
                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
                } else {
                    fileApp.isSelected = true;
                    mSelectedList.add(fileApp);
                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
                }
            }
        });
        holder.icon.setImageDrawable(fileApp.icon);
        holder.name.setText(fileApp.name);
        holder.size.setText(fileApp.strSize);
        if (fileApp.isSelected) {
            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
        } else {
            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
        }
        return convertView;
    }

    /**
     * 暂存变量类
     */
    static class ViewHolder {
        RelativeLayout layout;
        ImageView icon;
        TextView name;
        TextView size;
        ImageView ivCheckBox;
    }

}
