package de.immerarchiv.repository.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.immerarchiv.repository.model.AppendFile;
import de.immerarchiv.repository.model.BagItInfo;
import de.immerarchiv.repository.model.Checksum;
import de.immerarchiv.repository.model.Create;
import de.immerarchiv.repository.model.FileInfo;
import de.immerarchiv.repository.model.GetFilePart;
import de.immerarchiv.repository.model.ListAll;
import de.immerarchiv.repository.model.ListOne;
import de.immerarchiv.repository.model.MetaDataKeys;
import de.immerarchiv.repository.model.RepositoryEndpoint;
import de.immerarchiv.repository.model.Status;



public class RepositoryService implements MetaDataKeys {

	private final String token;
	private final String name;
	private final String url;
	private final String id;
	private final static Map<String,RepositoryEndpoint> cache = new HashMap<String,RepositoryEndpoint>();

	private final static Logger logger = LogManager.getLogger(RepositoryService.class);

	public RepositoryService(String id,String url, String name, String token) {

		this.id = id;
		this.url = url;
		this.name = name;
		this.token = token;

	}


	public String getId() {
		return id;
	}



	private CloseableHttpClient getHttpClient() throws GeneralSecurityException {

		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				return true;
			}
		}).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);

		// return HttpClients.createDefault();
		return HttpClients.custom().setSSLSocketFactory(sslsf).build();
	}

	private RepositoryEndpoint getEndpoint() throws IOException, GeneralSecurityException {

		String key = this.url + ":" + name;
		if(cache.containsKey(key))
			return cache.get(key);
		

		HttpGet httpget = new HttpGet(url + "/" + "info.json");

		
		CloseableHttpResponse response = execute(httpget);
		
		try {

			// Display status code
			logger.info(response.getStatusLine());
			// Display response
			HttpEntity entity = response.getEntity();
			String body = EntityUtils.toString(entity);

			logger.info("Response {}", body);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree = mapper.readTree(body);

			String state = tree.path("state").asText();
			String message = tree.path("message").asText();

			if (!state.equals("ok"))
				throw new IOException("Error (" + state + ") : " + message);

			String vers = tree.path("api_version").asText();
			if (!vers.equals("1.1"))
				throw new IOException("Wrong api_version: " + vers);

			RepositoryEndpoint repository = new RepositoryEndpoint();
			repository.grantType = tree.path("grant_type").asText();
			repository.checkSum = tree.path("check_sum").asText();

			JsonNode modules = tree.path("modules");
			repository.status = modules.path("status").asText();
			repository.listall = modules.path("listall").asText();
			repository.listone = modules.path("listone").asText();
			repository.getfilepart = modules.path("getfilepart").asText();
			repository.downloadfile = modules.path("downloadfile").asText();
			repository.checksum = modules.path("checksum").asText();
			repository.create = modules.path("create").asText();
			repository.putfilepart = modules.path("putfilepart").asText();
			repository.appendfile = modules.path("appendfile").asText();

			cache.put(key,repository);
			return repository;
		} finally {
			response.close();
		}
	}

	private CloseableHttpResponse execute(HttpUriRequest httpMethod) throws GeneralSecurityException, IOException {
		
		int hardTimeout = 60 * 1000;
		TimerTask task = new TimerTask() {
		    @Override
		    public void run() {
		        if (httpMethod != null) {
		        	logger.error("abort execute after 60 seconds");
		        	httpMethod.abort();
		        }
		    }
		};
		Timer timer = new Timer(true);
		
		timer.schedule(task, hardTimeout);
		
		
		CloseableHttpClient httpclient = getHttpClient();

		logger.trace("Request {}",httpMethod);
		CloseableHttpResponse response = httpclient.execute(httpMethod);
		logger.trace("Response {}",response);
		
        timer.cancel();

		return response;
		
	}


	private JsonNode resolve(String method, Object req) throws IOException, GeneralSecurityException {


		HttpPost httppost = new HttpPost(url + "/" + method);
		httppost.setHeader("Authorization", "Bearer " + token);

		ObjectMapper mapper = new ObjectMapper();

		String data = mapper.writeValueAsString(req);
		logger.info("Request {}", data);

		StringEntity input = new StringEntity(data);
		input.setContentType("application/json");
		httppost.setEntity(input);

		CloseableHttpResponse response = execute(httppost);

		try {

			// Display status code
			logger.info(response.getStatusLine());

			// Display response
			HttpEntity entity = response.getEntity();
			String body = EntityUtils.toString(entity);

			logger.info("Response {}", body);

			JsonNode tree = mapper.readTree(body);

			String state = tree.path("state").asText();
			String message = tree.path("message").asText();

			if (!state.equals("ok"))
				throw new IOException("Error (" + state + ") : " + message);

			return tree;

		} finally {
			response.close();
		}
	}

	private JsonNode resolveData(String method, Map<String,String> reg, byte[] data) throws IOException, GeneralSecurityException {


		HttpPost httppost = new HttpPost(url + "/" + method);
		httppost.setHeader("Authorization", "Bearer " + token);

		ObjectMapper mapper = new ObjectMapper();

		MultipartEntityBuilder builder = MultipartEntityBuilder.create()
              .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		for(Entry<String, String> e : reg.entrySet())
			builder.addTextBody(e.getKey(), e.getValue());
			
		HttpEntity input = builder
				.addBinaryBody("data", data,ContentType.DEFAULT_BINARY,"xx2.bin")
				.build();

		httppost.setEntity(input);
		
	
		CloseableHttpResponse response = execute(httppost);

		try {

			// Display status code
			logger.info(response.getStatusLine());

			// Display response
			HttpEntity entity = response.getEntity();
			String body = EntityUtils.toString(entity);

			logger.info("Response {}", body);

			JsonNode tree = mapper.readTree(body);

			String state = tree.path("state").asText();
			String message = tree.path("message").asText();

			if (!state.equals("ok"))
				throw new IOException("Error (" + state + ") : " + message);

			return tree;

		} finally {
			response.close();
		}
	}
	
	private void write(String method, Object req, OutputStream out) throws IOException, GeneralSecurityException {

		HttpPost httppost = new HttpPost(url + "/" + method);
		httppost.setHeader("Authorization", "Bearer " + token);

		ObjectMapper mapper = new ObjectMapper();

		String data = mapper.writeValueAsString(req);
		logger.info("Request {}", data);

		StringEntity input = new StringEntity(data);
		input.setContentType("application/json");
		httppost.setEntity(input);

		CloseableHttpResponse response = execute(httppost);
		try {

			// Display status code
			logger.info(response.getStatusLine());
			// Display response
			HttpEntity entity = response.getEntity();
			InputStream in = entity.getContent();
			copy(in,out);
		} finally {
			response.close();
		}
	}

	
	private void copy(InputStream in, OutputStream out) throws IOException {
		
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) != -1) {
		    out.write(buffer, 0, len);
		}
		
	}

	
	public MetaDataList resolveStatus() throws IOException, GeneralSecurityException, ParseException {
		
		RepositoryEndpoint endpoint = getEndpoint();
		Status req = new Status();

		req.repository = name;

		JsonNode tree = resolve(endpoint.status, req);
		

		MetaDataList metadata = new MetaDataList();
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US); ; //"Sun, 04 Dec 2016 13:42:04 +0000"
		Date date = sdf.parse(tree.path("lastmodified").asText());
		metadata.set(mdDateLastModified, date);
		metadata.set(mdRepositoryCntFiles, tree.path("files").asLong()); 
		metadata.set(mdRepositoryCntBagits, tree.path("bagits").asLong()); 
		metadata.set(mdRepositorySize, tree.path("size").asLong()); 
		metadata.set(mdRepositoryMaxSize, tree.path("maxsize").asLong());
		
		return metadata;

	}

	public String create(String bagItId, BagItInfo info) throws IOException, GeneralSecurityException {
		
		RepositoryEndpoint endpoint = getEndpoint();
		
		Create req = new Create();

		req.repository = name;
		req.bagit = bagItId;
		req.info = info.toArray();

		JsonNode tree = resolve(endpoint.create, req);
		return tree.path("bagit").asText();
	}
	


	public void putFilePart(String tempname, byte[] data) throws IOException, GeneralSecurityException {

		RepositoryEndpoint endpoint = getEndpoint();
		
		Map<String,String> req = new LinkedHashMap<>();
		req.put("repository",name);
		req.put("tempname",tempname);

		resolveData(endpoint.putfilepart, req, data);
	}
	
	public String appendFile(String bagItId, String filename, String tempname, String md5CheckSum) throws IOException, GeneralSecurityException {
		RepositoryEndpoint endpoint = getEndpoint();
		
		AppendFile req = new AppendFile();

		req.repository = name;
		req.bagit = bagItId;
		req.name = filename;
		req.tempname = tempname;
		req.md5 = md5CheckSum;
		req.mode = "complete";
		
		JsonNode tree = resolve(endpoint.appendfile, req);
		return tree.path("md5").asText();
	}
	
	public Map<String,MetaDataList> resolveBagits(int skip, int take) throws IOException, GeneralSecurityException, ParseException {

		RepositoryEndpoint endpoint = getEndpoint();

		ListAll req = new ListAll();

		req.repository = name;
		req.skip = skip;
		req.take = take;

		JsonNode tree = resolve(endpoint.listall, req);

		JsonNode bagits = tree.path("bagits");

		Map<String,MetaDataList>  bagitList = new HashMap<String,MetaDataList>();
		for (final JsonNode bagit : bagits) {
			String bagitId = bagit.path("bagit").asText();
			JsonNode infos = bagit.path("info");
			JsonNode status = bagit.path("status");

			MetaDataList metadata = new MetaDataList();
			for (final JsonNode info : infos) {
				String key = info.fieldNames().next();
				String value = info.path(key).asText();
				
				if(key.equals("Description"))
					metadata.add(mdDescription,value);
				else
					throw new IOException("Unknown Key "+ key);
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US); ; //"Sun, 04 Dec 2016 13:42:04 +0000"
			Date date = sdf.parse(status.path("lastmodified").asText());
			metadata.set(mdDateLastModified, date);
			metadata.set(mdBagitCntFiles, status.path("files").asLong()); 
			metadata.set(mdBagitSize, status.path("size").asLong()); 
			bagitList.put(bagitId,metadata);
		}
		return bagitList;

	}

	public List<FileInfo> resolveBagit(String bagit) throws IOException, GeneralSecurityException {

		RepositoryEndpoint endpoint = getEndpoint();

		ListOne req = new ListOne();

		req.repository = name;
		req.bagit = bagit;

		JsonNode tree = resolve(endpoint.listone, req);

		JsonNode data = tree.path("data");
		List<FileInfo> fileList = new ArrayList<FileInfo>();

		for (final JsonNode entry : data) {
			
			FileInfo fileInfo = new FileInfo();
			fileInfo.name = entry.path("name").asText();
			fileInfo.length = Long.parseLong(entry.path("length").asText());
			fileInfo.CheckSumKey = endpoint.checkSum;
			fileInfo.CheckSumValue = entry.path(endpoint.checkSum).asText();
			fileList.add(fileInfo);
			
		}
		return fileList;
	}

	public byte[] getFilePart(String bagit, String file, long offset, int maxlength) throws IOException, GeneralSecurityException {

		RepositoryEndpoint endpoint = getEndpoint();

		GetFilePart req = new GetFilePart();

		req.repository = name;
		req.bagit = bagit;
		req.name = file;
		req.offset = offset;
		req.maxlength = maxlength;
		
		
		ByteArrayOutputStream out = null;
		try
		{
			out = new ByteArrayOutputStream();
   		 	write(endpoint.getfilepart, req,out);
		}
		finally
		{
			if(out != null)
				out.close();
		}
	
		return out.toByteArray();
		
	}

	public void downloadFile(String bagit, String file,String tempFilename) throws IOException, GeneralSecurityException {
		
		RepositoryEndpoint endpoint = getEndpoint();

		GetFilePart req = new GetFilePart();

		req.repository = name;
		req.bagit = bagit;
		req.name = file;
	
		OutputStream out = null;
		try
		{
			out = new FileOutputStream(tempFilename);
   		 	write(endpoint.downloadfile, req,out);
		}
		finally
		{
			if(out != null)
				out.close();
		}
	}

	public String checksum(String bagit, String file) throws IOException, GeneralSecurityException {
		
		RepositoryEndpoint endpoint = getEndpoint();

		Checksum req = new Checksum();

		req.repository = name;
		req.bagit = bagit;
		req.name = file;
	

		JsonNode tree = resolve(endpoint.checksum, req);
		
		return tree.path(endpoint.checkSum).asText();
		
	}


	




	


	
}
