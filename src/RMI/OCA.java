package RMI;


import javafx.util.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

interface Ainte{
    void fun();
}
public class OCA {


    public static void main(final String []targets) {

     int[] num = {1,0,-1};

        Arrays.sort(num);

        for (int i : num){
            System.out.println(i);
        }
    }


    public int[] twoSum(int[] numbers, int target) {
        if(numbers == null || numbers.length < 2) return null;


        HashMap<Integer,Pair<Integer,Integer>> record = new HashMap<>();

        for (int i = 0;i < numbers.length ; i++ ){
            Pair<Integer,Integer> p = new Pair<>(numbers[i],i);
            record.put(numbers[i],p);
        }


        Arrays.sort(numbers);
        int i = 0, j = numbers.length-1;
        int[] res = new int[2];
        while(i < j){
            int sum = numbers[i] + numbers[j];
            if(sum == target){

                if(record.get(numbers[i]).getValue() < record.get(numbers[j]).getValue()){
                    res[0] =record.get(numbers[i]).getValue();
                    res[1] = record.get(numbers[j]).getValue();
                }else{
                    res[1] =record.get(numbers[i]).getValue();
                    res[0] = record.get(numbers[j]).getValue();
                }

                break;
            }

            else if(sum < target){
                i++;
            }else{
                j--;
            }
        }
        return res;
}
