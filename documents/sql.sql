CREATE TABLE Usuario (
    usuario_id INTEGER PRIMARY KEY,
    nome VARCHAR2(100) NOT NULL,
    email VARCHAR2(100) UNIQUE NOT NULL,
    senha_hash VARCHAR2(255) NOT NULL,
    data_cadastro DATE DEFAULT SYSDATE
);

CREATE TABLE Dispositivo (
    dispositivo_id INTEGER PRIMARY KEY,
    nome_dispositivo VARCHAR2(100) NOT NULL,
    potencia_watts NUMBER(5,2) NOT NULL,
    horas_uso_diario NUMBER(4,2) NOT NULL,
    usuario_id INTEGER NOT NULL,
    CONSTRAINT fk_usuario_dispositivo FOREIGN KEY (usuario_id) REFERENCES Usuario(usuario_id)
);

CREATE TABLE Consumo (
    consumo_id INTEGER PRIMARY KEY,
    data_registro DATE DEFAULT SYSDATE,
    consumo_mensal_kwh NUMBER(6,2) NOT NULL,
    dispositivo_id INTEGER NOT NULL,
    CONSTRAINT fk_dispositivo_consumo FOREIGN KEY (dispositivo_id) REFERENCES Dispositivo(dispositivo_id)
);

CREATE TABLE DicaEconomia (
    dica_id INTEGER PRIMARY KEY,
    texto_dica CLOB NOT NULL,
    categoria VARCHAR2(50),
    relevancia VARCHAR2(20)
);

CREATE TABLE Auditoria (
    audit_id INTEGER PRIMARY KEY,
    tabela VARCHAR2(50) NOT NULL,
    operacao VARCHAR2(10) NOT NULL,
    data TIMESTAMP DEFAULT SYSTIMESTAMP,
    usuario_id INTEGER,
    descricao CLOB
);

CREATE INDEX idx_usuario_email ON Usuario (email);
CREATE INDEX idx_dispositivo_usuario ON Dispositivo (usuario_id);
CREATE INDEX idx_consumo_dispositivo ON Consumo (dispositivo_id);

-- Sequences
CREATE SEQUENCE AUDIT_SEQ
START WITH 1         
INCREMENT BY 1       
NOCACHE;             

CREATE SEQUENCE SEQ_USUARIO START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_DISPOSITIVO START WITH 1000 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE SEQ_CONSUMO START WITH 1000 INCREMENT BY 1 NOCACHE; 
CREATE SEQUENCE SEQ_DICA_ECONOMIA START WITH 1000 INCREMENT BY 1 NOCACHE; 
CREATE SEQUENCE SEQ_AUDITORIA START WITH 1000 INCREMENT BY 1 NOCACHE; 

-- Trigger para Auditoria: Usuario
CREATE OR REPLACE TRIGGER trg_audit_usuario
AFTER INSERT OR UPDATE OR DELETE ON Usuario
FOR EACH ROW
DECLARE
    v_operacao VARCHAR2(10);
BEGIN
    IF INSERTING THEN
        v_operacao := 'INSERT';
    ELSIF UPDATING THEN
        v_operacao := 'UPDATE';
    ELSIF DELETING THEN
        v_operacao := 'DELETE';
    END IF;

    INSERT INTO Auditoria (audit_id, tabela, operacao, data, usuario_id, descricao)
    VALUES (
        AUDIT_SEQ.NEXTVAL,                
        'Usuario',                        
        v_operacao,                       
        SYSTIMESTAMP,                     
        NVL(:NEW.usuario_id, :OLD.usuario_id),  
        'Alteração na tabela Usuario'    
    );
END;
/

-- Trigger para Auditoria: Dispositivo
CREATE OR REPLACE TRIGGER trg_audit_dispositivo
AFTER INSERT OR UPDATE OR DELETE ON Dispositivo
FOR EACH ROW
DECLARE
    v_operacao VARCHAR2(10); 
