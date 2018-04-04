package Files;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Ido on 03/04/2018.
 */
public class MediaFileComparator implements Comparator<MediaFile>, Serializable {
    @Override
    public int compare(MediaFile o1, MediaFile o2) {
        return -o1.getLastSeen().compareTo(o2.getLastSeen());
    }
}
