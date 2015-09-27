package fi.ptm.sqlitewithloadersexample;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AddHighscoreDialogFragment.DialogListener{

    // This is the Adapter being used to display the list's data.
    private SimpleCursorAdapter adapter;
    // Context Menu for delete
    private final int UPDATE_ID = 0;
    private final int DELETE_ID = 1;
    // list view
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // find list view
        listView = (ListView)  findViewById(R.id.listView);
        // register listView's context menu (to delete row)
        registerForContextMenu(listView);
        // show highscores
        showHighscores();
    }

    // show hs in listview
    private void showHighscores() {
        // Fields from the database (projection)
        String[] from = new String[] { HighscoreDatabase.COLUMN_NAME, HighscoreDatabase.COLUMN_SCORE };
        // Fields on the UI to which we map
        int[] to = new int[] { R.id.name, R.id.score };
        // init loader, call data if needed
        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.list_item, null, from, to, 0);
        // show data in listView
        listView.setAdapter(adapter);
    }

    /** LOADER BELOW THIS ONE **/

    // Creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                HighscoreDatabase.COLUMN_ID,
                HighscoreDatabase.COLUMN_NAME,
                HighscoreDatabase.COLUMN_SCORE};
        CursorLoader cursorLoader = new CursorLoader(
                this,
                HighscoreContentProvider.CONTENT_URI,
                projection, null, null, "score DESC"); // highest score first
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // new data is available, use it
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

    /* insert */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String name, int score) {
        // add a new data
        ContentValues values=new ContentValues(2);
        values.put("name", name);
        values.put("score", score);
        getContentResolver().insert(HighscoreContentProvider.CONTENT_URI, values);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // TODO Auto-generated method stub
    }

    /* delete */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, UPDATE_ID, Menu.NONE, "Update");
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {

            case DELETE_ID:
                //Log.d("DELETE","DELETE");
                String[] args = {String.valueOf(info.id)};
                // url, where, args
                getContentResolver().delete(HighscoreContentProvider.CONTENT_URI, "_id=?",  args);
                break;

            case UPDATE_ID:
                // find layout and name
                RelativeLayout layout = (RelativeLayout) info.targetView;
                TextView textView = (TextView) layout.getChildAt(0); // name is first element
                String name = (String) textView.getText();
                // genereate some score
                int score = (int) Math.floor(Math.random()*10000);
                // generate values
                ContentValues values = new ContentValues(2);
                values.put("name", name);
                values.put("score", score);
                String[] args2 = {String.valueOf(info.id)};
                // uri, content values, string where, args
                getContentResolver().update(HighscoreContentProvider.CONTENT_URI, values, "_id=?", args2);
                break;

        }

        return(super.onOptionsItemSelected(item));
    }

    /** MENU BELOW THIS ONE **/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                AddHighscoreDialogFragment eDialog = new AddHighscoreDialogFragment();
                eDialog.show(getFragmentManager(), "Add a new highscore");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
