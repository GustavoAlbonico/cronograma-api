--LIMPADO DADOS E RESETANDO SEQUENCIA
TRUNCATE TABLE usuario RESTART IDENTITY CASCADE;
TRUNCATE TABLE professor RESTART IDENTITY CASCADE;
TRUNCATE TABLE coordenador RESTART IDENTITY CASCADE;
TRUNCATE TABLE data_bloqueada RESTART IDENTITY CASCADE;
TRUNCATE TABLE dia_semana_disponivel RESTART IDENTITY CASCADE;
TRUNCATE TABLE fase RESTART IDENTITY CASCADE;
TRUNCATE TABLE curso RESTART IDENTITY CASCADE;
TRUNCATE TABLE curso_fase RESTART IDENTITY CASCADE;
TRUNCATE TABLE disciplina RESTART IDENTITY CASCADE;
TRUNCATE TABLE periodo RESTART IDENTITY CASCADE;


--INSERTS

--USUARIO
INSERT INTO usuario
  (email, senha, nivel_acesso_enum)
VALUES
    ('teste@gmail.com', '123', 'ADMINISTRADOR'),
    ('lucas.bonfantecoordenador@example.com', 'senha456', 'COORDENADOR'),
    ('jorge.silva@example.com', 'senha123', 'PROFESSOR'),
    ('marcelo.mazon@example.com', 'senha123', 'PROFESSOR'),
    ('christine.vieira@example.com', 'senha123', 'PROFESSOR'),
    ('dayana.ricken@example.com', 'senha123', 'PROFESSOR'),
    ('jailson.torquarto@example.com', 'senha123', 'PROFESSOR'),
    ('rogerio.cortina@example.com', 'senha123', 'PROFESSOR'),
    ('daniel.goulart@example.com', 'senha123', 'PROFESSOR'),
    ('muriel.benhardt@example.com', 'senha123', 'PROFESSOR'),
    ('roberto.medeiros@example.com', 'senha123', 'PROFESSOR'),
    ('fernando.gabriel@example.com', 'senha123', 'PROFESSOR'),
    ('bruno.kurzawe@example.com', 'senha123', 'PROFESSOR'),
    ('cledemilson.santos@example.com', 'senha123', 'PROFESSOR'),
    ('roni.edson@example.com', 'senha123', 'PROFESSOR'),
    ('roseli.neto@example.com', 'senha123', 'PROFESSOR'),
    ('liliane.fernandes@example.com', 'senha123', 'PROFESSOR'),
    ('lucas.bonfante@example.com', 'senha123', 'PROFESSOR');

--PROFESSOR
INSERT INTO professor
  (cpf,nome_completo, telefone, usuario_id)
VALUES
  ('12345678901', 'Jorge Henrique da Silva Naspoli', '48996212844', 3),
  ('23456789012', 'Marcelo Mazon', '48996212954', 4),
  ('34567890123', 'Christine Vieira', '48996212844', 5),
  ('45678901234', 'Dayana Ricken', '48996212954', 6),
  ('56789012345', 'Jailson Torquarto', '48996212954', 7),
  ('67890123456', 'Rogério Cortina', '48997554040', 8),
  ('78901234567', 'Daniel Goulart', '48996508090', 9),
  ('89012345678', 'Muriel Benhardt', '45995709000', 10),
  ('90123456789', 'Roberto Fermino Medeiros', '45995709001', 11),
  ('01234567890', 'Fernando Gabriel', '44994700610', 12),
  ('12345678901', 'Bruno Kurzawe', '44994700611', 13),
  ('23456789012', 'Cledemilson dos Santos', '44994700612', 14),
  ('34567890123', 'Roni Edson dos Santos', '44994700613', 15),
  ('45678901234', 'Roseli Jenoveva Neto', '44994700614', 16),
  ('56789012345', 'Liliane Fernandes', '44994700615', 17),
  ('67890123456', 'Lucas Bonfante Rebelo', '44994700616', 18);

-- DIA DA SEMANA DISPONIVEL
INSERT INTO dia_semana_disponivel
  (dia_semana_enum, professor_id)
VALUES
   ('SEGUNDA_FEIRA', 1),

  ('SEGUNDA_FEIRA', 2),
  ('TERCA_FEIRA', 2),
  ('QUARTA_FEIRA', 2),

  ('SEGUNDA_FEIRA', 3),
  ('TERCA_FEIRA', 3),
  ('QUARTA_FEIRA', 3),

  ('SEGUNDA_FEIRA', 4),
  ('QUARTA_FEIRA', 4),
  ('SEXTA_FEIRA', 4),

  ('QUARTA_FEIRA', 5),
  ('SEXTA_FEIRA', 5),

  ('SEGUNDA_FEIRA', 6),
  ('QUARTA_FEIRA', 6),

  ('QUINTA_FEIRA', 7),
  ('SEXTA_FEIRA', 7),

  ('SEGUNDA_FEIRA', 8),
  ('TERCA_FEIRA', 8),
  ('QUARTA_FEIRA', 8),

  ('SEXTA_FEIRA', 9),
  ('QUINTA_FEIRA', 9),

  ('SEGUNDA_FEIRA', 10),
  ('TERCA_FEIRA', 10),

  ('QUARTA_FEIRA', 11),
  ('QUINTA_FEIRA', 11),
  ('SEXTA_FEIRA', 11),

  ('SEGUNDA_FEIRA', 12),
  ('QUARTA_FEIRA', 12),

  ('TERCA_FEIRA', 13),
  ('QUINTA_FEIRA', 13),

  ('SEGUNDA_FEIRA', 14),
  ('SEXTA_FEIRA', 14),

  ('TERCA_FEIRA', 15),
  ('QUARTA_FEIRA', 15),
  ('QUINTA_FEIRA', 15),

  ('SEXTA_FEIRA', 16),
  ('SEGUNDA_FEIRA', 16);

