# Step1: collect the documents to be indexed
scp cs5604s16_so@hadoop.dlib.vt.edu:dataset.tgz dataset.tgz
tar -xvzf dataset.tgz
cd CS5604S16/small_data/z_686
# copy src from local to destination in hadoop
hadoop fs -put part-m-0000 disease_collection
hadoop fs -ls
hbase shell 
#create 'tweets_disease', 'raw', 'analysis'
hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.columns=HBASE_ROW_KEY,raw:tweet_text tweets_disease disease_collection
solrctl instancedir --generate $HOME/hbase-collection1
vim $HOME/hbase-collection1/conf/schema.xml
solrctl instancedir --create hbase-collection1 $HOME/hbase-collection1
solrctl collection --create hbase-collection1
vim $HOME/morphline-hbase-mapper.xml
vim /etc/hbase-solr/conf/morphlines.conf
mkdir -p src/test/resources
cp /etc/hbase-solr/conf/log4j.properties src/test/resources/
hadoop --config /etc/hadoop/conf jar \
/usr/lib/hbase-solr/tools/hbase-indexer-mr-*-job.jar --conf \
/etc/hbase/conf/hbase-site.xml -D 'mapred.child.java.opts=-Xmx500m' \
--hbase-indexer-file $HOME/morphline-hbase-mapper.xml --zk-host \
127.0.0.1/solr --collection hbase-collection1 --go-live --log4j \
src/test/resources/log4j.properties
# add two properties
sudo vi /etc/hbase-solr/conf/hbase-indexer-site.xml
sudo service hbase-solr-indexer restart
hbase shell
#disable 'tweets_disease'
#alter 'tweets_disease', {NAME => 'raw', REPLICATION_SCOPE => 1}
#enable 'tweets_disease'
hbase-indexer add-indexer \
--name myIndexer \
--indexer-conf $HOME/morphline-hbase-mapper.xml \
--connection-param solr.zk=localhost:2181/solr \
--connection-param solr.collection=hbase-collection1 \
--zookeeper localhost:2181
