package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;


@Service
public class StudentService {

    private final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        logger.debug("Creating student with data: name={}, age={}",
                student.getName(), student.getAge());
        return studentRepository.save(student);
    }

    public Student findStudent(Long id) {
        logger.info("Was invoked method for find student");
        logger.debug("Finding student with id: {}", id);
        return studentRepository.findById(id).get();
    }

    public Student editStudent(Student student) {
        logger.info("Was invoked method for edit student");
        logger.debug("Editing student with id: {}, new data: name={}, age={}",
                student.getId(), student.getName(), student.getAge());
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        logger.info("Was invoked method for delete student");
        logger.debug("Attempting to delete student with id: {}", id);
        studentRepository.deleteById(id);
    }

    public Collection<Student> findByAge(int age) {
        logger.info("Was invoked method for find students by age");
        logger.debug("Searching for students with age: {}", age);
        ArrayList<Student> result = new ArrayList<>();
        for (Student student : studentRepository.findAll()) {
            if (student.getAge() == age) {
                result.add(student);
            }
        }
        logger.debug("Found {} students with age {}", result.size(), age);
        return result;
    }

    public List<Student> getAllStudents() {
        logger.info("Was invoked method for get all students");
        List<Student> students = studentRepository.findAll();
        logger.debug("Retrieved {} students from database", students.size());
        return students;
    }

    public List<Student> findStudentsByAgeRange(int minAge, int maxAge) {
        logger.info("Was invoked method for find students by age range");
        logger.debug("Searching for students with age between {} and {}", minAge, maxAge);

        List<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);
        logger.debug("Found {} students in age range {}-{}", students.size(), minAge, maxAge);
        return students;
    }

    public Faculty getStudentFaculty(Long studentId) {
        logger.info("Was invoked method for get student faculty");
        logger.debug("Getting faculty for student with id: {}", studentId);
        return facultyRepository.findFacultyByStudentId(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));
    }

    public List<Student> getFacultyStudents(Long facultyId) {
        logger.info("Was invoked method for get faculty students");
        logger.debug("Getting students for faculty with id: {}", facultyId);

        List<Student> students = studentRepository.findByFacultyId(facultyId);
        logger.debug("Found {} students for faculty id: {}", students.size(), facultyId);
        return students;
    }

    public long getStudentsCount() {
        logger.info("Was invoked method for get students count");
        long count = studentRepository.getCountStudents();
        logger.debug("Total students count: {}", count);
        return count;
    }

    public double getAverageAge() {
        logger.info("Was invoked method for get average age");
        Double average = studentRepository.getAverageAge();
        logger.debug("Average student age: {}", average);
        return average;
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        List<Student> students = studentRepository.getLastFiveStudents();
        logger.debug("Retrieved {} last students", students.size());
        return students;
    }

    public List<String> getStudentNamesStartingWithA() {
        logger.info("Был вызван метод для получения имен студентов, начинающихся на букву А");

        List<String> result = studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .filter(name -> name.toUpperCase().startsWith("А"))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());

        logger.debug("Найдено {} имен, начинающихся на А", result.size());
        return result;
    }

    public double getAverageStudentAge() {
        return studentRepository.findAll().stream()
                .mapToDouble(Student::getAge)
                .average()
                .orElse(0);
    }

    public long calculateSumParallel() {
        logger.info("Был вызван метод для вычисления суммы с использованием параллельного потока");
        long startTime = System.currentTimeMillis();
        long sum = LongStream.rangeClosed(1, 1_000_000)
                .parallel()
                .sum();
        long endTime = System.currentTimeMillis();
        logger.debug("Параллельный расчёт завершён за {} мс. Результат: {}", (endTime - startTime), sum);
        return sum;
    }

    public void printStudentsParallel() {
        List<Student> students = studentRepository.findAll();

        System.out.println("Основной поток " + students.get(0).getName());
        System.out.println("Основной поток " + students.get(1).getName());

        new Thread(() -> {
            System.out.println("Поток 1 " + students.get(2).getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Поток остановлен");
                ;
            }
            System.out.println("Поток 1 " + students.get(3).getName());
        }).start();

        new Thread(() -> {
            System.out.println("Поток 2 " + students.get(4).getName());
            System.out.println("Поток 2 " + students.get(5).getName());
        }).start();
    }

    public void printStudentsSynchronized() {
        logger.info("Был вызван метод для печати студентов в синхронизированном режиме");
        List<Student> students = studentRepository.findAll();

        final Object synchronous = new Object();

        System.out.println("Основной поток " + students.get(0).getName());
        System.out.println("Основной поток " + students.get(1).getName());


        new Thread(() -> {
            synchronized (synchronous) {
                System.out.println("Поток 1 " + students.get(2).getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("Поток остановлен");
                    ;
                }
                System.out.println("Поток 1 " + students.get(3).getName());
            }
        }).start();

        new Thread(() -> {
            synchronized (synchronous) {
                System.out.println("Поток 2 " + students.get(4).getName());
                System.out.println("Поток 2 " + students.get(5).getName());
            }
        }).start();
    }
}