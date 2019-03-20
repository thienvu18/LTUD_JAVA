import java.io.Serializable;
import java.util.Date;

public class LookupRecord implements Serializable, Comparable<LookupRecord>{
    private String _word;
    private Date _lookupTime;

    @Override
    public int compareTo(LookupRecord another) {
        return this._lookupTime.compareTo(another._lookupTime);
    }

    public LookupRecord(String word) {
        this._word = word;
        this._lookupTime = new Date();
    }

    String getWord() {
        return _word;
    }

    Date getLookupTime() {
        return _lookupTime;
    }
}
