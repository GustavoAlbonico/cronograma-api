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
    ('moda@example.com', 'senha456', 'COORDENADOR'),--2
    --moda
    ('gabriel.valga@example.com', 'senha123', 'PROFESSOR'),
    ('marina.casagrande@example.com', 'senha123', 'PROFESSOR'),
    ('endy.carlos@example.com', 'senha123', 'PROFESSOR'),
    ('debora.volpato@example.com', 'senha123', 'PROFESSOR'),
    ('josiane.minato@example.com', 'senha123', 'PROFESSOR'),
    ('lavinia.maccari@example.com', 'senha123', 'PROFESSOR'),
    ('fabiano.reis@example.com', 'senha123', 'PROFESSOR'),
    ('maria.matias@example.com', 'senha123', 'PROFESSOR'),
    ('polyane.reis@example.com', 'senha123', 'PROFESSOR'),
    ('ellen.fabrini@example.com', 'senha123', 'PROFESSOR'),
    ('eduardo.ribeiro@example.com', 'senha123', 'PROFESSOR'),--13

    ('katiane.araujo@example.com', 'senha123', 'PROFESSOR'),
    ('josilene.della@example.com', 'senha123', 'PROFESSOR'), --15

    --ads
    ('fernando.gabriel@example.com', 'senha123', 'PROFESSOR'),
    ('marcelo.mazon@example.com', 'senha123', 'PROFESSOR'),
    ('christine.vieira@example.com', 'senha123', 'PROFESSOR'),
    ('dayana.ricken@example.com', 'senha123', 'PROFESSOR'),
    ('jossuan@example.com', 'senha123', 'PROFESSOR'), --19


    ('lucas.bonfantecoordenador@example.com', 'senha456', 'COORDENADOR'), -- 20

    ('daniel.goulart@example.com', 'senha123', 'PROFESSOR'),
    ('rogerio.cortina@example.com', 'senha123', 'PROFESSOR'),
    ('muriel.benhardt@example.com', 'senha123', 'PROFESSOR'),
    ('roberto.medeiros@example.com', 'senha123', 'PROFESSOR');--24


--    ('jorge.silva@example.com', 'senha123', 'PROFESSOR'),
--    ('jailson.torquarto@example.com', 'senha123', 'PROFESSOR'),
--    ('bruno.kurzawe@example.com', 'senha123', 'PROFESSOR'),
--    ('cledemilson.santos@example.com', 'senha123', 'PROFESSOR'),
--    ('roni.edson@example.com', 'senha123', 'PROFESSOR'),
--    ('roseli.neto@example.com', 'senha123', 'PROFESSOR'),
--    ('liliane.fernandes@example.com', 'senha123', 'PROFESSOR'),

--PROFESSOR
INSERT INTO professor
  (cpf,nome_completo, telefone, usuario_id)
VALUES
--MODA
  ('12345678901', 'Gabriel Valga', '48996212844', 3),
  ('23456789012', 'Marina Casagrande', '48996212954', 4),
  ('34567890123', 'Endy Carlos', '48996212844', 5),
  ('45678901234', 'Débora Volpato', '48996212954', 6),
  ('56789012345', 'Josiane Minato', '48996212954', 7),
  ('67890123456', 'Lavinia Maccari', '48997554040', 8),
  ('78901234567', 'Fabiano Reis', '48996508090', 9),

  ('89012345678', 'Maria Matias', '45995709000', 10),
  ('90123456789', 'Polyane Reis', '45995709001', 11),
  ('01234567890', 'Ellen Fabrini', '44994700610', 12),
  ('12345566000', 'Eduardo Ribeiro', '44994700611', 13), -- 11

  ('01234501010', 'Katiane Araújo', '44994700610', 14),
  ('01010167890', 'Josilene Della', '44994700610', 15), --13

--ADS
  ('23456789012', 'Dayana Ricken', '44994700612', 16),
  ('34567890123', 'Fernando Gabriel', '44994700613', 17),
  ('45678901234', 'Marcelo Mazon', '44994700614', 18),
  ('56789012345', 'Christine Vieira', '44994700615', 19),
  ('67890123456', 'Jossuan Diniz', '44994700616', 20), -- 18

  ('30067890123', 'Daniel Goulart', '44994700613', 21),
  ('45008901234', 'Rogério Cortina', '44994700614', 22),
  ('56009012345', 'Muriel Bernhardt', '44994700615', 23),
  ('67012012056', 'Roberto Fermino Medeiros', '44994700616', 24); --22

