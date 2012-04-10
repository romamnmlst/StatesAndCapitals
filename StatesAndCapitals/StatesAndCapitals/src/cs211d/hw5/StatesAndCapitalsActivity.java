//Matthew Usnick
//CS211D HW5: States & Capitals
//StatesAndCapitalsActivity.java

package cs211d.hw5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Toast;

//@@@@@@@@@@@@@@@@@@@@@@@@ StatesAndCapitalsActivity @@@@@@@@@@@@@@@@@@@@@@@@@@@
/** Task: This activity is the main activity for the States and Capitals app.
 *         All game data is loaded in this activity and passed to the Play.java
 *         activity. There is also buttons to access the Help.java activity, and
 *         the Scores.java activity. Once the user enters their name and hits
 *         the "Play Game" button, the Play.java activity is launched and the 
 *         game begins.
*
* @author Matthew Usnick
*/
public class StatesAndCapitalsActivity extends Activity 
{
    // __________________________Class Variables _______________________________
    EditText nameInput;                   //to get users name
    ArrayList<String> dataArray;          //to store state/capital data 
    boolean fileLoaded;                   //true if us_states.txt is loaded
    boolean storageAvailable = false;     //true if SD card is available
    boolean storageWritable = false;      //true if SD card is writable
    
    //Log tag____________________
    private static final String TAG = Help.class.getSimpleName();
    
    ////////////////////////////////onCreate()//////////////////////////////////
    /** Task: onCreate is called when this activity is first created.
    *         This method calls it's super version of itself, as well as setting
    *         the content view to the main.xml layout. 
    *
    * @param b is a Bundle object
    */
    @Override //annotation
    public void onCreate(Bundle savedInstanceState) 
    {
        //required actions >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //pass bundle to super version of onCreate()
        super.onCreate(savedInstanceState);
        
        //set the layout for this activity (main.xml)
        setContentView(R.layout.main);
        
        //setup EditText >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //load from xml
        nameInput = (EditText)findViewById(R.id.nameEntry);
        
        //set OnKeyListener
        nameInput.setOnKeyListener(createTextListener());
        
        //initalize variables >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //initialize array list
        dataArray = new ArrayList<String>();
        
        //set fileLoaded to false
        fileLoaded = false;

        //load program data >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        loadContent();
        
    }//onCreate()
    
    //**************************createTextListener()****************************
    /** Task: this method creates and returns an anonymous class. This class is
    *         the OnKeyListener for the EditTexts. When the user presses
    *         "Return" in the EditText, the Play.java activity will be called,
    *         and the game will start.
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
                //if the user presses Return
                if((ke.getAction() == KeyEvent.ACTION_DOWN)
                && (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    //play the game
                    playGame();
                    
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
            //if playButton is clicked -----------------------------------------
            case (R.id.playButton):
                
                //call playGame()
                playGame();
                break; 
            
            //if Map menu button is clicked ------------------------------------
            case (R.id.helpButton):
                
                //launch Help.java activity
                Context con = getApplicationContext();
                Intent i = new Intent(con, Help.class);
                startActivity(i);
                break;
                
            //if score button is clicked ---------------------------------------    
            case(R.id.scoreButton):
                
                //launch Scores.java activity
                Context con2 = getApplicationContext();
                Intent in = new Intent(con2, Scores.class);
                startActivity(in);
                break;
            
            //if the ID is unspecified -----------------------------------------
            default:
                
                //log an error with the unknown ID to LogCat
                Log.e(TAG, "doClick(): unknown switch ID: " + v.getId());
                break;
        }
                    
    }//end doClick()
    
    //****************************installFile()*********************************
    /** Task: attempts to install the us_states.txt file from the res/raw file.
    *        
    * @param fileToInstall is the name of the file to install
    */    
    public void installFile(String fileToInstall)
    {
        //attempt to install the data file >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        try
        {
            //create input stream from resource
            InputStream is = getResources().openRawResource(R.raw.us_states);
            
            //create scanner from input stream
            Scanner sc = new Scanner (is);

            //load the specified filename 
            File file = new File(fileToInstall); 
     
            //create printwriter to print to file
            PrintWriter pw = new PrintWriter(file);

            //while the resource has data
            while(sc.hasNext())
            {
                //print a line from the resource to the new file
                pw.println(sc.nextLine());
            }

            //flush and close the print writer
            pw.flush(); 
            pw.close();
            is.close();
            
        }
        catch(IOException e)
        {
            Log.e(TAG, "installFile() catch block: " + e.toString());
        }

    }//end installFile() 

