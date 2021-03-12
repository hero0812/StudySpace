package com.zhx._00_string;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class StringAlgorithm {
    /**
     * 单词反转
     * 输入: "  hello world!  "
     * 输出: "world! hello"
     * 解释: 1. 输入字符串可以在前面或者后面包含多余的空格，但是反转后的字符不能包括。
     * 2. 如果两个单词间有多余的空格，将反转后单词间的空格减少到只含一个。
     */
//    public String reverseWords(String s) {
//        if (s == null) {
//            return "";
//        }
//        StringBuilder res = new StringBuilder();
//        String[] splitArr = s.trim().split(" ");
//        int length = splitArr.length;
//        for (int i = length - 1; i >= 0; i--) {
//            if ("".equals(splitArr[i])) {
//                continue;
//            }
//            res.append(splitArr[i]);
//            if (i > 0) {
//                res.append(" ");
//            }
//        }
//        return res.toString();
//    }

    /**
     * 字符串反转，双指针法
     * 1. 加工源字符串，去除首尾及单词间多余空格
     * 2. 对整个串反转 -- 首尾两个指针不断交换指向的字符，同步向中间走
     * 3. 二度反转 -- 对每个单词进行反转
     *
     * @param s
     * @return
     */
    public String reverseWords(String s) {
        StringBuilder sb = trimSpace(s);
        reverse(sb, 0, sb.length() - 1);
        //查找单词位置
        reverseWord(sb);
        return sb.toString();
    }

    private StringBuilder trimSpace(String s) {
        s = s.trim();//去首尾空格
        StringBuilder trimStr = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ' ' && (i > 0 && s.charAt(i - 1) == ' ')) {
                //排除单词间多余空格
                continue;
            }
            trimStr.append(c);
        }
        return trimStr;
    }

    private void reverseWord(StringBuilder sb) {
        int n = sb.length();
        int start = 0, end = 1;
        while (end < n) {
            //单词末尾
            while (end < n && sb.charAt(end) != ' ') {
                end++;
            }
            reverse(sb, start, end - 1);//end处是空格，不参与反转
            end++;
            start = end;
        }
    }

    private void reverse(StringBuilder sb, int start, int end) {
        int head = start;
        int tail = end;
        while (head < tail) {
            char tmp = sb.charAt(head);
            sb.setCharAt(head, sb.charAt(tail));
            sb.setCharAt(tail, tmp);
            head++;
            tail--;
        }
    }

    /**
     * 字符串左旋
     * 思想：
     * 以index = n 为中轴，分别读两次源字符串s，通过res拼接起来
     *
     * @param s 源字符串
     * @param n 旋转中轴（将下标n之前的元素旋转）
     * @return 结果字符串 res
     */
    public String reverseLeftWords(String s, int n) {
        StringBuilder res = new StringBuilder();
        if (s == null) {
            return null;
        }
        for (int i = n; i < s.length(); i++) {
            res.append(s.charAt(i));
        }
        for (int i = 0; i < n; i++) {
            res.append(s.charAt(i));
        }
        return res.toString();
    }

    /**
     * 循环一遍源字符串，按照字符出现的顺序，构建字典
     *
     * @param s
     * @return
     */
    public char firstUniqueChar(String s) {
        HashMap<Character, Boolean> dic = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            dic.put(c, !dic.containsKey(c));//已经存在了相同字符的话，置为false。
        }
        //因为要求s中第一个出现的不重复字符，所以要再遍历一遍s
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (dic.get(c)) {
                return c;
            }
        }
        return ' ';
    }

    public char firstUniqChar(String s) {
        if (s == null) {
            return ' ';
        }
        LinkedHashMap<Character, Boolean> dic = new LinkedHashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            dic.put(c, !dic.containsKey(c));//已经存在了相同字符的话，置为false。
        }
        //字典是有序的，所以只要查找字典中 value 为true对应的key即可。
        for (Map.Entry<Character, Boolean> entry : dic.entrySet()) {
            if (entry.getValue()) {
                return entry.getKey();
            }
        }
        return ' ';
    }

    /**
     * 大数相加
     * 模拟列竖式，双指针从两个串的末位同时向首部移动，同时一个变量记录是否有满10进位
     * 计算结果用StringBuilder 记录，结束后反转一下
     *
     * @param num1
     * @param num2
     * @return
     */
    public String addStrings(String num1, String num2) {
        int i = num1.length() - 1;
        int j = num2.length() - 1;
        int add = 0; //记录上一轮计算是否有进位
        StringBuilder ans = new StringBuilder();
        while (i >= 0 || j >= 0 || add > 0) {
            int x = i >= 0 ? num1.charAt(i) - '0' : 0;
            int y = j >= 0 ? num2.charAt(j) - '0' : 0;
            int sum = x + y + add;
            ans.append(sum % 10);
            add = sum / 10;
            i--;
            j--;
        }
        //计算过程是个十百千...的顺序，结果需要是从高到低的顺序
        ans.reverse();
        return ans.toString();
    }

    /**
     * 验证回文串
     * 双指针分别从头、尾同步向中间移动（只考虑字母和数字字符，跳过无效字符）
     * ，若两指针总指向想等的字符则为回文串；
     *
     * @param s
     * @return
     */
    public boolean isPalindrome(String s) {
        if (s.length() == 0)
            return true;
        int l = 0, r = s.length() - 1;
        while (l <= r) {
            char left = s.charAt(l);
            char right = s.charAt(r);
            if (!isValidChar(left)) {
                l++;
                continue;
            }
            if (!isValidChar(right)) {
                r--;
                continue;
            }
            if (Character.toLowerCase(left) == Character.toLowerCase(right)) {
                l++;
                r--;
                continue;
            }
            return false;
        }
        return true;
    }

    private boolean isValidChar(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }
//    private boolean isValidChar(char c) {
//        return Character.isLetterOrDigit(c);
//    }

    public static void main(String[] args) {
//        String testString = "the sky is blue";
        String testString2 = "race a car";
//        String testString3 = "a good   example";
        new StringAlgorithm().isPalindrome(testString2);
    }
}
