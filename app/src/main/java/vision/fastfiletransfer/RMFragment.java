package vision.fastfiletransfer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.Iterator;

import vis.OpenFile;
import vis.SelectedFilesQueue;
import vis.UserFile;
import vision.resourcemanager.DeleteFileDialogFragment;
import vision.resourcemanager.FileFolder;
import vision.resourcemanager.FileImage;
import vision.resourcemanager.RMBottomButtonFragment;
import vision.resourcemanager.RMGridFragmentImage;
import vision.resourcemanager.RMMainFragment;
import vision.resourcemanager.ResourceManagerInterface;

public class RMFragment extends Fragment {
    public static final int REQUEST_EVALUATE = 0X110;
    private static final String ARG_PARAM1 = "type";
    private static final String ARG_PARAM2 = "page";

    public View.OnClickListener mShareListener;
    public View.OnClickListener mCancelListener;
    public View.OnClickListener mOpenFileListener;
    public View.OnClickListener mDeleteFileListener;

    private byte type;
    private int page;

    private SelectedFilesQueue<UserFile> mSelectedList;

    private FragmentManager mFragmentManager;
    //    private LinearLayout btnLinearLayout;
    private RMBottomButtonFragment mRmBottomButtonFragment;
    private ResourceManagerInterface mListener;
    private RMMainFragment mRmMainFragment;
    private View mMainContain;
    private View mBtnContain;

//    private ShareFragment mShareFragment;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>用法： RMFragment.newInstance(
     * RMFragment.TYPE_FILE_TRANSFER,
     * RMFragment.PAGE_AUDIO | RMFragment.PAGE_IMAGE | RMFragment.PAGE_APP | RMFragment.PAGE_VIDEO | RMFragment.PAGE_TEXT);
     * </p>
     * <p>另外，在父Activity中必需要声明和实例化一个公共类：SelectedFilesQueue<vision.resourcemanager.File> mSelectedList，以存放用户选择的文件类。</p>
     *
     * @param type 使用类型.
     * @param page 需要显示的页面
     * @return A new instance of fragment RMFragment.
     */
    public static RMFragment newInstance(byte type, int page) {
        RMFragment fragment = new RMFragment();
        Bundle args = new Bundle();
        args.putByte(ARG_PARAM1, type);
        args.putInt(ARG_PARAM2, page);
        fragment.setArguments(args);
        return fragment;
    }

    public RMFragment() {
        // Required empty public constructor
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
        if (getArguments() != null) {
            type = getArguments().getByte(ARG_PARAM1);
            page = getArguments().getInt(ARG_PARAM2);
        }

        mSelectedList = mListener.getSelectedFilesQueue();

        Context context = getActivity();
        if (null != context) {
            mFragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        }
        if (null == mRmBottomButtonFragment) {
            mRmBottomButtonFragment = new RMBottomButtonFragment(this, type);
        }
        if (null == mRmMainFragment) {
            mRmMainFragment = new RMMainFragment(this, type, page, mSelectedList);
        }

        if (ResourceManagerInterface.TYPE_FILE_TRANSFER == type) {
            mShareListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.jumpToFragment(ResourceManagerInterface.SHARE_FRAGMENT, 0);
                }
            };
        } else if (ResourceManagerInterface.TYPE_RESOURCE_MANAGER == type) {
            mOpenFileListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OpenFile.openFile(getActivity(), mSelectedList.getPaths()[0]);
                }
            };
            mDeleteFileListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteFileDialogFragment dialog = new DeleteFileDialogFragment();
                    dialog.setTargetFragment(RMFragment.this, REQUEST_EVALUATE);
                    dialog.show(mFragmentManager, null);
                }
            };
        }

        mCancelListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAll();
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_manager, container, false);
        mMainContain = rootView.findViewById(R.id.mainContain);
        mBtnContain = rootView.findViewById(R.id.btnContain);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mFragmentManager.beginTransaction().add(R.id.mainContain, mRmMainFragment).commit();

        mFragmentManager.beginTransaction().add(R.id.btnContain, mRmBottomButtonFragment).commit();

        //---------------------------------------------------------------------


        mSelectedList.setOnDataChangedListener(new SelectedFilesQueue.OnDataChangedListener() {
            @Override
            public void onAddedListener(int size) {

                if (mBtnContain.getVisibility() == View.GONE) {
                    mBtnContain.setVisibility(View.VISIBLE);
                }

                if (ResourceManagerInterface.TYPE_FILE_TRANSFER == type) {
                    mRmBottomButtonFragment.btnLeft.setText(getText(R.string.share) + "(" + size + ")");
                } else if (ResourceManagerInterface.TYPE_RESOURCE_MANAGER == type) {
                    if (size == 1) {
                        mRmBottomButtonFragment.btnLeft.setText(R.string.open);
                        mRmBottomButtonFragment.btnLeft.setOnClickListener(mOpenFileListener);
                    } else {
                        mRmBottomButtonFragment.btnLeft.setText(R.string.cancel);
                        mRmBottomButtonFragment.btnLeft.setOnClickListener(mCancelListener);
                    }
                    mRmBottomButtonFragment.btnRight.setText(getText(R.string.delete) + "(" + size + ")");
                }
            }

            @Override
            public void onRemovedListener(int size) {
                if (size == 0) {
                    mBtnContain.setVisibility(View.GONE);
                    return;
                }
                if (type == ResourceManagerInterface.TYPE_FILE_TRANSFER) {
                    mRmBottomButtonFragment.btnLeft.setText("分享(" + mSelectedList.size() + ")");
                } else if (ResourceManagerInterface.TYPE_RESOURCE_MANAGER == type) {
                    if (size == 1) {
                        mRmBottomButtonFragment.btnLeft.setText("打开");
                        mRmBottomButtonFragment.btnLeft.setOnClickListener(mOpenFileListener);
                    } else {
                        mRmBottomButtonFragment.btnLeft.setText("取消");
                        mRmBottomButtonFragment.btnLeft.setOnClickListener(mCancelListener);
                    }
                    mRmBottomButtonFragment.btnRight.setText("删除(" + size + ")");
                }
            }
        });
    }

    //接收返回回来的数据
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EVALUATE) {
            int evaluate = data
                    .getIntExtra(DeleteFileDialogFragment.RESPONSE_EVALUATE, 0);
            if (-1 == evaluate) {
                deleteFile();
            }
        }
    }


    public void jumpInFolder(int indexOfFolder) {
        RMGridFragmentImage rmGridFragmentImage = RMGridFragmentImage.newInstance(indexOfFolder, mRmMainFragment.imageFolderAdapter);
        mFragmentManager.beginTransaction().hide(mRmMainFragment).add(R.id.mainContain, rmGridFragmentImage).addToBackStack(null).commit();
    }

    public void cancelAll() {
        for (UserFile file : mSelectedList.data) {
            file.isSelected = false;
        }
        SparseArray<FileFolder> fileFolderSparseArray = mListener.getImageFolder();
        for (int i = 0, nsize = fileFolderSparseArray.size(); i < nsize; i++) {
            fileFolderSparseArray.valueAt(i).selected = 0;
            fileFolderSparseArray.valueAt(i).isSelected = false;
        }
        mSelectedList.clear();
        mBtnContain.setVisibility(View.GONE);
        mRmMainFragment.refreshAll();
    }

    public void deleteFile() {
        boolean delete = false;
        Iterator selectedList = mSelectedList.data.iterator();
        UserFile file;
        while (selectedList.hasNext()) {
            file = (UserFile) selectedList.next();
            File trueFile = new File(file.data);
            if (trueFile.exists() && trueFile.delete()) {
                switch (file.type) {
                    case UserFile.TYPE_IMAGE: {
                        mListener.getImageFolder().valueAt(((FileImage) file).fatherID).mImages.remove(file.id);
                        mListener.getImageFolder().valueAt(((FileImage) file).fatherID).selected--;
                        break;
                    }
                    case UserFile.TYPE_AUDIO: {
                        mRmMainFragment.mFileAudio.remove(file.id);
                        break;
                    }
                    case UserFile.TYPE_VIDEO: {
                        mRmMainFragment.mFileVideo.remove(file.id);
                        break;
                    }
                    case UserFile.TYPE_TEXT: {
                        mRmMainFragment.mFileText.remove(file.id);
                        break;
                    }
                    case UserFile.TYPE_APP: {
                        mRmMainFragment.mFileApp.remove(file.id);
                        break;
                    }
                }
                delete = true;
            }
            selectedList.remove();
            //这里只为刷新界面
            mSelectedList.remove(null);
        }
        if (delete) {
            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
            mRmMainFragment.refreshAll();
        } else {
            Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
        }
    }
}
