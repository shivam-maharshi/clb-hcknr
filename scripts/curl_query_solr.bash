#!/usr/bin/env bash
# search for all the documents for specific term: q=term
curl http://localhost:8983/solr/gettingstarted/select?q=video
# search for term in the specific field only: q=fieldname:term
curl http://localhost:8983/solr/gettingstarted/select?q=name:black
# show the results only contain specific fields: fl=fieldname
curl http://localhost:8983/solr/gettingstarted/select?q=video&fl=id,name,price
# search for fields in certain ranges: q=fieldname:[* To *]
curl http://localhost:8983/solr/gettingstarted/select?q=price%3D0+TO+400&fl=id,name,price
# Faceting search
curl http://localhost:8983/solr/gettingstarted/select?q=price%3D0+TO+400&fl=id&facet=true&facet.field=cat