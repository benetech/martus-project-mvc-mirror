/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2015, Beneficent
Technology, Inc. (Benetech).

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
package org.martus.client.swingui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jfree.chart.JFreeChart;
import org.martus.client.core.BulletinGetterThread;
import org.martus.client.core.ConfigInfo;
import org.martus.client.reports.ReportOutput;
import org.martus.client.swingui.actions.CreateChartDialog;
import org.martus.client.swingui.dialogs.CreateChartDialogInterface;
import org.martus.client.swingui.dialogs.FancySearchDialogInterface;
import org.martus.client.swingui.dialogs.FxInSwingBulletinModifyDialog;
import org.martus.client.swingui.dialogs.ModelessBusyDlg;
import org.martus.client.swingui.dialogs.PreviewDlgInterface;
import org.martus.client.swingui.dialogs.ProgressMeterDialogInterface;
import org.martus.client.swingui.dialogs.PushButtonsDlgInterface;
import org.martus.client.swingui.dialogs.ReportFieldDlgInterface;
import org.martus.client.swingui.dialogs.SearchHelpDialogContents;
import org.martus.client.swingui.dialogs.SingleSelectionFieldChooserDialog;
import org.martus.client.swingui.dialogs.SortFieldsDlgInterface;
import org.martus.client.swingui.dialogs.TemplateDlgInterface;
import org.martus.client.swingui.dialogs.UiAboutDlg;
import org.martus.client.swingui.dialogs.UiBulletinModifyDlg;
import org.martus.client.swingui.dialogs.UiChartPreviewDlg;
import org.martus.client.swingui.dialogs.UiFancySearchDialogContents;
import org.martus.client.swingui.dialogs.UiModelessBusyDlg;
import org.martus.client.swingui.dialogs.UiPrintPreviewDlg;
import org.martus.client.swingui.dialogs.UiProgressWithCancelDlg;
import org.martus.client.swingui.dialogs.UiPushbuttonsDlg;
import org.martus.client.swingui.dialogs.UiReportFieldChooserDlg;
import org.martus.client.swingui.dialogs.UiReportFieldChooserDlg.ResultsHandler;
import org.martus.client.swingui.dialogs.UiReportFieldOrganizerDlg;
import org.martus.client.swingui.dialogs.UiShowScrollableTextDlg;
import org.martus.client.swingui.dialogs.UiSigninDlg;
import org.martus.client.swingui.dialogs.UiSortFieldsDlg;
import org.martus.client.swingui.dialogs.UiSplashDlg;
import org.martus.client.swingui.dialogs.UiStringInputDlg;
import org.martus.client.swingui.dialogs.UiTemplateDlg;
import org.martus.client.swingui.dialogs.UiWarningMessageDlg;
import org.martus.client.swingui.jfx.contacts.FxInSwingContactsStage;
import org.martus.client.swingui.jfx.generic.FxInSwingDialogStage;
import org.martus.client.swingui.jfx.generic.FxInSwingModalDialog;
import org.martus.client.swingui.jfx.generic.FxInSwingModalDialogStage;
import org.martus.client.swingui.jfx.generic.FxInSwingStage;
import org.martus.client.swingui.jfx.generic.FxRunner;
import org.martus.client.swingui.jfx.generic.FxShellController;
import org.martus.client.swingui.jfx.generic.FxStatusBar;
import org.martus.client.swingui.jfx.generic.ModalDialogWithSwingContents;
import org.martus.client.swingui.jfx.generic.PureFxStage;
import org.martus.client.swingui.jfx.generic.SigninInterface;
import org.martus.client.swingui.jfx.generic.SwingDialogContentPane;
import org.martus.client.swingui.jfx.generic.VirtualStage;
import org.martus.client.swingui.jfx.landing.FxInSwingMainStage;
import org.martus.client.swingui.jfx.landing.FxMainStage;
import org.martus.client.swingui.jfx.setupwizard.FxInSwingCreateNewAccountWizardStage;
import org.martus.client.swingui.jfx.setupwizard.FxInSwingSetupWizardStage;
import org.martus.clientside.FileDialogHelpers;
import org.martus.clientside.FormatFilter;
import org.martus.clientside.MtfAwareLocalization;
import org.martus.clientside.UiFileChooser;
import org.martus.clientside.UiUtilities;
import org.martus.common.EnglishCommonStrings;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.MiniFieldSpec;
import org.martus.common.packet.UniversalId;
import org.martus.swing.UiNotifyDlg;
import org.martus.swing.UiOptionPane;
import org.martus.swing.Utilities;

