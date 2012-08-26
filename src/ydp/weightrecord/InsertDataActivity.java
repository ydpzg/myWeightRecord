package ydp.weightrecord;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.Toast;

public class InsertDataActivity extends Activity {
	private EditText weightEditText = null;
	private DatePicker datePicker = null;
	private Button insertButton = null;
	private Button updateButton = null;
	private Button deleteButton = null;

	int year = 0;
	int month = 0;
	int day = 0;

	private DatabaseHelper db = null;
	private Cursor myCursor = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insertdata);
		weightEditText = (EditText) findViewById(R.id.weight);
		datePicker = (DatePicker) findViewById(R.id.date);
		insertButton = (Button) findViewById(R.id.insert);
		updateButton = (Button) findViewById(R.id.update);
		deleteButton = (Button) findViewById(R.id.delete);

		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);

		datePicker.init(year, month - 1, day, new insertDataOnListener());

		db = new DatabaseHelper(InsertDataActivity.this);

		insertButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!ifLegal()) {
					hintInfo("输入不合法,检查一下吧");
					return;
				}
				String tempDate = year + "/" + month + "/" + day;
				float tempWeight = Float.parseFloat(weightEditText.getText()
						.toString());
				String tempS = db.existData(tempDate);
				if (tempS != null) {
					hintInfo("当前时间已记录了" + tempS + "kg");
					return;
				}
				long temp = db.insert(tempDate, tempWeight);
				if (temp != -1) {
					hintInfo("插入记录成功");
				}
			}
		});
		updateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (weightEditText.getText().length() == 0) {
					return;
				}
				String tempDate = year + "/" + month + "/" + day;
				float tempWeight = Float.parseFloat(weightEditText.getText()
						.toString());
				long temp = db.update(tempDate, tempWeight);
				if (temp == 1) {
					hintInfo("更新记录成功");
				}
			}
		});
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String tempDate = year + "/" + month + "/" + day;
				String tempS = db.existData(tempDate);
				if (tempS != null) {
					long temp = db.delete(tempDate);
					if (temp == 1) {
						hintInfo("删除记录成功");
					}
				} else {
					hintInfo("没有记录可以删除");
				}
			}
		});
	}

	private boolean ifLegal() {
		if (weightEditText.getText().length() == 0) {
			return false;
		}
		String temp = weightEditText.getText().toString();
		boolean tempTwo = false;
		boolean belast = false;
		for (int i = 0; i < temp.length(); i++) {
			belast = false;
			if (!((temp.charAt(i) <= '9' && temp.charAt(i) >= '0') || temp
					.charAt(i) == '.')) {
				return false;
			}
			if (temp.charAt(i) == '.') {
				if (i == 0 || tempTwo) {
					return false;
				}
				tempTwo = true;
				belast = true;
			}
		}
		if (belast) {
			return false;
		}
		return true;
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

	private void hintInfo(String str) {
		Toast.makeText(InsertDataActivity.this, str, Toast.LENGTH_SHORT).show();
	}
}
