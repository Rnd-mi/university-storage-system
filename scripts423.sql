SELECT s.name, s.age, s.faculty_id FROM students s
LEFT JOIN faculties f
ON s.faculty_id = f.id;

SELECT s.id, s.name, s.age, s.faculty_id FROM students s
JOIN avatars a
ON s.id = a.student_id;
