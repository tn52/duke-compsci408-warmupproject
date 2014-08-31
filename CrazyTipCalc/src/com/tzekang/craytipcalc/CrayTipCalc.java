package com.tzekang.craytipcalc;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.tzekang.crazytipcalc.R;

public class CrayTipCalc extends Activity {
	
	// Constants used when saving and restoring
	
	private static final String TOTAL_BILL = "TOTAL_BILL";
	private static final String CURRENT_TIP = "CURRENT_TIP";
	private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";
	
	private double billBeforeTip;// Users bill before tip
	private double tipAmount;// Tip amount
	private double finalBill;// Bill plus Tip

	
	EditText billBeforeTipET;
	EditText tipAmountET;
	EditText finalBillET;
	
	private int[] checklistValues = new int[12]; // Sum of all radio buttons and check boxes
	
	CheckBox friendlyCheckBox; // Declare CheckBoxes
	CheckBox specialsCheckBox;	
	CheckBox opinionCheckBox;
	
	RadioGroup availableRadioGroup; // Declare RadioButtons
	RadioButton availableBadRadio;
	RadioButton availableOKRadio;
	RadioButton availableGoodRadio;
	
	Spinner problemsSpinner; // Declare Spinner (Drop Down Box)
	
	Button startChronometerButton; // Declare Buttons
	Button pauseChronometerButton;
	Button resetChronometerButton;
	
	Chronometer timeWaitingChronometer; // Declare Chronometer
	
	long secondsYouWaited = 0; // The number of seconds you spent waiting for waitress
	
