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





--
--
----INSERTS
--
--FUNCIONALIDADE
INSERT INTO funcionalidade
  (nome, descricao)
VALUES
  ('CRIAR', 'Adiciona uma entidade'),
  ('EDITAR', 'Altera informações'),
  ('CARREGAR', 'Busca informações'),
  ('DELETAR', 'Exclui uma informação do banco de dados'),
  ('IMPORTAR', 'importa um arquivo para dentro do sistema'),
  ('CARREGAR_POR_ID', 'carregar um informação pelo id');

--CONTROLLER
INSERT INTO controller
  (nome)
VALUES
  ('FASE_CONTROLLER'),
  ('USUARIO_CONTROLLER'),
  ('CURSO_CONTROLLER'),
  ('DISCIPLINA_CONTROLLER'),
  ('DATA_BLOQUEADA_CONTROLLER');

--CONTROLLER_FUNCIONALIDADE
INSERT INTO  controller_funcionalidade
  (controller_id, funcionalidade_id)
VALUES
  (1,1),
  (1,2),
  (1,3),
  (1,4),
  (2,1),
  (2,2),
  (2,3),
  (2,4),
  (2,5);

--NIVEL ACESSO
INSERT INTO nivel_acesso
  (nome, descricao)
VALUES
  ('ADMINISTRADOR', 'acesso geral'),
  ('COORDENADOR_GERAL', 'acesso geral'),
  ('COORDENADOR', 'acessso quase geral'),
  ('PROFESSOR', ' apenas visualiza'),
  ('USUARIO', 'apenas visualiza');

INSERT INTO nivel_acesso_controller
  (nivel_acesso_id, controller_id)
VALUES
  (1,1),
  (1,2),
  (2,1),
  (2,2);

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
    ('roberto.medeiros@example.com', 'senha123', 'PROFESSOR'),--24


    ('jorge.henrique.silva@example.com', 'senha123', 'PROFESSOR'),
    ('roni.edson@example.com', 'senha123', 'PROFESSOR'),
    ('bruno.kurzawe@example.com', 'senha123', 'PROFESSOR'),
    ('liliane.fernandes@example.com', 'senha123', 'PROFESSOR'),
    ('cledemilson.santos@example.com', 'senha123', 'PROFESSOR'),
    ('lucas.bonfanteprofessor@example.com', 'senha123', 'PROFESSOR');--30

