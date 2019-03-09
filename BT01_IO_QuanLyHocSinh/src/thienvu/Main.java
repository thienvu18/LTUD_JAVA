package thienvu;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

enum SortCondition {
    DIEM_ASC,
    DIEM_DESC,
    MAHS_ASC,
    MAHS_DESC
}

class Student implements Serializable {
    private int _maHS;
    private String _tenHS;
    private int _diem;
    private String _hinhAnh;
    private String _diaChi;
    private String _ghiChu;

    int get_maHS() {
        return _maHS;
    }

    public String get_tenHS() {
        return _tenHS;
    }

    int get_diem() {
        return _diem;
    }

    public String get_hinhAnh() {
        return _hinhAnh;
    }

    public String get_diaChi() {
        return _diaChi;
    }

    public String get_ghiChu() {
        return _ghiChu;
    }

    void set_maHS(int _maHS) {
        this._maHS = _maHS;
    }

    void set_tenHS(String _tenHS) {
        this._tenHS = _tenHS;
    }

    void set_diem(int _diem) {
        this._diem = _diem;
    }

    void set_hinhAnh(String _hinhAnh) {
        this._hinhAnh = _hinhAnh;
    }

    void set_diaChi(String _diaChi) {
        this._diaChi = _diaChi;
    }

    void set_ghiChu(String _ghiChu) {
        this._ghiChu = _ghiChu;
    }

    Student(int maHS, String tenHS, int diem, String hinhAnh, String diaChi, String ghiChu) {
        this._maHS = maHS;
        this._tenHS = tenHS;
        this._diem = diem;
        this._hinhAnh = hinhAnh;
        this._diaChi = diaChi;
        this._ghiChu = ghiChu;
    }

    Student(String dataString) {
        String[] dataArray;

        dataArray = dataString.split(",");

        this._maHS = Integer.parseInt(dataArray[0]);
        this._tenHS = dataArray[1];
        this._diem = Integer.parseInt(dataArray[2]);
        this._hinhAnh = dataArray[3];
        this._diaChi = dataArray[4];
        this._ghiChu = dataArray[5];
    }

    void show() {
        System.out.println("-----Thong tin hoc sinh-----");
        System.out.println("MHS: " + _maHS);
        System.out.println("TenHS: " + _tenHS);
        System.out.println("Diem: " + _diem);
        System.out.println("Hinh anh: " + _hinhAnh);
        System.out.println("Dia chi: " + _diaChi);
        System.out.println("GhiChu: " + _ghiChu);
    }

    String toCsvString() {
        return _maHS + "," + _tenHS + "," + _diem + "," + _hinhAnh + "," + _diaChi + "," + _ghiChu;
    }
}

class StudentManage {
    private ArrayList<Student> _students;

    StudentManage() {
        this._students = new ArrayList<>();
    }

    StudentManage(ArrayList<Student> _students) {
        this._students = new ArrayList<>(_students);
    }

    ArrayList<Student> get_students() {
        return _students;
    }

    int getstudentCount() {
        return _students.size();
    }

    void addStudent(int maHS, String tenHS, int diem, String hinhAnh, String diaChi, String ghiChu) {
        Student newStudent = new Student(maHS, tenHS, diem, hinhAnh, diaChi, ghiChu);
        _students.add(newStudent);
    }

    void updateStudent(int index, int maHS, String tenHS, int diem, String hinhAnh, String diaChi, String ghiChu) {
        _students.get(index).set_maHS(maHS);
        _students.get(index).set_tenHS(tenHS);
        _students.get(index).set_diem(diem);
        _students.get(index).set_hinhAnh(hinhAnh);
        _students.get(index).set_diaChi(diaChi);
        _students.get(index).set_ghiChu(ghiChu);
    }

    void showStudents(SortCondition sortCondition) {
        Comparator<Student> comparator = null;
        switch (sortCondition) {
            case MAHS_ASC:
                comparator = (o1, o2) -> o1.get_maHS() - o2.get_maHS();
                break;
            case MAHS_DESC:
                comparator = (o1, o2) -> o2.get_maHS() - o1.get_maHS();
                break;
            case DIEM_ASC:
                comparator = (o1, o2) -> o1.get_diem() - o2.get_diem();
                break;
            case DIEM_DESC:
                comparator = (o1, o2) -> o2.get_diem() - o1.get_diem();
                break;
        }

        ArrayList<Student> tmp = new ArrayList<>(_students);
        tmp.sort(comparator);
        tmp.forEach(Student::show);
    }

    void deleteStudent(int index) {
        _students.remove(index);
    }

