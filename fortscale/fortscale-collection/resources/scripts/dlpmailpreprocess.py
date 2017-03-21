#!/usr/bin/env python
import sys


def append_control_message():
    control_message = ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,Fortscale Control,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,," \
                      ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,"
    file_name = sys.argv[1]
    with open(file_name, "a") as file:
        file.write(control_message)
    return 0


def main():
    append_control_message()
    return 0


if __name__ == '__main__':
    sys.exit(main())
