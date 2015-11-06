import java.util.ArrayList;

/**
 * Created by Spencer on 11/3/2015.
 */
public class DFASetNode {
    private boolean start; //indicates whether it is the start state
    private boolean accept; //Indicates whether it is a accept state
    private boolean dead_state; //Indicates that this is a state that has a empty nfa_states ArrayList and rejects and loops to itself.
    private int id; //indicates state number in DFA
    private ArrayList<String> nfa_states; //Indicates the state numbers of the NFA that are in this DFA Node
    private ArrayList<Character> transitions; //This is every transition, so it is just the alphabet of the DFA
    private ArrayList<DFASetNode> to_states; //This will be the next DFASetNode that corresponds to the transition in the same index.

    public DFASetNode(ArrayList<Character> transitions){
        this.start = false;
        this.accept = false;
        this.dead_state = false;
        this.id = 0;
        this.nfa_states = new ArrayList<String>();
        this.transitions = transitions;
        this.to_states = new ArrayList<DFASetNode>();
    }

    //getter methods
    public boolean getStart(){ return this.start;}
    public boolean getAccept(){ return this.accept;}
    public boolean getDeadState(){ return this.dead_state;}
    public int getID(){ return this.id;}
    public ArrayList<String> getNFAStates(){ return this.nfa_states;}
    public ArrayList<Character> getTransitions() { return this.transitions;}
    public ArrayList<DFASetNode> getToStates() { return this.to_states;}

    //Setting methods
    public void setStart(boolean val){ this.start = val;}
    public void setAccept(boolean val){ this.accept = val;}
    public void setDeadState(boolean val){ this.dead_state = val;}
    public void setID(int val){ this.id = val;}
    public void setNFAStates(ArrayList<String> val){ this.nfa_states = val;}
    public void setToStates(ArrayList<DFASetNode> val){ this.to_states = val;}

}
