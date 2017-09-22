/*

Martus(TM) is a trademark of Beneficent Technology, Inc.
This software is (c) Copyright 2001-2017, Beneficent Technology, Inc.

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/
package org.martus.client.swingui.dialogs;

import org.martus.client.swingui.PureFxMainWindow;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PureFxWarningMessageDlg extends Dialog
{
	public PureFxWarningMessageDlg(String title, String okButtonLabel, String warningMessageLtoR, String warningMessageRtoL)
	{
		ButtonType okButton = new ButtonType(okButtonLabel, ButtonBar.ButtonData.OK_DONE);

		TextArea textLtoR = createTextArea(warningMessageLtoR, NodeOrientation.LEFT_TO_RIGHT, L_TO_R_PREF_ROW_COUNT);
		TextArea textRtoL = createTextArea(warningMessageRtoL, NodeOrientation.RIGHT_TO_LEFT, R_TO_L_PREF_ROW_COUNT);

		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(5, 5, 5, 5));
		gridPane.add(textLtoR, 0, 0);
		gridPane.add(textRtoL, 0, 1);

		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		PureFxMainWindow.updateIcon(stage);
		setTitle(title);
		getDialogPane().getButtonTypes().add(okButton);
		setGraphic(null);
		setHeaderText(null);
		getDialogPane().setContent(gridPane);

		showAndWait();
	}

	private TextArea createTextArea(String text, NodeOrientation orientation, int prefRowCount)
	{
		TextArea textArea = new TextArea(text);
		textArea.setNodeOrientation(orientation);

		textArea.setPrefRowCount(prefRowCount);
		textArea.setPrefColumnCount(PREF_COL_COUNT);
		textArea.setWrapText(true);
		textArea.setEditable(false);
		textArea.setPadding(new Insets(5, 5, 5, 5));
		textArea.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		return textArea;
	}

	private static final int PREF_COL_COUNT = 60;
	private static final int L_TO_R_PREF_ROW_COUNT = 32;
	private static final int R_TO_L_PREF_ROW_COUNT = 8;
}
