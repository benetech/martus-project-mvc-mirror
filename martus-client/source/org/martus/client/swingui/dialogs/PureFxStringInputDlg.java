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
import org.martus.client.swingui.UiFontEncodingHelper;
import org.martus.client.swingui.UiMainWindow;
import org.martus.clientside.UiLocalization;
import org.martus.common.EnglishCommonStrings;
import org.martus.util.language.LanguageOptions;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PureFxStringInputDlg extends Dialog<String>
{
	public PureFxStringInputDlg(UiMainWindow owner, String baseTag, String descriptionTag, String rawDescriptionText, String defaultText)
	{
		if(LanguageOptions.isRightToLeftLanguage())
			getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		else
			getDialogPane().setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

		UiLocalization localization = owner.getLocalization();
		UiFontEncodingHelper fontHelper = new UiFontEncodingHelper(owner.getDoZawgyiConversion());

		VBox mainBox = new VBox();
		mainBox.setPadding(new Insets(5, 5, 5, 5));
		mainBox.setSpacing(5);

		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		PureFxMainWindow.updateIcon(stage);
		setTitle(localization.getWindowTitle("input" + baseTag));
		setGraphic(null);
		setHeaderText(null);
		getDialogPane().setContent(mainBox);

		ButtonType okButton = new ButtonType(localization.getButtonLabel("input" + baseTag + "ok"), ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButton = new ButtonType(localization.getButtonLabel(EnglishCommonStrings.CANCEL), ButtonBar.ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

		TextField text = new TextField(fontHelper.getDisplayable(defaultText));

		if(descriptionTag.length() > 0)
			mainBox.getChildren().add(TextAreaCreator.createTextArea(localization.getFieldLabel(descriptionTag)));

		if(rawDescriptionText.length() > 0)
			mainBox.getChildren().add(TextAreaCreator.createTextArea(rawDescriptionText));

		mainBox.getChildren().add(TextAreaCreator.createTextArea(localization.getFieldLabel("input" + baseTag + "entry")));
		mainBox.getChildren().add(text);

		setResultConverter(dialogButton -> {
			if (dialogButton == okButton) {
				return fontHelper.getStorable(text.getText());
			}
			return null;
		});

		showAndWait();
	}
}
