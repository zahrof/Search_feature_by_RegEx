import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class GraphAdjacencyMatrix {

    class RennomageSommets {
        int nouveauNom;
        Set<Integer> ensembleSommets;

        public RennomageSommets(int nouveauNom) {
            this.nouveauNom = nouveauNom;
            this.ensembleSommets = new HashSet<>();
        }

        public RennomageSommets(int nouveauNom, Set<Integer> ensembleSommets) {
            this.nouveauNom = nouveauNom;
            this.ensembleSommets = ensembleSommets;
        }

        public void addState(int sommet){
            this.ensembleSommets.add(sommet);
        }

        public int getNouveauNom() {
            return nouveauNom;
        }

        public void setNouveauNom(int nouveauNom) {
            this.nouveauNom = nouveauNom;
        }

        public Set<Integer> getEnsembleSommets() {
            return ensembleSommets;
        }

    }

    int nbreStates;
    ArrayList<Integer>[][] automata;
    int counterStates=0;
    int counterNewStates=0;

    public GraphAdjacencyMatrix(int nbreStates) {
        this.nbreStates= nbreStates;
        // automata [0][0] -> au state 0  si je lis 'epsilon'
        // automata [0][97] -> au state 0  si je lis 'a'
        //automata [i][128] -> si 0 pas etat initial, si 1 etat initial
        //automata [i][128] -> si 0 pas etat final, si 1 etat final
        this.automata =new ArrayList[nbreStates][130];
    }

    public void addState(int idState, int nbreASCII, int stateToAdd){
        if(this.automata[idState][nbreASCII]==null) this.automata[idState][nbreASCII] = new ArrayList<>();
        this.automata[idState][nbreASCII].add(stateToAdd);
    }

    // Renvoi l'indice de l'etat finale. L'etat initial sera tjrs 0.
    public int fillMatrix(RegExTree tree, int counter){
                switch (tree.root){
                    case RegEx.ALTERN:
                        addState(counter,0 ,counter+1);
                        this.counterStates = fillMatrix(tree.subTrees.get(0),counter+1);
                        int finR1 = counterStates;
                        addState(counter,0,this.counterStates+1);
                        this.counterStates= fillMatrix(tree.subTrees.get(1),this.counterStates+1);
                        int finR2 = counterStates;
                        int fin = counterStates+1;
                        addState(finR1, 0, fin);
                        addState(finR2, 0, fin);
                        this.counterStates++;
                        return fin;
                    case RegEx.CONCAT:
                        this.counterStates = fillMatrix(tree.subTrees.get(0),counter);
                        addState(this.counterStates,0,this.counterStates+1);
                        this.counterStates++;
                        return fillMatrix(tree.subTrees.get(1), this.counterStates);
                    case RegEx.ETOILE:
                        addState(counter,0,counter+1);
                        this.counterStates = fillMatrix(tree.subTrees.get(0),counter+1);
                        addState(this.counterStates, 0,counter+1);
                        addState(counter, 0, this.counterStates+1);
                        addState(this.counterStates,0,this.counterStates+1);
                        this.counterStates++;
                        return this.counterStates;
                    default: // it is a leaf
                        addState(counter, tree.root, counter+1);
                        return counter+1;
                }
    }

    /**
     * Renvoi l'ensemble d'etats atteignable depuis state avec les epsilon transitions
     * @param state
     * @return
     */
    public Set<Integer> epsilonClosure(int state){
        Set<Integer> set;
        ArrayList<Integer> a = this.automata[state][0];
        if(a== null) set = new HashSet<>();
        else set = new HashSet<>(a);
        return set;
    }

    /**
     * Renvoi l'ensemble d'états atteignable avec le caractère caract. depuis l'état state.
     * @param state
     * @param caract
     * @return
     */
    public Set<Integer> move(int state, int caract){
        Set<Integer> set;
        ArrayList<Integer> a = this.automata[state][caract];
        if(a== null) set = new HashSet<>();
        else set = new HashSet<>(a);
        return set;
    }

    GraphAdjacencyMatrix subsetConstruction(GraphAdjacencyMatrix nfa){
        GraphAdjacencyMatrix res = new GraphAdjacencyMatrix(1000);
        LinkedList<RennomageSommets> fifo = new LinkedList<>();
        Set<Integer> set = epsilonClosure(0);
        set.add(0);

        RennomageSommets rs1 = new RennomageSommets(this.counterNewStates, set);
        fifo.add(rs1);
        while(!fifo.isEmpty()){
            RennomageSommets rs = fifo.pop();
            for(int i=64; i <=122 ; i++){
                for (Integer j: rs.ensembleSommets) {
                    Set<Integer> set2 = move(j,i);

                }

                
            }
        }

        return res;
    }


    public static void main(String arg[]){
        RegExTree tree = RegEx.parser(arg);
        GraphAdjacencyMatrix automata = new GraphAdjacencyMatrix(1000);
        System.out.print(automata.fillMatrix(tree, 0));
        automata.epsilonClosure(0);
    }
}
