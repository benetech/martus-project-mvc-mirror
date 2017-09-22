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
import org.martus.client.swingui.UiConstants;
import org.martus.client.swingui.UiMainWindow;
import org.martus.common.EnglishCommonStrings;
import org.martus.common.MiniLocalization;
import org.martus.util.language.LanguageOptions;

import javafx.geometry.NodeOrientation;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class PureFxSplashDlg extends Dialog
{
	public PureFxSplashDlg(MiniLocalization localization, String text)
	{
		if(LanguageOptions.isRightToLeftLanguage())
			getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		else
			getDialogPane().setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

		Pane mainPane = new StackPane();

		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		PureFxMainWindow.updateIcon(stage);
		setTitle("");
		setGraphic(null);
		setHeaderText(null);
		getDialogPane().setContent(mainPane);

		String versionInfo = UiMainWindow.getDisplayVersionInfo(localization);
		String copyrightInfo = UiConstants.copyright;
		String websiteInfo = UiConstants.website;
		String fullVersionInfo = "<html>" +
				"<p align='center'>" + text + "</p>" +
				"<p align='center'></p>" +
				"<p align='center'>" + versionInfo + "</p>" +
				"<p align='center'>" + copyrightInfo + "</p>" +
				"<p align='center'>" + websiteInfo + "</p>" +
				"</html>";

		WebView webView = new WebView();
		webView.getEngine().loadContent(fullVersionInfo);
		mainPane.getChildren().add(webView);

		getDialogPane().getButtonTypes().add(new ButtonType(localization.getButtonLabel(EnglishCommonStrings.OK), ButtonBar.ButtonData.OK_DONE));

		showAndWait();
	}
}
