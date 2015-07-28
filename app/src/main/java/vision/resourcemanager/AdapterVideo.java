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
import android.widget.LinearLayout;
import android.widget.TextView;

import vis.SelectedFilesQueue;
import vision.fastfiletransfer.R;

/**
 * Created by Vision on 15/7/1.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class AdapterVideo extends AdapterList {

    private SparseArray<FileVideo> videos;
    private SelectedFilesQueue mSelectedList;
    /**
     * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
     */
    private LruCache<Long, Bitmap> mMemoryCache;

    public AdapterVideo(Context context, SelectedFilesQueue selectedList) {
        super(context);
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
        videos = null;
        mSelectedList = null;
        mMemoryCache = null;
    }

    @Override
    public void setData(SparseArray<?> data) {
        this.videos = (SparseArray<FileVideo>) data;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (null == videos) {
            return 0;
        } else {
            return videos.size();
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
            convertView = inflater.inflate(R.layout.listitem_video, null);
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
        final FileVideo file = this.videos.valueAt(position);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.isSelected) {
                    file.isSelected = false;
                    mSelectedList.remove(file);
                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_off);
                } else {
                    file.isSelected = true;
                    mSelectedList.add(file);
                    holder.ivCheckBox.setImageResource(R.mipmap.listitem_checkbox_on);
                }
            }
        });
        holder.name.setText(file.name);
        holder.size.setText(file.strSize);
        holder.date.setText(file.strDate);
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
        private long origId;
        private Bitmap bm;

        public LoadImage(ImageView iv, long origId) {
            this.iv = iv;
            this.origId = origId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            bm = MediaStore.Video.Thumbnails.getThumbnail(cr, origId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
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

