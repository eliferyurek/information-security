import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.math.BigInteger;


public class check {


    public static void createIntegrity(String reg, String path, String log, String hash, String pathCer) throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();

        String hashVal = "";
        String sign = "";

        String s = readFile(reg);

        String[] parts = s.split("#");
        hashVal = parts[0];
        sign = parts[1];

        MessageDigest md = MessageDigest.getInstance("MD5"); 
    
        // digest() method is called to calculate message digest 
        //  of an input digest() return array of byte 
        byte[] messageDigest = md.digest(hashVal.getBytes()); 

        // Convert byte array into signum representation 
        BigInteger no = new BigInteger(1, messageDigest); 

        // Convert message digest into hex value 
        String hashtext = no.toString(16); 
        while (hashtext.length() < 32) { 
            hashtext = "0" + hashtext; 
        } 

        String success = dtf.format(now) + ": The directory is checked and no change is detected!";
        String fail = dtf.format(now) + ": Registry file verification failed!";
        Boolean alter = true, delete = true, create = true;
        
        try{
            Signature sig = Signature.getInstance("SHA1WithRSA");

            FileInputStream fin = new FileInputStream(pathCer);
            CertificateFactory f = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate)f.generateCertificate(fin);
            PublicKey pk = certificate.getPublicKey();
    
            byte[] data = hashtext.getBytes("UTF8");
    
            sig.initVerify(pk);
            sig.update(data);
    
            byte[] decodedBytes = Base64.getDecoder().decode(sign);

            

            ArrayList<String> checkDeleted = new ArrayList<String>();
            ArrayList<String> checkCreated = new ArrayList<String>();

            String[] partsbox = hashVal.split("\n");

            for(int k = 0; k<partsbox.length; k++){
                String[] partsboxsub = partsbox[k].split(" ");
                checkDeleted.add(partsboxsub[0]);               
            }

            ArrayList<String> checkHashNew = new ArrayList<String>();
            
            for(int k = 0; k<partsbox.length; k++){
                String[] partsboxsub = partsbox[k].split(" ");
                String has = partsboxsub[1];
                checkHashNew.add(has);   
            }

            
            File[] files = new File(path).listFiles();
            for (File file : files) {
                String pathFile = file.getAbsolutePath();
                checkCreated.add(pathFile);
                
                if(checkDeleted.contains(pathFile)){
                    if(hash.equals("MD5")){

                        MessageDigest md5Digest = MessageDigest.getInstance("MD5");                    
                        
                        String checksum = getFileChecksum(md5Digest, file);

                        for(int c = 0; c<partsbox.length; c++){
                            String[] partsboxsub = partsbox[c].split(" ");
                            String has = partsboxsub[1];

                            if(!checksum.equals(has) && pathFile.equals(partsboxsub[0]))  {
                                
                                String altered = dtf.format(now) + " " + pathFile + " is altered.";
                                alter = false;
                                appendStrToFile(log, altered);
                            } 
                        }
                            
                        
                    }
        
                    else {
                        //Use SHA-1 algorithm
                        MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
                        
                        //SHA-1 checksum 
                        String shaChecksum = getFileChecksum(shaDigest, file);
                        
                        for(int c = 0; c < partsbox.length; c++){
                            String[] partsboxsub = partsbox[c].split(" ");
                            String has = partsboxsub[1];

                            if(!shaChecksum.equals(has) && pathFile.equals(partsboxsub[0]))  {
                                //System.out.println("Hash -> " + shaChecksum + " " + has + " " + shaChecksum.equals(has));
                                //System.out.println("Path -> " + pathFile + " " + partsboxsub[0] );
                                String altered = dtf.format(now) + " " + pathFile + " is altered.";
                                alter = false;
                                appendStrToFile(log, altered);
                            } 
                        }
                    }

                }
                    
                else{
                    String created = dtf.format(now) + " " + pathFile + " is created.";
                    create = false;
                    appendStrToFile(log, created);
                }
            
            }


                for(int k = 0; k<partsbox.length; k++){
                    String[] partsboxsub = partsbox[k].split(" ");
                    if(!(checkCreated.contains(partsboxsub[0]))){                        
                        String deleted = dtf.format(now) + " " + partsboxsub[0] + " is deleted.";
                        delete = false;
                        appendStrToFile(log, deleted);
                    }
                }
            
                if(sig.verify(decodedBytes) && alter && delete && create)
                    appendStrToFile(log, success);
        }
        
        catch (Exception e) {
            //e.printStackTrace();
            appendStrToFile(log, fail);
        }

        

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

    public static void appendStrToFile(String fileName, String str) 
    { 
        try { 
  
            // Open given file in append mode. 
            BufferedWriter out = new BufferedWriter( new FileWriter(fileName, true)); 
            out.write(str + "\n"); 
            out.close(); 
        } 
        catch (IOException e) { 
            System.out.println("exception occoured" + e); 
        } 
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

    
    public void checkData(byte[] hash, byte[] sign) throws Exception {

        FileInputStream fin = new FileInputStream("ichecker_cert.cer");
        CertificateFactory f = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate)f.generateCertificate(fin);
        PublicKey pk = certificate.getPublicKey();

        Signature sig = Signature.getInstance("SHA1WithRSA");


        sig.initVerify(pk);
        sig.update(hash);

        System.out.println(sig.verify(sign));
      }
}
