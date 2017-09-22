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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.martus.client.core.ConfigInfo;
import org.martus.client.swingui.PureFxMainWindow;
import org.martus.client.swingui.UiFontEncodingHelper;
import org.martus.client.swingui.UiMainWindow;
import org.martus.clientside.UiLocalization;
import org.martus.common.EnglishCommonStrings;
import org.martus.common.MartusLogger;
import org.martus.util.UnicodeReader;
import org.martus.util.language.LanguageOptions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PureFxTemplateDlg extends Dialog<Boolean> implements TemplateDlgInterface
{
	public PureFxTemplateDlg(UiMainWindow mainWindowToUse, ConfigInfo infoToUse, File defaultDetailsFileToUse)
	{
		if(LanguageOptions.isRightToLeftLanguage())
			getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		else
			getDialogPane().setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

		info = infoToUse;
		mainWindow = mainWindowToUse;
		defaultDetailsFile = defaultDetailsFileToUse;

		fontHelper = new UiFontEncodingHelper(info.getDoZawgyiConversion());
		UiLocalization localization = mainWindow.getLocalization();

		PureFxMainWindow.updateIcon(getDialogPane());
		setTitle(localization.getWindowTitle("BulletinTemplate"));
		setGraphic(null);
		setHeaderText(null);

		details = new TextArea();
		details.setPrefColumnCount(PREF_COL_COUNT);
		details.setPrefRowCount(PREF_ROW_COUNT);
		details.setWrapText(true);

		details.setText(fontHelper.getDisplayable(info.getTemplateDetails()));

		Button loadFromFileButton = new Button(localization.getButtonLabel("ResetContents"));
		loadFromFileButton.addEventHandler(ActionEvent.ACTION, new LoadFileHandler());

		Button okButton = new Button(localization.getButtonLabel(EnglishCommonStrings.OK));
		okButton.addEventHandler(ActionEvent.ACTION, new OkHandler());

		Button cancelButton = new Button(localization.getButtonLabel(EnglishCommonStrings.CANCEL));
		cancelButton.addEventHandler(ActionEvent.ACTION, new CancelHandler());

		Button helpButton = new Button(localization.getButtonLabel("Help"));
		helpButton.addEventHandler(ActionEvent.ACTION, new HelpHandler());

		Label label = new Label(localization.getFieldLabel("TemplateDetails"));

		HBox space = new HBox();
		HBox.setHgrow(space, Priority.ALWAYS);

		HBox buttonBox = new HBox(loadFromFileButton, space, okButton, cancelButton, helpButton);
		buttonBox.setSpacing(10);

		VBox contentBox = new VBox(details, buttonBox);
		contentBox.setSpacing(10);

		HBox mainBox = new HBox(label, contentBox);
		mainBox.setSpacing(10);

		getDialogPane().setContent(mainBox);
	}

	class LoadFileHandler implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent event)
		{
			if(mainWindow.confirmDlg("ResetDefaultDetails"))
			{
				details.setText("");
				try
				{
					if(defaultDetailsFile.exists())
						loadFile(defaultDetailsFile);
				}
				catch (IOException e)
				{
					MartusLogger.logException(e);
					mainWindow.notifyDlg("ErrorReadingFile");
				}
			}
		}
	}

	class OkHandler implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent event)
		{
			result = true;
			info.setTemplateDetails(fontHelper.getStorable(details.getText()));
			closeDialog();
		}
	}

	class CancelHandler implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent event)
		{
			result = false;
			closeDialog();
		}
	}

	class HelpHandler implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent event)
		{
			UiLocalization localization = mainWindow.getLocalization();
			String title = localization.getWindowTitle("HelpDefaultDetails");
			String helpMsg = localization.getFieldLabel("HelpDefaultDetails");
			String helpMsgExample = localization.getFieldLabel("HelpExampleDefaultDetails");
			String helpMsgExample1 = localization.getFieldLabel("HelpExample1DefaultDetails");
			String helpMsgExample2 = localization.getFieldLabel("HelpExample2DefaultDetails");
			String helpMsgExampleEtc = localization.getFieldLabel("HelpExampleEtcDefaultDetails");
			String ok = localization.getButtonLabel(EnglishCommonStrings.OK);
			String[] contents = {helpMsg, "", "",helpMsgExample, helpMsgExample1, "", helpMsgExample2, "", helpMsgExampleEtc};
			String[] buttons = {ok};

			mainWindow.notifyDlg(title, contents, buttons);
		}
	}

	public void loadFile(File fileToLoad) throws IOException
	{
		StringBuilder data = new StringBuilder();
		BufferedReader reader = new BufferedReader(new UnicodeReader(fileToLoad));
		while(true)
		{
			String line = reader.readLine();
			if(line == null)
				break;

			data.append(line);
			data.append("\n");
		}
		reader.close();

		details.setText(fontHelper.getDisplayable(data.toString()));
	}

	@Override
	public boolean getResults()
	{
		return result;
	}

	@Override
	public void showDialog()
	{
		showAndWait();
	}

	private void closeDialog()
	{
		getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		close();
		getDialogPane().getButtonTypes().remove(ButtonType.CANCEL);
	}

	private UiMainWindow mainWindow;
	private ConfigInfo info;
	private File defaultDetailsFile;

	private UiFontEncodingHelper fontHelper;
	private TextArea details;
	private boolean result;

	private static final int PREF_COL_COUNT = 65;
	private static final int PREF_ROW_COUNT = 15;
}
