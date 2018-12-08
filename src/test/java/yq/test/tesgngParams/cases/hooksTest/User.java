package yq.test.tesgngParams.cases.hooksTest;

/**
 * Created by king on 2018/12/8.
 */
public class User {
    public String username = "lisi";
    private int age = 20;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String sayHello(String username) {
        return String.format("hello %s", username);
    }
}
