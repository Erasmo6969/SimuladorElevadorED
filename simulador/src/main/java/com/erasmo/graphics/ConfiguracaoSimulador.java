package com.erasmo.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import com.erasmo.controls.Simulador;
import com.erasmo.enums.ModeloHeuristica;
import com.erasmo.enums.TipoPainel;

public class ConfiguracaoSimulador extends JFrame {

    private static final long serialVersionUID = 1L;

    // Spinners para configuração numérica sem limites rígidos
    private JSpinner spinnerAndares;
    private JSpinner spinnerElevadores;
    private JSpinner spinnerPessoas;
    private JSpinner spinnerCapacidade;
    private JSpinner spinnerTempoDeslocamentoPadrao;
    private JSpinner spinnerTempoDeslocamentoPico;
    private JSpinner spinnerConsumoEnergiaPorDeslocamento;
    private JSpinner spinnerConsumoEnergiaPorParada;
    private JSpinner spinnerTempoMaximoEspera;
    private JSpinner spinnerVelocidadeSimulacao;
    private JSpinner spinnerTempoLimite;

    // Combos para seleção
    private JComboBox<String> comboTipoPainel;
    private JComboBox<String> comboHeuristica;

    // Checkbox para geração automática
    private JCheckBox checkGeracaoAutomatica;
    private JSpinner spinnerIntervaloGeracao;
    private JSpinner spinnerQtdPorGeracao;

    // Cores e fontes personalizadas
    private Color corFundo = new Color(240, 248, 255); // AliceBlue
    private Color corDestaque = new Color(70, 130, 180); // SteelBlue
    private Font fonteTitulo = new Font("Segoe UI", Font.BOLD, 14);
    private Font fonteLabel = new Font("Segoe UI", Font.PLAIN, 12);
    private Font fonteBotao = new Font("Segoe UI", Font.BOLD, 13);

    public ConfiguracaoSimulador() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Configuração Avançada do Simulador de Elevadores");
        setSize(800, 680);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(corFundo);

        // Inicializar componentes
        inicializarComponentes();