BEGIN
    IF INSERTING THEN
        v_operacao := 'INSERT';
    ELSIF UPDATING THEN
        v_operacao := 'UPDATE';
    ELSIF DELETING THEN
        v_operacao := 'DELETE';
    END IF;

    INSERT INTO Auditoria (audit_id, tabela, operacao, data, usuario_id, descricao)
    VALUES (
        AUDIT_SEQ.NEXTVAL,
        'Dispositivo',
        v_operacao,
        SYSTIMESTAMP,
        NVL(:NEW.usuario_id, :OLD.usuario_id),
        'Alteração na tabela Dispositivo'
    );
END;
/

-- Trigger para Auditoria: Consumo
CREATE OR REPLACE TRIGGER trg_audit_consumo
AFTER INSERT OR UPDATE OR DELETE ON Consumo
FOR EACH ROW
DECLARE
    v_operacao VARCHAR2(10);
BEGIN
    
    IF INSERTING THEN
        v_operacao := 'INSERT';
    ELSIF UPDATING THEN
        v_operacao := 'UPDATE';
    ELSIF DELETING THEN
        v_operacao := 'DELETE';
    END IF;

    INSERT INTO Auditoria (audit_id, tabela, operacao, data, usuario_id, descricao)
    VALUES (
        AUDIT_SEQ.NEXTVAL,
        'Consumo',
        v_operacao,
        SYSTIMESTAMP,
        NULL,  
        'Alteração na tabela Consumo'
    );
END;
/

-- Trigger para Auditoria: DicaEconomia
CREATE OR REPLACE TRIGGER trg_audit_dica
AFTER INSERT OR UPDATE OR DELETE ON DicaEconomia
FOR EACH ROW
DECLARE
    v_operacao VARCHAR2(10); 
BEGIN
    IF INSERTING THEN
        v_operacao := 'INSERT';
    ELSIF UPDATING THEN
        v_operacao := 'UPDATE';
    ELSIF DELETING THEN
        v_operacao := 'DELETE';
    END IF;

    
    INSERT INTO Auditoria (audit_id, tabela, operacao, data, usuario_id, descricao)
    VALUES (
        AUDIT_SEQ.NEXTVAL,
        'DicaEconomia',
        v_operacao,
        SYSTIMESTAMP,
        NULL,  
        'Alteração na tabela DicaEconomia'
    );
END;
/

-- Criando a Procedure para Inserir em Usuario
CREATE OR REPLACE PROCEDURE sp_insert_usuario (
    p_nome IN VARCHAR2,
    p_email IN VARCHAR2,
    p_senha_hash IN VARCHAR2
) AS
BEGIN
    INSERT INTO Usuario (usuario_id, nome, email, senha_hash, data_cadastro)
    VALUES (
        AUDIT_SEQ.NEXTVAL,  
        p_nome,
        p_email,
        p_senha_hash,
        SYSDATE             
    );
END;
/

-- Criando a Procedure para Inserir em Dispositivo
CREATE OR REPLACE PROCEDURE sp_insert_dispositivo (
    p_nome_dispositivo IN VARCHAR2,
    p_potencia_watts IN NUMBER,
    p_horas_uso_diario IN NUMBER,
    p_usuario_id IN INTEGER
) AS
    v_existe_usuario INTEGER;
BEGIN
    SELECT COUNT(1) INTO v_existe_usuario FROM Usuario WHERE usuario_id = p_usuario_id;

    IF v_existe_usuario = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Usuário não encontrado.');
    END IF;

    INSERT INTO Dispositivo (dispositivo_id, nome_dispositivo, potencia_watts, horas_uso_diario, usuario_id)
    VALUES (
        AUDIT_SEQ.NEXTVAL,
        p_nome_dispositivo,
        p_potencia_watts,
        p_horas_uso_diario,
        p_usuario_id
    );
END;
/

-- Criando a Procedure para Inserir em Consumo
CREATE OR REPLACE PROCEDURE sp_insert_consumo (
    p_data_registro IN DATE,
    p_consumo_mensal_kwh IN NUMBER,
    p_dispositivo_id IN INTEGER
) AS
    v_existe_dispositivo INTEGER;