import javafx.application.Platform;

public class FxInSwingMainWindow extends UiMainWindow
{
	public FxInSwingMainWindow() throws Exception
	{
		super();
	}
	
	@Override
	protected void initializeFrame()
	{

		swingFrame = new MainSwingFrame(this);
		updateIcon();
		setCurrentActiveFrame(this);
		getSwingFrame().setVisible(true);
		updateTitle();
		restoreWindowSizeAndState();

		if(UiSession.isJavaFx())
		{
			FxInSwingMainStage fxInSwingMainStage = new FxInSwingMainStage(this, getSwingFrame());
			mainStage = fxInSwingMainStage;
			setStatusBar(createStatusBar());
			FxRunner fxRunner = new FxRunner(fxInSwingMainStage);
			fxRunner.setAbortImmediatelyOnError();
			Platform.runLater(fxRunner);
			getSwingFrame().setContentPane(fxInSwingMainStage.getPanel());
		}
		else
		{
			setStatusBar(createStatusBar());
			mainPane = new UiMainPane(this, getUiState());
			getSwingFrame().setContentPane(mainPane);
		}

	}
	
	@Override
	protected void hideMainWindow()
	{
		getSwingFrame().setEnabled(false);
		getSwingFrame().setVisible(false);
	}
	
	@Override
	public void restoreWindowSizeAndState()
	{
		Dimension screenSize = Utilities.getViewableScreenSize();
		Dimension appDimension = getUiState().getCurrentAppDimension();
		Point appPosition = getUiState().getCurrentAppPosition();
		boolean showMaximized = false;
		if(Utilities.isValidScreenPosition(screenSize, appDimension, appPosition))
		{
			getSwingFrame().setLocation(appPosition);
			getSwingFrame().setSize(appDimension);
			if(getUiState().isCurrentAppMaximized())
				showMaximized = true;
		}
		else
			showMaximized = true;
		
		if(showMaximized)
		{
			getSwingFrame().setSize(screenSize.width - 50 , screenSize.height - 50);
			Utilities.maximizeWindow(getSwingFrame());
		}
		
		getUiState().setCurrentAppDimension(getMainWindowSize());
	}

	public StatusBar createStatusBar()
	{
		if(UiSession.isJavaFx())
			return new FxStatusBar(getLocalization());
		
		return new UiStatusBar(getLocalization());
	}

	@Override
	public JFrame getSwingFrame()
	{
		return swingFrame;
	}

	@Override
	public UiMainPane getMainPane()
	{
		return mainPane;
	}
	
	@Override
	public FxMainStage getMainStage()
	{
		return mainStage;
	}

	private void updateTitle() 
	{
		getSwingFrame().setTitle(getLocalization().getWindowTitle("main"));
	}
	
