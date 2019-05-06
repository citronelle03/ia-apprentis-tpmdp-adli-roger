package agent.planningagent;

import agent.ValueAgent;
import environnement.Action;
import environnement.Action2D;
import environnement.Etat;
import environnement.MDP;

import java.util.*;


/**
 * Cet agent met a jour sa fonction de valeur avec value iteration
 * et choisit ses actions selon la politique calculee.
 *
 * @author laetitiamatignon
 */
public class ValueIterationAgent extends PlanningValueAgent {
    /**
     * discount facteur
     */
    protected double gamma;

    /**
     * fonction de valeur des etats
     */
    protected HashMap<Etat, Double> V;

    /**
     * @param gamma
     * @param mdp
     */
    public ValueIterationAgent(double gamma, MDP mdp) {
        super(mdp);
        this.gamma = gamma;

        this.V = new HashMap<Etat, Double>();
        for (Etat etat : this.mdp.getEtatsAccessibles()) {
            V.put(etat, 0.0);
        }
    }


    public ValueIterationAgent(MDP mdp) {
        this(0.9, mdp);

    }

    /**
     * Mise a jour de V: effectue UNE iteration de value iteration (calcule V_k(s) en fonction de V_{k-1}(s'))
     * et notifie ses observateurs.
     * Ce n'est pas la version inplace (qui utilise la nouvelle valeur de V pour mettre a jour ...)
     */
    @Override
    public void updateV() {
        //delta est utilise dans la classe mere pour detecter la convergence de l'algorithme
        //Dans la classe mere, lorsque l'on planifie jusqu'a convergence, on arrete les iterations
        //lorsque delta < epsilon  (la valeur d'epsilon est choisi dans la classe mere)

        final HashMap<Etat, Double> Vavant = (HashMap<Etat, Double>) V.clone();

        //*** VOTRE CODE


        for (final Etat s : mdp.getEtatsAccessibles()) {
            final double newV = calculerNouvelleValeurDeV(s, Vavant);
            V.put(s, newV);
        }

        vmax = V.values().stream().mapToDouble(v -> v).max().getAsDouble();
        vmin = V.values().stream().mapToDouble(v -> v).min().getAsDouble();

        //mise a jour de vmax et vmin (attributs de la classe mere)
        //vmax et vmin sont utilises pour l'affichage du gradient de couleur:
        //vmax est la valeur max de V pour tout s
        //vmin est la valeur min de V pour tout s

        final ArrayList<Double> deltas = new ArrayList<>();

        for (final Map.Entry<Etat, Double> v : V.entrySet()) {
            final Etat s = v.getKey();

            deltas.add(Math.abs(Vavant.get(s) - V.get(s)));
        }

        this.delta = deltas.stream().mapToDouble(s -> s).max().getAsDouble();

        //******************* laisser cette notification a la fin de la methode
        this.notifyObs();
    }

    private double calculerNouvelleValeurDeV(final Etat s, final HashMap<Etat, Double> Vavant) {
        final List<Action> actionsPossibles = mdp.getActionsPossibles(s);
        return actionsPossibles.stream()
                .mapToDouble(a -> sumProbasEtatTransition(s, Vavant, a))
                .max()
                .orElse(0);
    }

    private Action calculerNouvelleValeurDeVArgMax(final Etat s, final HashMap<Etat, Double> Vavant) {
        final List<Action> actionsPossibles = mdp.getActionsPossibles(s);
        return actionsPossibles.stream()
                .max(Comparator.comparingDouble(a -> sumProbasEtatTransition(s, Vavant, a)))
                .orElse(Action2D.NONE);
    }

    private double sumProbasEtatTransition(final Etat s, final HashMap<Etat, Double> Vavant, final Action a1) {
        try {
            final Map<Etat, Double> probas = mdp.getEtatTransitionProba(s, a1);
            return probas.entrySet().stream()
                    .mapToDouble(p -> {
                        final Etat sp = p.getKey();
                        final Double t = p.getValue();
                        final double r = mdp.getRecompense(s, a1, sp);
                        final Double V_kMoins1 = Vavant.get(sp);

                        return t * (r + gamma * V_kMoins1);
                    })
                    .sum();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * renvoi l'action executee par l'agent dans l'etat e
     * Si aucune action n'est possible, renvoi Action2D.NONE
     */
    @Override
    public Action getAction(Etat e) {
        //*** VOTRE CODE

        return getPolitique(e).get(0);

    }

    /**
     * Renvoie la valeur de l'Etat _e
     * Cette methode est juste un getter, on ne calcule pas la valeur de l'etat _e ici
     * la valeur d'un etat est calculee dans updateV
     */
    @Override
    public double getValeur(Etat _e) {

        return V.getOrDefault(_e, 0.0);
    }

    /**
     * renvoi la (les) action(s) de valeur(s) max dans l'etat _e
     * (plusieurs actions sont renvoyees si valeurs identiques, liste vide si aucune action n'est possible)
     */
    @Override
    public List<Action> getPolitique(Etat _e) {
        //*** VOTRE CODE

        return Collections.singletonList(calculerNouvelleValeurDeVArgMax(_e, V));
    }

    @Override
    public void reset() {
        super.reset();

        for (Etat etat : this.mdp.getEtatsAccessibles()) {
            V.put(etat, 0.0);
        }

        this.notifyObs();
    }


    public HashMap<Etat, Double> getV() {
        return V;
    }

    public double getGamma() {
        return gamma;
    }

    @Override
    public void setGamma(double _g) {
        System.out.println("gamma= " + gamma);
        this.gamma = _g;
    }


}