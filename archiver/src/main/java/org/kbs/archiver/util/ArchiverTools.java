package org.kbs.archiver.util;
import java.util.List;
import org.kbs.archiver.*;
import org.kbs.archiver.lucene.Tools;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.library.*;
import org.apache.commons.cli.*;
import org.apache.lucene.index.IndexWriter;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ArchiverTools {

	private static ClassPathXmlApplicationContext appContext;

	public static void init() {
		appContext=new ClassPathXmlApplicationContext("applicationContext.xml");
		appContext.registerShutdownHook();

	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Options options = new Options();
		options.addOption("h", "help", false, "show help.");
		options.addOption("b", "board", false, "create board table.");
		options.addOption("a","article",false,"update article on board");
		options.addOption("f", "file", true, ".BOARDS file or .DIR file");
		CommandLineParser parser = new PosixParser();
		System.out.println("==========start util==========");
		try {
			CommandLine line = parser.parse(options, args, true);
			if (line.hasOption('h')) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "ArchiverTools", options );
			} else {
				init(); 
				if (line.hasOption('b'))
					createBoard(line);
				else if (line.hasOption('a')) {
					updateArticle(line);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("==========end util==========");
	}

	private static void updateArticle(CommandLine line) throws Exception {
		String filename=line.getOptionValue('f', "/home/archiver/bbs");
		IndexWriter writer=Tools.OpenWriter(appContext);
		if (writer==null)
			return;
		if (line.getArgs().length==0) {
			//todo all board
			ArchiverService service=new ArchiverService(appContext);
 			service.setBoardBaseDir(filename);
			service.run();
		} else {
			String boardname=line.getArgs()[0];
			BoardMapper boardMapper=(BoardMapper) appContext.getBean("boardMapper");
			BoardEntity board=boardMapper.getByName(boardname);
			if (board!=null) {
				ArchiverBoardImpl service=new ArchiverBoardImpl(appContext,board,filename,writer);
				service.call();
			} else {
				System.out.println("Board "+boardname+" not found.");
			}
		}
	}
	private static void createBoard(CommandLine line) {
		String filename=line.getOptionValue('f', ".BOARDS");
		ArchiverService service=new ArchiverService(appContext);
		if (new java.io.File(filename).exists()) {
			service.updateBoardDB(filename);
		} else {
			System.err.println(filename+" not found.");
		}
	}
}
