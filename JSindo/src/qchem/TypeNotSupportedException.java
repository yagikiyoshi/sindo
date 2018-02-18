package qchem;

/**
 * Thrown if the specified quantum chemistry program is not supported.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.0
 */
public class TypeNotSupportedException extends Exception {

   private static final long serialVersionUID = 1L;
   
   /**
    * Constructs the instance with a message.
    * @param message The error message.
    */
   public TypeNotSupportedException(String message){
      super(message);
   }

}