BEGIN
    
    SELECT COUNT(1) INTO v_existe_dispositivo FROM Dispositivo WHERE dispositivo_id = p_dispositivo_id;

    IF v_existe_dispositivo = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Dispositivo não encontrado.');
    END IF;

    
    INSERT INTO Consumo (consumo_id, data_registro, consumo_mensal_kwh, dispositivo_id)
    VALUES (
        AUDIT_SEQ.NEXTVAL,
        NVL(p_data_registro, SYSDATE),
        p_consumo_mensal_kwh,
        p_dispositivo_id
    );
END;
/

-- Criando a Procedure para Inserir em DicaEconomia
CREATE OR REPLACE PROCEDURE sp_insert_dica (
    p_texto_dica IN CLOB,
    p_categoria IN VARCHAR2,
    p_relevancia IN VARCHAR2
) AS
BEGIN
    INSERT INTO DicaEconomia (dica_id, texto_dica, categoria, relevancia)
    VALUES (
        AUDIT_SEQ.NEXTVAL,
        p_texto_dica,
        p_categoria,
        p_relevancia
    );
END;
/

-- Função: Cálculo do Consumo do Dispositivo (com tratamento de exceções)
CREATE OR REPLACE FUNCTION fn_calcular_consumo_dispositivo (
    p_potencia_watts NUMBER,
    p_horas_diarias NUMBER
) RETURN NUMBER AS
    v_consumo_mensal NUMBER;
BEGIN
    IF p_potencia_watts < 0 OR p_horas_diarias < 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Os valores de potência e horas diárias devem ser positivos.');
    END IF;

    v_consumo_mensal := (p_potencia_watts * p_horas_diarias * 30) / 1000;

    RETURN v_consumo_mensal;
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20002, 'Erro ao calcular o consumo do dispositivo: ' || SQLERRM);
END;
/

-- Função: Validação de Horas de Uso e Formato de Dados
CREATE OR REPLACE FUNCTION fn_validar_horas_uso (
    p_horas_diarias NUMBER
) RETURN BOOLEAN AS
BEGIN
    IF p_horas_diarias >= 0 AND p_horas_diarias <= 24 THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20003, 'Erro ao validar horas de uso: ' || SQLERRM);
END;
/

-- Função: Validação de E-mail
CREATE OR REPLACE FUNCTION fn_validar_email (
    p_email VARCHAR2
) RETURN BOOLEAN AS
    v_padrao_email CONSTANT VARCHAR2(100) := '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$';
BEGIN
    IF REGEXP_LIKE(p_email, v_padrao_email) THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20004, 'Erro ao validar formato de e-mail: ' || SQLERRM);
END;
/

-- Função: Calcular economia
CREATE OR REPLACE FUNCTION fn_calcular_economia_energia (
    p_consumo_kwh NUMBER,
    p_tarifa_kwh NUMBER
) RETURN NUMBER AS
    v_custo_mensal NUMBER;
BEGIN
    IF p_consumo_kwh < 0 OR p_tarifa_kwh < 0 THEN
        RAISE_APPLICATION_ERROR(-20005, 'Os valores de consumo e tarifa devem ser positivos.');
    END IF;

    v_custo_mensal := p_consumo_kwh * p_tarifa_kwh;

    RETURN v_custo_mensal;
EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20006, 'Erro ao calcular a economia de energia: ' || SQLERRM);
END;
/

