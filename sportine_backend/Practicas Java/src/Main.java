//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        Map<Integer, String> usuarios = new HashMap<>();
        usuarios.put(1,"Juan");
        usuarios.put(2,"Ana");
        usuarios.put(1,"Pedro"); // Sobrescribe a  "Juan"

        for(Map.Entry<Integer,String> entry : usuarios.entrySet()){
            System.out.println(
                    "ID: " + entry.getKey() +
                     " |  Nombre: " + entry.getValue()
            );
        }
    }
}



