package ydp.weightrecord;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class WeightListActivity extends Activity {

	private DatabaseHelper db = null;
	private Cursor myCursor = null;
	private ListView myListView = null;
	private TextView mTextView = null;
	private int weight_count = 0;
	
	private int _id;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weightlist);
		mTextView = (TextView) findViewById(R.id.text);
		myListView = (ListView) findViewById(R.id.weight_listView);
		db = new DatabaseHelper(WeightListActivity.this);
		myCursor = db.select();
		weight_count = db.dataCount();
		mTextView.setText("当前数据库里存在" + String.valueOf(weight_count) + "条体重记录");
		SimpleCursorAdapter adpater = new SimpleCursorAdapter(this,
				R.layout.weightshow, myCursor, new String[] {
						DatabaseHelper.FIELD_DATE, DatabaseHelper.FIELD_WEIGHT },
				new int[] { R.id.date_list, R.id.weight_list });
		myListView.setAdapter(adpater);
	}
}