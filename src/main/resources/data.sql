----INSERTS
--
--FUNCIONALIDADE
INSERT INTO funcionalidade
  (id,nome, descricao)
VALUES
  (1,'CRIAR', 'Adiciona uma entidade'),--1
  (2,'EDITAR', 'Altera informações'),--2
  (3,'CARREGAR', 'Busca todos os dados'),--3
  (4,'INATIVAR', 'inativa um dado'),--4
  (5,'ATIVAR', 'ativa um dado'),--5
  (6,'EXCLUIR', 'Exclui uma informação do banco de dados'),--6
  (7,'IMPORTAR', 'importa um arquivo para dentro do sistema'),--7
  (8,'CARREGAR_ATIVO', 'Busca todos os dados ativos'),--8
  (9,'ASSOCIAR', 'associa um coordenador a um professor'),--9
  (10,'CARREGAR_POR_ID', 'carregar uma informação pelo id'),--10
  (11,'FORMULARIO', 'envia um formulario com os dias da semana disponiveis relacionado ao professor'),--11
  (12,'EDITAR_gmail', 'edita o gmail'),--12
  (13,'REDEFINIR_SENHA', 'redefine a senha'),--13
  (14,'VALIDAR_TOKEN_REDEFINIR_SENHA', 'valida o token para redefinição da senha'), --14
  (15,'CARREGAR_ATIVO_POR_CURSO', 'carregar os dados ativos por curso'), --15
  (16,'CARREGAR_POR_PERIODO', 'carregar os dados por periodo, utlizado em visualizar de cronogramas'), --16
  (17,'CARREGAR_POR_USUARIO', 'carregar os dados por usuario, utilizado em index de alunos'),
  (18,'CARREGAR_DIA_SEMANA_DISPONIVEL', 'verifica se o professor possui dia semana disponvel (formulario)'),
  (19,'VISUALIZAR', 'altera o status da notificação para visualizado')
ON CONFLICT (id) DO NOTHING;

--CONTROLLER
INSERT INTO controller
  (id,nome,descricao)
VALUES
  (1,'USUARIO_CONTROLLER','ADMINISTRADOR'),--1
  (2,'PROFESSOR_CONTROLLER',null),--2
  (3,'COORDENADOR_CONTROLLER',null),--3
  (4,'ALUNO_CONTROLLER',null),--4
  (5,'EVENTO_CONTROLLER',null),--5
  (6,'PERIODO_CONTROLLER',null),--6
  (7,'FASE_CONTROLLER',null),--7
  (8,'CURSO_CONTROLLER',null),--8
  (9,'DISCIPLINA_CONTROLLER',null),--9
  (10,'DATA_BLOQUEADA_CONTROLLER',null),--10
  (11,'HISTORICO_CONTROLLER',null),--11
  (12,'CRONOGRAMA_CONTROLLER',null),--12
  (13,'DIA_CRONOGRAMA_CONTROLLER',null),--13

  (14,'PROFESSOR_CONTROLLER','PROFESSORES'),--14
  (15,'CRONOGRAMA_CONTROLLER','PROFESSORES'),--15

  (16,'CRONOGRAMA_CONTROLLER','ALUNOS'),--16

  (17,'USUARIO_CONTROLLER','rankingAcesso > 0'),--17

  (18,'CURSO_CONTROLLER','rankingAcesso == 2'),--18

  (19,'CURSO_CONTROLLER','rankingAcesso > 2'),

  (20,'DIA_SEMANA_DISPONIVEL_CONTROLLER',null),

  (21,'PROFESSOR_CONTROLLER','ALUNOS'),

  (22,'PERIODO_CONTROLLER','rankingAcesso > 2'),

  (23,'PERIODO_CONTROLLER','COORDENADOR'),

  (24,'FASE_CONTROLLER','COORDENADOR')
ON CONFLICT (id) DO NOTHING;

--CONTROLLER_FUNCIONALIDADE
INSERT INTO  controller_funcionalidade
  (controller_id, funcionalidade_id)
