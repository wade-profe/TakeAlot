import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Scratchpad {
    public static void main(String[] args) {

        Random rand = new Random();

        System.out.println(Calendar.getInstance().getTime().toString());

        String[] luke = {"lu", "ke"};
        String[] hilary = {"hil", "ary"};
        String[] tasneem = {"tas", "neem"};
        String[] wade = {"wa", "de"};

        for (int i = 0; i < 100; i++) {
            List<String> selected = new ArrayList<>();
            selected.add(luke[rand.nextInt(2)]);
            selected.add(hilary[rand.nextInt(2)]);
            selected.add(tasneem[rand.nextInt(2)]);
            selected.add(wade[rand.nextInt(2)]);
            List<String> name = new ArrayList<>();

            for (int j = 0; j < 4; j++) {
                int chosen = rand.nextInt(selected.size());
                name.add(selected.get(chosen));
                selected.remove(chosen);
            }

            System.out.println(name);

        }


//        List<String> strings = new ArrayList<>();
//        strings.add("5");
//        strings.add("4.9");
//        strings.add("3.5");
//        strings.add("5");
//        strings.add("1.5");
//
//        List<Double> doubles = new ArrayList<>();
//        strings.forEach((s) -> {
//            doubles.add(Double.parseDouble(s));
//        });
//
//        System.out.println("Bla");
    }
}
