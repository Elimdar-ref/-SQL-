-- Создание таблицы машин
CREATE TABLE car (
    id BIGSERIAL PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    price NUMERIC(15,2) NOT NULL
);

-- Создание таблицы людей
CREATE TABLE person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    age INTEGER NOT NULL,
    has_license BOOLEAN NOT NULL,
    car_id BIGINT
);

ALTER TABLE person ADD CONSTRAINT fk_person_car
    FOREIGN KEY (car_id)
    REFERENCES car(id)
    ON DELETE SET NULL;

ALTER TABLE person ADD CONSTRAINT chk_age_positive
CHECK (age > 0);