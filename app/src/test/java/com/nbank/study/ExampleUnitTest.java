package com.nbank.study;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        List<Integer> intList = Arrays.asList(2, 3, 1, 6, 4, 5);
        System.out.println("before sort:" + intList.toString());
        System.out.println("=========================");
        int[] d = new int[]{2, 3, 1, 6, 4, 5};
        Collections.sort(intList, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {

                System.out.println("==" + o1 + "|" + o2);
                // 返回值为int类型，大于0表示正序，小于0表示逆序
//                if (o1 < o2) {
//                    return 1;
//                }
//                if (o1 > o2) {
//                    return -1;
//                }
                return o1 - o2;
                //231645 321645 123645 623145 423165 523164
            }
        });
        System.out.println("after sort:" + intList.toString());
    }
}