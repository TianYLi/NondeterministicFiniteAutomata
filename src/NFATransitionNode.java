/**
 * Created by Spencer on 11/2/2015.
 */
public class NFATransitionNode {
    private String in_state; //indicates state that you are in
    private char transition; //indicates transition character
    private String to_state; //indicates state to go to if transition character is read

    public NFATransitionNode(String in_state, char transition, String to_state){
        this.in_state = in_state;
        this.transition = transition;
        this.to_state = to_state;
    }

    public String getInState(){
        return this.in_state;
    }

    public char getTransition(){
        return this.transition;
    }

    public String getToState(){
        return this.to_state;
    }
}
