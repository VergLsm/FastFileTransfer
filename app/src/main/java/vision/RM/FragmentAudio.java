package vision.RM;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ListAdapter;

public class FragmentAudio extends ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;
//    private ListView lvMic;
//    private AdapterAudio mAdapterAudio;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param adapter Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentAudio.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAudio newInstance(ListAdapter adapter, String param2) {
        FragmentAudio fragment = new FragmentAudio();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, String.valueOf(adapter));
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentAudio() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View rootView = inflater.inflate(R.layout.fragment_discarded_audio, container, false);
//        lvMic = (ListView)
//                rootView.findViewById(R.id.lvMic);
//        return rootView;
//    }
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mAdapterAudio = new AdapterAudio(getActivity());
//        lvMic.setAdapter(mAdapterAudio);
//        new RefreshList().execute();
//    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }


}