-- DIA DA SEMANA DISPONIVEL
INSERT INTO dia_semana_disponivel
  (dia_semana_enum, professor_id)
VALUES
--moda
   ('SEGUNDA_FEIRA', 1),
   ('TERCA_FEIRA', 1),
   ('QUINTA_FEIRA', 1), --Gabriel Valga
   ('SABADO', 1),

  ('SEGUNDA_FEIRA', 2), --Marina Casagrande

  ('TERCA_FEIRA', 3),
  ('QUINTA_FEIRA', 3), --Endy Carlos
  ('SEXTA_FEIRA', 3),

  ('QUARTA_FEIRA', 4), --Débora Volpato

  ('QUINTA_FEIRA', 5), --Josiane Minato
  ('SEXTA_FEIRA', 5),

  ('TERCA_FEIRA', 6), --Lavinia Maccari
  ('QUINTA_FEIRA', 6),

  ('SEGUNDA_FEIRA', 7), --Fabiano Reis
  ('SEXTA_FEIRA', 7),

  ('TERCA_FEIRA', 8), --Maria Matias

  ('QUARTA_FEIRA', 9), --Polyane Reis

  ('TERCA_FEIRA', 10),
  ('QUINTA_FEIRA', 10), --Ellen Fabrini

  ('SEXTA_FEIRA', 11), --Eduardo Ribeiro

  ('QUARTA_FEIRA', 12), --Katiane Araújo

  ('SEXTA_FEIRA', 13), --Josilene Della

    --VARIOS CURSOS
  ('SEGUNDA_FEIRA', 14),
  ('TERCA_FEIRA', 14),--Dayana Ricken
  ('QUINTA_FEIRA', 14),
  ('SABADO', 14),

   --ADS

  ('SEGUNDA_FEIRA', 15), --Fernando Gabriel
  ('QUARTA_FEIRA', 15),

  ('TERCA_FEIRA', 16),
  ('SEGUNDA_FEIRA', 16), --Marcelo Mazon
  ('SEXTA_FEIRA', 16),
  ('SABADO', 16),

  ('QUARTA_FEIRA', 17),--Christine Vieira
  ('SEXTA_FEIRA', 17),

  ('SEGUNDA_FEIRA', 18),--Jossuan Diniz
  ('QUINTA_FEIRA', 18),

  ('SEGUNDA_FEIRA', 19),--Daniel Goulart
  ('TERCA_FEIRA', 19),

  ('SEGUNDA_FEIRA', 20),--Rogério Cortina
  ('TERCA_FEIRA', 20),

  ('QUARTA_FEIRA', 21),
  ('QUINTA_FEIRA', 21), --Muriel Bernhardt
  ('SEXTA_FEIRA', 21),

  ('TERCA_FEIRA', 22),
  ('QUINTA_FEIRA',22), --Roberto Fermino Medeiros
  ('SEXTA_FEIRA', 22),
  ('SABADO',22);

--ADS

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
  ('MODA', '15264859523', '48595962856', 2),
  ('Lucas', '21524859523', '48595962856', 20);

--FASE
INSERT INTO fase
  (numero)
VALUES
  (1),
  (2),
  (3),
  (4),
  (5),
  (6);

--CURSO
INSERT INTO curso
  (nome, coordenador_id)
VALUES
  ('Moda', 1),
  ('ADS', 2);

--CURSO_FASE
INSERT INTO curso_fase
  (curso_id, fase_id)
VALUES
--MODA
  (1,2),
  (1,4),
  (1,6),

--ADS
  (2,1),
  (2,2);

--DISCIPLINA
INSERT INTO disciplina
  (nome, carga_horaria, carga_horaria_diaria, cor_hexadecimal, extensao_boolean_enum, curso_id, fase_id, professor_id)
