//Matthew Usnick
//CS211D HW5: States & Capitals
//Score.java

package cs211d.hw5;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Score @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
/** Task: This is a Score object. It contains a users name, score, and if it
*         is the currently active score in the game (which will be highlighted
*         in the score ranking).
*
* @author Matthew Usnick
*/
public class Score implements Comparable<Score>
{
    //private variables --------------------------------------------------------
    private String name;                //player name
    private int score;                  //player score
    private boolean isCurrentScore;     //if true this entry will be highlighted
                                        //if false this entry will be displayed
                                        //normally
    
    //Constructors >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public Score()
    {
        this("J. Doe", 0, false);
    }
    
    public Score(String n, int s, boolean b)
    {
        setName(n);
        setScore(s);
        setCurrent(b);
    }
    
    //Getters >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public String getName()
    {
        return name;
    }
    
    public int getScore()
    {
        return score;
    }
    
    public boolean isCurrentScore()
    {
        return isCurrentScore;
    }

    //Setters >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public void setName(String n)
    {
        name = n;
    }
    
    public void setScore(int s)
    {
        score = s;
    }
    
    public void setCurrent(boolean b)
    {
        isCurrentScore = b;
    }
    
    //compareTo >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //objects are sorted in descending order based on the score
    public int compareTo(Score s)
    {
        if(this.score == s.getScore())
        {
            return 0;
        }
        else if(this.score < s.getScore())
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
    
}//end Score.java
