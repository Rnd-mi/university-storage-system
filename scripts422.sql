CREATE TABLE cars(
	id SERIAL PRIMARY KEY,
	brand VARCHAR(30) NOT NULL,
	price BIGINT CHECK (price > 0)
);

CREATE TABLE persons(
	id SERIAL PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	age SMALLINT CHECK (age > 0),
	has_driver_license BOOLEAN DEFAULT false,
	car_id INTEGER
);

ALTER TABLE persons
ADD CONSTRAINT fk_constraint
FOREIGN KEY(car_id)
REFERENCES cars(id)
ON DELETE SET NULL;
