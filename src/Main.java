import java.util.ArrayList;

    public class Main {

        public static void main(String arg[]){
            Book book = new Book("/home/sslye/Workspace/DAAR/Search_feature_by_RegEx/src/Babylone.txt");
            String motif = "Sargon";

            ArrayList<KMP.Pose> kmp = (new KMP()).KMP(motif, book);
            System.out.print(kmp );
        }
}
