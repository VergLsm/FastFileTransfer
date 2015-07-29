package vision.fastfiletransfer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import vis.FilesList;
import vis.UserFile;


/**
 * 接收
 */
public class ReceiveActivity extends FragmentActivity {

    private FragmentManager mFragmentManager;
    public FilesList<UserFile> mFilesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_receive);
        //--------------------------------------------------------------------
        getWindow().setFeatureInt(
                Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_titlebar
        );

        Button btnTitleBarLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        TextView tvTitle = (TextView) findViewById(R.id.titlebar_tvtitle);
        tvTitle.setText("我要接收");
        //---------------------------------------------------------------------------

        if (null == mFragmentManager) {
            mFragmentManager = getSupportFragmentManager();
        }

        btnTitleBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragmentManager.getBackStackEntryCount() > 0) {
                    mFragmentManager.popBackStack();
                } else {
                    finish();
                }
            }
        });

        if (null == mFilesList) {
            mFilesList = new FilesList<UserFile>();
        }
        Intent intent = new Intent(ReceiveActivity.this, ReceiveService.class);
        bindService(intent, serConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReceiveService.sendLogout();
        unbindService(serConn);
        sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + Environment.getExternalStorageDirectory() + getResources().getString(R.string.recFolder))));

    }

    private ReceiveService mReceiveService;
    private ServiceConnection serConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.d("ServiceConnection", "onServiceConnected()");
            mReceiveService = ((ReceiveService.ReceiveBinder) service).getService();
            mReceiveService.setActivity(ReceiveActivity.this);
            mReceiveService.setFilesList(mFilesList);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mReceiveService = null;
        }
    };


}
