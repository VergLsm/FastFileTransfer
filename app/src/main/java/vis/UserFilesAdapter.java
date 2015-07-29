package vis;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import vis.widget.TextProgress;
import vision.fastfiletransfer.R;

/**
 * 用户列表的数据适配器
 */

public class UserFilesAdapter extends BaseAdapter {

    private FilesList<UserFile> dataList = null;
    private LayoutInflater inflater = null;
    private Context mContext;

    public UserFilesAdapter(Context context, FilesList<UserFile> fileList) {
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.dataList = fileList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
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
            convertView = inflater.inflate(R.layout.listitem_files, null);
            holder.layout = (LinearLayout) convertView
                    .findViewById(R.id.list_item_layout);
            holder.icon = (ImageView) convertView
                    .findViewById(R.id.image);
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);
            holder.tips = (TextView) convertView.findViewById(R.id.app_tips);
            holder.size = (TextProgress) convertView.findViewById(R.id.app_size_progressBar);
            holder.btn = (Button) convertView
                    .findViewById(R.id.download_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 这里position和app.id的值是相等的
//        final UserDevice userDevice = dataList.get(position);
        //这里并不关心key，不能用get(key)
        final UserFile userFile = (UserFile) dataList.valueAt(dataList.size() - 1 - position);
        holder.name.setText(userFile.name);
        Drawable drawable = null;
        switch (userFile.type) {
            case UserFile.TYPE_IMAGE: {
                drawable = mContext.getResources().getDrawable(R.mipmap.listitem_icon_image);
                break;
            }
            case UserFile.TYPE_AUDIO: {
                drawable = mContext.getResources().getDrawable(R.mipmap.listitem_icon_audio);
                break;
            }
            case UserFile.TYPE_VIDEO: {
                drawable = mContext.getResources().getDrawable(R.mipmap.listitem_icon_video);
                break;
            }
            case UserFile.TYPE_TEXT: {
                drawable = mContext.getResources().getDrawable(R.mipmap.listitem_icon_text);
                break;
            }
            case UserFile.TYPE_APP: {
                drawable = mContext.getResources().getDrawable(R.mipmap.listitem_icon_app);
                break;
            }
        }
        if (null != drawable) {
            holder.icon.setImageDrawable(drawable);
        }

        switch (userFile.state) {
            case UserFile.TRANSFER_STATE_NORMAL:
                holder.tips.setText("就绪");
                holder.btn.setVisibility(View.GONE);
                break;
            case UserFile.TRANSFER_STATE_TRANSFERRING:
                holder.size.setVisibility(View.VISIBLE);
                holder.btn.setVisibility(View.GONE);
                holder.tips.setVisibility(View.GONE);
                holder.size.setProgress((int) (userFile.completed * 100 / userFile.size));
                break;
            case UserFile.TRANSFER_STATE_FINISH:
                holder.tips.setText("传输完成");
                holder.tips.setVisibility(View.VISIBLE);
                holder.size.setVisibility(View.GONE);
                holder.btn.setVisibility(View.VISIBLE);
                holder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, userFile.name, Toast.LENGTH_SHORT)
                                .show();
                        FileOperations.openFile(mContext, Environment.getExternalStorageDirectory().getPath() + mContext.getResources().getString(R.string.recFolder) + "/" + userFile.name);
                    }
                });
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
        TextView tips;
        TextProgress size;
        Button btn;
    }

}
