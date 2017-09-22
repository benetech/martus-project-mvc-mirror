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
import org.martus.util.language.LanguageOptions;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;

public class PureFxPushButtonsDlg extends Dialog implements PushButtonsDlgInterface
{
	public PureFxPushButtonsDlg(String title, String[] buttonLabels)
	{
		if (LanguageOptions.isRightToLeftLanguage())
			getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		else
			getDialogPane().setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

		PureFxMainWindow.updateIcon(getDialogPane());
		setTitle(title);
		setGraphic(null);
		setHeaderText(null);

		VBox mainBox = new VBox();
		mainBox.setSpacing(10);
		mainBox.setPadding(new Insets(15, 5, 5, 5));
		mainBox.setAlignment(Pos.CENTER);

		getDialogPane().setContent(mainBox);

		for (String buttonLabel : buttonLabels)
		{
			mainBox.getChildren().add(createButton(buttonLabel));
		}

		getDialogPane().getScene().getWindow().setOnCloseRequest(event -> closeDialog());
	}

	private Button createButton(String label)
	{
		Button button = new Button(label);
		button.setOnAction(event ->
		{
			pressedButtonLabel = label;

			closeDialog();
		});

		return button;
	}

	private void closeDialog()
	{
		getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		close();
		getDialogPane().getButtonTypes().remove(ButtonType.CANCEL);
	}

	@Override
	public String getPressedButtonLabel()
	{
		return pressedButtonLabel;
	}

	@Override
	public void showDialog()
	{
		showAndWait();
	}

	private String pressedButtonLabel;
}
