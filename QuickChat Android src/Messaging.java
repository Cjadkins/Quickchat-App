package quickchat;


import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.quickchat.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Messaging extends Activity {

	private static final int ADD_NEW_FRIEND_ID = Menu.FIRST;
	private static final int Location_ID = Menu.FIRST + 1;
	private static final int EXIT_APP_ID = Menu.FIRST + 2;
	public static final int HELP_ID = Menu.FIRST + 3;
	public static final int PREVIOUS_CHATS_ID = Menu.FIRST + 4;

	private static final int MESSAGE_CANNOT_BE_SENT = 0;
	public String username;
	private EditText messageText;
	public static EditText messageHistoryText;
	private Button sendMessageButton;
	public Handler handler = new Handler();
	private ScheduledExecutorService scheduleTaskExecutor;

	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	 
		

		setContentView(R.layout.messaging_screen);   
		messageHistoryText = (EditText) findViewById(R.id.messageHistory);
		messageText = (EditText) findViewById(R.id.message);
		messageHistoryText.setText(messageHistoryText.getText() + (new String(new char[75]).replace("\0", " ")) + AppData.messages);
		
		scrollToBottom();
		
		//messageText.requestFocus();	
		
		
		
		
		sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
		
		

		setTitle("Messaging with " + AppData.otherUsername);
		scheduleTaskExecutor= Executors.newScheduledThreadPool(5);
	        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
	            	
		                @Override
		                public void run() {
									HttpAsyncTask GPS = new HttpAsyncTask(
											new CompletedTasks() {
												public void callBack(
														String result) { 
													messageHistoryText.setText("");
													AppData.response = result.substring(0, 11);
													AppData.m1 = result.substring(12);
													 parseServerResponse(AppData.response, AppData.m1);
													 scrollToBottom();

												}
													});

									if (GPS.isConnected(Messaging.this)) {
										GPS.execute("getchat.php", "chatroomHashID", AppData.chatroomHashID);
									}
		            };
		           
	        } , 0, 6, TimeUnit.SECONDS);
	

		
		
		
		
		
		sendMessageButton.setOnClickListener(new OnClickListener(){
			
			public void onClick(View arg0) {
				AppData.message = messageText.getText().toString();
				messageText.setText("");
					Thread thread = new Thread(){					
						public void run() {
								HttpAsyncTask talk = new HttpAsyncTask(
										new CompletedTasks() {
											public void callBack(String result) {
												if (result.startsWith("5"))
												{
													Toast.makeText(getApplicationContext(),
															"Could Not Send!",
															Toast.LENGTH_LONG).show();
				
												 }
												if (result.startsWith("0")) {
												
												
												HttpAsyncTask nextTask = new HttpAsyncTask(
														new CompletedTasks() {
															public void callBack(String result) {
																messageHistoryText.setText("");
																AppData.response = result.substring(0, 11);
																AppData.m1 = result.substring(12);
																 parseServerResponse(AppData.response, AppData.m1);
																	scrollToBottom();

																			
																 

																	
															};
														});

												nextTask.execute("getchat.php", "chatroomHashID", AppData.chatroomHashID);
											}};
										});
								if (talk.isConnected(Messaging.this)) {
									talk.execute("sendchat.php", "message", AppData.message, "chatroomHashID", AppData.chatroomHashID);
									
								}
							 else {

							}
						}						
					};
					thread.start();
					
										
				
				
			}});
		
				
	}



	private void scrollToBottom() {
		int y = (messageHistoryText.getLineCount() - 1) * messageHistoryText.getLineHeight(); // the " - 1" should send it to the TOP of the last line, instead of the bottom of the last line
		messageHistoryText.scrollTo(0, y);
		
	}



	@Override
	protected void onPause() {
		super.onPause();
	
		
	}

	@Override
	protected void onResume() 
	{		
		super.onResume();
		
	
		
		
		
	}


	
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	 

}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		boolean result = super.onCreateOptionsMenu(menu);		

		menu.add(0, ADD_NEW_FRIEND_ID, 0, R.string.find_friend);
		menu.add(0, Location_ID, 0, "GPS");
		menu.add(0, EXIT_APP_ID, 0, R.string.exit_application);
		menu.add(0, HELP_ID, 0, "Help");
		menu.add(0, PREVIOUS_CHATS_ID, 0, "Previous Chats");
		
		return result;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) 
	{		

		switch(item.getItemId()) 
		{	  
		case HELP_ID:
		{
			Intent ii = new Intent(Messaging.this, HelpMe.class);
			startActivity(ii);
			return true;
		}
			case ADD_NEW_FRIEND_ID:
			{
				Intent i = new Intent(Messaging.this, FindFriend.class);
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
																	Messaging.this,
																	mainActivity.class);
															Messaging.this.startActivity(i);
															Messaging.this.finish();
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

							if (loginTask.isConnected(Messaging.this)) {
								loginTask.execute("setlocation.php", "latitude", AppData.latitude, "longitude", AppData.longitude);
							}
					}						
				};
				thread.start();
			return true;
				
				
			
			}
			case PREVIOUS_CHATS_ID:
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
															"Could Not Get Previous Chats!",
															Toast.LENGTH_LONG).show();
				
												 }
												if (result.startsWith("0")) {
												Intent i = new Intent(
														Messaging.this,
														PreviousChats.class);
												Messaging.this.startActivity(i);
												Messaging.this.finish();
												AppData.friends.clear();
												StringTokenizer st = new StringTokenizer(result, " :,"); 
													while (st.hasMoreTokens()) {	
														AppData.friends.add(st.nextToken());
											}
											}}});

								if (Friend.isConnected(Messaging.this)) {
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
														Messaging.this,
														Login.class);
												Messaging.this.startActivity(ii);
												Messaging.this.finish();
											}
												});

								if (GPS.isConnected(Messaging.this)) {
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
	
	
public static void parseServerResponse(String response, String message) {
		
		
		boolean successful = (response.substring(0, 1).equals("0"));
		
	
		
		
		if (successful) {
			messageHistoryText.setText("");
			if (response.equals("0200000010:")) {
		}
		else if (response.equals("0200204030:")) {
			StringTokenizer st = new StringTokenizer(message, String.valueOf((char)0x7C));
			while (st.hasMoreTokens()) {
				for (int i=0;i<4;i++) {
					String s = st.nextToken();
					
					if (i == 0 || i == 1) {
						messageHistoryText.setText(messageHistoryText.getText() + (new String(new char[75]).replace("\0", " ")) + s);
			
					}
		}
			}
			}
		else{
			
		}
	}
}
	
	
}