VALUES
  (1,1),--CRIAR --USUARIO
  (1,13),--REDEFINIR_SENHA

  (2,1),--CRIAR
  (2,2),--EDITAR
  (2,3),--CARREGAR
  (2,4),--INATIVAR -- PROFESSOR
  (2,5),--ATIVAR
  (2,8),--CARREGAR_ATIVO
  (2,9),--ASSOCIAR
  (2,10),--CARREGAR_POR_ID

  (3,1),--CRIAR
  (3,2),--EDITAR
  (3,3),--CARREGAR  -- COORDENADOR
  (3,6),--EXCLUIR
  (3,9),--ASSOCIAR
  (3,10),--CARREGAR_POR_ID

  (4,1),--CRIAR
  (4,2),--EDITAR
  (4,3),--CARREGAR  --ALUNO
  (4,6),--EXCLUIR
  (4,7),--IMPORTAR
  (4,10),--CARREGAR_POR_ID

  (5,1),--CRIAR --EVENTO
  (5,3),--CARREGAR
  (5,19),--VISUALIZAR

  (6,1),--CRIAR
  (6,2),--EDITAR
  (6,3),--CARREGAR  --PERIODO
  (6,6),--EXCLUIR
  (6,10),--CARREGAR_POR_ID
  (6,17),--CARREGAR_POR_USUARIO


  (7,1),--CRIAR
  (7,2),--EDITAR
  (7,3),--CARREGAR  --FASE
  (7,4),--INATIVAR
  (7,5),--ATIVAR
  (7,8),--CARREGAR_ATIVO
  (7,10),--CARREGAR_POR_ID
  (7,15),--CARREGAR_ATIVO_POR_CURSO

  (8,1),--CRIAR
  (8,2),--EDITAR
  (8,3),--CARREGAR  --CURSO
  (8,4),--INATIVAR
  (8,5),--ATIVAR
  (8,8),--CARREGAR_ATIVO
  (8,10),--CARREGAR_POR_ID
  (8,16),--CARREGAR_POR_PERIODO
  (8,17),--CARREGAR_POR_USUARIO

  (9,1),--CRIAR
  (9,2),--EDITAR
  (9,3),--CARREGAR  --DISCIPLINA
  (9,4),--INATIVAR
  (9,5),--ATIVAR
  (9,10),--CARREGAR_POR_ID

  (10,1),--CRIAR
  (10,2),--EDITAR
  (10,3),--CARREGAR  --DATA_BLOQUEADA
  (10,6),--EXCLUIR
  (10,10),--CARREGAR_POR_ID

  (11,3),--CARREGAR  --HISTORICO

  (12,3),--CARREGAR  --CRONOGRAMA_CONTROLLER
  (12,6),--EXCLUIR

  (13,2),--EDITAR  --DIA_CRONOGRAMA_CONTROLLER
----------
  (14,11),--FORMULARIO -- PROFESSOR_CONTROLLER
  (14,18),--CARREGAR_DIA_SEMANA_DISPONIVEL            -- PROFESSOR
  (15,3),--CARREGAR  --CRONOGRAMA_CONTROLLER
------------------
  (16,3),--CARREGAR  --CRONOGRAMA_CONTROLLER  --ALUNO
  ------------------------
  (17,13),--REDEFINIR_SENHA --USUARIO_CONTROLLER -- rankingAcesso > 0
  --------------
  (18,16),--CARREGAR_POR_PERIODO  --CURSO -- COORDENADOR
  (18,17),--CARREGAR_POR_USUARIO
----------------
  (19,16),--CARREGAR_POR_PERIODO  --CURSO -- rankingAcesso > 2
------
  (20,3),--CARREGAR --DIA_SEMANA_DISPONIVEL -- rankingAcesso < 4
------
  (22,17),--CARREGAR_POR_USUARIO --PERIODO_CONTROLLER -- rankingAcesso > 2
------
  (23,3),--CARREGAR --PERIODO_CONTROLLER -- COORDENADOR
  (24,15)--CARREGAR_ATIVO_POR_CURSO --FASE_CONTROLLER
ON CONFLICT (controller_id, funcionalidade_id) DO NOTHING;

--NIVEL ACESSO
INSERT INTO nivel_acesso
  (id,nome, descricao, ranking_acesso)
