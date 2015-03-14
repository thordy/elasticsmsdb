# elasticsmsdb
Simple way to load messages from a iOS sms.db into Elasticsearch, and browse the data with Kibana

## Setup


### Prerequisites
#### Installing Elasticsearch
1. Head on over to the [Elasticsearch Download page](https://www.elastic.co/downloads/elasticsearch) and grab the newest release
2. Extract it to a folder and execute `$ES_HOME/bin/elasticsearch` (or `$ES_HOME\bin\elasticsearch.bat` on Windows)

#### Installing River JDBC Plugin
The River JDBC plugin does not come by default with Elasticsearch, and must be installed separately

1. Execute the following command: `./bin/plugin --install jdbc --url http://xbib.org/repository/org/xbib/elasticsearch/plugin/elasticsearch-river-jdbc/1.4.0.10/elasticsearch-river-jdbc-1.4.0.10.zip`. See the [River JDBC Plugin Installation Instructions](https://github.com/jprante/elasticsearch-river-jdbc#installation) for complete information.
2. We also need to grab [SQLite JDBC Driver](https://bitbucket.org/xerial/sqlite-jdbc/downloads) `version 3.7.2` and `version 3.8.7`, and save it to `$ES_HOME/plugin/jdbc`. See Troubleshooting for more information

#### Getting your sms.db
##### OS X
1. Open `~/Library/Application Support/MobileSync/Backup`
2. Go into the folder corresponding to your device (if you just did a backup look for the newest folder)
3. Find the file named `3d0d7e5fb2ce288813306e4d4636395e047a3d28`
4. Copy the file somewhere, and rename it to `sms.db`

##### Windows
1. Navigate to `%APPDATA%\Apple Computer\MobileSync\Backup\`
2. Go into the folder corresponding to your device 
3. Find the file named `3d0d7e5fb2ce288813306e4d4636395e047a3d28`
4. Copy the file somewhere, and rename it to `sms.db`


## Load messages into Elasticsearch
1. Start elasticsearch by running `$ES_HOME/bin/elasticsearch`
2. Change the path of `String smsDb = "sms.db"` in ElasticSMS.java to point towards your sms.db file
3. Run ElasticSMS to load all the data into Elasticsearch

## Browse the data with Kibana
1. Download [Kibana](https://www.elastic.co/downloads/kibana)
2. Start kibana by running `$KA_HOME/bin/kibana`
3. When asked for an index name enter `message*` and choose `date` as Time-field name
4. Explore your messages!

## Troubleshooting
* There is currently an issue with the `SQLite JDBC driver 3.8.7` (see [setReadOnly is not supported by SQLite](https://github.com/jprante/elasticsearch-river-jdbc/issues/250)) and elasticsearch river, but by adding both version 3.7.2 and 3.8.7 to the `$ES_HOME/plugin/jdbc` directory it seems to be working properly.
* On OS X there is some issue with SQLite and river jdbc plugin causing a
`Caused by: java.sql.SQLException: [SQLITE_NOTADB]  File opened that is not a database file (file is encrypted or is not a database)` even though it is pointed towards the correct database file