import os
import subprocess
import sys

temporary_file_name = 'temp_testsuite.js'

import_list = ['game_logic.js', 'test_websocket.js', 'game_events.js']

testsuite_main = 'testsuite.js'

def appendFileTo(source, destination):
    sourcefile = open(source, 'r')
    destinationfile = open(destination, 'a')
    for line in sourcefile:
        destinationfile.write(line)
    sourcefile.close()
    destinationfile.close()
    
# clear previous output
if os.path.isfile(temporary_file_name):
    os.remove(temporary_file_name)

# copy includes
for i in import_list:
    appendFileTo(i, temporary_file_name)

# copy main
appendFileTo(testsuite_main, temporary_file_name)

# run nodejs
code = subprocess.call(['nodejs', temporary_file_name])
sys.exit(code)