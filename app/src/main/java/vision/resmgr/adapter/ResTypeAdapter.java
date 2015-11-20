package vision.resmgr.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import vision.resmgr.IResMgr;
import vision.resmgr.ResTypeFragment;

/**
 * 资源类型管理适配器
 * Created by verg on 15/11/17.
 */
public class ResTypeAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private ResTypeFragment[] mFragments;

    public ResTypeAdapter(Context context, FragmentManager fm, ResTypeFragment[] listFragments) {
        super(fm);
        mContext = context;
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

    @Override
    public CharSequence getPageTitle(int position) {
        if (mFragments[position].getArguments() != null) {
            return mContext.getString(mFragments[position].getArguments().getInt(IResMgr.RES_TITLE));
        } else {
            return null;
        }
    }
}
