package qchem;

/**
 * Thrown if reading the output file results in error.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class OutputFileException extends Exception {

   private static final long serialVersionUID = 1L;
   
   /**
    * Constructs the object with a message.
    * @param message The error message.
    */
   public OutputFileException(String message){
      super(message);
   }

}
