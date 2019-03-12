package othello;

import java.util.*;

public class FinalBoss extends OthelloPlayer implements MiniMax {

	String name = "Final Boss";
	int[][] squareValues;
	private Random myRandom = new Random();
	private int testGameCount = 50;
	public static void main(String[] args) {
		System.out.println("run");
	}
	
	public FinalBoss(String name, int depthLimit) {
		super(name);
	}

	public FinalBoss(String name) {
		super(name);
		squareValues = new int[8][8];
		squareValues[0][0] = cornerVal;
		squareValues[0][7] = cornerVal;
		squareValues[7][7] = cornerVal;
		squareValues[7][0] = cornerVal;
		
		

		squareValues[0][1] = nearCorner;
		squareValues[1][0] = nearCorner;
		squareValues[7][6] = nearCorner;
		squareValues[6][7] = nearCorner;
		
		squareValues[0][6] = nearCorner;
		squareValues[6][0] = nearCorner;
		squareValues[7][1] = nearCorner;
		squareValues[1][7] = nearCorner;
		

		
		for(int i = 0; i < 4; ++i) {

			squareValues[7][i+2] = side;
			squareValues[0][i+2] = side;
			squareValues[i+2][7] = side;
			squareValues[i+2][0] = side;
		}
		
		for(int i= 1; i <7; ++i) {
			
			for(int j = 1; j < 7; ++j) {
				if(j==1 || j == 7 || i == 1 ||i ==7) {
					squareValues[i][j] = secondRow;
				}else {

					squareValues[i][j] = middle;
				}
			}
		}
		
		squareValues[1][1] =nearCorner;
		squareValues[6][6] =nearCorner;
		squareValues[1][6] =nearCorner;
		squareValues[6][1] = nearCorner;
		
		
	}
	
	static final int cornerVal =500;
	static final int nearCorner = -10;

	static final int middle = -2;
	static final int side = 2;
	static final int edgeRunValue = 4;
	static final int secondRow = -2;
	static final int cutoff = 250;
	static final int secureSideValue = 5;
	
	public int secureSide(GameState g, 
			int startX, int startY, 
			int dY, int dX, 
			GameState.Player checkPlayer) {
		
		if(g.getSquare(startX, startY) != checkPlayer ) {
			
			return 0;
		}
		int curX = startX+dX;
		int curY = startY+dY;
		for(int i = 1; i< 8; ++i) {
			if( g.getSquare(curX, curY) != checkPlayer) 
				return i*secureSideValue;
			curX+=dX;
			curY+=dY;
		}
		return 4*secureSideValue;
		
		
	}
	public int checkSide(GameState g, 
			int startX, int startY, 
			int dY, int dX, 
			GameState.Player checkPlayer) {
		int result = 0;
		boolean onRun = false;
		if( g.getSquare(startX, startY) == GameState.Player.EMPTY) {
			
		}else if(g.getSquare(startX, startY) == checkPlayer ) {
			
			onRun=true;
		}
		int curX = startX+dX;
		int curY = startY+dY;
		for(int i = 1; i< 8; ++i) {
			if( g.getSquare(curX, curY) == GameState.Player.EMPTY) {
				if(onRun) {

					result+=edgeRunValue;
					onRun=false;
				}
				
			}else if(g.getSquare(curX, curY)  == checkPlayer ) {
				if(onRun) {
				}else if( g.getSquare(curX-dX, curY-dY)
						==GameState.Player.EMPTY) {
					onRun = true;
				}else {
					onRun = false;
				}
			}else {
				onRun = false;
			}
			curX+=dX;
			curY+=dY;
		}
		if(onRun) {
			result+=edgeRunValue;
		}
		return result;
		
		
	}
	
	public int staticEvaluator(GameState state) {
		int p1Moves = state.getValidMoves(GameState.Player.PLAYER1).size();
		int p2Moves = state.getValidMoves(GameState.Player.PLAYER2).size();
		int base =p1Moves*p1Moves-p2Moves*p2Moves;
		

		for(int i = 0; i < 8; ++i) {
			for(int j = 0; j < 8; ++j) {
				if(state.getSquare(i,j) == GameState.Player.PLAYER1) {
					base+= squareValues[i][j];
				}
				if(state.getSquare(i,j) == GameState.Player.PLAYER2) {
					base-= squareValues[i][j];
				}
			}
		}
		
		base += checkSide(state, 0,0,0,1,GameState.Player.PLAYER1)-
				checkSide(state, 0,0,0,1,GameState.Player.PLAYER2);

		base += checkSide(state, 0,0,1,0,GameState.Player.PLAYER1)-
				checkSide(state, 0,0,1,0,GameState.Player.PLAYER2);
		base += checkSide(state, 7,7,-1,0,GameState.Player.PLAYER1)-
				checkSide(state, 7,7,-1,0,GameState.Player.PLAYER2);
		base += checkSide(state, 7,7,0,-1,GameState.Player.PLAYER1)-
				checkSide(state, 7,7,0,-1,GameState.Player.PLAYER2);

		
		
		base+=checkAllSecureSides(state, GameState.Player.PLAYER1)-
				checkAllSecureSides(state, GameState.Player.PLAYER2);
		

		if(Math.abs(base) < 0) {
			base+=this.mcEvaluator(state);
		}
		
		return base;
	}
	
