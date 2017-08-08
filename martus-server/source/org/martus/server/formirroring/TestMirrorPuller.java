package org.martus.server.formirroring;

import java.io.File;

import org.martus.common.LoggerToNull;
import org.martus.common.MartusUtilities;
import org.martus.common.crypto.MockMartusSecuritySha1;
import org.martus.server.forclients.MockMartusServer;
import org.martus.util.TestCaseEnhanced;

public class TestMirrorPuller extends TestCaseEnhanced
{
	public TestMirrorPuller(String name) 
	{
		super(name);
	}

	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();

		logger = new LoggerToNull();

		clientSecurity1 = MockMartusSecuritySha1.createClient();
		clientSecurity2 = MockMartusSecuritySha1.createOtherClient();
	}
	
	public void testLoadMirrorsToCall() throws Exception
	{
		MockMartusServer noCallsToMakeCore = new MockMartusServer(this);
		MirrorPuller noCallsToMake = new MirrorPuller(noCallsToMakeCore, logger);
		noCallsToMake.createMirroringRetrievers();
		assertEquals(0, noCallsToMake.retrieversWeWillCall.size());
		noCallsToMakeCore.deleteAllFiles();
		
		MockMartusServer twoCallsToMakeCore = new MockMartusServer(this);
		twoCallsToMakeCore.enterSecureMode();
		File mirrorsWhoWeCall = new File(twoCallsToMakeCore.getStartupConfigDirectory(), "mirrorsWhoWeCall");
		mirrorsWhoWeCall.mkdirs();
		File pubKeyFile1 = new File(mirrorsWhoWeCall, "code=1.2.3.4.5-ip=1.2.3.4.txt");
		MartusUtilities.exportServerPublicKey(clientSecurity1, pubKeyFile1);
		File pubKeyFile2 = new File(mirrorsWhoWeCall, "code=2.3.4.5.6-ip=2.3.4.5.txt");
		MartusUtilities.exportServerPublicKey(clientSecurity2, pubKeyFile2);
		MirrorPuller twoCallsToMake = new MirrorPuller(twoCallsToMakeCore, logger);
		twoCallsToMake.createMirroringRetrievers();
		assertEquals(2, twoCallsToMake.retrieversWeWillCall.size());
		mirrorsWhoWeCall.delete();
		twoCallsToMakeCore.deleteAllFiles();
	}
	
	LoggerToNull logger;
	MockMartusSecuritySha1 clientSecurity1;
	MockMartusSecuritySha1 clientSecurity2;
}
