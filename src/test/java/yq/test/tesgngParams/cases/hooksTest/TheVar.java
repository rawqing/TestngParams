package yq.test.tesgngParams.cases.hooksTest;

import yq.test.tesgngParams.utils.DoIt;

import java.util.Date;

public class TheVar {

    public String name = "zhangsan";
    public int age = 19;


    public String getTime() {
        return String.valueOf(new Date().getTime());
    }

    public String getTime(String date) {
        long simpleTime = DoIt.getSimpleTime("yyyy-MM-dd HH:mm:ss", date);
        return String.valueOf(simpleTime);
    }
    public String toUp(String string){
        return string.toUpperCase();
    }
}
