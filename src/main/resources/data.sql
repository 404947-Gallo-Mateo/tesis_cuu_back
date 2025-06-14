-- DISCIPLINES
MERGE INTO disciplines (id, name, description)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'Basquet', 'Básquet para todas las edades, con una división de Primera Categoría para los grandes talentos'),
  ('22222222-2222-2222-2222-222222222222', 'Boxeo', 'Boxeo para recreativo para niños y Boxeo competitivo para los adultos.'),
  ('33333333-3333-3333-3333-333333333333', 'Gimnasia Rítmica', 'Gimnasia Rítmica para todas las edades.'),
  ('44444444-4444-4444-4444-444444444444', 'Karate Do', 'Karate del mejor nivel continental, para todas las edades.');


-- CATEGORIES Basquet (11111111-1111-1111-1111-111111111111)
MERGE INTO categories (id, name, description, monthly_fee, discipline_id, available_spaces, min_age, max_age, allowed_genre)
VALUES
  ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'U7 - U9', 'Categoría para niños de 7 a 9 años.', 14000.00, '11111111-1111-1111-1111-111111111111', 20, 7, 9, 'MIXED'),
  ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'U11 - U13', 'Categoría para niños de 11 a 13 años.', 14500.00, '11111111-1111-1111-1111-111111111111', 25, 11, 13, 'MIXED'),
  ('aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'U15', 'Categoría para adolescentes de 14 a 15 años.', 15000.00, '11111111-1111-1111-1111-111111111111', 25, 14, 15, 'MIXED'),
  ('aaaaaaa4-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'U17', 'Categoría para adolescentes de 16 a 17 años.', 15500.00, '11111111-1111-1111-1111-111111111111', 25, 16, 17, 'MIXED'),
  ('aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'U21', 'Categoría para adolescentes y jóvenes de 18 a 21 años.', 16000.00, '11111111-1111-1111-1111-111111111111', 25, 18, 21, 'MIXED'),
  ('aaaaaaa6-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Primera', 'Categoría para adultos de 21 años en adelante.', 16500.00, '11111111-1111-1111-1111-111111111111', 20, 21, 40, 'MIXED');

-- CATEGORIES Boxeo (22222222-2222-2222-2222-222222222222)
MERGE INTO categories (id, name, description, monthly_fee, discipline_id, available_spaces, min_age, max_age, allowed_genre)
VALUES
  ('bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Recreativo', 'Recreativo básica para niños.', 12000.00, '22222222-2222-2222-2222-222222222222', 0, 10, 18, 'MIXED'),
  ('bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Competitivo', 'Competitivo avanzado para adultos.', 15000.00, '22222222-2222-2222-2222-222222222222', 0, 18, 55, 'MIXED');

-- CATEGORIES Gimnasia Ritmica (33333333-3333-3333-3333-333333333333)
MERGE INTO categories (id, name, description, monthly_fee, discipline_id, available_spaces, min_age, max_age, allowed_genre)
VALUES
  ('ccccccc2-cccc-cccc-cccc-cccccccccccc', 'Medio', 'Intermedio para niños de 6 o más años.', 12000.00, '33333333-3333-3333-3333-333333333333', 0, 6, 18, 'MIXED'),
  ('ccccccc1-cccc-cccc-cccc-cccccccccccc', 'Inicial', 'Recreativo básica para niños menores de 5 años.', 12000.00, '33333333-3333-3333-3333-333333333333', 0, 3, 5, 'MIXED'),
  ('ccccccc3-cccc-cccc-cccc-cccccccccccc', 'Avanzado', 'Nivel avanzado para mayores de 12 años.', 12000.00, '33333333-3333-3333-3333-333333333333', 0, 18, 55, 'MIXED');

-- CATEGORIES Karate Do (44444444-4444-4444-4444-444444444444)
MERGE INTO categories (id, name, description, monthly_fee, discipline_id, available_spaces, min_age, max_age, allowed_genre)
VALUES
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'Karate Tradicional', 'Karate Tradicional para todas las edades, a partir de los 5 años.', 15000.00, '44444444-4444-4444-4444-444444444444', 0, 5, 55, 'MIXED'),
  ('fffffff2-ffff-ffff-ffff-ffffffffffff', 'Deportivo', 'Karate Deportivo, a partir de los 5 años.', 15000.00, '44444444-4444-4444-4444-444444444444', 0, 5, 55, 'MIXED');


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
  ('bbbbbbb2-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'FRIDAY', '19:00:00', '20:00:00'),

-- Gimnasia Ritmica Inicial
  ('ccccccc1-cccc-cccc-cccc-cccccccccccc', 'TUESDAY', '17:30:00', '18:30:00'),
  ('ccccccc1-cccc-cccc-cccc-cccccccccccc', 'THURSDAY', '17:30:00', '18:30:00'),
-- Gimnasia Ritmica Medio
  ('ccccccc2-cccc-cccc-cccc-cccccccccccc', 'TUESDAY', '18:30:00', '19:30:00'),
  ('ccccccc2-cccc-cccc-cccc-cccccccccccc', 'THURSDAY', '18:30:00', '19:30:00'),
-- Gimnasia Ritmica Avanzado
  ('ccccccc3-cccc-cccc-cccc-cccccccccccc', 'TUESDAY', '18:30:00', '20:00:00'),
  ('ccccccc3-cccc-cccc-cccc-cccccccccccc', 'THURSDAY', '18:30:00', '20:00:00'),

--Karate Do Karate Tradicional
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'TUESDAY', '10:00:00', '11:00:00'),
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'TUESDAY', '18:15:00', '19:15:00'),
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'TUESDAY', '19:30:00', '20:30:00'),
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'TUESDAY', '20:30:00', '22:00:00'),
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'TUESDAY', '10:00:00', '11:00:00'),
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'TUESDAY', '18:15:00', '19:15:00'),
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'TUESDAY', '19:30:00', '20:30:00'),
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'TUESDAY', '20:30:00', '22:00:00'),
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'WEDNESDAY', '18:15:00', '19:15:00'),
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'WEDNESDAY', '19:30:00', '20:30:00'),
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'WEDNESDAY', '20:30:00', '22:00:00'),
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'FRIDAY', '18:15:00', '19:15:00'),
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'FRIDAY', '19:30:00', '20:30:00'),
  ('fffffff1-ffff-ffff-ffff-ffffffffffff', 'FRIDAY', '20:30:00', '22:00:00'),
  -- Karate Do Deportivo
  ('fffffff2-ffff-ffff-ffff-ffffffffffff', 'SATURDAY', '10:00:00', '12:00:00');

