package vision.resourcemanager;

import android.util.SparseArray;
import android.widget.Button;

import vis.SelectedFilesQueue;
import vis.UserFile;

/**
 * Created by Vision on 15/7/13.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public interface ResourceManagerInterface {

    byte TYPE_RESOURCE_MANAGER = 1;
    byte TYPE_FILE_TRANSFER = 2;


    int RM_FRAGMENT = 0;
    int SHARE_FRAGMENT = 1;
    int RM_IMAGE_GRID = 2;


//    void onFragmentInteraction(int arg1, int indexOfFolder);

    /**
     * 获取已经选择的文件队列
     *
     * @return 已经选择的文件队列
     */
    SelectedFilesQueue<UserFile> getSelectedFilesQueue();

    /**
     * 获取图片文件夹数据
     *
     * @return 图片文件夹数据
     */
    SparseArray<FileFolder> getImageFolder();

    /**
     * 设置标题栏的文字
     *
     * @param string 要让标题栏显示的文字
     */
    void setTitleText(String string);

    /**
     * 获取标题栏的文字
     *
     * @return 标题栏的文字
     */
    String getTitleText();

    /**
     * 获取标题栏右边的按钮
     *
     * @return 标题栏右边的按钮
     */
    Button getTitleRightBtn();

    /**
     * @param fragment      要跳转到的fragment
     * @param indexOfFolder 携带的信息，要显示的文件夹的序号
     */
    void jumpToFragment(int fragment, int indexOfFolder);

}
