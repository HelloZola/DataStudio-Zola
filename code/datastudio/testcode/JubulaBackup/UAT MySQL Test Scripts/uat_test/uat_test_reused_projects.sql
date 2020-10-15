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
-- Table structure for table `reused_projects`
--

DROP TABLE IF EXISTS `reused_projects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reused_projects` (
  `ID` bigint(20) NOT NULL,
  `PARENT_PROJ` bigint(20) DEFAULT NULL,
  `MAJOR_VERS_NUMBER` int(11) DEFAULT NULL,
  `MICRO_VERS_NUMBER` int(11) DEFAULT NULL,
  `MINOR_VERS_NUMBER` int(11) DEFAULT NULL,
  `REUSED_PROJ_GUID` varchar(255) DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  `VERSION_QUALIFIER` varchar(255) DEFAULT NULL,
  `FK_PROJ_PROPERTIES` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNQ_REUSED_PROJECTS_0` (`PARENT_PROJ`,`REUSED_PROJ_GUID`),
  KEY `FK_REUSED_PROJECTS_FK_PROJ_PROPERTIES` (`FK_PROJ_PROPERTIES`),
  CONSTRAINT `FK_REUSED_PROJECTS_FK_PROJ_PROPERTIES` FOREIGN KEY (`FK_PROJ_PROPERTIES`) REFERENCES `project_properties` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reused_projects`
--

LOCK TABLES `reused_projects` WRITE;
/*!40000 ALTER TABLE `reused_projects` DISABLE KEYS */;
INSERT INTO `reused_projects` VALUES (5277,4718,8,NULL,4,'8ab6b083150339b501150364afb90435',1,NULL,5256),(6297,5891,8,NULL,4,'8ab6b083150339b501150364afb90435',1,NULL,6276),(157448,19057,8,NULL,4,'8ab6b0832254bb19012254bb2bfe0001',1,NULL,157427),(157449,19057,8,NULL,4,'8ab6b083150339b501150364afb90435',1,NULL,157427),(157450,19057,8,NULL,4,'8a81818316dd751b0116dd75444a0001',1,NULL,157427),(361502,360755,8,NULL,4,'8ab6b0832254bb19012254bb2bfe0001',1,NULL,361481),(361503,360755,8,NULL,4,'8ab6b083150339b501150364afb90435',1,NULL,361481),(361504,360755,8,NULL,4,'8a81818316dd751b0116dd75444a0001',1,NULL,361481);
/*!40000 ALTER TABLE `reused_projects` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-06-29 15:44:50
