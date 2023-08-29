-- liquibase formatted sql

-- changeset rndmi:1
CREATE INDEX students_n_idx ON students(name);

-- changeset rndmi:2
CREATE INDEX faculties_nc_idx ON faculties(name, color);
