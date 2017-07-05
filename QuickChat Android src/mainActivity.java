package quickchat;

import java.util.StringTokenizer;

import com.quickchat.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public  class mainActivity extends ListActivity {
	
	public String ownusername = new String();

	public static final int PREVIOUS_CHATS = Menu.FIRST;
	private static final int ADD_NEW_FRIEND_ID = Menu.FIRST + 1;
	public static final int EXIT_APP_ID = Menu.FIRST + 2;
	public static final int HELP_ID = Menu.FIRST + 3;

	
	private Button refreshOthers;
	private static EditText mFriendUserNameText;
	public String usernameString = new String();


	@SuppressLint("InflateParams")
	private class GPSFinder extends ListActivity 
	{		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle("GPS");
		Button refreshAll = (Button) findViewById(R.id.refreshall);
		     refreshOthers = (Button) findViewById(R.id.refreshothers);
		 final GPSTracker mGPS = new GPSTracker(this);
			mGPS.getLocation();
			 final ListView listview = getListView();
		        listview.setTextFilterEnabled(true);
		        final ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, R.layout.list_screen, R.id.output, AppData.near);
		        listview.setAdapter(adapter);


	
	refreshAll.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
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
													"Could Not Refresh!",
													Toast.LENGTH_LONG).show();
		
										 }
										if (result.startsWith("0")) {
										HttpAsyncTask nextTask = new HttpAsyncTask(
												new CompletedTasks() {
													public void callBack(
															String result) {
														AppData.near.clear();
														StringTokenizer st = new StringTokenizer(result.substring(12), " ,"); 
														while (st.hasMoreTokens()) {	
															AppData.near.add(st.nextToken());
															listview.setAdapter(adapter);
															
														
													
													
												}
														
													};
												});

										nextTask.execute("getnearbyusers.php");
									}};
								});

						if (loginTask.isConnected(mainActivity.this)) {
							loginTask.execute("setlocation.php", "latitude", AppData.latitude, "longitude", AppData.longitude);
						}
				
				}						
			};
			thread.start();
								
		
		
	}});
	refreshOthers.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			Thread thread = new Thread(new Runnable()  {
                @Override
                public void run() {
                		AppData.near.clear();
							HttpAsyncTask GPS = new HttpAsyncTask(
									new CompletedTasks() {
										public void callBack(
												String result) {
											if (result.startsWith("5"))
											{
												Toast.makeText(getApplicationContext(),
														"Could Not Refresh!",
														Toast.LENGTH_LONG).show();
			
											 }
											if (result.startsWith("0")) {
											AppData.near.clear();
											StringTokenizer st = new StringTokenizer(result.substring(12), " :,"); 
											while (st.hasMoreTokens()) {	
												AppData.near.add(st.nextToken());
												
											
										
										
									}
									
										}
											}});

							if (GPS.isConnected(mainActivity.this)) {
								GPS.execute("getnearbyusers.php");
							}
						 
            };
           
            });
			
		thread.start();
			
			
	
	
		}});
	}
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
												"Could Not Open Chatroom!",
												Toast.LENGTH_LONG).show();
	
									 }
									if (result.startsWith("0")) {

									HttpAsyncTask nextTask = new HttpAsyncTask(
											new CompletedTasks() {
												public void callBack(String result) {
													Intent i = new Intent(
															mainActivity.this,
															Messaging.class);
													mainActivity.this.startActivity(i);
													mainActivity.this.finish();
													AppData.response = result.substring(0, 11);
													AppData.m1 = result.substring(12);
													 
																
													 parseServerResponse(AppData.response, AppData.m1);
															
												}});

									nextTask.execute("getchat.php", "chatroomHashID", AppData.chatroomHashID);
								}};
							});

					if (talk.isConnected(mainActivity.this)) {
						talk.execute("openchat.php", "otherUsername", AppData.otherUsername);
					}
			
			}					
		};
		thread.start();;

        finish();
	}  
	 @Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		boolean result = super.onCreateOptionsMenu(menu);
		
		 menu.add(0, PREVIOUS_CHATS, 0, R.string.previous_chats);
		 menu.add(0, ADD_NEW_FRIEND_ID, 0, R.string.find_friend);
		 menu.add(0, EXIT_APP_ID, 0, R.string.exit_application);
		 menu.add(0, HELP_ID, 0, "Help");
		 


		return result;
	}
    
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    
		switch(item.getItemId()) 
	    {
	    case HELP_ID:
		{
			Intent ii = new Intent(mainActivity.this, HelpMe.class);
			startActivity(ii);
			return true;
		}
	    	case PREVIOUS_CHATS:
	    	{
	    		Thread thread = new Thread(new Runnable() {
	                @Override
	                public void run() {
								HttpAsyncTask Friend = new HttpAsyncTask(
										new CompletedTasks() {
											public void callBack(
													String result) {
												if (result.startsWith("5"))
												{
													Toast.makeText(getApplicationContext(),
															"Could Not Open Page",
															Toast.LENGTH_LONG).show();
				
												 }
												if (result.startsWith("0")) {
												Intent i = new Intent(
														mainActivity.this,
														PreviousChats.class);
												mainActivity.this.startActivity(i);
												mainActivity.this.finish();
												AppData.friends.clear();
												StringTokenizer st = new StringTokenizer(result, " :,"); 
													while (st.hasMoreTokens()) {	
														AppData.friends.add(st.nextToken());
											}
											}}});

								if (Friend.isConnected(mainActivity.this)) {
									Friend.execute("getprevioususers.php");
								}
							 
	            };
	           
	            });
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
														mainActivity.this,
														Login.class);
												mainActivity.this.startActivity(ii);
												mainActivity.this.finish();
											}
												});

								if (GPS.isConnected(mainActivity.this)) {
									GPS.execute("logout.php");
								}
							 
	            };
	           
	            });
				
			thread.start();
			return true;
			}	
	    	case ADD_NEW_FRIEND_ID:
	    	{
	    		
	    		Intent iii = new Intent(mainActivity.this, FindFriend.class);
				startActivity(iii);
				return true;
			
	    }
	       
	    }    return super.onMenuItemSelected(featureId, item);
	}
	

	@Override
	protected void onResume() {
		
		   
		super.onResume();
	}
	
	@Override
	protected void onPause() 
	{
		
		super.onPause();
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
}