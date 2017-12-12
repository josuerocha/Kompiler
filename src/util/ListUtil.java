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
        
        if(list1 != null){
            output.addAll(list1);
        }
        if(list2 != null){
            output.addAll(list2);
        }
        return output;
    }
    
    public static List<Integer> makeList(int i){
        List<Integer> outputList = new ArrayList<>();
        outputList.add(i);
        return outputList;
    }
    
    
}
