package si.majcn.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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
        
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 1);
        } else {
        	Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
		case 1:
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(this, "BT enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "BT not enabled", Toast.LENGTH_SHORT).show();
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, 1);
            }
			break;
		default:
			break;
		}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    public void onClickButton(View v)
	{
		Toast.makeText(this, "Button Click", Toast.LENGTH_LONG).show();
	}
}