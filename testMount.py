#!/usr/bin/env jython

from os import mkdir
from java.io import File
import JSONDirSync

try:
	mkdir( "test.d" )
except:
	pass

complete = JSONDirSync.RecvDirContent( File( "test.d" ), "https://raw.githubusercontent.com/volleyballschlaeger/dirsyncer/master/testmnt1.json" )
print "completed: " + str( complete )
