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

public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Consulta CEP - ViaCEP");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel panel = new JPanel(new BorderLayout());
        JTextField cepField = new JTextField();
        JButton consultarBtn = new JButton("Consultar");
        JTextArea resultadoArea = new JTextArea();
        resultadoArea.setEditable(false);

        panel.add(new JLabel("Digite o CEP:"), BorderLayout.NORTH);
        panel.add(cepField, BorderLayout.CENTER);
        panel.add(consultarBtn, BorderLayout.EAST);
        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(resultadoArea), BorderLayout.CENTER);

        consultarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cep = cepField.getText().trim();
                if (cep.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Digite um CEP válido.");
                    return;
                }
                try {
                    String url = "https://viacep.com.br/ws/" + cep + "/json/";
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    conn.disconnect();

                    String json = content.toString();

                    // Checa se retornou "erro"
                    if (json.contains("\"erro\": true")) {
                        resultadoArea.setText("CEP não encontrado.");
                        return;
                    }

                    // Extrai todos os campos conhecidos
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

                } catch (Exception ex) {
                    resultadoArea.setText("Erro ao consultar o CEP.");
                }
            }
        });

        frame.setVisible(true);
    }

    // Método auxiliar usando regex para capturar os valores entre aspas
    private static String extrairCampo(String json, String campo) {
        Pattern pattern = Pattern.compile("\"" + campo + "\":\\s*\"(.*?)\"");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : "";
    }
}