-- Inserindo registros na tabela Usuario
BEGIN
    sp_insert_usuario('João Silva', 'joao.silva@email.com', 'hashed_senha_123');
    sp_insert_usuario('Maria Oliveira', 'maria.oliveira@email.com', 'hashed_senha_456');
    sp_insert_usuario('Carlos Santos', 'carlos.santos@email.com', 'hashed_senha_789');
    sp_insert_usuario('Ana Costa', 'ana.costa@email.com', 'hashed_senha_321');
    sp_insert_usuario('Pedro Lima', 'pedro.lima@email.com', 'hashed_senha_654');
    sp_insert_usuario('Julia Torres', 'julia.torres@email.com', 'hashed_senha_987');
    sp_insert_usuario('Felipe Souza', 'felipe.souza@email.com', 'hashed_senha_112');
    sp_insert_usuario('Camila Rocha', 'camila.rocha@email.com', 'hashed_senha_334');
    sp_insert_usuario('Thiago Martins', 'thiago.martins@email.com', 'hashed_senha_556');
    sp_insert_usuario('Renata Alves', 'renata.alves@email.com', 'hashed_senha_778');
END;
/

-- Inserindo registros na tabela Dispositivo
BEGIN
    sp_insert_dispositivo('Geladeira', 150, 24, 264);
    sp_insert_dispositivo('Televisão', 100, 5, 266);
    sp_insert_dispositivo('Ar Condicionado', 999, 8, 268);
    sp_insert_dispositivo('Microondas', 200, 1, 270);
    sp_insert_dispositivo('Notebook', 65, 6, 272);
    sp_insert_dispositivo('Máquina de Lavar', 500, 3, 274);
    sp_insert_dispositivo('Ventilador', 80, 10, 276);
    sp_insert_dispositivo('Carregador de Celular', 5, 3, 278);
    sp_insert_dispositivo('Secador de Cabelo', 600, 0.5, 280);
    sp_insert_dispositivo('Lâmpada LED', 10, 8, 282);
END;
/

-- Inserindo registros na tabela Consumo
BEGIN
    sp_insert_consumo(SYSDATE, 50, 284);
    sp_insert_consumo(SYSDATE, 15, 286);
    sp_insert_consumo(SYSDATE, 480, 288);
    sp_insert_consumo(SYSDATE, 36, 290);
    sp_insert_consumo(SYSDATE, 12, 292);
    sp_insert_consumo(SYSDATE, 45, 294);
    sp_insert_consumo(SYSDATE, 24, 296);
    sp_insert_consumo(SYSDATE, 0.45, 298);
    sp_insert_consumo(SYSDATE, 24, 300);
    sp_insert_consumo(SYSDATE, 2.4, 302);
END;
/

-- Inserindo registros na tabela DicaEconomia
BEGIN
    sp_insert_dica('Desligue aparelhos da tomada quando não estiverem em uso.', 'Geral', 'Alta');
    sp_insert_dica('Use lâmpadas LED em vez de fluorescentes.', 'Iluminação', 'Alta');
    sp_insert_dica('Mantenha o ar-condicionado em temperaturas entre 23°C e 25°C.', 'Climatização', 'Média');
    sp_insert_dica('Evite abrir a porta da geladeira com frequência.', 'Refrigeração', 'Média');
    sp_insert_dica('Utilize a máquina de lavar roupa com a capacidade máxima.', 'Eletrodomésticos', 'Alta');
    sp_insert_dica('Desligue o monitor do computador quando não estiver em uso.', 'Eletrônicos', 'Baixa');
    sp_insert_dica('Limpe o filtro do ar-condicionado regularmente.', 'Climatização', 'Média');
    sp_insert_dica('Prefira equipamentos com selo Procel de economia de energia.', 'Equipamentos', 'Alta');
    sp_insert_dica('Evite usar o chuveiro elétrico por longos períodos.', 'Chuveiro', 'Alta');
    sp_insert_dica('Aproveite a luz natural sempre que possível.', 'Iluminação', 'Alta');
END;
/

-- Exportação JSON:
CREATE OR REPLACE PROCEDURE sp_exportar_todos_json (
    p_resultado OUT CLOB
) AS
    v_json CLOB;
