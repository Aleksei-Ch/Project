-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: ssm
-- ------------------------------------------------------
-- Server version	5.7.10-log

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
-- Table structure for table `contacts`
--

DROP TABLE IF EXISTS `contacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contacts` (
  `id_user` int(11) DEFAULT NULL,
  `id_contact` int(11) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `fk_contacts_contact_idx` (`id_contact`),
  KEY `fk_contacts_user` (`id_user`),
  CONSTRAINT `fk_contacts_contact` FOREIGN KEY (`id_contact`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_contacts_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contacts`
--

LOCK TABLES `contacts` WRITE;
/*!40000 ALTER TABLE `contacts` DISABLE KEYS */;
INSERT INTO `contacts` VALUES (1,3,1),(3,1,2),(3,9,15),(9,3,16);
/*!40000 ALTER TABLE `contacts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages_queue`
--

DROP TABLE IF EXISTS `messages_queue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `messages_queue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fromUserID` varchar(15) NOT NULL,
  `toUser` varchar(15) NOT NULL,
  `msg` varchar(1024) NOT NULL,
  `time` varchar(5) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_messages_queue_1_idx` (`fromUserID`),
  KEY `fk_messages_queue_2_idx` (`toUser`),
  CONSTRAINT `fk_messages_queue_1` FOREIGN KEY (`fromUserID`) REFERENCES `users` (`login`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_messages_queue_2` FOREIGN KEY (`toUser`) REFERENCES `users` (`login`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages_queue`
--

LOCK TABLES `messages_queue` WRITE;
/*!40000 ALTER TABLE `messages_queue` DISABLE KEYS */;
/*!40000 ALTER TABLE `messages_queue` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(15) DEFAULT NULL,
  `hash_pass` varchar(150) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `lastname` varchar(45) DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login_UNIQUE` (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'login','$2a$04$rmlltI7HdqPaRGCy4GOcN.xRUNcLsKG80hwSyu7r9be1/qQ52/uBC','Alexus','C.','1994-04-26'),(3,'mary','$2a$04$oNjwyYtCxMJb07t4ynQv4unQyy/2Y0EOSv2rKU.ipYdK6HMfRGZuK','Mary','E.','1995-02-06'),(9,'logen','$2a$04$g0FDGg2.r7xRARzdiuGoQ.Q7VTQDWcmktBDFfoIwLKJ2fh3y7.E3e','qwer','laD','1990-03-31'),(10,'qwerty','$2a$04$Z29HCq30aJMEXMZvswDWIeAZoPTNq7xFRlFzvhaxCbqZdLAdcWCz2','User','Xyuser','2001-04-03');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-04-07 15:21:07
