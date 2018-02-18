package md;

/**
 * Thrown if the specified quantum chemistry program is not supported.
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.6
 */
public class BuilderException extends Exception {

   private static final long serialVersionUID = 1L;
   
   /**
    * Constructs the instance with a message.
    * @param message The error message.
    */
   public BuilderException(String message){
      super(message);
   }
}
