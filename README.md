Currency Exchange Rates Watcher
===============================

Web application monitors changes in foreign exchange rates.

Developed using Spring Framework, Jackson JSON Processor, Hibernate, MySQL, Websockets, jQuery,
Backbone.js and Underscore.js.

Application requires JRE 7.

###Screenshots###

![Main window](currency-watcher.png)

###Install###

Create database:

```
mysql> create user 'rates'@'localhost' identified by 'P@ssw0rd';
mysql> create database rates;
mysql> grant all privileges on rates.* to 'rates'@'localhost';
mysql> exit;
```

Create tables:

```
$ mysql -u rates -p rates < sql/mysql_schema.sql
```
