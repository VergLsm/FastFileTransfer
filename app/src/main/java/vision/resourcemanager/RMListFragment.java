package vision.resourcemanager;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import vision.fastfiletransfer.R;

/**
 * Created by Vision on 15/7/15.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class RMListFragment extends ListFragment {
    private View mEmptyView;

    public RMListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mEmptyView = inflater.inflate(R.layout.list_empty, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView list = getListView();
        list.setVerticalScrollBarEnabled(false);
        if (null != mEmptyView) {
            mEmptyView.setVisibility(View.GONE);
            ((ViewGroup) list.getParent()).addView(mEmptyView);
            list.setEmptyView(mEmptyView);
        }
    }
}
