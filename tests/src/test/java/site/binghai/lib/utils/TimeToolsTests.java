package site.binghai.lib.utils;

import org.junit.Test;

public class TimeToolsTests {
    @Test
    public void todayTest(){
        System.out.println(TimeTools.yesterday()[0]);
        System.out.println(TimeTools.yesterday()[1]);
        System.out.println(TimeTools.today()[0]);
        System.out.println(TimeTools.today()[1]);
    }
}