-- DATA BLOQUEADA
INSERT INTO data_bloqueada
  (motivo, data, usuario_id)
VALUES
  ('Natal','2024-12-25', 2),
  ('Feriado','2024-09-20', 2),
  ('Feriado1','2024-10-08', 2),
  ('Feriado2','2024-08-19', 2);

--COORDENADOR
INSERT INTO coordenador
  (nome_completo, cpf, telefone, usuario_id)
VALUES
  ('Lucas', '15264859523', '48595962856', 2);

--FASE
INSERT INTO fase
  (numero)
VALUES
  (1),
  (2),
  (3),
  (4),
  (5);

--CURSO
INSERT INTO curso
  (nome, coordenador_id)
VALUES
  ('Análise e desenvolvimento de sistemas', 1);

--CURSO_FASE
INSERT INTO curso_fase
  (curso_id, fase_id)
VALUES
  (1,1),
  (1,2),
  (1,3),
  (1,4),
  (1,5);

--DISCIPLINA
INSERT INTO disciplina
  (nome, carga_horaria, carga_horaria_diaria, cor_hexadecimal, extensao_boolean_enum, curso_id, fase_id, professor_id)
VALUES
  ('Engenharia de requisitos', 76, 4, '#00FF00', 'NAO', 1, 1, 1),
  ('Modelagem de dados', 76, 4, '#800080', 'NAO', 1, 1, 2),
  ('Introdução a programação de computadores', 152, 4, '#964b00', 'NAO', 1, 1, 3),
  ('Fundamentos de pesquisa', 40, 4, '#56070c', 'NAO', 1, 1, 4),
  ('Introdução a Computação', 36, 4, '#Ffa500', 'NAO', 1, 1, 5),

  ('Programação Orientada a Objetos', 76, 4, '#1E90FF', 'NAO', 1, 2, 6),
  ('Estrutura de dados', 40, 4, '#006400', 'NAO', 1, 2, 7),
  ('UI/UX Design de sistema', 36, 4, '#DAA520', 'NAO', 1, 2, 8),
  ('SGBD', 76, 4, '#8A2BE2', 'NAO', 1, 2, 9),
  ('Análise Orientada a Objetos', 76, 4, '#FF1493', 'NAO', 1, 2, 10),
  ('Desenvolvimento Web', 76, 4, '#F0E68C', 'NAO', 1, 2, 11),

  ('Cloud & ITOps', 76, 4, '#FF8C00', 'NAO', 1, 3, 12),
  ('Tecnologias e sistemas de informação gerencial', 40, 4, '#9933FF', 'NAO', 1, 3, 13),
  ('Arquitetura de software', 36, 4, '#FF3333', 'NAO', 1, 3, 14),
  ('Desenvolvimento Back End', 76, 4, '#6666FF', 'NAO', 1, 3, 15),
  ('Qualidade e testes de software', 76, 4, '#666600', 'NAO', 1, 3, 16),
  ('Desenvolvimento Front End', 76, 4, '#330000', 'NAO', 1, 3, 1),

  ('Desenvolvimento Web', 156, 4, '#99004C', 'NAO', 1, 4, 2),
  ('Desenvolvimento de sistemas embarcados', 76, 4, '#003300', 'NAO', 1, 4, 3),
  ('Engenharia de software', 76, 4, '#FF6666', 'NAO', 1, 4, 4),
  ('Fundamentos de projeto', 36, 4, '#003319', 'NAO', 1, 4, 5),
  ('Codificação de segurança da informação', 36, 4, '#660033', 'NAO', 1, 4, 6),
  ('Soluções para startup', 20, 4, '#00FF80', 'NAO', 1, 4, 7),

  ('Legislação aplicada a informação', 36, 4, '#FFB266', 'NAO', 1, 5, 8),
  ('Metodologia do trabalho cientifico', 40, 4, '#4C0099', 'NAO', 1, 5, 9),
  ('Tópicos especiais em ADS', 76, 4, '#99FFFF', 'NAO', 1, 5, 10),
  ('Desenvolvimento de Aplicação(Projeto)', 76, 4, '#FF66B2', 'NAO', 1, 5, 11),
  ('Desenvolvimento para dispositivos móveis', 76, 4, '#6A5ACD', 'NAO', 1, 5, 12),
  ('Desenvolvimento de aplicação(Sistema)', 40, 4, '#20B2AA', 'NAO', 1, 5, 13),
  ('Certificações em ADS', 36, 4, '#32CD32', 'NAO', 1, 5, 14);

-- PERIODO
INSERT INTO  periodo
  (data_inicial, data_final)
VALUES
  ('2024-07-29', '2024-12-12');
