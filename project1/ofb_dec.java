import java.util.ArrayList;
import java.util.List;

public class ofb_dec {
    private static boolean bitOf(char in) {
        return (in == '1');
    }
    
    private static char charOf(boolean in) {
        return (in) ? '1' : '0';
    }
    
    public String ofb_decr(String init, String input, byte[] data) {
        int round = 10;
        int inputLength = 0;
        StringBuilder newInput = new StringBuilder();
        newInput.append(input);
        StringBuilder finalValue = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        while(newInput.length() % 96 != 0) {
            newInput.append("0");
        }
        
        while(inputLength < newInput.length()) {
            String key = new String(data);
            
            String divideInput = "";
            int round1 = 0;
                                              
            for(int i=0; i<96; i++) {
                divideInput = divideInput + newInput.charAt(inputLength);
                inputLength++;	        	
            }
            
            List<String> strings = new ArrayList<String>();
            int index = 0;
            while (index < init.length()) {
                strings.add(init.substring(index, Math.min(index + 48, init.length())));
                index += 48;
            } 
            String left = strings.get(0);
            String right = strings.get(1);
           
            
            while(round1 != round) {
                
                String shifted1 = key.substring(1)+ key.substring(0,1);
                
                String evenKey =  shiftkey.shift_key(shifted1, round1);
                
                StringBuilder sb = new StringBuilder();
    
                //ri xor ki
                for (int i = 0; i < right.length(); i++) {
                    sb.append(charOf(bitOf(right.charAt(i)) ^ bitOf(evenKey.charAt(i))));
                }
    
                String resultxor = sb.toString();

                String funcresult = scramble.subsfunction(resultxor);
                
                StringBuilder leftxor = new StringBuilder();
                for (int i = 0; i < left.length(); i++) {
                    leftxor.append(charOf(bitOf(left.charAt(i)) ^ bitOf(funcresult.charAt(i))));
                }
                
                String lastxor = leftxor.toString();                
                
                key = shifted1;
                
                left = right;
                right = lastxor;
                
                round1++;
                
            }
            
            
            finalValue.append(left);
            finalValue.append(right);
            
            String xor = left + right;        
    
            //ri xor ki
            for (int i = 0; i < xor.length(); i++) {
                sb1.append(charOf(bitOf(divideInput.charAt(i)) ^ bitOf(xor.charAt(i))));
            }
            
            init = left + right;
           
        }
        
        
        //ri xor ki
        
        String finalString = sb1.toString();
        return finalString;
    }
}
