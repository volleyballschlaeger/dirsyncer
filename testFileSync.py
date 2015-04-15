#!/usr/bin/env jython

import java.io.File
import FileSync

print "Creating /dev/shm/fstab if not exists.";
md5 = FileSync.md5sum( java.io.File( "/etc/fstab" ) );
FileSync.RecvFile( java.io.File( "/dev/shm" ), "fstab", "file:///etc/fstab", md5 );