VALUES
  (1,'ADMINISTRADOR', 'acesso geral', 0),
  (2,'COORDENADOR_GERAL', 'acesso geral', 1),
  (3,'COORDENADOR', 'acessso quase geral', 2),
  (4,'PROFESSOR', ' apenas visualiza', 3),
  (5,'ALUNO', 'apenas visualiza', 4)
ON CONFLICT (id) DO NOTHING;

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
  (1,20), --DIA_SEMANA_DISPONIVEL_CONTROLLER

  (2,2), --PROFESSOR_CONTROLLER
  (2,3), --COORDENADOR_CONTROLLER
  (2,4), --ALUNO_CONTROLLER
  (2,5), --EVENTO_CONTROLLER
  (2,6), --PERIODO_CONTROLLER
  (2,7), --FASE_CONTROLLER
  (2,8), --CURSO_CONTROLLER         --COORDENADOR_GERAL
  (2,9), --DISCIPLINA_CONTROLLER
  (2,10), --DATA_BLOQUEADA_CONTROLLER
  (2,11), --HISTORICO_CONTROLLER
  (2,12), --CRONOGRAMA_CONTROLLER
  (2,13), --DIA_CRONOGRAMA_CONTROLLER
  (2,17), --USUARIO_CONTROLLER
  (2,20), --DIA_SEMANA_DISPONIVEL_CONTROLLER

  (3,2), --PROFESSOR_CONTROLLER
  (3,4), --ALUNO_CONTROLLER
  (3,5), --EVENTO_CONTROLLER
  (3,9), --DISCIPLINA_CONTROLLER
  (3,10), --DATA_BLOQUEADA_CONTROLLER   --COORDENADOR
  (3,11), --HISTORICO_CONTROLLER
  (3,12), --CRONOGRAMA_CONTROLLER
  (3,13), --DIA_CRONOGRAMA_CONTROLLER
  (3,17), --USUARIO_CONTROLLER
  (3,18), --CURSO_CONTROLLER
  (3,20), --DIA_SEMANA_DISPONIVEL_CONTROLLER
  (3,23), --PERIODO_CONTROLLER
  (3,24), --FASE_CONTROLLER

  (4,14), --PROFESSOR_CONTROLLER
  (4,15), --CRONOGRAMA_CONTROLLER       --PROFESSOR
  (4,17), --USUARIO_CONTROLLER
  (4,19), --CURSO_CONTROLLER
  (4,20), --DIA_SEMANA_DISPONIVEL_CONTROLLER
  (4,22), --PERIODO_CONTROLLER


  (5,16), --CRONOGRAMA_CONTROLLER
  (5,17), --USUARIO_CONTROLLER      --ALUNOS
  (5,19), --CURSO_CONTROLLER
  (5,21), --PROFESSOR_CONTROLLER
  (5,22) --PERIODO_CONTROLLER
ON CONFLICT (nivel_acesso_id, controller_id) DO NOTHING;

-- USUÁRIO
INSERT INTO usuario
  (id,email ,cpf, senha, nome)
