#  Compile the Program 
      javac -XDignore.symbol.file *.java

#  Run the Program 
     1- java ichecker createCert -k private key file -c certificate file
     2- java ichecker createReg -r  registry file -p path of the directory -l log file -h hash -k private key file 
     3- java ichecker check -r registry file -p path of the directory  -l log file -h hash -c certificate file
