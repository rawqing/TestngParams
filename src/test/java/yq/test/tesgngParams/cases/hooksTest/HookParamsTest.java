package yq.test.tesgngParams.cases.hooksTest;

import org.testng.annotations.Test;
import yq.test.tesgngParams.utils.HooksExplain;

public class HookParamsTest {


    @Test
    public void t() {
        String s = "`my name is $name , $age years old.`";
        String s2 = "${$.getTime()}";
        String s3 = "${$.getTime(\"2017-09-01 11:11:11\")}";
        String s4 = "${new Date().getTime()}";
        HooksExplain he = new HooksExplain();
        String a = he.explainStringTemplate(s);
        Object o2 = he.sentenceRes(s2);
        Object o3 = he.sentenceRes(s3);
        Object o4 = he.sentenceRes(s4);
        System.out.println(a);
        System.out.println(o2);
        System.out.println(o3);
        System.out.println(o4);
    }

    @Test
    public void t1() {
        String s = "${$.toUp( $name)}";
        String s1 = "`my name is ${$.toUp( $name)} , $age years old.`";
        String s2 = "`my name is ${user.username} , ${user.age} years old , i say ${user.sayHello($name)}`";
        HooksExplain he = new HooksExplain();
        Object o = he.sentenceRes(s);
        Object o1 = he.explainStringTemplate(s1);
        Object o2 = he.explainStringTemplate(s2);
        System.out.println(o);
        System.out.println(o1);
        System.out.println(o2);

    }
    @Test
    public void t2() {
        String s = "${$name}";
        HooksExplain he = new HooksExplain();
        Object o = he.sentenceRes(s);
        System.out.println(o);

    }

    @Test
    public void t3() {
        HooksExplain he = new HooksExplain();
        he.getMappingPath();
    }
}
