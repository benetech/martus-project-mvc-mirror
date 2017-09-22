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

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.martus.client.swingui.PureFxMainWindow;
import org.martus.util.language.LanguageOptions;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

public abstract class SwingInFxDialog extends Dialog
{
	public SwingInFxDialog()
	{
		if(LanguageOptions.isRightToLeftLanguage())
			getDialogPane().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		else
			getDialogPane().setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

		swingNode = new SwingNode();
		Pane mainPane = new StackPane();
		mainPane.getChildren().add(swingNode);

		PureFxMainWindow.updateIcon(getDialogPane());
		setTitle("");
		setGraphic(null);
		setHeaderText(null);
		getDialogPane().setContent(mainPane);

		getDialogPane().getScene().getWindow().setOnCloseRequest(event -> closeDialog());
	}

	public void createAndSetSwingContent()
	{
		SwingUtilities.invokeLater(() ->
		{
			JComponent component = createSwingContent();
			Dimension size = component.getPreferredSize();

			Platform.runLater(() -> setSizeAndCenterDialog(size));

			swingNode.setContent(component);
		});
	}

	private void setSizeAndCenterDialog(Dimension size)
	{
		double maxHeight = Screen.getPrimary().getVisualBounds().getHeight();
		double maxWidth = Screen.getPrimary().getVisualBounds().getWidth();

		double prefHeight = size.getHeight() + HEIGHT_OFFSET;
		double prefWidth = size.getWidth() + WIDTH_OFFSET;

		setHeight(Double.min(maxHeight, prefHeight));
		setWidth(Double.min(maxWidth, prefWidth));

		getDialogPane().getContent().getScene().getWindow().centerOnScreen();
	}

	public void closeDialog()
	{
		Platform.runLater(() -> {
			getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
			close();
			getDialogPane().getButtonTypes().remove(ButtonType.CANCEL);
		});
	}

	public abstract JComponent createSwingContent();

	private final SwingNode swingNode;

	private final static double WIDTH_OFFSET = 40;
	private final static double HEIGHT_OFFSET = 80;
}
