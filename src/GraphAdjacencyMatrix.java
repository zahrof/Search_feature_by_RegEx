import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GraphAdjacencyMatrix {
    int nbreStates;
    ArrayList<Integer>[][] automata;
    int counterStates=0;

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
    public Set<Integer> EpsilonClosure(int state){
        Set<Integer> set;
        ArrayList<Integer> a = this.automata[state][0];
        if(a== null) set = new HashSet<>();
        else set = new HashSet<>(a);
        return set;
    }


    public static void main(String arg[]){
        RegExTree tree = RegEx.parser(arg);
        GraphAdjacencyMatrix automata = new GraphAdjacencyMatrix(1000);
        System.out.print(automata.fillMatrix(tree, 0));
        automata.EpsilonClosure(0);
        automata.EpsilonClosure(1);
        automata.EpsilonClosure(2);
        automata.EpsilonClosure(3);
        automata.EpsilonClosure(4);
        automata.EpsilonClosure(5);
        automata.EpsilonClosure(6);
        automata.EpsilonClosure(7);
        automata.EpsilonClosure(8);
        automata.EpsilonClosure(9);
    }
}
