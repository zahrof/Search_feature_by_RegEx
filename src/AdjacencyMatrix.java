import java.util.*;

public class AdjacencyMatrix {
    int nbreStates;
    ArrayList<State>[][] automata; // Array List?

    public AdjacencyMatrix(int nbreStates) {
        this.nbreStates= nbreStates;
        this.automata =new ArrayList[nbreStates][130];
    }

    @Override
    public String toString() {
        String res="------- AUTOMATE ------- \n";
        int i =0;
        for(int k =0; k <this.nbreStates; k++) {
            for (int j = 97; j <= 99; j++) {
                if (this.automata[i][j] != null) {
                    res += "automata[" + i + "][" + j + "]:" + this.automata[i][j] + "\n";
                }
                if (j == 99) i++;
            }
        }

        return String.format(res);
    }

    private boolean sameStateSet(State elt1, State elt2, Set<SetOfStates> fnf) {
        int iteration =0;
        int findElt1=-1;
        int findElt2=-2;
        for (SetOfStates rs: fnf) {
            for (State s : rs.setOfStates) {
                if((elt1.nameState ==s.nameState)&&(elt1.isFinalState ==s.isFinalState)) findElt1 = iteration;
                if((elt2.nameState ==s.nameState)&&(elt2.isFinalState ==s.isFinalState)) findElt2 = iteration;
            }
            iteration++;
        }
        return findElt1==findElt2;
    }

    public void addTransition(int source, int letterOfTransition, int destination, boolean destinationIsFinal){
        if(this.automata[source][letterOfTransition]==null) this.automata[source][letterOfTransition] = new ArrayList<>();
        this.automata[source][letterOfTransition].add(new State(destination, destinationIsFinal));
    }

    // Renvoi l'indice de l'etat finale. L'etat initial sera tjrs 0.
    // Question 2.2
    public int fillMatrix(RegExTree tree, int counter, int sizeAutomaton){
        int counterStates;
        switch (tree.root){
            case RegEx.ALTERN:
                addTransition(counter,0 ,counter+1, false);
                counterStates = fillMatrix(tree.subTrees.get(0),counter+1,sizeAutomaton);
                int finR1 = counterStates;
                addTransition(counter,0,counterStates+1, false);
                counterStates= fillMatrix(tree.subTrees.get(1),counterStates+1,sizeAutomaton);
                int finR2 = counterStates;
                int fin = counterStates+1;
                counterStates++;
                if(counterStates==(sizeAutomaton-1)) {
                    addTransition(finR1, 0, fin, true);
                    addTransition(finR2, 0, fin, true);
                }else{
                    addTransition(finR1, 0, fin, false);
                    addTransition(finR2, 0, fin, false);
                }

                return fin;
            case RegEx.CONCAT:
                counterStates = fillMatrix(tree.subTrees.get(0),counter,sizeAutomaton);
                addTransition(counterStates,0,counterStates+1, false);
                counterStates++;
                return fillMatrix(tree.subTrees.get(1), counterStates,sizeAutomaton);
            case RegEx.ETOILE:
                addTransition(counter,0,counter+1, false);
                counterStates = fillMatrix(tree.subTrees.get(0),counter+1,sizeAutomaton);
                addTransition(counterStates, 0,counter+1, false);

                if(counterStates==(sizeAutomaton-1)) {
                    addTransition(counter, 0, counterStates + 1, true);
                    addTransition(counterStates, 0, counterStates + 1, true);
                }
                else{
                    addTransition(counter, 0, counterStates + 1, false);
                    addTransition(counterStates, 0, counterStates + 1, false);
                }
                counterStates++;
                return counterStates;
            default: // it is a leaf
                if(counter==0) addTransition(counter, tree.root, counter+1, true);
                else addTransition(counter, tree.root, counter+1, false);
                return counter+1;
        }
    }

    /**
     * Renvoi l'ensemble d'etats atteignable depuis state avec les epsilon transitions
     * @param state
     * @return
     */
    public Set<State> epsilonClosure(State state){
        Set<State> set = new HashSet<>();
        set.add(state);
        Queue<State> q = new LinkedList<>();
        q.add(state);
        while(!q.isEmpty()){
            State j= q.poll();
            if(this.automata[j.getNameState()][0]==null) continue;
            for (State e: this.automata[j.nameState][0]) {
                set.add(e);
                q.add(e);
            }
        }
        return set;
    }

