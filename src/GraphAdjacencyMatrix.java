    import java.util.*;

    public class GraphAdjacencyMatrix {

        static class RennomageSommets {
            int nouveauNom;
            Set<State> ensembleSommets;
            boolean final_state = false;

            public RennomageSommets(int nouveauNom, Set<State> ensembleSommets, boolean final_state) {
                this.nouveauNom = nouveauNom;
                this.ensembleSommets = ensembleSommets;
                this.final_state=final_state;
            }

            public RennomageSommets(int nouveauNom, boolean final_state) {
                this.nouveauNom = nouveauNom;
                this.final_state = final_state;
                this.ensembleSommets = new HashSet<>();
            }

            public void addState(State state) {
                boolean into=false;
                for (State s: this.ensembleSommets){
                    if(state.name_state==s.name_state && state.final_state==s.final_state) {
                        into = true;
                        break;
                    }
                }

                if(!into) this.ensembleSommets.add(state);
            }
        }

        static class State {
            int name_state;
            boolean final_state = false;

            public State(int name_state, boolean final_state) {
                this.name_state = name_state;
                this.final_state = final_state;
            }

            public int getName_state() {
                return name_state;
            }

            @Override
            public boolean equals(Object o) {

                // If the object is compared with itself then return true
                if (o == this) {
                    return true;
                }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
                if (!(o instanceof State)) {
                    return false;
                }

                // typecast o to Complex so that we can compare data members
                State c = (State) o;

                // Compare the data members and return accordingly
                return (this.name_state==c.name_state && this.final_state==c.final_state);
            }
        }

        int nbreStates;
        ArrayList<State>[][] automata; // Array List?

        public GraphAdjacencyMatrix(int nbreStates) {
            this.nbreStates= nbreStates;
            // automata [0][0] -> au state 0  si je lis 'epsilon'
            // automata [0][97] -> au state 0  si je lis 'a'
            //automata [i][128] -> si 0 pas etat initial, si 1 etat initial
            //automata [i][128] -> si 0 pas etat final, si 1 etat final
            this.automata =new ArrayList[nbreStates][130];
        }


        public void addState(int idState, int nbreASCII, int stateToAdd, boolean final_state){
            if(this.automata[idState][nbreASCII]==null) this.automata[idState][nbreASCII] = new ArrayList<>();
            this.automata[idState][nbreASCII].add(new State(stateToAdd, final_state));
        }

        /**
         * This method will count the number of states that will be in the automaton
         * @param tree
         * @return
         */
        public static int countSize(RegExTree tree, int counter ) {
           if (tree.subTrees.size()==1)counter  = countSize(tree.subTrees.get(0), counter);
           else {
                if (tree.subTrees.size() == 2) {
                    counter = countSize(tree.subTrees.get(0), counter);
                    counter = countSize(tree.subTrees.get(1), counter);
                }
            }
                switch (tree.root) {
                    case RegEx.CONCAT:
                        break;
                    default: // it is a leaf
                        counter = counter + 2;
                        break;
                }
            return counter;

        }

        // Renvoi l'indice de l'etat finale. L'etat initial sera tjrs 0.
        // Question 2.2
        public int fillMatrix(RegExTree tree, int counter, int sizeAutomaton){
            int counterStates=0;
                    switch (tree.root){
                        case RegEx.ALTERN:
                            addState(counter,0 ,counter+1, false);
                            counterStates = fillMatrix(tree.subTrees.get(0),counter+1,sizeAutomaton);
                            int finR1 = counterStates;
                            addState(counter,0,counterStates+1, false);
                            counterStates= fillMatrix(tree.subTrees.get(1),counterStates+1,sizeAutomaton);
                            int finR2 = counterStates;
                            int fin = counterStates+1;
                            counterStates++;
                            if(counterStates==(sizeAutomaton-1)) {
                                addState(finR1, 0, fin, true);
                                addState(finR2, 0, fin, true);
                            }else{
                                addState(finR1, 0, fin, false);
                                addState(finR2, 0, fin, false);
                            }

                            return fin;
                        case RegEx.CONCAT:
                            counterStates = fillMatrix(tree.subTrees.get(0),counter,sizeAutomaton);
                            addState(counterStates,0,counterStates+1, false);
                            counterStates++;
                            return fillMatrix(tree.subTrees.get(1), counterStates,sizeAutomaton);
                        case RegEx.ETOILE:
                            addState(counter,0,counter+1, false);
                            counterStates = fillMatrix(tree.subTrees.get(0),counter+1,sizeAutomaton);
                            addState(counterStates, 0,counter+1, false);

                            if(counterStates==(sizeAutomaton-1)) {
                                addState(counter, 0, counterStates + 1, true);
                                addState(counterStates, 0, counterStates + 1, true);
                            }
                            else{
                                addState(counter, 0, counterStates + 1, false);
                                addState(counterStates, 0, counterStates + 1, false);
                            }
                            counterStates++;
                            return counterStates;
                        default: // it is a leaf
                            if(counter==0) addState(counter, tree.root, counter+1, true);
                            else addState(counter, tree.root, counter+1, false);
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
                if(this.automata[j.getName_state()][0]==null) continue;
                for (State e: this.automata[j.name_state][0]) {
                    set.add(e);
                    q.add(e);
                }
            }
            return set;
        }

        /**
         * Renvoi l'ensemble d'états atteignable avec le caractère caract. depuis l'état state.
         * @param state
         * @param caract
         * @return
         */
        public Set<State> move(State state, int caract){
            Set<State> set;
            ArrayList<State> a = this.automata[state.name_state][caract];
            if(a== null) set = new HashSet<>();
            else set = new HashSet<>(a);
            return set;
        }

        public static Set<State> union(Set<State> s1, Set<State> s2){
            Set<State> res = new HashSet<>();
            for (State e: s1) res.add(e);
            for (State e: s2) res.add(e);
            return res;
        }

        // Question 2.3
        GraphAdjacencyMatrix subsetConstruction(GraphAdjacencyMatrix nfa){
            // Comment initialiser avec le bon nombre d'états?
            GraphAdjacencyMatrix res = new GraphAdjacencyMatrix(1000);
            int counterNewStates =0;
            // Create the start state of the DFA by taking the epsilon-closure of the start state of the NFA
            boolean final_state = false;
            Set<State> a = nfa.epsilonClosure(new State(0, false));
            for (State e : a ) if(e.final_state) final_state = true;
            RennomageSommets rs1 = new RennomageSommets(counterNewStates,a, final_state);
            counterNewStates++;
            Queue<RennomageSommets> q = new LinkedList<>();
            q.add(rs1);

            //For each possible input symbol:
            while(!q.isEmpty()) {
                RennomageSommets rs = q.poll();
                boolean final_state2 = false;
                //TO DO : test for EACH char
                for (int i = 97; i <= 99; i++) {
                    //Apply move to the newly-created state and the input symbol; this will return a set of states.
                    Set<State> set1 = new HashSet<>();
                    for (State e: rs.ensembleSommets) set1 = union(move(e,i),set1);
                    for(State e : set1) set1 = union(set1, epsilonClosure(e));
                    if (set1.equals(rs.ensembleSommets)){
                        boolean finalState= false;
                        for (State s : rs.ensembleSommets) if(s.final_state==true) finalState=true;
                        res.addState(rs.nouveauNom,i,rs.nouveauNom, finalState);

                        continue;
                    }
                    if(!set1.isEmpty()) {
                        for (State s : set1) if(s.final_state) final_state2=true;
                        RennomageSommets rs2 = new RennomageSommets(counterNewStates, set1,final_state2);
                        counterNewStates++;
                        q.add(rs2);
                        res.addState(rs.nouveauNom, i, rs2.nouveauNom, final_state2);
                    }
                }
            }
            return res;
        }

        public static Set<RennomageSommets> getFinalsNonFinals(GraphAdjacencyMatrix dfa){
            Set<RennomageSommets> srs = new HashSet<>();
            RennomageSommets finaux = new RennomageSommets(0, true);
            RennomageSommets nonFinaux = new RennomageSommets(1, false);
            nonFinaux.addState(new State(0, false));
            // Où la boucle doit elle s'arreter?
            for(int i =0; i <10; i++){
                // Il faudra faire pour chaque char
                for (int j =97; j <= 99; j++){
                    // cad si avec ce char on peut aller quelque part
                    if (dfa.automata[i][j]!=null) {
                        if (!dfa.automata[i][j].isEmpty()) {
                            for (State s : dfa.automata[i][j]) {
                                if (s.final_state) finaux.addState(s);
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


        public static void main(String arg[]){
            RegExTree tree = RegEx.parser(arg);
            int automatonSize= countSize(tree,0);
            GraphAdjacencyMatrix automata = new GraphAdjacencyMatrix(automatonSize);
            System.out.println("Size automaton "+ automatonSize);
            System.out.print(automata.fillMatrix(tree, 0,automatonSize));
            automata = automata.subsetConstruction(automata);
            getFinalsNonFinals(automata);
        }
    }
