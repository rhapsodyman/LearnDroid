package com.rhapsodyman.learndroid;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

 
public class DrivingModesFr extends Fragment implements OnClickListener {
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.driving_modes, container, false);
         
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onActivityCreated(savedInstanceState);
    	((MainActivity)getActivity()).findViewById(R.id.buttons).setOnClickListener(this);
    	((MainActivity)getActivity()).findViewById(R.id.acceler).setOnClickListener(this);
    	((MainActivity)getActivity()).findViewById(R.id.speech).setOnClickListener(this);
    	((MainActivity)getActivity()).findViewById(R.id.userprog).setOnClickListener(this);

    	
    }
 
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttons:
		startActivity(new Intent(((MainActivity)getActivity()), Buttons.class));
			break;

		case R.id.acceler:
			startActivity(new Intent(((MainActivity)getActivity()), Accelerometer.class));
			break;
		case R.id.speech:
			startActivity(new Intent(((MainActivity)getActivity()), SpeechSphinx.class));
			break;
		case R.id.userprog:
			startActivity(new Intent(((MainActivity)getActivity()), Programming.class));
			break;
		
		}
		
		
			
		
	}
}