VALUES
    (1,'gustavoalbonico@hotmail.com.br','59178627966', '$2a$10$ymh2UwJFfh10/FPTQkcRS.Vk3TB1mGVz4wMfCNe1WaZ1SJVuJPKIK', 'Administrador'),-- ADMIN
    (2,'coordenadormoda.valga@gmail.com','12385484901', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Moda coordenador'), --2 -- COORD

    (3,'gabriel.valga@gmail.com', '12345678901', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Gabriel Valga'), --3
    (4,'marina.casagrande@gmail.com', '23456789012', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Marina Casagrande'), --4
    (5,'endy.carlos@gmail.com', '34567890123', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Endy Carlos'), --5
    (6,'debora.volpato@gmail.com', '45678901234', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Débora Volpato'), --6
    (7,'josiane.minato@gmail.com', '56789012345', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Josiane Minato'), --7
    (8,'lavinia.maccari@gmail.com', '67890123456', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Lavinia Maccari'), --8
    (9,'fabiano.reis@gmail.com', '78901234567', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Fabiano Reis'), --9
    (10,'maria.matias@gmail.com', '89012345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Maria Matias'), --10
    (11,'polyane.reis@gmail.com', '90123456789', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Polyane Reis'), --11
    (12,'ellen.fabrini@gmail.com', '01234567890', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Ellen Fabrini'), --12
    (13,'eduardo.ribeiro@gmail.com', '12345566000', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Eduardo Ribeiro'), --13
    (14,'katiane.araujo@gmail.com', '01234501010', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Katiane Araújo'), --14
    (15,'josilene.della@gmail.com', '01010167890', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Josilene Della'), --15
    (16,'dayana.ricken@gmail.com', '23456789013', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Dayana Ricken'), --16
    (17,'fernando.gabriel@gmail.com', '34567890124', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Fernando Gabriel'), --17
    (18,'marcelo.mazon@gmail.com', '45678901235', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Marcelo Mazon'), --18
    (19,'christine.vieira@gmail.com', '56789012346', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Christine Vieira'), --19
    (20,'jossuan@gmail.com', '67890123457', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Jossuan Diniz'), --20
    (21,'daniel.goulart@gmail.com', '30067890123', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Daniel Goulart'), --21
    (22,'rogerio.cortina@gmail.com', '45008901234', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Rogério Cortina'), --22

    (23,'muriel.benhardt@gmail.com', '56009012345', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Muriel Bernhardt'), --23 --COORDE PROF

    (24,'roberto.medeiros@gmail.com', '67012012056', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Roberto Fermino Medeiros'), --24
    (25,'jorge.henrique.silva@gmail.com', '67007799056', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Jorge Henrique da Silva Naspolini'), --25
    (26,'roni.edson@gmail.com', '00012012056', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Roni Edson dos Santos'), --26
    (27,'fabricio.souza@gmail.com', '11012012056', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Fabricio de Souza Claudino Junior'), --27
    (28,'liliane.fernandes@gmail.com', '67011012056', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Liliane Fernandes'), --28
    (29,'cledemilson.santos@gmail.com', '62222012056', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Cledemilson dos Santos'), --29
    (30,'lucas.bonfanteprofessor@gmail.com', '67012014456', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Lucas Bonfante Rebelo'), --30

    (31,'felipe.soares@gmail.com', '88012345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Felipe Soares'), --31
    (32,'ana.clara.lima@gmail.com', '99012345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Ana Clara Lima'), --32
    (33,'rodrigo.araujo@gmail.com', '11112345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Rodrigo Araujo'), --33
    (34,'juliana.mendes@gmail.com', '22212345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Juliana Mendes'), --34
    (35,'beatriz.souza@gmail.com', '33312345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Beatriz Souza'), --35
    (36,'caio.pereira@gmail.com', '44412345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Caio Pereira'), --36  --ALUNO
    (37,'luana.martins@gmail.com', '55512345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Luana Martins'), --37
    (38,'mateus.silva@gmail.com', '66612345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Mateus Silva'), --38
    (39,'isabella.farias@gmail.com', '77712345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'Isabella Farias'), --39
    (40,'joao.pedro.ribeiro@gmail.com', '88812345678', '$2a$10$eEu4GznDM4NL/.y7siGO2eJMEi8CKbzPasF/J.IfK2MjvZbp1EMuu', 'João Pedro Ribeiro') --40
ON CONFLICT (id) DO NOTHING;

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

  (3,23),
  (4,23), --MURIEL

  (4,24),
  (4,25),
  (4,26),
  (4,27),
  (4,28),
  (4,29),
  (4,30),

  (5,31),
  (5,32),
  (5,33),
  (5,34),
  (5,35),
  (5,36),
  (5,37),
  (5,38),
  (5,39),
  (5,40)
ON CONFLICT (nivel_acesso_id, usuario_id) DO NOTHING;

 --PROFESSOR
INSERT INTO professor
 (id,telefone, usuario_id)
VALUES
    (1,'48996212844', 3), --3
    (2,'48996212954', 4), --4
    (3,'48996212844', 5), --5
    (4,'48996212954', 6), --6
    (5,'48996212954', 7), --7
    (6,'48997554040', 8), --8
    (7,'48996508090', 9), --9
    (8,'45995709000', 10), --10
    (9,'45995709001', 11), --11
    (10,'44994700610', 12), --12
    (11,'44994700611', 13), --13
    (12,'44994700610', 14), --14
    (13,'44994700610', 15), --15
    (14,'44994700612', 16), --16
    (15,'44994700613', 17), --17
    (16,'44994700614', 18), --18
    (17,'44994700615', 19), --19
    (18,'44994700616', 20), --20
    (19,'44994700613', 21), --21
    (20,'44994700614', 22), --22
    (21,'44994700615', 23), --23
    (22,'44994700616', 24), --24
    (23,'44994700616', 25), --25
    (24,'44994700616', 26), --26
    (25,'44994700616', 27), --27
    (26,'44994700616', 28), --28
    (27,'44994700616', 29), --29
    (28,'44994700616', 30) --30
ON CONFLICT (id) DO NOTHING;

INSERT INTO  dia_semana_disponivel
  (id,dia_semana_enum)
VALUES
  (1,'SEGUNDA_FEIRA'),
  (2,'TERCA_FEIRA'),
  (3,'QUARTA_FEIRA'),
  (4,'QUINTA_FEIRA'),
  (5,'SEXTA_FEIRA'),
  (6,'SABADO')
ON CONFLICT (id) DO NOTHING;

INSERT INTO
  professor_dia_semana_disponivel
  (dia_semana_disponivel_id, professor_id)
VALUES
--moda
  (1, 1),  -- SEGUNDA_FEIRA
  (2, 1),  -- TERCA_FEIRA
  (4, 1),  -- QUINTA_FEIRA --Gabriel Valga
  (6, 1),  -- SABADO

  (1, 2),  -- SEGUNDA_FEIRA --Marina Casagrande

  (2, 3),  -- TERCA_FEIRA
  (4, 3),  -- QUINTA_FEIRA --Endy Carlos
  (5, 3),  -- SEXTA_FEIRA

  (3, 4),  -- QUARTA_FEIRA --Débora Volpato

  (4, 5),  -- QUINTA_FEIRA
  (5, 5),  -- SEXTA_FEIRA --Josiane Minato

  (2, 6),  -- TERCA_FEIRA --Lavinia Maccari
  (4, 6),  -- QUINTA_FEIRA

  (1, 7),  -- SEGUNDA_FEIRA
  (5, 7),  -- SEXTA_FEIRA --Fabiano Reis
  (3, 7),  -- QUARTA_FEIRA --dataex

  (2, 8),  -- TERCA_FEIRA --Maria Matias
  (3, 8),  -- QUARTA_FEIRA --dataex

  (3, 9),  -- QUARTA_FEIRA --Polyane Reis

  (2, 10), -- TERCA_FEIRA
  (4, 10), -- QUINTA_FEIRA --Ellen Fabrini
  (6, 10), -- SABADO  --dataex

  (5, 11), -- SEXTA_FEIRA --Eduardo Ribeiro
  (4, 11), -- QUINTA_FEIRA --dataex

  (3, 12), -- QUARTA_FEIRA --Katiane Araújo
  (6, 12), -- SABADO --dataex

  (5, 13), -- SEXTA_FEIRA --Josilene Della
  (6, 13), -- SABADO --dataex

    --VARIOS CURSOS
  (1, 14), -- SEGUNDA_FEIRA
  (2, 14), -- TERCA_FEIRA --Dayana Ricken
  (4, 14), -- QUINTA_FEIRA
  (6, 14), -- SABADO

  --ADS

  (1, 15), -- SEGUNDA_FEIRA  --Fernando Gabriel
  (3, 15), -- QUARTA_FEIRA
  (4, 15), -- QUINTA_FEIRA --dataex

  (2, 16), -- TERCA_FEIRA
  (1, 16), -- SEGUNDA_FEIRA --Marcelo Mazon
  (5, 16), -- SEXTA_FEIRA
  (6, 16), -- SABADO

  (3, 17), -- QUARTA_FEIRA
  (5, 17), -- SEXTA_FEIRA --Christine Vieira
  (4, 17), -- QUINTA_FEIRA --dataex

  (1, 18), -- SEGUNDA_FEIRA --Jossuan Diniz
  (4, 18), -- QUINTA_FEIRA

  (1, 19), -- SEGUNDA_FEIRA --Daniel Goulart
  (2, 19), -- TERCA_FEIRA

  (1, 20), -- SEGUNDA_FEIRA --Rogério Cortina
  (2, 20), -- TERCA_FEIRA

  (3, 21), -- QUARTA_FEIRA
  (4, 21), -- QUINTA_FEIRA --Muriel Bernhardt
  (5, 21), -- SEXTA_FEIRA

  (2, 22), -- TERCA_FEIRA
  (3, 22), -- QUARTA_FEIRA --Roberto Fermino Medeiros
  (4, 22), -- QUINTA_FEIRA
  (5, 22), -- SEXTA_FEIRA
  (6, 22), -- SABADO

  (3, 23), -- QUARTA_FEIRA -- Jorge Henrique da Silva Naspolini
  (5, 23), -- SEXTA_FEIRA
  (4, 23), -- QUINTA_FEIRA --dataex

  (4, 24), -- QUINTA_FEIRA --Roni Edson dos Santos

  (6, 25), -- SABADO --Tales

  (1, 26), -- SEGUNDA_FEIRA  --Liliane Fernandes

  (4, 27), -- QUINTA_FEIRA  --Cledemilson dos Santos
  (5, 27), -- SEXTA_FEIRA

  (6, 28) -- SABADO --Lucas Bonfante Rebelo
ON CONFLICT (dia_semana_disponivel_id, professor_id) DO NOTHING;
--ADS

-- DATA BLOQUEADA
INSERT INTO data_bloqueada
  (id, motivo, data)
VALUES
  (1,'Feriado2','2024-12-4'),
  (2,'Feriado3','2024-11-20'),
  (3,'Feriado4','2024-11-15'),
  (4,'Feriado5','2024-10-15')
ON CONFLICT (id) DO NOTHING;

--COORDENADOR
INSERT INTO coordenador
  (id,telefone, usuario_id)
VALUES
  (1,'48595962856', 2),
  (2,'44994700615', 23)
ON CONFLICT (id) DO NOTHING;

--FASE
INSERT INTO fase
  (id,numero)
VALUES
  (1,1),
  (2,2),
  (3,3),
  (4,4),
  (5,5),
  (6,6)
ON CONFLICT (id) DO NOTHING;

--CURSO
INSERT INTO curso
  (id,nome, sigla, coordenador_id,status_enum)
VALUES
  (1,'Design de Moda','MODA', 1,'ATIVO'),
  (2,'Análise e Desenvolvimento de Sistemas','ADS', 2,'ATIVO'),
  (3,'Processos Gerencias','PG', null,'INATIVO'),
  (4,'Gestão da Tecnologia da Informação','TGTI', null,'INATIVO'),
  (5,'Gastronomia','GASTRO', null,'INATIVO')
ON CONFLICT (id) DO NOTHING;

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
  (2,5)
ON CONFLICT (curso_id, fase_id) DO NOTHING;

--DISCIPLINA
INSERT INTO disciplina
  (id,nome, carga_horaria, carga_horaria_diaria, cor_hexadecimal, extensao_boolean_enum, curso_id, fase_id, professor_id)
VALUES
--MODA
  (1,'Extensão em Design de Moda I', 50, 4, '#1E90FF', 'SIM', 1, 2, 1),
  (2,'Desenho de Moda I', 60, 4, '#006400', 'NAO', 1, 2, 2),
  (3,'História da Arte e da Moda', 60, 4, '#DAA520', 'NAO', 1, 2, 3),
  (4,'Marketing de Moda e Comportamento do Consumido', 70, 4, '#8A2BE2', 'NAO', 1, 2, 4),
  (5,'Ateliê de Confecção I - INICIO', 40, 4, '#FF1493', 'NAO', 1, 2, 5),
  (6,'Coolhunting - FINAL', 40, 4, '#F0E68C', 'NAO', 1, 2, 6),
  (7,'Técnicas de Modelagem I', 80, 4, '#F0E68C', 'NAO', 1, 2, 7),

  (8,'Materiais e Beneficiamento', 80, 4, '#99004C', 'NAO', 1, 4, 7),
  (9,'Ateliê de Confecção III', 80, 4, '#003300', 'NAO', 1, 4, 8),
  (10,'Moulage', 60, 4, '#FF6666', 'NAO', 1, 4, 9),
  (11,'Criatividade, Inovação e Sustentabilidade', 60, 4, '#003319', 'NAO', 1, 4, 10),
  (12,'Extensão em Design de Moda IV', 40, 4, '#660033', 'SIM', 1, 4, 1),
  (13,'Desenho Técnico de Moda - INICIO', 40, 4, '#00FF80', 'NAO', 1, 4, 3),
  (14,'Pesquisa de Mercado - FINAL', 40, 4, '#00FF80', 'NAO', 1, 4, 11),

  (15,'Desenvolvimento de Portfólio', 40, 4, '#3357FF', 'NAO', 1, 6, 1),
  (16,'Produção Audiovisual', 40, 4, '#FF33A6', 'NAO', 1, 6, 1),
  (17,'Desenvolvimento e Produção de Coleção - INICIO1', 40, 4, '#A633FF', 'NAO', 1, 6, 6),
  (18,'Styling - Final', 40, 4, '#FF8C33', 'NAO', 1, 6, 10),
  (19,'Desenvolvimento e Apresentação de Coleção', 80, 4, '#33FF8C', 'NAO', 1, 6, 12),
  (20,'Desenvolvimento e Produção de Coleção - INICIO2', 40, 4, '#2C1963', 'NAO', 1, 6, 3),
  (21,'Metodologia do Trabalho Científico - FINAL', 40, 4, '#FF2F00', 'NAO', 1, 6, 14),
  (22,'Desenvolvimento e Produção de Coleção1 ', 40, 4, '#234B1E', 'NAO', 1, 6, 5),
  (23,'Desenvolvimento e Produção de Coleção2 ', 40, 4, '#00FAFF', 'NAO', 1, 6, 13),

--ADS
  (24,'Fundamentos da Pesquisa', 40, 4, '#FF3333', 'NAO', 2, 1, 14),
  (25,'Introdução a Computação', 36, 4, '#33FFFF', 'NAO',2, 1, 15),
  (26,'Modelagem de Dados', 76, 4, '#FFFF33', 'NAO', 2, 1, 16),
  (27,'Introdução a Programação de Computadores', 152, 4, '#FF33FF', 'NAO', 2, 1, 17),
  (28,'Engenharia de Requisitos', 76, 4, '#33FF33', 'NAO', 2, 1, 18),
  (29,'Extensão em Análise e Desenvolvimento de Sistemas I', 45, 4, '#3399FF', 'SIM', 2, 1, 14),

  (30,'Estrutura de Dados', 40, 4, '#99004C', 'NAO', 2, 2, 16),
  (31,'UX/UI Design de Sistema', 36, 4, '#003300', 'NAO',2, 2, 19),
  (32,'Programação Orientada a Objetos', 76, 4, '#FF6666', 'NAO', 2, 2, 20),
  (33,'Análise Orientada a Objetos', 76, 4, '#003319', 'NAO', 2, 2, 21),
  (34,'Desenvolvimento Web', 76, 4, '#660033', 'NAO', 2, 2, 22),
  (35,'Sistema Gerenciador de Banco de Dados ', 76, 4, '#00FF80', 'NAO', 2, 2, 16),
  (36,'Extensão em Análise e Desenvolvimento de Sistemas II', 45, 4, '#00FF80', 'SIM', 2, 2, 16),

  (37,'Arquitetura de Software', 36, 4, '#99004C', 'NAO', 2, 3, 20),
  (38,'Tecnologias e Sistemas de Informação Gerencial', 40, 4, '#003300', 'NAO',2, 3, 16),
  (39,'Desenvolvimento Back End', 76, 4, '#FF6666', 'NAO', 2, 3, 22),
  (40,'Cloud & ITOps', 76, 4, '#003319', 'NAO', 2, 3, 15),
  (41,'Qualidade e testes de software', 76, 4, '#660033', 'NAO', 2, 3, 21),
  (42,'Desenvolvimento Front End', 76, 4, '#00FF80', 'NAO', 2, 3, 23),
  (43,'Extensão em Análise e Desenvolvimento de Sistemas III', 45, 4, '#00FF80', 'SIM', 2, 3, 22),

  (44,'Desenvolvimento para sistemas embarcados', 40, 4, '#99004C', 'NAO', 2, 4, 15),
  (45,'Gerenciamento de projetos de software', 36, 4, '#003300', 'NAO',2, 4, 18),
  (46,'Engenharia de software', 76, 4, '#FF6666', 'NAO', 2, 4, 19),
  (47,'Desenvolvimento Full Stack', 76, 4, '#003319', 'NAO', 2, 4, 23),
  (48,'Codificação segura', 76, 4, '#660033', 'NAO', 2, 4, 24),
  (49,'Desenvolvimento Full Stack', 76, 4, '#00FF80', 'NAO', 2, 4, 22),
  (50,'Extensão em Análise e Desenvolvimento de Sistemas IIII', 45, 4, '#00FF80', 'SIM', 2, 4, 25),

  (51,'Tópicos Especiais em ADS', 76, 4, '#9933FF', 'NAO', 2, 5, 26),
  (52,'Desenvolvimento para dispositivos móveis', 76, 4, '#FF3399', 'NAO',2, 5, null),
  (53,'Metodologia do Trabalho Científico', 40, 4, '#99FF33', 'NAO', 2, 5, 14),
  (54,'Desenvolvimento de aplicação (Sistema)', 76, 4, '#FF6633', 'NAO', 2, 5, 22),
  (55,'Legislação Aplicada a Informação', 36, 4, '#6633FF', 'NAO', 2, 5, 27),
  (56,'Desenvolvimento de Aplicação (Projeto)', 40, 4, '#33FF66', 'NAO', 2, 5, 21),
  (57,'Certificações em ADS', 36, 4, '#FF3366', 'NAO', 2, 5, 27),
  (58,'InovADS (integradora)', 20, 4, '#66FF33', 'NAO', 2, 5, 28)
ON CONFLICT (id) DO NOTHING;

-- PERIODO
INSERT INTO  periodo
  (id,nome,data_inicial, data_final)
VALUES
  (1,'2º Semestre', '2024-07-29', '2024-12-13')
ON CONFLICT (id) DO NOTHING;

--ALUNO
INSERT INTO aluno
  (id, usuario_id, curso_id)
VALUES
  (1, 31, 1), -- Felipe Soares
  (2, 32, 1), -- Ana Clara Lima
  (3, 33, 1), -- Rodrigo Araujo --MODA
  (4, 34, 1), -- Juliana Mendes
  (5, 35, 1), -- Beatriz Souza

  (6, 36, 2), -- Caio Pereira
  (7, 37, 2), -- Luana Martins
  (8, 38, 2), -- Mateus Silva -- ADS
  (9, 39, 2), -- Isabella Farias
  (10, 40, 2) -- João Pedro Ribeiro
ON CONFLICT (id) DO NOTHING;

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
  (10,3)
ON CONFLICT (aluno_id, fase_id) DO NOTHING;


--ATUALIZANDO A SEQUENCIA DAS PK

SELECT setval('funcionalidade_id_seq', COALESCE((SELECT MAX(id) FROM funcionalidade), 1), true);
SELECT setval('controller_id_seq', COALESCE((SELECT MAX(id) FROM controller), 1), true);
SELECT setval('nivel_acesso_id_seq', COALESCE((SELECT MAX(id) FROM nivel_acesso), 1), true);
SELECT setval('usuario_id_seq', COALESCE((SELECT MAX(id) FROM usuario), 1), true);
SELECT setval('professor_id_seq', COALESCE((SELECT MAX(id) FROM professor), 1), true);
SELECT setval('dia_semana_disponivel_id_seq', COALESCE((SELECT MAX(id) FROM dia_semana_disponivel), 1), true);
SELECT setval('data_bloqueada_id_seq', COALESCE((SELECT MAX(id) FROM data_bloqueada), 1), true);
SELECT setval('coordenador_id_seq', COALESCE((SELECT MAX(id) FROM coordenador), 1), true);
SELECT setval('fase_id_seq', COALESCE((SELECT MAX(id) FROM fase), 1), true);
SELECT setval('curso_id_seq', COALESCE((SELECT MAX(id) FROM curso), 1), true);
SELECT setval('disciplina_id_seq', COALESCE((SELECT MAX(id) FROM disciplina), 1), true);
SELECT setval('periodo_id_seq', COALESCE((SELECT MAX(id) FROM periodo), 1), true);
SELECT setval('aluno_id_seq', COALESCE((SELECT MAX(id) FROM aluno), 1), true);
