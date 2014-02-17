#!/bin/sh
cd /home/kcn/archiver/archiver
MAVEN_OPTS='-Xms256m -Xmx4096m -Dfile.encoding=UTF-8 -Djava.library.path=/home/kcn/.m2/repository/mysql/mysql-connector-java/5.1.18/mysql-connector-java-5.1.18.jar'
PATH=/usr/local/bin:/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin:/home/kcn/bin
CLASSPATH=/home/kcn/.m2/repository/mysql/mysql-connector-java/5.1.18/mysql-connector-java-5.1.18.jar
mvn compile exec:java -Dkbs.daemon.skipfiles="aaaaa.java" -Dexec.mainClass="org.kbs.archiver.daemon.ArchiverDaemon" -Ponlinedb -Dexec.args="$*" -Djava.class.path=/home/kcn/.m2/repository/mysql/mysql-connector-java/5.1.18/mysql-connector-java-5.1.18.jar
