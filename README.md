
# Consulta CEP com Java Swing e ViaCEP

Este projeto implementa uma aplicação em Java Swing que consulta endereços a partir de um CEP utilizando a API ViaCEP.

## Imports

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
```

- **javax.swing.** : Importa todos os componentes da biblioteca Swing, usada para criar interfaces gráficas (JFrame, JButton, JTextField, etc.).
- **java.awt.**: Importa classes básicas do AWT, principalmente para layouts e cores.
- **java.awt.event.**: Importa classes para lidar com eventos de interação do usuário, como cliques em botões.
- **java.io.**: Para ler dados de entrada (InputStreamReader, BufferedReader), usado para capturar a resposta da API.
- **java.net.**: Para abrir conexões HTTP (URL e HttpURLConnection).
- **java.util.regex.**: Para trabalhar com expressões regulares (regex), usado para extrair valores do JSON retornado.

## Classe Principal

```java
public class App {
    public static void main(String[] args) {
    }
}
```

- Cria a classe **App**.
- O método **main** é o ponto de entrada do programa. Aqui começa a execução.

## Criação da Janela

```java
    JFrame frame = new JFrame("Consulta CEP - ViaCEP");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(500, 500);

```

- Cria uma janela (**JFrame**) com o título **"Consulta CEP - ViaCEP"**.
- Define que a aplicação vai fechar quando o usuário clicar no X (**EXIT_ON_CLOSE**).
- Define o tamanho da janela como **500x500 pixels**.

## Painel e Componentes

```java
    JPanel panel = new JPanel(new BorderLayout());
    JTextField cepField = new JTextField();
    JButton consultarBtn = new JButton("Consultar");
    JTextArea resultadoArea = new JTextArea();
    resultadoArea.setEditable(false);
```

- **JPanel panel = new JPanel(new BorderLayout())**: Cria um painel que organiza os componentes usando BorderLayout, que divide a tela em NORTH, SOUTH, EAST, WEST e CENTER.
- **JTextField cepField**: Campo de texto onde o usuário digita o CEP.
- **JButton consultarBtn**: Botão que dispara a consulta do CEP.
- **JTextArea resultadoArea**: Área de texto onde serão exibidos os resultados da consulta.
- **resultadoArea.setEditable(false)**: Impede que o usuário edite o texto mostrado.

## Adicionando Componentes ao Painel

```java
    panel.add(new JLabel("Digite o CEP:"), BorderLayout.NORTH);
    panel.add(cepField, BorderLayout.CENTER);
    panel.add(consultarBtn, BorderLayout.EAST);
    frame.add(panel, BorderLayout.NORTH);
    frame.add(new JScrollPane(resultadoArea), BorderLayout.CENTER);
```

- **panel.add(new JLabel("Digite o CEP:"), BorderLayout.NORTH)**: Adiciona uma label no topo do painel.
- **panel.add(cepField, BorderLayout.CENTER)**: Adiciona o campo de texto no centro do painel.
- **panel.add(consultarBtn, BorderLayout.EAST)**: Coloca o botão à direita do painel.
- **frame.add(panel, BorderLayout.NORTH)**: Adiciona o painel completo na parte superior da janela.
- **frame.add(new JScrollPane(resultadoArea), BorderLayout.CENTER)**: Adiciona a área de resultado na parte central da janela com barra de rolagem, caso o texto seja grande.

## Evento do Botão

```java
    consultarBtn.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
```

- Adiciona um **listener** ao botão para capturar cliques.
- O código dentro de **actionPerformed** será executado quando o usuário clicar no botão.

## Leitura do CEP

```java
    String cep = cepField.getText().trim();
    if (cep.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "Digite um CEP válido.");
         return;
    }
