package com.erasmo.controls;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.erasmo.enums.ModeloHeuristica;
import com.erasmo.enums.TipoPainel;
import com.erasmo.structure.Pessoa;
import com.erasmo.structure.Predio;

public class Simulador implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Simulador.class.getName());

    private Predio predio;
    private int tempoSimulado;
    private int intervaloVisualizacao;
    private boolean simulacaoAtiva;
    private ModeloHeuristica modeloHeuristica;
    private int tempoLimiteSimulacao;


    // Parâmetros de configuração armazenados para reconfiguração
    private int numeroAndares;
    private int numeroElevadores;
    private int capacidadeElevador;
    private TipoPainel tipoPainel;
    private int tempoDeslocamentoPadrao;
    private int tempoDeslocamentoPico;
    private int consumoEnergiaPorDeslocamento;
    private int consumoEnergiaPorParada;
    private int tempoMaximoEspera;

    /**
     * Construtor que inicializa o simulador com os parâmetros fornecidos
     */
    public Simulador(int numeroAndares, int numeroElevadores, int capacidadeElevador,
            TipoPainel tipoPainel, int tempoDeslocamentoPadrao, int tempoDeslocamentoPico,
            int consumoEnergiaPorDeslocamento, int consumoEnergiaPorParada,
            ModeloHeuristica modeloHeuristica, int tempoMaximoEspera, int tempoLimiteSimulacao) {

        // Armazenar parâmetros para reconfiguração
        this.numeroAndares = numeroAndares;
        this.numeroElevadores = numeroElevadores;
        this.capacidadeElevador = capacidadeElevador;
        this.tipoPainel = tipoPainel;
        this.tempoDeslocamentoPadrao = tempoDeslocamentoPadrao;
        this.tempoDeslocamentoPico = tempoDeslocamentoPico;
        this.consumoEnergiaPorDeslocamento = consumoEnergiaPorDeslocamento;
        this.consumoEnergiaPorParada = consumoEnergiaPorParada;
        this.tempoMaximoEspera = tempoMaximoEspera;
        this.modeloHeuristica = modeloHeuristica;
        this.tempoLimiteSimulacao = tempoLimiteSimulacao;
        // Inicializar o prédio
        inicializarPredio();

        this.tempoSimulado = 0;
        this.intervaloVisualizacao = 1;
        this.simulacaoAtiva = false;
    }

    /**
     * Método separado para inicializar o prédio (facilita reinicialização)
     */
    private void inicializarPredio() {
        this.predio = new Predio(numeroAndares, numeroElevadores, capacidadeElevador,
                tipoPainel, tempoDeslocamentoPadrao, tempoDeslocamentoPico,
                consumoEnergiaPorDeslocamento, consumoEnergiaPorParada,
                modeloHeuristica, tempoMaximoEspera);
    }

    /**
     * Reconfigurar a simulação completa com novos parâmetros
     */
    public void reconfigurar(int numeroAndares, int numeroElevadores, int capacidadeElevador,
            TipoPainel tipoPainel, int tempoDeslocamentoPadrao, int tempoDeslocamentoPico,
            int consumoEnergiaPorDeslocamento, int consumoEnergiaPorParada,
            ModeloHeuristica modeloHeuristica, int tempoMaximoEspera, int tempoLimiteSimulacao) {

        // Garantir que a simulação esteja parada
        pararSimulacao();

        // Atualizar parâmetros
        this.numeroAndares = numeroAndares;
        this.numeroElevadores = numeroElevadores;
        this.capacidadeElevador = capacidadeElevador;
        this.tipoPainel = tipoPainel;
        this.tempoDeslocamentoPadrao = tempoDeslocamentoPadrao;
        this.tempoDeslocamentoPico = tempoDeslocamentoPico;
        this.consumoEnergiaPorDeslocamento = consumoEnergiaPorDeslocamento;
        this.consumoEnergiaPorParada = consumoEnergiaPorParada;
        this.modeloHeuristica = modeloHeuristica;
        this.tempoMaximoEspera = tempoMaximoEspera;
        this.tempoLimiteSimulacao = tempoLimiteSimulacao;

        // Resetar o tempo simulado
        this.tempoSimulado = 0;

        // Reinicializar o prédio
        inicializarPredio();

        // Limpar quaisquer referências antigas
        System.gc();
    }

    /**
     * Reiniciar a simulação com os mesmos parâmetros
     */
    public void reiniciar() {
        pararSimulacao();
        this.tempoSimulado = 0;
        inicializarPredio();
        LOGGER.log(Level.INFO, "Simulação reiniciada");
    }

    public void iniciarSimulacao() {
        simulacaoAtiva = true;
        LOGGER.log(Level.INFO, "Simulação iniciada! Modelo de Heurística: {0}, Tempo limite: {1} minutos",
                new Object[]{modeloHeuristica, tempoLimiteSimulacao});
        predio.exibirEstado();
    }

    public void pararSimulacao() {
        simulacaoAtiva = false;
        LOGGER.log(Level.INFO, "Simulação parada no minuto {0}", tempoSimulado);
    }

    public void executarCiclo() {
        if (!simulacaoAtiva) {
            return;
        }

        tempoSimulado++;
        predio.atualizar(tempoSimulado);


        // Sempre verifica o tempo limite
        if (tempoSimulado >= tempoLimiteSimulacao) {
            pararSimulacao();
            LOGGER.log(Level.INFO, "Simulação encerrada - Tempo limite atingido ({0} minutos)", tempoLimiteSimulacao);
        }
    }

    public void executarCiclos(int ciclos) {
        for (int i = 0; i < ciclos; i++) {
            executarCiclo();
            // Se a simulação foi parada (por tempo limite), interrompe o loop
            if (!simulacaoAtiva) {
                break;
            }
        }
    }

    public Pessoa adicionarPessoa(int andarOrigem, int andarDestino, boolean cadeirante, boolean idoso) {
        return predio.adicionarPessoa(andarOrigem, andarDestino, cadeirante, idoso, tempoSimulado);
    }

    // Getters
    public int getTempoSimulado() {
        return tempoSimulado;
    }

    public Predio getPredio() {
        return predio;
    }

    public boolean isSimulacaoAtiva() {
        return simulacaoAtiva;
    }

    public ModeloHeuristica getModeloHeuristica() {
        return modeloHeuristica;
    }

    public int getTempoLimiteSimulacao() {
        return tempoLimiteSimulacao;
    }

    // Getters para os parâmetros de configuração
    public int getNumeroAndares() {
        return numeroAndares;
    }

    public int getNumeroElevadores() {
        return numeroElevadores;
    }

    public int getCapacidadeElevador() {
        return capacidadeElevador;
    }

    public TipoPainel getTipoPainel() {
        return tipoPainel;
    }

    public int getTempoDeslocamentoPadrao() {
        return tempoDeslocamentoPadrao;
    }

    public int getTempoDeslocamentoPico() {
        return tempoDeslocamentoPico;
    }

    public int getConsumoEnergiaPorDeslocamento() {
        return consumoEnergiaPorDeslocamento;
    }

    public int getConsumoEnergiaPorParada() {
        return consumoEnergiaPorParada;
    }

    public int getTempoMaximoEspera() {
        return tempoMaximoEspera;
    }

    // Setters
    public void setIntervaloVisualizacao(int intervalo) {
        if (intervalo > 0) {
            this.intervaloVisualizacao = intervalo;
        }
    }

    public void setTempoLimiteSimulacao(int tempoLimite) {
        if (tempoLimite > 0) { // Garante que seja sempre positivo
            this.tempoLimiteSimulacao = tempoLimite;
        }
    }
}
