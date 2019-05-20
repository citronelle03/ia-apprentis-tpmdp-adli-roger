package pacman.environnementRL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;
import environnement.Etat;
/**
 * Classe pour définir un etat du MDP pour l'environnement pacman avec QLearning tabulaire

 */
public class EtatPacmanMDPClassic implements Etat , Cloneable{
	final int pacmanX;
	final int pacmanY;
	private StateGamePacman stategamepacman;

	final List<Integer> ghostsXs = new ArrayList<>();
	final List<Integer> ghostsYs = new ArrayList<>();

	final int nbfood;

	public EtatPacmanMDPClassic(StateGamePacman _stategamepacman){
		stategamepacman = _stategamepacman;

		pacmanX = _stategamepacman.getPacmanState(0).getX();
		pacmanY = _stategamepacman.getPacmanState(0).getY();

		final int numberOfGhosts = _stategamepacman.getNumberOfGhosts();
		for (int i = 0; i < numberOfGhosts; i++) {
			ghostsXs.add(_stategamepacman.getGhostState(i).getX());
			ghostsYs.add(_stategamepacman.getGhostState(i).getY());
		}

		nbfood = _stategamepacman.getMaze().getNbfood();
	}

	public int getDimensions() {
		final int sizeX = stategamepacman.getMaze().getSizeX();
		final int sizeY = stategamepacman.getMaze().getSizeY();
		final int numberOfGhosts = stategamepacman.getNumberOfGhosts();
		final int nbfood = stategamepacman.getMaze().getNbfood();

		return sizeX * sizeY
				* (sizeX * sizeY)^numberOfGhosts
				* nbfood;
	}


	@Override
	public String toString() {
		
		return "";
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final EtatPacmanMDPClassic that = (EtatPacmanMDPClassic) o;

		if (pacmanX != that.pacmanX) return false;
		if (pacmanY != that.pacmanY) return false;
		if (nbfood != that.nbfood) return false;
		if (ghostsXs != null ? !ghostsXs.equals(that.ghostsXs) : that.ghostsXs != null) return false;
		return ghostsYs != null ? ghostsYs.equals(that.ghostsYs) : that.ghostsYs == null;

	}

	@Override
	public int hashCode() {
		int result = pacmanX;
		result = 31 * result + pacmanY;
		result = 31 * result + (ghostsXs != null ? ghostsXs.hashCode() : 0);
		result = 31 * result + (ghostsYs != null ? ghostsYs.hashCode() : 0);
		result = 31 * result + nbfood;
		return result;
	}

	public Object clone() {
		EtatPacmanMDPClassic clone = null;
		try {
			// On recupere l'instance a renvoyer par l'appel de la 
			// methode super.clone()
			clone = (EtatPacmanMDPClassic)super.clone();
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implementons 
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		


		// on renvoie le clone
		return clone;
	}



	

}
