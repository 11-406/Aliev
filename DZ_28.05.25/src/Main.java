import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String word = "aapple ,";
        value(word,getWordChars(word));
    }

    public static void value(String word, char[] mas) {
        int[] res = new int[mas.length];
        for(int i = 0; i < mas.length; i++) {
            for(int j = 0; j < word.length(); j++) {
                if(mas[i] == word.charAt(j))  {
                    res[i]++;
                }
            }
            System.out.println(mas[i] + ": " + res[i]);
        }
    }

    public static char[] getWordChars(String word) {
        List<Character> temp = new ArrayList<>();
        for(int i = 0; i < word.length(); i++) {
            if(!temp.contains(word.charAt(i))) {
                temp.add(word.charAt(i));
            }
        }
        char[] res = new char[temp.size()];
        for(int i = 0; i < temp.size(); i++) {
            res[i] = temp.get(i);
        }
        return res;
    }
}