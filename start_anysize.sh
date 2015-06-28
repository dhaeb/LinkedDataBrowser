#!/bin/sh
echo "Starting java with memory: $1"
export JAVA_OPTS="-Xms$1 -Xmx$1 -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M -Dldb.dbpedia_surfaceforms=test/resources/en_surface_forms.ttl -Dldb.dbpedia_indexdir=index"; activator run
