package vibration;

public class BandProfileTest1 {

   public static void main(String[] args){
      double[] omegaData = {
            1744, 1744, 1746, 1732, 1733,
            1745, 1738, 1744, 1745, 1750,
            1710, 1700, 1714, 1692, 1704, 
            1714, 1684, 1695, 1704, 1714};
      double[] inttyData = {
            45.2, 11.1,  9.8, 52.2, 56.2,
            53.2, 50.3, 38.0, 37.4, 34.6,
            17.9, 36.6, 11.4, 62.1, 14.9,
            10.4, 60.2, 34.8, 16.6, 12.3};
      
      BandProfile intensity = new BandProfile(10.0);
      intensity.setBand(omegaData, inttyData);
      
      int nstep = 301;
      double[] omega = new double[nstep];
      double[] intty = new double[nstep];
      for(int i=0; i<nstep; i++){
         omega[i] = 1600.0 + (double)i;
         intty[i] = intensity.getIntensity(omega[i]);
      }
//      intensity.getIntensity(1600.0, 1900.0, nstep, omega, intty);
      
      for(int i=0; i<omega.length; i++){
         System.out.printf("%12.2f  %12.4f \n", omega[i], intty[i]);
      }
      
   }
}
