package vision.resourcemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import vis.SelectedFilesQueue;
import vis.widget.FasterGridView;
import vision.fastfiletransfer.R;

/**
 * Created by Vision on 15/7/13.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterGridImage extends AdapterList {

    private SparseArray<FileImage> fileImageSparseArray;
    private SelectedFilesQueue mSelectedList;
    private FileFolder mFileFolder;
    /**
     * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
     */
    private LruCache<Long, Bitmap> mMemoryCache;

    public AdapterGridImage(Context context, FileFolder fileFolder, SelectedFilesQueue selectedList) {
        super(context);
        mFileFolder = fileFolder;
        this.mSelectedList = selectedList;

        //获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        //给LruCache分配1/8 4M
        mMemoryCache = new LruCache<Long, Bitmap>(mCacheSize) {

            //必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(Long key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

        };

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        fileImageSparseArray = null;
        mSelectedList = null;
        mFileFolder = null;
        mMemoryCache = null;
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
        if (parent instanceof FasterGridView) {
            if (0 == position && ((FasterGridView) parent).isOnMeasure()) {
                return convertView;
            }
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
        holder.image.setTag(file.oid);

        Bitmap bitmap = mMemoryCache.get(file.oid);
        if (null != bitmap) {
            holder.image.setImageBitmap(bitmap);
        } else {
            holder.image.setImageResource(R.mipmap.listitem_icon_image);
            new LoadImage(holder.image, file.oid)
                    .execute();
        }
        return convertView;
    }

    private class ViewHolder {
        RelativeLayout layout;
        ImageView image;
        ImageView ivCheckBox;
    }

    @Override
    public void setData(SparseArray<?> sparseArray) {
        this.fileImageSparseArray = (SparseArray<FileImage>) sparseArray;
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
            mMemoryCache.put(origId, bm);
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
