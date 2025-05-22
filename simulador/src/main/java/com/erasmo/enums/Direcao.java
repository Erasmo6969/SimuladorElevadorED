package com.erasmo.enums;

public enum Direcao {
    SUBINDO,
    DESCENDO,
    PARADO;

    public Direcao oposto() {
        switch (this) {
            case SUBINDO:
                return DESCENDO;
            case DESCENDO:
                return SUBINDO;
            default:
                return PARADO;
        }
    }

    public static Direcao obterDirecaoPara(int andarAtual, int andarDestino) {
        if (andarDestino > andarAtual) {
            return SUBINDO;
        } else if (andarDestino < andarAtual) {
            return DESCENDO;
        } else {
            return PARADO;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case SUBINDO:
                return "↑";
            case DESCENDO:
                return "↓";
            default:
                return "•";
        }
    }
}
