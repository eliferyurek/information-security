import java.util.ArrayList;
import java.util.List;

public class ecb {
    private static boolean bitOf(char in) {
        return (in == '1');
    }
    
    private static char charOf(boolean in) {
        return (in) ? '1' : '0';
    }

    public String ecb_enc(String input, byte[] data) {
        int round = 10;
        int inputLength = 0;
        StringBuilder newInput = new StringBuilder();
        newInput.append(input);
        StringBuilder finalValue = new StringBuilder();
        
        while(newInput.length() % 96 != 0) {
            newInput.append("0");
        }
            
        while(inputLength < newInput.length()) {       
            String key = new String(data);        
            
            String divideInput = "";
            int round1 = 0;	        	        
           
            String newnewInput = newInput.toString();
            for(int i=0; i<96; i++) {
                divideInput = divideInput + newnewInput.charAt(inputLength);
                inputLength++;	        	
            }
            
            List<String> strings = new ArrayList<String>();
            int index = 0;
            while (index < divideInput.length()) {
                strings.add(divideInput.substring(index, Math.min(index + 48,divideInput.length())));
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
            
            
        }
        
        
        String finalString = finalValue.toString();
        return finalString;
    }
          
    
    public static String convertStringToBinary(String input) {
    
        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            result.append(
                    String.format("%8s", Integer.toBinaryString(aChar))   // char -> int, auto-cast
                            .replaceAll(" ", "0")                         // zero pads
            );
        }
        return result.toString();
    
    }

}
    
