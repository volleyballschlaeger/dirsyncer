#!/usr/bin/env jython

import os
import sys
from java.io import File
import SimplePathSync

s = SimplePathSync( File( sys.argv[1] ) )

for i in s.ListPaths( "file://" + os.path.abspath( sys.argv[1] ), False ):
	print i
