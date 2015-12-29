package de.devmil.paperlaunch;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import de.devmil.paperlaunch.view.fragments.EditFolderFragment;

public class EditFolderActivity extends Activity {

    private static final String ARG_FOLDERID = "folderId";

    private Toolbar mToolbar;
    private Tracker mTracker;

    public static Intent createLaunchIntent(Context context, long folderId) {
        Intent result = new Intent(context, EditFolderActivity.class);
        result.putExtra(ARG_FOLDERID, folderId);
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_folder);

        mTracker = ((PaperLaunchApp)getApplication()).getDefaultTracker();

        mToolbar = (Toolbar)findViewById(R.id.activity_edit_folder_toolbar);

        setActionBar(mToolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        long folderId = -1;
        if(getIntent().hasExtra(ARG_FOLDERID)) {
            folderId = getIntent().getLongExtra(ARG_FOLDERID, -1);
        }

        EditFolderFragment fragment = null;

        if(savedInstanceState  == null) {
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.add(R.id.activity_edit_folder_folder_fragment, fragment = EditFolderFragment.newInstance(folderId));
            trans.commit();
        } else {
            fragment = (EditFolderFragment)getFragmentManager().findFragmentById(R.id.activity_edit_folder_folder_fragment);
        }

        if(fragment != null) {
            fragment.setListener(new EditFolderFragment.IEditFolderFragmentListener() {
                @Override
                public void onFolderNameChanged(String newName) {
                    setTitle(newName);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Edit folder");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
