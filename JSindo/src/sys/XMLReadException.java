package sys;

import java.io.IOException;

/**
 * Thrown if reading the xml file results in error (Format error, IO error).
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class XMLReadException extends IOException {

   private static final long serialVersionUID = 1L;

   /**
    * Constructs the object with a message.
    * @param message The error message.
    */
   public XMLReadException(String message){
      super(message);
   }
}
