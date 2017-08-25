package com.handsome.robot.Activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handsome.robot.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link NewSpaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewSpaceFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String IMAGE_UNSPECIFIED = "image/*";

    private final int IMAGE_CODE = 1; // 图库
    private final int REQUEST_CODE = 2; // 这里的IMAGE_CODE是自己任意定义的
    public final int TAKE_PHOTO_CODE = 3;//相机RequestCode

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final int PERMISSION_REQUEST_CAMERA = 1;

    public final int TYPE_TAKE_PHOTO = 1;//Uri获取类型判断


    private TextView tvSave;

    private ImageView imagePhoto;

    //三轴距离
    private TextView tvL_1;
    private TextView tvL_2;
    private TextView tvC_2;
    private TextView tvR_1;

    //测试姿态
    private TextView tvImagePosX_1;
    private TextView tvImagePosY_1;
    private TextView tvImagePosZ_1;
    private TextView tvImagePosX_2;
    private TextView tvImagePosY_2;
    private TextView tvImagePosZ_2;

    //标定值和定位值长和宽
    private TextView tvspaceLength_1;
    private TextView tvspaceLength_2;
    private Button btnspaceLength_1;
    private Button btnspaceLength_2;


    private TextView tvspaceWidth_1;
    private TextView tvspaceWidth_2;
    private Button btnspaceWidth_1;
    private Button btnspaceWidth_2;

    private TextView tvspaceHeight_1;
    private TextView tvspaceHeight_2;
    private Button btnspaceHeight_1;
    private Button btnspaceHeight_2;


    private BleUtils sBleUtils;
    private NavigationActivity sNavActivity;
    private DrawerActivity sDrawerActivity;

    //拍照图片文件
    private File mediaFile;
    //图片文件与相关信息
    private TextView tvImageDistance;
    private TextView tvImageName;
    private TextView tvImageTime;
    private TextView tvImageLocation;
    private String imageLeftDistance;
    private String imageCenterDistance;
    private String imageRightDistance;
    //位置
    private Location location;
    private String latitude;
    private String longitude;

    //姿态角度
    private String directionAngle; //方向角，指向东南西北，绕Z轴角度
    private String slantAngle; //倾斜角，手机头部和尾部抬起的角度，绕X轴角度
    private String rotationAngle; //旋转角,手机左右侧抬起的角度，绕Y轴角度
    DecimalFormat decimalFormat=new DecimalFormat(".0");


    //声明一个AlertDialog构造器
    private AlertDialog.Builder builder;


    // TODO: Rename and change types of parameters
    private String mParam1;

    /*private OnFragmentInteractionListener mListener;*/

    public NewSpaceFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SizeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewSpaceFragment newInstance(String param1) {
        NewSpaceFragment fragment = new NewSpaceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_space, container, false);
        Bundle bundle = getArguments();
        //保存文字
        tvSave = (TextView) view.findViewById(R.id.txt_save_image_space);
        tvSave.setOnClickListener(this);
        //照片显示
        imagePhoto = (ImageView) view.findViewById(R.id.iv_image_space);
        //左中右距离
        tvL_1 = (TextView) view.findViewById(R.id.tv_left_distance_space_1);
        tvL_2 = (TextView) view.findViewById(R.id.tv_left_distance_space_2);
        tvC_2 = (TextView) view.findViewById(R.id.tv_center_distance_space_2);
        tvR_1 = (TextView) view.findViewById(R.id.tv_right_distance_space_1);
        //测试姿态
        tvImagePosX_1 = (TextView)view.findViewById(R.id.tv_image_posture_x_space_1);
        tvImagePosY_1 = (TextView)view.findViewById(R.id.tv_image_posture_y_space_1);
        tvImagePosZ_1 = (TextView)view.findViewById(R.id.tv_image_posture_z_space_1);

        tvImagePosX_2 = (TextView)view.findViewById(R.id.tv_image_posture_x_space_2);
        tvImagePosY_2 = (TextView)view.findViewById(R.id.tv_image_posture_y_space_2);
        tvImagePosZ_2 = (TextView)view.findViewById(R.id.tv_image_posture_z_space_2);


        //标定值
        tvspaceLength_1 = (TextView)view.findViewById(R.id.tv_length_space_1);
        tvspaceWidth_1 = (TextView)view.findViewById(R.id.tv_width_space_1);
        tvspaceHeight_1 = (TextView)view.findViewById(R.id.tv_height_space_1);
        btnspaceLength_1 = (Button)view.findViewById(R.id.btn_in_length_space_1);
        btnspaceLength_1.setOnClickListener(this);
        btnspaceWidth_1 = (Button)view.findViewById(R.id.btn_in_width_space_1);
        btnspaceWidth_1.setOnClickListener(this);
        btnspaceHeight_1 = (Button)view.findViewById(R.id.btn_in_height_space_1);
        btnspaceHeight_1.setOnClickListener(this);


        //定位值
        tvspaceLength_2 = (TextView)view.findViewById(R.id.tv_length_space_2);
        tvspaceWidth_2 = (TextView)view.findViewById(R.id.tv_width_space_2);
        tvspaceHeight_2 = (TextView)view.findViewById(R.id.tv_height_space_2);
        btnspaceLength_2 = (Button)view.findViewById(R.id.btn_in_length_space_2);
        btnspaceLength_2.setOnClickListener(this);
        btnspaceWidth_2 = (Button)view.findViewById(R.id.btn_in_width_space_2);
        btnspaceWidth_2.setOnClickListener(this);
        btnspaceHeight_2 = (Button)view.findViewById(R.id.btn_in_height_space_2);
        btnspaceHeight_2.setOnClickListener(this);

        return view;
    }


    //获取图片保存的文件地址和名称
    public Uri getMediaFileUri(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "梅特勒");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        //创建Media File
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HH:mm:ss").format(new Date());
        File mediaFile;
        if (type == TYPE_TAKE_PHOTO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + longitude +"_" + latitude + "_" + timeStamp + ".jpg");
            setMediaFile(mediaFile);
        } else {
            return null;
        }
        return Uri.fromFile(mediaFile);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_save_image_space:
                try {
                    saveToSD(myShot(getActivity()), "/storage/emulated/0/Pictures/梅特勒/", "space-" + getTimeNow() + ".png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_in_length_space_1:

                break;
            case R.id.btn_in_width_space_1:

                break;
            case R.id.btn_in_height_space_1:

                break;
            case R.id.btn_in_length_space_2:

                break;
            case R.id.btn_in_width_space_2:

                break;
            case R.id.btn_in_height_space_2:

                break;
        }
    }

    //保存到手机SD卡，供分享0519
    private void saveToSD(Bitmap bmp, String dirName, String fileName) throws IOException {
        // 判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(dirName);
            // 判断文件夹是否存在，不存在则创建
            if (!dir.exists()) {
                dir.mkdir();
            }

            File file = new File(dirName + fileName);
            // 判断文件是否存在，不存在则创建
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                if (fos != null) {
                    // 第一参数是图片格式，第二个是图片质量，第三个是输出流
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    // 用完关闭
                    fos.flush();
                    fos.close();
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //当前屏幕截图0519
    public Bitmap myShot(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();

        return bmp;
    }

    public String getTimeNow(){
        SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy年MM月dd日    HH:mm:ss     ");
        Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
        String    str    =    formatter.format(curDate);
        return str;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        //针对SD写访问申请的回调
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                /*//权限被授予
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                startActivityForResult(intent, IMAGE_CODE);*/

            } else {
                // Permission Denied
                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        //对于系统相机申请的回调
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限被授予
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoUri = getMediaFileUri(TYPE_TAKE_PHOTO);
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takeIntent, TAKE_PHOTO_CODE);
            }
        } else {
            // Permission Denied
            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
       /* if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        sDrawerActivity = (DrawerActivity) activity;
        sBleUtils = sDrawerActivity.getBleUtils();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bm = null;

        // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口

        ContentResolver resolver = getActivity().getContentResolver();
        //对于图库访问的回调
        if (requestCode == IMAGE_CODE) {
            try {
              /*  bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                imagePhoto.setImageBitmap(ThumbnailUtils.extractThumbnail(bm, 300, 300));  //使用系统的一个工具类，
                // 参数列表为 Bitmap Width,Height  这里使用压缩后显示，否则在华为手机上ImageView 没有显示
                // 显得到bitmap图片
                // imageView.setImageBitmap(bm);*/
                //似乎这三句就够了。下面的cursor总是返回null
                Uri originalUri = data.getData(); // 获得图片的uri
                Bitmap bit = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(originalUri));
                //图片进行缩略显示-防止图片缓存溢出
                imagePhoto.setImageBitmap(ThumbnailUtils.extractThumbnail(bit, 300, 300));

                //下面的似乎不行-指针总是返回空
                /*String[] proj = {MediaStore.Images.Media.DATA};

                // 好像是android多媒体数据库的封装接口，具体的看Android文档
                Cursor cursor = resolver.query(originalUri, proj, null, null, null);

                // 按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                cursor.moveToFirst();
                // 最后根据索引值获取图片路径
                String path = cursor.getString(column_index);

                imagePhoto.setImageBitmap(BitmapFactory.decodeFile(path));*/
            } catch (Exception e) {
                Log.e("TAG-->Error", e.toString());
            }
        }
        //对于拍照行为的回调
        if (requestCode == TAKE_PHOTO_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.hasExtra("data")) {
                        Log.i("URI", "data is not null");
                        Bitmap bitmap = data.getParcelableExtra("data");
                        imagePhoto.setImageBitmap(bitmap);//imageView即为当前页面需要展示照片的控件，可替换
                    }
                } else {
                    Log.i("URI", "Data is null");
                    //获得拍照时候的左中右距离，文件名，拍照时间与经纬度坐标
                    File photoFile = getMediaFile();
                    /*String photoName = photoFile.getName();
                    String photoTime = photoName.split("_")[3].split("\\.")[0];
                    String photoLongitude = photoName.split("_")[1];
                    String photoLatitude = photoName.split("_")[2];*/
                    /*photoName = photoName.substring(0,3) +"_"+ photoTime.substring(9,17)+ ".jpg";*/
                    /*姿态处理*/
                   /* float[] angleValue = sNavActivity.getValues();
*/
                  /*  //方向角-绕Z轴角度，南方为0/360，顺时针增加度数。
                    directionAngle = decimalFormat.format(angleValue[0]+(float)180.00);
                    //倾斜角-绕X轴角度，水平面朝上为180°，面朝下360/0°，手机顶部往上翘起减少，对着你往后转增加度数
                    slantAngle = decimalFormat.format(angleValue[1]+(float)180.00);
                    //旋转角-绕Y轴角度，水平面朝上为180°，面朝下360/0°，手机左侧抬起旋转增加，顺时针增加度数
                    rotationAngle = decimalFormat.format(angleValue[2]+(float)180.00);*/

                   /* //方向角-绕Z轴角度，南方为0/360，顺时针增加度数。
                    directionAngle = decimalFormat.format(angleValue[0]+(float)180.00);
                    //倾斜角-绕X轴角度，水平面朝上为0°，面朝下+-180°，手机顶部往上翘起减少，对着你往后转增加度数
                    slantAngle = decimalFormat.format(angleValue[1]);
                    //旋转角-绕Y轴角度，水平面朝上为0°，面朝下+-180°，手机左侧抬起旋转增加，顺时针增加度数
                    rotationAngle = decimalFormat.format(angleValue[2]);*/



                    //更新UI-LCR Distance
                   /* imageLeftDistance = tvL.getText().toString();
                    imageCenterDistance = tvC.getText().toString();
                    imageRightDistance = tvR.getText().toString();
                    tvImageDistance.setText(imageLeftDistance + "-" + imageCenterDistance + "-" + imageRightDistance);
                    tvImageName.setText(photoName);
                    tvImageTime.setText(photoTime);*/



                    //将照片显示
                    Bitmap bitmap = BitmapFactory.decodeFile(getMediaFile().getPath());
                    imagePhoto.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 250, 200));//imageView即为当前页面需要展示照片的控件，可替换

                    //更新UI-经纬度和姿态
                   /* tvImageLocation.setText(photoLongitude + "-" + photoLatitude);
                    tvImagePosX.setText("X:" + slantAngle + "°" + " ");
                    tvImagePosY.setText("Y:" + rotationAngle + "°" + " ");
                    tvImagePosZ.setText("Z:" + directionAngle + " °" + " ");*/
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);


    }


    public File getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(File mediaFile) {
        this.mediaFile = mediaFile;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public TextView getTvL_1() {
        return tvL_1;
    }

    public TextView getTvL_2() {
        return tvL_2;
    }

    public TextView getTvC_2() {
        return tvC_2;
    }

    public TextView getTvR_1() {
        return tvR_1;
    }

    public TextView getTvImagePosX_1() {
        return tvImagePosX_1;
    }

    public TextView getTvImagePosY_1() {
        return tvImagePosY_1;
    }

    public TextView getTvImagePosZ_1() {
        return tvImagePosZ_1;
    }

    public TextView getTvImagePosX_2() {
        return tvImagePosX_2;
    }

    public TextView getTvImagePosY_2() {
        return tvImagePosY_2;
    }

    public TextView getTvImagePosZ_2() {
        return tvImagePosZ_2;
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
   /* public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
