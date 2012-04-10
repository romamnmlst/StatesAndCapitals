//Matthew Usnick
//CS211D HW5: States & Capitals
//Help.java

package cs211d.hw5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Play @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
/** Task: This activity is called from StatesAndCapitals.java and is the 
*         interactive game activity of this app. States or capitals are 
*         displayed, and the user must enter the corresponding state or capital,
*         via text field that offers auto-complete suggestions. Toast messages
*         are displayed to inform the user if their answer was right or wrong.
*         The user can request a state or capital to be displayed. The user's
*         score and current question number are tracked and displayed. Once all
*         50 combinations are answered, the game is over, and the Scores.java
*         activity is called.
*
* @author Matthew Usnick
*/
public class Play extends Activity
{
    // __________________________Class Variables _______________________________
    //GUI elements_______________
    ToggleButton toggleButton;             //button to switch stateMode variable
    AutoCompleteTextView textField;        //text field for user input
    Button submitButton;                   //button to submit answer
    TextView scoreField;                   //displays current score
    TextView currentText;                  //displays current state/capital name
    ImageView currentImage;                //displays image of state or blur map
    
    //ArrayList__________________
    ArrayList<String> pairs;       //arraylist to hold states and capitals pairs
    HashMap<String, Integer> imageMap;
    
    //booleans___________________
    boolean stateMode;           //true: State displayed. User enters capital.
                                 //false: Capital displayed. User enters  state.
    
    //ints_______________________
    int userScore;               //number of questions answered correctly so far
    int questionNumber;          //number of questions answered so far
    int randomNumber;            //current random position in the ArrayList

    //strings____________________
    String name;                 //current player's name
    String currentState;         //current  state
    String currentCapital;       //current capital
    
    //Log tag____________________
    private static final String TAG = Help.class.getSimpleName();
    
    ////////////////////////////////onCreate()//////////////////////////////////
    /** Task: onCreate is called when this activity is first created.
    *         This method calls it's super version of itself, as well as setting
    *         the content view to the play.xml layout. The GUI elements are 
    *         initialized, the extra data that was passed to this activity is
    *         collected and assigned to variables, and the first question of the
    *         game is set up.
    *
    * @param b is a Bundle object
    */
    @Override //annotation
    public void onCreate(Bundle b)
    {
        //required actions >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //pass bundle to super version of onCreate()
        super.onCreate(b);
        
        //set the layout for this activity (play.xml)
        setContentView(R.layout.play);
        
        //check for null invocation >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        Intent in = this.getIntent(); 
        if(in == null)
        {
            Log.d(TAG, "No intent invoked this activity.");
        }
        
        //set up GUI elements>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //get IDs---------------------------------------------------------------
        toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        scoreField = (TextView)findViewById(R.id.scoreField);
        currentText = (TextView)findViewById(R.id.currentText);
        textField = (AutoCompleteTextView)findViewById(R.id.answerField);
        currentImage = (ImageView)findViewById(R.id.questionImage);
        

        //set up textField------------------------------------------------------
        //define auto-complete data 
        String[] autoData = getResources().getStringArray(R.array.data_array);
        ArrayAdapter<String> adapter = 
                   new ArrayAdapter<String>(this, 
                                            R.layout.autocomplete_item, 
                                            autoData);
        textField.setAdapter(adapter);
                
        //setup imageMap
        setupImageMap();
        
        //create OnKeyListener for textField
        textField.setOnKeyListener(createTextListener());
        
        //get extra data >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //get state/captial ArrayList
        pairs = getIntent().getStringArrayListExtra("gameContent");
        
        //get player name
        name = getIntent().getStringExtra("playerName");

        //initialize variables >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        userScore = 0;
        questionNumber = 0;
        stateMode = false;
        
        //set up the first question of the game >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        setupNextQuestion();
        
    }//end onCreate()
    
