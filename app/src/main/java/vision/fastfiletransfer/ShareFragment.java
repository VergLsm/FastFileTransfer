package vision.fastfiletransfer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vis.DevicesList;
import vis.UserDevice;
import vis.UserDevicesAdapter;
import vis.net.protocol.ReceiveServer;


public class ShareFragment extends Fragment {

//    private static final int FILE_SELECT_CODE = 55;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

//    private ResourceManagerInterface mListener;

    private Context context;
    //    private String filePath;
//    private TextView tvFileName;
//    private Button btnSelectFile;
    private Button btnSend;
    private TextView tvName;
//    private ImageView pb;
    private ListView lvDevices;
    private RelativeLayout rlNobody;
    private DevicesList<UserDevice> mDevicesList;
    private UserDevicesAdapter mUserDevicesAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShareFragment.
     */
    public static ShareFragment newInstance(String param1, String param2) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ShareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_share, container, false);
        tvName = (TextView) rootview.findViewById(R.id.tvTips);
        lvDevices = (ListView) rootview.findViewById(R.id.lvDevices);
//        pb = (ImageView) rootview.findViewById(R.id.fragment_share_pb);
        rlNobody = (RelativeLayout)
                rootview.findViewById(R.id.rlNobody);
        btnSend = (Button) rootview.findViewById(R.id.btnSend);
        return rootview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDevicesList = ((ShareActivity) context).mDevicesList;
        mUserDevicesAdapter = new UserDevicesAdapter(context, mDevicesList);
        lvDevices.setAdapter(mUserDevicesAdapter);

        //这里实现一个接口监听是ListView数据是否有变化，然后做相对应的改变
        mDevicesList.setOnDataChangedListener(new DevicesList.OnDataChangedListener() {
            @Override
            public void onAdded(int size) {
                lvDevices.setVisibility(View.VISIBLE);
                rlNobody.setVisibility(View.GONE);
            }

            @Override
            public void onDataChanged() {
                mUserDevicesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRemoved(int size) {
                if (size == 0) {
                    lvDevices.setVisibility(View.GONE);
                    rlNobody.setVisibility(View.VISIBLE);
                }
            }
        });

        tvName.setText(new String(ReceiveServer.LOCALNAME));
//        pb.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.my_rotate));
        setAllTheThing();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (0 == mDevicesList.size()) {
            this.rlNobody.setVisibility(View.VISIBLE);
            this.lvDevices.setVisibility(View.GONE);

        } else {
            this.lvDevices.setVisibility(View.VISIBLE);
            this.rlNobody.setVisibility(View.GONE);

        }
        if (((ShareActivity) context).mSelectedFilesQueue.isEmpty()) {
            this.btnSend.setEnabled(false);
            this.btnSend.setText("没有选择文件");
        } else {
            this.btnSend.setEnabled(true);
            this.btnSend.setText(
                    "轻触这里发送(" + ((ShareActivity) context)
                            .mSelectedFilesQueue.size() + ")"
            );
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    /**
     * set好所有的东西
     */
    private void setAllTheThing() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ShareActivity) context).shareService.sendFlies();
            }
        });
    }


}
