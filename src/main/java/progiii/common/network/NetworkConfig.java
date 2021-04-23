package progiii.common.network;

/**
 *
 * Raccoglie le configurazioni per la comunicazione di rete (IP, porta e pull period)
 * NB il pull period è basso per (5 secondi) per motivi didattici
 * Una implementazione reale dovrebbe avere un delay maggiore (gmail aggiorna automaticamente i client ogni 15 minuti)
 * Questo eviterebbe un sovraccarico di richieste dai client
 */
public class NetworkConfig {
    public static final String REMOTE_IP = "localhost";
    public static final int SERVER_PORT = 34198;
    public static final int PULL_PERIOD = 5;
}
