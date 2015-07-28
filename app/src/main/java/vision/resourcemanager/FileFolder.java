package vision.resourcemanager;

import android.util.SparseArray;

import vis.SelectedFilesQueue;
import vis.UserFile;

/**
 * Created by Vision on 15/7/14.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class FileFolder extends UserFile {
    public SparseArray<FileImage> mImages;
    public int selected;

    public void selectAll(SelectedFilesQueue selectedList){
        for (int i = 0, nsize = mImages.size(); i < nsize; i++) {
            FileImage fileImage = mImages.valueAt(i);
            if (selectedList.add(fileImage)) {
                selected++;
                fileImage.isSelected = true;
            }
        }
        isSelected = true;
    }

    public void cancelAll(SelectedFilesQueue selectedList){
        for (int i = 0, nsize = mImages.size(); i < nsize; i++) {
            FileImage fileImage = mImages.valueAt(i);
            if (selectedList.remove(fileImage)) {
                selected--;
                fileImage.isSelected = false;
            }
        }
        isSelected = false;
    }
}
