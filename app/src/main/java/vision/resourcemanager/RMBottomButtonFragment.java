package vision.resourcemanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import vision.fastfiletransfer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RMBottomButtonFragment extends Fragment {
//    private static final String ARG_PARAM1 = "type";

    private Button btnLeft;
    private Button btnRight;
    private View.OnClickListener btnLeftOnClickListener;
    private int btnRightText;
    private View.OnClickListener btnRightOnClickListener;
    //    private RMFragment rmFragment;
//    private byte type;

    public static RMBottomButtonFragment newInstance(byte type) {
        RMBottomButtonFragment fragment = new RMBottomButtonFragment();
//        Bundle args = new Bundle();
//        args.putByte(ARG_PARAM1, type);
//        fragment.setArguments(args);
        return fragment;
    }

    public RMBottomButtonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            type = getArguments().getByte(ARG_PARAM1);
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bottom_rm_buttom, container, false);
        btnLeft = (Button)
                rootView.findViewById(R.id.btnLeft);
        btnRight = (Button)
                rootView.findViewById(R.id.btnRight);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != btnLeftOnClickListener) {
            btnLeft.setOnClickListener(btnLeftOnClickListener);
        }
        if (0 != btnRightText) {
            btnRight.setText(btnRightText);
        }
        if (null != btnRightOnClickListener) {
            btnRight.setOnClickListener(btnRightOnClickListener);
        }
    }

    public void setBtnLeftOnClickListener(Button.OnClickListener btnOnClickListener) {
        btnLeft.setOnClickListener(btnOnClickListener);
    }

    public void setBtnRightOnClickListener(Button.OnClickListener btnOnClickListener) {
        btnRight.setOnClickListener(btnOnClickListener);
    }

    public void setBtnLeftText(CharSequence text) {
        btnLeft.setText(text);
    }

    public void setBtnLeftText(int resid) {
        btnLeft.setText(resid);
    }

    public void setBtnRightText(CharSequence text) {
        btnRight.setText(text);
    }

    public void setBtnRightText(int resid) {
        btnRight.setText(resid);
    }

    public void setOcBtnLeftOnClickListener(View.OnClickListener onClickListener) {
        this.btnLeftOnClickListener = onClickListener;
    }

    public void setOcBtnRightOnClickListener(View.OnClickListener onClickListener) {
        this.btnRightOnClickListener = onClickListener;
    }

    public void setOcBtnRightText(int resid) {
        this.btnRightText = resid;
    }
}
