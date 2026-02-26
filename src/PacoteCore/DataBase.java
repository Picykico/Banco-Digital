package PacoteCore;

import java.sql.*;

public class DataBase {

    private static final String URL = "jdbc:sqlite:banco_digital.db";

    public static Connection conectar() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(URL);
            return conn;

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void criartabelas() {

//        Criação das tabelas das usuarios
        String SqlUsuarios = """
            CREATE TABLE IF NOT EXISTS usuarios (
                             id  INTEGER PRIMARY KEY AUTOINCREMENT,
                             nome TEXT NOT NULL,
                             cpf TEXT UNIQUE NOT NULL,
                             senha TEXT NOT NULL,
                             senha4 TEXT NOT NULL                 
                             
                                              );
                             """;

//        Criação das tabelas das contas
        String SqlContas = """
            CREATE TABLE IF NOT EXISTS contas (
                             id INTEGER PRIMARY KEY AUTOINCREMENT,
                             usuario_id INTEGER NOT NULL,
                             saldo REAL NOT NULL,
                             
                             FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
                                       );
                           """;

        try(Connection conn = conectar(); 
            Statement stmt = conn.createStatement()){
            
            stmt.execute(SqlUsuarios);
            stmt.execute(SqlContas);
            
            
        }catch(SQLException e){
            e.printStackTrace();
            
        }
    }

    public static int inserirUsuario(String nome, String cpf, String senha, String senha4) {

        String sql = "INSERT INTO usuarios (nome, cpf, senha, senha4) VALUES(?, ?, ?, ?)";

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nome);
            stmt.setString(2, cpf);
            stmt.setString(3, senha);
            stmt.setString(4, senha4);

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static void criarConta(int usuarioId) {

        String sql = """
                     INSERT INTO contas (usuario_id, saldo)
                     VALUES (?, 0)
                     
                     """;

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean validarSenha4(int usuarioId, String TSenha4) {

        String sql = "SELECT senha4 FROM usuarios WHERE id = ?";

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String senha4Banco = rs.getString("senha4");
                return senha4Banco.equals(TSenha4);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String buscarcpf(String Tcpf) {

        String sql = "SELECT cpf FROM usuarios WHERE cpf = ?"; // vai buscar todos as colunas porque queremosum Usuario completo!

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, Tcpf);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("cpf");

            } else {
                return null;
            }

        } catch (SQLException e) {

            e.printStackTrace();
            return null;

        }

    }

    public static boolean validarSenha(int usuarioId, String TSenha) {

        String sql = "SELECT senha FROM usuarios WHERE id = ?";

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String senhaBanco = rs.getString("senha");
                return senhaBanco.equals(TSenha);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Integer buscarIdporCPF(String cpf) {

        String sql = "SELECT id FROM usuarios WHERE cpf = ?";

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        return null;
    }

    public static Usuario buscarUsuarioporCPF(String cpf) {
        String sql = "SELECT nome, cpf, senha, senha4 FROM usuarios WHERE cpf = ?";

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("senha").toCharArray(),
                        rs.getString("senha4").toCharArray()
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ContaBancaria buscarContaporUsuarioId(String usuario_id) {
        String sql = "SELECT saldo FROM contas WHERE usuario_id = ?";

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new ContaBancaria(rs.getDouble("saldo"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
