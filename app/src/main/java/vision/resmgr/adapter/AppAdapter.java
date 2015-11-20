package vision.resmgr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import vis.UserFile;
import vision.fastfiletransfer.R;
import vision.resmgr.entity.ResFileEntry;

/**
 * 应用列表适配器
 * Created by verg on 15/11/18.
 */
public class AppAdapter extends RecyclerAdapter<AppAdapter.AppViewHolder> {

    public AppAdapter(Context context, List<ResFileEntry> data) {
        super(data);
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.griditem_app, parent, false);

        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        ResFileEntry file = getData(position);

//        holder.layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (fileApp.isSelected) {
//                    fileApp.isSelected = false;
//                    mSelectedList.remove(fileApp);
////                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
//                } else {
//                    fileApp.isSelected = true;
//                    mSelectedList.add(fileApp);
////                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
//                }
//            }
//        });
//
        holder.icon.setImageDrawable(file.getIcon());
        holder.name.setText(file.getLabel());
        holder.size.setText(UserFile.bytes2kb(file.getSize()));
//        if (fileApp.isSelected) {
//            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
//        } else {
//            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
//        }
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layout;
        ImageView icon;
        TextView name;
        TextView size;
        ImageView ivCheckBox;

        public AppViewHolder(View itemView) {
            super(itemView);
            layout = (RelativeLayout) itemView
                    .findViewById(R.id.list_item_layout);
            icon = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            size = (TextView) itemView.findViewById(R.id.tvSize);
            ivCheckBox = (ImageView) itemView.findViewById(R.id.ivCheckBox);
        }
    }
}
