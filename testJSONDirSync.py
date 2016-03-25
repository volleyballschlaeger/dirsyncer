#!/usr/bin/env jython

from os import mkdir
from java.io import File
from org.json import JSONArray
import JSONDirSync

desc = """
[
  {
    "name": "eicar",
    "type": "dir",
    "entries": [
      {
        "name": "eicar.asm",
        "type": "file",
        "url": "https://raw.githubusercontent.com/volleyballschlaeger/eicar/master/eicar.asm",
        "md5": "7e81d137ab58b63ddbc21f4fd95ff2ab"
      },
      {
        "name": "Makefile",
        "type": "file",
        "url": "https://raw.githubusercontent.com/volleyballschlaeger/eicar/master/Makefile",
        "md5": "16e533b03adc60bc4bb2106f9d240a52"
      }
    ]
  },
  {
    "name": "fstab",
    "type": "file",
    "url": "file:///etc/fstab"
  }
]
"""

try:
	mkdir( "test.d" )
except:
	pass

complete = JSONDirSync.RecvDirContent( File( "test.d" ), JSONArray( desc ) )
print "completed: " + str( complete )
