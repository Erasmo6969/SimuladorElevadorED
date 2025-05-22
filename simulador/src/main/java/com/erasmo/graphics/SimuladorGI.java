package com.erasmo.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.erasmo.controls.Simulador;
import com.erasmo.enums.TipoPainel;
import com.erasmo.relatory.RelatorioSimulacao;
import com.erasmo.structure.Andar;
import com.erasmo.structure.Elevador;
import com.erasmo.structure.Pessoa;
import com.erasmo.structure.Predio;
import com.erasmo.tads.Lista;

public class SimuladorGI extends JFrame {

    private static final long serialVersionUID = 1L;

    // Constantes de estilo
    private static final Color COR_FUNDO = new Color(240, 248, 255);
    private static final Color COR_PRIMARIA = new Color(70, 130, 180);
    private static final Color COR_SECUNDARIA = new Color(100, 170, 220);
    private static final Color COR_ELEVADOR_PARADO = new Color(50, 180, 100);
    private static final Color COR_ELEVADOR_MOVIMENTO = new Color(230, 160, 30);
    private static final Color COR_DESTAQUE = new Color(52, 152, 219);
    private static final Color COR_ALERTA = new Color(231, 76, 60);

    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 11);

    // Formatos de data reutilizáveis
    private static final SimpleDateFormat DATE_FORMAT_LOG = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT_FULL = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT_FILE = new SimpleDateFormat("yyyyMMdd_HHmmss");

    // Componentes da interface
    private JPanel painelPredio;
    private JPanel painelEstatisticas;
    private JPanel painelVisualizacao;
    private JTextArea areaLogEventos;
    private JLabel labelTempo, labelEnergia, labelEspera, labelGeracaoAutomatica;
    private JLabel labelStatus, labelTempoLimite;
    private JProgressBar barraProgresso;
    private JButton botaoIniciarPausar;

    // Variáveis de controle
    private Simulador simulador;
    private Timer timerSimulacao, timerPessoas;
    private Random random = new Random();
    private boolean geracaoAutomatica = false;
    private int intervaloGeracaoPessoas = 10;
    private int quantidadePessoasPorGeracao = 2;
    private boolean simulacaoIniciada = false;

    public SimuladorGI(Simulador simulador) {
        this.simulador = simulador;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Silenciar exceção, interface padrão será usada
        }

        inicializarGUI();
    }

    private void inicializarGUI() {
        configurarJanela();
        criarMenu();
        criarBarraFerramentas();
        criarPaineis();
        criarTimers();
        registrarEvento("Simulação inicializada com " + simulador.getPredio().getNumeroAndares() + " andares e " + simulador.getPredio().getCentralDeControle().getElevadores().tamanho() + " elevadores.", true); setVisible(true);
    }

    private void configurarJanela() {
        setTitle("Simulador Gráfico Interativo de Elevadores");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COR_FUNDO);
        setLayout(new BorderLayout(5, 5));
    }

    private void criarMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(COR_PRIMARIA);
        menuBar.setBorder(BorderFactory.createEmptyBorder());

        // Menu Arquivo
        JMenu menuArquivo = criarMenu("Arquivo", KeyEvent.VK_A);
        adicionarItemMenu(menuArquivo, "Nova Simulação", KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK, e -> voltarParaTelaConfiguracao());
        adicionarItemMenu(menuArquivo, "Salvar Simulação", KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK, e -> salvarSimulacao());
        adicionarItemMenu(menuArquivo, "Carregar Simulação", KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK, e -> carregarSimulacao());
        menuArquivo.addSeparator();
        adicionarItemMenu(menuArquivo, "Sair", KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK, e -> System.exit(0));

        // Menu Simulação
        JMenu menuSimulacao = criarMenu("Simulação", KeyEvent.VK_S);
        adicionarItemMenu(menuSimulacao, "Iniciar/Pausar", KeyEvent.VK_F5, 0, e -> alternarEstadoSimulacao());
        adicionarItemMenu(menuSimulacao, "Encerrar Simulação", KeyEvent.VK_F6, 0, e -> encerrarSimulacao());
        menuSimulacao.addSeparator();
        adicionarItemMenu(menuSimulacao, "Gerar Relatório", KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK, e -> gerarRelatorio());

        // Menu Configurações
        JMenu menuConfiguracoes = criarMenu("Configurações", KeyEvent.VK_C);
        adicionarItemMenu(menuConfiguracoes, "Velocidade de Simulação", -1, 0, e -> configurarVelocidade());

        menuBar.add(menuArquivo);
        menuBar.add(menuSimulacao);
        menuBar.add(menuConfiguracoes);

        setJMenuBar(menuBar);
    }

    private JMenu criarMenu(String titulo, int mnemonic) {
        JMenu menu = new JMenu(titulo);
        menu.setMnemonic(mnemonic);
        menu.setForeground(Color.BLACK);
        menu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return menu;
    }

    private void adicionarItemMenu(JMenu menu, String titulo, int tecla, int modificador, ActionListener acao) {
        JMenuItem item = new JMenuItem(titulo);
        item.setFont(FONTE_NORMAL);
        item.setBackground(Color.BLACK);
        if (tecla != -1) {
            item.setAccelerator(KeyStroke.getKeyStroke(tecla, modificador));
        }
        item.addActionListener(acao);
        menu.add(item);
    }

    private void criarBarraFerramentas() {
        JToolBar barraFerramentas = new JToolBar();
        barraFerramentas.setFloatable(false);
        barraFerramentas.setBackground(COR_PRIMARIA);
        barraFerramentas.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Botões principais
        botaoIniciarPausar = criarBotao("Iniciar", e -> alternarEstadoSimulacao());
        barraFerramentas.add(botaoIniciarPausar);
        barraFerramentas.addSeparator(new Dimension(10, 0));

        barraFerramentas.add(criarBotao("Encerrar", e -> encerrarSimulacao()));
        barraFerramentas.addSeparator(new Dimension(20, 0));
        barraFerramentas.add(criarBotao("+ Pessoas", e -> adicionarPessoasManualmente()));

        // Espaço flexível
        barraFerramentas.add(Box.createHorizontalGlue());
        barraFerramentas.add(criarBotao("Relatório", e -> gerarRelatorio()));

        add(barraFerramentas, BorderLayout.NORTH);
    }

    private JButton criarBotao(String texto, ActionListener acao) {
        JButton botao = new JButton(texto);
        botao.setFont(FONTE_NORMAL);
        botao.setFocusPainted(false);
        botao.addActionListener(acao);
        return botao;
    }

    private void criarPaineis() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBackground(COR_FUNDO);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel dividido - simulação à esquerda, informações à direita
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(850);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        // Painel de visualização
        painelVisualizacao = new JPanel(new BorderLayout(5, 5));
        painelVisualizacao.setBackground(COR_FUNDO);
        painelVisualizacao.setBorder(BorderFactory.createEmptyBorder());

        painelPredio = new JPanel(new GridBagLayout());
        painelPredio.setBackground(COR_FUNDO);
        painelPredio.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(painelPredio);
        scrollPane.setBorder(criarBordaTitulada("Visualização do Prédio", COR_PRIMARIA));
        painelVisualizacao.add(scrollPane, BorderLayout.CENTER);

        // Painel de informações
        JPanel painelInfo = new JPanel(new BorderLayout(5, 5));
        painelInfo.setBackground(COR_FUNDO);
        painelEstatisticas = criarPainelEstatisticas();
        JPanel painelLog = criarPainelLog();
        painelInfo.add(painelEstatisticas, BorderLayout.NORTH);
        painelInfo.add(painelLog, BorderLayout.CENTER);

        splitPane.setLeftComponent(painelVisualizacao);
        splitPane.setRightComponent(painelInfo);

        painelPrincipal.add(splitPane, BorderLayout.CENTER);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private javax.swing.border.Border criarBordaTitulada(String titulo, Color cor) {
        return BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder(cor),
                        titulo,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        FONTE_TITULO,
                        cor
                ),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );
    }

    private JPanel criarPainelEstatisticas() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(criarBordaTitulada("Estatísticas da Simulação", COR_PRIMARIA));

        // Status da simulação
        JPanel painelStatus = criarPainelFluxo(FlowLayout.LEFT);
        labelStatus = criarLabel("Status: Parado", FONTE_SUBTITULO, COR_PRIMARIA);
        painelStatus.add(labelStatus);
        painel.add(painelStatus);

        // Informações de tempo
        JPanel painelTempo = criarPainelFluxo(FlowLayout.LEFT);
        labelTempo = criarLabel("Tempo: 0 min", FONTE_NORMAL, null);
        painelTempo.add(labelTempo);
        painel.add(painelTempo);

        // Barra de progresso
        JPanel painelBarra = new JPanel(new BorderLayout(5, 0));
        painelBarra.setBackground(COR_FUNDO);
        labelTempoLimite = criarLabel("Limite: 0/0 min", FONTE_PEQUENA, null);
        painelBarra.add(labelTempoLimite, BorderLayout.EAST);

        barraProgresso = new JProgressBar(0, 100);
        barraProgresso.setStringPainted(true);
        barraProgresso.setString("0%");
        barraProgresso.setFont(FONTE_PEQUENA);
        painelBarra.add(barraProgresso, BorderLayout.CENTER);
        painel.add(painelBarra);

        painel.add(Box.createVerticalStrut(10));

        // Informações adicionais
        JPanel painelInfos = new JPanel(new GridBagLayout());
        painelInfos.setBackground(COR_FUNDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        labelEnergia = criarLabel("Energia: 0", FONTE_NORMAL, null);
        painelInfos.add(labelEnergia, gbc);

        gbc.gridy = 1;
        labelEspera = criarLabel("Pessoas esperando: 0", FONTE_NORMAL, null);
        painelInfos.add(labelEspera, gbc);

        gbc.gridy = 2;
        labelGeracaoAutomatica = criarLabel("Geração automática: Desativada", FONTE_NORMAL, null);
        painelInfos.add(labelGeracaoAutomatica, gbc);

        painel.add(painelInfos);

        return painel;
    }

    private JPanel criarPainelFluxo(int alinhamento) {
        JPanel painel = new JPanel(new FlowLayout(alinhamento));
        painel.setBackground(COR_FUNDO);
        return painel;
    }

    private JLabel criarLabel(String texto, Font fonte, Color cor) {
        JLabel label = new JLabel(texto);
        label.setFont(fonte);
        if (cor != null) {
            label.setForeground(cor);
        }
        return label;
    }

    private JPanel criarPainelLog() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(criarBordaTitulada("Log de Eventos", COR_PRIMARIA));

        areaLogEventos = new JTextArea();
        areaLogEventos.setEditable(false);
        areaLogEventos.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaLogEventos.setLineWrap(true);
        areaLogEventos.setWrapStyleWord(true);
        areaLogEventos.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(areaLogEventos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        painel.add(scrollPane, BorderLayout.CENTER);

        // Botões do painel de log
        JPanel painelBotoesLog = criarPainelFluxo(FlowLayout.LEFT);
        painelBotoesLog.add(criarBotao("Limpar Log", e -> areaLogEventos.setText("")));
        painelBotoesLog.add(criarBotao("Salvar Log", e -> salvarLogEventos()));
        painel.add(painelBotoesLog, BorderLayout.SOUTH);

        return painel;
    }

    private void criarTimers() {
        timerSimulacao = new Timer(1000, e -> {
            simulador.executarCiclo();
            atualizarVisualizacao();
            registrarEvento("Ciclo executado - Tempo: " + simulador.getTempoSimulado() + " min", false);
        });

        timerPessoas = new Timer(1000, e -> {
            if (simulador.getTempoSimulado() % intervaloGeracaoPessoas == 0) {
                adicionarPessoasAleatorias(quantidadePessoasPorGeracao);
                registrarEvento("Pessoas geradas automaticamente: " + quantidadePessoasPorGeracao, false);
            }
        });
    }

    private void atualizarVisualizacao() {
        // Obter dados atuais
        int tempoAtual = simulador.getTempoSimulado();
        int tempoLimite = simulador.getTempoLimiteSimulacao();
        int totalPessoasEsperando = contarPessoasEsperando();

        // Atualizar estatísticas
        labelTempo.setText("Tempo: " + tempoAtual + " min");
        labelEnergia.setText("Energia: " + simulador.getPredio().getCentralDeControle().getConsumoEnergiaTotal());
        labelEspera.setText("Pessoas esperando: " + totalPessoasEsperando);

        // Atualizar progresso
        int percentual = (int) (((double) tempoAtual / tempoLimite) * 100);
        int tempoRestante = tempoLimite - tempoAtual;

        labelTempoLimite.setText("Limite: " + tempoAtual + "/" + tempoLimite + " min");
        barraProgresso.setValue(percentual);
        barraProgresso.setString(percentual + "%");

        // Configurar cor da barra baseado no tempo restante
        double percentualRestante = (double) tempoRestante / tempoLimite;
        if (percentualRestante <= 0.1) {
            barraProgresso.setForeground(COR_ALERTA);
        } else if (percentualRestante <= 0.25) {
            barraProgresso.setForeground(Color.ORANGE);
        } else {
            barraProgresso.setForeground(COR_DESTAQUE);
        }

        // Verificar se atingiu o limite de tempo
        if (tempoAtual >= tempoLimite) {
            finalizarSimulacaoPorTempoLimite();
        }

        // Atualizar visualização gráfica
        atualizarPainelPredio();
    }

    private int contarPessoasEsperando() {
        int total = 0;
        for (int i = 0; i < simulador.getPredio().getNumeroAndares(); i++) {
            total += simulador.getPredio().getAndar(i).getNumPessoasEsperando();
        }
        return total;
    }

    private void finalizarSimulacaoPorTempoLimite() {
        timerSimulacao.stop();
        timerPessoas.stop();
        simulador.pararSimulacao();
        botaoIniciarPausar.setText("Encerrada");
        botaoIniciarPausar.setEnabled(false);
        labelStatus.setText("Status: Encerrado (limite atingido)");
        registrarEvento("Simulação encerrada - Tempo limite atingido: "
                + simulador.getTempoLimiteSimulacao() + " min", false);

        // Oferecer para gerar relatório
        int resposta = JOptionPane.showConfirmDialog(this,
                "A simulação atingiu o tempo limite de " + simulador.getTempoLimiteSimulacao()
                + " minutos. Deseja gerar o relatório?", "Simulação Encerrada", JOptionPane.YES_NO_OPTION);
        if (resposta == JOptionPane.YES_OPTION) {
            gerarRelatorio();
        }
    }

    private void atualizarPainelPredio() {
        // Limpar painel atual
        painelPredio.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);

        int numAndares = simulador.getPredio().getNumeroAndares();
        Lista<Elevador> elevadores = simulador.getPredio().getCentralDeControle().getElevadores();

        // Adicionar cabeçalho
        adicionarCabecalhoPredio(gbc, elevadores.tamanho());

        // Adicionar cada andar (da parte superior para a inferior)
        for (int i = numAndares - 1; i >= 0; i--) {
            gbc.gridy = numAndares - i;
            gbc.gridx = 0;

            // Número do andar
            String nomeAndar = i == 0 ? "Térreo" : "Andar " + i;
            adicionarCelulaAndar(gbc, nomeAndar);

            // Situação dos elevadores nesse andar
            for (int j = 0; j < elevadores.tamanho(); j++) {
                gbc.gridx = j + 1;
                painelPredio.add(criarPainelElevador(elevadores.obter(j), i), gbc);
            }

            // Fila de pessoas
            gbc.gridx = elevadores.tamanho() + 1;
            painelPredio.add(criarPainelPessoas(simulador.getPredio().getAndar(i), i), gbc);
        }

        painelPredio.revalidate();
        painelPredio.repaint();
    }

    private void adicionarCabecalhoPredio(GridBagConstraints gbc, int numElevadores) {
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Cabeçalho "Andar"
        JLabel labelCabecalho = criarLabelCabecalho("Andar", 80);
        painelPredio.add(labelCabecalho, gbc);

        // Cabeçalhos dos elevadores
        for (int i = 0; i < numElevadores; i++) {
            gbc.gridx = i + 1;
            JLabel labelElevador = criarLabelCabecalho("E" + (i + 1), 60);
            painelPredio.add(labelElevador, gbc);
        }

        // Cabeçalho "Fila"
        gbc.gridx = numElevadores + 1;
        JLabel labelFila = criarLabelCabecalho("Fila", 200);
        painelPredio.add(labelFila, gbc);
    }

    private JLabel criarLabelCabecalho(String texto, int largura) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(FONTE_SUBTITULO);
        label.setOpaque(true);
        label.setBackground(COR_PRIMARIA);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        label.setPreferredSize(new Dimension(largura, 30));
        return label;
    }

    private void adicionarCelulaAndar(GridBagConstraints gbc, String texto) {
        JLabel labelAndar = new JLabel(texto, SwingConstants.CENTER);
        labelAndar.setFont(FONTE_NORMAL);
        labelAndar.setOpaque(true);
        labelAndar.setBackground(COR_SECUNDARIA);
        labelAndar.setForeground(Color.DARK_GRAY);
        labelAndar.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        labelAndar.setPreferredSize(new Dimension(80, 40));
        painelPredio.add(labelAndar, gbc);
    }

    private JPanel criarPainelElevador(Elevador elevador, int numeroAndar) {
        JPanel painelElevador = new JPanel(new BorderLayout());
        painelElevador.setPreferredSize(new Dimension(70, 50));
        painelElevador.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        if (elevador.getAndarAtual() == numeroAndar) {
            // Elevador está neste andar
            painelElevador.setBackground(elevador.isEmMovimento()
                    ? COR_ELEVADOR_MOVIMENTO : COR_ELEVADOR_PARADO);

            // ID do elevador
            JLabel labelId = criarLabel("E" + elevador.getId(),
                    new Font("Segoe UI", Font.BOLD, 12), Color.WHITE);
            labelId.setHorizontalAlignment(SwingConstants.CENTER);
            painelElevador.add(labelId, BorderLayout.NORTH);

            // Capacidade
            JLabel labelCapacidade = criarLabel(
                    elevador.getNumPassageiros() + "/" + elevador.getCapacidadeMaxima(),
                    new Font("Segoe UI", Font.BOLD, 12), Color.WHITE);
            labelCapacidade.setHorizontalAlignment(SwingConstants.CENTER);
            painelElevador.add(labelCapacidade, BorderLayout.CENTER);

            // Direção
            String direcao = "";
            switch (elevador.getDirecao()) {
                case SUBINDO:
                    direcao = "↑";
                    break;
                case DESCENDO:
                    direcao = "↓";
                    break;
                default:
                    direcao = "•";
                    break;
            }

            JLabel labelDirecao = criarLabel(direcao, new Font("Dialog", Font.BOLD, 16), Color.WHITE);
            labelDirecao.setHorizontalAlignment(SwingConstants.CENTER);
            painelElevador.add(labelDirecao, BorderLayout.SOUTH);
        } else {
            // Elevador não está neste andar - mostrar apenas o poço
            painelElevador.setBackground(new Color(240, 240, 240));
            JLabel labelPoço = criarLabel("│", new Font("Dialog", Font.PLAIN, 14), Color.GRAY);
            labelPoço.setHorizontalAlignment(SwingConstants.CENTER);
            painelElevador.add(labelPoço, BorderLayout.CENTER);
        }

        return painelElevador;
    }

    private JPanel criarPainelPessoas(Andar andar, int numeroAndar) {
        JPanel painelPessoas = new JPanel(new BorderLayout());
        painelPessoas.setPreferredSize(new Dimension(200, 40));
        painelPessoas.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        painelPessoas.setBackground(new Color(250, 250, 250));

        if (andar.temPessoasEsperando()) {
            TipoPainel tipoPainel = andar.getPainelChamadas().getTipoPainel();
            StringBuilder pessoasStr = new StringBuilder();
            Lista<Pessoa> pessoas = andar.getPessoasEsperando();

            for (int i = 0; i < pessoas.tamanho(); i++) {
                Pessoa pessoa = pessoas.obter(i);

                // Símbolo da pessoa (cadeirante, idoso ou normal)
                pessoasStr.append(pessoa.isCadeirante() ? "◊" : (pessoa.isIdoso() ? "♦" : "•"));

                // Indicador de destino/direção
                if (tipoPainel == TipoPainel.PAINEL_NUMERICO) {
                    pessoasStr.append("→").append(pessoa.getAndarDestino());
                } else if (tipoPainel == TipoPainel.DOIS_BOTOES) {
                    pessoasStr.append(pessoa.getAndarDestino() > numeroAndar ? "↑" : "↓");
                }

                if (i < pessoas.tamanho() - 1) {
                    pessoasStr.append(" ");
                }
            }

            // Símbolos das pessoas
            JLabel labelPessoas = criarLabel(pessoasStr.toString(),
                    new Font("Segoe UI", Font.BOLD, 12), new Color(30, 30, 30));
            labelPessoas.setHorizontalAlignment(SwingConstants.CENTER);

            // Contagem total
            JLabel labelContagem = criarLabel(" (" + pessoas.tamanho() + ")",
                    FONTE_PEQUENA, Color.DARK_GRAY);
            labelContagem.setHorizontalAlignment(SwingConstants.RIGHT);

            painelPessoas.add(labelPessoas, BorderLayout.CENTER);
            painelPessoas.add(labelContagem, BorderLayout.EAST);
        } else {
            // Andar vazio
            JLabel labelVazio = criarLabel("Vazio", FONTE_NORMAL, Color.GRAY);
            labelVazio.setHorizontalAlignment(SwingConstants.CENTER);
            painelPessoas.add(labelVazio, BorderLayout.CENTER);
        }

        return painelPessoas;
    }

    public void adicionarPessoasAleatorias(int quantidade) {
        int numeroAndares = simulador.getPredio().getNumeroAndares();

        for (int i = 0; i < quantidade; i++) {
            int andarOrigem = random.nextInt(numeroAndares);
            int andarDestino;

            // Encontrar um andar de destino diferente da origem
            do {
                andarDestino = random.nextInt(numeroAndares);
            } while (andarDestino == andarOrigem);

            // Algumas pessoas são especiais
            boolean cadeirante = random.nextDouble() < 0.1;  // 10% de chance
            boolean idoso = random.nextDouble() < 0.2;       // 20% de chance

            simulador.adicionarPessoa(andarOrigem, andarDestino, cadeirante, idoso);
        }

        atualizarVisualizacao();
    }

    private void adicionarPessoasManualmente() {
        String input = JOptionPane.showInputDialog(this,
                "Digite a quantidade de pessoas para adicionar:",
                "Adicionar Pessoas", JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                int quantidade = Integer.parseInt(input.trim());
                if (quantidade > 0) {
                    adicionarPessoasAleatorias(quantidade);
                    registrarEvento("Adicionadas " + quantidade + " pessoas manualmente", false);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, insira um número válido.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void alternarEstadoSimulacao() {
        // Verificar se já atingiu o tempo limite
        if (simulador.getTempoSimulado() >= simulador.getTempoLimiteSimulacao()) {
            JOptionPane.showMessageDialog(this,
                    "A simulação já atingiu o tempo limite. Por favor, encerre e inicie uma nova simulação.",
                    "Simulação Finalizada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!simulacaoIniciada) {
            iniciarSimulacao();
            simulacaoIniciada = true;
            botaoIniciarPausar.setText("Pausar");
        } else if (simulador.isSimulacaoAtiva()) {
            pausarSimulacao();
            botaoIniciarPausar.setText("Retomar");
        } else {
            iniciarSimulacao();
            botaoIniciarPausar.setText("Pausar");
        }
    }

    private void iniciarSimulacao() {
        if (!simulador.isSimulacaoAtiva()) {
            simulador.iniciarSimulacao();
            timerSimulacao.start();
            if (geracaoAutomatica) {
                timerPessoas.start();
            }
            labelStatus.setText("Status: Em execução");
            registrarEvento("Simulação iniciada", false);
        }
    }

    private void pausarSimulacao() {
        if (simulador.isSimulacaoAtiva()) {
            simulador.pararSimulacao();
            timerSimulacao.stop();
            timerPessoas.stop();
            labelStatus.setText("Status: Pausado");
            registrarEvento("Simulação pausada", false);
        }
    }

    private void encerrarSimulacao() {
        timerSimulacao.stop();
        timerPessoas.stop();

        try {
            simulador.pararSimulacao();

            // Recria simulador com mesmas configurações
            Predio predio = simulador.getPredio();
            Elevador primeiroElevador = predio.getCentralDeControle().getElevadores().obter(0);
            int tempoLimiteAtual = simulador.getTempoLimiteSimulacao();

            simulador = new Simulador(
                    predio.getNumeroAndares(),
                    predio.getCentralDeControle().getElevadores().tamanho(),
                    primeiroElevador.getCapacidadeMaxima(),
                    predio.getTipoPainel(),
                    2, 4, 8, 3, // valores padrão
                    simulador.getModeloHeuristica(),
                    30,
                    tempoLimiteAtual // Mantém o mesmo tempo limite
            );

            simulacaoIniciada = false;
            botaoIniciarPausar.setText("Iniciar");
            botaoIniciarPausar.setEnabled(true);

            registrarEvento("Simulação encerrada e reiniciada", false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao reiniciar a simulação: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }

        atualizarVisualizacao();
        labelStatus.setText("Status: Encerrado");
    }

    public void setVelocidadeSimulacao(int velocidadeMs) {
        timerSimulacao.setDelay(velocidadeMs);
        timerPessoas.setDelay(velocidadeMs);
        registrarEvento("Velocidade da simulação alterada para " + velocidadeMs + "ms", false);
    }

    private void configurarVelocidade() {
        String[] opcoes = {"Lenta (2s)", "Normal (1s)", "Rápida (0.5s)", "Muito rápida (0.1s)"};
        int escolha = JOptionPane.showOptionDialog(this,
                "Selecione a velocidade de simulação:", "Velocidade de Simulação",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opcoes, opcoes[1]);

        if (escolha >= 0) {
            int[] delays = {2000, 1000, 500, 100};
            setVelocidadeSimulacao(delays[escolha]);
        }
    }

    public void configurarGeracaoAutomatica(boolean ativar, int intervalo, int quantidade) {
        geracaoAutomatica = ativar;
        intervaloGeracaoPessoas = intervalo;
        quantidadePessoasPorGeracao = quantidade;
        labelGeracaoAutomatica.setText("Geração automática: "
                + (geracaoAutomatica ? "Ativada" : "Desativada"));

        if (geracaoAutomatica && simulador.isSimulacaoAtiva()) {
            timerPessoas.start();
        } else {
            timerPessoas.stop();
        }

        registrarEvento("Geração automática " + (geracaoAutomatica ? "ativada" : "desativada")
                + " (intervalo: " + intervalo + ", quantidade: " + quantidade + ")", false);
    }

    private void registrarEvento(String evento, boolean isPrimeiro) {
        String timestamp = DATE_FORMAT_LOG.format(new Date());
        String logEntry;

        if (isPrimeiro) {
            String dataCompleta = DATE_FORMAT_FULL.format(new Date());
            logEntry = "=== SIMULAÇÃO INICIADA EM " + dataCompleta + " ===\n";
            logEntry += "[" + timestamp + "] " + evento + "\n";
            areaLogEventos.setText(logEntry);
        } else {
            logEntry = "[" + timestamp + "] " + evento + "\n";
            areaLogEventos.append(logEntry);
        }

        // Rolar para o final
        areaLogEventos.setCaretPosition(areaLogEventos.getDocument().getLength());
    }

    private void salvarLogEventos() {
        JFileChooser fileChooser = criarSeletorArquivo(
                "Salvar Log de Eventos", "txt", "Arquivos de Texto (*.txt)",
                "log_simulacao_" + DATE_FORMAT_FILE.format(new Date()) + ".txt");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = obterArquivoComExtensao(fileChooser.getSelectedFile(), ".txt");

            try (FileOutputStream fos = new FileOutputStream(arquivo)) {
                fos.write(areaLogEventos.getText().getBytes());
                JOptionPane.showMessageDialog(this,
                        "Log salvo com sucesso!",
                        "Salvar Log", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                mostrarErro("Erro ao salvar o log: " + ex.getMessage());
            }
        }
    }

    private JFileChooser criarSeletorArquivo(String titulo, String extensao,
            String descricao, String nomeArquivoPadrao) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(titulo);
        fileChooser.setFileFilter(new FileNameExtensionFilter(descricao, extensao));
        fileChooser.setSelectedFile(new File(nomeArquivoPadrao));
        return fileChooser;
    }

    private File obterArquivoComExtensao(File arquivo, String extensao) {
        if (!arquivo.getAbsolutePath().endsWith(extensao)) {
            return new File(arquivo.getAbsolutePath() + extensao);
        }
        return arquivo;
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void salvarSimulacao() {
        boolean estaAtiva = simulador.isSimulacaoAtiva();
        if (estaAtiva) {
            pausarSimulacao();
        }

        JFileChooser fileChooser = criarSeletorArquivo(
                "Salvar Simulação", "sim", "Arquivos de Simulação (*.sim)",
                "simulacao_" + DATE_FORMAT_FILE.format(new Date()) + ".sim");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = obterArquivoComExtensao(fileChooser.getSelectedFile(), ".sim");

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo))) {
                oos.writeObject(simulador);
                JOptionPane.showMessageDialog(this, "Simulação salva com sucesso!",
                        "Salvar Simulação", JOptionPane.INFORMATION_MESSAGE);
                registrarEvento("Simulação salva em: " + arquivo.getName(), false);
            } catch (IOException ex) {
                mostrarErro("Erro ao salvar: " + ex.getMessage());
            }
        }

        if (estaAtiva) {
            iniciarSimulacao();
        }
    }

    private void carregarSimulacao() {
        pausarSimulacao();

        JFileChooser fileChooser = criarSeletorArquivo(
                "Carregar Simulação", "sim", "Arquivos de Simulação (*.sim)", null);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(fileChooser.getSelectedFile()))) {

                simulador = (Simulador) ois.readObject();
                atualizarVisualizacao();
                labelStatus.setText("Status: Carregado");
                simulacaoIniciada = false;
                botaoIniciarPausar.setText("▶ Iniciar");
                botaoIniciarPausar.setEnabled(true);

                registrarEvento("Simulação carregada de: " + fileChooser.getSelectedFile().getName(), false);

                // Exibe informação sobre o tempo limite
                JOptionPane.showMessageDialog(this,
                        "Simulação carregada com sucesso!\nTempo atual: " + simulador.getTempoSimulado()
                        + " minutos\nTempo limite: " + simulador.getTempoLimiteSimulacao() + " minutos",
                        "Carregar Simulação", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException ex) {
                mostrarErro("Erro ao carregar: " + ex.getMessage());
            }
        }
    }

    private void gerarRelatorio() {
        // Pausa a simulação temporariamente
        boolean estaAtiva = simulador.isSimulacaoAtiva();
        if (estaAtiva) {
            pausarSimulacao();
        }

        try {
            RelatorioSimulacao relatorio = new RelatorioSimulacao(simulador);
            String conteudoRelatorio = relatorio.gerarRelatorio();

            registrarEvento("Relatório gerado", false);

            // Preparar componentes do relatório
            JTextArea areaTexto = new JTextArea(conteudoRelatorio);
            areaTexto.setEditable(false);
            areaTexto.setFont(new Font("Consolas", Font.PLAIN, 12));
            areaTexto.setBackground(new Color(250, 250, 250));
            areaTexto.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JScrollPane scrollPane = new JScrollPane(areaTexto);
            scrollPane.setPreferredSize(new Dimension(800, 600));
            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            // Painel para o diálogo
            JPanel painelRelatorio = new JPanel(new BorderLayout(10, 10));
            painelRelatorio.setBackground(COR_FUNDO);
            painelRelatorio.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Título
            JLabel labelTitulo = criarLabel("Relatório da Simulação de Elevadores",
                    new Font("Segoe UI", Font.BOLD, 18), COR_PRIMARIA);
            labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);
            labelTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

            painelRelatorio.add(labelTitulo, BorderLayout.NORTH);
            painelRelatorio.add(scrollPane, BorderLayout.CENTER);

            // Botões
            JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            painelBotoes.setBackground(COR_FUNDO);

            JButton botaoSalvar = criarBotao("Salvar Relatório", e -> {
                JOptionPane.getRootFrame().dispose();
                salvarRelatorioEmArquivo(relatorio);
            });

            JButton botaoFechar = criarBotao("Fechar", e -> JOptionPane.getRootFrame().dispose());

            painelBotoes.add(botaoSalvar);
            painelBotoes.add(botaoFechar);
            painelRelatorio.add(painelBotoes, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, painelRelatorio,
                    "Relatório da Simulação", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            mostrarErro("Erro ao gerar relatório: " + e.getMessage());
        }

        // Retoma a simulação se estava ativa
        if (estaAtiva) {
            iniciarSimulacao();
        }
    }

    private void salvarRelatorioEmArquivo(RelatorioSimulacao relatorio) {
        JFileChooser fileChooser = criarSeletorArquivo(
                "Salvar Relatório", "txt", "Arquivos de Texto (*.txt)",
                "relatorio_simulacao_" + DATE_FORMAT_FILE.format(new Date()) + ".txt");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = obterArquivoComExtensao(fileChooser.getSelectedFile(), ".txt");

            try {
                relatorio.salvarRelatorio(arquivo.getAbsolutePath());
                JOptionPane.showMessageDialog(this,
                        "Relatório salvo com sucesso!",
                        "Salvar Relatório", JOptionPane.INFORMATION_MESSAGE);
                registrarEvento("Relatório salvo em: " + arquivo.getName(), false);
            } catch (IOException ex) {
                mostrarErro("Erro ao salvar o relatório: " + ex.getMessage());
            }
        }
    }

    private void voltarParaTelaConfiguracao() {
        // Confirmar a volta para a tela de configuração
        if (simulacaoIniciada) {
            int resposta = JOptionPane.showConfirmDialog(this,
                    "A simulação será encerrada. Deseja voltar para a tela de configuração?",
                    "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (resposta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Parar timers e simulação
        if (timerSimulacao.isRunning()) {
            timerSimulacao.stop();
        }
        if (timerPessoas.isRunning()) {
            timerPessoas.stop();
        }

        simulador.pararSimulacao();
        simulacaoIniciada = false;

        // Fechar a janela atual e abrir configuração
        this.dispose();

        try {
            new ConfiguracaoSimulador();
        } catch (Exception e) {
            mostrarErro("Erro ao abrir a tela de configuração: " + e.getMessage());
        }
    }
}
