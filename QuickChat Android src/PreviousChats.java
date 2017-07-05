package quickchat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.quickchat.R;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PreviousChats extends ListActivity 
{
	private static final int ADD_NEW_FRIEND_ID = Menu.FIRST;
	private static final int Location_ID = Menu.FIRST + 1;
	private static final int EXIT_APP_ID = Menu.FIRST + 2;
	private static EditText mFriendUserNameText;
	public String usernameString = new String();
	private ScheduledExecutorService scheduleTaskExecutor;
	public static final int HELP_ID = Menu.FIRST + 3;


	@SuppressLint("InflateParams")
	public class UsersAdapter extends ListActivity {
		 
		
	   

	public class MessageReceiver extends  BroadcastReceiver  {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			
		}

	};
	public MessageReceiver messageReceiver = new MessageReceiver();


	};
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		AppData.friends.remove(0);
		setTitle("Previous Chats");
        setContentView(R.layout.list_screen);
        final ListView listview = getListView();
        listview.setTextFilterEnabled(true);
        final ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, R.layout.list_screen, R.id.output, AppData.friends);
        listview.setAdapter(adapter);
        scheduleTaskExecutor= Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            	
	                @Override
	                public void run() {
								HttpAsyncTask GPS = new HttpAsyncTask(
										new CompletedTasks() {
											public void callBack(
													String result) { 
												AppData.friends.clear();
												StringTokenizer st = new StringTokenizer(result.substring(12), " :,"); 
													while (st.hasMoreTokens()) {	
														AppData.friends.add(st.nextToken());
														
														listview.setAdapter(adapter);	 

												
												
											}
											}
												});

								if (GPS.isConnected(PreviousChats.this)) {
									GPS.execute("getprevioususers.php");
								}
	            };
	           
        } , 0, 20, TimeUnit.MINUTES);
            
        		}
        

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);		
    	AppData.otherUsername = (String) l.getItemAtPosition(position);

    	Thread thread = new Thread(){					
			public void run() {
					HttpAsyncTask talk = new HttpAsyncTask(
							new CompletedTasks() {
								public void callBack(String result) {
									AppData.chatroomHashID = result.substring(12);
									if (result.startsWith("5"))
									{
										Toast.makeText(getApplicationContext(),
												"Could Not Open Chat!",
												Toast.LENGTH_LONG).show();
	
									 }
									if (result.startsWith("0")) {
									
									
									HttpAsyncTask nextTask = new HttpAsyncTask(
											new CompletedTasks() {
												public void callBack(String result) {
													Intent i = new Intent(
															PreviousChats.this,
															Messaging.class);
													PreviousChats.this.startActivity(i);
													PreviousChats.this.finish();
													AppData.response = result.substring(0, 11);
													AppData.m1 = result.substring(12);
													 
																
													 parseServerResponse(AppData.response, AppData.m1);
																}
												

   													
														

														
															;
												}
															
											);
									
																
									nextTask.execute("getchat.php", "chatroomHashID", AppData.chatroomHashID);
								}};
							});

					if (talk.isConnected(PreviousChats.this)) {
						talk.execute("openchat.php", "otherUsername", AppData.otherUsername);
					}
			
			}					
		};
	
		thread.start();;
        

        finish();
	}


	

	@Override
	protected void onPause() 
	{
		super.onPause();
	}

	@Override
	protected void onResume() 
	{
			
		super.onResume();
		
		

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		boolean result = super.onCreateOptionsMenu(menu);		

		menu.add(0, ADD_NEW_FRIEND_ID, 0, R.string.find_friend);
		menu.add(0, Location_ID, 0, "GPS");
		menu.add(0, EXIT_APP_ID, 0, R.string.exit_application);
		menu.add(0, HELP_ID, 0, "Help");
		
		return result;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) 
	{		

		switch(item.getItemId()) 
		{	  
		case HELP_ID:
		{
			Intent ii = new Intent(PreviousChats.this, HelpMe.class);
			startActivity(ii);
			return true;
		}
			case ADD_NEW_FRIEND_ID:
			{
				Intent i = new Intent(PreviousChats.this, FindFriend.class);
				startActivity(i);
				return true;
			}		
			case Location_ID:
			{	
				final GPSTracker mGPS = new GPSTracker(this);
				mGPS.getLocation();
				final double Lat = mGPS.getLatitude();
				final double Long = mGPS.getLongitude();
				AppData.latitude = Double.toString(Lat);
			    AppData.longitude = Double.toString(Long);
				Thread thread = new Thread(){					
					public void run() {
							HttpAsyncTask loginTask = new HttpAsyncTask(
									new CompletedTasks() {
										public void callBack(
												String result) {
											if (result.startsWith("5"))
											{
												Toast.makeText(getApplicationContext(),
														"Could Not Get Location!",
														Toast.LENGTH_LONG).show();
			
											 }
											if (result.startsWith("0")) {
											
											HttpAsyncTask nextTask = new HttpAsyncTask(
													new CompletedTasks() {
														public void callBack(
																String result) {
															Intent i = new Intent(
																	PreviousChats.this,
																	mainActivity.class);
															PreviousChats.this.startActivity(i);
															PreviousChats.this.finish();
															AppData.near.clear();
															StringTokenizer st = new StringTokenizer(result.substring(12), " ,");
															while (st.hasMoreTokens()) {	
																AppData.near.add(st.nextToken());
																
															
														
														
													}
														};
													});

											nextTask.execute("getnearbyusers.php");
										}};
									});

							if (loginTask.isConnected(PreviousChats.this)) {
								loginTask.execute("setlocation.php", "latitude", AppData.latitude, "longitude", AppData.longitude);
							}
					}						
				};
				thread.start();
			return true;
				
				
			
			}
			case EXIT_APP_ID:
			{
				Thread thread = new Thread(new Runnable()  {
	                @Override
	                public void run() {
								HttpAsyncTask GPS = new HttpAsyncTask(
										new CompletedTasks() {
											public void callBack(
													String result) {
												Intent ii = new Intent(
														PreviousChats.this,
														Login.class);
												PreviousChats.this.startActivity(ii);
												PreviousChats.this.finish();
											}
												});

								if (GPS.isConnected(PreviousChats.this)) {
									GPS.execute("logout.php");
								}
							 
	            };
	           
	            });
				
			thread.start();
			return true;
			}		
			
		}

		return super.onMenuItemSelected(featureId, item);		
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
	
		
		
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
						//Messaging.messageHistoryText.setText(Messaging.messageHistoryText.getText() + (new String(new char[75]).replace("\0", " ")) + s);
			
					}
		}
			}}
		else{
			
		}
	}
}
};
