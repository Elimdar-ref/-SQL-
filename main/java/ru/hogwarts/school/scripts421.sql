-- Таблица Студентов
ALTER TABLE student
    ADD CONSTRAINT chk_age CHECK (age >= 16 OR age IS NULL);

ALTER TABLE student
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE student
    ADD CONSTRAINT unique_student UNIQUE (name);

ALTER TABLE student
    ALTER COLUMN age SET DEFAULT 20;

-- Таблица Факультета
ALTER TABLE faculty
    ADD CONSTRAINT unique_faculty UNIQUE (name, color);
