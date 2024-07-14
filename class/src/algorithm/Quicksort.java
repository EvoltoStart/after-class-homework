package algorithm;

public class Quicksort {
    static int[] partition(int arr[],int low,int high){
       int base =arr[(low+high)/2];
       while (low<=high){

           while(low<=high&&arr[low]<base){
               low++;
           }
           while(low<=high&&arr[high]>base){
               high--;
           }
           if(low<=high){
               int temp=arr[low];
               arr[low]=arr[high];
               arr[high]=temp;
               low++;
               high--;
           }
       }

       return new int[]{high,low};
    }
    static void quickSort(int arr[],int low,int high){
        if(low<high){
            int[]  p=partition(arr, low, high);
            quickSort(arr, low, p[0]);
            quickSort(arr ,p[1], high);
        }
    }

    public static void main(String[] args) {

        int arr[]={5,3,8,6,2,7,1,4};
        quickSort(arr, 0, arr.length-1);
        for(int i=0;i<arr.length;i++){
            System.out.print(arr[i]+" ");
        }

    }
}
