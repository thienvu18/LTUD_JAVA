import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static TwoWayDictionary _dictionary = new TwoWayDictionary();
    private static Language _language = Language.ENGLISH;
    private static TreeSet<String> _favorites = new TreeSet<>();
    private static RunningState _runningState = RunningState.INIT;
    private static History _history = new History();

    private static void createFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    private static void loadData() {
        try {
            _dictionary.loadData("dictionary.xml");
            _history.loadFromFile("history.dat");

            createFile("favorites.dat");
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("favorites.dat"));
            _favorites.addAll((TreeSet<String>) in.readObject());

            _runningState = RunningState.RUNNING;
        } catch (Exception e) {
            System.out.println("Không nạp được dữ liệu. Chương trình kết thúc!");
            _runningState = RunningState.EXIT;
        }
    }

    private static void saveData() {
        _history.saveToFile("history.dat");
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("favorites.dat"));
            out.writeObject(_favorites);
        } catch (IOException ignored) {
        }
    }

    private static void printMeaning(String word, TreeSet<String> meanings) {
        System.out.println(word + " có các nghĩa như sau: ");
        for (String meaning : meanings) {
            System.out.println("\t" + meaning);
        }
        System.out.println();
    }

    private static void switchLanguage() {
        System.out.println("Vui lòng chọn ngôn ngữ muốn tra cứu");
        System.out.println("\t1. Anh - Việt");
        System.out.println("\t2. Việt - Anh");
        System.out.println("\t0. Quay về");
        System.out.print("Lựa chọn của bạn: ");

        int key = getInputWithValidated(2);

        switch (key) {
            case 0:
                break;
            case 1:
                _language = Language.ENGLISH;
                System.out.println("Bạn đã chọn Anh - Việt");
                break;
            case 2:
                _language = Language.VIETNAMESE;
                System.out.println("Bạn đã chọn Việt - Anh");
                break;
        }
    }

    private static void translateToVietnamese(String english) {
        TreeSet<String> meanings = _dictionary.getVietnameseMeanings(english);

        if (meanings == null) {
            System.out.println("Không tìm thấy nghĩa của từ cần tra");
        } else {
            printMeaning(english, meanings);
        }
    }

    private static void translateToEnglish(String vietnamese) {
        TreeSet<String> meanings = _dictionary.getEnglishMeanings(vietnamese);

        if (meanings == null) {
            System.out.println("Không tìm thấy nghĩa của từ cần tra");
        } else {
            printMeaning(vietnamese, meanings);
        }
    }

    private static int getInputWithValidated(int maxMenuIndex) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        int key = -1;
        try {
            key = Integer.parseInt(input);
        } catch (Exception ignored) {
        }

        while (key < 0 || key > maxMenuIndex) { //'0' equal to 48
            System.out.print("Lựa chọn không hợp lệ, vui lòng chọn lại: ");
            input = sc.nextLine();
            try {
                key = Integer.parseInt(input);
            } catch (Exception ignored) {
            }
        }

        return key;
    }

    private static void markFavorite(String word) {
        _favorites.add(word);
    }

    private static void unmarkFavorite(String word) {
        _favorites.remove(word);
    }

    private static boolean isFavorite(String word) {
        return _favorites.contains(word);
    }

    private static void printFavorite() {
        System.out.println("Bạn muốn sắp xếp danh sách theo thứ tự nào?");
        System.out.println("\t1. A-Z");
        System.out.println("\t2. Z-A");
        System.out.println("\t0. Quay lại");

        int key = getInputWithValidated(2);

        switch (key) {
            case 0:
                break;
            case 1:
                _favorites.iterator().forEachRemaining(word -> System.out.println("\t" + word));
                break;
            case 2:
                _favorites.descendingIterator().forEachRemaining(word -> System.out.println("\t" + word));
                break;
        }
    }

    private static String getInput(String prompt) {
        Scanner sc = new Scanner(System.in);
        System.out.print(prompt);
        return sc.nextLine();
    }

    private static void translate() {
        String input = getInput("Nhập từ cần tra: ");
        switch (_language) {
            case ENGLISH:
                translateToVietnamese(input);
                break;
            case VIETNAMESE:
                translateToEnglish(input);
                break;
        }

        if (!isFavorite(input)) {
            System.out.print("Từ \"" + input + "\" chưa có trong danh sách yêu thích, bạn có muốn thêm vào? (Y/n): ");
            Scanner sc = new Scanner(System.in);
            String s = sc.nextLine();
            if (!s.equals("N") && !s.equals("n")) {
                markFavorite(input);
                System.out.println("Thêm vào yêu thích thành công");
            }
        }

        _history.addRecord(input);
    }

    private static void Statistics() {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date from = null, to = null;
        boolean valid = false;
        HashMap<String, Integer> req = null;
        while (!valid) {
            try {

                from = formatter.parse(getInput("Vui lòng nhập ngày bắt đầu (dd/MM/yyyy): "));
                valid = true;

            } catch (ParseException ignored) {
                System.out.println("Ngày bắt đầu không hợp lệ");
            }
        }

        valid = false;
        while (!valid) {
            try {

                to = formatter.parse(getInput("Vui lòng nhập ngày kết thúc (dd/MM/yyyy): "));
                if (from.before(to)) {
                    valid = true;
                } else {
                    System.out.println("Vui lòng nhập ngày kết thúc lớn hơn ngày bắt đầu");
                }

            } catch (ParseException ignored) {
                System.out.println("Ngày kết thúc không hợp lệ");
            }
        }

        req = _history.getFrequencyInPeriod(from, to);
        
        if (req.size() == 0) {
            System.out.println("Bạn chưa tra cứu từ nào trong khoảng thời gian đã nhập");
        } else {
            String leftAlignFormat = "| %-15s | %-6d |%n";

            System.out.format("+-----------------+--------+%n");
            System.out.format("|        Từ       | Tần số |%n");
            System.out.format("+-----------------+--------+%n");

            for (Map.Entry<String, Integer> lookupRecord : req.entrySet()) {
                System.out.format(leftAlignFormat, lookupRecord.getKey(), lookupRecord.getValue());
            }

            System.out.format("+-----------------+--------+%n");
        }
    }

    private static void menu() {
        System.out.println("---------Menu----------");
        System.out.println("1. Cài đặt ngôn ngữ tra cứu.");
        System.out.println("2. Tra cứu nghĩa của từ.");
        System.out.println("3. Hiển thị danh sách yêu thích.");
        System.out.println("4. Thống kê từ đã tra.");
        System.out.println("0. Thoát");
        System.out.print("Lựa chọn của bạn: ");

        int key = getInputWithValidated(4);

        switch (key) {
            case 1:
                switchLanguage();
                break;
            case 2:
                translate();
                break;
            case 3:
                printFavorite();
                break;
            case 4:
                Statistics();
                break;
            case 0:
                _runningState = RunningState.EXIT;
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        while (true) {
            switch (_runningState) {
                case INIT:
                    System.out.println("Đang tải dữ liệu");
                    loadData();
                    break;
                case RUNNING:
                    menu();
                    break;
                case EXIT:
                    System.out.println("Đang lưu lại dữ liệu");
                    saveData();
                    System.out.println("Chương trình kết thúc!");
                    System.exit(0);
                    break;
            }
        }
    }
}
