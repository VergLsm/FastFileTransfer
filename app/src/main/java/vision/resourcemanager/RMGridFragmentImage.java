package vision.resourcemanager;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import vis.SelectedFilesQueue;
import vis.UserFile;
import vision.fastfiletransfer.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RMGridFragmentImage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RMGridFragmentImage extends Fragment {
    private static BaseAdapter folderAdapter;
    private SelectedFilesQueue<UserFile> mSelectedList;

    private String oldTitleName;
    private Button btnTitleRight;

    private GridView imageGrid;
    private AdapterGridImage mAdapterGridImage;
    private SparseArray<FileImage> mFileImage;

    private ResourceManagerInterface mListener;
    private int folderId;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p/>
     * //     * @param param1 Parameter 1.
     * //     * @param param2 Parameter 2.
     *
     * @return A new instance of fragment RMGridFragmentImage.
     */
    public static RMGridFragmentImage newInstance(int folderId, BaseAdapter folderAdapter) {
        RMGridFragmentImage.folderAdapter = folderAdapter;
        RMGridFragmentImage fragment = new RMGridFragmentImage(folderId);
        return fragment;
    }

    public RMGridFragmentImage(int folderId) {
        this.folderId = folderId;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ResourceManagerInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ResourceManagerInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedList = mListener.getSelectedFilesQueue();
        btnTitleRight = mListener.getTitleRightBtn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_grid_image, container, false);
        imageGrid = (GridView) rootView.findViewById(R.id.imageGrid);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final FileFolder fileFolder = mListener.getImageFolder().get(folderId);
        SparseArray<FileImage> fileImage = fileFolder.mImages;

//        tvTitle.setText(fileFolder.name);
        oldTitleName = mListener.getTitleText();
        mListener.setTitleText(fileFolder.name);
        mAdapterGridImage = new AdapterGridImage(getActivity(), fileFolder, mListener.getSelectedFilesQueue());
        mAdapterGridImage.setData(fileImage);

        imageGrid.setAdapter(mAdapterGridImage);

        btnTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileFolder.isSelected) {
                    fileFolder.cancelAll(mListener.getSelectedFilesQueue());
                } else {
                    fileFolder.selectAll(mListener.getSelectedFilesQueue());
                }
                mAdapterGridImage.notifyDataSetChanged();
            }
        });
        btnTitleRight.setText("全选");
        btnTitleRight.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener.setTitleText(oldTitleName);
        folderAdapter.notifyDataSetChanged();
        btnTitleRight.setVisibility(View.GONE);
    }

}
