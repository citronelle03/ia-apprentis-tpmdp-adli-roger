package agent.rlapproxagent;


import agent.rlagent.QLearningAgent;
import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Environnement;
import environnement.Etat;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent qui apprend avec QLearning en utilisant approximation de la Q-valeur :
 * approximation lineaire de fonctions caracteristiques
 *
 * @author laetitiamatignon
 */
public class QLApproxAgent extends QLearningAgent {

    private List<Double> poids;

    private FeatureFunction featurefunction;

    public QLApproxAgent(double alpha, double gamma, Environnement _env, FeatureFunction _featurefunction) {
        super(alpha, gamma, _env);

        featurefunction = _featurefunction;
        poids = new ArrayList<>(featurefunction.getFeatureNb());

        for (int i = 0; i < featurefunction.getFeatureNb(); ++i) {
            poids.add(1d);
        }
    }


    @Override
    public double getQValeur(Etat e, Action a) {
        final double[] features = featurefunction.getFeatures(e, a);

        int sum = 0;
        for (int i = 0; i < features.length; i++) {
            sum += poids.get(i) * features[i];
        }

        return sum;
    }


    @Override
    public void endStep(Etat s, Action a, Etat sp, double r) {
        if (RLAgent.DISPRL) {
            System.out.println("QL: mise a jour poids pour etat \n" + s + " action " + a + " etat' \n" + sp + " r " + r);
        }
        //inutile de verifier si e etat absorbant car dans runEpisode et threadepisode
        //arrete episode lq etat courant absorbant

        final ArrayList<Double> anciensPoids = new ArrayList<>(poids);

        final double qValeurSpA = getValeur(sp);
        final double qValeurSA = getQValeur(s, a);

        for (int i = 0; i < anciensPoids.size(); i++) {
            double newPoids = anciensPoids.get(i)
                    + alpha * (r + gamma * qValeurSpA - qValeurSA) * featurefunction.getFeatures(s, a)[i];

            poids.set(i, newPoids);
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.qvaleurs.clear();

        //*** VOTRE CODE
        poids = new ArrayList<>(featurefunction.getFeatureNb());

        for (int i = 0; i < featurefunction.getFeatureNb(); ++i) {
            poids.add(1d);
        }

        this.episodeNb = 0;
        this.notifyObs();
    }


}
