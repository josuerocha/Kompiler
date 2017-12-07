package util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jr
 */


public class ListUtil {
    
    public static List<Integer> merge(List<Integer> list1, List<Integer> list2){
        List<Integer> output = new ArrayList<>();
        
        output.addAll(list1);
        output.addAll(list2);
        
        return output;
    }
    
    
}