BEGIN
    
    SELECT JSON_ARRAYAGG(
        JSON_OBJECT(
            'usuario_id' VALUE u.usuario_id,
            'nome' VALUE u.nome,
            'email' VALUE u.email,
            'dispositivos' VALUE (
                SELECT JSON_ARRAYAGG(
                    JSON_OBJECT(
                        'dispositivo_id' VALUE d.dispositivo_id,
                        'nome_dispositivo' VALUE d.nome_dispositivo,
                        'potencia_watts' VALUE d.potencia_watts,
                        'consumos' VALUE (
                            SELECT JSON_ARRAYAGG(
                                JSON_OBJECT(
                                    'consumo_id' VALUE c.consumo_id,
                                    'data_registro' VALUE TO_CHAR(c.data_registro, 'YYYY-MM-DD'),
                                    'consumo_mensal_kwh' VALUE c.consumo_mensal_kwh
                                )
                            )
                            FROM Consumo c
                            WHERE c.dispositivo_id = d.dispositivo_id
                        )
                    )
                )
                FROM Dispositivo d
                WHERE d.usuario_id = u.usuario_id
            )
        )
    ) INTO v_json
    FROM Usuario u;

    p_resultado := v_json;
END;
/

SET SERVEROUTPUT ON;

DECLARE
    v_json_output CLOB;
BEGIN
    sp_exportar_todos_json(v_json_output);
    DBMS_OUTPUT.PUT_LINE(v_json_output);
END;
/

-- Package: pkg_usuario_dispositivo
CREATE OR REPLACE PACKAGE pkg_usuario_dispositivo AS
    PROCEDURE sp_insert_usuario(p_nome IN VARCHAR2, p_email IN VARCHAR2, p_senha_hash IN VARCHAR2);
    PROCEDURE sp_insert_dispositivo(p_nome_dispositivo IN VARCHAR2, p_potencia_watts IN NUMBER, p_horas_diarias IN NUMBER, p_usuario_id IN NUMBER);
    FUNCTION fn_validar_horas_uso(p_horas_diarias IN NUMBER) RETURN BOOLEAN;
END pkg_usuario_dispositivo;
/

CREATE OR REPLACE PACKAGE BODY pkg_usuario_dispositivo AS
    PROCEDURE sp_insert_usuario(p_nome IN VARCHAR2, p_email IN VARCHAR2, p_senha_hash IN VARCHAR2) AS
    BEGIN
        INSERT INTO Usuario (usuario_id, nome, email, senha_hash, data_cadastro)
        VALUES (AUDIT_SEQ.NEXTVAL, p_nome, p_email, p_senha_hash, SYSDATE);
    END sp_insert_usuario;

    PROCEDURE sp_insert_dispositivo(p_nome_dispositivo IN VARCHAR2, p_potencia_watts IN NUMBER, p_horas_diarias IN NUMBER, p_usuario_id IN NUMBER) AS
        v_existe_usuario INTEGER;
    BEGIN
        SELECT COUNT(1) INTO v_existe_usuario FROM Usuario WHERE usuario_id = p_usuario_id;
        IF v_existe_usuario = 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'Usuário não encontrado.');
        END IF;

        INSERT INTO Dispositivo (dispositivo_id, nome_dispositivo, potencia_watts, horas_uso_diario, usuario_id)
        VALUES (AUDIT_SEQ.NEXTVAL, p_nome_dispositivo, p_potencia_watts, p_horas_diarias, p_usuario_id);
    END sp_insert_dispositivo;

    FUNCTION fn_validar_horas_uso(p_horas_diarias IN NUMBER) RETURN BOOLEAN AS
    BEGIN
        IF p_horas_diarias >= 0 AND p_horas_diarias <= 24 THEN
            RETURN TRUE;
        ELSE
            RETURN FALSE;
        END IF;
    END fn_validar_horas_uso;
END pkg_usuario_dispositivo;
/

-- Trigger: trg_audit_consumo + Package: pkg_consumo
CREATE OR REPLACE TRIGGER trg_audit_consumo
AFTER INSERT OR UPDATE OR DELETE ON Consumo
FOR EACH ROW
DECLARE
    v_operacao VARCHAR2(10);
