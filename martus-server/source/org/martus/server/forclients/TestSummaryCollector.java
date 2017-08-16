/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2007, Beneficent
Technology, Inc. (The Benetech Initiative).

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

package org.martus.server.forclients;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.martus.common.bulletin.Bulletin;
import org.martus.common.crypto.MockMartusSecuritySha2;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.ReadableDatabase;
import org.martus.common.network.NetworkInterfaceConstants;
import org.martus.common.packet.BulletinHeaderPacket;
import org.martus.common.packet.UniversalId;
import org.martus.server.main.MartusServer;
import org.martus.server.main.ServerBulletinStore;
import org.martus.server.main.TestServerBulletinStore;
import org.martus.util.TestCaseEnhanced;


public class TestSummaryCollector extends TestCaseEnhanced
{
	public TestSummaryCollector(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		server = new MockMartusServer(this);
		authorSecurity = MockMartusSecuritySha2.createClient();
		ServerBulletinStore store = server.getStore();

		original = new Bulletin(authorSecurity);
		original.setImmutable();
		File zip1 = TestServerBulletinStore.createZipFile(original, authorSecurity);
		store.saveZipFileToDatabase(zip1, authorSecurity.getPublicKeyString());
		zip1.delete();
		
		firstClone = new Bulletin(authorSecurity);
		firstClone.createDraftCopyOf(original, store.getDatabase());
		firstClone.setImmutable();
		File zip2 = TestServerBulletinStore.createZipFile(firstClone, authorSecurity);
		store.saveZipFileToDatabase(zip2, authorSecurity.getPublicKeyString());
		zip2.delete();

		clone = new Bulletin(authorSecurity); 
		clone.createDraftCopyOf(firstClone, store.getDatabase());
		File zip3 = TestServerBulletinStore.createZipFile(clone, authorSecurity);
		store.saveZipFileToDatabase(zip3, authorSecurity.getPublicKeyString());
		zip3.delete();
		
	}
	
	public void tearDown() throws Exception
	{
		server.deleteAllFiles();
		super.tearDown();
	}

	class MockSummaryCollector extends SummaryCollector
	{
		protected MockSummaryCollector(MartusServer serverToUse, String authorAccountToUse, Vector retrieveTagsToUse)
		{
			super(serverToUse, authorAccountToUse, retrieveTagsToUse);
		}

		public String callerAccountId()
		{
			return authorAccountId;
		}

		public boolean isWanted(DatabaseKey key)
		{
			return true;
		}

		public boolean isAuthorized(BulletinHeaderPacket bhp)
		{
			return bhp.getAccountId().equals(callerAccountId());
		}
		
	}
	
	public void testSummarCollectorOmitsOldVersions() throws Exception
	{
		
		Set leafUids = server.getStore().getAllBulletinLeafUids();
		assertEquals(1, leafUids.size());
		
		String authorId = authorSecurity.getPublicKeyString();
		SummaryCollector collector = new MockSummaryCollector(server, authorId, new Vector());
		Vector localIds = collector.collectSummaries();
		assertEquals(1, localIds.size());
		assertEquals(clone.getLocalId() + "=" + clone.getFieldDataPacket().getLocalId(), localIds.get(0));
	}
	
	public void testIsLeaf() throws Exception
	{
		ServerBulletinStore store = server.getStore();
		Set leafUids = store.getAllBulletinLeafUids();
		assertTrue("Must have at least one leaf", leafUids.size() > 0);
		int i = 0;
		for(Iterator iter = leafUids.iterator(); iter.hasNext();)
		{
			UniversalId leafUid = (UniversalId) iter.next();
			assertTrue("Should be leaf:"+i, store.isLeaf(leafUid));
			++i;
		}
		
		assertNotContains(original.getUniversalId(), leafUids);
		assertTrue(store.hasNewerRevision(original.getUniversalId()));
	}
	
	public void testTags() throws Exception
	{
		ReadableDatabase db = server.getDatabase();
		BulletinHeaderPacket bhp = clone.getBulletinHeaderPacket();
		String fdpLocalId = bhp.getFieldDataPacketId();
		Vector tags = new Vector();
		
		String minimalSummary = bhp.getLocalId() + "=" + fdpLocalId;
		
		String noTags = SummaryCollector.extractSummary(bhp, db, tags, server.getLogger());
		assertEquals(minimalSummary, noTags);
		
		String history = original.getLocalId() + " " + firstClone.getLocalId() + " ";

		String expectedSizeDateHistoryStart = minimalSummary + "=";
		String expectedSizeDateHistoryEnd = "=" + bhp.getLastSavedTime() + "=" + history;

		tags.add(NetworkInterfaceConstants.TAG_BULLETIN_SIZE);
		tags.add(NetworkInterfaceConstants.TAG_BULLETIN_DATE_SAVED);
		tags.add(NetworkInterfaceConstants.TAG_BULLETIN_HISTORY);
		String gotSizeDateHistory = SummaryCollector.extractSummary(bhp, db, tags, server.getLogger());
		assertStartsWith(expectedSizeDateHistoryStart, gotSizeDateHistory);
		assertEndsWith(expectedSizeDateHistoryEnd, gotSizeDateHistory);
		
		
		String expectedHistoryDateSizeStart = minimalSummary + "=" + history + "=" + bhp.getLastSavedTime() +"=";

		tags.clear();
		tags.add(NetworkInterfaceConstants.TAG_BULLETIN_HISTORY);
		tags.add(NetworkInterfaceConstants.TAG_BULLETIN_DATE_SAVED);
		tags.add(NetworkInterfaceConstants.TAG_BULLETIN_SIZE);
		String gotHistoryDateSize = SummaryCollector.extractSummary(bhp, db, tags, server.getLogger());
		assertStartsWith(expectedHistoryDateSizeStart, gotHistoryDateSize);
	}

	AbstractMockMartusServer server;
	MockMartusSecuritySha2 authorSecurity;
	Bulletin original;
	Bulletin firstClone;
	Bulletin clone;
}
