package vision.resourcemanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;

/**
 * 删除确认对话框<br>
 * Created by Vision on 15/7/24.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class DeleteFileDialogFragment extends DialogFragment {
    public static final String RESPONSE_EVALUATE = "response_evaluate";

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle("确认删除吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        setResult(which);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                });
        return builder.create();
    }

    // 设置返回数据
    protected void setResult(int which) {
        // 判断是否设置了targetFragment
        if (getTargetFragment() == null)
            return;

        Intent intent = new Intent();
        intent.putExtra(RESPONSE_EVALUATE, which);
        getTargetFragment().onActivityResult(RMFragment.REQUEST_EVALUATE,
                Activity.RESULT_OK, intent);

    }

}
