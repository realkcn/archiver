package org.kbs.archiver.daemon;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import org.kbs.archiver.*;
import org.kbs.archiver.lucene.SolrUpdater;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.library.FileHeaderInfo;
import org.kbs.library.TwoObject;
import org.kbs.library.SimpleException;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ArchiverDaemon {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiverDaemon.class);
	private static ClassPathXmlApplicationContext appContext;
	private static ArrayBlockingQueue< TwoObject<BoardEntity,FileHeaderInfo> > workqueue;
	private static BoardMapper boardMapper;

	public static void init() {
		appContext = new ClassPathXmlApplicationContext(
				"spring.xml");
		appContext.registerShutdownHook();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		init();
		Options options = new Options();
		options.addOption("h", "help", false, "show help.");
		options.addOption("d", "directory", true,
				"directory of bbs data.");
		options.addOption("t", "test", false, "test only");
		CommandLineParser parser = new BasicParser();
		LOG.info("==========start daemon==========");
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption('h') || (line.getArgs().length == 0)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("ArchiverDaemon\n"
						+"        update [boardname]\n" , options);
			} else {
				String cat = line.getArgs()[0];
				if (cat.equals("update")) {
					updateService(line);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.info("==========end daemon==========");
	}

	private static void updateService(CommandLine line) throws Exception {
		String filename = line.getOptionValue('d', "/home/archiver/bbs");
		boolean testonly = line.hasOption('t');
		boolean useLastUpdate = false;
		ServerSocket server;

		System.out.println("Set test only mode:" + testonly);
		System.out.println("Set use last update:" + useLastUpdate);

		//listen port
		InetAddress addr = InetAddress.getByName("10.0.4.236");
		try {
			server = new ServerSocket(5111, 0, addr);
		} catch (IOException e) {
			LOG.error("cannot bind to port");
			return;
		} finally {
		}

		//init boardMapper
		boardMapper = (BoardMapper) appContext.getBean("boardMapper");

		//producer - consumer : init consumers
		int nThreads = 0;
		Properties config = (Properties) appContext.getBean("configproperties");
		if (config.get("workerthreads") != null)
			nThreads = Integer.parseInt((String) config.get("workerthreads"));
		if (nThreads <= 0)
			nThreads = 4;

		try {
			Thread[] workerthread = new Thread[nThreads];
			workqueue = new ArrayBlockingQueue< TwoObject<BoardEntity,FileHeaderInfo> >(20);
			for (int i = 0; i < nThreads; i++) {
				ArticleImpl worker = new ArticleImpl(appContext, workqueue, filename);
				worker.setTestonly(testonly);
				worker.setUseLastUpdate(useLastUpdate);
				workerthread[i] = new Thread(worker);
				workerthread[i].start();
			}
		} catch (SimpleException e) {
			LOG.error("run", e);
		} finally {

		}

		//TODO: 开一个Timer定时scan .BOARDS变化？

		//accept port
		while(true){
			Socket s = server.accept();
			LOG.warn("accepted");
			new Thread(new socketThread(s)).start();
		}
	}

	static class socketThread implements Runnable {
		private Socket socket;
		private InputStream in;

		public socketThread(Socket s){
			socket = s;
		}

		public void run() {

			try {
				handleSocket();
			} catch (Exception e){
				return;
			}
		}

		private void handleSocket() throws Exception {
			in = socket.getInputStream();

			while(true){
				byte[] buf = new byte[256];
				int tlen=0;
				while(true){
					int len = in.read(buf, tlen, 256 - tlen);
					if(len < 0){
						return;
					}
					tlen += len;
					if(tlen >= 256){
						break;
					}
				}

				//parse FileHeaderInfo
				FileHeaderInfo fh = new FileHeaderInfo();
				ByteArrayInputStream bytearray_in = new ByteArrayInputStream(buf);
				if(fh.readBBSDir(bytearray_in) != 0){
					LOG.warn("get data readBBSDir error");
					continue;
				}

				//parse boardname
				byte[] bname = new byte[30];
				int len;
				for(len=0; len < 30; len++){
					bname[len] = buf[222 + len];
					if(bname[len] == 0){
						break;
					}
				}
				bname[29] = 0;
				String boardname = new String(bname, 0, len, "GBK");

				//get board
				BoardEntity board = boardMapper.getByName(boardname);
				if (board == null) {
					LOG.warn("board not exist");
					continue;
				}

				//producer add
				LOG.warn("add article: " + boardname + ":" + fh.getArticleid() );
				TwoObject<BoardEntity,FileHeaderInfo> param = new TwoObject<BoardEntity,FileHeaderInfo>(board,fh);
				workqueue.put( param );
			}
		}
	}

			// 结束
			/* TODO
            // 更新solr
            SolrUpdater solrUpdater=new SolrUpdater();
            solrUpdater.init(ctx);
            solrUpdater.deltaImport();
*/
}
