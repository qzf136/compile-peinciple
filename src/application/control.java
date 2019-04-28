package application;

import Semantic.Grammar;
import Semantic.Print;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class control {

	@FXML
	TextArea areaTable;
	@FXML
	TextArea areaCode;
	@FXML
	TextArea areaError;
	
	@FXML
	TextField fieldGrammar;
	@FXML
	TextField fieldSentence;
	
	@FXML
	Label labelGrammar;
	@FXML
	Label labelSentence;
	
	@FXML
	Button button;
	
	public void parse(ActionEvent event) {
		Grammar.parseGrammar(fieldGrammar.getText());
		Grammar.parseSentence(fieldSentence.getText());
		print();
	}
	
	public void print() {
		areaTable.setText(Print.printCharTabel());
		areaCode.setText(Print.printCodes());
		areaError.setText(Print.printErrorMsg());
	}
	
}
