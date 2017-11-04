import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by TangChen on 17/11/1.
 */
public interface DataCallBack {
    public void dataCallBack(Map<String, String> m) throws IOException;
}
