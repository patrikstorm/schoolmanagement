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
public class Course implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Course() {
    }

    public Course(String name) {
        this.name = name;
    }

    @Basic
    private String name;

    @ManyToMany(mappedBy = "courses", fetch = FetchType.EAGER)
    private List<Education> educations;

    @ManyToMany
    private List<Teacher> teachers;

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

    public List<Education> getEducations() {
        if (educations == null) {
            educations = new ArrayList<>();
        }
        return this.educations;
    }

    public void setEducations(List<Education> educations) {
        this.educations = educations;
    }

    public void addEducation(Education education) {
        getEducations().add(education);
    }

    public void removeEducation(Education education) {
        getEducations().remove(education);
    }

    public List<Teacher> getTeachers() {
        if (teachers == null) {
            teachers = new ArrayList<>();
        }
        return this.teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public void addTeacher(Teacher teacher) {
        getTeachers().add(teacher);
        teacher.getCourses().add(this);
    }

    public void removeTeacher(Teacher teacher) {
        getTeachers().remove(teacher);
        teacher.getCourses().remove(this);
    }

    @Override
    public String toString() {
        return "Course{" + "id=" + id + ", name=" + name + '}';
    }

}