----PROFESSOR
--INSERT INTO professor
--  (cpf,nome_completo, telefone, usuario_id)
--VALUES
----MODA
--  ('12345678901', 'Gabriel Valga', '48996212844', 3),
--  ('23456789012', 'Marina Casagrande', '48996212954', 4),
--  ('34567890123', 'Endy Carlos', '48996212844', 5),
--  ('45678901234', 'Débora Volpato', '48996212954', 6),
--  ('56789012345', 'Josiane Minato', '48996212954', 7),
--  ('67890123456', 'Lavinia Maccari', '48997554040', 8),
--  ('78901234567', 'Fabiano Reis', '48996508090', 9),
--
--  ('89012345678', 'Maria Matias', '45995709000', 10),
--  ('90123456789', 'Polyane Reis', '45995709001', 11),
--  ('01234567890', 'Ellen Fabrini', '44994700610', 12),
--  ('12345566000', 'Eduardo Ribeiro', '44994700611', 13), -- 11
--
--  ('01234501010', 'Katiane Araújo', '44994700610', 14),
--  ('01010167890', 'Josilene Della', '44994700610', 15), --13
--
----ADS
--  ('23456789012', 'Dayana Ricken', '44994700612', 16),
--  ('34567890123', 'Fernando Gabriel', '44994700613', 17),
--  ('45678901234', 'Marcelo Mazon', '44994700614', 18),
--  ('56789012345', 'Christine Vieira', '44994700615', 19),
--  ('67890123456', 'Jossuan Diniz', '44994700616', 20), -- 18
--
--  ('30067890123', 'Daniel Goulart', '44994700613', 21),
--  ('45008901234', 'Rogério Cortina', '44994700614', 22),
--  ('56009012345', 'Muriel Bernhardt', '44994700615', 23),
--  ('67012012056', 'Roberto Fermino Medeiros', '44994700616', 24), --22
--
--  ('67007799056', 'Jorge Henrique da Silva Naspolini', '44994700616', 25),
--  ('00012012056', 'Roni Edson dos Santos', '44994700616', 26),
--  ('11012012056', 'Bruno Kurzawe', '44994700616', 27),
--  ('67011012056', 'Liliane Fernandes', '44994700616', 28),
--  ('62222012056', 'Cledemilson dos Santos', '44994700616', 29),
--  ('67012014456', 'Lucas Bonfante Rebelo', '44994700616', 30);-- 28
--
---- DIA DA SEMANA DISPONIVEL
--INSERT INTO dia_semana_disponivel
--  (dia_semana_enum, professor_id)
--VALUES
----moda
--   ('SEGUNDA_FEIRA', 1),
--   ('TERCA_FEIRA', 1),
--   ('QUINTA_FEIRA', 1), --Gabriel Valga
--   ('SABADO', 1),
--
--  ('SEGUNDA_FEIRA', 2), --Marina Casagrande
--
--  ('TERCA_FEIRA', 3),
--  ('QUINTA_FEIRA', 3), --Endy Carlos
--  ('SEXTA_FEIRA', 3),
--
--  ('QUARTA_FEIRA', 4), --Débora Volpato
--
--  ('QUINTA_FEIRA', 5), --Josiane Minato
--  ('SEXTA_FEIRA', 5),
--
--  ('TERCA_FEIRA', 6), --Lavinia Maccari
--  ('QUINTA_FEIRA', 6),
--
--  ('SEGUNDA_FEIRA', 7), --Fabiano Reis
--  ('SEXTA_FEIRA', 7),
--  ('QUARTA_FEIRA', 7),--dataex
--
--  ('TERCA_FEIRA', 8), --Maria Matias
--  ('QUARTA_FEIRA', 8), --dataex
--
--  ('QUARTA_FEIRA', 9), --Polyane Reis
--
--  ('TERCA_FEIRA', 10),
--  ('QUINTA_FEIRA', 10), --Ellen Fabrini
--  ('SABADO', 10), --dataex
--
--  ('SEXTA_FEIRA', 11), --Eduardo Ribeiro
--  ('QUINTA_FEIRA', 11), --dataex
--
--  ('QUARTA_FEIRA', 12), --Katiane Araújo
--  ('SABADO', 12), --dataex
--
--  ('SEXTA_FEIRA', 13), --Josilene Della
--  ('SABADO', 13), --dataex
--
--    --VARIOS CURSOS
--  ('SEGUNDA_FEIRA', 14),
--  ('TERCA_FEIRA', 14),--Dayana Ricken
--  ('QUINTA_FEIRA', 14),
--  ('SABADO', 14),
--
--   --ADS
--
--  ('SEGUNDA_FEIRA', 15), --Fernando Gabriel
--  ('QUARTA_FEIRA', 15),
--  ('QUINTA_FEIRA', 15),--dataex
--
--  ('TERCA_FEIRA', 16),
--  ('SEGUNDA_FEIRA', 16), --Marcelo Mazon
--  ('SEXTA_FEIRA', 16),
--  ('SABADO', 16),
--
--  ('QUARTA_FEIRA', 17),--Christine Vieira
--  ('SEXTA_FEIRA', 17),
--  ('QUINTA_FEIRA', 17), --dataex
--
--  ('SEGUNDA_FEIRA', 18),--Jossuan Diniz
--  ('QUINTA_FEIRA', 18),
--
--  ('SEGUNDA_FEIRA', 19),--Daniel Goulart
--  ('TERCA_FEIRA', 19),
--
--  ('SEGUNDA_FEIRA', 20),--Rogério Cortina
--  ('TERCA_FEIRA', 20),
--
--  ('QUARTA_FEIRA', 21),
--  ('QUINTA_FEIRA', 21), --Muriel Bernhardt
--  ('SEXTA_FEIRA', 21),
--
--  ('TERCA_FEIRA', 22),
--  ('QUARTA_FEIRA', 22),
--  ('QUINTA_FEIRA',22), --Roberto Fermino Medeiros
--  ('SEXTA_FEIRA', 22),
--  ('SABADO',22),
--
--  ('QUARTA_FEIRA', 23),-- Jorge Henrique da Silva Naspolini
--  ('SEXTA_FEIRA', 23),
--  ('QUINTA_FEIRA', 23),--dataex
--
--  ('QUINTA_FEIRA', 24),--Roni Edson dos Santos
--
--  ('SABADO', 25), --Bruno Kurzawe
--
--  ('SEGUNDA_FEIRA', 26), --Liliane Fernandes
--
--  ('QUINTA_FEIRA', 27), --Cledemilson dos Santos
--  ('SEXTA_FEIRA', 27),
--
--  ('SABADO', 28);--Lucas Bonfante Rebelo
--
----ADS
--
---- DATA BLOQUEADA
--INSERT INTO data_bloqueada
--  (motivo, data, usuario_id)
--VALUES
--  ('Feriado2','2024-12-4', 2),
--  ('Feriado3','2024-11-20', 2),
--  ('Feriado4','2024-11-15', 2),
--  ('Feriado5','2024-10-15', 2);
--
----COORDENADOR
--INSERT INTO coordenador
--  (nome_completo, cpf, telefone, usuario_id)
--VALUES
--  ('MODA', '15264859523', '48595962856', 2),
--  ('Lucas', '21524859523', '48595962856', 20);
--
----FASE
--INSERT INTO fase
--  (numero)
--VALUES
--  (1),
--  (2),
--  (3),
--  (4),
--  (5),
--  (6);
--
----CURSO
--INSERT INTO curso
--  (nome, sigla, coordenador_id)
--VALUES
--  ('Moda','MODA', 1),
--  ('Analise e Desenvolvimento de Sistemas','ADS', 2);
--
----CURSO_FASE
--INSERT INTO curso_fase
--  (curso_id, fase_id)
--VALUES
----MODA
--  (1,2),
--  (1,4),
--  (1,6),
--
----ADS
--  (2,1),
--  (2,2),
--  (2,3),
--  (2,4),
--  (2,5);
--
----DISCIPLINA
--INSERT INTO disciplina
--  (nome, carga_horaria, carga_horaria_diaria, cor_hexadecimal, extensao_boolean_enum, curso_id, fase_id, professor_id)
--VALUES
----MODA
--  ('Extensão em Design de Moda I', 50, 4, '#1E90FF', 'SIM', 1, 2, 1),
--  ('Desenho de Moda I', 60, 4, '#006400', 'NAO', 1, 2, 2),
--  ('História da Arte e da Moda', 60, 4, '#DAA520', 'NAO', 1, 2, 3),
--  ('Marketing de Moda e Comportamento do Consumido', 70, 4, '#8A2BE2', 'NAO', 1, 2, 4),
--  ('Ateliê de Confecção I - INICIO', 40, 4, '#FF1493', 'NAO', 1, 2, 5),
--  ('Coolhunting - FINAL', 40, 4, '#F0E68C', 'NAO', 1, 2, 6),
--  ('Técnicas de Modelagem I', 80, 4, '#F0E68C', 'NAO', 1, 2, 7),
--
--  ('Materiais e Beneficiamento', 80, 4, '#99004C', 'NAO', 1, 4, 7),
--  ('Ateliê de Confecção III', 80, 4, '#003300', 'NAO', 1, 4, 8),
--  ('Moulage', 60, 4, '#FF6666', 'NAO', 1, 4, 9),
--  ('Criatividade, Inovação e Sustentabilidade', 60, 4, '#003319', 'NAO', 1, 4, 10),
--  ('Extensão em Design de Moda IV', 40, 4, '#660033', 'SIM', 1, 4, 1),
--  ('Desenho Técnico de Moda - INICIO', 40, 4, '#00FF80', 'NAO', 1, 4, 3),
--  ('Pesquisa de Mercado - FINAL', 40, 4, '#00FF80', 'NAO', 1, 4, 11),
--
--  ('Desenvolvimento de Portfólio', 40, 4, '#FF5733', 'NAO', 1, 6, 1),
--  ('Produção Audiovisual', 40, 4, '#33FF57', 'NAO', 1, 6, 1),
--  ('Desenvolvimento e Produção de Coleção - INICIO1', 40, 4, '#3357FF', 'NAO', 1, 6, 6),
--  ('Styling - Final', 40, 4, '#FF33A6', 'NAO', 1, 6, 10),
--  ('Desenvolvimento e Apresentação de Coleção', 80, 4, '#A633FF', 'NAO', 1, 6, 12),
--  ('Desenvolvimento e Produção de Coleção - INICIO2', 40, 4, '#33FFA6', 'NAO', 1, 6, 3),
--  ('Metodologia do Trabalho Científico - FINAL', 40, 4, '#FF8C33', 'NAO', 1, 6, 14),
--  ('Desenvolvimento e Produção de Coleção1 ', 40, 4, '#8C33FF', 'NAO', 1, 6, 5),
--  ('Desenvolvimento e Produção de Coleção2 ', 40, 4, '#33FF8C', 'NAO', 1, 6, 13),
--
----ADS
--  ('Fundamentos da Pesquisa', 40, 4, '#FF3333', 'NAO', 2, 1, 14),
--  ('Introdução a Computação', 36, 4, '#33FFFF', 'NAO',2, 1, 15),
--  ('Modelagem de Dados', 76, 4, '#FFFF33', 'NAO', 2, 1, 16),
--  ('Introdução a Programação de Computadores', 152, 4, '#FF33FF', 'NAO', 2, 1, 17),
--  ('Engenharia de Requisitos', 76, 4, '#33FF33', 'NAO', 2, 1, 18),
--  ('Extensão em Análise e Desenvolvimento de Sistemas I', 45, 4, '#3399FF', 'SIM', 2, 1, 14),
--
--  ('Estrutura de Dados', 40, 4, '#99004C', 'NAO', 2, 2, 16),
--  ('UX/UI Design de Sistema', 36, 4, '#003300', 'NAO',2, 2, 19),
--  ('Programação Orientada a Objetos', 76, 4, '#FF6666', 'NAO', 2, 2, 20),
--  ('Análise Orientada a Objetos', 76, 4, '#003319', 'NAO', 2, 2, 21),
--  ('Desenvolvimento Web', 76, 4, '#660033', 'NAO', 2, 2, 22),
--  ('Sistema Gerenciador de Banco de Dados ', 76, 4, '#00FF80', 'NAO', 2, 2, 16),
--  ('Extensão em Análise e Desenvolvimento de Sistemas II', 45, 4, '#00FF80', 'SIM', 2, 2, 16),
--
--  ('Arquitetura de Software', 36, 4, '#99004C', 'NAO', 2, 3, 20),
--  ('Tecnologias e Sistemas de Informação Gerencial', 40, 4, '#003300', 'NAO',2, 3, 16),
--  ('Desenvolvimento Back End', 76, 4, '#FF6666', 'NAO', 2, 3, 22),
--  ('Cloud & ITOps', 76, 4, '#003319', 'NAO', 2, 3, 15),
--  ('Qualidade e testes de software', 76, 4, '#660033', 'NAO', 2, 3, 21),
--  ('Desenvolvimento Front End', 76, 4, '#00FF80', 'NAO', 2, 3, 23),
--  ('Extensão em Análise e Desenvolvimento de Sistemas III', 45, 4, '#00FF80', 'SIM', 2, 3, 22),
--
--  ('Desenvolvimento para sistemas embarcados', 40, 4, '#99004C', 'NAO', 2, 4, 15),
--  ('Gerenciamento de projetos de software', 36, 4, '#003300', 'NAO',2, 4, 18),
--  ('Engenharia de software', 76, 4, '#FF6666', 'NAO', 2, 4, 19),
--  ('Desenvolvimento Full Stack', 76, 4, '#003319', 'NAO', 2, 4, 23),
--  ('Codificação segura', 76, 4, '#660033', 'NAO', 2, 4, 24),
--  ('Desenvolvimento Full Stack', 76, 4, '#00FF80', 'NAO', 2, 4, 22),
--  ('Extensão em Análise e Desenvolvimento de Sistemas IIII', 45, 4, '#00FF80', 'SIM', 2, 4, 25),
--
--  ('Tópicos Especiais em ADS', 76, 4, '#9933FF', 'NAO', 2, 5, 26),
--  ('Desenvolvimento para dispositivos móveis', 76, 4, '#FF3399', 'NAO',2, 5, null),
--  ('Metodologia do Trabalho Científico', 40, 4, '#99FF33', 'NAO', 2, 5, 14),
--  ('Desenvolvimento de aplicação (Sistema)', 76, 4, '#FF6633', 'NAO', 2, 5, 22),
--  ('Legislação Aplicada a Informação', 36, 4, '#6633FF', 'NAO', 2, 5, 27),
--  ('Desenvolvimento de Aplicação (Projeto)', 40, 4, '#33FF66', 'NAO', 2, 5, 21),
--  ('Certificações em ADS', 36, 4, '#FF3366', 'NAO', 2, 5, 27),
--  ('InovADS (integradora)', 20, 4, '#66FF33', 'NAO', 2, 5, 28);
--
---- PERIODO
--INSERT INTO  periodo
--  (data_inicial, data_final)
--VALUES
--  ('2024-07-29', '2024-12-13');