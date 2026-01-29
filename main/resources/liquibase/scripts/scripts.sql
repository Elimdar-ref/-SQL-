--liquibase formatted sql

--changeset your_name:1
CREATE INDEX IF NOT EXISTS idx_student_name ON student(name);

--changeset your_name:2
CREATE INDEX IF NOT EXISTS idx_faculty_title_color ON faculty(title, color);