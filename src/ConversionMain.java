/**
 * ConversionMain.java
 *
 * This program takes in a instructions to a NFA that is specified in a text file.
 * Then converts the NFA instructions into instructions to create a DFA.
 *
 * Author: Spencer McDonald and Jack Li
 */

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ConversionMain {
    public static void main(String [] args){
        if(args.length <= 1){ //If no filename was entered
            System.out.println("Error: Invalid File Name");
            System.exit(1);
        }
        String filename = args[0];
        String outputname = args[1];
        readFile(filename, outputname);
    }

    public static void readFile(String filename, String outputname){
        int nfa_num_states = 0; //number of states in NFA
        char[] temp_alphabet;
        ArrayList<Character> alphabet = new ArrayList<Character>(); //ArrayList that contains alphabet
        ArrayList<NFATransitionNode> nfa_transitions = new ArrayList<NFATransitionNode>(); //ArrayList that contains transitions for NFA
        int nfa_start_state = 0;
        int[] nfa_accept_states = null;

        try {
            //Initialize FileReader and BufferedReader
            FileReader file_reader = new FileReader(filename);
            BufferedReader buffered_reader = new BufferedReader(file_reader);

            //Get number of states in NFA
            nfa_num_states = Integer.parseInt(buffered_reader.readLine());
            /*System.out.println(nfa_num_states);*/

            //Store alphabet in array
            temp_alphabet = (buffered_reader.readLine()).toCharArray();
            for(int temp = 0; temp < temp_alphabet.length; temp++){
                alphabet.add(temp_alphabet[temp]);
            }
            /*for(int b = 0; b < temp_alphabet.length; b++){
                System.out.print(temp_alphabet[b]);
            }*/

            System.out.println();
            //Read in transitions and store them in an ArrayList of Transition Objects
            String line;
            while((line = buffered_reader.readLine()) != null){
                StringTokenizer strtok = new StringTokenizer(line);
                if(strtok.countTokens() != 0) {
                    String in_state = strtok.nextToken();
                    String temp = strtok.nextToken();
                    char transition = temp.charAt(1);
                    String to_state = strtok.nextToken();

                    //Create and store data in NFATransitionNode
                    NFATransitionNode obj = new NFATransitionNode(in_state, transition, to_state);
                    nfa_transitions.add(obj);
                } else {
                    break; //When reached empty line, break out of loop
                }
            }
            /*for(int c = 0; c < nfa_transitions.size(); c++){
                NFATransitionNode node = nfa_transitions.get(c);
                System.out.println(node.getInState() + " " + node.getTransition() + " " + node.getToState());
            }*/

            //Get start state
            nfa_start_state = Integer.parseInt(buffered_reader.readLine());
            /*System.out.println(nfa_start_state);*/

            //Get NFA accept states
            line = buffered_reader.readLine();
            StringTokenizer strtok2 = new StringTokenizer(line);
            nfa_accept_states = new int[strtok2.countTokens()];
            for(int a = 0; a < nfa_accept_states.length; a++){
                nfa_accept_states[a] = Integer.parseInt(strtok2.nextToken());
                /*System.out.print(nfa_accept_states[a] + " ");*/
            }
            buffered_reader.close();
        } catch(FileNotFoundException e) {
            System.out.println(e);
            System.exit(1);
        } catch(IOException e) {
            System.out.println(e);
            System.exit(1);
        }

        //After reading in the file, create the DFA
        createDFA(nfa_num_states, alphabet, nfa_transitions, nfa_start_state, nfa_accept_states, outputname);
    }

    public static void createDFA(int nfa_num_states, ArrayList<Character> alphabet, ArrayList<NFATransitionNode> nfa_transitions, int nfa_start_state, int[] nfa_accept_states, String outputname){
        //Get the start state for the DFA
        DFASetNode dfa_start_state = getStartState(alphabet, nfa_transitions, nfa_start_state, nfa_accept_states);
        /*for(int test = 0; test < dfa_start_state.getNFAStates().size(); test++){
            System.out.print(dfa_start_state.getNFAStates().get(test) + " ");
        }*/

        //This is a ArrayList that contains all of the DFASetNodes
        ArrayList<DFASetNode> dfa = new ArrayList<DFASetNode>();
        dfa.add(dfa_start_state);
        for(int loop = 0; loop < dfa.size(); loop++){
            DFASetNode obj = dfa.get(loop);
            ArrayList<DFASetNode> next = new ArrayList<DFASetNode>();
            //If it is a dead state then skip
            if(obj.getDeadState() == false) {
                for (int loop2 = 0; loop2 < alphabet.size(); loop2++) {
                    DFASetNode new_dfa_node = createDFASetNode(alphabet, nfa_transitions, dfa.get(loop), nfa_accept_states, alphabet.get(loop2));
                    int check = checkInDFA(dfa, new_dfa_node);
                    if(check == -1){
                        new_dfa_node.setID(dfa.size() + 1);
                        dfa.add(new_dfa_node);
                        next.add(new_dfa_node);
                    } else if(check == -2) {
                        int deadcheck = checkDeadNodeExists(dfa);
                        if(deadcheck == -1){
                            new_dfa_node.setID(dfa.size() + 1);
                            dfa.add(new_dfa_node);
                            next.add(new_dfa_node);
                        } else {
                            new_dfa_node.setID(deadcheck + 1);
                            next.add(new_dfa_node);
                        }
                    } else {
                        new_dfa_node.setID(check + 1);
                        next.add(new_dfa_node);
                    }
                }
                obj.setToStates(next);
                dfa.set(loop, obj);
            }
        }
        writeDFA(alphabet, dfa, outputname);
    }

    public static void writeDFA(ArrayList<Character> alphabet, ArrayList<DFASetNode> dfa, String outputname){
        try {
            FileWriter writer = new FileWriter(outputname, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            //write number of states
            int num_states = dfa.size();
            bufferedWriter.write("" + num_states);
            bufferedWriter.newLine();

            //write alphabet
            String alph = "";
            for(int alphloop = 0; alphloop < alphabet.size(); alphloop++){
                alph += "" + alphabet.get(alphloop);
            }
            bufferedWriter.write(alph);
            bufferedWriter.newLine();

            //keep track of accept states
            ArrayList<String> accept_states = new ArrayList<String>();
            //write transitions
            for(int dfaloop = 0; dfaloop < dfa.size(); dfaloop++){
                DFASetNode tran = dfa.get(dfaloop);
                if(tran.getAccept() == true){
                    accept_states.add("" + tran.getID());
                }
                for(int loop = 0; loop < alphabet.size(); loop++){
                    DFASetNode next = tran.getToStates().get(loop);
                    bufferedWriter.write("" + tran.getID() + " '" + alphabet.get(loop) + "' " + next.getID());
                    bufferedWriter.newLine();
                }
            }
            //write start state
            bufferedWriter.write("1");
            bufferedWriter.newLine();

            //write accept states
            String accept = "";
            for(int accloop = 0; accloop < accept_states.size(); accloop++){
                accept += accept_states.get(accloop) + " ";
            }
            bufferedWriter.write(accept);
            bufferedWriter.close();

        } catch (IOException e){
            System.out.println(e);
            System.exit(1);
        }
    }
    public static int checkDeadNodeExists(ArrayList<DFASetNode> dfa){
        for(int loop = 0; loop < dfa.size(); loop++){
            DFASetNode temp = dfa.get(loop);
            if(temp.getDeadState() == true){
                return loop;
            }
        }
        return -1;
    }

    //returns index number if it already exists
    public static int checkInDFA(ArrayList<DFASetNode> dfa, DFASetNode new_dfa_node){
        ArrayList<String> new_node_states = new_dfa_node.getNFAStates();
        if(new_node_states.size() == 0){
            return -2;
        }
        for(int loop = 0; loop < dfa.size(); loop++){
            ArrayList<String> dfa_states = dfa.get(loop).getNFAStates();
            if(dfa_states.size() == new_node_states.size()){
                int count = 0;
                for(int loop2 = 0; loop2 < dfa_states.size(); loop2++){
                    for(int loop3 = 0; loop3 < new_node_states.size(); loop3++){
                        if(dfa_states.get(loop2).compareTo(new_node_states.get(loop3)) == 0){
                            count++;
                        }
                    }
                }
                if(count == new_node_states.size()){
                    return loop;
                }
            }
        }
        return -1;
    }
    public static DFASetNode createDFASetNode(ArrayList<Character> alphabet, ArrayList<NFATransitionNode> nfa_transitions, DFASetNode dfa_node, int[] nfa_accept_nodes, char charac){
        DFASetNode ret = new DFASetNode(alphabet);
        ArrayList<String> new_nfa_states = new ArrayList<String>();
        ArrayList<String> nfa_states = dfa_node.getNFAStates();

        for(int loop = 0; loop < nfa_states.size(); loop++){
            for(int loop2 = 0; loop2 < nfa_transitions.size(); loop2++){
                NFATransitionNode temp = nfa_transitions.get(loop2);
                if(temp.getInState().compareTo(nfa_states.get(loop)) == 0 &&  (temp.getTransition() == charac)){
                    if(checkIt(new_nfa_states , temp.getToState()) == true){
                        new_nfa_states.add(temp.getToState());
                    }
                }
            }
        }

        //Follow epsilon transitions
        for(int loop3 = 0; loop3 < new_nfa_states.size(); loop3++){
            for(int loop4 = 0; loop4 < nfa_transitions.size(); loop4++){
                NFATransitionNode temp2 = nfa_transitions.get(loop4);
                if(temp2.getInState().compareTo(new_nfa_states.get(loop3)) == 0 && (temp2.getTransition() == 'e')){
                    if(checkIt(new_nfa_states, temp2.getToState()) == true){
                        new_nfa_states.add(temp2.getToState());
                    }
                }
            }
        }
        /*for(int test = 0; test < new_nfa_states.size(); test++){
            System.out.print(new_nfa_states.get(test) + " ");
        }*/

        ret.setNFAStates(new_nfa_states);
        //checks to see if there are any NFA transitions
        if(new_nfa_states.size() == 0){
            ret.setDeadState(true);
            ArrayList<DFASetNode> dfa_next = new ArrayList<DFASetNode>();
            //Sets next set nodes to itself
            for(int deadloop = 0; deadloop < alphabet.size(); deadloop++){
                    dfa_next.add(ret);
            }
            ret.setToStates(dfa_next);
        } else {
            //Determines if it is a accepting state
            for (int alphloop = 0; alphloop < nfa_accept_nodes.length; alphloop++) {
                for (int alphloop2 = 0; alphloop2 < new_nfa_states.size(); alphloop2++) {
                    if (nfa_accept_nodes[alphloop] == Integer.parseInt(new_nfa_states.get(alphloop2))) {
                        ret.setAccept(true);
                        break;
                    }
                }
                if (ret.getAccept() == true) {
                    break;
                }
            }
        }
        return ret;
    }

    //This gets the start state for the dfa and returns it located in the object DFASetNode
    public static DFASetNode getStartState(ArrayList<Character> alphabet, ArrayList<NFATransitionNode> nfa_transitions, int nfa_start_state, int[] nfa_accept_states){
        DFASetNode ret = new DFASetNode(alphabet);
        ret.setID(1);
        ret.setStart(true);
        //Create ArrayList to store nfa state numbers
        ArrayList<String> nfa_states = new ArrayList<String>();
        nfa_states.add("" + nfa_start_state);
        //loop through the nfa state numbers
        for(int loop = 0; loop < nfa_states.size(); loop++){
            //Loop through all the NFA transition nodes
            for(int temp = 0; temp < nfa_transitions.size(); temp++) {
                NFATransitionNode obj = nfa_transitions.get(temp);
                if (obj.getInState().compareTo("" + nfa_states.get(loop)) == 0) {
                    //If the transition is a epsilon transition
                    if (obj.getTransition() == 'e') {
                        //check if the nfa state number is already in the ArrayList
                        boolean check = checkIt(nfa_states, obj.getToState());
                        if (check == true) {
                            nfa_states.add(obj.getToState());
                        }
                    }
                }
            }
        }
        ret.setNFAStates(nfa_states);
        for(int outloop = 0; outloop < nfa_accept_states.length; outloop++) {
            for (int accloop = 0; accloop < nfa_states.size(); accloop++) {
                if(nfa_accept_states[outloop] == Integer.parseInt(nfa_states.get(accloop))){
                    ret.setAccept(true);
                    break;
                }
            }
            if(ret.getAccept() == true){
                break;
            }
        }
        return ret;
    }

    //This method checks to see if a state is already in the ArrayList so there won't be duplicates
    public static boolean checkIt(ArrayList<String> nfa_states, String state){
        for(int temp = 0; temp < nfa_states.size(); temp++){
            if(nfa_states.get(temp).compareTo(state) == 0){
                return false;
            }
        }
        return true;
    }

}