-- USERS
INSERT INTO users (id, keycloak_id, role, username, email, first_name, last_name, birth_date, genre)
VALUES
  ('99999999-1111-1111-1111-111111111111', 'a1d5ca02-e4e8-41d2-9fff-de744a3b782e', 'ADMIN_CUU', 'soyAdmin', 'aaaa@example.com', 'no', 'tampoco', '2004-10-16', 'MALE'),
  ('99999999-3333-3333-3333-333333333333', '3a07120e-0b2d-4edf-9560-33d17a43660e', 'STUDENT', 'juanjo', 'Juan@example.com', 'Juan', 'Pérez', '2010-04-15', 'MALE'),
  ('99999999-4444-4444-4444-444444444444', 'kc-teacher-uuid-22222222', 'TEACHER', 'Profe Lucia', 'Lucia@example.com', 'Lucía', 'Gómez', '1985-09-30', 'FEMALE'),
  ('99999999-5555-5555-5555-555555555555', '2915b895-b362-4f5f-ac0a-7d1018575c7f', 'TEACHER', 'Profe SOR', 'profe@kc.com', 'profe', 'keycloak', '1985-09-30', 'MALE');


-- STUDENT INSCRIPTIONS
INSERT INTO student_inscriptions (id, student_id, discipline_id, category_id, created_date)
VALUES
  ('11111111-5555-5555-5555-555555555555', '99999999-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', 'aaaaaaa6-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2025-05-12'), -- Basquet Primera
  ('11111111-3333-3333-3333-333333333333', '99999999-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111', 'aaaaaaa5-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2025-04-12'), -- Basquet U21
  ('11111111-6666-6666-6666-666666666666', '99999999-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', 'bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '2025-06-12'), -- Boxeo Recreativo
  ('11111111-7777-7777-7777-777777777777', '99999999-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444', 'fffffff1-ffff-ffff-ffff-ffffffffffff', '2025-01-12'), -- Karate Do Karate Tradicional
  ('11111111-8888-8888-8888-888888888888', '99999999-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'aaaaaaa6-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2025-04-12'), -- Basquet Primera
  ('11111111-9999-9999-9999-999999999999', '99999999-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 'bbbbbbb1-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '2025-03-12'), -- Boxeo Recreativo
  ('11111111-2222-2222-2222-222222222222', '99999999-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', 'fffffff1-ffff-ffff-ffff-ffffffffffff', '2025-02-12'); -- Karate Do Karate Tradicional

-- TEACHER DISCIPLINES
INSERT INTO teacher_disciplines (teacher_id, discipline_id)
VALUES
  ('99999999-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111'), -- Basquet
  ('99999999-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222222'), -- Boxeo
  ('99999999-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333'), -- gimnasia artisica
  ('99999999-5555-5555-5555-555555555555', '44444444-4444-4444-4444-444444444444'); -- karate

-- Insertamos las relaciones en la tabla intermedia discipline_teachers
INSERT INTO discipline_teachers (discipline_id, teacher_id)
VALUES
    ('11111111-1111-1111-1111-111111111111', '99999999-4444-4444-4444-444444444444'), -- Basquet
    ('22222222-2222-2222-2222-222222222222', '99999999-5555-5555-5555-555555555555'), -- Boxeo
    ('33333333-3333-3333-3333-333333333333', '99999999-5555-5555-5555-555555555555'), -- gimnasia artisica
    ('44444444-4444-4444-4444-444444444444', '99999999-5555-5555-5555-555555555555');