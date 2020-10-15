-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 10.18.96.229    Database: uat_test
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
-- Table structure for table `test_data_cat`
--

DROP TABLE IF EXISTS `test_data_cat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test_data_cat` (
  `ID` bigint(20) NOT NULL,
  `PARENT_PROJ` bigint(20) DEFAULT NULL,
  `NAME` varchar(4000) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  `FK_PARENT` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `PI_DATA_CAT_PARENT_PROJ` (`PARENT_PROJ`),
  KEY `FK_TEST_DATA_CAT_FK_PARENT` (`FK_PARENT`),
  CONSTRAINT `FK_TEST_DATA_CAT_FK_PARENT` FOREIGN KEY (`FK_PARENT`) REFERENCES `test_data_cat` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_data_cat`
--

LOCK TABLES `test_data_cat` WRITE;
/*!40000 ALTER TABLE `test_data_cat` DISABLE KEYS */;
INSERT INTO `test_data_cat` VALUES (3738,3,NULL,1,NULL),(4667,4465,NULL,1,NULL),(5253,4718,NULL,1,NULL),(5784,5403,NULL,1,NULL),(6273,5891,NULL,1,NULL),(157165,19057,NULL,1,NULL),(361478,360755,NULL,1,NULL);
/*!40000 ALTER TABLE `test_data_cat` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-06-29 15:45:33
