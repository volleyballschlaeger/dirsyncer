#!/usr/bin/env jython

import os
from java.io import File
import SimplePathSync

s = SimplePathSync( File( "/var/www/sync" ) )

for i in s.ListPaths( "http://localhost/sync", True ):
	print i
