package vision.fastfiletransfer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Vision on 15/6/30.<br>
 * Email:Vision.lsm.2012@gmail.com
 */
public class RMAdapter extends FragmentPagerAdapter {

    private Fragment[] mFragments;

    public RMAdapter(FragmentManager fm, Fragment[] listFragments) {
        super(fm);
        this.mFragments = listFragments;
    }

    @Override
    public Fragment getItem(int position) {
        try {
            return mFragments[position];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException("No fragment at position " + position);
        }
    }

    @Override
    public int getCount() {
        if (null != mFragments) {
            return mFragments.length;
        } else {
            return 0;
        }
    }

}