	TextView timeWaitingTextView; // TextView for the chronometer
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cray_tip_calc); // Inflate the GUI
		
		//just starting application (not restarting after pause or stop) // Check if app just started, or if it is being restored 
		if(savedInstanceState == null){
			// Just started
			billBeforeTip = 0.0;
			tipAmount = 0.15;
			finalBill = 0.0;
			
		//returning to app after pause
		}else{
			// App is being restored
			billBeforeTip = savedInstanceState.getDouble(BILL_WITHOUT_TIP);
			tipAmount = savedInstanceState.getDouble(CURRENT_TIP);
			finalBill = savedInstanceState.getDouble(TOTAL_BILL);
		}
		
		//initialize edittext boxes
		billBeforeTipET = (EditText) findViewById(R.id.billEditText) ; // Users bill before tip
		tipAmountET= (EditText) findViewById(R.id.tipEditText) ;       // Tip amount
		finalBillET= (EditText) findViewById(R.id.finalBillEditText) ; // Bill plus tip
		
		// Initialize the SeekBar and add a ChangeListener
		tipSeekBar = (SeekBar)findViewById(R.id.changeTipSeekBar) ;
		
		tipSeekBar.setOnSeekBarChangeListener(tipSeekBarListener);
		
		// Add change listener for when the bill before tip is changed - See more at: http://www.newthinktank.com/2013/05/android-development-tutorial-4/#sthash.tjWZmVyl.dpuf
		billBeforeTipET.addTextChangedListener(billBeforeTipListener); 
		
		friendlyCheckBox = (CheckBox) findViewById(R.id.friendlyCheckBox); // Initialize CheckBoxs
		specialsCheckBox = (CheckBox) findViewById(R.id.specialsCheckBox);
		opinionCheckBox = (CheckBox) findViewById(R.id.opinionCheckBox);
		
		setUpIntroCheckBoxes(); // Add change listeners to check boxes
		
		availableRadioGroup = (RadioGroup) findViewById(R.id.availableRadioGroup); // Initialize RadioGroups
		availableBadRadio = (RadioButton) findViewById(R.id.availableBadRadio);    // Initialize RadioButtons
		availableOKRadio = (RadioButton) findViewById(R.id.availableOKRadio);
		availableGoodRadio = (RadioButton) findViewById(R.id.availableGoodRadio);
		
		addChangeListenerToRadios(); // Add ChangeListener To Radio buttons
		
		problemsSpinner = (Spinner) findViewById(R.id.problemsSpinner); // Initialize the Spinner
		problemsSpinner.setPrompt("Problem Solving");

		
		addItemSelectedListenerToSpinner(); // Add ItemSelectedListener To Spinner

		
		startChronometerButton = (Button) findViewById(R.id.startChronometerButton); // Initialize Buttons
		pauseChronometerButton = (Button) findViewById(R.id.pauseChronometerButton);		
		resetChronometerButton = (Button) findViewById(R.id.resetChronometerButton);	
		
		setButtonOnClickListeners(); // Add setOnClickListeners for buttons
		
		timeWaitingChronometer = (Chronometer) findViewById(R.id.timeWaitingChronometer);  // Initialize Chronometer
		
		timeWaitingTextView = (TextView) findViewById(R.id.timeWaitingTextView); // TextView for Chronometer

		
	}
	
	// Called when the bill before tip amount is changed - See more at: http://www.newthinktank.com/2013/05/android-development-tutorial-4/#sthash.tjWZmVyl.dpuf
	private TextWatcher billBeforeTipListener = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			try{
				// Change the billBeforeTip to the new input
				billBeforeTip = Double.parseDouble(arg0.toString());
			}
			
			catch(NumberFormatException e){
				billBeforeTip = 0.0;
			}
			
			updateTipAndFinalBill();
			
		}
		
	};
    // Update the tip amount and add tip to bill to find the final bill amount
	private void updateTipAndFinalBill(){
		
		// Get tip amount
		double tipAmount = Double.parseDouble(tipAmountET.getText().toString());
		
		// The bill before tip amount was set in billBeforeTipListener
		// Get the bill plus the tip
		double finalBill = billBeforeTip + (billBeforeTip*tipAmount);
		
		// Set the total bill amount including the tip 
		// Convert into a 2 decimal place String

		finalBillET.setText(String.format("%.02f", finalBill));
	}
	
	// Called when a device changes in some way. For example, when a keyboard is popped out, or when the devie is rotated. 
	// Used to save state information that you'd like to be made available. 
	
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putDouble(TOTAL_BILL, finalBill);
		outState.putDouble(CURRENT_TIP, tipAmount);
		outState.putDouble(BILL_WITHOUT_TIP, billBeforeTip);
	}
	
	// SeekBar used to make a custom tip
	private SeekBar tipSeekBar;
	private OnSeekBarChangeListener tipSeekBarListener = new OnSeekBarChangeListener(){

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub
			
			// Get the value set on the SeekBar
			tipAmount = (tipSeekBar.getProgress())* .01;
			// Set tipAmountET with the value from the SeekBar
			tipAmountET.setText(String.format("%.02f",tipAmount));
			// Update all the other EditTexts
			updateTipAndFinalBill();
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	
	private void setUpIntroCheckBoxes(){
		
		friendlyCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
				checklistValues[0] = (friendlyCheckBox.isChecked())?4:0;
				
				setTipFromWaitressChecklist();
				
				updateTipAndFinalBill();				
				
			}
		});

		specialsCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
				checklistValues[1] = (specialsCheckBox.isChecked())?1:0;
				
				setTipFromWaitressChecklist();
				
				updateTipAndFinalBill();				
				
			}
		});
		
		opinionCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
				checklistValues[2] = (opinionCheckBox.isChecked())?2:0;
				
				setTipFromWaitressChecklist();
				
				updateTipAndFinalBill();				
				
			}
		});
	}
	
	private void setTipFromWaitressChecklist(){
		int checklistTotal = 0;
		
		for(int item:checklistValues){
			checklistTotal +=item;
		}
		
		tipAmountET.setText(String.format("%.02f", checklistTotal*.01));
	}
	
	
	private void addChangeListenerToRadios(){
		availableRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				
				checklistValues[3] = (availableBadRadio.isChecked())?-1:0;
				checklistValues[4] = (availableOKRadio.isChecked())?2:0;
				checklistValues[5] = (availableGoodRadio.isChecked())?4:0;
				
				setTipFromWaitressChecklist();
				updateTipAndFinalBill();
			}
			
		});
	}
	
	private void addItemSelectedListenerToSpinner(){
		problemsSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				checklistValues[6] = (problemsSpinner.getSelectedItem()).equals("Bad")?-1:0;
				checklistValues[7] = (problemsSpinner.getSelectedItem()).equals("OK")?3:0;
				checklistValues[8] = (problemsSpinner.getSelectedItem()).equals("Good")?6:0;
				
				setTipFromWaitressChecklist();
				updateTipAndFinalBill();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		
	}
	
	private void setButtonOnClickListeners(){
		startChronometerButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				int stoppedMilliseconds = 0;
				
				String chronoText = timeWaitingChronometer.getText().toString();
				String array[] = chronoText.split(":");
				
				if(array.length ==2){
					stoppedMilliseconds = Integer.parseInt(array[0])*60*1000 +
							Integer.parseInt(array[1])*1000;
					
				}else if (array.length ==3){
					stoppedMilliseconds = Integer.parseInt(array[0])*60*60*1000 +
							Integer.parseInt(array[1])*1000 +
							Integer.parseInt(array[2])*1000;
				}
				timeWaitingChronometer.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);
				
				secondsYouWaited = Long.parseLong(array[1]);
				
				updateTipBasedOnTimeWaited(secondsYouWaited);
				
				timeWaitingChronometer.start();
			}
			
		});
		
		pauseChronometerButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				timeWaitingChronometer.stop();
				
			}
			
		});
		
		resetChronometerButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub			
			timeWaitingChronometer.setBase(SystemClock.elapsedRealtime());
			secondsYouWaited = 0;
			}
		});
	}
	
	
	private void updateTipBasedOnTimeWaited(long secondsYouWaited){
		checklistValues[9] = (secondsYouWaited>10)?-2:2;
		setTipFromWaitressChecklist();
		updateTipAndFinalBill();
				
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cray_tip_calc, menu);
		return true;
	}

}
