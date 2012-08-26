package ydp.weightrecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


@SuppressLint("NewApi")
public class BackupTask extends AsyncTask<String, Void, Integer> {
	private static final String COMMAND_BACKUP = "backupDatabase";
	public static final String COMMAND_RESTORE = "restoreDatabase";
	private Context mContext;

	public BackupTask(Context context) {
		this.mContext = context;
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		// TODO Auto-generated method stub
		File dbFile = new File("/data/data/ydp.weightrecord/databases/weight_db");

		File backup = new File(Environment.getExternalStorageDirectory() + "/", dbFile.getName());
		String command = params[0];
		if (command.equals(COMMAND_BACKUP)) {
			try {
				backup.createNewFile();
				fileCopy(dbFile, backup);
				Log.d("backup", "ok");
				return 11;
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("backup", "fail");
				return 12;
			}
		} else if (command.equals(COMMAND_RESTORE)) {
			try {
				fileCopy(backup, dbFile);
				Log.d("restore", "success");
				return 21;
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("restore", "fail");
				return 22;
			}
		} else {
			return null;
		}
	}

	private void fileCopy(File dbFile, File backup) throws IOException {
		// TODO Auto-generated method stub
		FileChannel inChannel = new FileInputStream(dbFile).getChannel();
		FileChannel outChannel = new FileOutputStream(backup).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}
}