    public Set<State> union(Set<State> s1, Set<State> s2){
        Set<State> res = new HashSet<>();
        for (State e: s1) res.add(e);
        for (State e: s2) res.add(e);
        return res;
    }

    /**
     * Renvoi l'ensemble d'états atteignable avec le caractère caract. depuis l'état state.
     * @param state
     * @param caract
     * @return
     */
    public Set<State> move(State state, int caract){
        Set<State> set;
        ArrayList<State> a = this.automata[state.nameState][caract];
        if(a== null) set = new HashSet<>();
        else set = new HashSet<>(a);
        return set;
    }

    AdjacencyMatrix subsetConstruction(){
        // Comment initialiser avec le bon nombre d'états?
        AdjacencyMatrix res = new AdjacencyMatrix(1000);
        int counterNewStates =0;
        // Create the start state of the DFA by taking the epsilon-closure of the start state of the NFA
        boolean final_state = false;
        Set<State> a = epsilonClosure(new State(0, false));
        for (State e : a ) if(e.isFinalState) final_state = true;
        SetOfStates rs1 = new SetOfStates(counterNewStates,a, final_state);
        counterNewStates++;
        Queue<SetOfStates> q = new LinkedList<>();
        q.add(rs1);

        //For each possible input symbol:
        while(!q.isEmpty()) {
            SetOfStates rs = q.poll();
            boolean final_state2 = false;
            //TO DO : test for EACH char
            for (int i = 97; i <= 99; i++) {
                //Apply move to the newly-created state and the input symbol; this will return a set of states.
                Set<State> set1 = new HashSet<>();
                for (State e: rs.setOfStates) set1 = union(move(e,i),set1);
                for(State e : set1) set1 = union(set1, epsilonClosure(e));
                if (set1.equals(rs.setOfStates)){
                    boolean finalState= false;
                    for (State s : rs.setOfStates) if(s.isFinalState ==true) finalState=true;
                    res.addTransition(rs.nameSetOfStates,i,rs.nameSetOfStates, finalState);

                    continue;
                }
                if(!set1.isEmpty()) {
                    for (State s : set1) if(s.isFinalState) final_state2=true;
                    SetOfStates rs2 = new SetOfStates(counterNewStates, set1,final_state2);
                    counterNewStates++;
                    q.add(rs2);
                    res.addTransition(rs.nameSetOfStates, i, rs2.nameSetOfStates, final_state2);
                }
            }
        }
        return res;
    }

    public Set<SetOfStates> getFinalsNonFinals(){
        Set<SetOfStates> srs = new HashSet<>();
        SetOfStates finaux = new SetOfStates(0, true);
        SetOfStates nonFinaux = new SetOfStates(1, false);
        nonFinaux.addState(new State(0, false));
        // Où la boucle doit elle s'arreter?
        for(int i =0; i <10; i++){
            // Il faudra faire pour chaque char
            for (int j =97; j <= 99; j++){
                // cad si avec ce char on peut aller quelque part
                if (this.automata[i][j]!=null) {
                    if (!this.automata[i][j].isEmpty()) {
                        for (State s : this.automata[i][j]) {
                            if (s.isFinalState) finaux.addState(s);
                            else nonFinaux.addState(s);
                        }
                    }
                }
            }

        }
        srs.add(finaux);
        srs.add(nonFinaux);
        return srs;
    }

