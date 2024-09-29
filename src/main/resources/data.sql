----LIMPANDO DADOS E RESETANDO SEQUENCIA
TRUNCATE TABLE funcionalidade RESTART IDENTITY CASCADE;
TRUNCATE TABLE controller RESTART IDENTITY CASCADE;
TRUNCATE TABLE controller_funcionalidade RESTART IDENTITY CASCADE;
TRUNCATE TABLE nivel_acesso RESTART IDENTITY CASCADE;
TRUNCATE TABLE nivel_acesso_controller RESTART IDENTITY CASCADE;
TRUNCATE TABLE usuario RESTART IDENTITY CASCADE;
TRUNCATE TABLE usuario_nivel_acesso RESTART IDENTITY CASCADE;

TRUNCATE TABLE professor RESTART IDENTITY CASCADE;
TRUNCATE TABLE coordenador RESTART IDENTITY CASCADE;
TRUNCATE TABLE data_bloqueada RESTART IDENTITY CASCADE;
TRUNCATE TABLE dia_semana_disponivel RESTART IDENTITY CASCADE;
TRUNCATE TABLE fase RESTART IDENTITY CASCADE;
TRUNCATE TABLE curso RESTART IDENTITY CASCADE;
TRUNCATE TABLE curso_fase RESTART IDENTITY CASCADE;
TRUNCATE TABLE disciplina RESTART IDENTITY CASCADE;
TRUNCATE TABLE periodo RESTART IDENTITY CASCADE;
TRUNCATE TABLE aluno RESTART IDENTITY CASCADE;
TRUNCATE TABLE aluno_fase RESTART IDENTITY CASCADE;


----INSERTS
--
--FUNCIONALIDADE
INSERT INTO funcionalidade
  (nome, descricao)
VALUES
  ('CRIAR', 'Adiciona uma entidade'),--1
  ('EDITAR', 'Altera informações'),--2
  ('CARREGAR', 'Busca todos os dados'),--3
  ('INATIVAR', 'inativa um dado'),--4
  ('ATIVAR', 'ativa um dado'),--5
  ('EXCLUIR', 'Exclui uma informação do banco de dados'),--6
  ('IMPORTAR', 'importa um arquivo para dentro do sistema'),--7
  ('CARREGAR_ATIVO', 'Busca todos os dados ativos'),--8
  ('ASSOCIAR_COORDENADOR', 'associa um coordenador a um professor'),--9
  ('CARREGAR_POR_ID', 'carregar uma informação pelo id'),--10
  ('FORMULARIO_DIA_SEMANA_DISPONIVEL', 'envia um formulario com os dias da semana disponiveis'),--11
  ('EDITAR_EMAIL', 'edita o email'),--12
  ('REDEFINIR_SENHA', 'redefine a senha'),--13
  ('VALIDAR_TOKEN_REDEFINIR_SENHA', 'valida o token para redefinição da senha'), --14
  ('CARREGAR_ATIVO_POR_CURSO', 'carregar os dados ativos por curso'); --15

--CONTROLLER
INSERT INTO controller
  (nome,descricao)
VALUES
  ('USUARIO_CONTROLLER',null),--1
  ('PROFESSOR_CONTROLLER',null),--2
  ('COORDENADOR_CONTROLLER',null),--3
  ('ALUNO_CONTROLLER',null),--4
  ('EVENTO_CONTROLLER',null),--5
  ('PERIODO_CONTROLLER',null),--6
  ('FASE_CONTROLLER',null),--7
  ('CURSO_CONTROLLER',null),--8
  ('DISCIPLINA_CONTROLLER',null),--9
  ('DATA_BLOQUEADA_CONTROLLER',null),--10
  ('HISTORICO_CONTROLLER',null),--11
  ('CRONOGRAMA_CONTROLLER',null),--12
  ('DIA_CRONOGRAMA_CONTROLLER',null),--13

  ('PROFESSOR_CONTROLLER','personalizado para professores'),--14
  ('CRONOGRAMA_CONTROLLER','personalizado para professores'),--15

  ('CRONOGRAMA_CONTROLLER','personalizado para alunos');--16

