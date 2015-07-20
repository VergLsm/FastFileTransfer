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
//        tvFileName = (TextView) rootview.findViewById(R.id.tvFileName);
//        btnSelectFile = (Button) rootview.findViewById(R.id.btnSelectFile);
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

//        lvDevices.setAdapter(((ShareActivity) context).mFFTService.getAdapter());
        tvName.setText(new String(ReceiveServer.LOCALNAME));
        setAllTheThing();
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((ShareActivity) context).mFFTService.getAdapter().notifyDataSetChanged();
        if (((ShareActivity) context).mSelectedFilesQueue.isEmpty()) {
//            this.tvFileName.setText("没有选择文件");
            this.lvDevices.setVisibility(View.VISIBLE);
            this.rlNobody.setVisibility(View.GONE);
            this.btnSend.setEnabled(false);
            this.btnSend.setText("没有选择文件");
//                    Toast.makeText(context, "没有选择文件", Toast.LENGTH_SHORT)
//                            .show();
        } else {
//            this.tvFileName.setText(
//                    "已选择" + ((ShareActivity) context)
//                            .mSelectedFilesQueue.size() + "个文件"
//            );
            this.rlNobody.setVisibility(View.VISIBLE);
            this.lvDevices.setVisibility(View.GONE);
            this.btnSend.setEnabled(true);
            this.btnSend.setText(
                    "已选择" + ((ShareActivity) context)
                            .mSelectedFilesQueue.size() + "个文件"
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
//        btnSelectFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                showFileChooser();
//            }
//        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] paths = ((ShareActivity) context).mSelectedFilesQueue.getPaths();
//                ((ShareActivity) context).mFFTService.sendFlies(context, paths);
                ((ShareActivity) context).shareService.sendFlies(context, paths);
            }
        });
    }

//    /**
//     * 显示文件选择器
//     */
//    private void showFileChooser() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        try {
//            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(context, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
//        }
//    }

}
