import java.util.Set;

public class State {
    int nameState;
    boolean isFinalState;

    public State(int nameState, boolean isFinalState) {
        this.nameState = nameState;
        this.isFinalState = isFinalState;
    }

    public int getNameState() {
        return nameState;
    }
    @Override
    public String toString() {
        return String.format("Name of the state: "+ this.nameState + "| Is final: "+ this.isFinalState + "  ");
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof State)) return false;
        State c = (State) o;
        return ((this.nameState ==c.nameState && this.isFinalState ==c.isFinalState));
    }


}
