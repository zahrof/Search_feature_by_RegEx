    import java.util.*;

    public class BookStrategy {



        public static void main(String arg[]){
            RegExTree tree = RegEx.parser(arg);
            int automatonSize= RegEx.countSize(tree,0);
            AdjacencyMatrix automata = new AdjacencyMatrix(automatonSize);
            automata.fillMatrix(tree, 0,automatonSize);
            automata = automata.subsetConstruction();
            Set<SetOfStates> minAutomata = automata.minimisation();
             minAutomata = SetOfStates.rennomage(minAutomata);
            automata = automata.creationMinAutomata(minAutomata);
            System.out.println(" RES "+automata.toString());
        }








    }
