package quickchat;


import java.util.StringTokenizer;

import com.quickchat.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	protected static final int NOT_CONNECTED_TO_SERVICE = 0;
	protected static final int FILL_BOTH_USERNAME_AND_PASSWORD = 1;
	public static final String AUTHENTICATION_FAILED = "0";
	public static final String FRIEND_LIST = "FRIEND_LIST";
	protected static final int MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT = 2;
	protected static final int NOT_CONNECTED_TO_NETWORK = 3;
	private EditText usernameText;
	private EditText passwordText;
	private Button cancelButton;
	public static final int SIGN_UP_ID = Menu.FIRST;
	public static final int EXIT_ID = Menu.FIRST + 1;
	public static final int HELP_ID = Menu.FIRST + 2;

	EditText resultBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		

		setContentView(R.layout.login_screen);
		setTitle("Login");

		Button loginButton = (Button) findViewById(R.id.login);
		cancelButton = (Button) findViewById(R.id.cancel_login);
		usernameText = (EditText) findViewById(R.id.username);
		passwordText = (EditText) findViewById(R.id.password);
		

		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (usernameText.length() > 4 && passwordText.length() > 4) {
					AppData.username = usernameText.getText().toString();
					final String passwordString = passwordText.getText().toString();

					boolean isValid = true;

					for (int charNum = 0; charNum < AppData.username.length(); ++charNum) {
						if (AppData.username.charAt(charNum) < '!'
								|| AppData.username.charAt(charNum) > '~') {
							isValid = false;

							break;
						}
					}

					if (isValid) {
						for (int charNum = 0; charNum < passwordString.length(); ++charNum) {
							if (passwordString.charAt(charNum) < '!'
									|| passwordString.charAt(charNum) > '~') {
								isValid = false;

								break;
							}
						}
					}

					if (isValid) {
						class LoginThread implements Runnable {
							@Override
							public void run() {
								String encryptedPassword = EncryptString
										.encryptSHA512(passwordString);

								if (!encryptedPassword.isEmpty()
										|| encryptedPassword.length() != 512) {
									HttpAsyncTask loginTask = new HttpAsyncTask(
											new CompletedTasks() {
												public void callBack(
														String result) {
													

													if (result.startsWith("5"))
													{
														Toast.makeText(getApplicationContext(),
																"Invalid username or password!",
																Toast.LENGTH_LONG).show();
					
													 }
												 if (result.startsWith("0")) {
													HttpAsyncTask nextTask = new HttpAsyncTask(
															new CompletedTasks() {
																public void callBack(
																		String result) {
																	Intent i = new Intent(
																			Login.this,
																			PreviousChats.class);
																	Login.this
																			.startActivity(i);
																	Login.this
																			.finish();
																	AppData.friends.clear();
																	StringTokenizer st = new StringTokenizer(result, " :,"); 
																		while (st.hasMoreTokens()) {	
																			AppData.friends.add(st.nextToken());
																			
																		
																	
																	
																};
																} 
																
															});

													nextTask.execute("getprevioususers.php");
													
												}
												
												}});
									
									if (loginTask.isConnected(Login.this)) {
										loginTask.execute("login.php",
												"username", AppData.username,
												"password", encryptedPassword);
									}
								} 
							};
						}

						LoginThread loginThread = new LoginThread();

						loginThread.run();
					} 
					
				} 
				
				else {
					/*
					 * Username or Password is not filled, alert the user
					 */
					Toast.makeText(getApplicationContext(),
							R.string.fill_both_username_and_password,
							Toast.LENGTH_LONG).show();
					// showDialog(FILL_BOTH_USERNAME_AND_PASSWORD);
				}
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// imService.exit();
				finish();

			}

		});
	}
	

	@Override
	protected Dialog onCreateDialog(int id) {
		int message = -1;
		switch (id) {
		case NOT_CONNECTED_TO_SERVICE:
			message = R.string.not_connected_to_service;
			break;
		case FILL_BOTH_USERNAME_AND_PASSWORD:
			message = R.string.fill_both_username_and_password;
			break;
		case MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT:
			message = R.string.make_sure_username_and_password_correct;
			break;
		case NOT_CONNECTED_TO_NETWORK:
			message = R.string.not_connected_to_network;
			break;
		default:
			break;
		}

		if (message == -1) {
			return null;
		} else {
			return new AlertDialog.Builder(Login.this)
					.setMessage(message)
					.setPositiveButton(R.string.OK,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked OK so do some stuff */
								}
							}).create();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		menu.add(0, SIGN_UP_ID, 0, R.string.sign_up);
		menu.add(0, EXIT_ID, 0, "Exit");
		menu.add(0, HELP_ID, 0, "Help");

		return result;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case SIGN_UP_ID:
			Intent i = new Intent(Login.this, SignUp.class);
			startActivity(i);
			return true;
		case EXIT_ID:
			cancelButton.performClick();
			return true;
		case HELP_ID:
			Intent ii = new Intent(Login.this, HelpMe.class);
			startActivity(ii);
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}
	
	
}