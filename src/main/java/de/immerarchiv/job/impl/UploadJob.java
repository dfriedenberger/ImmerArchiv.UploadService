package de.immerarchiv.job.impl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;




import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.immerarchiv.job.interfaces.Job;
import de.immerarchiv.job.model.BagIt;
import de.immerarchiv.job.model.FolderFile;
import de.immerarchiv.job.model.Priority;
import de.immerarchiv.repository.impl.RepositoryService;
import de.immerarchiv.util.interfaces.NameService;

public class UploadJob implements Job {

	private final static Logger logger = LogManager.getLogger(UploadJob.class);

	//upload_max_filesize in php.ini (apache hat ebenfalls POST_MAX_SIZE) 
	private static final long POST_MAX_SIZE = 1024 * 1024 * 2;

	private final RepositoryService repositoryService;
	private final BagIt bagIt;
	private final FolderFile file;
	private final String tempname;

	private long sended = 0;

	public UploadJob(RepositoryService repositoryService,NameService nameService,BagIt bagIt, FolderFile file) {

		this.repositoryService = repositoryService;
		this.bagIt = bagIt;
		this.file = file;
		this.tempname = nameService.generateTempName(file.getSafeName());

	}

	@Override
	public void init() throws Exception {
	}

	@Override
	public boolean next() throws Exception {
		

		if(sended < file.getLength())
		{
			int cnt = (int) Math.min(file.getLength() - sended, POST_MAX_SIZE );
			
			
			byte[] data = new byte[cnt];

			try(RandomAccessFile raf = new RandomAccessFile(file.getFile(), "r"))
			{
				raf.seek(sended);
				int r = raf.read(data);
				if(r != cnt)
					throw new IOException("Could not read complete Buffer cnt:"+cnt+" r:"+r+" file:"+file);
				sended += r;
			}
			
			repositoryService.putFilePart(tempname, data);
		
			return true;
		}
		repositoryService.appendFile(bagIt.getId(), file.getSafeName(), tempname, file.getMd5());
		
		
		logger.info("upload file {} to {}",file,bagIt);
		
	
		
		return false;
	}

	@Override
	public void finish() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Priority priority() {
		return Priority.Upload;
	}

	@Override
	public List<Job> getNext() {
		// TODO Auto-generated method stub
		return null;
	}

}
