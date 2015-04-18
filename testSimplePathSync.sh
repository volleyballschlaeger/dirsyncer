#!/bin/bash

echo "etc/fstab.txt;file:///etc/fstab;$( md5sum /etc/fstab | cut -d " " -f 1 );" | ./testSimplePathSync.py /dev/shm
