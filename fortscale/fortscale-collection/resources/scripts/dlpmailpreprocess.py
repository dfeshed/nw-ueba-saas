#!/usr/bin/env python
import os
import sys
from subprocess import PIPE,Popen


def run():
    if len(sys.argv) != 3:
        print "Bad number of arguments. should be 2 (file name & method(preprocess | cleanup)"
        sys.exit(1)
    control_message = ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,Fortscale Control,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,," \
                      ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,"
    file_name = sys.argv[1]
    method = sys.argv[2]
    with open(file_name, "a") as file:
        if method == "preprocess":
            file.write(control_message)
        elif method == "cleanup":
            line = Popen(['tail', '-1', file_name], stdout=PIPE).communicate()[0]
            if line == control_message:
                remove_last_line(file_name)
        else:
            error_message = "bad method: " + method + ". valid methods are: preprocess, cleanup" + os.linesep
            sys.stderr.write(error_message)
            return -1
    return 0


def remove_last_line(file_name):  # credit: Saqib@stackoverflow
    file = open(file_name, "r+")

    # Move the pointer (similar to a cursor in a text editor) to the end of the file.
    file.seek(0, os.SEEK_END)

    # This code means the following code skips the very last character in the file -
    # i.e. in the case the last line is null we delete the last line
    # and the penultimate one
    pos = file.tell() - 1

    # Read each character in the file one at a time from the penultimate
    # character going backwards, searching for a newline character
    # If we find a new line, exit the search
    while pos > 0 and file.read(1) != "\n":
        pos -= 1
        file.seek(pos, os.SEEK_SET)

    # So long as we're not at the start of the file, delete all the characters ahead of this position
    if pos > 0:
        file.seek(pos, os.SEEK_SET)
        file.truncate()

    file.close()


def main():
    return run()


if __name__ == '__main__':
    sys.exit(main())
