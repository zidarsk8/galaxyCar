package si.majcn.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SimpleBTActivity extends Activity {
	
	private BluetoothAdapter mBluetoothAdapter = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }
    
    public void onClickButton(View v)
	{
		Toast.makeText(this, "Button Click", Toast.LENGTH_LONG).show();
	}
}