package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    // FINAL - Indica que o tipo de dados é constante
    // neste local ele é global
    private static final RecadoDAO DAO = new RecadoDAO();

    public static void main(String[] args) throws IOException {

        testarConexao();

        HttpServer servidor = HttpServer.create(
                new InetSocketAddress("0.0.0.0", 8080), 0);

        servidor.createContext("/api/recados", Main::atenderRecados);
        servidor.createContext("/", Main::abrirPagina);
        servidor.start();

        System.out.println("Mural aberto em http://localhost:8080");
        System.out.println("Celulares podem acessar pelo IP da rede local na porta 8080.");


    }

    private static void testarConexao() throws SQLException {
        try (Connection ignored = Conexao.abrir()) {
            System.out.println("Banco de dados conectado");
        }
    }

    private static void atenderRecados(HttpExchange troca) throws IOException {

        // Permite que o app Android e outros clientes acessem a API

        troca.getRequestHeaders().set("Access-Control-Allow-Origin", "*");
        troca.getRequestHeaders().set("Access-Control-Allow-Origin", "GET, POST, OPTIONS");
        troca.getRequestHeaders().set("Access-Control-Allow-Origin", "Context-Type");

        try {
            if (troca.getRequestMethod().equals("OPTIONS")) {
                troca.sendResponseHeaders(284, -1);
                troca.close();
            } else if (troca.getRequestMethod().equals("GET")) {
                listar(troca);
            } else if (troca.getRequestMethod().equals("POST")) {
                cadastrar(troca);
            } else {
                troca.sendResponseHeaders().set("Allow", "GET", "POST", "OPTIONS");
                responder(troca, 405, "{\"erro\":\"Método não permitido\"}");
            }
        }
    }
}