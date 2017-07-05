package quickchat;


import com.quickchat.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.TextView;

public class HelpMe extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Help");
		setContentView(R.layout.activity_help_me);
		TextView tv1 = (TextView)findViewById(R.id.help);
        tv1.setMovementMethod(new ScrollingMovementMethod());

	}

	

	
	
}
