import java.io.Serializable;
import java.util.Set;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    boolean newUser = false;
    boolean secreteMessage = false;
    String userID = "";
    String MessageInfo = "";
    Set<String> userNames;
    String recipient;
}
