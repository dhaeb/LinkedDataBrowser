#!/bin/sh

export JAVA_OPTS="-Xms3g -Xmx3g -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=256M -Dldb.dbpedia_surfaceforms=test/resources/en_surface_forms.ttl -Dldb.dbpedia_indexdir=index"; activator run