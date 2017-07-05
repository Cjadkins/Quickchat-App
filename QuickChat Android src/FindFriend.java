package quickchat;

import java.util.StringTokenizer;

import com.quickchat.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindFriend extends Activity implements OnClickListener {

	private static Button mAddFriendButton;
	private static Button mCancelButton;
	public static EditText mFriendUserNameText;
	public static String chatroomHashID;

	private static final int TYPE_FRIEND_USERNAME = 0;
	private static final String LOG_TAG = "AddFriend";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_new_friend);
		setTitle("Find Friend");

		mAddFriendButton = (Button) findViewById(R.id.addFriend);
		mCancelButton = (Button) findViewById(R.id.cancel);
		mFriendUserNameText = (EditText) findViewById(R.id.newFriendUsername);

		if (mAddFriendButton != null) {
			mAddFriendButton.setOnClickListener(this);
		} else {
			Log.e(LOG_TAG, "onCreate: mAddFriendButton is null");
			throw new NullPointerException("onCreate: mAddFriendButton is null");
		}

		if (mCancelButton != null) {
			mCancelButton.setOnClickListener(this);
		} else {
			Log.e(LOG_TAG, "onCreate: mCancelButton is null");
			throw new NullPointerException("onCreate: mCancelButton is null");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	public void onClick(View view) {
		if (view == mCancelButton) {
			finish();
		} else if (view == mAddFriendButton) {
			addNewFriend();
		} else {
			Log.e(LOG_TAG, "onClick: view clicked is unknown");
		}
	}

	// TODO: Remove deprecated method
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(FindFriend.this);
		if (id == TYPE_FRIEND_USERNAME) {
			builder.setTitle(R.string.find_friend)
					.setMessage(R.string.type_friend_username)
					.setPositiveButton(R.string.OK,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// TODO
								}
							});
		}

		return builder.create();
	}

	public void addNewFriend() {
        if (mFriendUserNameText.length() > 0) {
        	   AppData.otherUsername = mFriendUserNameText.getText().toString(); 
        	   AppData.messages.clear();
        	   Thread thread = new Thread(){					
       			public void run() {
       					HttpAsyncTask talk = new HttpAsyncTask(
       							new CompletedTasks() {
       								public void callBack(String result) {
       									AppData.chatroomHashID = result.substring(12);
       									
       									if (result.startsWith("5"))
										{
											Toast.makeText(getApplicationContext(),
													"Invalid Username!",
													Toast.LENGTH_LONG).show();
		
										 }
       									
       									
       									if (result.startsWith("0")) {
       									HttpAsyncTask nextTask = new HttpAsyncTask(
       											new CompletedTasks() {
       												public void callBack(String result) {
       													Intent i = new Intent(
       															FindFriend.this,
       															Messaging.class);
       													FindFriend.this.startActivity(i);
       													FindFriend.this.finish();
       													AppData.messages.clear();
       													AppData.response = result.substring(0, 11);
    													AppData.m1 = result.substring(12);
    													 
    																
    													 parseServerResponse(AppData.response, AppData.m1);
       															;
       															}
       															
       											});
											
       									nextTask.execute("getchat.php", "chatroomHashID", AppData.chatroomHashID);
       								}}
       								
       								;
       							});

       					if (talk.isConnected(FindFriend.this)) {
       						talk.execute("openchat.php", "otherUsername", AppData.otherUsername);
       					}
       			
       			}					
       		};
       		thread.start();;
           
            finish();
        } else {
            Log.e(LOG_TAG, "addNewFriend: username length (" + mFriendUserNameText.length() + ") is < 0");
            Toast.makeText(FindFriend.this, R.string.type_friend_username, Toast.LENGTH_LONG).show();
        }
    
}
public static void parseServerResponse(String response, String message) {
		
		
		boolean successful = (response.substring(0, 1).equals("0"));
		
	
		
		
		if (successful) {
			AppData.messages.clear();

			if (response.equals("0200000010:")) {
			}
			else if (response.equals("0200204030:")) {
			StringTokenizer st = new StringTokenizer(message, String.valueOf((char)0x7C));
			while (st.hasMoreTokens()) {
				for (int i=0;i<4;i++) {
					String s = st.nextToken();
					
					if (i == 0 || i == 1) {
						AppData.messages.add(s);

//						Messaging.messageHistoryText.setText(Messaging.messageHistoryText.getText() + (new String(new char[75]).replace("\0", " ")) + s);
			
					}
		}
			}}
		else{
			
		}
	}
}	
}