import java.util.ArrayList;


public class KMP {


    public KMP(){

    }

    public ArrayList<Pose> KMP(String motif, Book book){
        ArrayList<Pose> result = new ArrayList<>();
        Pose current = new Pose(0,0,0);
        int[] carryover = carryover(motif);
        
        Pose start = current.copy();
        int index = 0;
        while (current.page < book.size()){
            if(motif.charAt(index) == book.get(current.page, current.line, current.col)) {
                index++;
                if (index == motif.length()) {
                    result.add(start.copy());

                    current = move(book, start, carryover[index] + 1);
                    index = 0;
                }
                else{
                    current = move(book, current, 1);
                }
            } else {
                current = move(book, start, carryover[index] + 1);
                index = 0;
            }
            if (index == 0) start = current.copy();

        }

        return result;
    }



    private Pose move(Book book, Pose start, int gap){
        if (start.col < book.get(start.page, start.line).length() - gap)
            return new Pose(start.page, start.line, start.col + gap);
        else if (start.line < book.size(start.page) - gap)
            return new Pose(start.page, start.line + gap, 0);
        else // if (start.page < book.size() - gap)
            return new Pose(start.page + gap, 0, 0);
    }

    public int[] carryover(String motif){
        int[] carryover = new int[motif.length() + 1];

        return carryover;
    }
    public class Pose{
        public int page, line, col;

        public Pose(int page, int line, int col){
            this.page = page;
            this.line = line;
            this.col = col;
        }

        public Pose copy(){
            return new Pose(page, line, col);
        }

        public String toString(){
            return "(" + Integer.toString(page) + ", "
                    + Integer.toString(line) + ", "
                    + Integer.toString(col) + ")";
        }
    }


}

