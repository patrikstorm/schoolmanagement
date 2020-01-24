/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CRUD;

import java.util.List;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import schoolmanagement.domain.Course;

public class CourseDAO {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU");

    static Scanner sc = new Scanner(System.in);

    public static void createCourse() {
        System.out.println("What is the name of the new course?");
        String name = sc.nextLine();

        EntityManager em = emf.createEntityManager();

        Course course = new Course(name);

        em.getTransaction().begin();

        em.persist(course);

        em.getTransaction().commit();
    }

    public static void updateCourse(Long id) {

        EntityManager em = emf.createEntityManager();

        Course course = em.find(Course.class, id);

        System.out.println("What is the new name for the course?");
        String name = sc.nextLine();

        em.getTransaction().begin();
        course.setName(name);
        em.getTransaction().commit();

    }

    public static void deleteCourse(Long id) {

        EntityManager em = emf.createEntityManager();

        Course course = em.find(Course.class, id);

        em.getTransaction().begin();

        em.remove(course);

        em.getTransaction().commit();

    }

    public static void showCourse(Long id) {

        EntityManager em = emf.createEntityManager();

        Course course = em.find(Course.class, id);

        System.out.println(course);

    }


    public static void showEducationCourses() {

        System.out.println("Educations:");

        EducationDAO.showAll().forEach(education -> {
            System.out.println("Education: [" + education.getId() + "] - " + education.getName());
            education.getCourses().forEach(System.out::println);
        });;
    }

    public static List<Course> showAll() {

        EntityManager em = emf.createEntityManager();
        List<Course> courses = em.createQuery("SELECT t FROM Course t", Course.class).getResultList();
        for (Course course : courses) {
            System.out.println(course);
        }
        return courses;
    }

}
