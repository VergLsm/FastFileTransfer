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

import vision.fastfiletransfer.R;
import vision.resmgr.entity.ResFileEntry;

/**
 * 图片列表适配器
 * Created by verg on 15/11/18.
 */
public class ImageFolderAdapter extends RecyclerAdapter<ImageFolderAdapter.ImageFolderViewHolder> {
    private List<ResFileEntry> mData;

    public ImageFolderAdapter(Context context, List<ResFileEntry> data) {
        super(data);
    }

    @Override
    public ImageFolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_image, parent, false);

        return new ImageFolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageFolderViewHolder holder, int position) {
        ResFileEntry file = getData(position);

//        holder.ivCheckBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (file.isSelected) {
//                    file.cancelAll(mSelectedList);
//                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
//                } else {
//                    file.selectAll(mSelectedList);
//                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
//                }
//                notifyDataSetChanged();
//            }
//        });
//        holder.layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rmMainFragment.jumpInFolder(file.id);
//            }
//        });

        holder.name.setText(file.getLabel());
//        holder.size.setText("共" + file.mImages.size() + "个");
//        if (file.selected > 0) {
//            holder.date.setText("已选择" + file.selected + "个");
//        } else {
//            holder.date.setText("");
//        }
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
//            holder.image.setImageResource(R.mipmap.listitem_icon_image);
//            new LoadImage(holder.image, file.oid)
//                    .execute();
//        }
    }

    static class ImageFolderViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        ImageView image;
        TextView name;
        TextView size;
        TextView date;
        ImageView ivCheckBox;

        public ImageFolderViewHolder(View itemView) {
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