BEGIN
    IF INSERTING THEN
        v_operacao := 'INSERT';
    ELSIF UPDATING THEN
        v_operacao := 'UPDATE';
    ELSIF DELETING THEN
        v_operacao := 'DELETE';
    END IF;

    INSERT INTO Auditoria (audit_id, tabela, operacao, data, descricao)
    VALUES (
        AUDIT_SEQ.NEXTVAL,
        'Consumo',
        v_operacao,
        SYSTIMESTAMP,
        'Alteração na tabela Consumo'
    );
END;
/

CREATE OR REPLACE PACKAGE pkg_consumo AS
    PROCEDURE sp_insert_consumo(p_data_registro IN DATE, p_consumo_mensal_kwh IN NUMBER, p_dispositivo_id IN NUMBER);
END pkg_consumo;
/

CREATE OR REPLACE PACKAGE BODY pkg_consumo AS
    PROCEDURE sp_insert_consumo(p_data_registro IN DATE, p_consumo_mensal_kwh IN NUMBER, p_dispositivo_id IN NUMBER) AS
        v_existe_dispositivo INTEGER;
    BEGIN
        SELECT COUNT(1) INTO v_existe_dispositivo FROM Dispositivo WHERE dispositivo_id = p_dispositivo_id;
        IF v_existe_dispositivo = 0 THEN
            RAISE_APPLICATION_ERROR(-20002, 'Dispositivo não encontrado.');
        END IF;

        INSERT INTO Consumo (consumo_id, data_registro, consumo_mensal_kwh, dispositivo_id)
        VALUES (AUDIT_SEQ.NEXTVAL, NVL(p_data_registro, SYSDATE), p_consumo_mensal_kwh, p_dispositivo_id);
    END sp_insert_consumo;
END pkg_consumo;
/


-- Package: pkg_utilitarios
CREATE OR REPLACE PACKAGE pkg_utilitarios AS
    FUNCTION fn_validar_email(p_email IN VARCHAR2) RETURN BOOLEAN;
    PROCEDURE sp_exportar_json(p_usuario_id IN NUMBER, p_resultado OUT CLOB);
END pkg_utilitarios;
/

CREATE OR REPLACE PACKAGE BODY pkg_utilitarios AS
    FUNCTION fn_validar_email(p_email IN VARCHAR2) RETURN BOOLEAN AS
        v_padrao_email CONSTANT VARCHAR2(100) := '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$';
    BEGIN
        IF REGEXP_LIKE(p_email, v_padrao_email) THEN
            RETURN TRUE;
        ELSE
            RETURN FALSE;
        END IF;
    END fn_validar_email;

    PROCEDURE sp_exportar_json(p_usuario_id IN NUMBER, p_resultado OUT CLOB) AS
        v_json CLOB;
    BEGIN
        SELECT JSON_OBJECT(
            'usuario_id' VALUE u.usuario_id,
            'nome' VALUE u.nome,
            'email' VALUE u.email,
            'dispositivos' VALUE (
                SELECT JSON_ARRAYAGG(
                    JSON_OBJECT(
                        'dispositivo_id' VALUE d.dispositivo_id,
                        'nome_dispositivo' VALUE d.nome_dispositivo,
                        'potencia_watts' VALUE d.potencia_watts,
                        'consumos' VALUE (
                            SELECT JSON_ARRAYAGG(
                                JSON_OBJECT(
                                    'consumo_id' VALUE c.consumo_id,
                                    'data_registro' VALUE TO_CHAR(c.data_registro, 'YYYY-MM-DD'),
                                    'consumo_mensal_kwh' VALUE c.consumo_mensal_kwh
                                )
                            )
                            FROM Consumo c
                            WHERE c.dispositivo_id = d.dispositivo_id
                        )
                    )
                )
                FROM Dispositivo d
                WHERE d.usuario_id = u.usuario_id
            )
        ) INTO v_json
        FROM Usuario u
        WHERE u.usuario_id = p_usuario_id;

        p_resultado := v_json;
    END sp_exportar_json;
END pkg_utilitarios;
/
