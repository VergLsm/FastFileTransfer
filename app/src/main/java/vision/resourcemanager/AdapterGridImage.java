package vision.resourcemanager;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import vis.SelectedFilesQueue;
import vision.fastfiletransfer.R;

/**
 * Created by Vision on 15/7/13.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterGridImage extends BaseAdapter {

    private final LayoutInflater inflater;
    protected ContentResolver cr;
    private SparseArray<FileImage> fileImageSparseArray;
    private SelectedFilesQueue mSelectedList;
    private FileFolder mFileFolder;


    public AdapterGridImage(Context context, FileFolder fileFolder, SelectedFilesQueue selectedList) {
        mFileFolder = fileFolder;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cr = context.getContentResolver();
        this.mSelectedList = selectedList;
    }

    @Override
    public int getCount() {
        if (null != fileImageSparseArray) {
            return fileImageSparseArray.size();
        } else {
            return 0;
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.griditem_image, null);
            holder.layout = (RelativeLayout) convertView.findViewById(R.id.grid_image_layout);
            holder.image = (ImageView) convertView.findViewById(R.id.grid_item_iv);
            holder.ivCheckBox = (ImageView) convertView.findViewById(R.id.grid_item_cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final FileImage file = this.fileImageSparseArray.valueAt(position);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.isSelected) {
                    file.isSelected = false;
                    if (mSelectedList.remove(file)) {
                        mFileFolder.selected--;
                        if (mFileFolder.isSelected) {
                            mFileFolder.isSelected = false;
                        }
                    }
                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
                } else {
                    file.isSelected = true;
                    if (mSelectedList.add(file)) {
                        mFileFolder.selected++;
                        if (mFileFolder.selected == mFileFolder.mImages.size()) {
                            mFileFolder.isSelected = true;
                        }
                    }
                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
                }
            }
        });

        if (file.isSelected) {
            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
        } else {
            holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
        }
        holder.image.setImageResource(R.mipmap.listitem_icon_image);
        holder.image.setTag(file.oid);
        new LoadImage(holder.image, file.oid)
                .execute();
        return convertView;
    }

    private class ViewHolder {
        RelativeLayout layout;
        ImageView image;
        ImageView ivCheckBox;
    }

    public void setData(SparseArray<FileImage> sparseArray) {
        this.fileImageSparseArray = sparseArray;
        this.notifyDataSetChanged();
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
        }
    }

}
