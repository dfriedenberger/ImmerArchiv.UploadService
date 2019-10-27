package de.immerarchiv.job;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.immerarchiv.job.impl.ArchivImpl;
import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.FolderFileComparerService;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.job.model.WrongCheckSumException;

public class ArchivImplTest {

	@Mock 
	FolderFileComparerService comparerService;
	
	@Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }
	
	private List<FolderFile> createBagitFiles(String keys) {
		List<FolderFile> files = new ArrayList<>();
		for(char k : keys.toCharArray())
		{
			FolderFile file = new FolderFile();
			file.setName("name"+k);
			file.setMd5("md5"+k);
			files.add(file);
		}
		
		
		return files;
	}
	
	@Test
	public void testFindBagitsAll() {
		
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService);
		
		BagIt bagit = new BagIt();
		bagit.setRepo("repo2");
		bagit.setId("bagitid");
		bagit.setFiles(0);
		bagit.setLastModified(0);

		List<FolderFile> bagItFiles = createBagitFiles("ab");
		archiv.addBagIt(bagit);
		archiv.addFile(bagit,bagItFiles);
		
		BagIt bagit2 = new BagIt();
		bagit2.setRepo("repo1");
		bagit2.setId("bagitid");
		bagit2.setFiles(0);
		bagit2.setLastModified(0);

		List<FolderFile> bagItFiles2 = createBagitFiles("ab");
		archiv.addBagIt(bagit2);
		archiv.addFile(bagit2,bagItFiles2);
		
		BagIt bagit3 = new BagIt();
		bagit3.setRepo("repo1");
		bagit3.setId("bagitidother");
		bagit3.setFiles(0);
		bagit3.setLastModified(0);

		List<FolderFile> bagItFiles3 = createBagitFiles("cd");
		archiv.addBagIt(bagit3);
		archiv.addFile(bagit3,bagItFiles3);
		
		List<FolderFile> files = new ArrayList<FolderFile>();
		
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles))).thenReturn(true);
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles2))).thenReturn(true); 
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles3))).thenReturn(false);

		
		List<BagIt> bagits = archiv.findBagits(files);
		
		assertEquals(2, bagits.size());
		assertEquals("repo1",bagits.get(0).getRepo());
		assertEquals("bagitid",bagits.get(0).getId());
		assertEquals("repo2",bagits.get(1).getRepo());
		assertEquals("bagitid",bagits.get(1).getId());
		
		assertEquals(2,archiv.selectBagItsForRepository("repo1").size());
		assertEquals(1,archiv.selectBagItsForRepository("repo2").size());
	}

	


	@Test
	public void testFindBagitsAndCompleteWithSameId() {
		
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService);
		
		BagIt bagit = new BagIt();
		bagit.setRepo("repo2");
		bagit.setId("bagitid");
		bagit.setFiles(0);
		bagit.setLastModified(0);

		List<FolderFile> bagItFiles = createBagitFiles("ab");
		archiv.addBagIt(bagit);
		archiv.addFile(bagit,bagItFiles);
		
		BagIt bagit2 = new BagIt();
		bagit2.setRepo("repo1");
		bagit2.setId("bagitid1");
		bagit2.setFiles(0);
		bagit2.setLastModified(0);

		List<FolderFile> bagItFiles2 = createBagitFiles("cd");
		archiv.addBagIt(bagit2);
		archiv.addFile(bagit2,bagItFiles2);
		
		BagIt bagit3 = new BagIt();
		bagit3.setRepo("repo1");
		bagit3.setId("bagitidother");
		bagit3.setFiles(0);
		bagit3.setLastModified(0);

		List<FolderFile> bagItFiles3 = createBagitFiles("e");
		archiv.addBagIt(bagit3);
		archiv.addFile(bagit3,bagItFiles3);
		
		
		List<FolderFile> files = new ArrayList<FolderFile>();
		

		when(comparerService.isSameFolder(same(files) , eq(bagItFiles))).thenReturn(true);
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles2))).thenReturn(false); //e.g if is empty matcher failed
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles3))).thenReturn(false);
		
		
		List<BagIt> bagits = archiv.findBagits(files);
		
		assertEquals(2, bagits.size());
		
		assertEquals(3,archiv.selectBagItsForRepository("repo1").size());
		assertEquals(1,archiv.selectBagItsForRepository("repo2").size());
	}

	@Test
	public void testFindBagitsAndCompleteWithNewBagits() {
		
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService);
		
		BagIt bagit = new BagIt();
		bagit.setRepo("repo2");
		bagit.setId("bagitid");
		bagit.setFiles(0);
		bagit.setLastModified(0);

		List<FolderFile> bagItFiles = createBagitFiles("ab");
		archiv.addBagIt(bagit);
		archiv.addFile(bagit,bagItFiles);
	
		
		BagIt bagit3 = new BagIt();
		bagit3.setRepo("repo1");
		bagit3.setId("bagitidother");
		bagit3.setFiles(0);
		bagit3.setLastModified(0);

		List<FolderFile> bagItFiles3 = createBagitFiles("cd");
		archiv.addBagIt(bagit3);
		archiv.addFile(bagit3,bagItFiles3);
		
		List<FolderFile> files = new ArrayList<FolderFile>();
		
			
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles))).thenReturn(true);
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles3))).thenReturn(false);
		
		
		List<BagIt> bagits = archiv.findBagits(files);
		
		assertEquals(2, bagits.size());
		
		assertEquals(2,archiv.selectBagItsForRepository("repo1").size());
		assertEquals(1,archiv.selectBagItsForRepository("repo2").size());
	}
	
	@Test
	public void testFindBagitsAndCreateNewBagits() {
		
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService);
				
		BagIt bagit3 = new BagIt();
		bagit3.setRepo("repo1");
		bagit3.setId("bagitidother");
		bagit3.setFiles(0);
		bagit3.setLastModified(0);

		List<FolderFile> bagItFiles3 = createBagitFiles("cd");
		archiv.addBagIt(bagit3);
		archiv.addFile(bagit3,bagItFiles3);
		
		List<FolderFile> files = new ArrayList<FolderFile>();
	
		
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles3))).thenReturn(false);

		
		List<BagIt> bagits = archiv.findBagits(files);
		
		assertEquals(2, bagits.size());
		
		assertEquals(2,archiv.selectBagItsForRepository("repo1").size());
		assertEquals(1,archiv.selectBagItsForRepository("repo2").size());

	}
	
	

	@Test
	public void testFileExists() throws WrongCheckSumException
	{
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService);
		
		BagIt bagIt = new BagIt();
		bagIt.setRepo("repo1");
		bagIt.setId("bagitidother");
		bagIt.setFiles(0);
		bagIt.setLastModified(0);
		
		
		
		FolderFile file = new FolderFile();
		file.setName("name");
		file.setMd5("md51");
		file.setLength(1234);
		
		List<FolderFile> bagItFiles = new ArrayList<>();
		bagItFiles.add(file);
		archiv.addBagIt(bagIt);
		archiv.addFile(bagIt,bagItFiles);
		
		
		
		FolderFile file1 = new FolderFile();
		file1.setName("name");
		file1.setMd5("md51");
		file1.setLength(1234);
		
		assertTrue(archiv.fileExists(bagIt, file1));
		
		FolderFile file2 = new FolderFile();
		file2.setName("name2");
		file2.setMd5("md52");
		file2.setLength(1234);
		
		assertFalse(archiv.fileExists(bagIt, file2));
		
	}

	@Test(expected = WrongCheckSumException.class)
	public void testFileExistsWithWrongCheckSum() throws WrongCheckSumException
	{
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService);
		
		BagIt bagIt = new BagIt();
		bagIt.setRepo("repo1");
		bagIt.setId("bagitidother");
		bagIt.setFiles(0);
		bagIt.setLastModified(0);
		
		
		
		FolderFile file = new FolderFile();
		file.setName("name");
		file.setMd5("md51");
		file.setLength(1234);
		
		List<FolderFile> bagItFiles = new ArrayList<>();
		bagItFiles.add(file);
		archiv.addBagIt(bagIt);
		archiv.addFile(bagIt,bagItFiles);
		
		
		
		FolderFile file1 = new FolderFile();
		file1.setName("name");
		file1.setMd5("md51-wrong");
		file1.setLength(1234);
		
		archiv.fileExists(bagIt, file1);
		
	
		
	}
	
	
	@Test
	public void testAddBagIt()
	{
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService);
		
		BagIt bagIt = new BagIt();
		bagIt.setRepo("repo1");
		bagIt.setId("bagitidother");
		bagIt.setFiles(0);
		bagIt.setLastModified(0);
		
		archiv.addBagIt(bagIt);
	}
	
	@Test
	public void testSelectBagItsForRepository()
	{
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService);
		
		BagIt bagit = new BagIt();
		bagit.setRepo("repo2");
		bagit.setId("bagitid");
		bagit.setFiles(0);
		bagit.setLastModified(0);

		archiv.addBagIt(bagit);
		
		BagIt bagit2 = new BagIt();
		bagit2.setRepo("repo1");
		bagit2.setId("bagitid");
		bagit2.setFiles(0);
		bagit2.setLastModified(0);

		archiv.addBagIt(bagit2);

		
		BagIt bagit3 = new BagIt();
		bagit3.setRepo("repo1");
		bagit3.setId("bagitidother");
		bagit3.setFiles(0);
		bagit3.setLastModified(0);

		archiv.addBagIt(bagit3);

		
		List<BagIt> bagits = archiv.selectBagItsForRepository("repo1");
		
		assertEquals(2, bagits.size());
		
	}
	

	

	
}
