package vis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import vis.widget.TextProgress;
import vision.fastfiletransfer.R;

/**
 * 用户列表的数据适配器
 */

public class UserDevicesAdapter extends BaseAdapter {

    private DevicesList<UserDevice> mDevicesList = null;
    private LayoutInflater inflater = null;
    private Context mContext;

    public UserDevicesAdapter(Context context, DevicesList<UserDevice> devicesList) {
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mDevicesList = devicesList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mDevicesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDevicesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater
                    .inflate(R.layout.listitem_devices, null);
            holder.layout = (LinearLayout) convertView
                    .findViewById(R.id.list_item_layout);
            holder.icon = (ImageView) convertView
                    .findViewById(R.id.image);
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);
            holder.sendTips = (TextView) convertView.findViewById(R.id.listitem_devices_tips);
            holder.tips = (TextView) convertView
                    .findViewById(R.id.app_tips);
            holder.size = (TextProgress) convertView
                    .findViewById(R.id.app_size_progressBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 这里position和app.id的值是相等的
//        final UserDevice userDevice = mDevicesList.get(position);
        //这里并不关心key，不能用get(key)
        final UserDevice ud = mDevicesList.valueAt(position);
        holder.name.setText(ud.name);
//        Drawable drawable = mContext.getResources().getDrawable(R.mipmap.app_icon);
//        holder.icon.setImageDrawable(drawable);

        switch (ud.state) {
            case UserDevice.TRANSFER_STATE_NORMAL:
                holder.tips.setText("就绪");
                break;
            case UserDevice.TRANSFER_STATE_TRANSFERRING:
                holder.size.setVisibility(View.VISIBLE);
                holder.tips.setVisibility(View.GONE);
                holder.sendTips.setText("(" + ud.currentFile + "/" + ud.fileTotal + ")");
                holder.size.setProgress(ud.completed);
                break;
            case UserDevice.TRANSFER_STATE_FINISH:
                holder.tips.setText("传输完成");
                holder.tips.setVisibility(View.VISIBLE);
                holder.size.setVisibility(View.GONE);
                break;
        }
        return convertView;
    }

    /**
     * 暂存变量类
     */
    static class ViewHolder {
        LinearLayout layout;
        ImageView icon;
        TextView name;
        TextView sendTips;
        TextView tips;
        TextProgress size;
    }

}
