package othello;

import java.util.*;

public class ABOthelloPlayer extends OthelloPlayer implements MiniMax {
	// the depth limit of the search tree for each move
	protected int depthLimit;
	// current state represented as a note
	protected MiniMaxNode initialNode;

	public ABOthelloPlayer (String name, int depthLimit) {
		super(name);
		this.depthLimit = depthLimit;
	}
	
	public Square getMove(GameState currentState, Date deadline) {
		// the max player will be always the player 1
		boolean isMaxTurn = currentState.getCurrentPlayer().equals(GameState.Player.PLAYER1);
		// initialize the root node of our current search tree
		this.initialNode = new MiniMaxNode(currentState, null, isMaxTurn);
		
		Square nextMove = null;
		
		// the default current value for comparison
		int currentValue = isMaxTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		// initialize the value of alpha (guaranteed value for max) and beta (guaranteed value for min)
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		
		if (initialNode.branchingFactor != 0) {
			HashSet<Square> validMoves = (HashSet<Square>) initialNode.state.getValidMoves();
			for (Square move : validMoves) {
				GameState childState = currentState.applyMove(move);
				MiniMaxNode curChild = new MiniMaxNode(childState, initialNode, !initialNode.isMaxPlayer);
				if (isMaxTurn) {
					// max player's turn, update nextMove if the current move results in larger miniMax value
					if (alphaBeta(1, curChild, alpha, beta) > currentValue) 
						nextMove = move;
				}
				else {
					// min player's turn, update nextMove if the current move results in smaller miniMax value
					if (alphaBeta(1, curChild, alpha, beta) < currentValue)
						nextMove = move;
				}
			}
		}	
		return nextMove;
	}
	
	public int alphaBeta(int curDepth, MiniMaxNode curNode, int alpha, int beta) {
		if (curDepth == this.depthLimit || curNode.branchingFactor == 0) {
			// we reach the terminal state (either reaching the depth limit or a leaf node)
			return this.staticEvaluator(curNode.state);
		}
		
		// the current node is not a leaf node, so it can be expanded
		Collection<MiniMaxNode> children = curNode.expand();
		if (curNode.isMaxPlayer) {
			int maxValue = Integer.MIN_VALUE;				
			for (MiniMaxNode child : children) {
				maxValue = Math.max(maxValue, alphaBeta(curDepth+1, child, alpha, beta));
				if (beta <= maxValue)
					return maxValue;
				alpha = Math.max(alpha, maxValue);
			}
			return maxValue;
		}
		else {
			int minValue = Integer.MAX_VALUE;
			for (MiniMaxNode child : children) {
				minValue = Math.min(minValue, alphaBeta(curDepth+1, child, alpha, beta));		
				if (minValue <= alpha)
					return minValue;
				beta = Math.min(beta, minValue);
			}
			return minValue;
		}
	}
	
	public int staticEvaluator(GameState state) {
		return state.getScore(GameState.Player.PLAYER1);
	}
}