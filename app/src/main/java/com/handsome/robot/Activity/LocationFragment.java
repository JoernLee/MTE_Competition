package com.handsome.robot.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.handsome.robot.R;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private TextView tvX;
    private TextView tvY;
    private TextView tvZ;

    private GLSurfaceView viewLocation;

    //OpenGL窗口
    private MyGLSurfaceView mGLView;


    // TODO: Rename and change types of parameters
    private String mParam1;


   /* private OnFragmentInteractionListener mListener;*/

    public LocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment LocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance(String param1) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        Bundle bundle = getArguments();

        viewLocation= (GLSurfaceView) view.findViewById(R.id.view_location_image);

        //如果本设备支持OpenGl ES 2.0
        if (IsSupported()) {
            // 创建渲染器实例
            MyGLRenderer mRenderer = new MyGLRenderer();
          /*  // 设置渲染器
            mGLView.setRenderer(mRenderer);
            //设置渲染模式
            mGLView. setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);*/
            //设置渲染器
            viewLocation.setRenderer(mRenderer);
            //设置渲染模式
            viewLocation.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }

        tvX= (TextView) view.findViewById(R.id.tv_x);
        tvY= (TextView) view.findViewById(R.id.tv_y);
        tvZ= (TextView) view.findViewById(R.id.tv_z);


        return view;
    }

    private boolean IsSupported() {
        ActivityManager activityManager =(ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;
        return supportsEs2;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (viewLocation != null) {
            viewLocation.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewLocation!= null) {
            viewLocation.onResume();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
  /*  public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
