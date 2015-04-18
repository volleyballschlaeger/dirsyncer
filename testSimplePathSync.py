#!/usr/bin/env jython

import sys
from java.io import File
import SimplePathSync

s = SimplePathSync( File( sys.argv[1] ) )

for line in sys.stdin:
	s.RecvPath( line );
