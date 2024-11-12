import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class MensagensSeguras extends JFrame {
    private JTextField mensagemField;
    private JPasswordField senhaField;
    private JTextArea resultadoArea;
    private JButton criptografarButton;
    private JButton alternarModoButton;
    private boolean modoCriptografar = true;

    public MensagensSeguras() {
        setTitle("Mensagens Seguras");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(60, 63, 65));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel modoLabel = new JLabel("Modo: Criptografar");
        modoLabel.setForeground(Color.YELLOW);
        modoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(modoLabel, gbc);

        JLabel mensagemLabel = new JLabel("Mensagem:");
        mensagemLabel.setForeground(Color.WHITE);
        mensagemLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(mensagemLabel, gbc);

        mensagemField = new JTextField(20);
        gbc.gridx = 1;
        add(mensagemField, gbc);

        JLabel senhaLabel = new JLabel("Senha:");
        senhaLabel.setForeground(Color.WHITE);
        senhaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(senhaLabel, gbc);

        senhaField = new JPasswordField(20);
        gbc.gridx = 1;
        add(senhaField, gbc);

        criptografarButton = new JButton("Criptografar");
        criptografarButton.setBackground(new Color(0, 128, 0));
        criptografarButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(criptografarButton, gbc);

        resultadoArea = new JTextArea(3, 20);
        resultadoArea.setLineWrap(true);
        resultadoArea.setWrapStyleWord(true);
        resultadoArea.setEditable(false);
        resultadoArea.setBackground(new Color(50, 50, 50));
        resultadoArea.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(resultadoArea);
        gbc.gridy = 4;
        add(scrollPane, gbc);

        alternarModoButton = new JButton("Alternar para Descriptografar");
        alternarModoButton.setBackground(new Color(70, 130, 180));
        alternarModoButton.setForeground(Color.WHITE);
        gbc.gridy = 5;
        add(alternarModoButton, gbc);

        criptografarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mensagem = mensagemField.getText();
                String senha = new String(senhaField.getPassword());

                if (senha.length() < 8) {
                    JOptionPane.showMessageDialog(null, "Erro: A senha deve ter pelo menos 8 caracteres.", "Erro de Senha", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    if (modoCriptografar) {
                        String criptografada = criptografarAES(mensagem, senha);
                        resultadoArea.setText(criptografada);
                    } else {
                        String descriptografada = descriptografarAES(mensagem, senha);
                        resultadoArea.setText(descriptografada);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro na " + (modoCriptografar ? "criptografia" : "descriptografia") + ".", "Erro", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        alternarModoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modoCriptografar = !modoCriptografar;
                modoLabel.setText("Modo: " + (modoCriptografar ? "Criptografar" : "Descriptografar"));
                criptografarButton.setText(modoCriptografar ? "Criptografar" : "Descriptografar");
                alternarModoButton.setText(modoCriptografar ? "Alternar para Descriptografar" : "Alternar para Criptografar");
            }
        });

        setVisible(true);
    }

    private SecretKeySpec gerarChaveAES(String senha) throws Exception {
        byte[] key = senha.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        return new SecretKeySpec(key, 0, 16, "AES");
    }

    private String criptografarAES(String mensagem, String senha) throws Exception {
        SecretKeySpec chave = gerarChaveAES(senha);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, chave);
        byte[] textoCriptografado = cipher.doFinal(mensagem.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(textoCriptografado);
    }

    private String descriptografarAES(String mensagem, String senha) throws Exception {
        SecretKeySpec chave = gerarChaveAES(senha);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, chave);
        byte[] textoDescriptografado = cipher.doFinal(Base64.getDecoder().decode(mensagem));
        return new String(textoDescriptografado, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MensagensSeguras::new);
    }
}
