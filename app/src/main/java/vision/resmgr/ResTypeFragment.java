package vision.resmgr;


import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import vision.fastfiletransfer.R;
import vision.resmgr.adapter.AppAdapter;
import vision.resmgr.adapter.AudioAdapter;
import vision.resmgr.adapter.ImageFolderAdapter;
import vision.resmgr.adapter.RecyclerAdapter;
import vision.resmgr.adapter.VideoAdapter;
import vision.resmgr.entity.ResFileEntry;
import vision.resmgr.loader.AppListLoader;
import vision.resmgr.loader.OtherListLoader;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResTypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResTypeFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ResFileEntry>>, SwipeRefreshLayout.OnRefreshListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private byte mType;
    private SwipeRefreshLayout mSrl;
    private RecyclerView mRec;

    private RecyclerAdapter mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param type  Parameter 1.
     * @param title Parameter 2.
     * @return A new instance of fragment ResTypeFragment.
     */
    public static ResTypeFragment newInstance(byte type, @StringRes int title) {
        ResTypeFragment fragment = new ResTypeFragment();
        Bundle args = new Bundle();
        args.putByte(IResMgr.RES_TYPE, type);
        args.putInt(IResMgr.RES_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public ResTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getByte(IResMgr.RES_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_resmgr, container, false);
        mSrl = (SwipeRefreshLayout) rootView.findViewById(R.id.srl);
        mRec = (RecyclerView) rootView.findViewById(R.id.rv);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSrl.setOnRefreshListener(this);
        mSrl.setRefreshing(true);

        RecyclerView.LayoutManager layoutManager = null;
        if ((mType & IResMgr.PAGE_APP) != 0) {
            layoutManager = new GridLayoutManager(getActivity(), 3);
        } else {
            layoutManager = new LinearLayoutManager(getActivity());
        }
        mRec.setLayoutManager(layoutManager);


        if ((mType & IResMgr.PAGE_APP) != 0) {
            mAdapter = new AppAdapter(getContext(), null);
        } else if ((mType & IResMgr.PAGE_IMAGE) != 0) {
            mAdapter = new ImageFolderAdapter(getActivity(), null);
        } else if ((mType & IResMgr.PAGE_AUDIO) != 0) {
            mAdapter = new AudioAdapter(getActivity(), null);
        } else if ((mType & IResMgr.PAGE_VIDEO) != 0) {
            mAdapter = new VideoAdapter(getActivity(), null);
        }
        mRec.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public Loader<List<ResFileEntry>> onCreateLoader(int id, Bundle args) {

        Uri baseUri;
        String[] projection;
        String sortOrder;
        if ((mType & IResMgr.PAGE_APP) != 0) {
            return new AppListLoader(getActivity());
        } else if ((mType & IResMgr.PAGE_IMAGE) != 0) {
            baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.SIZE,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATE_MODIFIED,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
            };
            sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
        } else if ((mType & IResMgr.PAGE_AUDIO) != 0) {
            baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.SIZE,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DATE_ADDED,
                    MediaStore.Audio.Media.DATE_MODIFIED
            };
            sortOrder = MediaStore.Audio.Media.DATE_MODIFIED + " DESC";
        } else if ((mType & IResMgr.PAGE_VIDEO) != 0) {
            baseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DATE_MODIFIED
            };
            sortOrder = MediaStore.Video.Media.DATE_MODIFIED + " DESC";
        } else {
            baseUri = null;
            projection = null;
            sortOrder = null;
        }
        return new OtherListLoader(getActivity(), baseUri, projection, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<List<ResFileEntry>> loader, List<ResFileEntry> data) {

        // Set the new data in the adapter.
        mAdapter.setData(data);
        mSrl.setRefreshing(false);

    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.setData(null);
    }

    @Override
    public void onRefresh() {
        getLoaderManager().initLoader(0, null, this);
    }
}
