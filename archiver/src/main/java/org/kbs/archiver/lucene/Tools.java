package org.kbs.archiver.lucene;

import java.io.File;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LimitTokenCountAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.context.ApplicationContext;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class Tools {
	private static IndexWriter writer;
	public static String getLucenceDirectory(ApplicationContext appContext) {
		Properties config = (Properties) appContext.getBean("configproperties");
		String workdir = ".";
		if (config.get("workdir") != null) {
			workdir = config.get("workdir") + "/lucene";
		}
		return workdir;
	}
	public static IndexWriter OpenWriter(ApplicationContext appContext,OpenMode mode,String dirsuffix) {
		if (writer!=null)
			return writer;
		synchronized (Tools.class) {
		if (writer!=null)
                        return writer;
		try {
		String indexDirectory=Tools.getLucenceDirectory(appContext)+dirsuffix;
		File index = new File(indexDirectory);
		FSDirectory indexdir=FSDirectory.open(index);
		if(IndexWriter.isLocked(indexdir)) {
			IndexWriter.unlock(indexdir);
		}
		Analyzer analyzer = new IKAnalyzer();// 采用的分词器
		LimitTokenCountAnalyzer limitanalyzer = new LimitTokenCountAnalyzer(
				analyzer, 1000);
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_35,
				limitanalyzer);
		conf.setOpenMode(mode);
			writer = new IndexWriter(indexdir, conf);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		}
		return writer;
	}
	public static IndexWriter OpenWriter(ApplicationContext appContext) {
		return OpenWriter(appContext,OpenMode.APPEND,"");
	}
}
