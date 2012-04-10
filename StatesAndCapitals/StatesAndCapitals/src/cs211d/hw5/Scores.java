//Matthew Usnick
//CS211D HW5: States & Capitals
//Scores.java

package cs211d.hw5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Scores @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
/** Task: This activity prints the scores page. if there is a new score, it is
*         displayed in yellow, otherwise scores are printed in blue. 
*         This activity checks to see if the SD card is available, and if it is
*         loads the scores.txt. If scores.txt does not exist, it is created.
*         New scores are also appended to the file. 
*
* @author Matthew Usnick
*/
public class Scores extends Activity
{
    // __________________________Class Variables _______________________________
    //ArrayList of Score objects ---------
    ArrayList<Score>  scoreArrayList;
    
    //GUI elements -----------------------
    
    LinearLayout ll;
    LinearLayout scoreContainer;          //contains all scoreEntry additions
    ScrollView scrollLayout;              //contains scoreContainer
    LinearLayout entryToFocus;            //variable to track which scoreEntry
                                          //to highlight

    //Score variables --------------------
    String name;                          //user name
    int currentScore;                     //user score
    boolean newScore;                     //true if a new score is added
    
    //storage booleans -------------------
    boolean storageAvailable = false;
    boolean storageWritable = false;
    
    //Log tag____________________
    private static final String TAG = Help.class.getSimpleName();
    
    ////////////////////////////////onCreate()//////////////////////////////////
    /** Task: onCreate is called when this activity is first created.
    *         This method calls it's super version of itself, as well as setting
    *         the content view to the scoreview.xml layout. The score data is
    *         read from the SD card, if a new score is passed from Play.java it 
    *         is added to the score file on the SD card, and added to the list
    *         of scores to display. The scores are finally printed, in 
    *         descending order on the device. If a new score was just added, it
    *         will be highlighted in yellow, and the entry will be scrolled to
    *         be visible (if it is out of view).
    *
    * @param b is a Bundle object
    */
    @Override //annotation
    public void onCreate(Bundle b)
    {
        //required actions >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //pass bundle to super version of onCreate()
        super.onCreate(b);
        
        //set the layout for this activity (scoreview.xml)
        setContentView(R.layout.scoreview);
      
        //check for null invocation >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        Intent in = this.getIntent(); 
        if(in == null)
        {
            Log.d(TAG, "No intent invoked this activity.");
        }
        
        //initialize variables >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //initialize ArrayList
        scoreArrayList = new ArrayList<Score>();

        //initialize entryToFocus variable
        entryToFocus = null; 
    
        //load score data from SD card >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        loadScores();
        
        //check to see if a new score was added from the Play.java activity >>>>
        checkForNewScore();
        
        //sort the Score arraylist in descending order >>>>>>>>>>>>>>>>>>>>>>>>>
        Collections.sort(scoreArrayList);

        //initialize GUI elements >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        scrollLayout = (ScrollView)findViewById(R.id.scrollScores);
        ll = (LinearLayout)findViewById(R.id.scoreContainer);
        
        //print all scores on the device >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        printScores();
        
    }//end onCreate()
    
    //****************************appendScore()*********************************
    /** Task: this method attempts to add a new score to the scores.txt file on
    *         the SD card. If the file cannot be accessed or appended, display
    *         a toast message alerting the user that there was a problem in
    *         loading the score data.
    *         
    * @param name is the player name to add to scores.txt
    * @param score is the player score to add to scores.txt      
    */
    public void appendScore(String name, int score)
    {
        //if SDcard is mounted and writable >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if(storageIsAvailable())
        {
            //get the SD Card directory
            File sdCard = Environment.getExternalStorageDirectory();
     
            //get the absolute path of the SD card
            File dir = new File(sdCard.getAbsolutePath());

            //set file name to "scores.txt"
            String fileName = "scores.txt";
            
            //load the score file
            File contentFile = new File (dir, fileName);
            
            //try to write to file
            try
            {
                //create printwriter and allow appending
                PrintWriter pw = 
                             new PrintWriter(new FileWriter(contentFile, true));
                
                //append name and score, delimited by ","
                pw.println(name + "," + score );
                
                //flush and close the printwriter
                pw.flush();
                pw.close();
            }
            //catch any errors in appending the file
            catch (Exception e)
            {
                //log the error
                Log.e(TAG, 
                     "appendScore(): catch block: Error appending score file: " 
                    + e);
            }
        }
        //if sdCard is not available
        else
        {
            mkToast(getString(R.string.scoreFileNotLoaded_Toast), 
                    Toast.LENGTH_LONG);
        }
        
    }//end appendScore()
    
    //***************************checkForNewScore()*****************************
    /** Task: display the specified toast message for a specified duration.
    * 
    *   @param msg is the string to display
    *   @param duration is the length of time to display the message
    */ 
    public void checkForNewScore()
    {
        //if a new score was passed to this activity from the Play.java activity
        //set newScore to true. If no score was passed, set it to false
        newScore = getIntent().getBooleanExtra("newScore",false);
        
        //if a new score was passed >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if(newScore)
        {
            //get user name
            name = getIntent().getStringExtra("name");
            
            //get user score
            currentScore = getIntent().getIntExtra("currentScore", 0);
            
            //create score object with name, score, and set currentScore to true
            scoreArrayList.add(new Score(name, currentScore, newScore));
            
            //append score file
            appendScore(name, currentScore);
        }
        
    }//end checkForNewScore()
    