    //*****************************loadContent()********************************
    /** Task: attempts to load data from us_states.txt. Checks to see if SD card
    *         is available. If it is, checks to see if the file is available.
    *         If there is not file, create it. Then load the file. 
    */
    public void loadContent()
    {
        //if SDcard is mounted and writable >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if(storageIsAvailable())
        {
            //get the data file >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            //get the SD Card directory
            File sdCard = Environment.getExternalStorageDirectory();
     
            //get the absolute path of the SD card
            File dir = new File(sdCard.getAbsolutePath());
            
            //set the filename
            String fileName = "us_states.txt";
            
            //get the content file
            File contentFile = new File (dir, fileName);
            
            //if the file doesn't exist >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            if(!contentFile.exists())
            {
                installFile(dir + "/" + fileName);
            } 

            //try loading the data file >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            try
            {
                //load file into scanner
                Scanner sc = new Scanner(contentFile);
                
                //Throw away column titles and dividing lines 
                //(1st 2 lines of file)
                for(int i=0; i<2; i++)
                {
                    sc.nextLine();
                }
             
                //get every line of the file >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                for(int i=0; i<50; i++)
                {
                    //store the line
                    String line = sc.nextLine();
                    
                    //split the lines by multiple white spaces as the delimiter
                    String[] pair = line.split("\\s\\s+");

                    //add state and capital to the arraylist delemited by ","
                    dataArray.add(pair[0] + "," + pair[1]);
                }

                //set fileLoaded to true
                fileLoaded = true;
            }
            //catch file not found >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            catch (FileNotFoundException e)
            {
                mkToast(getString(R.string.fileNotLoaded_Toast),
                        Toast.LENGTH_LONG);
                
                Log.e(TAG, "loadData() catch block: " + e.toString());
                
                //set fileLoaded to false
                fileLoaded = false;
            }
        }
        //if sdCard is not available >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        else
        {
            Log.e(TAG, "loadScores() else: SD card not available." );
        }

    }//end loadData()    
    
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
        text.setGravity(Gravity.CENTER, 0, -80);
        
        //show the toast message
        text.show();
    }
    
    //*******************************playGame()*********************************
    /** Task: this method creates and returns an anonymous class. This class is
    *         the OnKeyListener for the EditTexts. When the user presses
    *         "Return" in the EditText, the Play.java activity will be called,
    *         and the game will start.
    *        
    * @return OnKeyListener       
    */
    public void playGame()
    {
        //get the users name from the edittext 
        String userName = nameInput.getText().toString();
        
        //if the user hasn't enter their name >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if(userName.length() == 0)
        {
            //display a toast message directing them to enter their name
            mkToast(getString(R.string.enterName_Toast),
                    Toast.LENGTH_SHORT);
       
        }
        //if the data file isn't loaded >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        else if(!fileLoaded)
        {
            //display a toast message alerting them that the game data has not
            //loaded.
            mkToast(getString(R.string.fileNotLoaded_Toast),
                    Toast.LENGTH_LONG);    
        }
        //otherwise play the game >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        else
        {
            //create the intent for Play.class
            Context con = getApplicationContext();
            Intent i = new Intent(con, Play.class);
            
            //pass the game data arraylist
            i.putExtra("gameContent", dataArray);
            
            //pass the players name
            i.putExtra("playerName", userName);

            //start the game activity
            startActivity(i);
                
            //when returning to the home screen via the "back" button, set the
            //edit text to empty
            nameInput.setText("");
        }

    }//end playGame()    
 
    //**************************storageIsAvailable()****************************
    /** Task: this method checks to see if the SD card is available and 
    *         writable. 
    *         
    * @return true is storage is available and writable
    */
    public boolean storageIsAvailable()
    {   
        //check storage >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //get storage state
        String state = Environment.getExternalStorageState();
        
        //if the SDcard is mounted and writable
        if(Environment.MEDIA_MOUNTED.equals(state))
        {
            storageAvailable = storageWritable = true;
        }
        //if the SDcard is mounted but not writable 
        else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            storageAvailable = true;
            storageWritable = false;
        }
        //if the SDcard is not mounted
        else
        {
            storageAvailable = storageWritable = false;
        }
        
        //return results >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if(storageAvailable && storageWritable)
        {
            return true;
        }
        else
        {
            return false;
        }
        
    }//end storageIsAvailable()
    
}//end StatesAndCapitalsActivity.java