--LIMPADO DADOS E RESETANDO SEQUENCIA
TRUNCATE TABLE usuario RESTART IDENTITY CASCADE;
TRUNCATE TABLE professor RESTART IDENTITY CASCADE;
TRUNCATE TABLE coordenador RESTART IDENTITY CASCADE;
TRUNCATE TABLE data_excecao RESTART IDENTITY CASCADE;
TRUNCATE TABLE dia_semana_disponivel RESTART IDENTITY CASCADE;
TRUNCATE TABLE fase RESTART IDENTITY CASCADE;
TRUNCATE TABLE curso RESTART IDENTITY CASCADE;
TRUNCATE TABLE curso_fase RESTART IDENTITY CASCADE;
TRUNCATE TABLE disciplina RESTART IDENTITY CASCADE;


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

-- DATA EXCECAO
INSERT INTO  data_excecao
  (motivo, data, professor_id)
VALUES
  ('Preciso de folga', '2024-10-22', 1),
  ('Tirar o dente', '2024-11-05', 1),
  ('A padroa falou para pedir', '2024-12-08', 2),
  ('Reunião do meu filho', '2024-09-17', 3),
  ('Vovó morreu', '2024-09-28', 7),
  ('Não quero trabalhar nesse dia', '2024-08-10', 8);


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
  (carga_horaria, cor_hexadecimal, nome, carga_horaria_diaria, fase_id, curso_id, professor_id)
VALUES
  (76, '#00FF00', 'Engenharia de requisitos', 4, 1, 1, 1),
  (76, '#800080', 'Modelagem de dados', 4, 1, 1, 2),
  (152, '#964b00', 'Introdução a programação de computadores', 4, 1, 1, 3),
  (40, '#56070c', 'Fundamentos de pesquisa', 4, 1, 1, 4),
  (36, '#Ffa500', 'Introdução a Computação', 4, 1, 1, 5),

  (76, '#1E90FF', 'Programação Orientada a Objetos', 4, 2, 1, 6),
  (40, '#006400', 'Estrutura de dados', 4, 2, 1, 7),
  (36, '#DAA520', 'UI/UX Design de sistema', 4, 2, 1, 8),
  (76, '#8A2BE2', 'SGBD', 4, 2, 1, 9),
  (76, '#FF1493', 'Análise Orientada a Objetos', 4, 2, 1, 10),
  (76, '#F0E68C', 'Desenvolvimento Web', 4, 2, 1, 11),

  (76, '#FF8C00', 'Cloud & ITOps', 4, 3, 1, 12),
  (40, '#9933FF', 'Tecnologias e sistemas de informação gerencial', 4, 3, 1, 13),
  (36, '#FF3333', 'Arquitetura de software', 4, 3, 1, 14),
  (76, '#6666FF', 'Desenvolvimento Back End', 4, 3, 1, 15),
  (76, '#666600', 'Qualidade e testes de software', 4, 3, 1, 16),
  (76, '#330000', 'Desenvolvimento Front End', 4, 3, 1, 1),

  (156, '#99004C', 'Desenvolvimento Web', 4, 4, 1, 2),
  (76, '#003300', 'Desenvolvimento de sistemas embarcados', 4, 4, 1, 3),
  (76, '#FF6666', 'Engenharia de software', 4, 4, 1, 4),
  (36, '#003319', 'Fundamentos de projeto', 4, 4, 1, 5),
  (36, '#660033', 'Codificação de segurança da informação', 4, 4, 1, 6),
  (20, '#00FF80', 'Soluções para startup', 4, 4, 1, 7),

  (36, '#FFB266', 'Legilação aplicada a informação', 4, 5, 1, 8),
  (40, '#4C0099', 'Metodologia do trabalho cientifico', 4, 5, 1, 9),
  (76, '#99FFFF', 'Topicos especiais em ADS', 4, 5, 1, 10),
  (76, '#FF66B2', 'Desenvolvimento de Aplicação(Projeto)', 4, 5, 1, 11),
  (76, '#6A5ACD', 'Desenvolvimento para dispositivos móveis', 4, 5, 1, 12),
  (40, '#20B2AA', 'Desenvolvimento de aplicação(Sistema)', 4, 5, 1, 13),
  (36, '#32CD32', 'Certificações em ADS', 4, 5, 1, 14);
