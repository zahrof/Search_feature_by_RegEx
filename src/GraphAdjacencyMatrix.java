    import java.util.*;

    public class GraphAdjacencyMatrix {


        static class Couple{
            State a; State b;
            public Couple(State a, State b) {
                this.a = a; this.b = b;
            }
        }

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

            @Override
            public String toString() {
                String eS="";
                for (State s: this.ensembleSommets) {
                    eS+= "Nom sous-sommet "+ s.name_state + "  état final: "+ s.final_state+ "\n";
                }
                return String.format("Nom sommet: "+ this.nouveauNom+ "\n   Etat final "+ this.final_state + "\n  "+ eS);
            }
        }

        static class State {
            int name_state;
            boolean final_state;

            public State(int name_state, boolean final_state) {
                this.name_state = name_state;
                this.final_state = final_state;
            }

            public int getName_state() {
                return name_state;
            }
            @Override
            public String toString() {
                return String.format("Nom sommet: "+ this.name_state+ "\n   Etat final "+ this.final_state + "\n  ");
            }

            @Override
            public boolean equals(Object o) {
                if (o == this) return true;
                if (!(o instanceof State)) return false;
                State c = (State) o;
                return ((this.name_state==c.name_state && this.final_state ==c.final_state));
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

        private Set<RennomageSommets> minimisation() {
            Set<RennomageSommets> fnf = getFinalsNonFinals(this);

            int newCounter=0;
            boolean changes=false;
            do{
                Set<RennomageSommets> newStates = new HashSet<>();
                for (RennomageSommets rs : fnf) {
                    Set<Couple> couples = new HashSet<>();
                    if(rs.ensembleSommets.size()==1){
                        newStates.add(new RennomageSommets(newCounter, rs.ensembleSommets, rs.final_state));
                        newCounter++;
                        continue;
                    }
                    Iterator<State> i = rs.ensembleSommets.iterator();
                    while(i.hasNext()){
                        State current = i.next();
                        for (State s: rs.ensembleSommets) {
                            if((current.name_state==s.name_state)&&(current.final_state==s.final_state)) continue;
                            boolean into = false;
                            for (Couple c: couples) if((current==c.a && s ==c.b)||(s==c.a && current==c.b)) into=true;
                            if(!into) couples.add(new Couple(current, s));
                        }
                    }
                    for (Couple c: couples) {
                        boolean equals= true;
                        System.out.println("Element ("+ c.a +" , "+c.b+")" );
                        //Faudra mettre pour tous les char
                        for(int k = 97; k <= 99; k++){
                            if(((this.automata[c.a.name_state][k]!=null)&&
                                    (this.automata[c.b.name_state][k]==null))
                                    ||((this.automata[c.a.name_state][k]==null)&&
                                    (this.automata[c.b.name_state][k]!=null))){
                                equals = false;
                                Set<State> es1 = new HashSet<>(),eS2 = new HashSet<>();
                                es1.add(c.a); eS2.add(c.b);
                                RennomageSommets a = new RennomageSommets(newCounter, es1, c.a.final_state);
                                if(!present(newStates,a)){
                                    newStates.add(a);
                                    newCounter++;
                                }

                                RennomageSommets b = new RennomageSommets(newCounter, eS2, c.b.final_state);
                                if(!present(newStates,b)){
                                    newStates.add(b);
                                    newCounter++;
                                }
                                continue;
                            }
                            if((this.automata[c.a.name_state][k]==null)&&
                                    (this.automata[c.b.name_state][k]==null)) continue;
                            if(!sameStateSet(c.a,c.b,fnf)) equals= false;
                        }if(equals){
                            newStates= deleteSeparatedCouple(c.a, c.b,newStates);
                            Set<State> eS = new HashSet<>();
                            eS.add(c.a); eS.add(c.b);
                            newStates.add(new RennomageSommets(newCounter, eS, c.b.final_state));
                            newCounter++;
                        }

                    }

                }
                if(equals(newStates,fnf)) changes= false;
                fnf = newStates;
            }while(changes);
            return fnf;
        }

        private boolean equals(Set<RennomageSommets> newStates, Set<RennomageSommets> fnf) {
            if(newStates.size()!=fnf.size()) return false;
            for (RennomageSommets rs: newStates) {
                boolean contains = false;
                for (RennomageSommets rs2: fnf) {
                    if(equals(rs,rs2)) contains = true;
                }
                if(!contains) return false;
            }
            return true;
        }

        private Set<RennomageSommets> deleteSeparatedCouple(State a, State b, Set<RennomageSommets> newStates) {
            Set<RennomageSommets> res = new HashSet<>();
            if(newStates.size()==0) return res;
            for (RennomageSommets rs: newStates) {
                RennomageSommets x = new RennomageSommets(rs.nouveauNom, rs.final_state);
                Set<State> y = new HashSet<>();
                for (State u:rs.ensembleSommets) {
                    if(u.equals(a)||u.equals(b)) continue;
                    y.add(u);
                }
                x.ensembleSommets = y;
                if(y.size()>0) res.add(x);
            }
            return res;
        }

        private boolean present(Set<RennomageSommets> newStates, RennomageSommets a) {
            for (RennomageSommets s: newStates) if(isSubSet(a,s)) return true;
            return false;
        }

        //a est un sous ensemble de s
        private boolean isSubSet(RennomageSommets a, RennomageSommets s) {
            for(State a1 : a.ensembleSommets){
                boolean isHere=false;
                for (State a2: s.ensembleSommets ) {
                    if(a1.equals(a2)) isHere=true;
                    break;
                }
                if (!isHere) return false;
            }

            return true;
        }

        private boolean equals(RennomageSommets a, RennomageSommets s) {
            if(a.final_state!=s.final_state) return false;
            if(a.ensembleSommets.size()!=s.ensembleSommets.size()) return false;
            for (State b: a.ensembleSommets) {
                boolean present = false;
                for (State c: s.ensembleSommets)
                    if(c.name_state==b.name_state && c.final_state==b.final_state) present=true;
                if(!present) return false;
            }
            return true;

        }

        private boolean sameStateSet(State elt1, State elt2, Set<RennomageSommets> fnf) {
            int iteration =0;
            // Pas très propre sorry ^^'
            int findElt1=999999; // Infinite
            int findElt2=9999999;
            for (RennomageSommets rs: fnf) {
                for (State s : rs.ensembleSommets) {
                    if((elt1.name_state==s.name_state)&&(elt1.final_state==s.final_state)) findElt1 = iteration;
                    if((elt2.name_state==s.name_state)&&(elt2.final_state==s.final_state)) findElt2 = iteration;
                }
                iteration++;
            }
            return findElt1==findElt2;
        }


        public static void main(String arg[]){
            RegExTree tree = RegEx.parser(arg);
            int automatonSize= countSize(tree,0);
            GraphAdjacencyMatrix automata = new GraphAdjacencyMatrix(automatonSize);
            System.out.println("Size automaton "+ automatonSize);
            System.out.print(automata.fillMatrix(tree, 0,automatonSize));
            automata = automata.subsetConstruction(automata);
            Set<RennomageSommets> minAutomata = automata.minimisation();
            System.out.println("min automata avant renommage"+minAutomata);
            System.out.println();
             minAutomata = rennomage(minAutomata);
            System.out.println("min automata apres renommage"+minAutomata);
            System.out.println();
            automata = creationMinAutomata(minAutomata, automata);
            System.out.println(" RES "+automata.toString());
        }

        private static Set<RennomageSommets> rennomage(Set<RennomageSommets> minAutomata) {
            Set<RennomageSommets> rs = new HashSet<>();
            int counter=0;
            for (RennomageSommets a: minAutomata) {
                RennomageSommets aux = new RennomageSommets(counter, a.ensembleSommets,a.final_state);
                rs.add(aux);
                counter++;
            }

            return rs;
        }

        private static GraphAdjacencyMatrix creationMinAutomata(Set<RennomageSommets> minAutomata,
                                                                GraphAdjacencyMatrix automata) {
            GraphAdjacencyMatrix res = new GraphAdjacencyMatrix(minAutomata.size());
            System.out.println("min automata "+minAutomata);
            System.out.println();
            for (RennomageSommets rs: minAutomata) {
                ArrayList<Integer> tab = new ArrayList<>();
                for(State s2: rs.ensembleSommets){
                    for(int i=97; i <=99; i++){
                        if(automata.automata[s2.name_state][i]!=null) tab.add(i);
                    }

                }
                for(int n: tab){
                   // if(rs.nouveauNom==3) System.out.println("hey s2 "+ s2);
                    System.out.println("res1["+rs.nouveauNom+"]["+n+"]");
                   if(res.automata[rs.nouveauNom][n]==null){
                       ArrayList<State> al = new ArrayList<>();
                       al.add(find(automata,minAutomata,rs,n));
                       res.automata[rs.nouveauNom][n] = al;
                   }
                }

            }
            return res;
        }

        private static State find(GraphAdjacencyMatrix autOr, Set<RennomageSommets> newAut,
                                  RennomageSommets s, int n) {
            Iterator<State> i = s.ensembleSommets.iterator();
            State a = null;
            if(i.hasNext()) a =  i.next();
            State res = autOr.automata[a.name_state][n].get(0);

            for(RennomageSommets rs : newAut){
                for(State s3 : rs.ensembleSommets){
                    if(s3.name_state==res.name_state) {
                        System.out.println(" TOTO "+ rs.nouveauNom );
                        return new State(rs.nouveauNom,rs.final_state);
                    }
                }
            }
            return res;
        }


    }
