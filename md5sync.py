#!/usr/bin/env jython

import sys
from java.io import File
import SimpleDirSync

if len( sys.argv ) < 2:
	print "Usage: " + sys.argv[0] + " URL"
	quit()

s = SimpleDirSync( File( "." ) )
list = s.RecvEntries( sys.argv[1] )

for i in list:
	print i
print
for i in list:
	i.doit()
