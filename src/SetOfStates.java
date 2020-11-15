import java.util.HashSet;
import java.util.Set;

public class SetOfStates {
    int nameSetOfStates;
    Set<State> setOfStates;
    boolean isFinalState;

    public SetOfStates(int nameSetOfStates, Set<State> setOfStates, boolean isFinalState) {
        this.nameSetOfStates = nameSetOfStates;
        this.setOfStates = setOfStates;
        this.isFinalState = isFinalState;
    }

    public SetOfStates(int nameSetOfStates, boolean isFinalState) {
        this.nameSetOfStates = nameSetOfStates;
        this.isFinalState = isFinalState;
        this.setOfStates = new HashSet<>();
    }

    public void addState(State state) {
        boolean into=false;
        for (State s: this.setOfStates){
            if(state.nameState ==s.nameState && state.isFinalState ==s.isFinalState) {
                into = true;
                break;
            }
        }
        if(!into) this.setOfStates.add(state);
    }

    //a est un sous ensemble de s
    protected static boolean isSubSet(SetOfStates a, SetOfStates s) {
        for(State a1 : a.setOfStates){
            boolean isHere=false;
            for (State a2: s.setOfStates) {
                if(a1.equals(a2)) isHere=true;
                break;
            }
            if (!isHere) return false;
        }

        return true;
    }

    protected static Set<SetOfStates> rennomage(Set<SetOfStates> minAutomata) {
        Set<SetOfStates> rs = new HashSet<>();
        int counter=0;
        for (SetOfStates a: minAutomata) {
            SetOfStates aux = new SetOfStates(counter, a.setOfStates,a.isFinalState);
            rs.add(aux);
            counter++;
        }

        return rs;
    }

    protected static boolean present(Set<SetOfStates> newStates, SetOfStates a) {
        for (SetOfStates s: newStates) if(SetOfStates.isSubSet(a,s)) return true;
        return false;
    }

    protected static boolean equals(SetOfStates a, SetOfStates s) {
        if(a.isFinalState !=s.isFinalState) return false;
        if(a.setOfStates.size()!=s.setOfStates.size()) return false;
        for (State b: a.setOfStates) {
            boolean present = false;
            for (State c: s.setOfStates)
                if(c.nameState ==b.nameState && c.isFinalState ==b.isFinalState) present=true;
            if(!present) return false;
        }
        return true;

    }

    @Override
    public String toString() {
        String eS="";
        for (State s: this.setOfStates) {
            eS+= "Name of the sub-state: "+ s.nameState + "\n  Is final : "+ s.isFinalState + "\n";
        }
        return String.format("Name of the state: "+ this.nameSetOfStates + "\nIs final: "+ this.isFinalState + "\n  "+ eS);
    }
}
