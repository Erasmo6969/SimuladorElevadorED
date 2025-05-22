package com.erasmo;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.erasmo.graphics.ConfiguracaoSimulador;

/**
 * Classe principal que inicializa a aplicação de simulação de elevadores.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Método principal que inicia a aplicação.
     *
     * @param args argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        LOGGER.log(Level.INFO, "Iniciando aplicação de simulação de elevadores");

        // Configura a interface gráfica para rodar na thread de eventos do Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Tenta definir o look and feel do sistema operacional
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                LOGGER.log(Level.INFO, "Look and feel configurado: {0}",
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Não foi possível configurar o look and feel: {0}",
                        e.getMessage());
            }

            try {
                // Cria e exibe a tela de configuração inicial
                ConfiguracaoSimulador telaInicial = new ConfiguracaoSimulador();
                telaInicial.setVisible(true);
                LOGGER.log(Level.INFO, "Tela de configuração inicializada com sucesso");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro ao inicializar a tela de configuração", e);
                System.err.println("Erro ao iniciar a aplicação: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
