package CRUD;

import java.util.List;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import schoolmanagement.domain.Student;

public class StudentDAO {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU");

    static Scanner sc = new Scanner(System.in);

    public static void createStudent(Student s) {

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        em.persist(s);

        em.getTransaction().commit();

        em.close();

    }

    public static void updateStudent(long id) {

        EntityManager em = emf.createEntityManager();
            Student student = em.find(Student.class, id);

            System.out.println("What is the new name of the student?");
            String name = sc.nextLine();

            student.setName(name);

            em.getTransaction().begin();

            em.merge(student);

            em.getTransaction().commit();

            em.close();
    }

    public static void showAll() {
        EntityManager em = emf.createEntityManager();
        List<Student> students = em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
        for (Student student : students) {
            System.out.println(student);
        }

    }
}
