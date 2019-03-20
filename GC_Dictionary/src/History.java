import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

class History {
    private TreeSet<LookupRecord> _lookupRecords;

    History() {
        this._lookupRecords = new TreeSet<>();
    }

    private void createFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    void loadFromFile(String filePath) {
        try {
            createFile(filePath);
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));
            _lookupRecords.addAll((TreeSet<LookupRecord>) in.readObject());
        } catch (Exception ignored) {
        }
    }

    void saveToFile(String filePath) {
        try {
            createFile(filePath);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath));
            out.writeObject(_lookupRecords);
        } catch (IOException ignored) {
        }
    }

    void addRecord(String word) {
        _lookupRecords.add(new LookupRecord(word));
    }

    HashMap<String, Integer> getFrequencyInPeriod(Date from, Date to) {
        HashMap<String, Integer> rs = new HashMap<>();

        for (LookupRecord record : _lookupRecords) {
            String word = record.getWord();
            Date date = record.getLookupTime();
            if (date.after(from) && date.before(to)) {
                Integer i = rs.get(word);
                rs.put(word, (i == null) ? 1 : i + 1);
            }
        }
        return rs;
    }

}