--CONTROLLER_FUNCIONALIDADE
INSERT INTO  controller_funcionalidade
  (controller_id, funcionalidade_id)
VALUES
  (1,1),--CRIAR --USUARIO

  (2,1),--CRIAR
  (2,2),--EDITAR
  (2,3),--CARREGAR  -- PROFESSOR
  (2,4),--INATIVAR
  (2,5),--ATIVAR
  (2,8),--CARREGAR_ATIVO
  (2,9),--ASSOCIAR_COORDENADOR

  (3,1),--CRIAR
  (3,2),--EDITAR
  (3,3),--CARREGAR  -- COORDENADOR
  (3,4),--INATIVAR
  (3,5),--ATIVAR
  (3,8),--CARREGAR_ATIVO

  (4,1),--CRIAR
  (4,2),--EDITAR
  (4,3),--CARREGAR  --ALUNO
  (4,6),--EXCLUIR
  (4,7),--IMPORTAR

  (5,1),--CRIAR --EVENTO

  (6,1),--CRIAR
  (6,2),--EDITAR
  (6,3),--CARREGAR  --PERIODO
  (6,4),--INATIVAR
  (6,8),--CARREGAR_ATIVO

  (7,1),--CRIAR
  (7,2),--EDITAR
  (7,3),--CARREGAR  --FASE
  (7,4),--INATIVAR
  (7,5),--ATIVAR
  (7,8),--CARREGAR_ATIVO
  (7,15),--CARREGAR_ATIVO_POR_CURSO

  (8,1),--CRIAR
  (8,2),--EDITAR
  (8,3),--CARREGAR  --CURSO
  (8,4),--INATIVAR
  (8,5),--ATIVAR
  (8,8),--CARREGAR_ATIVO

  (9,1),--CRIAR
  (9,2),--EDITAR
  (9,3),--CARREGAR  --DISCIPLINA
  (9,4),--INATIVAR
  (9,5),--ATIVAR
  (9,8),--CARREGAR_ATIVO

  (10,1),--CRIAR
  (10,2),--EDITAR
  (10,3),--CARREGAR  --DATA_BLOQUEADA
  (10,6),--EXCLUIR

  (11,3),--CARREGAR  --HISTORICO

  (12,3),--CARREGAR  --CRONOGRAMA_CONTROLLER
  (12,6),--EXCLUIR

  (13,2),--EDITAR  --DIA_CRONOGRAMA_CONTROLLER
----------

  (14,11),--FORMULARIO_DIA_SEMANA_DISPONIVEL -- PROFESSOR

  (15,3),--CARREGAR  --CRONOGRAMA_CONTROLLER

------------------
  (16,3);--CARREGAR  --CRONOGRAMA_CONTROLLER

--NIVEL ACESSO
INSERT INTO nivel_acesso
  (nome, descricao, ranking_acesso)
VALUES
  ('ADMINISTRADOR', 'acesso geral', 0),
  ('COORDENADOR_GERAL', 'acesso geral', 1),
  ('COORDENADOR', 'acessso quase geral', 2),
  ('PROFESSOR', ' apenas visualiza', 3),
  ('ALUNO', 'apenas visualiza', 4);

INSERT INTO nivel_acesso_controller
  (nivel_acesso_id, controller_id)
