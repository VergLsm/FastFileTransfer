package vision.resmgr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import vis.UserFile;
import vision.fastfiletransfer.R;
import vision.resmgr.entity.ResFileEntry;
import vision.resourcemanager.FileVideo;

/**
 * 图片列表适配器
 * Created by verg on 15/11/18.
 */
public class VideoAdapter extends RecyclerAdapter<VideoAdapter.VideoViewHolder> {

    public VideoAdapter(Context context, List<ResFileEntry> data) {
        super(data);
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_video, parent, false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        ResFileEntry file = getData(position);

//        holder.layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (file.isSelected) {
//                    file.isSelected = false;
//                    mSelectedList.remove(file);
//                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
//                } else {
//                    file.isSelected = true;
//                    mSelectedList.add(file);
//                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
//                }
//            }
//        });
        holder.name.setText(file.getLabel());
        holder.size.setText(ResFileEntry.bytes2kb(file.getFile().length()));
        holder.date.setText(ResFileEntry.dateFormat(file.getFile().lastModified()));
//        if (file.isSelected) {
//            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
//        } else {
//            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
//        }

//        holder.image.setTag(file.oid);
//        Bitmap bitmap = mMemoryCache.get(file.oid);
//        if (null != bitmap) {
//            holder.image.setImageBitmap(bitmap);
//        } else {
//            holder.image.setImageResource(R.mipmap.listitem_icon_video);
//            new LoadImage(holder.image, file.oid)
//                    .execute();
//        }
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        ImageView image;
        TextView name;
        TextView size;
        TextView date;
        ImageView ivCheckBox;

        public VideoViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView
                    .findViewById(R.id.list_item_layout);
            image = (ImageView) itemView
                    .findViewById(R.id.image);
            name = (TextView) itemView
                    .findViewById(R.id.name);
            size = (TextView)
                    itemView.findViewById(R.id.tvSize);
            date = (TextView)
                    itemView.findViewById(R.id.tvDate);
            ivCheckBox = (ImageView)
                    itemView.findViewById(R.id.ivCheckBox);
        }
    }
}