    protected Set<SetOfStates> minimisation() {
        Set<SetOfStates> fnf = getFinalsNonFinals();

        int newCounter=0;
        boolean changes=false;
        do{
            Set<SetOfStates> newStates = new HashSet<>();
            for (SetOfStates rs : fnf) {
                Set<Couple> couples = new HashSet<>();
                if(rs.setOfStates.size()==1){
                    newStates.add(new SetOfStates(newCounter, rs.setOfStates, rs.isFinalState));
                    newCounter++;
                    continue;
                }
                Iterator<State> i = rs.setOfStates.iterator();
                while(i.hasNext()){
                    State current = i.next();
                    for (State s: rs.setOfStates) {
                        if((current.nameState ==s.nameState)&&(current.isFinalState ==s.isFinalState)) continue;
                        boolean into = false;
                        for (Couple c: couples) if((current==c.a && s ==c.b)||(s==c.a && current==c.b)) into=true;
                        if(!into) couples.add(new Couple(current, s));
                    }
                }
                for (Couple c: couples) {
                    boolean equals= true;
                    //Faudra mettre pour tous les char
                    for(int k = 97; k <= 99; k++){
                        if(((this.automata[c.a.nameState][k]!=null)&&
                                (this.automata[c.b.nameState][k]==null))
                                ||((this.automata[c.a.nameState][k]==null)&&
                                (this.automata[c.b.nameState][k]!=null))){
                            equals = false;
                            Set<State> es1 = new HashSet<>(),eS2 = new HashSet<>();
                            es1.add(c.a); eS2.add(c.b);
                            SetOfStates a = new SetOfStates(newCounter, es1, c.a.isFinalState);
                            if(!SetOfStates.present(newStates,a)){
                                newStates.add(a);
                                newCounter++;
                            }

                            SetOfStates b = new SetOfStates(newCounter, eS2, c.b.isFinalState);
                            if(!SetOfStates.present(newStates,b)){
                                newStates.add(b);
                                newCounter++;
                            }
                            continue;
                        }
                        if((this.automata[c.a.nameState][k]==null)&&
                                (this.automata[c.b.nameState][k]==null)) continue;
                        if(!sameStateSet(c.a,c.b,fnf)) equals= false;
                    }if(equals){
                        newStates= deleteSeparatedCouple(c.a, c.b,newStates);
                        Set<State> eS = new HashSet<>();
                        eS.add(c.a); eS.add(c.b);
                        newStates.add(new SetOfStates(newCounter, eS, c.b.isFinalState));
                        newCounter++;
                    }
                }
            }
            if(equals(newStates,fnf)) changes= false;
            fnf = newStates;
        }while(changes);
        return fnf;
    }

    private boolean equals(Set<SetOfStates> newStates, Set<SetOfStates> fnf) {
        if(newStates.size()!=fnf.size()) return false;
        for (SetOfStates rs: newStates) {
            boolean contains = false;
            for (SetOfStates rs2: fnf) {
                if(SetOfStates.equals(rs,rs2)) contains = true;
            }
            if(!contains) return false;
        }
        return true;
    }

    private State find(Set<SetOfStates> newAut,
                       SetOfStates s, int n) {
        Iterator<State> i = s.setOfStates.iterator();
        State a = null;
        if(i.hasNext()) a =  i.next();
        State res = this.automata[a.nameState][n].get(0);

        for(SetOfStates rs : newAut){
            for(State s3 : rs.setOfStates){
                if(s3.nameState ==res.nameState) {
                    return new State(rs.nameSetOfStates,rs.isFinalState);
                }
            }
        }
        return res;
    }

    private Set<SetOfStates> deleteSeparatedCouple(State a, State b, Set<SetOfStates> newStates) {
        Set<SetOfStates> res = new HashSet<>();
        if(newStates.size()==0) return res;
        for (SetOfStates rs: newStates) {
            SetOfStates x = new SetOfStates(rs.nameSetOfStates, rs.isFinalState);
            Set<State> y = new HashSet<>();
            for (State u:rs.setOfStates) {
                if(u.equals(a)||u.equals(b)) continue;
                y.add(u);
            }
            x.setOfStates = y;
            if(y.size()>0) res.add(x);
        }
        return res;
    }

    protected AdjacencyMatrix creationMinAutomata(Set<SetOfStates> minAutomata) {
        AdjacencyMatrix res = new AdjacencyMatrix(minAutomata.size());
        for (SetOfStates rs: minAutomata) {
            ArrayList<Integer> tab = new ArrayList<>();
            for(State s2: rs.setOfStates){
                for(int i=97; i <=99; i++){
                    if(this.automata[s2.nameState][i]!=null) tab.add(i);
                }

            }
            for(int n: tab){
                if(res.automata[rs.nameSetOfStates][n]==null){
                    ArrayList<State> al = new ArrayList<>();
                    al.add(find(minAutomata,rs,n));
                    res.automata[rs.nameSetOfStates][n] = al;
                }
            }

        }
        return res;
    }

}
