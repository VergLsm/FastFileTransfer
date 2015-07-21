package vision.fastfiletransfer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiveScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiveScanFragment extends Fragment {
    private TextView tvTips;

    public static ReceiveScanFragment newInstance() {
        return new ReceiveScanFragment();
    }

    public ReceiveScanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_receive_scan, container, false);
        tvTips = (TextView) rootView.findViewById(R.id.tvTips);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 通知界面更新
     *
     * @param text 更新信息
     */
    public void setTips(CharSequence text) {
        if (tvTips != null) {
            tvTips.setText(text);
        }
    }

}
