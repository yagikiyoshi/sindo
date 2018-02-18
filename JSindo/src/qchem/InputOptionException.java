package qchem;

/**
 * Thrown if reading the input options results in error.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class InputOptionException extends Exception {

   private static final long serialVersionUID = 1L;
   
   /**
    * Constructs the object with a message.
    * @param message The error message.
    */
   public InputOptionException(String message){
      super(message);
   }

}
