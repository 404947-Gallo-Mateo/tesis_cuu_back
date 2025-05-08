-- DISCIPLINES
MERGE INTO disciplines (id, name, description)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'Basquet', 'Disciplina de Basquet para todas las edades.'),
  ('22222222-2222-2222-2222-222222222222', 'Boxeo', 'Disciplina de Boxeo para niños y adultos.');

-- CATEGORIES (Basquet)
MERGE INTO categories (id, name, description, monthly_fee, discipline_id, available_spaces, min_age, max_age, allowed_genre)
VALUES
  ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'U7 - U9', 'Categoría para niños de 7 a 9 años.', 14000.00, '11111111-1111-1111-1111-111111111111', 20, 7, 9, 'MIXED'),
  ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'U11 - U13', 'Categoría para niños de 11 a 13 años.', 14500.00, '11111111-1111-1111-1111-111111111111', 25, 11, 13, 'MIXED'),
  ('aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'U15', 'Categoría para adolescentes de 14 a 15 años.', 15000.00, '11111111-1111-1111-1111-111111111111', 25, 14, 15, 'MIXED'),
  ('aaaaaaa4-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'U17', 'Categoría para adolescentes de 16 a 17 años.', 15500.00, '11111111-1111-1111-1111-111111111111', 25, 16, 17, 'MIXED'),
  ('aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'U21', 'Categoría para adolescentes y jóvenes de 18 a 21 años.', 16000.00, '11111111-1111-1111-1111-111111111111', 25, 18, 21, 'MIXED'),
  ('aaaaaaa6-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Primera', 'Categoría para adultos de 21 años en adelante.', 16500.00, '11111111-1111-1111-1111-111111111111', 20, 21, 40, 'MIXED');

-- CATEGORIES (Natación)
MERGE INTO categories (id, name, description, monthly_fee, discipline_id, available_spaces, min_age, max_age, allowed_genre)
VALUES
  ('bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Recreativo', 'Recreativo básica para niños.', 12000.00, '22222222-2222-2222-2222-222222222222', 0, 10, 18, 'MIXED'),
  ('bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Competitivo', 'Competitivo avanzado para adultos.', 15000.00, '22222222-2222-2222-2222-222222222222', 0, 18, 55, 'MIXED');


-- SCHEDULES
DELETE FROM category_schedule;

INSERT INTO category_schedule (category_id, day_of_week, start_hour, end_hour)
VALUES
-- basquet U7 U9
  ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'TUESDAY', '18:15:00', '19:15:00'),
  ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'THURSDAY', '18:15:00', '19:15:00'),

-- basquet U11 U13
  ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'TUESDAY', '19:15:00', '20:15:00'),
  ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'THURSDAY', '19:15:00', '20:15:00'),

-- basquet U15
  ('aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'TUESDAY', '19:15:00', '21:15:00'),
  ('aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'THURSDAY', '19:15:00', '21:15:00'),
  ('aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'FRIDAY', '19:30:00', '21:00:00'),

-- basquet U17
  ('aaaaaaa4-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'TUESDAY', '20:15:00', '22:15:00'),
  ('aaaaaaa4-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'THURSDAY', '20:15:00', '22:15:00'),
  ('aaaaaaa4-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'FRIDAY', '19:30:00', '21:00:00'),

-- basquet U21
  ('aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'TUESDAY', '22:15:00', '23:30:00'),
  ('aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'THURSDAY', '22:15:00', '23:30:00'),

-- basquet PRIMERA
  ('aaaaaaa6-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'TUESDAY', '22:15:00', '23:30:00'),
  ('aaaaaaa6-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'THURSDAY', '22:15:00', '23:30:00'),


-- boxeo Recreativo
  ('bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'MONDAY', '19:00:00', '20:00:00'),
  ('bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'WEDNESDAY', '19:00:00', '20:00:00'),
  ('bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'FRIDAY', '19:00:00', '20:00:00'),

-- boxeo Competitivo
  ('bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'MONDAY', '19:00:00', '20:00:00'),
  ('bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'TUESDAY', '19:00:00', '20:00:00'),
  ('bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'WEDNESDAY', '19:00:00', '20:00:00'),
  ('bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'THURSDAY', '19:00:00', '20:00:00'),
  ('bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'FRIDAY', '19:00:00', '20:00:00');


-- USERS
INSERT INTO users (id, keycloak_id, role, username, email, first_name, last_name, birth_date, genre)
VALUES
  ('33333333-3333-3333-3333-333333333333', 'kc-student-uuid-11111111', 'STUDENT', 'juanjo', 'Juan@example.com', 'Juan', 'Pérez', '2010-04-15', 'MALE'),
  ('44444444-4444-4444-4444-444444444444', 'kc-teacher-uuid-22222222', 'TEACHER', 'Profe Lucia', 'Lucia@example.com', 'Lucía', 'Gómez', '1985-09-30', 'FEMALE');

-- STUDENT INSCRIPTIONS
INSERT INTO student_inscriptions (id, student_id, discipline_id, category_id)
VALUES
  ('55555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', 'aaaaaaa6-aaaa-aaaa-aaaa-aaaaaaaaaaaa'), -- Basquet Primera
  ('66666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', 'bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbbb'); -- Boxeo Recreativo

-- TEACHER DISCIPLINES
INSERT INTO teacher_disciplines (teacher_id, discipline_id)
VALUES
  ('44444444-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111'), -- Basquet
  ('44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222'); -- Boxeo
