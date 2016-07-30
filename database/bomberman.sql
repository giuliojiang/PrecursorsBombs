-- MySQL dump 10.13  Distrib 5.7.12, for Linux (x86_64)
--
-- Host: localhost    Database: bomberman
-- ------------------------------------------------------
-- Server version	5.7.12-0ubuntu1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Friends`
--

DROP TABLE IF EXISTS `Friends`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Friends` (
  `id` int(11) NOT NULL,
  `friend_id` int(11) NOT NULL,
  KEY `friend_id` (`friend_id`),
  KEY `id` (`id`),
  CONSTRAINT `Friends_ibfk_1` FOREIGN KEY (`friend_id`) REFERENCES `Player` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `Friends_ibfk_2` FOREIGN KEY (`id`) REFERENCES `Player` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Friends`
--

LOCK TABLES `Friends` WRITE;
/*!40000 ALTER TABLE `Friends` DISABLE KEYS */;
INSERT INTO `Friends` VALUES (2,1),(1,2),(13,12),(12,13),(14,15),(15,14),(15,16),(16,15),(23,10),(10,23);
/*!40000 ALTER TABLE `Friends` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Items`
--

DROP TABLE IF EXISTS `Items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Items` (
  `item_id` int(10) NOT NULL AUTO_INCREMENT,
  `type` text NOT NULL,
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Items`
--

LOCK TABLES `Items` WRITE;
/*!40000 ALTER TABLE `Items` DISABLE KEYS */;
INSERT INTO `Items` VALUES (1,'BOMB1'),(2,'BOMB2');
/*!40000 ALTER TABLE `Items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PendingFriends`
--

DROP TABLE IF EXISTS `PendingFriends`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PendingFriends` (
  `receiver` int(11) NOT NULL,
  `requester` int(11) NOT NULL,
  KEY `receiver` (`receiver`),
  KEY `requester` (`requester`),
  CONSTRAINT `PendingFriends_ibfk_1` FOREIGN KEY (`receiver`) REFERENCES `Player` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `PendingFriends_ibfk_2` FOREIGN KEY (`requester`) REFERENCES `Player` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PendingFriends`
--

LOCK TABLES `PendingFriends` WRITE;
/*!40000 ALTER TABLE `PendingFriends` DISABLE KEYS */;
/*!40000 ALTER TABLE `PendingFriends` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Player`
--

DROP TABLE IF EXISTS `Player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Player` (
  `user_id` int(20) NOT NULL AUTO_INCREMENT,
  `username` text NOT NULL,
  `password` text NOT NULL,
  `email` text NOT NULL,
  `playername` text NOT NULL,
  `guild` text NOT NULL,
  `points` int(11) NOT NULL,
  `experience` int(11) NOT NULL,
  `lastgameplayed` datetime NOT NULL,
  `wins` int(11) NOT NULL,
  `totalgames` int(11) NOT NULL,
  `salt` text NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Player`
--

LOCK TABLES `Player` WRITE;
/*!40000 ALTER TABLE `Player` DISABLE KEYS */;
INSERT INTO `Player` VALUES (1,'USER2','f7876479c44060666591e8eb8addadd713818d8d','EMAIL2','NAME2','GUILD2',20,23,'2016-05-13 00:00:00',5,555,'007569915176501558'),(2,'USER','92aa857e4bac349239ea02e37c388f53c227109c','EMAIL','NAME','GUILD',200,500,'2016-05-18 00:00:00',500,501,'003071716480440482'),(10,'giulio2','0fb93feb32adce9ee99402d314c73e2e6b3f97d1','giulio2@jstudios.ovh','giulio2','none',0,0,'2016-06-10 10:42:13',6,10,'008797969880558704'),(11,'Jimmy','2d4dcaf124b10a42cbf5a93aa4abe4a948807fd1','jimmy@theFish.com','Jimmy','none',0,0,'2016-06-10 10:43:48',3,13,'006611436837805806'),(12,'abaro','feec55791495c35c7346a6ae0b42c14014777f57','abaro@abaro.com','abaro','none',0,0,'2016-06-10 11:48:38',0,1,'009023012014006302'),(13,'dom','38ec1b9f358f95c112d987e897e16afeb5156eb5','dom@dom.dom','dom','none',0,0,'2016-06-10 11:51:18',0,0,'0011630004269937566'),(14,'Emil','850ae72977631e90486c92f5869294bcf4fcef36','ek1414@ic.ac.uk','Emil','none',0,0,'2016-06-10 12:31:10',0,10,'009125331447287459'),(15,'David!','608c123babe2b4dbf90855590492708bb1f87ddb','something@ic.ac.uk','David!','none',0,0,'2016-06-10 12:35:53',2,9,'003902967105503826'),(16,'bruce','f57d8f5827d1cab7f971e354439af32e54b8a067','bruce@b.ic','bruce','none',0,0,'2016-06-10 12:38:57',1,6,'008926396060945528'),(23,'giulio','fbecfc087dd0d223fcb12363e94d6a80ba428602','giulio@jstudios.ovh','giulio','none',0,0,'2016-06-11 11:23:32',1,25,'009152067501874612');
/*!40000 ALTER TABLE `Player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PlayerItem`
--

DROP TABLE IF EXISTS `PlayerItem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PlayerItem` (
  `uid` int(11) NOT NULL,
  `iid` int(11) NOT NULL,
  KEY `uid` (`uid`),
  KEY `iid` (`iid`),
  CONSTRAINT `PlayerItem_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `Player` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `PlayerItem_ibfk_2` FOREIGN KEY (`iid`) REFERENCES `Items` (`item_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PlayerItem`
--

LOCK TABLES `PlayerItem` WRITE;
/*!40000 ALTER TABLE `PlayerItem` DISABLE KEYS */;
INSERT INTO `PlayerItem` VALUES (1,1),(1,2);
/*!40000 ALTER TABLE `PlayerItem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `System`
--

DROP TABLE IF EXISTS `System`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `System` (
  `type` text NOT NULL,
  `value` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `System`
--

LOCK TABLES `System` WRITE;
/*!40000 ALTER TABLE `System` DISABLE KEYS */;
INSERT INTO `System` VALUES ('uid',27),('iid',5);
/*!40000 ALTER TABLE `System` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-06-13 11:22:11
