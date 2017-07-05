package quickchat;

import com.quickchat.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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


public class SignUp extends Activity {
	
	private static final int FILL_ALL_FIELDS = 0;
	protected static final int TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS = 1;
	private static final int SIGN_UP_FAILED = 2;
	private static final int SIGN_UP_USERNAME_CRASHED = 3;
	private static final int SIGN_UP_SUCCESSFULL = 4;
	protected static final int USERNAME_AND_PASSWORD_LENGTH_SHORT = 5;
	public static final int HELP_ID = Menu.FIRST;

	
	private EditText usernameText;
	private EditText passwordText;
	private EditText passwordAgainText;


   

	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);    

	    
	               
	        setContentView(R.layout.sign_up_screen);
	        setTitle("Sign up");
	        
	        Button signUpButton = (Button) findViewById(R.id.signUp);
	        Button cancelButton = (Button) findViewById(R.id.cancel_signUp);
	        usernameText = (EditText) findViewById(R.id.userName);
	        passwordText = (EditText) findViewById(R.id.password);  
	        passwordAgainText = (EditText) findViewById(R.id.passwordAgain);  
	        
	        
	        signUpButton.setOnClickListener(new OnClickListener(){
				public void onClick(View arg0) 
				{						
					if (usernameText.length() >= 5 &&		
						passwordText.length() >= 5 && 
						passwordAgainText.length() >= 5
						)
					{
						final String usernameString = usernameText.getText().toString();
						final String passwordString = passwordText.getText().toString();
						
						boolean isValid = true;
						
						for (int charNum = 0; charNum < usernameString.length(); ++charNum) {
							if (usernameString.charAt(charNum) < '!' || usernameString.charAt(charNum) > '~') {
								isValid = false;
								
								break;
							}
						}
						
						if (isValid) {
							for (int charNum = 0; charNum < passwordString.length(); ++charNum) {
								if (passwordString.charAt(charNum) < '!' || passwordString.charAt(charNum) > '~') {
									isValid = false;
									
									break;
								}
							}
						}
						
						if (isValid) {
							if (passwordText.getText().toString().equals(passwordAgainText.getText().toString())) {
								if (usernameText.length() >= 5 && passwordText.length() >= 5) {
									
									class LoginThread implements Runnable {
										@Override
										public void run() {
											String encryptedPassword = EncryptString
													.encryptSHA512(passwordString);
										
											if (!encryptedPassword.isEmpty()
													|| encryptedPassword.length() != 512) {
											HttpAsyncTask task = new HttpAsyncTask(new CompletedTasks(){
												public void callBack(String result) {
													if (result.startsWith("5"))
													{
														Toast.makeText(getApplicationContext(),
																"Username Exists!",
																Toast.LENGTH_LONG).show();
					
													 }
													if (result.startsWith("0")) {
													Intent i = new Intent(SignUp.this, Login.class);
											    	SignUp.this.startActivity(i);
													SignUp.this.finish();
													
													
												}}
											});
											
											if (task.isConnected(SignUp.this)) {
												task.execute("register.php", "username", usernameString, "password", encryptedPassword);
											}
										};
									}
									}
									LoginThread loginThread = new LoginThread();
									
									loginThread.run();
								
								}	else{
									Toast.makeText(getApplicationContext(),R.string.username_and_password_length_short, Toast.LENGTH_LONG).show();
									//showDialog(USERNAME_AND_PASSWORD_LENGTH_SHORT);
								}
							}
						}
						else {
							Toast.makeText(getApplicationContext(),"Make Sure Password Matches", Toast.LENGTH_LONG).show();
							//showDialog(TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS);
						}
						
					}
					else {
						Toast.makeText(getApplicationContext(),"Make Sure Username And Password Are Longer Than 5 Characters", Toast.LENGTH_LONG).show();
						//showDialog(FILL_ALL_FIELDS);
						
					}				
				}       	
	        });
	        
	        cancelButton.setOnClickListener(new OnClickListener(){
				public void onClick(View arg0) 
				{						
					finish();					
				}	        	
	        });
	        
	        
	    }
	
	
	protected Dialog onCreateDialog(int id) 
	{    	
		  	
		switch (id) 
		{
			case TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS:			
				return new AlertDialog.Builder(SignUp.this)       
				.setMessage(R.string.signup_type_same_password_in_password_fields)
				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked OK so do some stuff */
					}
				})        
				.create();			
			case FILL_ALL_FIELDS:				
				return new AlertDialog.Builder(SignUp.this)       
				.setMessage(R.string.signup_fill_all_fields)
				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked OK so do some stuff */
					}
				})        
				.create();
			case SIGN_UP_FAILED:
				return new AlertDialog.Builder(SignUp.this)       
				.setMessage(R.string.signup_failed)
				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked OK so do some stuff */
					}
				})        
				.create();
			case SIGN_UP_USERNAME_CRASHED:
				return new AlertDialog.Builder(SignUp.this)       
				.setMessage(R.string.signup_username_crashed)
				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked OK so do some stuff */
					}
				})        
				.create();
			case SIGN_UP_SUCCESSFULL:
				return new AlertDialog.Builder(SignUp.this)       
				.setMessage(R.string.signup_successfull)
				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				})        
				.create();	
			case USERNAME_AND_PASSWORD_LENGTH_SHORT:
				return new AlertDialog.Builder(SignUp.this)       
				.setMessage(R.string.username_and_password_length_short)
				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/* User clicked OK so do some stuff */
					}
				})        
				.create();
			default: 
				return null;
				
		}

	
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		menu.add(0, HELP_ID, 0, "Help");

		return result;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		
		case HELP_ID:
			Intent ii = new Intent(SignUp.this, HelpMe.class);
			startActivity(ii);
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}
	

}
