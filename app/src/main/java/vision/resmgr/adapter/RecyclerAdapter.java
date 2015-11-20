package vision.resmgr.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import vision.resmgr.entity.ResFileEntry;

/**
 * Created by verg on 15/11/19.
 */
public abstract class RecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private List<ResFileEntry> mData;

    public RecyclerAdapter(List<ResFileEntry> data) {
        mData = data;
    }

    public void setData(List<ResFileEntry> data) {
        mData = data;
        notifyDataSetChanged();
    }

    protected ResFileEntry getData(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        if (null != mData) {
            return mData.size();
        } else {
            return 0;
        }
    }

}
