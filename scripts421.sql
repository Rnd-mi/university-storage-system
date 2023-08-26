ALTER TABLE students
ADD CONSTRAINT age_constraint
CHECK (age > 15);

ALTER TABLE students
ADD CONSTRAINT unique_constraint
UNIQUE (name);

ALTER TABLE students
ALTER COLUMN name
SET NOT NULL;

ALTER TABLE faculties
ADD CONSTRAINT unique_pair
UNIQUE (name, color);

ALTER TABLE students
ALTER COLUMN age
SET DEFAULT 20;