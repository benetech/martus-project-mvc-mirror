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

import java.util.HashMap;
import java.util.Map;

import org.martus.client.swingui.PureFxMainWindow;
import org.martus.common.MartusLogger;
import org.martus.util.TokenReplacement;
import org.martus.util.language.LanguageOptions;

import javafx.geometry.NodeOrientation;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PureFxNotifyDlg extends Alert
{
	public PureFxNotifyDlg(String title, String[] contents, String[] buttons)
	{
		this(title, contents, buttons, new HashMap());
	}

	public PureFxNotifyDlg(String title, String[] contents, String[] buttons, Map tokenReplacement)
	{
		super(AlertType.NONE);
		initialize(title, contents, buttons, tokenReplacement);
	}

	public void initialize(String title, String[] contents, String[] buttons, Map tokenReplacement) {

		if(LanguageOptions.isRightToLeftLanguage())
			getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		else
			getDialogPane().setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

		try
		{
			title = TokenReplacement.replaceTokens(title, tokenReplacement);
			contents = TokenReplacement.replaceTokens(contents, tokenReplacement);
			buttons = TokenReplacement.replaceTokens(buttons, tokenReplacement);

			Stage stage = (Stage) getDialogPane().getScene().getWindow();
			PureFxMainWindow.updateIcon(stage);
			setTitle(title);
			setGraphic(null);
			setHeaderText(null);

			StringBuilder wrappedContents = new StringBuilder();
			for (String content : contents)
			{
				wrappedContents.append(content);
				wrappedContents.append("\n");
			}

			getButtonTypes().add(new ButtonType(buttons[0], ButtonBar.ButtonData.OK_DONE));
			for (int i = 1; i < buttons.length; i++)
				getButtonTypes().add(new ButtonType(buttons[i], ButtonBar.ButtonData.CANCEL_CLOSE));

			VBox mainBox = new VBox(TextAreaCreator.createTextArea(wrappedContents.toString()));
			getDialogPane().setContent(mainBox);

			showAndWait();
		}
		catch (TokenReplacement.TokenInvalidException e)
		{
			MartusLogger.logException(e);
		}
	}
}
