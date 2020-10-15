-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 10.18.96.229    Database: uat_ci
-- ------------------------------------------------------
-- Server version	5.5.29

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
-- Table structure for table `used_toolkits`
--

DROP TABLE IF EXISTS `used_toolkits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `used_toolkits` (
  `ID` bigint(20) NOT NULL,
  `MAJOR_VERSION` int(11) NOT NULL,
  `MINOR_VERSION` int(11) NOT NULL,
  `PARENT_PROJ` bigint(20) DEFAULT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNQ_USED_TOOLKITS_0` (`NAME`,`PARENT_PROJ`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `used_toolkits`
--

LOCK TABLES `used_toolkits` WRITE;
/*!40000 ALTER TABLE `used_toolkits` DISABLE KEYS */;
INSERT INTO `used_toolkits` VALUES (4465,4,6,3,'com.bredexsw.guidancer.ConcreteToolkitPlugin',1),(4466,4,0,3,'com.bredexsw.guidancer.AbstractToolkitPlugin',1),(4719,1,1,4467,'com.bredexsw.guidancer.HtmlToolkitPlugin',1),(5404,4,0,4720,'org.eclipse.jubula.JavaFXToolkitPlugin',1),(5891,4,1,5405,'com.bredexsw.guidancer.GefToolkitPlugin',1),(6373,4,1,5892,'com.bredexsw.guidancer.SwtToolkitPlugin',1),(3526602,4,6,25226,'com.bredexsw.guidancer.ConcreteToolkitPlugin',1),(3526603,4,0,25226,'com.bredexsw.guidancer.AbstractToolkitPlugin',1),(3526604,4,1,25226,'com.bredexsw.guidancer.SwtToolkitPlugin',1);
/*!40000 ALTER TABLE `used_toolkits` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-06-28 16:52:48
