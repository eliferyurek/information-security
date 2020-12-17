public class shiftkey {
    public static String shift_key(String shifted1, int round){
        String evenKey = String.valueOf(shifted1.charAt(0));
        String oddKey = String.valueOf(shifted1.charAt(1));
        
        for(int i=2; i<96; i=i+2) {
            evenKey = evenKey + shifted1.charAt(i);
        }
        
        for(int i=3; i<96; i=i+2) {
            oddKey = oddKey + shifted1.charAt(i);
        }
                
        if (round % 2 == 0) {
            return evenKey;
        }
        else {
            return oddKey;
        }
        
    }
}
