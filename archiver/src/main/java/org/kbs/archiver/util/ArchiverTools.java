package org.kbs.archiver.util;

import java.util.List;

import org.kbs.archiver.*;
import org.kbs.archiver.lucene.SolrUpdater;
import org.kbs.archiver.persistence.BoardMapper;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ArchiverTools {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiverTools.class);
	private static ClassPathXmlApplicationContext appContext;

	public static void init() {
		appContext = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		appContext.registerShutdownHook();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		init();
		Options options = new Options();
		options.addOption("h", "help", false, "show help.");
		/*
		 * options.addOption("b", "board", false, "create board table.");
		 * options.addOption("a", "article", false, "update article on board");
		 */
		options.addOption("d", "directory", true,
				"directory of bbs data.");
		options.addOption("t", "test", false, "test only");
		options.addOption(null, "nolastupdate", false,
				"don't use last update for board.Deprecated");
		CommandLineParser parser = new BasicParser();
		LOG.info("==========start util==========");
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption('h') || (line.getArgs().length == 0)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("ArchiverTools\n"
						+"        ignore boardname [true|false]\n"
						+"        update [boardname]\n"
						+"        fixoriginid [boardname] [force]\n", options);
			} else {
				String cat = line.getArgs()[0];
				if (cat.equals("ignore")) {
					ignoreService(line);
				} else if (cat.equals("update")) {
					updateService(line);
				} else if (cat.equals("fixoriginid")) {
					fixOriginidService(line);
				}
				/*
				 * if (line.hasOption('b')) createBoard(line); else if
				 * (line.hasOption('a')) { updateArticle(line); }
				 */
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.info("==========end util==========");
	}
	private static void fixOriginidService(CommandLine line) throws Exception {
		boolean testonly = line.hasOption('t');
		String filename = line.getOptionValue('d', "/home/archiver/bbs");
		if (line.getArgs().length==1) {
			System.err.println("no board name");
			return;
		}
		String boardname = line.getArgs()[1];
		ArchiverService service = new ArchiverService(appContext);
		service.setTestonly(testonly);
		service.setBoardBaseDir(filename);
		boolean force=false;
		if (line.getArgs().length>2) {
			if (line.getArgs()[2].equals("force"))
				force=false;
			else {
				if (line.getArgs()[2].equals("reset")) {
					
				}
			}
		}
		service.fixOriginid(boardname,force);
	}
	
	private static void ignoreService(CommandLine line) throws Exception {
		boolean testonly = line.hasOption('t');
		if (line.getArgs().length==1) {
			System.err.println("command ignore boardname [false]");
			return;
		}
		String boardname = line.getArgs()[1];
		ArchiverService service = new ArchiverService(appContext);
		service.setTestonly(testonly);
		boolean ignored=true;
		if (line.getArgs().length>2) {
			if (line.getArgs()[2].equals("false"))
				ignored=false;
		}
		System.out.println("Set board "+boardname+" ignored:"+ignored);
		service.ignoreBoard(boardname, ignored);
	}

	private static void updateService(CommandLine line) throws Exception {
		String filename = line.getOptionValue('d', "/home/archiver/bbs");
		boolean testonly = line.hasOption('t');
//		boolean useLastUpdate = !line.hasOption("nolastupdate");
		boolean useLastUpdate = false;
		System.out.println("Set test only mode:" + testonly);
		System.out.println("Set use last update:" + useLastUpdate);
		if (line.getArgs().length == 1) { // no argument
			// all board
			ArchiverService service = new ArchiverService(appContext);
			service.setTestonly(testonly);
			service.setUseLastUpdate(useLastUpdate);
			service.setBoardBaseDir(filename);
			//update board
			service.updateBoardDB(filename+"/.BOARDS");
			//update article
			service.run();
		} else {
			String boardname = line.getArgs()[1];
			BoardMapper boardMapper = (BoardMapper) appContext
					.getBean("boardMapper");
			BoardEntity board = boardMapper.getByName(boardname);
			if (board != null) {
				ArchiverBoardImpl service = new ArchiverBoardImpl(
						appContext, null, filename);
				service.setTestonly(testonly);
				service.setUseLastUpdate(useLastUpdate);
				service.work(board);
                SolrUpdater solrUpdater=new SolrUpdater();
                solrUpdater.init(appContext);
                solrUpdater.deltaImport();
			} else {
				LOG.error("Board " + boardname + " not found.");
			}
		}
	}
}
