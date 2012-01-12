package org.kbs.archiver.lucene;

import java.util.Properties;

import org.springframework.context.ApplicationContext;

public class Tools {
	public static String getLucenceDirectory(ApplicationContext appContext) {
		Properties config = (Properties) appContext.getBean("configproperties");
		String workdir = ".";
		if (config.get("workdir") != null) {
			workdir = config.get("workdir") + "/lucene";
		}
		return workdir;
	}
}