    String toCsvString() {
        StringBuilder str = new StringBuilder();
        for (Student student : _students) {
            str.append(student.toCsvString()).append('\n');
        }

        return str.toString();
    }

    void fromCsvString(String dataString) {
        String[] dataArray;

        dataArray = dataString.split("\n");
        for (String line : dataArray) {
            Student student = new Student(line);
            _students.add(student);
        }
    }

    int findStudentById(int maHS) {
        for (int i = 0; i < _students.size(); i++) {
            if (_students.get(i).get_maHS() == maHS) return i;
        }
        return -1;
    }
}

public class Main {

    private static StudentManage _studentManage;

    private static boolean readData(String filePath) {
        boolean isSuccess = false;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));
            _studentManage = new StudentManage((ArrayList<Student>) in.readObject());
            isSuccess = true;
        }
        catch (EOFException e) {
            System.out.println("File dữ liệu rỗng, bỏ qua quá trình đọc dữ liệu.");
            _studentManage = new StudentManage();
            isSuccess = true;
        }
        catch (IOException | ClassNotFoundException ignored) {

        }

        return isSuccess;
    }

    private static boolean writeData(String filePath) {
        boolean isSuccess = false;
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath));
            out.writeObject(_studentManage.get_students());
            isSuccess = true;
        } catch (IOException ignored) {
        }

        return isSuccess;
    }

    private static void addStudent() {
        Scanner in = new Scanner(System.in);
        int maHS, diem;
        String tenHS, hinhAnh, diaChi, ghiChu;

        System.out.print("Nhập MHS: ");
        maHS = in.nextInt();
        in.nextLine();

        System.out.print("Nhập TenHS: ");
        tenHS = in.nextLine();

        System.out.print("Nhập Diem: ");
        diem = in.nextInt();
        in.nextLine();

        System.out.print("Nhập đường dẫn đến hình ảnh: ");
        hinhAnh = in.nextLine();

        System.out.print("Nhập dia chi: ");
        diaChi = in.nextLine();

        System.out.print("Nhập GhiChu: ");
        ghiChu = in.nextLine();

        _studentManage.addStudent(maHS, tenHS, diem, hinhAnh, diaChi, ghiChu);

        System.out.println("Thêm học sinh thành công");

        System.out.println("Nhấn Enter để tiếp tục");
        in.nextLine();
    }

    private static void showStudents() {
        Scanner in = new Scanner(System.in);
        String userChoice = "";

        System.out.println("Bạn muốn hiển thị danh sách theo thứ tự nào?");
        System.out.println("\t1. MHS tăng dần");
        System.out.println("\t2. MHS giảm dần");
        System.out.println("\t3. Diem tăng dần");
        System.out.println("\t4. Diem giảm dần");
        System.out.print("Vui lòng chọn: ");

        userChoice = in.nextLine();

        switch (userChoice) {
            case "1":
                _studentManage.showStudents(SortCondition.MAHS_ASC);
                break;
            case "2":
                _studentManage.showStudents(SortCondition.MAHS_DESC);
                break;
            case "3":
                _studentManage.showStudents(SortCondition.DIEM_ASC);
                break;
            case "4":
                _studentManage.showStudents(SortCondition.DIEM_DESC);
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ");
                break;
        }
        System.out.println("Nhấn Enter để tiếp tục");
        in.nextLine();
    }

    private static void importFromCsv() {
        Scanner in = new Scanner(System.in);

        System.out.println("Vui lòng nhập đường dẫn file: ");
        String filePath = in.nextLine();

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            StringBuilder str = new StringBuilder();

            for (String line : lines) {
                str.append(line).append('\n');
            }

            _studentManage.fromCsvString(str.toString());

            System.out.println("Nhập danh sách sinh viên thành công");
        } catch (IOException e) {
            System.out.println("Nhập danh sách sinh viên thất bại");
        }
        System.out.println("Nhấn Enter để tiếp tục");
        in.nextLine();
    }

    private static void exportToCsv() {
        Scanner in = new Scanner(System.in);

        System.out.println("Vui lòng nhập đường dẫn file: ");
        String filePath = in.nextLine();

        String csvString = _studentManage.toCsvString();
        String[] scvArray = csvString.split("\n");
        List<String> lines = Arrays.asList(scvArray);

        try {
            Files.write(Paths.get(filePath), lines, StandardCharsets.UTF_8);
            System.out.println("Xuát danh sách sinh viên thành công");
        } catch (IOException e) {
            System.out.println("Xuát danh sách sinh viên thất bại");
        }
        System.out.println("Nhấn Enter để tiếp tục");
        in.nextLine();
    }

    private static void updateStudent() {
        Scanner in = new Scanner(System.in);
        int maHS, diem;
        String tenHS, hinhAnh, diaChi, ghiChu;
        int studentIndex;

        System.out.print("Vui lòng nhập MHS của học sinh cần cập nhật: ");
        maHS = in.nextInt();
        in.nextLine();

        studentIndex = _studentManage.findStudentById(maHS);

        if (studentIndex == -1) {
            System.out.println("Học sinh vừa chọn không tồn tại");
        } else {
            System.out.println("Bạn đã chọn cập nhật thông tin cho học sinh có MHS = " + maHS);
            System.out.println("Vui lòng nhập thông tin mới");
            System.out.print("Nhập MHS: ");
            maHS = in.nextInt();
            in.nextLine();

            System.out.print("Nhập TenHS: ");
            tenHS = in.nextLine();

            System.out.print("Nhập Diem: ");
            diem = in.nextInt();
            in.nextLine();

            System.out.print("Nhập đường dẫn đến hình ảnh: ");
            hinhAnh = in.nextLine();

            System.out.print("Nhập dia chi: ");
            diaChi = in.nextLine();

            System.out.print("Nhập GhiChu: ");
            ghiChu = in.nextLine();

            _studentManage.updateStudent(studentIndex, maHS, tenHS, diem, hinhAnh, diaChi, ghiChu);
            System.out.println("Cập nhật học sinh thành công");
        }
        System.out.println("Nhấn Enter để tiếp tục");
        in.nextLine();
    }

    private static void deleteStudent() {
        Scanner in = new Scanner(System.in);
        int maHS;
        int studentIndex;

        System.out.print("Vui lòng nhập MHS cần xoá: ");
        maHS = in.nextInt();
        in.nextLine();

        studentIndex = _studentManage.findStudentById(maHS);

        if (studentIndex == -1) {
            System.out.println("Học sinh đã nhập không tồn tại");
        } else {
            System.out.print("Bạn có chắc muốn xoá học sinh có MHS = " + maHS + "? (Y/N)");
            String res = in.nextLine();
            if (res.equals("Y")) {
                _studentManage.deleteStudent(studentIndex);
                System.out.println("Xoá học sinh thành công");
            } else {
                System.out.println("Đã huỷ bỏ lệnh xoá học sinh");
            }
        }
        System.out.println("Nhấn Enter để tiếp tục");
        in.nextLine();
    }

    private static void displayMenu() {
        System.out.println("---Chương trình quản lý học sinh----");
        System.out.println("Vui lòng chọn: ");
        System.out.println("\t1. Thêm học sinh mới");
        System.out.println("\t2. Cập nhật thông tin học sinh");
        System.out.println("\t3. Xoá học sinh");
        System.out.println("\t4. Xem danh sách học sinh");
        System.out.println("\t5. Nhập danh sách học sinh từ file csv");
        System.out.println("\t6. Xuất danh sách học sinh ra file csv");
        System.out.println("\t0. Thoát chương trình");
        System.out.print("Vui lòng chọn: ");
    }

    private static void createFile() {
        File file = new File("data.bin");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Không thể tạo file data cho chương trình. Chương trình kết thúc");
                System.exit(-1);
            }
        }
    }

    private static void startProgram() {
        createFile();
        System.out.println("Đang nạp dữ liệu từ file");
        if (readData("data.bin")) {
            System.out.println("Nạp dữ liệu hoàn tất");
            System.out.println("Đã nhập " + _studentManage.getstudentCount() + " học sinh từ file");
        } else {
            System.out.println("Không thể nạp dữ liệu từ file");
        }
    }

    private static void endProgram() {
        createFile();
        System.out.println("Chuẩn bị lưu dữ liệu xuống file");
        if (writeData("data.bin")) {
            System.out.println("Lưu dữ liệu thành công, thoát chương trình");
            System.exit(0);
        } else {
            System.out.println("Không thể lưu dữ liệu xuống file, nếu thoát dữ liệu sẽ bị mất");
            System.out.print("Bạn có thật sự muốn thoát? (Y/N) ");

            Scanner in = new Scanner(System.in);
            String res = in.nextLine();
            if (res.equals("Y")) {
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String userChoice = "";

        startProgram();
        while (true) {
            displayMenu();

            userChoice = in.nextLine();

            switch (userChoice) {
                case "1":
                    addStudent();
                    break;
                case "2":
                    updateStudent();
                    break;
                case "3":
                    deleteStudent();
                    break;
                case "4":
                    showStudents();
                    break;
                case "5":
                    importFromCsv();
                    break;
                case "6":
                    exportToCsv();
                    break;
                case "0":
                    endProgram();
                    break;
                default:
                    break;
            }
        }
    }
}
