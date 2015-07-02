package vision.RM;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import vision.fastfiletransfer.R;
import vision.fastfiletransfer.ShareActivity;

/**
 * Created by Vision on 15/6/30.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterImage extends AdapterList {

    private SparseArray<Image> images;
    private Context context;

    public AdapterImage(Context context, FragmentImage.OnRMFragmentListener mListener) {
        super(context);
        this.context = context;
    }

    @Override
    void setData(SparseArray<?> data) {
        this.images = (SparseArray<Image>) data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == images) {
            return 0;
        } else {
            return images.size();
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
            convertView = inflater.inflate(R.layout.listitem_image, null);
            holder.layout = (LinearLayout) convertView
                    .findViewById(R.id.list_item_layout);
            holder.image = (ImageView) convertView
                    .findViewById(R.id.image);
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);
            holder.checkBox = (CheckBox)
                    convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.image.setImageResource(R.mipmap.explorer_c_icon_image_p);
        }

        final Image image = this.images.get(position);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                image.isSelected = isChecked;
                if (isChecked) {
                    ((ShareActivity) context).mTransmissionQueue.add(image);
                } else {
                    ((ShareActivity) context).mTransmissionQueue.remove(image);
                }
            }
        });
        holder.name.setText(image.name);
        holder.checkBox.setChecked(image.isSelected);
//        Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(cr, image.id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
//        holder.image.setImageBitmap(bm);
        holder.image.setTag(image.id);
        new LoadImage(holder.image, image.id)
                .execute();
        return convertView;
    }

    /**
     * 暂存变量类
     */
    static class ViewHolder {
        LinearLayout layout;
        ImageView image;
        TextView name;
        CheckBox checkBox;
    }

    private class LoadImage extends AsyncTask<Void, Void, Void> {

        private ImageView iv;
        private long origId;
        private Bitmap bm;

        public LoadImage(ImageView iv, long origId) {
            this.iv = iv;
            this.origId = origId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            bm = MediaStore.Images.Thumbnails.getThumbnail(cr, origId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (iv.getTag() != null && ((long) iv.getTag()) == origId) {
                iv.setImageBitmap(bm);
            }
//            super.onPostExecute(aVoid);
        }
    }

}

class Image {
    public long id;
    public String data;
    public String name;
    public boolean isSelected;
}