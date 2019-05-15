package agent.rlagent;

import environnement.Action;
import environnement.Action2D;
import environnement.Environnement;
import environnement.Etat;

import java.util.*;

/**
 * @author laetitiamatignon
 */
public class QLearningAgent extends RLAgent {
    /**
     * format de memorisation des Q valeurs: utiliser partout setQValeur car cette methode notifie la vue
     */
    protected HashMap<Etat, HashMap<Action, Double>> qvaleurs;

    //AU CHOIX: vous pouvez utiliser une Map avec des Pair pour cles
    //protected HashMap<Pair<Etat,Action>,Double> qvaleurs;


    public QLearningAgent(double alpha, double gamma,
                          Environnement _env) {
        super(alpha, gamma, _env);
        qvaleurs = new HashMap<>();


    }


    /**
     * renvoi action(s) de plus forte(s) valeur(s) dans l'etat e
     * (plusieurs actions sont renvoyees si valeurs identiques)
     * renvoi liste vide si aucunes actions possibles dans l'etat (par ex. etat absorbant)
     */
    @Override
    public List<Action> getPolitique(Etat e) {
        List<Action> returnactions = new ArrayList<Action>();
        final List<Action> actionsLegales = this.getActionsLegales(e);

        if (actionsLegales.size() == 0) {//etat  absorbant; impossible de le verifier via environnement
            System.out.println("aucune action legale");
            return new ArrayList<Action>();

        }

        //*** VOTRE CODE
        final Action action = qvaleurs.getOrDefault(e, new HashMap<>())
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(Action2D.NONE);

        returnactions.add(action);

        return returnactions;
    }

    /**
     * renvoi V(e) = max_a Q(e,a)
     */
    @Override
    public double getValeur(Etat e) {
        return qvaleurs.getOrDefault(e, new HashMap<>())
                .values()
                .stream()
                .mapToDouble(v -> v)
                .max()
                .orElse(0);
    }

    /**
     * renvoi Q(e,a), et 0 si les cles n'existent pas
     */
    @Override
    public double getQValeur(Etat e, Action a) {
        return qvaleurs.getOrDefault(e, new HashMap<>())
                .getOrDefault(a, 0d);
    }


    /**
     * juste un setter: met la Q valeur du couple (e,a) a d
     * la valeur d du couple est calcule dans endStep
     */
    @Override
    public void setQValeur(Etat e, Action a, double d) {
        qvaleurs.computeIfAbsent(e, k -> new HashMap<>());
        qvaleurs.get(e).put(a, d);

        //mise a jour de vmax et vmin (attributs de la classe mere)
        //vmax et vmin sont utilises pour l'affichage du gradient de couleur:
        //vmax est la valeur max de V pour tout s
        //vmin est la valeur min de V pour tout s

        vmax = qvaleurs.values()
                .stream()
                .mapToDouble(m -> m.values().stream().mapToDouble(v -> v).max().orElse(0))
                .max()
                .orElse(0);

        vmin = qvaleurs.values()
                .stream()
                .mapToDouble(m -> m.values().stream().mapToDouble(v -> v).min().orElse(0))
                .min()
                .orElse(0);

        this.notifyObs();
    }


    /**
     * mise a jour du couple etat-valeur (e,a) apres chaque interaction <etat e,action a, etatsuivant esuivant, recompense reward>
     * la mise a jour s'effectue lorsque l'agent est notifie par l'environnement apres avoir realise une action.
     *
     * @param e
     * @param a
     * @param esuivant
     * @param reward
     */
    @Override
    public void endStep(Etat e, Action a, Etat esuivant, double reward) {
        if (RLAgent.DISPRL)
            System.out.println("QL mise a jour etat " + e + " action " + a + " etat' " + esuivant + " r " + reward);

        final double newD = (1 - alpha) * getQValeur(e, a) + alpha * (reward + gamma * getValeur(esuivant));
        setQValeur(e, a, newD);
    }

    @Override
    public Action getAction(Etat e) {
        this.actionChoisie = this.stratExplorationCourante.getAction(e);
        return this.actionChoisie;
    }

    @Override
    public void reset() {
        super.reset();
        this.qvaleurs.clear();

        this.episodeNb = 0;
        this.notifyObs();
    }


}
