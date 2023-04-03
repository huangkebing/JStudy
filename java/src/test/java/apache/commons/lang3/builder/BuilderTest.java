package apache.commons.lang3.builder;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;

public class BuilderTest {

    @Test
    public void test(){
        Person a = new Person("a", 18);
        System.out.println(a);
    }
}

class Person{
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("age", age)
                .toString();
//        return "Person{" +
//                "name='" + name + '\'' +
//                ", age=" + age +
//                '}';
    }
}
