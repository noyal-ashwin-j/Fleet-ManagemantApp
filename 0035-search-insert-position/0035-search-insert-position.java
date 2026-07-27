class Solution {
    public int searchInsert(int[] nums, int target) {
        int low = 0;
        int high = nums.length-1;
        int mid=0;
        for (int i=0; i<nums.length;i++){
            for ( int j=0; j<nums.length;j++){
                mid=(low+high)/2;
                if(nums[mid]==target){
                    return mid;
                }
                else if(target>nums[mid]){
                   low = mid+ 1;                   
                }
                else if (target<nums[mid]){
                   high= mid-1;
                }
            }
        }return low;
    }
}