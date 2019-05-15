package agent.strategy;

import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Etat;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Strategie qui renvoit un choix aleatoire avec proba epsilon, un choix glouton (suit la politique de l'agent) sinon
 *
 * @author lmatignon
 */
public class StrategyGreedy extends StrategyExploration {
    /**
     * parametre pour probabilite d'exploration
     */
    protected double epsilon;
    private Random rand = new Random();


    public StrategyGreedy(RLAgent agent, double epsilon) {
        super(agent);
        this.epsilon = epsilon;
    }

    @Override
    public Action getAction(Etat _e) {//renvoi null si _e absorbant
        double d = rand.nextDouble();
        List<Action> actions = agent.getActionsLegales(_e);
        if (actions.isEmpty()) {
            return null;
        }

        if (d <= epsilon) {
            final int actionAleatoireIndex = rand.nextInt(actions.size());
            return actions.get(actionAleatoireIndex);
        } else {
            return actions.stream()
                    .max(Comparator.comparingDouble(a -> agent.getQValeur(_e, a)))
                    .get();
        }
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
        System.out.println("epsilon:" + epsilon);
    }

/*	@Override
	public void setAction(Action _a) {
		// TODO Auto-generated method stub
		
	}*/

}