	@Override
	public void rawError(String errorText)
	{
		JOptionPane.showMessageDialog(null, errorText, "ERROR", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void rawSetCursor(Object newCursor)
	{
		getSwingFrame().setCursor((Cursor)newCursor);
	}

	@Override
	public Object getWaitCursor()
	{
		return Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	}

	@Override
	public Object getExistingCursor()
	{
		return getSwingFrame().getCursor();
	}
	
	@Override
	protected void showMainWindow()
	{
		getSwingFrame().setVisible(true);
		getSwingFrame().setEnabled(true);
		getSwingFrame().toFront();
	}

	@Override
	protected void obscureMainWindow()
	{
		getSwingFrame().setLocation(100000, 0);
		getSwingFrame().setSize(0,0);
		getSwingFrame().setEnabled(false);
	}
	
	public static FxInSwingStage createGenericStage(UiMainWindow observerToUse, Window windowToUse, FxShellController shellController, String cssName)
	{
		return new FxInSwingStage(observerToUse, windowToUse, shellController, cssName);
	}

	@Override
	public void createAndShowLargeModalDialog(VirtualStage stage) throws Exception
	{
		createAndShowDialog((FxInSwingDialogStage) stage, FxInSwingModalDialog.EMPTY_TITLE, LARGE_PREFERRED_DIALOG_SIZE);
	}

	@Override
	public void createAndShowModalDialog(FxShellController controller, Dimension preferedDimension, String titleTag)
	{
		FxInSwingModalDialogStage stage = new FxInSwingModalDialogStage(this, controller);
		createAndShowDialog(stage, titleTag, preferedDimension);
	}

	@Override
	public void createAndShowContactsDialog() throws Exception
	{
		createAndShowLargeModalDialog(new FxInSwingContactsStage(this));
	}

	@Override
	public void createAndShowSetupWizard() throws Exception
	{
		createAndShowLargeModalDialog(new FxInSwingCreateNewAccountWizardStage(this));
	}
	
	@Override
	public void createAndShowLegacySetupWizard() throws Exception
	{
		createAndShowLargeModalDialog(new FxInSwingSetupWizardStage(this));
	}
	
	private void createAndShowDialog(FxInSwingDialogStage stage, String titleTag, Dimension dimension)
	{
		if (dimension == null)
			dimension = LARGE_PREFERRED_DIALOG_SIZE;
		
		FxInSwingModalDialog dialog = createDialog(this);
		dialog.setIconImage(Utilities.getMartusIconImage());
		
		if (titleTag.length() > 0)
			dialog.setTitle(getLocalization().getWindowTitle(titleTag));
		
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.getContentPane().setPreferredSize(dimension);
		dialog.pack();
		dialog.getContentPane().add(stage.getPanel());
		stage.setDialog(dialog);
		stage.runOnFxThreadMaybeLater(new FxRunner(stage));
	
		Utilities.packAndCenterWindow(dialog);
		setCurrentActiveDialog(dialog);
		try
		{
			dialog.setVisible(true);
		}
		finally
		{
			setCurrentActiveDialog(null);
		}
	}

	@Override
	public Dimension getMainWindowSize()
	{
		return getSwingFrame().getSize();
	}

	@Override
	public Point getMainWindowLocation()
	{
		return getSwingFrame().getLocation();
	}

	@Override
	public boolean isMainWindowMaximized()
	{
		return getSwingFrame().getExtendedState()==JFrame.MAXIMIZED_BOTH;
	}


	private static FxInSwingModalDialog createDialog(UiMainWindow owner)
	{
		JFrame frame = owner.getSwingFrame();
		if(frame != null)
			return new FxInSwingModalDialog(frame);
	
		return new FxInSwingModalDialog();
	}

	@Override
	public void modifyBulletin(Bulletin b) throws Exception
	{
		getCurrentUiState().setModifyingBulletin(true);
		UiBulletinModifyDlg dlg = null;
		try
		{
			dlg = new FxInSwingBulletinModifyDialog(b, this);
			dlg.restoreFrameState();
			setCurrentActiveFrame(dlg);
			hideMainWindow();
			dlg.setVisible(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if(dlg != null)
				dlg.dispose();
			doneModifyingBulletin();
			throw(e);
		}
	}

	public ModelessBusyDlg createSplashScreen()
	{
		return new UiModelessBusyDlg(new ImageIcon(UiAboutDlg.class.getResource("Martus-logo-black-text-160x72.png")));
	}

	public ModelessBusyDlg createBulletinLoadScreen()
	{
		return new UiModelessBusyDlg(getLocalization().getFieldLabel("waitingForBulletinsToLoad"));
	}

	public void showMessageDialog(String message)
	{
		JOptionPane.showMessageDialog(null, message);
	}

	protected void initializationErrorExitMartusDlg(String message)
	{
		String title = "Error Starting Martus";
		String cause = "Unable to start Martus: \n" + message;
		String ok = "OK";
		String[] buttons = { ok };
		UiOptionPane pane = new UiOptionPane(cause, UiOptionPane.INFORMATION_MESSAGE, UiOptionPane.DEFAULT_OPTION,
				null, buttons);
		JDialog dialog = pane.createDialog(null, title);
		dialog.setVisible(true);
		System.exit(1);
	}

	protected void showWarningMessageDlg(JFrame owner, String title, String okButtonLabel, String warningMessageLtoR, String warningMessageRtoL)
	{
		new UiWarningMessageDlg(owner, "", getLocalization().getButtonLabel(EnglishCommonStrings.OK), warningMessageLtoR, warningMessageRtoL);
	}

	public boolean confirmDlg(JFrame parent, String baseTag)
	{
		return UiUtilities.confirmDlg(getLocalization(), parent, baseTag);
	}

	public boolean confirmDlg(JFrame parent, String baseTag, Map tokenReplacement)
	{
		return UiUtilities.confirmDlg(getLocalization(), parent, baseTag, tokenReplacement);
	}

	public boolean confirmDlg(JFrame parent, String title, String[] contents)
	{
		return UiUtilities.confirmDlg(getLocalization(), parent, title, contents);
	}

	public boolean confirmDlg(JFrame parent, String title, String[] contents, String[] buttons)
	{
		return UiUtilities.confirmDlg(parent, title, contents, buttons);
	}

	public boolean confirmDlg(String title, String[] contents, String[] buttons, Map tokenReplacement)
	{
		return UiUtilities.confirmDlg(getCurrentActiveFrame().getSwingFrame(), title, contents, buttons, tokenReplacement);
	}

	protected boolean confirmDlg(JFrame parent, String title, String[] contents, String[] buttons, Map tokenReplacement)
	{
		return UiUtilities.confirmDlg(parent, title, contents, buttons, tokenReplacement);
	}

	protected void notifyDlg(JFrame parent, String baseTag, String titleTag, Map tokenReplacement)
	{
		UiUtilities.notifyDlg(getLocalization(), parent, baseTag, titleTag, tokenReplacement);
	}

	public void notifyDlg(String title, String[] contents, String[] buttons)
	{
		new UiNotifyDlg(getCurrentActiveFrame().getSwingFrame(), title, contents, buttons);
	}

	public void messageDlg(JFrame parent, String baseTag, String message, Map tokenReplacement)
	{
		UiUtilities.messageDlg(getLocalization(), parent, baseTag, message, tokenReplacement);
	}

	protected void notifyDlg(Frame owner, String title, String[] contents, String[] buttons, Map tokenReplacement)
	{
		new UiNotifyDlg(owner, title, contents, buttons, tokenReplacement);
	}

	protected void notifyDlg(String title, String[] contents, String[] buttons, Map tokenReplacement)
	{
		new UiNotifyDlg(title, contents, buttons, tokenReplacement);
	}

	public File showFileOpenDialog(String title, String okButtonLabel, File directory, FormatFilter filter)
	{
		return FileDialogHelpers.doFileOpenDialog(getCurrentActiveFrame().getSwingFrame(), title, okButtonLabel, directory, filter);
	}

	protected File showFileSaveDialog(String title, File directory, String defaultFilename, FormatFilter filter)
	{
		return FileDialogHelpers.doFileSaveDialog(getCurrentActiveFrame().getSwingFrame(), title, directory, defaultFilename, filter, getLocalization());
	}

	public File showChooseDirectoryDialog(String windowTitle)
	{
		return UiFileChooser.displayChooseDirectoryDialog(getCurrentActiveFrame().getSwingFrame(), windowTitle);
	}

	protected File showFileSaveDialog(String title, File directory, Vector<FormatFilter> filters)
	{
		while(true)
		{
			JFileChooser fileChooser = createFileChooser(title, directory, filters);

			int userResult = fileChooser.showSaveDialog(getCurrentActiveFrame().getSwingFrame());
			if (userResult != JFileChooser.APPROVE_OPTION)
				return null;

			File selectedFile = fileChooser.getSelectedFile();

			FormatFilter selectedFilter = (FormatFilter) fileChooser.getFileFilter();
			selectedFile = getFileWithExtension(selectedFile, selectedFilter.getExtension());

			if (!selectedFile.exists())
				return selectedFile;

			if (confirmDlg(getCurrentActiveFrame().getSwingFrame(), "OverWriteExistingFile"))
				return selectedFile;

			directory = selectedFile.getParentFile();
		}
	}

	public File showFileOpenDialog(String title, File directory, Vector<FormatFilter> filters)
	{
		JFileChooser fileChooser = createFileChooser(title, directory, filters);

		int userResult = fileChooser.showOpenDialog(getCurrentActiveFrame().getSwingFrame());
		if(userResult != JFileChooser.APPROVE_OPTION)
			return null;

		return fileChooser.getSelectedFile();
	}

	protected File[] showMultiFileOpenDialog(String title, File directory, Vector<FormatFilter> filters)
	{
		JFileChooser fileChooser = createFileChooser(title, directory, filters);
		fileChooser.setMultiSelectionEnabled(true);

		int userResult = fileChooser.showOpenDialog(getCurrentActiveFrame().getSwingFrame());
		if(userResult != JFileChooser.APPROVE_OPTION)
			return new File[0];

		return fileChooser.getSelectedFiles();
	}

	private JFileChooser createFileChooser(String title, File directory, Vector<FormatFilter> filters)
	{
		JFileChooser fileChooser = new JFileChooser(directory);
		fileChooser.setDialogTitle(title);
		filters.forEach(filter -> fileChooser.addChoosableFileFilter(filter));

		// NOTE: Apparently the all file filter has a Mac bug, so this is a workaround
		fileChooser.setAcceptAllFileFilterUsed(false);
		return fileChooser;
	}

	public void runInUiThreadLater(Runnable toRun)
	{
		SwingUtilities.invokeLater(toRun);
	}

	public void runInUiThreadAndWait(Runnable toRun) throws InterruptedException, InvocationTargetException
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			toRun.run();
			return;
		}

		SwingUtilities.invokeAndWait(toRun);
	}

	public String getStringInput(String baseTag, String descriptionTag, String rawDescriptionText, String defaultText)
	{
		UiStringInputDlg inputDlg = new UiStringInputDlg(this, baseTag, descriptionTag, rawDescriptionText, defaultText);
		inputDlg.setFocusToInputField();
		inputDlg.setVisible(true);
		return inputDlg.getResult();
	}

	public boolean showScrollableTextDlg(String titleTag, String okButtonTag, String cancelButtonTag, String descriptionTag, String text)
	{
		UiShowScrollableTextDlg dlg = new UiShowScrollableTextDlg(this, titleTag, okButtonTag, cancelButtonTag, descriptionTag, text, null);
		return dlg.getResult();
	}

	public void displayScrollableMessage(String titleTag, String message, String okButtonTag, Map tokenReplacement)
	{
		new UiShowScrollableTextDlg(this, titleTag, okButtonTag, MtfAwareLocalization.UNUSED_TAG, MtfAwareLocalization.UNUSED_TAG, message, tokenReplacement, null);
	}

	public void displayScrollableMessage(String titleTag, String message, String okButtonTag, JComponent bottomPanel)
	{
		new UiShowScrollableTextDlg(this, titleTag, okButtonTag, MtfAwareLocalization.UNUSED_TAG, MtfAwareLocalization.UNUSED_TAG, message, bottomPanel);
	}

	@Override
	public void setCurrentActiveStage(PureFxStage newActiveStage)
	{
	}

	@Override
	public PureFxStage getCurrentActiveStage()
	{
		return null;
	}

	protected ModalBusyDialogInterface createModalBusyDialog(String dialogTag)
	{
		return new ModalBusyDialog(this, dialogTag);
	}

	public ProgressMeterDialogInterface createProgressMeter(String tagToUse)
	{
		return new UiProgressWithCancelDlg(this, tagToUse);
	}

	protected FancySearchDialogInterface createFancySearchDialog()
	{
		return new UiFancySearchDialogContents(this);
	}

	protected void showFancySearchDialog(FancySearchDialogInterface fancySearchDialog)
	{
		ModalDialogWithSwingContents.show((UiFancySearchDialogContents) fancySearchDialog);
	}

	public Vector getBulletins(UniversalId[] uids) throws Exception
	{
		BulletinGetterThread thread = new BulletinGetterThread(getStore(), uids);
		doBackgroundWork(thread, "PreparingBulletins");
		return thread.getBulletins();
	}

	public PushButtonsDlgInterface createPushButtonsDlg(String title, String[] buttonLabels)
	{
		return new UiPushbuttonsDlg(this, title, buttonLabels);
	}

	public ReportFieldDlgInterface createReportFieldChooserDlg(FieldSpec[] specs)
	{
		return new UiReportFieldChooserDlg(this, specs);
	}

	public ReportFieldDlgInterface createReportFieldChooserDlg(FieldSpec[] specs, ResultsHandler resultsHandler)
	{
		return new UiReportFieldChooserDlg(this, specs, resultsHandler);
	}

	public ReportFieldDlgInterface createReportFieldOrganizerDlg()
	{
		return new UiReportFieldOrganizerDlg(this);
	}

	public SortFieldsDlgInterface createSortFieldsDlg(MiniFieldSpec[] specsToAllow)
	{
		return new UiSortFieldsDlg(this, specsToAllow);
	}

	public PreviewDlgInterface createPrintPreviewDlg(ReportOutput output)
	{
		return new UiPrintPreviewDlg(this, output);
	}

	public ReportFieldDlgInterface createSingleSelectionFieldChooserDialog(FieldSpec[] specs, ResultsHandler resultsHandler)
	{
		return new SingleSelectionFieldChooserDialog(this, specs, resultsHandler);
	}

	public CreateChartDialogInterface createChartCreateDialog()
	{
		return new CreateChartDialog(this);
	}

	public PreviewDlgInterface createChartPreviewDlg(JFreeChart chart)
	{
		return new UiChartPreviewDlg(this, chart);
	}

	public void showSearchHelpMessage(String title, String message, String closeButton)
	{
		SwingDialogContentPane panel = new SearchHelpDialogContents(this, title, message, closeButton);
		ModalDialogWithSwingContents.show(panel);
	}

	public SigninInterface createAndShowSigninDialog(int mode, String username, char[] password)
	{
		return new UiSigninDlg(this, mode, username, password);
	}

	public SigninInterface createAndShowSigninDialog(JFrame owner, int mode, String username, char[] password)
	{
		return new UiSigninDlg(this, owner, mode, username, password);
	}

	protected void hideActiveWindowContent()
	{
		JFrame frame = getCurrentActiveFrame().getSwingFrame();
		if(frame != null)
		{
			frame.setGlassPane(new WindowObscurer());
			frame.getGlassPane().setVisible(true);
		}

		JDialog dialog = getCurrentActiveDialog();
		if(dialog != null)
		{
			dialog.setGlassPane(new WindowObscurer());
			dialog.getGlassPane().setVisible(true);
		}
	}

	protected void showActiveWindowContent()
	{
		JFrame frame = getCurrentActiveFrame().getSwingFrame();
		if(frame != null)
		{
			frame.getGlassPane().setVisible(false);
		}

		JDialog dialog = getCurrentActiveDialog();
		if(dialog != null)
		{
			dialog.getGlassPane().setVisible(false);
		}
	}

	protected void showSplashDlg(String text)
	{
		new UiSplashDlg(getLocalization(), text);
	}

	protected TemplateDlgInterface createTemplateDialog(ConfigInfo info, File defaultDetailsFile)
	{
		return new UiTemplateDlg(this, info, defaultDetailsFile);
	}

	public void updateIcon()
	{
		Image image = Utilities.getMartusIconImage();
		getSwingFrame().setIconImage(image);
		//com.apple.eawt.Application.getApplication().setDockIconImage(image);
	}

	private JFrame swingFrame;
	private UiMainPane mainPane;
	private FxMainStage mainStage;
}
