package ydp.weightrecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CompareActivity extends Activity {
	private TextView compareDateText = null;
	private EditText compareDateEdit = null;
	private DatabaseHelper db = null;
	private Cursor myCursor = null;
	private ListView myListView = null;
	private Button addButton = null;
	private Button delButton = null;
	private Button addAllButton = null;
	private SimpleAdapter adpater = null;
	private String minDate = null;
	private String maxDate = null;
	private CustomDatePickerDialog chooseDateDialog = null;

	int year = 0;
	int month = 0;
	int day = 0;

	private List<Map<String, Object>> list;
	private Map<String, Object> map;
	private String country;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		country = getResources().getConfiguration().locale.getCountry();
		setContentView(R.layout.compare);
		compareDateText = (TextView) findViewById(R.id.compareDateText);
		compareDateEdit = (EditText) findViewById(R.id.compareDateEdit);
		addButton = (Button) findViewById(R.id.addbtn);
		delButton = (Button) findViewById(R.id.delbtn);
		addAllButton = (Button) findViewById(R.id.addAllbtn);

		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);

		myListView = (ListView) findViewById(R.id.compare_listView);
		db = new DatabaseHelper(CompareActivity.this);
		myCursor = db.select();
		maxDate = db.getMaxDate();
		minDate = db.getMinDate();
		adpater = new SimpleAdapter(this, getData(), R.layout.weightshow,
				new String[] { DatabaseHelper.FIELD_DATE,
						DatabaseHelper.FIELD_WEIGHT }, new int[] {
						R.id.date_list, R.id.weight_list });
		myListView.setAdapter(adpater);

		chooseDateDialog = new CustomDatePickerDialog(this,
				new OnDateSetListener() {
					public void onDateSet(DatePicker view, int mYear,
							int monthOfYear, int dayOfMonth) {
						compareDateEdit.setText(year + "/" + (monthOfYear + 1));
						year = mYear;
						month = monthOfYear + 1;
					}
				}, year, month - 1, day);
		compareDateEdit.setFocusable(false);
		compareDateEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDateDialog();
			}
		});

		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(compareDateEdit.length() == 0) {
					hintInfo("请输入年和月");
				} else if (db.getMonthWeigh(year, month) < 0.001) {
					hintInfo("无此月份数据");
				} else {
					addData(year + "/" + month,
							String.valueOf(db.getMonthWeigh(year, month)));
					adpater.notifyDataSetChanged();
				}
			}
		});

		addAllButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addAllData();
				adpater.notifyDataSetChanged();
			}
		});

		delButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				list.clear();
				adpater.notifyDataSetChanged();
			}
		});
	}

	private List<Map<String, Object>> getData() {
		list = new ArrayList<Map<String, Object>>();
		addAllData();
		return list;
	}

	private void addData(String date, String weight) {
		map = new HashMap<String, Object>();
		map.put(DatabaseHelper.FIELD_DATE, date);
		map.put(DatabaseHelper.FIELD_WEIGHT, weight);
		list.add(map);
	}

	private void addAllData() {
		if (maxDate == null) {
			return;
		}
		int maxYear, maxMonth, minYear, minMonth, sMonth, eMonth;
		String[] temp;
		String tempWeight;
		maxDate = db.getMaxDate();
		minDate = db.getMinDate();
		temp = maxDate.split("/");
		maxYear = Integer.valueOf(temp[0]);
		maxMonth = Integer.valueOf(temp[1]);
		temp = minDate.split("/");
		minYear = Integer.valueOf(temp[0]);
		minMonth = Integer.valueOf(temp[1]);
		sMonth = minMonth;
		eMonth = maxMonth;
		
		for (int i = maxYear; i >= minYear; i--) {
			if (maxYear != minYear) {
				if (i == maxYear) {
					sMonth = 1;
					eMonth = maxMonth;
				} else if (i == minYear) {
					sMonth = minMonth;
					eMonth = 12;
				} else {
					sMonth = 1;
					eMonth = 12;
				}
			}
			for (int j = eMonth; j >= sMonth; j--) {
				tempWeight = String.valueOf(db.getMonthWeigh(i, j));
				if (tempWeight != "0.0") {
					addData(i + "/" + j, tempWeight);
				}
			}
		}
	}

	private class insertDataOnListener implements OnDateChangedListener {

		@Override
		public void onDateChanged(DatePicker view, int pYear, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			year = pYear;
			month = monthOfYear + 1;
			day = dayOfMonth;
		}
	}

	private void showDateDialog() {
    	final AlertDialog mDialog= new AlertDialog.Builder(CompareActivity.this).create(); 
    	View v = View.inflate(this, R.layout.datedialog, null);
    	mDialog.setView(v); 
    	mDialog.setTitle(year + "年" + month + "月");
    	mDialog.setIcon(android.R.drawable.ic_dialog_info);

    	mDialog.getWindow().setGravity(Gravity.CENTER); 

    	final EditText yearEdit = (EditText)v.findViewById(R.id.yearEdit);
    	final EditText MonthEdit = (EditText)v.findViewById(R.id.monthEdit);
    	
    	Button btnAddYear = (Button)v.findViewById(R.id.btnAddYear);
    	Button btnAddMonth = (Button)v.findViewById(R.id.btnAddMonth);
    	Button btnMusYear = (Button)v.findViewById(R.id.btnMusYear);
    	Button btnMusMonth = (Button)v.findViewById(R.id.btnMusMonth);
    	yearEdit.setText(String.valueOf(year));
    	MonthEdit.setText(String.valueOf(month));
    	btnAddYear.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				year++;
				yearEdit.setText(String.valueOf(year));
				mDialog.setTitle(year + "年" + month + "月");
			}
		});
    	btnMusYear.setOnClickListener(new OnClickListener() {
    		
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			year--;
    			yearEdit.setText(String.valueOf(year));
    			mDialog.setTitle(year + "年" + month + "月");
    		}
    	});
    	btnAddMonth.setOnClickListener(new OnClickListener() {
    		
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			month++;
    			if(month == 13) {
    				month = 1;
    			}
    			MonthEdit.setText(String.valueOf(month));
    			mDialog.setTitle(year + "年" + month + "月");
    		}
    	});
    	btnMusMonth.setOnClickListener(new OnClickListener() {
    		
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
    			month--;
    			if(month == 0) {
    				month = 12;
    			}
    			MonthEdit.setText(String.valueOf(month));
    			mDialog.setTitle(year + "年" + month + "月");
    		}
    	});
    	mDialog.setButton("确定", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				compareDateEdit.setText(year + "/" + month);
			}
		});
    	mDialog.show(); 
    }



	private void hintInfo(String str) {
		Toast.makeText(CompareActivity.this, str, Toast.LENGTH_SHORT).show();
	}
}
