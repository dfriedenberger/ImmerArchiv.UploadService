package de.immerarchiv.repository.model;

public interface MetaDataKeys {
	
	
	final static String mdRepositorySize             = "repository.size";
	final static String mdRepositoryMaxSize          = "repository.maxsize";
	final static String mdRepositoryCntFiles         = "repository.cnt.files";
	final static String mdRepositoryCntBagits        = "repository.cnt.bagits";
	final static String mdBagitCntFiles              = "bagit.cnt.files";
	final static String mdBagitSize                  = "bagit.size";

	
	final static String mdDateLastModified           = "date.lastmodified";
	final static String mdDescription                = "description";
	
	final static String mdFileSize             = "file.size";
	final static String mdFileExtension        = "file.extension";
	final static String mdFileMIMEType         = "file.mime-type";
	final static String mdFileCkSumPrefix      = "file.chksum.";
	final static String mdFileCkSumMd5 = mdFileCkSumPrefix + "md5";
	final static String mdFileCkSumValid       = "file.chksum-valid";

	final static String mdImageWith                  = "image.width";
	final static String mdImageHeight                = "image.height";
	final static String mdImageDateOriginal          = "image.date.original";
	final static String mdImageGeoLocationLatitude   = "image.geolocation.latitude";
	final static String mdImageGeoLocationLongitude  = "image.geolocation.longitude";
	
	final static String mdPDFaVersion                = "pdf.pdfaversion";
	
	
	final static String mdDocumentCreator            = "document.creator";
	final static String mdDocumentDateCreation       = "document.date.creation";
}
