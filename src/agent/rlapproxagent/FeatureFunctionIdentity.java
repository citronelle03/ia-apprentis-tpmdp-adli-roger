package agent.rlapproxagent;

import environnement.Action;
import environnement.Etat;

import java.util.HashMap;
import java.util.Map;

/**
 * Vecteur de fonctions caracteristiques phi_i(s,a): autant de fonctions caracteristiques que de paire (s,a),
 * <li> pour chaque paire (s,a), un seul phi_i qui vaut 1  (vecteur avec un seul 1 et des 0 sinon).
 * <li> pas de biais ici
 *
 * @author laetitiamatignon
 */
public class FeatureFunctionIdentity implements FeatureFunction {
    private final int size;
    private int lastPosition;
    private int nbEtat;
    private int nbAction;
    private double[] features;
    private Map<Integer, Integer> hashcodesPosition;
    private int nextPosition = 0;

    public FeatureFunctionIdentity(int _nbEtat, int _nbAction) {
        nbEtat = _nbEtat;
        nbAction = _nbAction;
        size = nbAction * nbEtat;
        features = new double[size];
        lastPosition = 0;
        hashcodesPosition = new HashMap<>();
    }

    @Override
    public int getFeatureNb() {
        return size;
    }

    @Override
    public double[] getFeatures(Etat e, Action a) {
        features[lastPosition] = 0;

        final int hashcode = e.hashCode() * a.ordinal();
        Integer newPosition;
        newPosition = hashcodesPosition.get(hashcode);

        if (newPosition == null) {
            hashcodesPosition.put(hashcode, this.nextPosition);
            newPosition = this.nextPosition;
            this.nextPosition++;
        }

        features[newPosition] = 1;
        lastPosition = newPosition;

        return features;
    }


}
