package vision.resourcemanager;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import vis.SelectedFilesQueue;
import vis.UserFile;
import vision.fastfiletransfer.R;
import vision.fastfiletransfer.RMAdapter;
import vision.fastfiletransfer.RMFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class RMMainFragment extends Fragment {

    private ViewPager vp;
    private Fragment[] mFragments;
    private AdapterList[] mAdapterLists;
    public SparseArray<FileAudio> mFileAudio;
    public SparseArray<FileVideo> mFileVideo;
    public SparseArray<FileText> mFileText;
    public SparseArray<FileApp> mFileApp;

    private RMAdapter mViewPagerAdapter;
    private TextView[] tab;
    private int pageCount;

    private RMFragment rmFragment;
    private byte type;
    private int page;
    private SelectedFilesQueue<UserFile> mSelectedList;

    public BaseAdapter imageFolderAdapter;

    public RMMainFragment(RMFragment rmFragment, byte type, int page, SelectedFilesQueue<UserFile> mSelectedList) {
        this.rmFragment = rmFragment;
        // Required empty public constructor
        this.type = type;
        this.page = page;
        this.mSelectedList = mSelectedList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT < 11) {
            // 低版本手机兼容
            if ((page & ResourceManagerInterface.PAGE_TEXT) != 0) {
                //去掉PAGE_TEXT位
                page &= ~ResourceManagerInterface.PAGE_TEXT;
            }
        }
        this.pageCount = NumCount2(page);

        mFragments = new Fragment[this.pageCount];
        mAdapterLists = new AdapterList[this.pageCount];

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rm_main, container, false);
        tab = new TextView[this.pageCount];
        for (int i = 0; i < this.pageCount; i++) {
            tab[i] = (TextView) rootView.findViewById(R.id.tab_1 + i);
            tab[i].setOnClickListener(new TxListener(i));
            tab[i].setVisibility(View.VISIBLE);
        }

        vp = (ViewPager)
                rootView.findViewById(R.id.vp);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        for (int i = 0; i < this.pageCount; i++) {
            mFragments[i] = new RMListFragment();
        }

        int pageIndex = 0;
        if ((page & ResourceManagerInterface.PAGE_APP) != 0) {
            mAdapterLists[pageIndex] = new AdapterApp(getActivity(), mSelectedList);
            tab[pageIndex].setText(R.string.apk);
//            mFragments[pageIndex] = new RMGridFragmentApp();
            mFragments[pageIndex] = new RMGridFragment();
            new RefreshAppList(mFragments[pageIndex], mAdapterLists[pageIndex]).execute();
            pageIndex++;
        }
        if ((page & ResourceManagerInterface.PAGE_IMAGE) != 0) {
            mAdapterLists[pageIndex] = new AdapterFolderImage(getActivity(), rmFragment, mSelectedList);
            tab[pageIndex].setText(R.string.img);
            new RefreshImageDirList(mFragments[pageIndex], mAdapterLists[pageIndex]).execute();
            pageIndex++;
        }
        if ((page & ResourceManagerInterface.PAGE_AUDIO) != 0) {
            mAdapterLists[pageIndex] = new AdapterAudio(getActivity(), mSelectedList);
            tab[pageIndex].setText(R.string.audio);
            new RefreshAudioList(mFragments[pageIndex], mAdapterLists[pageIndex]).execute();
            pageIndex++;
        }
        if ((page & ResourceManagerInterface.PAGE_VIDEO) != 0) {
            mAdapterLists[pageIndex] = new AdapterVideo(getActivity(), mSelectedList);
            tab[pageIndex].setText(R.string.video);
            new RefreshVideoList(mFragments[pageIndex], mAdapterLists[pageIndex]).execute();
            pageIndex++;
        }
        if ((page & ResourceManagerInterface.PAGE_TEXT) != 0) {
            mAdapterLists[pageIndex] = new AdapterText(getActivity(), mSelectedList);
            tab[pageIndex].setText(R.string.text);
            new RefreshTextList(mFragments[pageIndex], mAdapterLists[pageIndex]).execute();
        }


        mViewPagerAdapter = new RMAdapter(getFragmentManager(), mFragments);
        vp.setAdapter(mViewPagerAdapter);

        vp.setCurrentItem(0);
        tab[0].setTextColor(getResources().getColor(R.color.tab_text_color_selected));


        //---------------------------------------------------------------------

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mViewPagerAdapter.getCount(); i++) {
                    tab[i].setTextColor(getResources().getColor(R.color.tab_text_color_normal));
                }
                tab[position].setTextColor(getResources().getColor(R.color.tab_text_color_selected));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private class RefreshImageDirList extends AsyncTask<Void, Void, SparseArray<?>> {
        public SparseArray<FileFolder> imagesFolder;
        private Fragment mFragment;
        private AdapterList mAdapterList;

        public RefreshImageDirList(Fragment mFragment, AdapterList adapterList) {
            this.mFragment = mFragment;
            this.mAdapterList = adapterList;
        }

        protected SparseArray<?> doInBackground(Void... params) {
            Context context = getActivity();
            if (null == context) {
                return null;
            }
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Images.ImageColumns._ID,
                            MediaStore.Images.ImageColumns.DATA,
                            MediaStore.Images.ImageColumns.SIZE,
                            MediaStore.Images.ImageColumns.DISPLAY_NAME,
                            MediaStore.Images.ImageColumns.DATE_MODIFIED,
                            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    },
                    null,
                    null,
                    MediaStore.Images.Media.DATE_MODIFIED + " DESC");
            if (cursor.moveToFirst()) {
                imagesFolder = ((ResourceManagerInterface) getActivity()).getImageFolder();
                FileFolder folder;
                FileImage file;
                int folderID = 0;
                String folderName;
                do {
                    file = new FileImage();
                    file.data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    if (!new File(file.data).exists()) {
                        continue;
                    }
                    file.oid = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                    file.type = UserFile.TYPE_IMAGE;
                    file.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                    file.strSize = UserFile.bytes2kb(file.size);
                    file.name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    file.date = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                    file.strDate = UserFile.dateFormat(file.date);
                    folderName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
                    folder = null;
                    //遍历文件夹数组，找到相对应文件夹
                    for (int i = 0, nsize = imagesFolder.size(); i < nsize; i++) {
                        FileFolder fileFolder = imagesFolder.valueAt(i);
                        if (fileFolder.name.equals(folderName)) {
                            folder = fileFolder;
                            break;
                        }
                    }
                    //对应文件夹不存在
                    if (null == folder) {
                        //新建
                        folder = new FileFolder();
                        folder.id = folderID;
                        folder.name = folderName;
                        folder.mImages = new SparseArray<FileImage>();
                        imagesFolder.put(folderID++, folder);
                    }
                    file.id = folder.mImages.size();
                    file.fatherID = folder.id;
                    folder.mImages.put(file.id, file);
                    if (folder.oid == 0) {
                        //设置相册的封面图片
                        folder.oid = folder.mImages.valueAt(0).oid;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            return imagesFolder;
        }

        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
            if (null == sparseArray) {
                return;
            }
            if (null != mAdapterList) {
                imageFolderAdapter = mAdapterList;
                mAdapterList.setData(sparseArray);
            }
            if (null != mFragment) {
                ((ListFragment) mFragment).setListAdapter(mAdapterList);
            }
        }
    }

    private class RefreshAudioList extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<FileAudio> audios;
        private Fragment mFragment;
        private AdapterList mAdapterList;

        public RefreshAudioList(Fragment mFragment, AdapterList adapterList) {
            this.mFragment = mFragment;
            this.mAdapterList = adapterList;
        }

        protected SparseArray<?> doInBackground(Void... params) {
            audios = new SparseArray<FileAudio>();
            Context context = getActivity();
            if (null == context) {
                return null;
            }
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media.DATA,
                            MediaStore.Audio.Media.SIZE,
                            MediaStore.Audio.Media.DISPLAY_NAME,
                            MediaStore.Audio.Media.DATE_ADDED,
                            MediaStore.Audio.Media.DATE_MODIFIED
                    }, null, null, null);
            if (cursor.moveToFirst()) {
                FileAudio file;
                int i = 0;
                do {
                    file = new FileAudio();
//                    fileAudio.id = curAudio.getLong(curAudio.getColumnIndex(MediaStore.Audio.Media._ID));
                    file.id = i;
                    file.data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    if (!new File(file.data).exists()) {
                        continue;
                    }
                    file.type = UserFile.TYPE_AUDIO;
                    file.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                    file.strSize = UserFile.bytes2kb(file.size);
                    file.name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    file.date = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                    file.strDate = UserFile.dateFormat(file.date);
                    this.audios.put(i++, file);
                } while (cursor.moveToNext());
            }
            cursor.close();
            mFileAudio = audios;
            return audios;
        }


        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
            if (null == sparseArray) {
                return;
            }
            if (null != mAdapterList) {
                mAdapterList.setData(sparseArray);
            }
            if (null != mFragment) {
                ((ListFragment) mFragment).setListAdapter(mAdapterList);
            }
        }
    }

    private class RefreshVideoList extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<FileVideo> videos;
        private Fragment mFragment;
        private AdapterList mAdapterList;

        public RefreshVideoList(Fragment mFragment, AdapterList adapterList) {
            this.mFragment = mFragment;
            mAdapterList = adapterList;
        }

        protected SparseArray<?> doInBackground(Void... params) {
            videos = new SparseArray<FileVideo>();
            Context context = getActivity();
            if (null == context) {
                return null;
            }
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Video.Media._ID,
                            MediaStore.Video.Media.DATA,
                            MediaStore.Video.Media.SIZE,
                            MediaStore.Video.Media.DISPLAY_NAME,
                            MediaStore.Video.Media.DATE_MODIFIED
                    },
                    null,
                    null,
                    MediaStore.Video.Media.DATE_MODIFIED + " DESC");
            if (cursor.moveToFirst()) {
                FileVideo file;
                int i = 0;
                do {
                    file = new FileVideo();
                    file.oid = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    file.id = i;
                    file.data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    if (!new File(file.data).exists()) {
                        continue;
                    }
                    file.type = UserFile.TYPE_VIDEO;
                    file.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                    file.strSize = UserFile.bytes2kb(file.size);
                    file.name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    file.date = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                    file.strDate = UserFile.dateFormat(file.date);
                    this.videos.put(i++, file);
                } while (cursor.moveToNext());
            }
            cursor.close();
            mFileVideo = videos;
            return videos;
        }


        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
            if (null == sparseArray) {
                return;
            }
            if (null != mAdapterList) {
                mAdapterList.setData(sparseArray);
            }
            if (null != mFragment) {
                ((ListFragment) mFragment).setListAdapter(mAdapterList);
            }
        }

    }

    /**
     *
     */
    private class RefreshTextList extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<FileText> texts;
        private Fragment mFragment;
        private AdapterList mAdapterList;

        public RefreshTextList(Fragment mFragment, AdapterList adapterList) {
            this.mFragment = mFragment;
            mAdapterList = adapterList;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        protected SparseArray<?> doInBackground(Void... params) {
            texts = new SparseArray<>();
            Context context = getActivity();
            if (null == context) {
                return null;
            }
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Files.getContentUri("external"),
                    new String[]{
                            MediaStore.Files.FileColumns._ID,
                            MediaStore.Files.FileColumns.DATA,
                            MediaStore.Files.FileColumns.SIZE,
                            MediaStore.Files.FileColumns.MIME_TYPE,
                            MediaStore.Files.FileColumns.DATE_MODIFIED
                    },
                    MediaStore.Files.FileColumns.MIME_TYPE + " LIKE ?",
                    new String[]{"text/%"},
                    null);
            if (cursor.moveToFirst()) {
                FileText file;
                int i = 0;
                do {
                    file = new FileText();
//                    file.id = curText.getLong(curText.getColumnIndex(MediaStore.Files.FileColumns._ID));
                    file.id = i;
                    file.data = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                    if (!new File(file.data).exists()) {
                        continue;
                    }
                    file.type = UserFile.TYPE_TEXT;
                    file.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                    file.strSize = UserFile.bytes2kb(file.size);
                    file.name = file.data.substring(file.data.lastIndexOf("/") + 1);
                    file.date = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                    file.strDate = UserFile.dateFormat(file.date);
                    this.texts.put(i++, file);
                } while (cursor.moveToNext());
            }
            cursor.close();
            mFileText = texts;
            return texts;
        }

        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
            if (null == sparseArray) {
                return;
            }
            if (null != mAdapterList) {
                mAdapterList.setData(sparseArray);
            }
            if (null != mFragment) {
                ((ListFragment) mFragment).setListAdapter(mAdapterList);
            }
        }

    }

    private class RefreshAppList extends AsyncTask<Void, Void, SparseArray<?>> {
        SparseArray<FileApp> apps;
        private Fragment mFragment;
        private AdapterList mAdapterList;

        public RefreshAppList(Fragment mFragment, AdapterList adapterList) {
            this.mFragment = mFragment;
            mAdapterList = adapterList;
        }

        protected SparseArray<?> doInBackground(Void... params) {
            apps = new SparseArray<FileApp>();
            Context context = getActivity();
            if (null == context) {
                return null;
            }
            PackageManager packageManager = context.getPackageManager();
            List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);

            FileApp file;

            ApplicationInfo applicationInfo;
            for (int i = 0, j = 0; i < applicationInfos.size(); i++) {
                applicationInfo = applicationInfos.get(i);
                boolean isUserApp = false;
                if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    isUserApp = true;
                } else if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    isUserApp = true;
                }
                if (isUserApp) {
                    file = new FileApp();
                    file.id = i;
                    file.icon = packageManager.getApplicationIcon(applicationInfo);
                    file.name = (String) packageManager.getApplicationLabel(applicationInfo);
                    file.data = applicationInfo.publicSourceDir;
                    if (!new File(file.data).exists()) {
                        continue;
                    }
                    file.type = UserFile.TYPE_APP;
                    file.strSize = UserFile.bytes2kb(new File(file.data).length());
                    file.strDate = file.data.substring(file.data.lastIndexOf("/") + 1);
//                    try {
//                        file.data = getActivity().getPackageManager().getApplicationInfo("vision.fastfiletransfer", 0).sourceDir;
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }
                    apps.put(j++, file);
                }
            }
            mFileApp = apps;
            return apps;
        }

        @Override
        protected void onPostExecute(SparseArray<?> sparseArray) {
            if (null == sparseArray) {
                return;
            }
            if (null != mAdapterList) {
                mAdapterList.setData(sparseArray);
            }
            if (null != mFragment) {
                ((RMGridFragment) mFragment).setGridAdapter(mAdapterList);
            }
        }

    }

    public void refreshAll() {
        for (AdapterList adapterList : mAdapterLists) {
            adapterList.notifyDataSetChanged();
        }
    }


    public class TxListener implements View.OnClickListener {
        private int index = 0;

        public TxListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            vp.setCurrentItem(index);
        }

    }

    public static int NumCount2(int a) {
        int num = 0;
        while (a != 0) {
            a &= (a - 1);
            num++;
        }
        return num;
    }

}
