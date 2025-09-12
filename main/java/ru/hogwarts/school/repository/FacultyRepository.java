package ru.hogwarts.school.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.List;
import java.util.Optional;


public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    List<Faculty> findByNameOrColorContainsIgnoreCase(String name, String color);

    Optional<Faculty> findFacultyByStudentId(@Param("studentId") Long studentId);
}