VALUES
--MODA
  ('Extensão em Design de Moda I', 50, 4, '#1E90FF', 'SIM', 1, 2, 1),
  ('Desenho de Moda I', 60, 4, '#006400', 'NAO', 1, 2, 2),
  ('História da Arte e da Moda', 60, 4, '#DAA520', 'NAO', 1, 2, 3),
  ('Marketing de Moda e Comportamento do Consumido', 70, 4, '#8A2BE2', 'NAO', 1, 2, 4),
  ('Ateliê de Confecção I - INICIO', 40, 4, '#FF1493', 'NAO', 1, 2, 5),
  ('Coolhunting - FINAL', 40, 4, '#F0E68C', 'NAO', 1, 2, 6),
  ('Técnicas de Modelagem I', 80, 4, '#F0E68C', 'NAO', 1, 2, 7),

  ('Materiais e Beneficiamento', 80, 4, '#99004C', 'NAO', 1, 4, 7),
  ('Ateliê de Confecção III', 80, 4, '#003300', 'NAO', 1, 4, 8),
  ('Moulage', 60, 4, '#FF6666', 'NAO', 1, 4, 9),
  ('Criatividade, Inovação e Sustentabilidade', 60, 4, '#003319', 'NAO', 1, 4, 10),
  ('Extensão em Design de Moda IV', 40, 4, '#660033', 'SIM', 1, 4, 1),
  ('Desenho Técnico de Moda - INICIO', 40, 4, '#00FF80', 'NAO', 1, 4, 3),
  ('Pesquisa de Mercado - FINAL', 40, 4, '#00FF80', 'NAO', 1, 4, 11),

  ('Desenvolvimento de Portfólio', 40, 4, '#99004C', 'NAO', 1, 6, 1),
  ('Produção Audiovisual', 40, 4, '#003300', 'NAO', 1, 6, 1),
  ('Desenvolvimento e Produção de Coleção - INICIO', 40, 4, '#FF6666', 'NAO', 1, 6, 6),
  ('Styling - Final', 40, 4, '#003319', 'NAO', 1, 6, 10),
  ('Desenvolvimento e Apresentação de Coleção', 80, 4, '#660033', 'SIM', 1, 6, 12),
  ('Desenvolvimento e Produção de Coleção - INICIO', 40, 4, '#00FF80', 'NAO', 1, 6, 3),
  ('Metodologia do Trabalho Científico - FINAL', 40, 4, '#00FF80', 'NAO', 1, 6, 14),
  ('Desenvolvimento e Produção de Coleção ', 40, 4, '#00FF80', 'NAO', 1, 6, 5),
  ('Desenvolvimento e Produção de Coleção ', 40, 4, '#00FF80', 'NAO', 1, 6, 13),

--ADS
  ('Fundamentos da Pesquisa', 40, 4, '#99004C', 'NAO', 2, 1, 14),
  ('Introdução a Computação', 36, 4, '#003300', 'NAO',2, 1, 15),
  ('Modelagem de Dados', 76, 4, '#FF6666', 'NAO', 2, 1, 16),
  ('Introdução a Programação de Computadores', 152, 4, '#003319', 'NAO', 2, 1, 17),
  ('Engenharia de Requisitos', 76, 4, '#660033', 'SIM', 2, 1, 18),
  ('Extensão em Análise e Desenvolvimento de Sistemas I', 45, 4, '#00FF80', 'SIM', 2, 1, 14),

  ('Estrutura de Dados', 40, 4, '#99004C', 'NAO', 2, 2, 16),
  ('UX/UI Design de Sistema', 36, 4, '#003300', 'NAO',2, 2, 19),
  ('Programação Orientada a Objetos', 76, 4, '#FF6666', 'NAO', 2, 2, 20),
  ('Análise Orientada a Objetos', 76, 4, '#003319', 'NAO', 2, 2, 21),
  ('Desenvolvimento Web', 76, 4, '#660033', 'SIM', 2, 2, 22),
  ('Sistema Gerenciador de Banco de Dados ', 76, 4, '#00FF80', 'NAO', 2, 2, 16),
  ('Extensão em Análise e Desenvolvimento de Sistemas II', 45, 4, '#00FF80', 'SIM', 2, 2, 16);

-- PERIODO
INSERT INTO  periodo
  (data_inicial, data_final)
VALUES
  ('2024-07-29', '2024-12-13');
