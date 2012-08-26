package ydp.weightrecord;

import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.Toast;



public class MainActivityGroup extends ActivityGroup implements
		CompoundButton.OnCheckedChangeListener {

	private ViewGroup container = null;
	private LocalActivityManager localActivityManager = null;

	private static final String ACTIVITY_WEIGHTLIST = "weightListActivity";
	private static final String ACTIVITY_CHART = "chartActivity";
	private static final String ACTIVITY_INSERTDATA = "insertDataActivity";
	private static final String ACTIVITY_Compare = "compareActivity";

	protected final static int MENU_BACKUP = 1;
	protected final static int MENU_RESTORE = 2;
	protected final static int MENU_CLEAR = 3;
	protected final static int MENU_ABOUT = 4;
	protected final static int MENU_QUIT = 5;
	
	private DatabaseHelper db = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ȫ����ʾ����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		((RadioButton) findViewById(R.id.radio_button0)).setChecked(true);
		initRadioBtns();
		localActivityManager = getLocalActivityManager();
		container = getContainer();
		setContainerView(ACTIVITY_INSERTDATA, new Intent(
				MainActivityGroup.this, InsertDataActivity.class));
		db = new DatabaseHelper(MainActivityGroup.this);
	}

	private ViewGroup getContainer() {
		return (ViewGroup) findViewById(R.id.container);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MENU_BACKUP, 0, R.string.BACKUP);
		menu.add(Menu.NONE, MENU_RESTORE, 0, R.string.RESTORE);
		menu.add(Menu.NONE, MENU_CLEAR, 0, R.string.CLEAR);
		menu.add(Menu.NONE, MENU_ABOUT, 0, R.string.ABOUT);
		menu.add(Menu.NONE, MENU_QUIT, 0, R.string.QUIT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_BACKUP:
			operation("backup");
			break;
		case MENU_RESTORE:
			operation("restore");
			break;
		case MENU_CLEAR:
			operation("clear");
			break;
		case MENU_ABOUT:
			operation("about");
			break;
		case MENU_QUIT:
			operation("quit");
			break;
		default:
			break;
		}
		return true;
	}

	private void initRadioBtns() {
		initRadioBtn(R.id.radio_button0);
		initRadioBtn(R.id.radio_button1);
		initRadioBtn(R.id.radio_button2);
		initRadioBtn(R.id.radio_button3);
	}

	private void initRadioBtn(int id) {
		RadioButton btn = (RadioButton) findViewById(id);
		btn.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {

			case R.id.radio_button0:
				setContainerView(ACTIVITY_INSERTDATA, new Intent(
						MainActivityGroup.this, InsertDataActivity.class));
				break;
			case R.id.radio_button1:
				setContainerView(ACTIVITY_WEIGHTLIST, new Intent(
						MainActivityGroup.this, WeightListActivity.class));
				break;
			case R.id.radio_button2:
				if(db.isEmpty()){
					container.removeAllViews();
					hintInfo("���ݿ���û������");
				}else {
					setContainerView(ACTIVITY_CHART, new ShowChart(
							MainActivityGroup.this).getIntent());
				}
				break;
			case R.id.radio_button3:
				setContainerView(ACTIVITY_Compare, new Intent(
						MainActivityGroup.this, CompareActivity.class));
				break;

			default:
				break;
			}
		}
	}

	private void setContainerView(String activityName, Intent intent) {

		// �Ƴ����ݲ���ȫ����View
		container.removeAllViews();

		Activity contentActivity = localActivityManager
				.getActivity(activityName);
		if (activityName == ACTIVITY_WEIGHTLIST
				|| activityName == ACTIVITY_CHART || null == contentActivity) {
			localActivityManager.startActivity(activityName,
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		}

		// ����Activity
		container.addView(localActivityManager.getActivity(activityName)
				.getWindow().getDecorView(), new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	private void operation(String cmd) {

		if (cmd == "backup") {
			dataBackup();
		} else if (cmd == "restore") {
			dataRecover();
		} else if (cmd == "clear") {
			if(db.isEmpty()) {
				hintInfo("��ǰ���ݿ��ǿյ�");
			}else {
				showAllDialog();	
			}		
		} else if (cmd == "about") {
			showAbout();
		} else if (cmd == "quit") {
			MainActivityGroup.this.finish();
			System.exit(0);
		}
	}

	private void showAllDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityGroup.this);
		builder.setTitle("�Ƿ�������ݿ⣿");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage("�˲������ɳ��������ǰ�뿼�Ǳ��ݲ�������ֹ��ɾ��");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int temp = db.deleteAll();
				if(temp != 0){
					hintInfo("���ݿ��Ѿ����");
				}
			}
		});
		builder.setNegativeButton("ȡ��", null);
		builder.create().show();
	}
	
	
	@SuppressLint("NewApi")
	private void dataRecover() {
		// TODO Auto-generated method stub
		int temp = 0;
		AsyncTask<String, Void, Integer> callBack = new BackupTask(this).execute("restoreDatabase");
		try {
			temp = callBack.get();			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(temp == 21) {
			hintInfo("���ݿ�ָ��ɹ���");
		}else if(temp == 22){
			hintInfo("���ݿ�ָ�ʧ�ܣ�");
		}
	}

	// ���ݱ���
	@SuppressLint("NewApi")
	private void dataBackup() {
		// TODO Auto-generated method stub
		int temp = 0;
		AsyncTask<String, Void, Integer> callBack = new BackupTask(this).execute("backupDatabase");
		try {
			temp = callBack.get();			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(temp == 11) {
			hintInfo("���ݿⱸ�ݳɹ���");
		}else if(temp == 12){
			hintInfo("���ݿⱸ��ʧ�ܣ�");
		}
	}
	
	private void showAbout(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityGroup.this);
		builder.setTitle("����");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage(R.string.info);
		builder.setPositiveButton("ȷ��", null);
		builder.create().show();
	}

	private void hintInfo(String str){
		Toast.makeText(MainActivityGroup.this, str, Toast.LENGTH_SHORT).show();
	}
}