select student.name, student.age, faculty.name from student INNER JOIN faculty ON faculty.id = faculty_id;

select student.name, student.age, avatar.file_path from student INNER JOIN avatar ON student.id = avatar.student_id;