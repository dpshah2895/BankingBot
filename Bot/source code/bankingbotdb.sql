-- phpMyAdmin SQL Dump
-- version 4.4.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: May 19, 2017 at 08:39 PM
-- Server version: 5.6.26
-- PHP Version: 5.6.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bankingbotdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE IF NOT EXISTS `account` (
  `AcNo` bigint(11) NOT NULL,
  `DateOfOpen` date NOT NULL,
  `Type` char(1) NOT NULL,
  `UserName` varchar(20) NOT NULL,
  `Password` varchar(42) NOT NULL,
  `Balance` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`AcNo`, `DateOfOpen`, `Type`, `UserName`, `Password`, `Balance`) VALUES
(11111111111, '2017-03-09', 'S', 'kiner_shah', 'fadd1c77b1cea55b6cb7d319a7d6ae7e25ac436e', 1100),
(11111111112, '2017-03-10', 'S', '', '', 0);

-- --------------------------------------------------------

--
-- Table structure for table `accountholder`
--

CREATE TABLE IF NOT EXISTS `accountholder` (
  `FName` varchar(20) NOT NULL,
  `MName` varchar(20) NOT NULL,
  `LName` varchar(20) NOT NULL,
  `PermanentAddress` varchar(100) NOT NULL,
  `ResidentialAddress` varchar(100) NOT NULL,
  `DOB` date NOT NULL,
  `Age` int(11) NOT NULL DEFAULT '0',
  `Email` varchar(30) NOT NULL,
  `ResPhoneNo` int(8) NOT NULL,
  `MobPhoneNo` bigint(10) NOT NULL,
  `Gender` char(1) NOT NULL,
  `AcNo` bigint(11) NOT NULL,
  `PAN` char(10) NOT NULL,
  `RegHash` varchar(42) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `accountholder`
--

INSERT INTO `accountholder` (`FName`, `MName`, `LName`, `PermanentAddress`, `ResidentialAddress`, `DOB`, `Age`, `Email`, `ResPhoneNo`, `MobPhoneNo`, `Gender`, `AcNo`, `PAN`, `RegHash`) VALUES
('Kiner', 'Bharat', 'Shah', 'K/203, Vardhman Nagar, Dr. R. P. Road, Mulund(W), Mumbai-400', 'K/203, Vardhman Nagar, Dr. R. P. Road, Mulund(W), Mumbai-400', '1996-05-20', 20, 'kiner.shah@somaiya.edu', 22222222, 9999999999, 'M', 11111111111, 'AAA111BBB2', '0eb0b2bd3ab40ffc066e16cdc376a21cdac76941'),
('Darshan', 'Pankaj', 'Shah', 'Kandivali (West), Mumbai', 'Kandivali (west), Mumbai', '1995-12-02', 21, 'darshan.ps@somaiya.edu', 22222223, 9999999990, 'M', 11111111112, 'AAA111CCC3', 'b1e0e0787b7ed9c8cbed086eb37cc22d48ee9623');

-- --------------------------------------------------------

--
-- Table structure for table `chequebookrequests`
--

CREATE TABLE IF NOT EXISTS `chequebookrequests` (
  `RequestID` bigint(20) NOT NULL DEFAULT '999999999',
  `AccountHolderName` varchar(50) NOT NULL,
  `DeliveryAddress` varchar(100) NOT NULL,
  `LeavesNumber` int(11) NOT NULL,
  `DeliveryMethod` text NOT NULL,
  `AcNo` bigint(20) NOT NULL,
  `DateTime` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `chequebookrequests`
--

INSERT INTO `chequebookrequests` (`RequestID`, `AccountHolderName`, `DeliveryAddress`, `LeavesNumber`, `DeliveryMethod`, `AcNo`, `DateTime`) VALUES
(999999999, 'Default', 'Default', 0, 'By Post', 0, 'NULL'),
(1000000000, 'Kiner Bharat Shah', 'K/203, Vardhman Nagar, Dr. R. P. Road, Mulund(W), Mumbai-400', 25, 'By Post', 11111111111, '15-05-2017 12:59:41'),
(1000000001, 'Kiner Bharat Shah', 'K/203, Vardhman Nagar, Dr. R. P. Road, Mulund(W), Mumbai-400', 25, 'By Post', 11111111111, '19-05-2017 20:03:42');

-- --------------------------------------------------------

--
-- Table structure for table `commandquery`
--

CREATE TABLE IF NOT EXISTS `commandquery` (
  `CQID` int(11) NOT NULL,
  `Subject` varchar(30) NOT NULL,
  `Object` varchar(30) NOT NULL,
  `Action` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `externaluser`
--

CREATE TABLE IF NOT EXISTS `externaluser` (
  `Name` varchar(20) NOT NULL,
  `Age` int(11) NOT NULL,
  `Gender` char(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `infoquery`
--

CREATE TABLE IF NOT EXISTS `infoquery` (
  `IQID` int(11) NOT NULL,
  `Subject` varchar(30) NOT NULL,
  `Object` varchar(30) NOT NULL,
  `QuestionType` varchar(10) NOT NULL,
  `Response` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE IF NOT EXISTS `transactions` (
  `TransId` bigint(11) NOT NULL,
  `SenderAcNo` bigint(11) NOT NULL,
  `ReceiverAcNo` bigint(11) NOT NULL,
  `Timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Amount` float NOT NULL,
  `Type` char(1) NOT NULL,
  `Balance` float NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`TransId`, `SenderAcNo`, `ReceiverAcNo`, `Timestamp`, `Amount`, `Type`, `Balance`) VALUES
(100000000001, 11111111111, 11111111112, '2017-03-10 11:23:18', 200, 'T', 1000),
(100000000002, 11111111111, 11111111111, '2017-03-28 08:10:41', 100, 'D', 1100);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`AcNo`);

--
-- Indexes for table `accountholder`
--
ALTER TABLE `accountholder`
  ADD PRIMARY KEY (`PAN`),
  ADD UNIQUE KEY `PAN` (`PAN`),
  ADD KEY `fk_ac_no` (`AcNo`);

--
-- Indexes for table `chequebookrequests`
--
ALTER TABLE `chequebookrequests`
  ADD PRIMARY KEY (`RequestID`);

--
-- Indexes for table `commandquery`
--
ALTER TABLE `commandquery`
  ADD PRIMARY KEY (`CQID`);

--
-- Indexes for table `infoquery`
--
ALTER TABLE `infoquery`
  ADD PRIMARY KEY (`IQID`);

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`TransId`),
  ADD KEY `fk_ac_no1` (`SenderAcNo`),
  ADD KEY `fk_ac_no2` (`ReceiverAcNo`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `accountholder`
--
ALTER TABLE `accountholder`
  ADD CONSTRAINT `fk_ac_no` FOREIGN KEY (`AcNo`) REFERENCES `account` (`AcNo`);

--
-- Constraints for table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `fk_ac_no1` FOREIGN KEY (`SenderAcNo`) REFERENCES `account` (`AcNo`),
  ADD CONSTRAINT `fk_ac_no2` FOREIGN KEY (`ReceiverAcNo`) REFERENCES `account` (`AcNo`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
