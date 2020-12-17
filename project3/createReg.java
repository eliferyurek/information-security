import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class createReg {

    public static void createRegister(String path, String reg, String log, String hash, String pri) throws Exception {

        PrintWriter writer = new PrintWriter(reg, "UTF-8");
        PrintWriter writer_log = new PrintWriter(log, "UTF-8");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your password: ");
        String inputString = scanner.nextLine();
        // System.out.println(inputString);

        String s = readFile(pri);
        String mainKey = "";
        File[] files = new File(path).listFiles();

        try {
            String decrypted = decrypt(inputString, s);
            String meaningData = decrypted.substring(decrypted.length() - 14);
            mainKey = decrypted.substring(0, decrypted.length() - 14);

            if (meaningData.equals("privatekeyfile")) {
                // System.out.println("Decrypted: " + decrypted + " " + decrypted.length());
                // System.out.println("Main Key: " + mainKey + " " + mainKey.length());

                showFiles(files, reg, hash, log, writer, writer_log, mainKey);
            }
        } catch (Exception e) {
            DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            LocalDateTime now1 = LocalDateTime.now();
            
            appendtoLog(files, writer_log, writer, reg, hash);
            writer_log.print(dtf1.format(now1) + ": Wrong password attempt!\n");
        }

        writer_log.close();
        writer.close();
        scanner.close();
    }

    public static void showFiles(File[] files, String reg, String hash, String log, PrintWriter writer,
        PrintWriter writer_log, String realKey) throws Exception {

        appendtoLog(files, writer_log, writer, reg, hash);

        writer.close();
        String s = readFile(reg);

        MessageDigest md = MessageDigest.getInstance("MD5");

        byte[] messageDigest = md.digest(s.getBytes());

        BigInteger no = new BigInteger(1, messageDigest);

        // Convert message digest into hex value
        String hashtext = no.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }

        Signature sig = Signature.getInstance("SHA1WithRSA");

        byte[] data = hashtext.getBytes("UTF8");

        sig.initSign(loadPrivateKey(realKey));
        sig.update(data);
        byte[] signatureBytes = sig.sign();

        String signature = Base64.getEncoder().encodeToString(signatureBytes);

        String signNew = "#" + signature + "#";

        appendStrToFile(reg, signNew);

        writer_log.close();

    }

    public static void appendtoLog(File[] files, PrintWriter writer_log, PrintWriter writer, String reg, String hash)
            throws Exception {
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");  
            LocalDateTime now1 = LocalDateTime.now(); 
            writer_log.print(dtf1.format(now1) + ": Registry file is created at " + reg + "\n");
            int file_no = 0;
    
            String hashValues = "";
    
            for (File file : files) {
                
                if(hash.equals("MD5")){
                    //Use MD5 algorithm
                    MessageDigest md5Digest = MessageDigest.getInstance("MD5");
                    
                    //Get the checksum
                    String checksum = getFileChecksum(md5Digest, file);
                    file_no++;
                    //see checksum
                    
                    writer.print(file + " " + checksum + "\n");
                    hashValues = hashValues + file + " " + checksum + "\n";
                       
                }
     
                else {
                    //Use SHA-1 algorithm
                    MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
                    file_no++;
                    
                    //SHA-1 checksum 
                    String shaChecksum = getFileChecksum(shaDigest, file);
                    writer.print(file + " " + shaChecksum + "\n");
                    hashValues = hashValues + file + " " + shaChecksum + "\n";
                }
                
                writer_log.print(dtf1.format(now1) + ": " + file + " is added to registry.\n"); 
                
            }
            String str_fileNo = String.valueOf(file_no);
            writer_log.print(dtf1.format(now1) + ": ");
            writer_log.print(str_fileNo);
            writer_log.print(" files are added to the registry and registry creation is finished!\n");
    }

    public static void appendStrToFile(String fileName, String str) 
    { 
        try { 
  
            // Open given file in append mode. 
            BufferedWriter out = new BufferedWriter( new FileWriter(fileName, true)); 
            out.write(str); 
            out.close(); 
        } 
        catch (IOException e) { 
            System.out.println("exception occoured" + e); 
        } 
    } 

    public static PrivateKey loadPrivateKey(String key64) throws     GeneralSecurityException, IOException {
        byte[] clear = Base64.getDecoder().decode(key64.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;

   }


    private static String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);
        
        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0; 
        
        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };
        
        //close the stream; We don't need it now.
        fis.close();
        
        //Get the hash's bytes
        byte[] bytes = digest.digest();
        
        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        //return complete hash
    return sb.toString();
    }


    public static String readFile(String filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            return everything;
        } finally {
            br.close();
        }
    }

    public static String decrypt(String key, String encrypted) throws Exception {
        byte[] bytesOfKey = key.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] keyBytes = md.digest(bytesOfKey);

        final byte[] encryptedBytes = Base64.getMimeDecoder().decode(encrypted);

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        final byte[] resultBytes = cipher.doFinal(encryptedBytes);
        return new String(resultBytes);
    }

}
