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

import javax.swing.JComponent;

import org.martus.client.swingui.PureFxMainWindow;
import org.martus.client.swingui.UiMainWindow;
import org.martus.clientside.MtfAwareLocalization;
import org.martus.clientside.UiLocalization;
import org.martus.util.TokenReplacement;
import org.martus.util.language.LanguageOptions;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class PureFxScrollableTextDlg extends Dialog<Boolean>
{
	public PureFxScrollableTextDlg(UiMainWindow owner, String titleTag, String okButtonTag, String cancelButtonTag, String descriptionTag, String text)
	{
		this(owner, titleTag, okButtonTag, cancelButtonTag, descriptionTag, text, new HashMap());
	}

	public PureFxScrollableTextDlg(UiMainWindow owner, String titleTag, String okButtonTag, String cancelButtonTag, String descriptionTag, String text, Map tokenReplacement)
	{
		this(owner, titleTag, okButtonTag, cancelButtonTag, descriptionTag, text, tokenReplacement, null);
	}

	protected PureFxScrollableTextDlg(UiMainWindow owner, String titleTag, String okButtonTag, String cancelButtonTag, String descriptionTag, String text, Map tokenReplacement, JComponent bottomPanel)
	{
		if(LanguageOptions.isRightToLeftLanguage())
			getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		else
			getDialogPane().setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

		try
		{
			UiLocalization localization = owner.getLocalization();
			String windowTitle = localization.getWindowTitle(titleTag);

			VBox mainBox = new VBox();
			mainBox.setPadding(new Insets(5, 5, 5, 5));
			mainBox.setSpacing(5);

			PureFxMainWindow.updateIcon(getDialogPane());
			setTitle(TokenReplacement.replaceTokens(windowTitle, tokenReplacement));
			setGraphic(null);
			setHeaderText(null);
			getDialogPane().setContent(mainBox);

			String buttonLabel = localization.getButtonLabel(okButtonTag);
			ButtonType okButton = new ButtonType(TokenReplacement.replaceTokens(buttonLabel, tokenReplacement), ButtonBar.ButtonData.OK_DONE);
			getDialogPane().getButtonTypes().add(okButton);

			if(!cancelButtonTag.equals(MtfAwareLocalization.UNUSED_TAG))
			{
				buttonLabel = localization.getButtonLabel(cancelButtonTag);
				ButtonType cancelButton = new ButtonType(TokenReplacement.replaceTokens(buttonLabel, tokenReplacement), ButtonBar.ButtonData.CANCEL_CLOSE);
				getDialogPane().getButtonTypes().add(cancelButton);
			}

			if(!descriptionTag.equals(MtfAwareLocalization.UNUSED_TAG))
			{
				String fieldLabel = localization.getFieldLabel(descriptionTag);
				fieldLabel = TokenReplacement.replaceTokens(fieldLabel, tokenReplacement);

				TextArea description = TextAreaCreator.createTextArea(TokenReplacement.replaceTokens(fieldLabel, tokenReplacement));
				mainBox.getChildren().add(description);
			}

			TextArea details = TextAreaCreator.createTextArea(TokenReplacement.replaceTokens(text, tokenReplacement));
			details.setPrefHeight(DETAILS_PREF_HEIGHT);
			mainBox.getChildren().add(details);

			setResultConverter(dialogButton -> dialogButton == okButton);

			addBottomPanel(mainBox, bottomPanel);

			showAndWait();
		}
		catch (TokenReplacement.TokenInvalidException e)
		{
			e.printStackTrace();
		}
	}

	protected void addBottomPanel(Pane mainBox, JComponent bottomPanel)
	{
	}

	private static final double DETAILS_PREF_HEIGHT = 400;
}