VALUES
  (1,1), --USUARIO_CONTROLLER
  (1,2), --PROFESSOR_CONTROLLER
  (1,3), --COORDENADOR_CONTROLLER
  (1,4), --ALUNO_CONTROLLER
  (1,5), --EVENTO_CONTROLLER
  (1,6), --PERIODO_CONTROLLER --ADMINISTRADOR
  (1,7), --FASE_CONTROLLER
  (1,8), --CURSO_CONTROLLER
  (1,9), --DISCIPLINA_CONTROLLER
  (1,10), --DATA_BLOQUEADA_CONTROLLER
  (1,11), --HISTORICO_CONTROLLER
  (1,12), --CRONOGRAMA_CONTROLLER
  (1,13), --DIA_CRONOGRAMA_CONTROLLER

  (2,2), --PROFESSOR_CONTROLLER
  (2,3), --COORDENADOR_CONTROLLER
  (2,4), --ALUNO_CONTROLLER
  (2,5), --EVENTO_CONTROLLER
  (2,6), --PERIODO_CONTROLLER --COORDENADOR_GERAL
  (2,7), --FASE_CONTROLLER
  (2,8), --CURSO_CONTROLLER
  (2,9), --DISCIPLINA_CONTROLLER
  (2,10), --DATA_BLOQUEADA_CONTROLLER
  (2,11), --HISTORICO_CONTROLLER
  (2,12), --CRONOGRAMA_CONTROLLER
  (2,13), --DIA_CRONOGRAMA_CONTROLLER

  (3,2), --PROFESSOR_CONTROLLER
  (3,4), --ALUNO_CONTROLLER
  (3,5), --EVENTO_CONTROLLER      --COORDENADOR
  (3,9), --DISCIPLINA_CONTROLLER
  (3,10), --DATA_BLOQUEADA_CONTROLLER
  (3,11), --HISTORICO_CONTROLLER
  (3,12), --CRONOGRAMA_CONTROLLER
  (3,13), --DIA_CRONOGRAMA_CONTROLLER

  (4,14), --PROFESSOR_CONTROLLER    --PROFESSOR
  (4,15), --CRONOGRAMA_CONTROLLER

  (5,16); --CRONOGRAMA_CONTROLLER   -- ALUNOS

-- USUÁRIO
INSERT INTO usuario
  (cpf, senha, nome)
