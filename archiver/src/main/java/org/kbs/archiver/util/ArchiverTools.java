package org.kbs.archiver.util;

import java.util.List;

import org.kbs.archiver.*;
import org.kbs.archiver.persistence.BoardMapper;
import org.apache.commons.cli.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ArchiverTools {

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
		Options options = new Options();
		options.addOption("h", "help", false, "show help.");
		/*
		options.addOption("b", "board", false, "create board table.");
		options.addOption("a", "article", false, "update article on board");
		*/
		options.addOption("f", "file", true, ".BOARDS file or .DIR file");
		options.addOption("t", "test", false, "test only");
		options.addOption(null, "nolastupdate", false,
				"don't use last update for board.Deprecated");
		CommandLineParser parser = new BasicParser();
		System.out.println("==========start util==========");
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption('h')||(line.getArgs().length==0)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("ArchiverTools", options);
			} else {
				init();
				String cat = line.getArgs()[0];
				if (cat.equals("board")) {
					boardService(line);
				} else if (cat.equals("article")) {
					articleSercie(line);
				}
				/*
				if (line.hasOption('b'))
					createBoard(line);
				else if (line.hasOption('a')) {
					updateArticle(line);
				}
				*/
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("==========end util==========");
	}
	
	private static void boardService(CommandLine line) throws Exception {
		if (line.getArgs().length==1) {
			System.out.print(b)
		}
		String command = line.getArgs()[1];
	}
	private static void updateArticle(CommandLine line) throws Exception {
		String filename = line.getOptionValue('f', "/home/archiver/bbs");
		boolean testonly = line.hasOption('t');
		boolean useLastUpdate = !line.hasOption("nolastupdate");
		System.out.println("Set test only mode:" + testonly);
		System.out.println("Set use last update:" + useLastUpdate);
		if (line.getArgs().length == 0) {
			// todo all board
			ArchiverService service = new ArchiverService(appContext);
			service.setTestonly(testonly);
			service.setUseLastUpdate(useLastUpdate);
			service.setBoardBaseDir(filename);
			service.run();
		} else {
			String boardname = line.getArgs()[0];
			BoardMapper boardMapper = (BoardMapper) appContext
					.getBean("boardMapper");
			BoardEntity board = boardMapper.getByName(boardname);
			if (board != null) {
				ArchiverBoardImpl service = new ArchiverBoardImpl(appContext,
						null, filename);
				service.setTestonly(testonly);
				service.setUseLastUpdate(useLastUpdate);
				service.work(board);
			} else {
				System.out.println("Board " + boardname + " not found.");
			}
		}
	}

	private static void createBoard(CommandLine line) {
		String filename = line.getOptionValue('f', ".BOARDS");
		ArchiverService service = new ArchiverService(appContext);
		boolean testonly = line.hasOption('t');
		System.out.println("Set test only mode:" + testonly);
		if (new java.io.File(filename).exists()) {
			service.setTestonly(testonly);
			service.updateBoardDB(filename);
		} else {
			System.err.println(filename + " not found.");
		}
	}
}
