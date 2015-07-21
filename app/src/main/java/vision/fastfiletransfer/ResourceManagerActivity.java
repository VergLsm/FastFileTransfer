package vision.fastfiletransfer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import vis.SelectedFilesQueue;
import vision.resourcemanager.File;
import vision.resourcemanager.FileFolder;
import vision.resourcemanager.RMGridFragmentImage;
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
    public SelectedFilesQueue<File> mSelectedFilesQueue;

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_resource_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        RMFragment.PAGE_AUDIO | RMFragment.PAGE_IMAGE /*| RMFragment.PAGE_APP*/ | RMFragment.PAGE_VIDEO | RMFragment.PAGE_TEXT);
                fragmentTransaction.replace(R.id.rmContain, mRMFragment);
                break;
            }
            case RM_IMAGE_GRID: {
                RMGridFragmentImage rmGridFragmentImage = RMGridFragmentImage.newInstance(indexOfFolder, null);
                fragmentTransaction.hide(mRMFragment);
                fragmentTransaction.add(R.id.rmContain, rmGridFragmentImage);
                fragmentTransaction.addToBackStack(null);
                break;
            }
            default: {
                return;
            }
        }
        fragmentTransaction.commit();
    }

    @Override
    public SelectedFilesQueue<File> getSelectedFilesQueue() {
        if (null == mSelectedFilesQueue) {
            mSelectedFilesQueue = new SelectedFilesQueue<File>();
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
