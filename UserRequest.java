import java.io.Serializable;
import java.util.Random;

public class UserRequest implements Serializable {

   private static final long serialVersionUID = 1L;

   private String IP;
   private int RequestSize;

   public UserRequest() {
      this.IP = generateRandomIP();
      this.RequestSize = new Random().nextInt(100) + 1;
   }

   private String generateRandomIP() {
      Random r = new Random();
      return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
   }

   public String getIP() {
      return IP;
   }

   public int getRequestSize() {
      return RequestSize;
   }

   @Override
   public String toString() {
      return "UserRequest [IP=" + IP + ", RequestSize=" + RequestSize + "]";
   }
}