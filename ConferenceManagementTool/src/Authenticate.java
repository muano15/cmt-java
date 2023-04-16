import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Authenticate {
    public static String encode(String user_pword){
        byte[] encrypt= Base64.getEncoder().encode(user_pword.getBytes());
        return new String(encrypt);
    }

    public static String decode(String user_pword){
        byte[] decrypt= Base64.getDecoder().decode(user_pword.getBytes());
        return new String(decrypt);
    }

    public static String HashStr(String pword) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] messageDigest = md.digest(pword.getBytes());

        BigInteger bigInt = new BigInteger(1, messageDigest);

        return bigInt.toString(16);
    }
}