    //**************************installScoreFile()******************************
    /** Task: attempts to install the scores.txt file. Header and divider are 
    *         added. Scores will be added by completeing the game, via 
    *         appendScore()
    *        
    * @param fileToInstall is the name of the file to install
    */
    public void installScoreFile(String fileToInstall)
    {
        //attempt to install the score file >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        try
        {
            //load the specified file name
            File file = new File(fileToInstall); 
     
            //create a printwriter for the file
            PrintWriter pw = new PrintWriter(file);

            //add section titles and divider to the file
            pw.println("Name,Score");
            pw.println("----------");
            
            //flush and close the print writer
            pw.flush(); 
            pw.close();
        }
        catch(IOException e)
        {
            Log.e(TAG, "installScoreFile() catch block: " + e.toString());
        }

    }//end installScoreFile()
        
    //*****************************loadScores()*********************************
    /** Task: attempts to load scores from scores.txt. Checks to see if SD card
    *         is available. If it is, checks to see if the file is available.
    *         If there is not file, create it. Then load the file. 
    */
    public void loadScores()
    {
        //if SDcard is mounted and writable >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if(storageIsAvailable())
        {
            //get the score file >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            //get the SD Card directory
            File sdCard = Environment.getExternalStorageDirectory();
     
            //get the absolute path of the SD card
            File dir = new File(sdCard.getAbsolutePath());

            //set file name
            String fileName = "scores.txt";
            
            //load the file
            File scoreFile = new File (dir, fileName);
            
            //if the file doesn't exist >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            if(!scoreFile.exists())
            {
                //install the file
                installScoreFile(dir + "/" + fileName);
            }

            //try loading the score file >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            try
            {
                //load file into scanner
                Scanner sc = new Scanner(scoreFile);
                 
                //Throw away column titles and dividing lines 
                //(1st 2 lines of file)
                for(int i=0; i<2; i++)
                {
                    sc.nextLine();
                }
                
                //while there a scores to load >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                while(sc.hasNextLine())
                {                   
                    //parse name and score from line
                    String[] temp = sc.nextLine().split(",");
                    
                    //add new score object with name, score, and set 
                    //currentScore to false
                    scoreArrayList.add(new Score(temp[0], 
                                                 Integer.parseInt(temp[1]),
                                                 false));
                }
            }
            //catch file not found >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            catch (FileNotFoundException e)
            {
                mkToast(
                        getResources().getString(R.string.fileNotLoaded_Toast),
                        Toast.LENGTH_LONG);
                
                Log.e(TAG, "loadScores() catch block: " + e.toString());
            }
            
        }
        //if sdCard is not available >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        else
        {
            Log.e(TAG, "loadScores() else: SD card not available." );
        }
        
    }//end loadScores()
     
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

    //****************************printScores()*********************************
    /** Task: print the scores on the device. Add text views to  linear layouts
    *         which are added to a layout container. If there is a new score 
    *         to display, highlight it in yellow. 
    *         
    *         Note: design issue: layout information was not being recognized 
    *               from the xml file. I had to manually set the layout params
    *               in the java code. Need to figure this out for future use.
    */ 
    public void printScores()
    {
        //for all of the scores in the arraylist >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        for(int i = 0; i<scoreArrayList.size(); i++)
        {
            //create GUI element >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            //create a scoreEntry layout from scoreentry.xml
            LinearLayout scoreEntry = (LinearLayout) 
                         getLayoutInflater().inflate(R.layout.scoreentry, null);
            
            //create a textview for rank number with specified layout params
            TextView rank = (TextView)
                getLayoutInflater().inflate(R.layout.scoreposition_layout,null);
            rank.setLayoutParams(
                new LinearLayout.LayoutParams(90, LayoutParams.WRAP_CONTENT));
            
            //create a textview for player name with specified layout params
            TextView tvName = (TextView)
                    getLayoutInflater().inflate(R.layout.scorename_layout,null);
            tvName.setLayoutParams(
               new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT,8.0f));
            
            //create a textview for player score with specified layout params
            TextView tvScore = (TextView)
                  getLayoutInflater().inflate(R.layout.scorenumber_layout,null);
            tvScore.setLayoutParams(
               new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT,4.0f));
    
            //get the score information to print from the arraylist >>>>>>>>>>>>
            Score scoreToPrint = scoreArrayList.get(i);
            
            //if the score is a new entry >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            if(scoreToPrint.isCurrentScore())
            {
                //set background to be a yellow drawable 
                scoreEntry.setBackgroundDrawable(getResources().getDrawable(
                                       R.drawable.currentscoreentrybackground));
                
                //keep a reference to this entry, so it can be focused 
                entryToFocus = scoreEntry;
            }
            //if score is not a new entry >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            else
            {
                //set background to be a blue drawable
               scoreEntry.setBackgroundDrawable(
                   getResources().getDrawable(R.drawable.scoreentrybackground));
            }
            
            //set text views >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            //set the position number with a period after it
            rank.setText(i+1 + ".");
            
            //set the player name
            tvName.setText(scoreToPrint.getName());
            
            //set the score with /50 after it
            tvScore.setText(scoreToPrint.getScore() + "/50");
            
            //add views >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            //add text views to the scoreEntry layout 
            scoreEntry.addView(rank);
            scoreEntry.addView(tvName);
            scoreEntry.addView(tvScore);
            
            //add score entry layout to the container layout
            ll.addView(scoreEntry);
        }
        
        //if there was a new score entry, focus it on the screen >>>>>>>>>>>>>>>
        if(newScore)
        {
            //create anonymous thread
            scrollLayout.post(new Runnable() 
            { 
                public void run() 
                { 
                    //get the Y position of entryToFocus and scroll to it
                    scrollLayout.scrollTo(0, (int)entryToFocus.getY()); 
                } 
            });
        }
    }//end printScores()
    
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
    
}//end Scores.java