    //**************************createTextListener()****************************
    /** Task: this method creates and returns an anonymous class. This class is
    *         the OnKeyListener for the AutoCompleteTextView. This method is 
    *         set up to deal with pressing enter while the AutoComplete dialog
    *         is displayed. If a suggestion is selected, hitting enter only
    *         accepts the suggestion, and does not call verifyAnswer(). This 
    *         prevents the passing of the users incomplete String as their 
    *         answer. 
    *         
    *         Note: current bug: if the user does not use the AutoComplete 
    *               suggestion, and hits "enter" on the keypad, a new line 
    *               character is inserted. Current solution is to remove newline
    *               characters from the user input, however the user will have
    *               to pressed "enter" again to submit the answer. Possible 
    *               solution may involve a OnItemSelectedListener to allow for
    *               verifying that no AutoComplete item is selected, and if so
    *               call verifyAnswer() without inserting a newline character.
    *               This solution requires more time to research and implement.
    *        
    * @return OnKeyListener       
    */
    public OnKeyListener createTextListener()
    {
        //return the defined anonymous class
        return (new View.OnKeyListener()
        {
            //Overridden method
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent ke)
            {
                //if the user presses enter, and the AutoComplete popup is not
                //showing, and there is no AutoComplete action being performed
                if((ke.getAction() == KeyEvent.ACTION_DOWN)
                    && (keyCode == KeyEvent.KEYCODE_ENTER)
                    && !textField.isPopupShowing()
                    && !textField.isPerformingCompletion())
                {
                    //verify the users answer
                    verifyAnswer();
                            
                    //always return true
                    return true;
                }
                //if the user presses enter, and the popup window is not showing
                else if((ke.getAction() == KeyEvent.ACTION_DOWN)
                         && (keyCode == KeyEvent.KEYCODE_ENTER)
                         && !textField.isPopupShowing())
                {
                     
                    //verify the users answer
                    verifyAnswer();
                            
                    //always return true
                    return true;       
                }

                //always return false if the Return key was not pressed
                return false;
     
            }//end onKey()
        });//end onKeyListener()
        
    }//end createTextListener()
    
    //********************************doClick()*********************************
    /** Task: this method handles all button click events. The corresponding
    *         actions are called.
    *         
    * @param v is a View object. It is used to find the button ID that called
    *        this method.       
    */
    public void doClick(View v)
    {
        //Switch structure to find Button ID >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        switch(v.getId())
        {
            //if state/capital toggle button is clicked ------------------------
            case (R.id.toggleButton):

                //if button is checked
                if(toggleButton.isChecked())
                {
                    //set mode to display a state
                    stateMode = true;
                }
                //if not checked
                else
                {
                    //set mode to display a capital
                    stateMode = false;
                }
                
                //set up a new question
                setupNextQuestion();
                
                break;
            
            //if submit button is clicked --------------------------------------
            case (R.id.submitButton):
                
                //verify the users input
                verifyAnswer();
            
                break;
            
            //if the ID is unspecified -----------------------------------------
            default:
                
                //log an error with the unknown ID to LogCat
                Log.e(TAG, "doClick(): unknown switch ID: " + v.getId());
                
                break;
        }
        
    }//end doClick()
    
    //*******************************mkToast()**********************************
    /** Task: display the specified toast message for a specified duration.
    * 
    *   @param msg is the string to display
    *   @param duration is the length of time to display the message
    */ 
    public void mkToast(String msg, int duration)
    {
        //create a toast message with the specified text, and duration
        Toast text = Toast.makeText(getApplicationContext(), 
                                    msg, 
                                    duration);
        
        //change toast location to center horizontal and closer to the top
        text.setGravity(Gravity.CENTER, 0, -60);
        
        //show the toast message
        text.show();
    }    
    
    //****************************setupImageMap()*******************************
    /** Task: Initializes the hashmap to store image IDs. Assigns key and value
    *        for each state. 
    */ 
    public void setupImageMap()
    {
        imageMap = new HashMap<String, Integer>();
        
        imageMap.put("Alabama", R.drawable.alabama);
        imageMap.put("Alaska", R.drawable.alaska );
        imageMap.put("Arizona", R.drawable.arizona);
        imageMap.put("Arkansas", R.drawable.arkansas);
        imageMap.put("California", R.drawable.california);
        imageMap.put("Colorado", R.drawable.colorado);
        imageMap.put("Connecticut", R.drawable.connecticut);
        imageMap.put("Delaware", R.drawable.delaware);
        imageMap.put("Florida", R.drawable.florida);
        imageMap.put("Georgia", R.drawable.georgia);
        imageMap.put("Hawaii", R.drawable.hawaii);
        imageMap.put("Idaho", R.drawable.idaho);
        imageMap.put("Illinois", R.drawable.illinois);
        imageMap.put("Indiana", R.drawable.indiana);
        imageMap.put("Iowa", R.drawable.iowa);
        imageMap.put("Kansas", R.drawable.kansas);
        imageMap.put("Kentucky", R.drawable.kentucky);
        imageMap.put("Louisiana", R.drawable.louisiana);
        imageMap.put("Maine", R.drawable.maine);
        imageMap.put("Maryland", R.drawable.maryland);
        imageMap.put("Massachusetts", R.drawable.massachusetts);
        imageMap.put("Michigan", R.drawable.michigan);
        imageMap.put("Minnesota", R.drawable.minnesota);
        imageMap.put("Mississippi", R.drawable.mississippi);
        imageMap.put("Missouri", R.drawable.missouri);
        imageMap.put("Montana", R.drawable.montana);
        imageMap.put("Nebraska", R.drawable.nebraska);
        imageMap.put("Nevada", R.drawable.nevada);
        imageMap.put("New Hampshire", R.drawable.newhampshire);
        imageMap.put("New Jersey", R.drawable.newjersey);
        imageMap.put("New Mexico", R.drawable.newmexico);
        imageMap.put("New York", R.drawable.newyork);
        imageMap.put("North Carolina", R.drawable.northcarolina);
        imageMap.put("North Dakota", R.drawable.northdakota);
        imageMap.put("Ohio", R.drawable.ohio);
        imageMap.put("Oklahoma", R.drawable.oklahoma);
        imageMap.put("Oregon", R.drawable.oregon);
        imageMap.put("Pennsylvania", R.drawable.pennsylvania);
        imageMap.put("Rhode Island", R.drawable.rhodeisland);
        imageMap.put("South Carolina", R.drawable.southcarolina);
        imageMap.put("South Dakota", R.drawable.southdakota);
        imageMap.put("Tennessee", R.drawable.tennessee);
        imageMap.put("Texas", R.drawable.texas);
        imageMap.put("Utah", R.drawable.utah);
        imageMap.put("Vermont", R.drawable.vermont);
        imageMap.put("Virginia", R.drawable.virginia);
        imageMap.put("Washington", R.drawable.washington);
        imageMap.put("West Virginia", R.drawable.westvirginia);
        imageMap.put("Wisconsin", R.drawable.wisconsin);
        imageMap.put("Wyoming", R.drawable.wyoming);
        
    }//end setImageMap()
    
    //***************************setupNextQuestion()****************************
    /** Task: randomly generate a number to select the next question. parse the
    *         state and capital from the random index number and store them.
    *         update the GUI to show the next question, and update the hint
    *         text to display if the user should enter state/capital. remove
    *         the user's entry from the previous question and focus the text
    *         field. if there are no more questions to set up, the game is over
    *         and pass the users name and score to the Scores.java activity.  
    */
    public void setupNextQuestion()
    {
        //if there are no more questions to set up
        if(pairs.size() == 0)
        {
            //load the Scores.class activity >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            //create the Scores intent
            Intent in = new Intent(getApplicationContext(), Scores.class);
            
            //indicate a new score is being sent
            in.putExtra("newScore", true);
            
            //pass the name of the player
            in.putExtra("name", name);
            
            //pass the players score
            in.putExtra("currentScore", userScore);
            
            //start the activity
            startActivity(in);
            
            //return to main screen after hitting "back" button from the score
            //screen
            finish();
        }
        //otherwise setup the next question
        else
        {  
            //generate random number between 0 and size of the ArrayList >>>>>>>
            Random randomGenerator = new Random();
            randomNumber = randomGenerator.nextInt(pairs.size());
            
            //parse the state and capital from the random location >>>>>>>>>>>>>
            String[] currentPair = pairs.get(randomNumber).split(",");
            
            //set current state and capital
            currentState = currentPair[0];
            currentCapital = currentPair[1];
            
            //shrink arraylist to fit number of entries left 
            pairs.trimToSize();

            //update GUI elements >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            //if a state is shown
            if(stateMode)
            {
                //update the state to display
                currentText.setText(currentState);
                
                //set the text field hint to enter a capital
                textField.setHint(getString(R.string.capital_Hint));
                
                //update the image to display the corresponding state image
                currentImage.setImageDrawable(
                        getResources().getDrawable(imageMap.get(currentState)));
            }
            //if a capital is shown
            else
            {
                //update the capital to display
                currentText.setText(currentCapital);
                
                //set the text field hint to enter a state
                textField.setHint(getString(R.string.state_Hint));
                
                //update the image to display the blur map with question mark 
                //image
                currentImage.setImageResource(R.drawable.blurmap);
            }
            
            //set focus to text field
            textField.requestFocus();
            
            //remove user's last answer from the text field
            textField.setText("");
        }
        
    }//end setupNextQuestion()
    
    //******************************updateScore()*******************************
    /** Task: Update the users score. If they answered correctly, increase their
    *         score by 1. Update the questionNumber. Calculate the user's 
    *         grade percentage. If their score is 80% or above, make the text
    *         green, if their score is 60% or above, make their score orange, 
    *         and if their score is less than 60%, make their score red. Update
    *         the score GUI.
    * 
    *   @param correct indicates if the user's answer was correct or not.
    */  
    public void updateScore(Boolean correct)
    {
        //if the user enter a correct answer >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if(correct)
        {
            //update their score by 1
            userScore++;
        }
        
        //update the question number by 1 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        questionNumber++;
        
        //calculate score color >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        double userPercent = ((double)questionNumber)/userScore;
        
        //if their average is 80% or above
        if(userPercent <= 1.25)
        {
            //set the score text to green
            scoreField.setTextColor(
                                   getResources().getColor(R.color.scoreGreen));
        }
        //if their average is 60-79%
        else if(userPercent > 1.25 && userPercent<= 1.6)
        {
            //set the score text to orange
            scoreField.setTextColor(getResources().getColor(R.color.orange));
        }
        //if their average is below 60%
        else
        {
            //set the score text to red
            scoreField.setTextColor(getResources().getColor(R.color.red));
        }
        
        //update Score GUI with current score >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        scoreField.setText(userScore + "/" + questionNumber);
 
    }//end updateScore()
    
    //*****************************verifyAnswer()*******************************
    /** Task: this method checks a user's answer. user's input has spaces and
    *         newline characters removed. display a toast message informing the
    *         user that their answer is correct/incorrect. remove the current
    *         question to avoid repetition. set up the next question. If there
    *         are no more questions, skip validation.     
    */
    public void verifyAnswer()
    {
        //get user response >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //(remove space and newline characters)
        String userResponse = textField.getText().toString();
        userResponse = userResponse.replaceAll(" ","").replaceAll("\n", "");
                
        //if all of the questions have been answered >>>>>>>>>>>>>>>>>>>>>>>>>>>
        //(this is only applicable if the user hit's the submit button after
        // returning to this activity from the Scores activity.)
        if(questionNumber >= 50)
        {
            //display a message directing the user to start a new game
            mkToast(getString(R.string.gameOver_Toast), 
                    Toast.LENGTH_SHORT);
            
            //end this method with no further action
            return;
        }
                
        //if the user hasn't entered an answer >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if(userResponse.length() == 0)
        {
            //if a state is displayed
            if(stateMode)
            {
                //display a message directing the user to enter a capital
                mkToast(getString(R.string.enterCapital_Toast), 
                        Toast.LENGTH_SHORT);
                
                //end this method with no further action
                return;
            }
            //if a capital is displayed
            else
            {
                //display a message directing the user to enter a state
                mkToast(getString(R.string.enterState_Toast), 
                        Toast.LENGTH_SHORT);
                
                //end this method with no further action
                return;
            }
        }
        
        //Validate user's answer >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //if a state is displayed (user enters it's capital) 
        if(stateMode)
        {   
            //if users response is correct 
            //(ignore case and remove spaces from the current capital answer)
            if(userResponse.equalsIgnoreCase(currentCapital.replaceAll(" ","")))
            {                   
                //display "correct" toast
                mkToast(getString(R.string.correct_Toast), Toast.LENGTH_SHORT);
                
                //update user score and question number
                updateScore(true); //true means: increase the score by 1
            }
            //wrong answer
            else
            {
                //display "incorrect" toast and provide the correct answer
                mkToast(getString(R.string.incorrect_Toast) + currentCapital, 
                        Toast.LENGTH_SHORT);

                //update only the question number
                updateScore(false); //false means: do not increase the score
            }
        }
        //if a capital is displayed (user enters it's state)
        else
        {   
            //if users response is correct 
            //(ignore case and remove spaces from the current state answer)
            if(userResponse.equalsIgnoreCase(currentState.replaceAll(" ","")))
            {                             
                //display "correct" toast
                mkToast(getString(R.string.correct_Toast), 
                        Toast.LENGTH_SHORT);
                
                //update user score and question number
                updateScore(true); //true means: increase the score by 1
            }
            //wrong answer
            else
            {
                //display "incorrect" toast and provide the correct answer
                mkToast(getString(R.string.incorrect_Toast) + currentState, 
                        Toast.LENGTH_SHORT);

                //update only the question number
                updateScore(false); //false means: do not increase the score
            }
        }//end validate answer
        
     
        //if there are still questions left to answer >>>>>>>>>>>>>>>>>>>>>>>>>>
        if(pairs.size() != 0)
        {
            //remove the current state/capital combination from the array to 
            //avoid repeating questions 
            pairs.remove(randomNumber);
            
            //set up the next question
            setupNextQuestion();
        }
        
    }//end verifyAnswer()    

}//end Play.java
