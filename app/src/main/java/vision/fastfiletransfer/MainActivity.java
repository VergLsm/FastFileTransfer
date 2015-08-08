package vision.fastfiletransfer;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import vision.resourcemanager.ResourceManager;

public class MainActivity extends FragmentActivity {

    private ImageButton btnShare;
    private ImageButton btnReceive;
//    private TextView tvModel;
    private TextView tvInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        LayoutInflater inflater = getLayoutInflater();
        View rootView = inflater.inflate(R.layout.activity_titlebar, null, false);
        RelativeLayout top = (RelativeLayout) findViewById(R.id.activity_main_top);
        top.addView(rootView);
        Button btnTitleBarLeft = (Button) rootView.findViewById(R.id.titlebar_btnLeft);
        TextView tvTitle = (TextView) rootView.findViewById(R.id.titlebar_tvtitle);
        tvTitle.setText("文件快传");

//        tvModel = (TextView) findViewById(R.id.tvModel);
        btnShare = (ImageButton) findViewById(R.id.btnShare);
        btnReceive = (ImageButton) findViewById(R.id.btnReceive);
        tvInvite = (TextView) findViewById(R.id.tvInvite);
//        tvModel.setText("本机: " + android.os.Build.MODEL.replaceAll("\\s|-", ""));
//        Log.d("SSID:", android.os.Build.MODEL.replaceAll("\\s|-", ""));

        btnTitleBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(MainActivity.this, ShareActivity.class);
                shareIntent.putExtra("hasSDcard", ResourceManager.checkSDcard(getResources().getString(R.string.recFolder)));
                startActivity(shareIntent);
            }
        });
        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ResourceManager.checkSDcard(getResources().getString(R.string.recFolder))) {
                    Intent receiveIntent = new Intent(MainActivity.this, ReceiveActivity.class);
                    startActivity(receiveIntent);
                } else {
                    Toast.makeText(MainActivity.this, "请检查SD卡是否正确安装", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickShare();
//                Toast.makeText(MainActivity.this, "分享些啥？", Toast.LENGTH_SHORT)
//                        .show();
            }
        });
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory() + getResources().getString(R.string.recFolder)}, null, null);
        } else {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory() + getResources().getString(R.string.recFolder))));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClickShare() {
        String share = "http://wzapi27.yidont.com/mobilem/app/app_v2/rule/register.php?uid=1&c=2345";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "【亿动手机助手】\n 打电话不要钱,wifi免费连,你下载,我送钱！" + share + "(请用浏览器打开下载安装)");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "请选择"));

    }
}