```

- **cepField.getText().trim()**: Lê o texto digitado no campo de CEP e remove espaços no início/fim.

- Se o CEP estiver vazio, mostra uma **mensagem de alerta** e encerra a execução do evento.

## Conexão com ViaCEP

```java
    try {
        String url = "https://viacep.com.br/ws/" + cep + "/json/";
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
```

- Monta a URL de consulta usando o CEP digitado.
- Abre uma conexão HTTP com essa URL.
- Define o método da requisição como **GET** (somente pegar dados).

## Leitura da Resposta

```java
    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String inputLine;
    StringBuilder content = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
    }
    in.close();
    conn.disconnect();
```

- **InputStreamReader** e **BufferedReader** lêem o retorno da API linha por linha.
- **StringBuilder content** armazena todo o JSON retornado.
- Fecha o **BufferedReader** e a conexão HTTP.

## Tratamento do JSON

```java
    String json = content.toString();

    if (json.contains("\"erro\": true")) {
        resultadoArea.setText("CEP não encontrado.");
        return;
    }
```

- Converte **StringBuilder** em String.
- Verifica se o JSON contém **"erro": true** (quando o CEP não existe).
- Se sim, exibe "CEP não encontrado" e encerra a execução.

## Extração de Campos

```java
    String cepRes = extrairCampo(json, "cep");
    String logradouro = extrairCampo(json, "logradouro");
    String complemento = extrairCampo(json, "complemento");
    String unidade = extrairCampo(json, "unidade");
    String bairro = extrairCampo(json, "bairro");
    String localidade = extrairCampo(json, "localidade");
    String uf = extrairCampo(json, "uf");
    String estado = extrairCampo(json, "estado");
    String regiao = extrairCampo(json, "regiao");
    String ibge = extrairCampo(json, "ibge");
    String gia = extrairCampo(json, "gia");
    String ddd = extrairCampo(json, "ddd");
    String siafi = extrairCampo(json, "siafi");
```

- Cada linha usa o método **extrairCampo** para capturar os valores do JSON correspondente ao campo solicitado.
- Ex.: **"cep"** retorna o CEP formatado, **"bairro"** retorna o bairro, **"ddd"** retorna o código da região, etc.

## Exibição dos Resultados

```java
    resultadoArea.setText(
        "CEP: " + cepRes + "\n" +
        "Logradouro: " + logradouro + "\n" +
        "Complemento: " + complemento + "\n" +
        "Unidade: " + unidade + "\n" +
        "Bairro: " + bairro + "\n" +
        "Cidade: " + localidade + "\n" +
        "UF: " + uf + "\n" +
        "Estado: " + estado + "\n" +
        "Região: " + regiao + "\n" +
        "IBGE: " + ibge + "\n" +
        "GIA: " + gia + "\n" +
        "DDD: " + ddd + "\n" +
        "SIAFI: " + siafi
    );
```

- Concatena todos os valores extraídos do JSON e exibe na **JTextArea.**

## Tratamento de Exceção

```java
    } catch (Exception ex) {
        resultadoArea.setText("Erro ao consultar o CEP.");
    }
```

- Captura qualquer erro na conexão, leitura ou parsing do JSON e exibe mensagem de erro para o usuário.

## Exibição da Janela

```java
    frame.setVisible(true);
```

- Torna a janela visível.

## Método auxiliar para extrair campos do JSON

```java
    private static String extrairCampo(String json, String campo) {
        Pattern pattern = Pattern.compile("\"" + campo + "\":\\s*\"(.*?)\"");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : "";
    }
```

- Usa expressão regular para encontrar o valor de um campo no JSON.
- **"campo": "valor"** → extrai **"valor"**.
- Se não encontrar, retorna **""** (vazio).

---

### Resumo do fluxo do programa

1. Cria interface gráfica com campo de CEP, botão e área de resultado.
2. Usuário digita o CEP e clica em “Consultar”.
3. Programa conecta na API ViaCEP via HTTP GET.
4. Lê a resposta JSON da API.
5. Verifica se o CEP existe.
6. Extrai os campos do JSON usando regex.
7. Exibe os dados do endereço na área de texto.

