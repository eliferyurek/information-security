import java.util.HashMap;

public class scramble {

    private static boolean bitOf(char in) {
        return (in == '1');
    }
    
    private static char charOf(boolean in) {
        return (in) ? '1' : '0';
    }


    public static String subsfunction(String resultxor) {
        //access to hashmap
        HashMap<String, String> subBoxHash = substitutionBox();
        
        
        //divide the xor result into eight
        String p1 = String.valueOf(resultxor.charAt(0));
        String p2 = String.valueOf(resultxor.charAt(6));
        String p3 = String.valueOf(resultxor.charAt(12));
        String p4 = String.valueOf(resultxor.charAt(18));
        String p5 = String.valueOf(resultxor.charAt(24));
        String p6 = String.valueOf(resultxor.charAt(30));
        String p7 = String.valueOf(resultxor.charAt(36));
        String p8 = String.valueOf(resultxor.charAt(42));
        
        
        for(int i=1; i<6; i++) {
            p1 = p1 + resultxor.charAt(i);
        }
        
        for(int i=7; i<12; i++) {
            p2 = p2 + resultxor.charAt(i);
        }
        
        for(int i=13; i<18; i++) {
            p3 = p3 + resultxor.charAt(i);
        }
        
        for(int i=19; i<24; i++) {
            p4 = p4 + resultxor.charAt(i);
        }
        
        for(int i=25; i<30; i++) {
            p5 = p5 + resultxor.charAt(i);
        }
        
        for(int i=31; i<36; i++) {
            p6 = p6 + resultxor.charAt(i);
        }
        
        for(int i=37; i<42; i++) {
            p7 = p7 + resultxor.charAt(i);
        }
        
        for(int i=43; i<48; i++) {
            p8 = p8 + resultxor.charAt(i);
        }

        
        //xor divisors
        StringBuilder p12 = new StringBuilder();
        StringBuilder p34 = new StringBuilder();
        StringBuilder p56 = new StringBuilder();
        StringBuilder p78 = new StringBuilder();
    
        for (int i = 0; i < p1.length(); i++) {
            p12.append(charOf(bitOf(p1.charAt(i)) ^ bitOf(p2.charAt(i))));
        }
        
        for (int i = 0; i < p3.length(); i++) {
            p34.append(charOf(bitOf(p3.charAt(i)) ^ bitOf(p4.charAt(i))));
        }
        
        for (int i = 0; i < p5.length(); i++) {
            p56.append(charOf(bitOf(p5.charAt(i)) ^ bitOf(p6.charAt(i))));
        }
        
        for (int i = 0; i < p7.length(); i++) {
            p78.append(charOf(bitOf(p7.charAt(i)) ^ bitOf(p8.charAt(i))));
        }
    
        String newp12 = p12.toString();
        String newp34 = p34.toString();
        String newp56 = p56.toString();
        String newp78 = p78.toString();
        
       
        
        //take values from hashmap
        String valuep1 = subBoxHash.get(p1);
        String valuep2 = subBoxHash.get(p2);
        String valuep3 = subBoxHash.get(p3);
        String valuep4 = subBoxHash.get(p4);
        String valuep5 = subBoxHash.get(p5);
        String valuep6 = subBoxHash.get(p6);
        String valuep7 = subBoxHash.get(p7);
        String valuep8 = subBoxHash.get(p8);
        String valuep12 = subBoxHash.get(newp12);
        String valuep34 = subBoxHash.get(newp34);
        String valuep56 = subBoxHash.get(newp56);
        String valuep78 = subBoxHash.get(newp78);
    
        //combine them
        String permutation = valuep1 + valuep2 + valuep3 + valuep4 + valuep5 + valuep6 + valuep7 + valuep8 + valuep12 + valuep34 + valuep56 + valuep78;
        StringBuilder perm = new StringBuilder();
        
        //apply permutation process
        for(int i=0; i<47; i=i+2) {
            String temp = String.valueOf(permutation.charAt(i));
            String temp2 = String.valueOf(permutation.charAt(i+1));
            
            perm.append(temp2);
            perm.append(temp);
        }
        
        
        String finalperm = perm.toString();
        
        
        return finalperm;
    }
       
    
    public static HashMap<String, String> substitutionBox() {
        HashMap<String, String> subBox = new HashMap<String, String>();
        
        //00
        subBox.put("000000", "0010"); 
        subBox.put("000010", "1100");
        subBox.put("000100", "0100");
        subBox.put("000110", "0001");
        
        subBox.put("001000", "0111");
        subBox.put("001010", "1010"); 
        subBox.put("001100", "1011");
        subBox.put("001110", "0110");
        
        subBox.put("010000", "1000");
        subBox.put("010010", "0101");
        subBox.put("010100", "0011"); 
        subBox.put("010110", "1111");
        
        subBox.put("011000", "1101");
        subBox.put("011010", "0000");
        subBox.put("011100", "1110");
        subBox.put("011110", "1001");
        
        //01
        subBox.put("000001", "1110"); 
        subBox.put("000011", "1011");
        subBox.put("000101", "0010");
        subBox.put("000111", "1100");
        
        subBox.put("001001", "0100");
        subBox.put("001011", "0111"); 
        subBox.put("001101", "1101");
        subBox.put("001111", "0001");
        
        subBox.put("010001", "0101");
        subBox.put("010011", "0000");
        subBox.put("010101", "1111"); 
        subBox.put("010111", "1010");
        
        subBox.put("011001", "0011");
        subBox.put("011011", "1001");
        subBox.put("011101", "1000");
        subBox.put("011111", "0110");
        
        //10
        subBox.put("100000", "0100"); 
        subBox.put("100010", "0010");
        subBox.put("100100", "0001");
        subBox.put("100110", "1011");
        
        subBox.put("101000", "1010");
        subBox.put("101010", "1101"); 
        subBox.put("101100", "0111");
        subBox.put("101110", "1000");
        
        subBox.put("110000", "1111");
        subBox.put("110010", "1001");
        subBox.put("110100", "1100"); 
        subBox.put("110110", "0101");
        
        subBox.put("111000", "0110");
        subBox.put("111010", "0011");
        subBox.put("111100", "0000");
        subBox.put("111110", "1110");
        
        //11
        subBox.put("100001", "1011"); 
        subBox.put("100011", "1000");
        subBox.put("100101", "1100");
        subBox.put("100111", "0111");
        
        subBox.put("101001", "0001");
        subBox.put("101011", "1110"); 
        subBox.put("101101", "0010");
        subBox.put("101111", "1101");
        
        subBox.put("110001", "0110");
        subBox.put("110011", "1111");
        subBox.put("110101", "0000"); 
        subBox.put("110111", "1001");
        
        subBox.put("111001", "1010");
        subBox.put("111011", "0100");
        subBox.put("111101", "0101");
        subBox.put("111111", "0011");
        
        return subBox;
        
    }
}
