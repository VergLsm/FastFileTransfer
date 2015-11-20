package vision.resmgr.loader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import vision.resmgr.entity.IResEntry;
import vision.resmgr.entity.ResFileEntry;

/**
 * 其它列表加载器
 * Created by verg on 15/11/19.
 */
public class OtherListLoader extends AsyncTaskLoader<List<ResFileEntry>> {

    private Uri mUri;
    private String[] mProjection;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mSortOrder;

    List<ResFileEntry> mFileEntries;

    public OtherListLoader(Context context, Uri uri, String[] projection, String selection,
                           String[] selectionArgs, String sortOrder) {
        super(context);
        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
    }

    @Override
    public List<ResFileEntry> loadInBackground() {

        Cursor cursor = getContext().getContentResolver().query(mUri, mProjection, mSelection,
                mSelectionArgs, mSortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            mFileEntries = new ArrayList<>(cursor.getCount());
            ResFileEntry file;
            int i = 0;
            do {
                file = new ResFileEntry(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)), IResEntry.TYPE_AUDIO);
                if (!file.getFile().exists()) {
                    continue;
                }
                file.setLabel(cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                mFileEntries.add(file);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return mFileEntries;
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<ResFileEntry> files) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (files != null) {
                onReleaseResources(files);
            }
        }
        List<ResFileEntry> oldApps = mFileEntries;
        mFileEntries = files;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(files);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mFileEntries != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mFileEntries);
        }

        // Start watching for changes in the app data.
//        if (mPackageObserver == null) {
//            mPackageObserver = new PackageIntentReceiver(this);
//        }

        // Has something interesting in the configuration changed since we
        // last built the app list?
//        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

        if (takeContentChanged() || mFileEntries == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<ResFileEntry> apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mFileEntries != null) {
            onReleaseResources(mFileEntries);
            mFileEntries = null;
        }

        // Stop monitoring for changes.
//        if (mPackageObserver != null) {
//            getContext().unregisterReceiver(mPackageObserver);
//            mPackageObserver = null;
//        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<ResFileEntry> apps) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }


}
