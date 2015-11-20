package vision.resmgr.adapter;

import android.content.Context;
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

/**
 * 图片列表适配器
 * Created by verg on 15/11/18.
 */
public class AudioAdapter extends RecyclerAdapter<AudioAdapter.AudioViewHolder> {

    public AudioAdapter(Context context, List<ResFileEntry> data) {
        super(data);
    }

    @Override
    public AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_audio, parent, false);

        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AudioViewHolder holder, int position) {
        ResFileEntry file = getData(position);

//        holder.layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (fileAudio.isSelected) {
//                    fileAudio.isSelected = false;
//                    mSelectedList.remove(fileAudio);
//                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
//                } else {
//                    fileAudio.isSelected = true;
//                    mSelectedList.add(fileAudio);
//                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
//                }
//            }
//        });

        holder.name.setText(file.getLabel());
        holder.size.setText(ResFileEntry.bytes2kb(file.getFile().length()));
        holder.date.setText(ResFileEntry.dateFormat(file.getFile().lastModified()));
//        if (fileAudio.isSelected) {
//            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
//        } else {
//            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
//        }
    }

    static class AudioViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView name;
        TextView size;
        TextView date;
        ImageView ivCheckBox;

        public AudioViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView
                    .findViewById(R.id.list_item_layout);
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
