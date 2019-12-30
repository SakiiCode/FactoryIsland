package ml.sakii.factoryisland;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

class ProxyPrintStream extends PrintStream{    
    private PrintStream fileStream = null;
    private PrintStream originalPrintStream = null;
    public ProxyPrintStream(PrintStream out, String FilePath) {
        super(out);
        originalPrintStream = out;
         try {
             FileOutputStream fout = new FileOutputStream(FilePath,true);
             fileStream = new PrintStream(fout);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
    }    
    @Override
	public void print(final String str) {
        originalPrintStream.print(str);
        fileStream.print(str);
    }
    @Override
	public void println(final String str) {
        originalPrintStream.println(str);
        fileStream.println(str);
    }        
}