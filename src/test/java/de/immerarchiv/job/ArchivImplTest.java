package de.immerarchiv.job;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.immerarchiv.job.impl.ArchivImpl;
import de.immerarchiv.job.interfaces.Archiv;
import de.immerarchiv.job.interfaces.FolderFileComparerService;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.DifferentBagItsException;
import de.immerarchiv.job.model.Folder;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.job.model.WrongCheckSumException;
import de.immerarchiv.job.model.WrongFilenameException;
import de.immerarchiv.util.interfaces.NameService;

public class ArchivImplTest {

	@Mock 
	FolderFileComparerService comparerService;

	@Mock 
	NameService nameService;

	@Mock
	Function<Map<BagIt, Double>, List<BagIt>> bestStrategy;
	
	@Captor
	private ArgumentCaptor<Map<BagIt, Double>> captor;
	
	@Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }
	
	private List<FolderFile> createBagitFiles(String keys) {
		List<FolderFile> files = new ArrayList<>();
		for(char k : keys.toCharArray())
		{
			FolderFile file = new FolderFile();
			file.setSafeName("name"+k);
			file.setMd5("md5"+k);
			files.add(file);
		}
		
		
		return files;
	}
	
	@Test
	public void testFindBagitsAll() throws DifferentBagItsException {
		
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService,nameService,bestStrategy);
		
		BagIt bagit = new BagIt();
		bagit.setRepo("repo2");
		bagit.setId("bagitid");
		bagit.setDescription("bagit 1");

		bagit.setFiles(0);
		bagit.setLastModified(0);

		List<FolderFile> bagItFiles = createBagitFiles("ab");
		archiv.addBagIt(bagit);
		archiv.addFile(bagit,bagItFiles);
		
		BagIt bagit2 = new BagIt();
		bagit2.setRepo("repo1");
		bagit2.setId("bagitid");
		bagit2.setDescription("bagit 1");
		bagit2.setFiles(0);
		bagit2.setLastModified(0);

		List<FolderFile> bagItFiles2 = createBagitFiles("ab");
		archiv.addBagIt(bagit2);
		archiv.addFile(bagit2,bagItFiles2);
		
		BagIt bagit3 = new BagIt();
		bagit3.setRepo("repo1");
		bagit3.setId("bagitidother");
		bagit3.setDescription("bagit 2");
		bagit3.setFiles(0);
		bagit3.setLastModified(0);

		List<FolderFile> bagItFiles3 = createBagitFiles("cd");
		archiv.addBagIt(bagit3);
		archiv.addFile(bagit3,bagItFiles3);
		
		List<FolderFile> files = new ArrayList<FolderFile>();
		
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles))).thenReturn(0.1);
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles2))).thenReturn(0.1); 
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles3))).thenReturn(0.0);

		
		
		List<BagIt> bagitsResult = new ArrayList<>();
		bagitsResult.add(bagit);
		bagitsResult.add(bagit2);

		when(bestStrategy.apply(captor.capture())).thenReturn(bagitsResult);
		
		List<BagIt> bagits = archiv.findBagits(null,files);
		
		
		Map<BagIt, Double> candidates = captor.getValue();
		assertEquals(2, candidates.size());
		assertEquals(0.1,candidates.get(bagit),0.0);
		assertEquals(0.1,candidates.get(bagit2),0.0);

		assertEquals(2, bagits.size());
		assertEquals("repo2",bagits.get(0).getRepo());
		assertEquals("bagitid",bagits.get(0).getId());
		assertEquals("repo1",bagits.get(1).getRepo());
		assertEquals("bagitid",bagits.get(1).getId());
		
		assertEquals(2,archiv.selectBagItsForRepository("repo1").size());
		assertEquals(1,archiv.selectBagItsForRepository("repo2").size());
	}


	@Test
	public void testFindBagitsAndCompleteWithSameId() throws DifferentBagItsException {
		
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService,nameService,bestStrategy);
		
		BagIt bagit = new BagIt();
		bagit.setRepo("repo2");
		bagit.setId("bagitid");
		bagit.setDescription("bagit 1");

		bagit.setFiles(0);
		bagit.setLastModified(0);

		List<FolderFile> bagItFiles = createBagitFiles("ab");
		archiv.addBagIt(bagit);
		archiv.addFile(bagit,bagItFiles);
		
		BagIt bagit2 = new BagIt();
		bagit2.setRepo("repo1");
		bagit2.setId("bagitid1");
		bagit2.setDescription("bagit 2");
		bagit2.setFiles(0);
		bagit2.setLastModified(0);

		List<FolderFile> bagItFiles2 = createBagitFiles("cd");
		archiv.addBagIt(bagit2);
		archiv.addFile(bagit2,bagItFiles2);
		
		BagIt bagit3 = new BagIt();
		bagit3.setRepo("repo1");
		bagit3.setId("bagitidother");
		bagit3.setDescription("bagit 3");
		bagit3.setFiles(0);
		bagit3.setLastModified(0);

		List<FolderFile> bagItFiles3 = createBagitFiles("e");
		archiv.addBagIt(bagit3);
		archiv.addFile(bagit3,bagItFiles3);
		
		
		List<FolderFile> files = new ArrayList<FolderFile>();
		

		when(comparerService.isSameFolder(same(files) , eq(bagItFiles))).thenReturn(1.0);
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles2))).thenReturn(0.0); //e.g falls bagit noch leer ist
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles3))).thenReturn(0.0);
		
		List<BagIt> bagitsResult = new ArrayList<>();
		bagitsResult.add(bagit);

		when(bestStrategy.apply(captor.capture())).thenReturn(bagitsResult);
		
		List<BagIt> bagits = archiv.findBagits(null,files);
		
		
		Map<BagIt, Double> candidates = captor.getValue();
		assertEquals(1, candidates.size());
		assertEquals(1.0,candidates.get(bagit),0.0);
		
		
		assertEquals(2, bagits.size());
		
		//test new Bagit
		assertEquals("repo1",bagits.get(1).getRepo());
		assertEquals("bagitid",bagits.get(1).getId());
		assertEquals("bagit 1",bagits.get(1).getDescription());

		assertEquals(3,archiv.selectBagItsForRepository("repo1").size());
		assertEquals(1,archiv.selectBagItsForRepository("repo2").size());
	}

	@Test
	public void testFindBagitsAndCompleteWithNewBagits() throws DifferentBagItsException {
		
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService,nameService,bestStrategy);
		
		BagIt bagit = new BagIt();
		bagit.setRepo("repo2");
		bagit.setId("bagitid");
		bagit.setDescription("bagit 1");
		bagit.setFiles(0);
		bagit.setLastModified(0);

		List<FolderFile> bagItFiles = createBagitFiles("ab");
		archiv.addBagIt(bagit);
		archiv.addFile(bagit,bagItFiles);
	
		
		BagIt bagit3 = new BagIt();
		bagit3.setRepo("repo1");
		bagit3.setId("bagitidother");
		bagit3.setDescription("bagit 2");
		bagit3.setFiles(0);
		bagit3.setLastModified(0);

		List<FolderFile> bagItFiles3 = createBagitFiles("cd");
		archiv.addBagIt(bagit3);
		archiv.addFile(bagit3,bagItFiles3);
		
		List<FolderFile> files = new ArrayList<FolderFile>();
		
			
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles))).thenReturn(1.0);
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles3))).thenReturn(0.0);
		
		List<BagIt> bagitsResult = new ArrayList<>();
		bagitsResult.add(bagit);

		when(bestStrategy.apply(captor.capture())).thenReturn(bagitsResult);
		
		List<BagIt> bagits = archiv.findBagits(null,files);
		
		
		Map<BagIt, Double> candidates = captor.getValue();
		assertEquals(1, candidates.size());
		assertEquals(1.0,candidates.get(bagit),0.0);
		
		assertEquals(2, bagits.size());
		
		//test new Bagit
		assertEquals("repo1",bagits.get(1).getRepo());
		assertEquals("bagitid",bagits.get(1).getId());
		assertEquals("bagit 1",bagits.get(1).getDescription());
		
		
		assertEquals(2,archiv.selectBagItsForRepository("repo1").size());
		assertEquals(1,archiv.selectBagItsForRepository("repo2").size());
	}
	
	@Test
	public void testFindBagitsAndCreateNewBagits() throws DifferentBagItsException {
		
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService,nameService,bestStrategy);
				
		BagIt bagit3 = new BagIt();
		bagit3.setRepo("repo1");
		bagit3.setId("bagitidother");
		bagit3.setDescription("bagit 1");
		bagit3.setFiles(0);
		bagit3.setLastModified(0);

		List<FolderFile> bagItFiles3 = createBagitFiles("cd");
		archiv.addBagIt(bagit3);
		archiv.addFile(bagit3,bagItFiles3);
		
		List<FolderFile> files = new ArrayList<FolderFile>();
	
		List<BagIt> bagitsResult = new ArrayList<>();
		when(bestStrategy.apply(captor.capture())).thenReturn(bagitsResult);
		
		when(comparerService.isSameFolder(same(files) , eq(bagItFiles3))).thenReturn(0.0);

		Folder folder = new Folder();
		when(nameService.createDescription(folder)).thenReturn("folder description");
		
		
		List<BagIt> bagits = archiv.findBagits(folder,files);
		
		
		Map<BagIt, Double> candidates = captor.getValue();
		assertEquals(0, candidates.size());
		
		
		
		assertEquals(2, bagits.size());
		
		//test new Bagit
		assertEquals("repo1",bagits.get(0).getRepo());
		assertTrue(Pattern.matches("[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}", bagits.get(0).getId()));
		assertEquals("folder description",bagits.get(0).getDescription());
		assertEquals("repo2",bagits.get(1).getRepo());
		assertEquals(bagits.get(0).getId(),bagits.get(1).getId());
		assertEquals(bagits.get(0).getDescription(),bagits.get(1).getDescription());
		
		assertEquals(2,archiv.selectBagItsForRepository("repo1").size());
		assertEquals(1,archiv.selectBagItsForRepository("repo2").size());

	}
	

	

	@Test
	public void testFileExists() throws WrongCheckSumException, WrongFilenameException
	{
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService,nameService,bestStrategy);
		
		BagIt bagIt = new BagIt();
		bagIt.setRepo("repo1");
		bagIt.setId("bagitidother");
		bagIt.setFiles(0);
		bagIt.setLastModified(0);
		
		
		
		FolderFile file = new FolderFile();
		file.setSafeName("name");
		file.setMd5("md51");
		file.setLength(1234);
		
		List<FolderFile> bagItFiles = new ArrayList<>();
		bagItFiles.add(file);
		archiv.addBagIt(bagIt);
		archiv.addFile(bagIt,bagItFiles);
		
		
		
		FolderFile file1 = new FolderFile();
		file1.setSafeName("name");
		file1.setMd5("md51");
		file1.setLength(1234);
		
		assertTrue(archiv.fileExists(bagIt, file1));
		
		FolderFile file2 = new FolderFile();
		file2.setSafeName("name2");
		file2.setMd5("md52");
		file2.setLength(1234);
		
		assertFalse(archiv.fileExists(bagIt, file2));
		
	}

	@Test(expected = WrongCheckSumException.class)
	public void testFileExistsWithWrongCheckSum() throws WrongCheckSumException, WrongFilenameException
	{
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService,nameService,bestStrategy);
		
		BagIt bagIt = new BagIt();
		bagIt.setRepo("repo1");
		bagIt.setId("bagitidother");
		bagIt.setFiles(0);
		bagIt.setLastModified(0);
		
		
		
		FolderFile file = new FolderFile();
		file.setSafeName("name");
		file.setMd5("md51");
		file.setLength(1234);
		
		List<FolderFile> bagItFiles = new ArrayList<>();
		bagItFiles.add(file);
		archiv.addBagIt(bagIt);
		archiv.addFile(bagIt,bagItFiles);
		
		
		
		FolderFile file1 = new FolderFile();
		file1.setSafeName("name");
		file1.setMd5("md51-wrong");
		file1.setLength(1234);
		
		archiv.fileExists(bagIt, file1);
		
	
		
	}
	
	@Test(expected = WrongFilenameException.class)
	public void testFileExistsWithWrongName() throws WrongFilenameException, WrongCheckSumException
	{
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService,nameService,bestStrategy);
		
		BagIt bagIt = new BagIt();
		bagIt.setRepo("repo1");
		bagIt.setId("bagitidother");
		bagIt.setFiles(0);
		bagIt.setLastModified(0);
		
		
		
		FolderFile file = new FolderFile();
		file.setSafeName("name");
		file.setMd5("md51");
		file.setLength(1234);
		
		List<FolderFile> bagItFiles = new ArrayList<>();
		bagItFiles.add(file);
		archiv.addBagIt(bagIt);
		archiv.addFile(bagIt,bagItFiles);
		
		
		
		FolderFile file1 = new FolderFile();
		file1.setSafeName("name1");
		file1.setMd5("md51");
		file1.setLength(1234);
		
		archiv.fileExists(bagIt, file1);
		
	
		
	}
	
	@Test
	public void testAddBagIt()
	{
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService,nameService,bestStrategy);
		
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
		Archiv archiv = new ArchivImpl(new String[]{ "repo1", "repo2"},comparerService,nameService,bestStrategy);
		
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
