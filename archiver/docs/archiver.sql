-- MySQL dump 10.13  Distrib 5.6.13, for Linux (x86_64)
--
-- Host: localhost    Database: archiver
-- ------------------------------------------------------
-- Server version	5.6.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES UTF8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `article` (
  `boardid` bigint(20) unsigned NOT NULL COMMENT '文章所属版面',
  `threadid` bigint(20) unsigned NOT NULL COMMENT '文章所属主题数',
  `articleid` bigint(20) unsigned NOT NULL COMMENT '文章唯一ID',
  `author` varchar(14) COLLATE utf8_bin NOT NULL COMMENT '作者',
  `posttime` datetime NOT NULL COMMENT '发表时间',
  `attachment` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '附件个数',
  `subject` varchar(60) COLLATE utf8_bin NOT NULL COMMENT '标题',
  `isvisible` tinyint(1) NOT NULL DEFAULT '1',
  `originid` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'bbs原始id',
  `filename` varchar(20) COLLATE utf8_bin NOT NULL COMMENT 'bbs对应的文件名',
  `replyid` bigint(20) DEFAULT NULL COMMENT '回复的对应的文章id，注意这里是orginid，不是归档站的id',
  `encodingurl` varchar(16) COLLATE utf8_bin NOT NULL COMMENT '用于url中的id',
  PRIMARY KEY (`articleid`),
  KEY `originaid` (`originid`,`boardid`) USING HASH,
  KEY `boardid` (`boardid`),
  KEY `threadid` (`threadid`),
  KEY `encodingurl` (`encodingurl`) USING HASH,
  KEY `author` (`author`) USING HASH,
  KEY `posttime2` (`author`,`posttime`),
  KEY `author_board_posttime_visible` (`boardid`,`posttime`,`author`,`isvisible`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `article0408`
--

DROP TABLE IF EXISTS `article0408`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `article0408` (
  `boardid` bigint(20) unsigned NOT NULL COMMENT '文章所属版面',
  `threadid` bigint(20) unsigned NOT NULL COMMENT '文章所属主题数',
  `articleid` bigint(20) unsigned NOT NULL COMMENT '文章唯一ID',
  `author` varchar(14) COLLATE utf8_bin NOT NULL COMMENT '作者',
  `posttime` datetime NOT NULL COMMENT '发表时间',
  `attachment` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '附件个数',
  `subject` varchar(60) COLLATE utf8_bin NOT NULL COMMENT '标题',
  `isvisible` tinyint(1) NOT NULL DEFAULT '1',
  `originid` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'bbs原始id',
  `filename` varchar(20) COLLATE utf8_bin NOT NULL COMMENT 'bbs对应的文件名',
  `replyid` bigint(20) DEFAULT NULL COMMENT '回复的对应的文章id，注意这里是orginid，不是归档站的id',
  `encodingurl` varchar(16) COLLATE utf8_bin NOT NULL COMMENT '用于url中的id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `articlebackup`
--

DROP TABLE IF EXISTS `articlebackup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `articlebackup` (
  `boardid` bigint(20) unsigned NOT NULL COMMENT '文章所属版面',
  `threadid` bigint(20) unsigned NOT NULL COMMENT '文章所属主题数',
  `articleid` bigint(20) unsigned NOT NULL COMMENT '文章唯一ID',
  `author` varchar(14) COLLATE utf8_bin NOT NULL COMMENT '作者',
  `posttime` datetime NOT NULL COMMENT '发表时间',
  `attachment` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '附件个数',
  `subject` varchar(60) COLLATE utf8_bin NOT NULL COMMENT '标题',
  `isvisible` tinyint(1) NOT NULL DEFAULT '1',
  `originid` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'bbs原始id',
  `filename` varchar(20) COLLATE utf8_bin NOT NULL COMMENT 'bbs对应的文件名',
  `replyid` bigint(20) DEFAULT NULL COMMENT '回复的对应的文章id，注意这里是orginid，不是归档站的id',
  `encodingurl` varchar(16) COLLATE utf8_bin NOT NULL COMMENT '用于url中的id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `articlebody`
--

DROP TABLE IF EXISTS `articlebody`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `articlebody` (
  `articleid` bigint(20) NOT NULL,
  `body` mediumtext COLLATE utf8_bin,
  PRIMARY KEY (`articleid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
/*!50100 PARTITION BY KEY (articleid)
PARTITIONS 10 */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `articlebody0408`
--

DROP TABLE IF EXISTS `articlebody0408`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `articlebody0408` (
  `articleid` bigint(20) NOT NULL,
  `body` mediumtext COLLATE utf8_bin
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `articlebodybackup`
--

DROP TABLE IF EXISTS `articlebodybackup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `articlebodybackup` (
  `articleid` bigint(20) NOT NULL,
  `body` mediumtext COLLATE utf8_bin
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `attachment`
--

DROP TABLE IF EXISTS `attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attachment` (
  `attachmentid` bigint(20) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `articleid` bigint(20) NOT NULL,
  `attachmentorder` int(11) DEFAULT NULL,
  `data` mediumblob,
  `encodingurl` varchar(16) COLLATE utf8_bin NOT NULL,
  `boardid` bigint(20) DEFAULT NULL,
  KEY `article` (`articleid`,`attachmentorder`) USING BTREE,
  KEY `pk` (`attachmentid`),
  KEY `encodingurl` (`encodingurl`) USING HASH,
  KEY `boardid` (`boardid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin
/*!50100 PARTITION BY HASH (articleid)
PARTITIONS 10 */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `attachment0408`
--

DROP TABLE IF EXISTS `attachment0408`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attachment0408` (
  `attachmentid` bigint(20) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `articleid` bigint(20) NOT NULL,
  `attachmentorder` int(11) DEFAULT NULL,
  `data` mediumblob,
  `encodingurl` varchar(16) COLLATE utf8_bin NOT NULL,
  `boardid` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `attbackup`
--

DROP TABLE IF EXISTS `attbackup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attbackup` (
  `attachmentid` bigint(20) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `articleid` bigint(20) NOT NULL,
  `attachmentorder` int(11) DEFAULT NULL,
  `data` mediumblob,
  `encodingurl` varchar(16) COLLATE utf8_bin NOT NULL,
  `boardid` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `board`
--

DROP TABLE IF EXISTS `board`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `board` (
  `boardid` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '版面id，数字，主键',
  `name` varchar(45) CHARACTER SET utf8 NOT NULL COMMENT '版面名，英文',
  `cname` varchar(45) COLLATE utf8_bin DEFAULT NULL COMMENT '版面名，中文',
  `ishidden` tinyint(1) DEFAULT '0' COMMENT '是否为隐藏版面',
  `threads` int(11) DEFAULT NULL COMMENT '有多少therad',
  `articles` int(11) DEFAULT NULL COMMENT '有多少article',
  `lastarticleid` bigint(20) NOT NULL DEFAULT '0' COMMENT '上一次归档的最后一个id，是bbs内部的那个id，不是archiver内部的id',
  `groupid` char(1) COLLATE utf8_bin DEFAULT '0' COMMENT '分区，bbs里面的分区',
  `section` varchar(10) COLLATE utf8_bin DEFAULT NULL COMMENT '分类',
  `ignored` tinyint(1) DEFAULT '0' COMMENT '是否不归档',
  `regenerate` tinyint(1) DEFAULT '0' COMMENT '归档的时候重新生成originid,一次性的',
  `lastdeleteid` bigint(20) DEFAULT '0' COMMENT '上次删除的ID',
  PRIMARY KEY (`boardid`),
  UNIQUE KEY `name_2` (`name`),
  UNIQUE KEY `boardid` (`boardid`),
  KEY `name` (`name`) USING HASH,
  KEY `ishidden` (`ishidden`)
) ENGINE=InnoDB AUTO_INCREMENT=1531 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Save the information of board';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deleted`
--

DROP TABLE IF EXISTS `deleted`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `deleted` (
  `originid` bigint(20) NOT NULL,
  `boardid` bigint(20) NOT NULL,
  `deletetime` datetime DEFAULT NULL,
  `deleteby` varchar(14) COLLATE utf8_bin DEFAULT NULL,
  UNIQUE KEY `pk` (`boardid`,`originid`) USING HASH,
  KEY `board` (`boardid`),
  KEY `sort` (`deletetime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deleted20140301`
--

DROP TABLE IF EXISTS `deleted20140301`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `deleted20140301` (
  `originid` bigint(20) NOT NULL,
  `boardid` bigint(20) NOT NULL,
  `deletetime` datetime DEFAULT NULL,
  `deleteby` varchar(14) COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `newestthread`
--

DROP TABLE IF EXISTS `newestthread`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `newestthread` (
  `boardid` bigint(20) NOT NULL COMMENT '该主题所属的版面ID',
  `subject` varchar(60) COLLATE utf8_bin NOT NULL COMMENT '标题',
  `posttime` datetime NOT NULL COMMENT '发表时间',
  `articlenumber` int(11) NOT NULL COMMENT '主题文章数',
  `author` varchar(14) COLLATE utf8_bin NOT NULL COMMENT '原作者',
  `lastreply` varchar(14) COLLATE utf8_bin NOT NULL COMMENT '最后一个回复的ID',
  `lastposttime` datetime NOT NULL COMMENT '上次发文时间',
  `threadid` bigint(20) unsigned NOT NULL COMMENT '主题id',
  `originid` bigint(20) unsigned NOT NULL COMMENT 'bbs系统的文章id，用于查询',
  `encodingurl` varchar(16) COLLATE utf8_bin NOT NULL COMMENT '用于url中的id',
  `isvisible` tinyint(1) DEFAULT '1',
  `boardname` varchar(45) COLLATE utf8_bin DEFAULT NULL COMMENT '版面名称',
  `groupid` char(1) COLLATE utf8_bin DEFAULT NULL COMMENT '版面分区',
  PRIMARY KEY (`threadid`),
  KEY `sortbygroup` (`groupid`,`lastposttime`),
  KEY `articlenumber` (`articlenumber`),
  KEY `lastposttime` (`lastposttime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='The information of thread';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence`
--

DROP TABLE IF EXISTS `sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence` (
  `name` varchar(40) COLLATE utf8_bin NOT NULL,
  `value` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `thread`
--

DROP TABLE IF EXISTS `thread`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `thread` (
  `boardid` bigint(20) NOT NULL COMMENT '该主题所属的版面ID',
  `subject` varchar(60) COLLATE utf8_bin NOT NULL COMMENT '标题',
  `posttime` datetime NOT NULL COMMENT '发表时间',
  `articlenumber` int(11) NOT NULL COMMENT '主题文章数',
  `author` varchar(14) COLLATE utf8_bin NOT NULL COMMENT '原作者',
  `lastreply` varchar(14) COLLATE utf8_bin NOT NULL COMMENT '最后一个回复的ID',
  `lastposttime` datetime NOT NULL COMMENT '上次发文时间',
  `threadid` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主题id',
  `originid` bigint(20) unsigned NOT NULL COMMENT 'bbs系统的文章id，用于查询',
  `encodingurl` varchar(16) COLLATE utf8_bin NOT NULL COMMENT '用于url中的id',
  `isvisible` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`threadid`),
  KEY `author` (`author`) USING HASH,
  KEY `orginid` (`originid`,`boardid`) USING HASH,
  KEY `encodingurl` (`encodingurl`) USING HASH,
  KEY `boardid` (`boardid`),
  KEY `lastposttime2` (`boardid`,`lastposttime`),
  KEY `author_board_posttime_visible` (`boardid`,`posttime`,`author`,`isvisible`),
  KEY `author_board_lastposttime_visible` (`boardid`,`lastposttime`,`author`,`isvisible`)
) ENGINE=InnoDB AUTO_INCREMENT=20485381 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='The information of thread';
/*!40101 SET character_set_client = @saved_cs_client */;

