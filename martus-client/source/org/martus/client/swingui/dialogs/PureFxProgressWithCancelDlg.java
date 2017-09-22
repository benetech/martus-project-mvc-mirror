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
import org.martus.client.swingui.UiMainWindow;
import org.martus.clientside.UiLocalization;
import org.martus.common.EnglishCommonStrings;
import org.martus.common.MartusLogger;
import org.martus.util.language.LanguageOptions;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PureFxProgressWithCancelDlg extends Dialog implements ProgressMeterDialogInterface
{
	public PureFxProgressWithCancelDlg(UiMainWindow mainWindowToUse, String tagToUse)
	{
		if (LanguageOptions.isRightToLeftLanguage())
			getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		else
			getDialogPane().setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

		mainWindow = mainWindowToUse;
		tag = tagToUse;
		UiLocalization localization = mainWindow.getLocalization();

		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		PureFxMainWindow.updateIcon(stage);
		setTitle(localization.getWindowTitle(tagToUse));
		setGraphic(null);
		setHeaderText(null);

		fxProgressBar = new ProgressBar();

		HBox box = new HBox();
		box.setPadding(new Insets(10, 10, 10, 10));
		box.getChildren().addAll(fxProgressBar);

		getDialogPane().setContent(box);

		ButtonType cancelButtonType = new ButtonType(localization.getButtonLabel(EnglishCommonStrings.CANCEL), ButtonBar.ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(cancelButtonType);

		cancel = (Button) getDialogPane().lookupButton(cancelButtonType);
		cancel.addEventFilter(ActionEvent.ACTION, event ->
		{
			if (mainWindow.confirmDlg(tag + "Cancel"))
				requestExit();
			event.consume();
		});

		getDialogPane().getScene().getWindow().setOnCloseRequest(event ->
		{
			requestExit();
			event.consume();
		});

		setOnCloseRequest(event -> requestExit());

		updateProgressBar(0.0);
	}

	private void requestExit()
	{
		isExitRequested = true;
		cancel.setDisable(true);
	}

	@Override
	public boolean shouldExit()
	{
		return isExitRequested;
	}

	@Override
	public void hideProgressMeter()
	{
		Platform.runLater(() -> fxProgressBar.setVisible(false));
	}

	@Override
	public void updateProgressMeter(int currentValue, int maxValue)
	{
		double percentComplete = (double) currentValue / (double) maxValue;

		Platform.runLater(() -> updateProgressBar(percentComplete));
	}

	@Override
	public void setStatusMessage(String message)
	{
		MartusLogger.log("UiProgressWithCancelDlg cannot setStatusMessage: " + message);
	}

	@Override
	public void finished()
	{
		Platform.runLater(() -> close());
	}

	@Override
	public void showDialog()
	{
		showAndWait();
	}

	private void updateProgressBar(double currentProgress)
	{
		fxProgressBar.setProgress(currentProgress);
		fxProgressBar.setVisible(true);
	}

	private ProgressBar fxProgressBar;
	private Button cancel;

	private UiMainWindow mainWindow;
	private String tag;

	private boolean isExitRequested;
}
