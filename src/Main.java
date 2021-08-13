import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

	private GridPane biggp = new GridPane(), solutiongp = new GridPane();
	private StackPane sp1 = new StackPane(), sp2 = new StackPane();
	private VBox vb1 = new VBox(), vb2 = new VBox();
	private HBox buttons = new HBox();
	private Button bSolve = new Button("Solve"), bRestart = new Button("Restart"), bExit = new Button("Exit");
	private Integer[][] sudokuMat = new Integer[9][9];
	private Integer[][] resultMat = new Integer[9][9];
	private Label[] lArr = new Label[81];
	private Stage window1 = new Stage(), window2 = new Stage();
	private Scene s1 = new Scene(vb1), s2 = new Scene(vb2);
	private boolean isChanged = false;
	private int matchRow, matchCol, matchNum;
	private int options = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);

	}

	public void start(Stage primaryStage) {

		initBoard();
		sp1.getChildren().add(bSolve);
		buttons.getChildren().addAll(bExit, bRestart);
		buttons.setSpacing(10);
		buttons.setAlignment(Pos.CENTER);
		sp2.getChildren().add(buttons);
		vb1.getChildren().addAll(biggp, sp1);
		vb2.getChildren().addAll(solutiongp, sp2);
		vb1.setPadding(new Insets(10));
		sp1.setPadding(new Insets(20));
		vb2.setPadding(new Insets(10));
		sp2.setPadding(new Insets(20));
		biggp.setGridLinesVisible(true);
		bSolve.setOnAction(e -> {
			createMat();
			solve();
			// printMat();
			presentSolution();
		});
		bRestart.setOnAction(e -> {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					sudokuMat[i][j] = null;
					resultMat[i][j] = null;
				}
			}
			cleanBoard();
			cleanResults();
			window2.close();
			window1.show();

		});
		bExit.setOnAction(e -> System.exit(0));
		solutiongp.setGridLinesVisible(true);
		window1.setScene(s1);
		window1.setTitle("Sudoku Solver");
		window1.show();

	}

	public void initBoard() {

		Integer[] numbers = { 1, 2, 3, 4, 5, 6, 7, 8, 9, null };
		int index = -1;
		for (int l = 0; l < 9; l++) {
			GridPane gp = new GridPane();
			if (l % 3 == 0)
				index++;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					ObservableList<Integer> numbersList = FXCollections.observableArrayList(numbers);
					ComboBox<Integer> numberscb = new ComboBox<Integer>(numbersList);
					gp.add(numberscb, j, i);
				}
			}
			biggp.add(gp, l % 3, index);

		}
	}

	public void createMat() {
		try {
			int row = 0, col = 0, index = 0, x = 0, y = 0;
			for (Node node : biggp.getChildren()) {
				GridPane gp = (GridPane) node;
				for (Node n : gp.getChildren()) {
					ComboBox<?> cb = (ComboBox<?>) n;
					sudokuMat[row][col] = (Integer) cb.getValue();
					index++;
					col++;
					if (index % 3 == 0) {
						col = y;
						row++;
					}
					if (index % 9 == 0) {
						y = y + 3;
						col = y;
						row = x;
					}
					if (index % 27 == 0) {
						x = x + 3;
						row = x;
						y = 0;
						col = 0;
					}
				}

			}

		} catch (Exception ex) {

		}
	}

	public void solve() {
		int r = 0, c = 0;
		do {
			isChanged = false;
			for (int n = 1; n < 10; n++) {
				for (int element = 0; element < 9; element++) {
					checkSubMat(r, c, n);
					checkRow(element, n);
					checkCol(element, n);
					c = c + 3;
					if (c % 9 == 0) {
						c = 0;
						r = r + 3;
					}

				}
				r = 0;
				c = 0;
			}
			checkEverySquare();
		} while (isChanged == true);
	}

	public void checkSubMat(int row, int col, int num) {
		if (doesNumAppearsInSubMat(row, col, num) == true)
			return;
		for (int i = row; i < row + 3; i++) {
			for (int j = col; j < col + 3; j++) {
				if (sudokuMat[i][j] == null) {
					boolean doesExistsInRow = doesNumAppearsInRow(i, num);
					boolean doesExistsInCol = doesNumAppearsInCol(j, num);
					if (doesExistsInRow == false && doesExistsInCol == false) {
						options++;
						matchRow = i;
						matchCol = j;
					}

				}
			}
		}
		if (options == 1) {
			sudokuMat[matchRow][matchCol] = num;
			resultMat[matchRow][matchCol] = num;
			isChanged = true;
		}
		options = 0;
	}

	public void checkRow(int row, int num) {
		boolean doesExistsInRow = doesNumAppearsInRow(row, num);
		if (doesExistsInRow)
			return;
		for (int j = 0; j < 9; j++) {
			boolean doesExistsInCol = doesNumAppearsInCol(j, num);
			boolean doesExistsInSubMat = doesNumAppearsInSubMat((row / 3) + 2 * (row / 3), (j / 3) + 2 * (j / 3), num);
			if (sudokuMat[row][j] == null && doesExistsInCol == false && doesExistsInSubMat == false) {
				options++;
				matchCol = j;
			}
		}
		if (options == 1) {
			sudokuMat[row][matchCol] = num;
			resultMat[row][matchCol] = num;
			isChanged = true;
		}
		options = 0;
	}

	public void checkCol(int col, int num) {
		boolean doesExistsInCol, doesExistsInRow, doesExistsInSubMat;
		doesExistsInCol = doesNumAppearsInCol(col, num);
		if (doesExistsInCol)
			return;
		for (int i = 0; i < 9; i++) {
			doesExistsInRow = doesNumAppearsInRow(i, num);
			doesExistsInSubMat = doesNumAppearsInSubMat((i / 3) + 2 * (i / 3), (col / 3) + 2 * (col / 3), num);
			if (sudokuMat[i][col] == null && doesExistsInRow == false && doesExistsInSubMat == false) {
				options++;
				matchRow = i;
			}
		}
		if (options == 1) {
			sudokuMat[matchRow][col] = num;
			resultMat[matchRow][col] = num;
			isChanged = true;
		}
		options = 0;
	}

	public void checkEverySquare() {
		boolean c1, c2, c3;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sudokuMat[i][j] == null) {
					for (int n = 1; n < 9; n++) {
						c1 = doesNumAppearsInSubMat((i / 3) + 2 * (i / 3), (j / 3) + 2 * (j / 3), n);
						c2 = doesNumAppearsInRow(i, n);
						c3 = doesNumAppearsInCol(j, n);
						if (!c1 && !c2 && !c3) {
							options++;
							matchNum = n;
						}
					}
				}
				if (options == 1) {
					sudokuMat[i][j] = matchNum;
					resultMat[i][j] = matchNum;
					isChanged = true;			
				}
				options = 0;
			}
		}

	}

	public boolean doesNumAppearsInSubMat(int row, int col, int num) {
		for (int i = row; i < row + 3; i++) {
			for (int j = col; j < col + 3; j++) {
				if (sudokuMat[i][j] != null && sudokuMat[i][j] == num)
					return true;
			}
		}
		return false;
	}

	public boolean doesNumAppearsInRow(int r, int num) {
		for (int j = 0; j < 9; j++) {
			if (sudokuMat[r][j] != null && sudokuMat[r][j] == num)
				return true;
		}
		return false;
	}

	public boolean doesNumAppearsInCol(int c, int num) {
		for (int i = 0; i < 9; i++) {
			if (sudokuMat[i][c] != null && sudokuMat[i][c] == num)
				return true;
		}
		return false;
	}

	public void printMat() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.print(sudokuMat[i][j] + " ");
			}
			System.out.println();
		}
	}

	public void presentSolution() {
		try {
			int index = -1, counter = 0;
			for (int s = 0; s < 9; s++) {
				GridPane gp = new GridPane();
				if (s % 3 == 0)
					index++;
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						lArr[counter] = new Label();
						if (sudokuMat[index * 3 + i][(s % 3) * 3 + j] != null)
							lArr[counter].setText("   " + sudokuMat[index * 3 + i][(s % 3) * 3 + j].toString() + "   ");
						lArr[counter].setFont(new Font("Ink Free", 22));
						if (resultMat[index * 3 + i][(s % 3) * 3 + j] != null)
							lArr[counter].setTextFill(Color.BLUE);
						gp.add(lArr[counter], j, i);
						counter++;
					}
				}
				solutiongp.add(gp, s % 3, index);

			}
			window2.setScene(s2);
			window2.setTitle("Sudoku Solver - results");
			window1.close();
			window2.show();
		} catch (java.lang.NullPointerException ex) {
			JOptionPane.showMessageDialog(null, "No solution found!");
		}
	}

	public void cleanBoard() {
		try {
			for (Node node : biggp.getChildren()) {
				GridPane gp = (GridPane) node;
				for (Node n : gp.getChildren()) {
					ComboBox<?> cb = (ComboBox<?>) n;
					cb.setValue(null);
				}
			}

		} catch (Exception ex) {

		}

	}

	public void cleanResults() {
		try {

			for (Label l : lArr) {
				l.setText(null);
			}

		} catch (Exception ex) {

		}

	}
}
