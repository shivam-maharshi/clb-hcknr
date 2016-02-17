# start Solr and listenning on port 8983 (default)
# can specifiy a server directory: bin/solr start -d newServerDir
bin/solr start -p 8983
# stop Solr
bin/solr stop -all
# restart Solr
bin/solr restart
# create a Solr core before indexing and searching
bin/solr create -c gettingstarted
# delete a Solr core
# bin/solr delete -c gettingstarted
# use Solr command line tool (post script) to index different types of documents
bin/post -c gettingstarted  example/exampledocs/*.xml
# check status
bin/solr status
# go to browser and see the Admin Console page
open http://localhost:8983/solr/