        // Mostrar a janela
        setVisible(true);
    }

    private void inicializarComponentes() {
        // Utilizando JTabbedPane para organizar as configurações em abas
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(fonteTitulo);
        abas.setBackground(corFundo);

        // Aba de configurações básicas
        JPanel painelBasico = criarPainelConfiguracaoBasica();
        abas.addTab("Configurações Básicas", painelBasico);

        // Aba de configurações avançadas
        JPanel painelAvancado = criarPainelConfiguracaoAvancada();
        abas.addTab("Configurações Avançadas", painelAvancado);

        // Aba de geração automática
        JPanel painelGeracao = criarPainelGeracaoAutomatica();
        abas.addTab("Geração Automática", painelGeracao);

        // Painel principal com BorderLayout
        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBackground(corFundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Adicionar logo ou título no topo
        JLabel labelTitulo = new JLabel("Simulador de Elevadores", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelTitulo.setForeground(corDestaque);
        labelTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        painelPrincipal.add(labelTitulo, BorderLayout.NORTH);

        // Adicionar abas ao centro
        painelPrincipal.add(abas, BorderLayout.CENTER);

        // Adicionar botões na parte inferior
        painelPrincipal.add(criarPainelBotoes(), BorderLayout.SOUTH);

        // Adicionar o painel principal à janela
        add(painelPrincipal);
    }

    private JSpinner criarSpinner(int valorPadrao, int min, int max, int step) {
        // Usar Integer.MAX_VALUE como máximo para efetivamente remover limites superiores
        SpinnerNumberModel model = new SpinnerNumberModel(valorPadrao, min, Integer.MAX_VALUE, step);
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(fonteLabel);

        // Definir o tamanho preferido para o spinner
        spinner.setPreferredSize(new Dimension(150, 25));

        return spinner;
    }

    private JPanel criarPainelConfiguracaoBasica() {
        JPanel painel = new JPanel();
        painel.setLayout(new GridBagLayout());
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Número de andares
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel labelAndares = new JLabel("Número de andares:");
        labelAndares.setFont(fonteLabel);
        painel.add(labelAndares, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        spinnerAndares = criarSpinner(8, 2, Integer.MAX_VALUE, 1);
        painel.add(spinnerAndares, gbc);

        // Número de elevadores
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel labelElevadores = new JLabel("Número de elevadores:");
        labelElevadores.setFont(fonteLabel);
        painel.add(labelElevadores, gbc);

        gbc.gridx = 1;
        spinnerElevadores = criarSpinner(2, 1, Integer.MAX_VALUE, 1);
        painel.add(spinnerElevadores, gbc);

        // Quantidade de pessoas
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel labelPessoas = new JLabel("Quantidade inicial de pessoas:");
        labelPessoas.setFont(fonteLabel);
        painel.add(labelPessoas, gbc);

        gbc.gridx = 1;
        spinnerPessoas = criarSpinner(20, 0, Integer.MAX_VALUE, 1);
        painel.add(spinnerPessoas, gbc);

        // Capacidade máxima
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel labelCapacidade = new JLabel("Capacidade máxima dos elevadores:");
        labelCapacidade.setFont(fonteLabel);
        painel.add(labelCapacidade, gbc);

        gbc.gridx = 1;
        spinnerCapacidade = criarSpinner(6, 1, Integer.MAX_VALUE, 1);
        painel.add(spinnerCapacidade, gbc);

        // Tempo limite
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel labelTempoLimite = new JLabel("Tempo limite (minutos):");
        labelTempoLimite.setFont(fonteLabel);
        painel.add(labelTempoLimite, gbc);

        gbc.gridx = 1;
        spinnerTempoLimite = criarSpinner(150, 1, Integer.MAX_VALUE, 10);
        painel.add(spinnerTempoLimite, gbc);

        // Velocidade de simulação
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel labelVelocidade = new JLabel("Velocidade da simulação (ms):");
        labelVelocidade.setFont(fonteLabel);
        painel.add(labelVelocidade, gbc);

        gbc.gridx = 1;
        spinnerVelocidadeSimulacao = criarSpinner(1000, 10, Integer.MAX_VALUE, 100);
        painel.add(spinnerVelocidadeSimulacao, gbc);

        // Tipo de painel
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        JLabel labelTipoPainel = new JLabel("Tipo de painel:");
        labelTipoPainel.setFont(fonteLabel);
        painel.add(labelTipoPainel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        comboTipoPainel = new JComboBox<>(new String[]{
            "Único botão",
            "Dois botões (subir/descer)",
            "Painel numérico"
        });
        comboTipoPainel.setSelectedIndex(1);
        comboTipoPainel.setFont(fonteLabel);
        painel.add(comboTipoPainel, gbc);

        // Modelo de heurística
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        JLabel labelHeuristica = new JLabel("Modelo de heurística:");
        labelHeuristica.setFont(fonteLabel);
        painel.add(labelHeuristica, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        comboHeuristica = new JComboBox<>(new String[]{
            "Sem heurística (atendimento na ordem de chegada)",
            "Otimização do tempo de espera",
            "Otimização do consumo de energia"
        });
        comboHeuristica.setSelectedIndex(0);
        comboHeuristica.setFont(fonteLabel);
        painel.add(comboHeuristica, gbc);

        return painel;
    }

    private JPanel criarPainelConfiguracaoAvancada() {
        JPanel painel = new JPanel();
        painel.setLayout(new GridBagLayout());
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Tempo deslocamento padrão
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel labelTempoPadrao = new JLabel("Tempo deslocamento padrão (minutos):");
        labelTempoPadrao.setFont(fonteLabel);
        painel.add(labelTempoPadrao, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        spinnerTempoDeslocamentoPadrao = criarSpinner(2, 1, Integer.MAX_VALUE, 1);
        painel.add(spinnerTempoDeslocamentoPadrao, gbc);

        // Tempo deslocamento pico
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel labelTempoPico = new JLabel("Tempo deslocamento pico (minutos):");
        labelTempoPico.setFont(fonteLabel);
        painel.add(labelTempoPico, gbc);

        gbc.gridx = 1;
        spinnerTempoDeslocamentoPico = criarSpinner(4, 1, Integer.MAX_VALUE, 1);
        painel.add(spinnerTempoDeslocamentoPico, gbc);

        // Consumo energia por deslocamento
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel labelConsumoDeslocamento = new JLabel("Consumo energia por deslocamento:");
        labelConsumoDeslocamento.setFont(fonteLabel);
        painel.add(labelConsumoDeslocamento, gbc);

        gbc.gridx = 1;
        spinnerConsumoEnergiaPorDeslocamento = criarSpinner(10, 1, Integer.MAX_VALUE, 1);
        painel.add(spinnerConsumoEnergiaPorDeslocamento, gbc);

        // Consumo energia por parada
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel labelConsumoParada = new JLabel("Consumo energia por parada:");
        labelConsumoParada.setFont(fonteLabel);
        painel.add(labelConsumoParada, gbc);

        gbc.gridx = 1;
        spinnerConsumoEnergiaPorParada = criarSpinner(5, 1, Integer.MAX_VALUE, 1);
        painel.add(spinnerConsumoEnergiaPorParada, gbc);

        // Tempo máximo de espera
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel labelTempoMaximo = new JLabel("Tempo máximo de espera (minutos):");
        labelTempoMaximo.setFont(fonteLabel);
        painel.add(labelTempoMaximo, gbc);

        gbc.gridx = 1;
        spinnerTempoMaximoEspera = criarSpinner(10, 1, Integer.MAX_VALUE, 1);
        painel.add(spinnerTempoMaximoEspera, gbc);

        return painel;
    }

    private JPanel criarPainelGeracaoAutomatica() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Checkbox de ativação
        checkGeracaoAutomatica = new JCheckBox("Ativar geração automática de pessoas");
        checkGeracaoAutomatica.setFont(fonteTitulo);
        checkGeracaoAutomatica.setBackground(corFundo);
        checkGeracaoAutomatica.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(checkGeracaoAutomatica);

        painel.add(Box.createVerticalStrut(20));

        // Painel para intervalos
        JPanel painelIntervalos = new JPanel(new GridBagLayout());
        painelIntervalos.setBackground(corFundo);
        painelIntervalos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(corDestaque),
                "Configurações de Geração",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                fonteTitulo,
                corDestaque));
        painelIntervalos.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Intervalo de geração
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel labelIntervalo = new JLabel("Intervalo de geração (ciclos):");
        labelIntervalo.setFont(fonteLabel);
        painelIntervalos.add(labelIntervalo, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        spinnerIntervaloGeracao = criarSpinner(10, 1, Integer.MAX_VALUE, 1);
        painelIntervalos.add(spinnerIntervaloGeracao, gbc);

        // Quantidade por geração
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel labelQuantidade = new JLabel("Quantidade por geração:");
        labelQuantidade.setFont(fonteLabel);
        painelIntervalos.add(labelQuantidade, gbc);

        gbc.gridx = 1;
        spinnerQtdPorGeracao = criarSpinner(2, 1, Integer.MAX_VALUE, 1);
        painelIntervalos.add(spinnerQtdPorGeracao, gbc);

        painel.add(painelIntervalos);

        // Adicionar espaço para deixar a interface mais leve
        painel.add(Box.createVerticalGlue());

        return painel;
    }

    private JPanel criarPainelBotoes() {
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.X_AXIS));
        painelBotoes.setBackground(corFundo);
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        painelBotoes.add(Box.createHorizontalGlue());

        JButton botaoIniciar = new JButton("Iniciar Simulação");
        botaoIniciar.setFont(fonteBotao);
        botaoIniciar.addActionListener(e -> iniciarSimulacao());

        JButton botaoCancelar = new JButton("Cancelar");
        botaoCancelar.setFont(fonteBotao);
        botaoCancelar.addActionListener(e -> System.exit(0));

        painelBotoes.add(botaoCancelar);
        painelBotoes.add(Box.createHorizontalStrut(15));
        painelBotoes.add(botaoIniciar);

        return painelBotoes;
    }

    private void iniciarSimulacao() {
        try {
            // Obter valores da configuração a partir dos spinners
            int numeroAndares = (Integer) spinnerAndares.getValue();
            int numeroElevadores = (Integer) spinnerElevadores.getValue();
            int capacidadeElevador = (Integer) spinnerCapacidade.getValue();
            int quantidadePessoas = (Integer) spinnerPessoas.getValue();
            int tempoDeslocamentoPadrao = (Integer) spinnerTempoDeslocamentoPadrao.getValue();
            int tempoDeslocamentoPico = (Integer) spinnerTempoDeslocamentoPico.getValue();
            int consumoEnergiaPorDeslocamento = (Integer) spinnerConsumoEnergiaPorDeslocamento.getValue();
            int consumoEnergiaPorParada = (Integer) spinnerConsumoEnergiaPorParada.getValue();
            int tempoMaximoEspera = (Integer) spinnerTempoMaximoEspera.getValue();
            int velocidadeSimulacao = (Integer) spinnerVelocidadeSimulacao.getValue();
            int tempoLimite = (Integer) spinnerTempoLimite.getValue();
            boolean geracaoAutomatica = checkGeracaoAutomatica.isSelected();
            int intervaloGeracao = (Integer) spinnerIntervaloGeracao.getValue();
            int qtdPorGeracao = (Integer) spinnerQtdPorGeracao.getValue();

            // Validação básica
            if (numeroAndares < 2) {
                throw new IllegalArgumentException("O prédio deve ter pelo menos 2 andares");
            }
            if (numeroElevadores < 1) {
                throw new IllegalArgumentException("Deve haver pelo menos 1 elevador");
            }
            if (capacidadeElevador < 1) {
                throw new IllegalArgumentException("A capacidade deve ser pelo menos 1");
            }

            // Obter tipo de painel
            TipoPainel tipoPainel;
            switch (comboTipoPainel.getSelectedIndex()) {
                case 0:
                    tipoPainel = TipoPainel.UNICO_BOTAO;
                    break;
                case 2:
                    tipoPainel = TipoPainel.PAINEL_NUMERICO;
                    break;
                default:
                    tipoPainel = TipoPainel.DOIS_BOTOES;
            }

            // Obter modelo de heurística
            ModeloHeuristica modeloHeuristica;
            switch (comboHeuristica.getSelectedIndex()) {
                case 0:
                    modeloHeuristica = ModeloHeuristica.SEM_HEURISTICA;
                    break;
                case 2:
                    modeloHeuristica = ModeloHeuristica.OTIMIZACAO_CONSUMO_ENERGIA;
                    break;
                default:
                    modeloHeuristica = ModeloHeuristica.OTIMIZACAO_TEMPO_ESPERA;
            }

            // Criar o simulador com as configurações incluindo tempo limite
            Simulador simulador = new Simulador(
                    numeroAndares,
                    numeroElevadores,
                    capacidadeElevador,
                    tipoPainel,
                    tempoDeslocamentoPadrao,
                    tempoDeslocamentoPico,
                    consumoEnergiaPorDeslocamento,
                    consumoEnergiaPorParada,
                    modeloHeuristica,
                    tempoMaximoEspera,
                    tempoLimite
            );

            // Fechar esta janela
            this.dispose();

            // Iniciar o simulador GUI com as configurações
            SimuladorGI simuladorGI = new SimuladorGI(simulador);
            simuladorGI.setVisible(true);

            // Configurar a velocidade da simulação
            simuladorGI.setVelocidadeSimulacao(velocidadeSimulacao);

            // Configurar geração automática
            if (geracaoAutomatica) {
                simuladorGI.configurarGeracaoAutomatica(geracaoAutomatica, intervaloGeracao, qtdPorGeracao);
            }

            // Adicionar pessoas iniciais se necessário
            if (quantidadePessoas > 0) {
                simuladorGI.adicionarPessoasAleatorias(quantidadePessoas);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao iniciar a simulação: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
}
