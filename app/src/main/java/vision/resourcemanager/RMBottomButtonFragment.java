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
    private static final String ARG_PARAM1 = "type";

    public Button btnLeft;
    public Button btnRight;
//    private RMFragment rmFragment;
    private byte type;

    public static RMBottomButtonFragment newInstance(byte type) {
        RMBottomButtonFragment fragment = new RMBottomButtonFragment();
        Bundle args = new Bundle();
        args.putByte(ARG_PARAM1, type);
        fragment.setArguments(args);
        return fragment;
    }

    public RMBottomButtonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getByte(ARG_PARAM1);
        }

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

        if (ResourceManagerInterface.TYPE_FILE_TRANSFER == type) {
            btnLeft.setOnClickListener(rmFragment.mShareListener);
            btnRight.setOnClickListener(rmFragment.mCancelListener);
            btnRight.setText("取消");
        } else if (ResourceManagerInterface.TYPE_RESOURCE_MANAGER == type) {
            //这边始终是删除
            btnRight.setOnClickListener(rmFragment.mDeleteFileListener);
        }

    }

}
