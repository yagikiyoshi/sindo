package md;

public interface SegmentList {

   /**
    * Returns the number of segments
    * @return the number of segments
    */
   public int getNumOfSegment();

   /**
    * Returns all segments
    * @return segments in array
    */
   public Segment[] getSegmentALL();   
   
   /**
    * Returns a segment
    * @param index The index of the segment 
    * @return the segment
    */
   public Segment getSegment(int index);

}
