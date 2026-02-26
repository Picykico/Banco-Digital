package PacoteCore;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class APIServer {

    static BancoDigital banco;

    public static void main(String[] args) throws Exception {
        
        DataBase.criartabelas();
        
        banco = new BancoDigital();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", exchange -> {

            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();

            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File("src/PacoteUI" + path);

            if (!file.exists() || file.isDirectory()) {
                String erro = "Arquivo login.html não encontrado";
                exchange.sendResponseHeaders(404, erro.length());
                exchange.getResponseBody().write(erro.getBytes());
                exchange.close();
                return;
            }

            byte[] html = java.nio.file.Files.readAllBytes(file.toPath());

            String contentType = "text/html; charset = UTF-8";

            if (path.endsWith(".css")) {
                contentType = "text/css";
            } else if (path.endsWith(".js")) {
                contentType = "application/javascript";
            }

            exchange.getResponseHeaders().add("Content-Type", contentType);
            exchange.sendResponseHeaders(200, html.length);

            OutputStream os = exchange.getResponseBody();
            os.write(html);
            os.close();

        });

        server.createContext("/login", exchange -> {
            try {
                if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                String body = lerBody(exchange);

                String cpf = extrair(body, "cpf");
                String senhaStr = extrair(body, "senha");
                String senha4Str = extrair(body, "senha4");

                ResultLogin resultado = banco.validarUsuario(
                        cpf,
                        senhaStr.toCharArray(),
                        senha4Str.toCharArray()
                );

                String resposta;

                switch (resultado) {
                    case LOGIN_OK:
                        resposta = "{\"status\":\"ok\"}";
                        break;
                    default:
                        resposta = "{\"status\":\"erro\",\"msg\":\"CPF ou a senha ou a senha de 4 digitos estão incorretos tente dinovo\"}";
                }

                enviar(exchange, resposta);

            } catch (Exception e) {
                e.printStackTrace();
                enviar(exchange, "{\"status\":\"erro\",\"msg\":\"Erro interno\"}");
            }
        });

        server.createContext("/saldo", exchange -> {

            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed

                return;
            }

            String resposta;

            try {
                double saldo = banco.getSaldo();

                resposta = "{\"saldo\":" + saldo + "}";
                exchange.sendResponseHeaders(200, resposta.getBytes().length);

            } catch (Exception e) {
                resposta = "{\"erro\":\" Usuario não encontrado\"}";
                exchange.sendResponseHeaders(401, resposta.getBytes().length);
            }

            OutputStream os = exchange.getResponseBody();
            os.write(resposta.getBytes());
            os.close();
        });

        server.createContext("/deposito", exchange -> {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            // Ler corpo
            InputStream is = exchange.getRequestBody();
            StringBuilder body = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    body.append(line);
                }
            }

            String json = body.toString();

            // Extrair dados
            String cpf = json.split("\"cpf\":\"")[1].split("\"")[0];
            double valor = Double.parseDouble(
                    json.split("\"valor\":")[1].split("}")[0]
            );

            boolean sucesso = banco.depositarporCpf(cpf, valor);

            String resposta;

            if (sucesso) {
                resposta = "{\"status\":\"ok\",\"msg\":\"Depósito realizado\"}";
            } else {
                resposta = "{\"status\":\"erro\",\"msg\":\"Falha no depósito\"}";
            }

            byte[] responseBytes = resposta.getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        });

        server.createContext("/cadastro", exchange -> {
                try {

                    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                        exchange.sendResponseHeaders(405, -1);
                        return;
                    }

                    String body = lerBody(exchange);
                    System.out.println("JSON recebido: " + body); // 👈 DEBUG

                    String cpf = extrair(body, "cpf");
                    String nome = extrair(body, "nome");
                    String senha = extrair(body, "senha");
                    String senha4 = extrair(body, "senha4");

                    ResultCadastro result = banco.CadastrarUsuario(nome, cpf, senha, senha4);

                    String resposta;

                    switch (result) {
                        case CADASTRO_OK:
                            resposta = "{\"status\":\"ok\"}";
                            break;
                        case CPF_JA_CADASTRADO:
                            resposta = "{\"status\":\"erro\",\"msg\":\"CPF já cadastrado\"}";
                            break;
                        default:
                            resposta = "{\"status\":\"erro\",\"msg\":\"Erro inesperado\"}";
                    }

                    enviar(exchange, resposta);

                } catch (Exception e) {
                    e.printStackTrace(); // 👈 MOSTRA O ERRO REAL
                    enviar(exchange, "{\"status\":\"erro\",\"msg\":\"Erro interno\"}");
                }
            });

            server.createContext("/saque", exchange -> {
                if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                String body = lerBody(exchange);
                String cpf = extrair(body, "cpf");
                double valor = Double.parseDouble(extrair(body, "valor"));

                boolean ok = banco.saqueporCpf(cpf, valor);

                String resposta = ok
                        ? "{\"status\":\"ok\"}"
                        : "{\"status\":\"erro\"}";

                enviar(exchange, resposta);
            });
            server.createContext("/historico", exchange -> {
                if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                String body = lerBody(exchange);
                String cpf = extrair(body, "cpf");

                String[] hist = banco.getHistoricoPorCpf(cpf);

                String resposta;
                if (hist == null) {
                    resposta = "{\"status\":\"erro\"}";
                } else {
                    resposta = "{\"historico\":\"" + String.join("|", hist) + "\"}";
                }

                enviar(exchange, resposta);
            });

            server.start();
            System.out.println("API rodando em http://localhost:8080");
        }

    static String lerBody(HttpExchange exchange) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody())
        );
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    static String extrair(String json, String campo) {
        return json.split("\"" + campo + "\":\"")[1].split("\"")[0];
    }

    static void enviar(HttpExchange exchange, String resposta) throws IOException {
        exchange.sendResponseHeaders(200, resposta.getBytes().length);
        exchange.getResponseBody().write(resposta.getBytes());
        exchange.close();
    }

}
