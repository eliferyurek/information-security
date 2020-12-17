import java.io.FileInputStream;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class createCert {

    public static void createKey(String pathKey, String pathCer) throws Exception {
        generateKeyPair();
        generateCert();

        FileInputStream is = new FileInputStream("ichecker.jks");
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        String password = "password";
        char[] passwd = password.toCharArray();
        keystore.load(is, passwd);
        String alias = "ichecker";
        PrivateKey key = (PrivateKey) keystore.getKey(alias, passwd);


        byte[] publicKeyString = key.getEncoded();
        String encodedString = Base64.getEncoder().encodeToString(publicKeyString);
        encodedString = encodedString.concat("privatekeyfile");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your password: ");
        String inputString = scanner.nextLine();

        String aes_key = encrypt(inputString, encodedString);
        

        PrintWriter writerKey = new PrintWriter(pathKey, "UTF-8");
        writerKey.print(aes_key);

        scanner.close();
        writerKey.close();

    }
     
    // Generate keypair
    public static void generateKeyPair(){
        String command = " -genkeypair "+
                         " -alias ichecker "+
                         " -keyalg RSA "+
                         " -sigalg SHA256withRSA "+
                         " -dname CN=Java "+
                         " -storetype JKS "+
                         " -keypass password "+
                         " -keystore ichecker.jks " + 
                         " -storepass password";
        execute(command);
    }

    public static void generateCert(){

        String command = " -export " +
                         " -file certificate.cer "+
                         " -keystore ichecker.jks "+
                         " -alias ichecker "+
                         " -storepass password ";
        execute(command);
    }
     
    // Execute the commands
    public static void execute(String command){
        try{
            //System.out.println(command);
            sun.security.tools.keytool.Main.main(parse(command));
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
     
    // Parse command
    private static String[] parse(String command){
        String[] options = command.trim().split("\\s+");
        return options;
    }

    public static String encrypt(String key, String msg) throws Exception {       
        byte[] bytesOfKey = key.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = md.digest(bytesOfKey);


        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        final byte[] resultBytes = cipher.doFinal(msg.getBytes());
        return Base64.getMimeEncoder().encodeToString(resultBytes);
    }
}

