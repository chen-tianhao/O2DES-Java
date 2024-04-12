package O2DES_Java_Test;

class Person {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Student {
    private String name;

    public Student(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

interface Callback {
    void callback();
}

class Test
{
    public void Schedule(Callback operator) {
        operator.callback();
    }

    public void Arrive(Object... args) {
        for (Object arg : args) {
            if (arg instanceof Person) {
                System.out.println("[A] - " + ((Person)arg).getName() + " is person.");
            }
            else if (arg instanceof Student) {
                System.out.println("[A] - " + ((Student)arg).getName() + " is student.");
            }
            else
            {
                System.out.println("[A] - Input is neither person nor student");
            }
        }
    }

    public void Depart(Person p, Student s) {
        System.out.println("[D] - " + p.getName() + " is person.");
        System.out.println("[D] - " + s.getName() + " is student.");
    }

    public void executeA(Object... args)
    {
        Schedule(() -> Arrive(args));
    }

    public void executeD(Person p, Student s)
    {
        Schedule(() -> Depart(p, s));
    }
}

public class Demo {
    public static void main(String[] args) {
        Test test = new Test();
        Person p1 = new Person("CTH");
        Student s1 = new Student("CHTH");
        test.executeA(p1, s1, null);
        test.executeD(p1, s1);
    }
}