	public int mcEvaluator(GameState state) {
		int result = 0;
		for(int i= 0 ; i < testGameCount ;++i) {
			result +=playRandomGame(state);
		}
		return result;
	}
	
	public int playRandomGame(GameState g) {
		//System.out.println("start g" + g.toString());
		while(g.getStatus() == GameState.GameStatus.PLAYING) {
			AbstractSet<Square> moves = g.getValidMoves();
			
			int item = myRandom .nextInt(moves.size());
			//System.out.println(item);
			int i =0;
			for(Square s : moves) {
				if(i == item) {

					g=g.applyMove(s, false);
					break;
				}
				i++;
			}
		}
		//System.out.println("end g" + g.toString());
		if(g.getStatus() == GameState.GameStatus.PLAYER1WON) {
			//System.out.println("player1");
			return 1;
		}else if(g.getStatus() == GameState.GameStatus.TIE) {
			return 0;
		}else {
			//System.out.println("player2");
			return -1;
		}
	}
	public int checkAllSecureSides(GameState g,GameState.Player checkPlayer) {
		int total = 0;
		total+=this.secureSide(g, 0, 0, 1, 0, checkPlayer);
		total+=this.secureSide(g, 0, 0, 0, 1, checkPlayer);
		total+=this.secureSide(g, 0, 7, 1, 0, checkPlayer);
		total+=this.secureSide(g, 0, 7, 0, -1, checkPlayer);
		total+=this.secureSide(g, 7, 0, -1, 0, checkPlayer);
		total+=this.secureSide(g, 7, 0, 0, 1, checkPlayer);
		total+=this.secureSide(g, 7, 7, -1, 0, checkPlayer);
		total+=this.secureSide(g, 7, 7, 0, -1, checkPlayer);
		total+=this.secureSide(g, 0, 7, 1, 0, checkPlayer);
		total+=this.secureSide(g, 0, 7, 0, -1, checkPlayer);
				
		return total;
	}

	static class SquareValue implements Comparable<SquareValue>{
		Square s;
		double value;
		GameState g;
		
		public SquareValue(Square s, double value, GameState g) {
			this.s =s;
			this.value =value;
			this.g=g ;
		}
		@Override
		public int compareTo(SquareValue arg0) {
			if( value - arg0.value > 0) {
				return -1;
			}else if(value == arg0.value) {
				return 0;
			}else {
				return 1;
			}
		}
		
		public String toString() {
			return s.toString() + " " + value;
		}
		
	}
	static double totalDepth = 0;
	@Override
	public Square getMove(GameState currentState, Date deadline) {

		boolean isMax = currentState.getCurrentPlayer() == GameState.Player.PLAYER1;
		
		Date curTime = new Date();
		long totalTime = deadline.getTime()-curTime.getTime();
		double turnMultiplier = isMax ? 1 : -1;
		ArrayList<Square> validMoves = new ArrayList<>(currentState.getValidMoves());
		ArrayList<SquareValue> squaresAndValues = new ArrayList<>();		
		for(Square s : validMoves) {
			squaresAndValues.add(new SquareValue(s, 0, currentState.applyMove(s)));
		}	
		count = 0;
		for(int depth = 0; depth <= 20; ++depth) {
			curTime = new Date();			
			for(int i =0; i < squaresAndValues.size(); ++i) {
				double  value = stateValue(squaresAndValues.get(i).g,
						0, depth, !isMax, -200000, 200000, deadline) * turnMultiplier;
				if(Math.abs(value)==1000) {
					System.out.println("emergency stop");
					totalDepth+=i-1;
					return squaresAndValues.get(0).s;
				}			
				squaresAndValues.get(i).value = value;	
			}
			Collections.sort(squaresAndValues);
		}
		
		
		return squaresAndValues.get(0).s;

	}
	
	static int count = 0;
	public double stateValue(GameState cur, int depthSoFar, 
			int maxDepth, boolean maxTurn, double alpha,
			double beta, Date deadline ) {
		count++;
		if(cur.getStatus()!=GameState.GameStatus.PLAYING) {
			if(cur.getStatus() == GameState.GameStatus.TIE) {
				return 0;
			}
			return 100*(cur.getScore(GameState.Player.PLAYER1)-
					cur.getScore(GameState.Player.PLAYER2));
		}
		
		if(depthSoFar == maxDepth) {
			return staticEvaluator(cur);
		}
		if(maxDepth-depthSoFar == 3) {
			Date curTime = new Date();
			if(deadline.getTime() - curTime.getTime() < cutoff) {
				return 1000;
			}
		}
		
		Collection<Square> moves = cur.getValidMoves();

		if(moves.size() == 0) {
			return stateValue(cur, depthSoFar+1, maxDepth, !maxTurn, alpha, beta,
					deadline);
		}
		
			
		
		for(Square s : moves) {
			GameState g = cur.applyMove(s,true);
			double result = stateValue(g, depthSoFar+1, maxDepth, !maxTurn, alpha, beta,
					deadline);
			if(Math.abs(result) == 1000) {
				return 1000;
			}
			if(maxTurn) {
				if(result>alpha) {
					alpha = result;
				}
				
				if(result >= beta) {
					return result;
				}
			}else {
				if(result < beta) {
					beta = result;
				}
				
				if(result <= alpha) {
					return result;
				}
			}
		}
		
		return maxTurn ? alpha : beta;
	}
}