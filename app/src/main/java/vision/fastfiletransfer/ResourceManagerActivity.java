package vision.fastfiletransfer;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import vis.SelectedFilesQueue;
import vis.UserFile;
import vision.resourcemanager.FileFolder;
import vision.resourcemanager.RMFragment;
import vision.resourcemanager.ResourceManager;
import vision.resourcemanager.ResourceManagerInterface;

public class ResourceManagerActivity extends FragmentActivity implements ResourceManagerInterface {

    private TextView tvTitle;
    private Button btnTitleBarRight;

    private FragmentManager fragmentManager;
    private RMFragment mRMFragment;
    private SparseArray<FileFolder> mImagesFolder;
    /**
     * 文件选择队列
     */
    public SelectedFilesQueue<UserFile> mSelectedFilesQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_resource_manager);
        getWindow().setFeatureInt(
                Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_titlebar
        );
        fragmentManager = getSupportFragmentManager();
        jumpToFragment(ResourceManagerInterface.RM_FRAGMENT, 0);

        Button btnTitleBarLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        btnTitleBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                } else {
                    finish();
                }
            }
        });
        tvTitle = (TextView) findViewById(R.id.titlebar_tvtitle);
        tvTitle.setText("资源管理");

        btnTitleBarRight = (Button) findViewById(R.id.titlebar_btnRight);

        if (ResourceManager.checkSDcard(getResources().getString(R.string.recFolder))) {
            // 必须在查找前进行全盘的扫描，否则新加入的图片是无法得到显示的(加入对sd卡操作的权限)
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory() + ""}, null, null);
            } else {
                sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + Environment.getExternalStorageDirectory() + "")));
            }
        } else {
            Toast.makeText(this, "请检查SD卡是否正确安装", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void jumpToFragment(int fragmentType, int indexOfFolder) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        switch (fragmentType) {
            case RM_FRAGMENT: {
                mRMFragment = RMFragment.newInstance(
                        /*RMFragment.TYPE_FILE_TRANSFER,*/
                        ResourceManagerInterface.TYPE_RESOURCE_MANAGER,
                        ResourceManagerInterface.PAGE_AUDIO | ResourceManagerInterface.PAGE_IMAGE /*| RMFragment.PAGE_APP*/ | ResourceManagerInterface.PAGE_VIDEO | ResourceManagerInterface.PAGE_TEXT);
                fragmentTransaction.replace(R.id.rmContain, mRMFragment);
                break;
            }
            default: {
                return;
            }
        }
        fragmentTransaction.commit();
    }

    @Override
    public SelectedFilesQueue<UserFile> getSelectedFilesQueue() {
        if (null == mSelectedFilesQueue) {
            mSelectedFilesQueue = new SelectedFilesQueue<UserFile>();
        }
        return this.mSelectedFilesQueue;
    }

    @Override
    public SparseArray<FileFolder> getImageFolder() {
        if (null == mImagesFolder) {
            mImagesFolder = new SparseArray<FileFolder>();
        }
        return mImagesFolder;
    }

    @Override
    public void setTitleText(String string) {
        this.tvTitle.setText(string);
    }

    @Override
    public String getTitleText() {
        return this.tvTitle.getText().toString();
    }

    @Override
    public Button getTitleRightBtn() {
        return this.btnTitleBarRight;
    }

}
