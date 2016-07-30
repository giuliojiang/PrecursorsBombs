#!/usr/bin/env python

import sys
import subprocess

args = sys.argv[1:]

cmd = []
cmd.append('java')
cmd.append('-Xmx256M')
cmd.append('-cp')
cmd.append('bin:jar/lib/ext/mysql-connector.jar')
cmd.append('net.precursorsbombs.serverlogic.BasicServer')
for a in args:
    cmd.append(a)
    
print(cmd)
code = subprocess.call(cmd)
sys.exit(code)
