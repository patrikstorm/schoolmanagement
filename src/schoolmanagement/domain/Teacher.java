package schoolmanagement.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * @author User1
 */
@Entity
public class Teacher implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Teacher() {
    }

    public Teacher(String name) {
        this.name = name;
    }

    @Basic
    private String name;

    @ManyToMany(mappedBy = "teachers", fetch = FetchType.EAGER)
    private List<Course> courses = new ArrayList<>();

    ;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Course> getCourses() {
      
        return this.courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public void addCourse(Course course) {
        getCourses().add(course);
        course.getTeachers().add(this);
    }

    public void removeCourse(Course course) {
        getCourses().remove(course);
    }

    @Override
    public String toString() {
        return "Teacher{" + "id=" + id + ", name=" + name + '}';
    }

}
