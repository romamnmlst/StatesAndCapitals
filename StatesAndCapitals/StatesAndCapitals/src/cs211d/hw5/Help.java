//Matthew Usnick
//CS211D HW5: States & Capitals
//Help.java

package cs211d.hw5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Help @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
/** Task: This activity is called from StatesAndCapitals.java and provides a
*         scrollable help text to guide the user in how to play this game and
*         navigate the app.
*
* @author Matthew Usnick
*/
public class Help extends Activity
{
    // __________________________Class Variables _______________________________
    //Define the Log tag for this activity
    private static final String TAG = Help.class.getSimpleName();
    
    ////////////////////////////////onCreate()//////////////////////////////////
    /** Task: onCreate is called when this activity is first created.
    *         This method calls it's super version of itself, as well as setting
    *         the content view to the help.xml layout. 
    *
    * @param b is a Bundle object
    */
    @Override //annotation
    public void onCreate(Bundle savedInstanceState) 
    {
        //required actions >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        //pass Bundle to super version of this method
        super.onCreate(savedInstanceState);
        
        //Set content view to help.xml
        setContentView(R.layout.help);
        
        //check for null invocation >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        Intent in = this.getIntent(); 
        if(in == null)
        {
            Log.d(TAG, "No intent invoked this activity.");
        }
        
    }//end onCreate()
}//end Help.java
