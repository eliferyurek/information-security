public class ichecker {
    public static void main(String[] args) throws Exception {
        String pathKey = "";
        String pathCer = "";
        String regCheck = "";
        String reg = "";
        String path = "";
        String pathCheck = "";
        String log = "";
        String hash = "";
        String pri = "";

        // PrintWriter in;
        
        if(args[0].equals("createCert")){ 
            for(int i=1;i<args.length;i++) {
                if(args[i].equals("-k")){ 
                    pathKey = args[i+1]; 
                }
                if(args[i].equals("-c")){ 
                    pathCer = args[i+1]; }
            }
         
         createCert.createKey(pathKey, pathCer);
          
        }
         

        if (args[0].equals("createReg")) {
            for (int i = 1; i < args.length; i++) {
                if (args[i].equals("-r")) {
                    reg = args[i + 1];
                }
                if (args[i].equals("-p")) {
                    path = args[i + 1];
                }
                if (args[i].equals("-l")) {
                    log = args[i + 1];
                }
                if (args[i].equals("-h")) {
                    hash = args[i + 1];
                }
                if (args[i].equals("-k")) {
                    pri = args[i + 1];
                }
            }

            createReg.createRegister(path, reg, log, hash, pri);
        }

        if(args[0].equals("check")){
            for(int i=1; i < args.length; i++) {
                if(args[i].equals("-r")){
                    regCheck = args[i+1];            
                }
                if(args[i].equals("-p")){
                   pathCheck = args[i+1];
                }
                if(args[i].equals("-l")){
                    log = args[i+1];                   
                }
                if(args[i].equals("-h")){
                    hash = args[i+1];
                }
                if(args[i].equals("-c")){
                    pathCer = args[i+1];
                }               
            }

            check.createIntegrity(regCheck, pathCheck, log, hash, pathCer);  
            
        }
    }


}
   