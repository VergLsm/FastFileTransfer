package vision.resourcemanager;

import android.util.SparseArray;

import vis.SelectedFilesQueue;

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
    SelectedFilesQueue<File> getSelectedFilesQueue();

    SparseArray<FileFolder> getImageFolder();

    /**
     * @param fragment      要跳转到的fragment
     * @param indexOfFolder 携带的信息，要显示的文件夹的序号
     */
    void jumpToFragment(int fragment, int indexOfFolder);

}
