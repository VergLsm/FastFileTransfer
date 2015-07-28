package vision.resourcemanager;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.SoftReference;

import vis.SelectedFilesQueue;
import vision.fastfiletransfer.R;
import vision.fastfiletransfer.RMFragment;

/**
 * Created by Vision on 15/6/30.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterFolderImage extends AdapterList {

    private SparseArray<FileFolder> imagesFolder;
    private Context mContext;
    private RMFragment rmFragment;
    private SelectedFilesQueue mSelectedList;
    private SparseArray<SoftReference<Bitmap>> imageCaches;

    public AdapterFolderImage(Context context, RMFragment rmFragment, SelectedFilesQueue selectedList) {
        super(context);
        this.mContext = context;
        this.rmFragment = rmFragment;
        this.mSelectedList = selectedList;
        imageCaches = new SparseArray<SoftReference<Bitmap>>();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        imagesFolder = null;
        mSelectedList = null;
        mContext = null;
        rmFragment = null;
        imageCaches = null;
    }


    @Override
    public void setData(SparseArray<?> data) {
        this.imagesFolder = (SparseArray<FileFolder>) data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == imagesFolder) {
            return 0;
        } else {
            return imagesFolder.size();
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
            holder.size = (TextView)
                    convertView.findViewById(R.id.tvSize);
            holder.date = (TextView)
                    convertView.findViewById(R.id.tvDate);
            holder.ivCheckBox = (ImageView)
                    convertView.findViewById(R.id.ivCheckBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final FileFolder file = this.imagesFolder.valueAt(position);
        holder.ivCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.isSelected) {
                    file.cancelAll(mSelectedList);
                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
                } else {
                    file.selectAll(mSelectedList);
                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
                }
                notifyDataSetChanged();
            }
        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rmFragment.jumpInFolder(file.id);
            }
        });

        holder.name.setText(file.name);
        holder.size.setText("共" + file.mImages.size() + "个");
        if (file.selected > 0) {
            holder.date.setText("已选择" + file.selected + "个");
        } else {
            holder.date.setText("");
        }
        if (file.isSelected) {
            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
        } else {
            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
        }

        holder.image.setTag(file.oid);
        SoftReference<Bitmap> sb = imageCaches.get(position);
        if (null != sb) {
            Bitmap bitmap = sb.get();
            if (null != bitmap) {
                holder.image.setImageBitmap(bitmap);
            }else{
                holder.image.setImageResource(R.mipmap.listitem_icon_image);
                new LoadImage(holder.image, position, file.oid)
                        .execute();
            }
        } else {
            holder.image.setImageResource(R.mipmap.listitem_icon_image);
            new LoadImage(holder.image, position, file.oid)
                    .execute();
        }

        return convertView;
    }

    /**
     * 暂存变量类
     */
    static class ViewHolder {
        LinearLayout layout;
        ImageView image;
        TextView name;
        TextView size;
        TextView date;
        ImageView ivCheckBox;
    }

    private class LoadImage extends AsyncTask<Void, Void, Void> {

        private ImageView iv;
        private int position;
        private long origId;
        private Bitmap bm;

        public LoadImage(ImageView iv, int position, long origId) {
            this.iv = iv;
            this.position = position;
            this.origId = origId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            bm = MediaStore.Images.Thumbnails.getThumbnail(cr, origId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            imageCaches.put(position, new SoftReference<Bitmap>(bm));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (iv.getTag() != null && ((long) iv.getTag()) == origId) {
                iv.setImageBitmap(bm);
            }
        }
    }

}

