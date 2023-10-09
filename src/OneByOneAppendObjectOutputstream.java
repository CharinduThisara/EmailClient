import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class OneByOneAppendObjectOutputstream  extends ObjectOutputStream {

    //this class is made to stop adding a header to the serialized Email objects when appending

    // 1. Default constructor
    public OneByOneAppendObjectOutputstream() throws IOException
    {
        super();
    }
    // 1. Parameterized constructor
    public OneByOneAppendObjectOutputstream(OutputStream o) throws IOException
    {
        super(o);
    }
    // Method of this class overridden
    @Override
    public void writeStreamHeader() throws IOException
    {
        //override the writeStreamHeader method in ObjectOutputStream class
        //method returns without adding a header
        return;
    }
}