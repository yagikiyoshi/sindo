package makePES;

/**
 * Manage file name conventions in MakeGrid modules
 * @author Kiyoshi Yagi
 * @version 1.0
 * @since Sindo 3.7
 */
public class GridFileName {

   private String minfoDirectory = "";
   private String mkg = "mkg-";
   private String eq  = "eq";
   
   /**
    * Returns a name for a given combination of mode (q3q2q1)
    * @param mm the mode combination
    * @return the name
    */
   private String getMCname(int[] mm){
      String basename = "q";
      for(int i=mm.length-1; i>0; i--){
       basename = basename + (mm[i]+1)+"q";         
      }
      basename = basename + (mm[0]+1);         
      return basename;
   }

   public void setMinfoDirectoryName(String name){
      minfoDirectory = name;
      if(! name.isEmpty() && ! name.endsWith("/")){
         minfoDirectory = minfoDirectory+"/";
      }
   }
   
   /**
    * Returns a filename for storing the data at Q=0
    * @return the filename
    */
   public String getEQFileName(){
      return eq;
   }
   
   /**
    * Returns a filename for storing the data of a given combination of mode (q3q2q1)
    * @param mm the mode combination
    * @return the name
    */
   public String getGridFileName(int[] mm){
      return this.getMCname(mm);
   }
   
   /**
    * Returns a filename for Q=0 including the minfo directory name 
    * @return the filename
    */
   public String getRunNameEQ(){
      return minfoDirectory+mkg+this.getEQFileName();
   }
   /**
    * Returns a filename for a grid point including the minfo directory name for a given combination, grid, and point. 
    * @param mm the mode combination
    * @param nGrid  the number of grid
    * @param npoint the number of point
    * @return the filename
    */
   public String getRunNameGrid(int[] mm, int nGrid, int npoint){
      return minfoDirectory+mkg+this.getMCname(mm)+"-"+nGrid+"-"+npoint;
   }
   
}
