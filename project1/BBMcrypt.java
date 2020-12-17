import java.io.*;
import java.util.Base64;
import java.util.Scanner;

public class BBMcrypt {

 
    public static void main(String[] args) throws IOException {

        
        String init = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
                                
        String enc_or_dec = args[0];
        String key = "", in = "", mode = "";
        BufferedWriter outFile = null;
        

        for(int i=1; i<8; i++){
            if(args[i].equals("-K"))
                key = args[i+1];
            else if(args[i].equals("-I")){
                File file = new File(args[i+1]);
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) 
                    in = in + sc.nextLine(); 
                sc.close();
            }               
            else if(args[i].equals("-M"))
                mode = args[i+1];
            else if(args[i].equals("-O")){
                
                outFile = null;
                outFile = new BufferedWriter(new FileWriter(args[i+1], true));
            }
                
        }
        byte[] decodedBytes = Base64.getDecoder().decode(key);
        

        if (enc_or_dec.equals("enc")){
            if(mode.equals("ECB")){
                ecb ecb_e = new ecb();
                String ecb = ecb_e.ecb_enc(in, decodedBytes);
                outFile.append(ecb);
            }
            else if(mode.equals("CBC")){
                cbc cbc_e = new cbc();
                String cbc = cbc_e.cbc_enc(init, in, decodedBytes);
                outFile.append(cbc);
            }
            else if(mode.equals("OFB")){
                ofb ofb_e = new ofb();
                String ofb = ofb_e.ofb_enc(init, in, decodedBytes);
                outFile.append(ofb);
            }
        }
        else if (enc_or_dec.equals("dec")){
            if(mode.equals("ECB")){
                ecb_dec ecb_drc = new ecb_dec();
                String ecb_dec = ecb_drc.ecb_decr(in, decodedBytes);
                outFile.append(ecb_dec);
            }
            else if(mode.equals("CBC")){
                cbc_dec cbc_drc = new cbc_dec();
                String cbc_dec = cbc_drc.cbc_decr(init, in, decodedBytes);
                outFile.append(cbc_dec);
            }
            else if(mode.equals("OFB")){
                ofb_dec ofb_drc = new ofb_dec();
                String ofb_dec = ofb_drc.ofb_decr(init, in, decodedBytes);
                outFile.append(ofb_dec);
            }
        }

        outFile.close();
        
        

    }



}