VALUES
    ('99999999999', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'ADMINISTRADOR'),-- ADMIN
    ('12385484901', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Moda coordenador'), --2 -- COORD

    ('12345678901', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Gabriel Valga'), --3
    ('23456789012', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Marina Casagrande'), --4
    ('34567890123', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Endy Carlos'), --5
    ('45678901234', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Débora Volpato'), --6
    ('56789012345', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Josiane Minato'), --7
    ('67890123456', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Lavinia Maccari'), --8
    ('78901234567', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Fabiano Reis'), --9
    ('89012345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Maria Matias'), --10
    ('90123456789', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Polyane Reis'), --11
    ('01234567890', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Ellen Fabrini'), --12
    ('12345566000', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Eduardo Ribeiro'), --13
    ('01234501010', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Katiane Araújo'), --14     --PROF
    ('01010167890', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Josilene Della'), --15
    ('23456789013', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Dayana Ricken'), --16
    ('34567890124', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Fernando Gabriel'), --17
    ('45678901235', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Marcelo Mazon'), --18
    ('56789012346', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Christine Vieira'), --19
    ('67890123457', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Jossuan Diniz'), --20
    ('30067890123', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Daniel Goulart'), --21
    ('45008901234', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Rogério Cortina'), --22
    ('56009012345', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Muriel Bernhardt'), --23
    ('67012012056', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Roberto Fermino Medeiros'), --24
    ('67007799056', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Jorge Henrique da Silva Naspolini'), --25
    ('00012012056', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Roni Edson dos Santos'), --26
    ('11012012056', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Bruno Kurzawe'), --27
    ('67011012056', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Liliane Fernandes'), --28
    ('62222012056', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Cledemilson dos Santos'), --29

    ('67012014456', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Lucas Bonfante Rebelo'), --30 --COORDE PROF

    ('88012345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Felipe Soares'), --31
    ('99012345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Ana Clara Lima'), --32
    ('11112345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Rodrigo Araujo'), --33
    ('22212345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Juliana Mendes'), --34
    ('33312345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Beatriz Souza'), --35 --ALUNOS
    ('44412345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Caio Pereira'), --36
    ('55512345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Luana Martins'), --37
    ('66612345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Mateus Silva'), --38
    ('77712345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Isabella Farias'), --39
    ('88812345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'João Pedro Ribeiro'); --40

INSERT INTO  usuario_nivel_acesso
  (nivel_acesso_id, usuario_id)
VALUES
  (1,1),
  (3,2),
  (4,3),
  (4,4),
  (4,5),
  (4,6),
  (4,7),
  (4,8),
  (4,9),
  (4,10),
  (4,11),
  (4,12),
  (4,13),
  (4,14),
  (4,15),
  (4,16),
  (4,17),
  (4,18),
  (4,19),
  (4,20),
  (4,21),
  (4,22),
  (4,23),
  (4,24),
  (4,25),
  (4,26),
  (4,27),
  (4,28),
  (4,29),

  (3,30),
  (4,30), --LUCAS

  (5,31),
  (5,32),
  (5,33),
  (5,34),
  (5,35),
  (5,36),
  (5,37),
  (5,38),
  (5,39),
  (5,40);

 --PROFESSOR
INSERT INTO professor
 (email, telefone, usuario_id)
VALUES
    ('gabriel.valga@example.com', '48996212844', 3), --3
    ('marina.casagrande@example.com', '48996212954', 4), --4
    ('endy.carlos@example.com', '48996212844', 5), --5
    ('debora.volpato@example.com', '48996212954', 6), --6
    ('josiane.minato@example.com', '48996212954', 7), --7
    ('lavinia.maccari@example.com', '48997554040', 8), --8
    ('fabiano.reis@example.com',  '48996508090', 9), --9
    ('maria.matias@example.com', '45995709000', 10), --10
    ('polyane.reis@example.com',  '45995709001', 11), --11
    ('ellen.fabrini@example.com', '44994700610', 12), --12
    ('eduardo.ribeiro@example.com',  '44994700611', 13), --13
    ('katiane.araujo@example.com', '44994700610', 14), --14
    ('josilene.della@example.com',  '44994700610', 15), --15
    ('dayana.ricken@example.com', '44994700612', 16), --16
    ('fernando.gabriel@example.com',  '44994700613', 17), --17
    ('marcelo.mazon@example.com', '44994700614', 18), --18
    ('christine.vieira@example.com', '44994700615', 19), --19
    ('jossuan@example.com', '44994700616', 20), --20
    ('daniel.goulart@example.com', '44994700613', 21), --21
    ('rogerio.cortina@example.com','44994700614', 22), --22
    ('muriel.benhardt@example.com', '44994700615', 23), --23
    ('roberto.medeiros@example.com','44994700616', 24), --24
    ('jorge.henrique.silva@example.com','44994700616', 25), --25
    ('roni.edson@example.com','44994700616', 26), --26
    ('bruno.kurzawe@example.com','44994700616', 27), --27
    ('liliane.fernandes@example.com', '44994700616', 28), --28
    ('cledemilson.santos@example.com', '44994700616', 29), --29
    ('lucas.bonfanteprofessor@example.com','44994700616', 30); --30

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
  ('QUARTA_FEIRA', 7),--dataex

  ('TERCA_FEIRA', 8), --Maria Matias
  ('QUARTA_FEIRA', 8), --dataex

  ('QUARTA_FEIRA', 9), --Polyane Reis

  ('TERCA_FEIRA', 10),
  ('QUINTA_FEIRA', 10), --Ellen Fabrini
  ('SABADO', 10), --dataex

  ('SEXTA_FEIRA', 11), --Eduardo Ribeiro
  ('QUINTA_FEIRA', 11), --dataex

  ('QUARTA_FEIRA', 12), --Katiane Araújo
  ('SABADO', 12), --dataex

  ('SEXTA_FEIRA', 13), --Josilene Della
  ('SABADO', 13), --dataex

    --VARIOS CURSOS
  ('SEGUNDA_FEIRA', 14),
  ('TERCA_FEIRA', 14),--Dayana Ricken
  ('QUINTA_FEIRA', 14),
  ('SABADO', 14),

   --ADS

  ('SEGUNDA_FEIRA', 15), --Fernando Gabriel
  ('QUARTA_FEIRA', 15),
  ('QUINTA_FEIRA', 15),--dataex

  ('TERCA_FEIRA', 16),
  ('SEGUNDA_FEIRA', 16), --Marcelo Mazon
  ('SEXTA_FEIRA', 16),
  ('SABADO', 16),

  ('QUARTA_FEIRA', 17),--Christine Vieira
  ('SEXTA_FEIRA', 17),
  ('QUINTA_FEIRA', 17), --dataex

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
  ('QUARTA_FEIRA', 22),
  ('QUINTA_FEIRA',22), --Roberto Fermino Medeiros
  ('SEXTA_FEIRA', 22),
  ('SABADO',22),

  ('QUARTA_FEIRA', 23),-- Jorge Henrique da Silva Naspolini
  ('SEXTA_FEIRA', 23),
  ('QUINTA_FEIRA', 23),--dataex

  ('QUINTA_FEIRA', 24),--Roni Edson dos Santos

  ('SABADO', 25), --Bruno Kurzawe

  ('SEGUNDA_FEIRA', 26), --Liliane Fernandes

  ('QUINTA_FEIRA', 27), --Cledemilson dos Santos
  ('SEXTA_FEIRA', 27),

  ('SABADO', 28);--Lucas Bonfante Rebelo

--ADS

-- DATA BLOQUEADA
INSERT INTO data_bloqueada
  (motivo, data, usuario_id)
VALUES
  ('Feriado2','2024-12-4', 2),
  ('Feriado3','2024-11-20', 2),
  ('Feriado4','2024-11-15', 2),
  ('Feriado5','2024-10-15', 2);

--COORDENADOR
INSERT INTO coordenador
  (email, telefone, usuario_id)
VALUES
  ('teste@hotmail.com.br','48595962856', 2),
  ('lucas.bonfanteprofessor@example.com','44994700616', 30);

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
  (nome, sigla, coordenador_id)
VALUES
  ('Moda','MODA', 1),
  ('Analise e Desenvolvimento de Sistemas','ADS', 2);

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
  (2,2),
  (2,3),
  (2,4),
  (2,5);

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

  ('Desenvolvimento de Portfólio', 40, 4, '#FF5733', 'NAO', 1, 6, 1),
  ('Produção Audiovisual', 40, 4, '#33FF57', 'NAO', 1, 6, 1),
  ('Desenvolvimento e Produção de Coleção - INICIO1', 40, 4, '#3357FF', 'NAO', 1, 6, 6),
  ('Styling - Final', 40, 4, '#FF33A6', 'NAO', 1, 6, 10),
  ('Desenvolvimento e Apresentação de Coleção', 80, 4, '#A633FF', 'NAO', 1, 6, 12),
  ('Desenvolvimento e Produção de Coleção - INICIO2', 40, 4, '#33FFA6', 'NAO', 1, 6, 3),
  ('Metodologia do Trabalho Científico - FINAL', 40, 4, '#FF8C33', 'NAO', 1, 6, 14),
  ('Desenvolvimento e Produção de Coleção1 ', 40, 4, '#8C33FF', 'NAO', 1, 6, 5),
  ('Desenvolvimento e Produção de Coleção2 ', 40, 4, '#33FF8C', 'NAO', 1, 6, 13),

--ADS
  ('Fundamentos da Pesquisa', 40, 4, '#FF3333', 'NAO', 2, 1, 14),
  ('Introdução a Computação', 36, 4, '#33FFFF', 'NAO',2, 1, 15),
  ('Modelagem de Dados', 76, 4, '#FFFF33', 'NAO', 2, 1, 16),
  ('Introdução a Programação de Computadores', 152, 4, '#FF33FF', 'NAO', 2, 1, 17),
  ('Engenharia de Requisitos', 76, 4, '#33FF33', 'NAO', 2, 1, 18),
  ('Extensão em Análise e Desenvolvimento de Sistemas I', 45, 4, '#3399FF', 'SIM', 2, 1, 14),

  ('Estrutura de Dados', 40, 4, '#99004C', 'NAO', 2, 2, 16),
  ('UX/UI Design de Sistema', 36, 4, '#003300', 'NAO',2, 2, 19),
  ('Programação Orientada a Objetos', 76, 4, '#FF6666', 'NAO', 2, 2, 20),
  ('Análise Orientada a Objetos', 76, 4, '#003319', 'NAO', 2, 2, 21),
  ('Desenvolvimento Web', 76, 4, '#660033', 'NAO', 2, 2, 22),
  ('Sistema Gerenciador de Banco de Dados ', 76, 4, '#00FF80', 'NAO', 2, 2, 16),
  ('Extensão em Análise e Desenvolvimento de Sistemas II', 45, 4, '#00FF80', 'SIM', 2, 2, 16),

  ('Arquitetura de Software', 36, 4, '#99004C', 'NAO', 2, 3, 20),
  ('Tecnologias e Sistemas de Informação Gerencial', 40, 4, '#003300', 'NAO',2, 3, 16),
  ('Desenvolvimento Back End', 76, 4, '#FF6666', 'NAO', 2, 3, 22),
  ('Cloud & ITOps', 76, 4, '#003319', 'NAO', 2, 3, 15),
  ('Qualidade e testes de software', 76, 4, '#660033', 'NAO', 2, 3, 21),
  ('Desenvolvimento Front End', 76, 4, '#00FF80', 'NAO', 2, 3, 23),
  ('Extensão em Análise e Desenvolvimento de Sistemas III', 45, 4, '#00FF80', 'SIM', 2, 3, 22),

  ('Desenvolvimento para sistemas embarcados', 40, 4, '#99004C', 'NAO', 2, 4, 15),
  ('Gerenciamento de projetos de software', 36, 4, '#003300', 'NAO',2, 4, 18),
  ('Engenharia de software', 76, 4, '#FF6666', 'NAO', 2, 4, 19),
  ('Desenvolvimento Full Stack', 76, 4, '#003319', 'NAO', 2, 4, 23),
  ('Codificação segura', 76, 4, '#660033', 'NAO', 2, 4, 24),
  ('Desenvolvimento Full Stack', 76, 4, '#00FF80', 'NAO', 2, 4, 22),
  ('Extensão em Análise e Desenvolvimento de Sistemas IIII', 45, 4, '#00FF80', 'SIM', 2, 4, 25),

  ('Tópicos Especiais em ADS', 76, 4, '#9933FF', 'NAO', 2, 5, 26),
  ('Desenvolvimento para dispositivos móveis', 76, 4, '#FF3399', 'NAO',2, 5, null),
  ('Metodologia do Trabalho Científico', 40, 4, '#99FF33', 'NAO', 2, 5, 14),
  ('Desenvolvimento de aplicação (Sistema)', 76, 4, '#FF6633', 'NAO', 2, 5, 22),
  ('Legislação Aplicada a Informação', 36, 4, '#6633FF', 'NAO', 2, 5, 27),
  ('Desenvolvimento de Aplicação (Projeto)', 40, 4, '#33FF66', 'NAO', 2, 5, 21),
  ('Certificações em ADS', 36, 4, '#FF3366', 'NAO', 2, 5, 27),
  ('InovADS (integradora)', 20, 4, '#66FF33', 'NAO', 2, 5, 28);

-- PERIODO
INSERT INTO  periodo
  (data_inicial, data_final)
VALUES
  ('2024-07-29', '2024-12-13');

--ALUNO
INSERT INTO aluno
  (email, telefone, usuario_id, curso_id)
VALUES
  ('felipe.soares@email.com', '11987654321', 31, 1), -- Felipe Soares
  ('ana.clara.lima@email.com', '11987654322', 32, 1), -- Ana Clara Lima
  ('rodrigo.araujo@email.com', '11987654323', 33, 1), -- Rodrigo Araujo --MODA
  ('juliana.mendes@email.com', '11987654324', 34, 1), -- Juliana Mendes
  ('beatriz.souza@email.com', '11987654325', 35, 1), -- Beatriz Souza

  ('caio.pereira@email.com', '11987654326', 36, 2), -- Caio Pereira
  ('luana.martins@email.com', '11987654327', 37, 2), -- Luana Martins
  ('mateus.silva@email.com', '11987654328', 38, 2), -- Mateus Silva -- ADS
  ('isabella.farias@email.com', '11987654329', 39, 2), -- Isabella Farias
  ('joao.pedro.ribeiro@email.com', '11987654330', 40, 2); -- João Pedro Ribeiro

--ALUNO_FASE
INSERT INTO  aluno_fase
  (aluno_id, fase_id)
VALUES
  (1,2),
  (1,4),
  (2,6),
  (3,2), --MODA
  (4,4),
  (5,6),

  (6,4),
  (6,5),
  (7,5),
  (8,1), --ADS
  (9,2),
  (10,3);
