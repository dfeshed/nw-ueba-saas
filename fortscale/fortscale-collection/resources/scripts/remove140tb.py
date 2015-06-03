#!/usr/bin/env python
from __future__ import print_function
__author__ = ','.join((
    'Maxim Kovgan <maxk@fortscale.com>',
    'Eran Silverman <erans@fortscale.com>',
))
# File Name: remove140tb
#    Created on: 12/17/14 at 12:03 PM
#    Copyright by Fortscale Security ltd. 2014

import sys


def get_evt_bytes(evt_line):
    parts = evt_line.split(', ')
### Dec 16 2014 00:00:01 sjce-vpn-cluster-2 : %ASA-4-113019: Group = AnyConnect_profile, Username = ragokula, IP = 73.189.241.130, Session disconnected. Session Type: SSL, Duration: 6h:58m:34s, Bytes xmt: 37106655, Bytes rcv: 6119134, Reason: Idle Timeout
#    print("length: " + str(len(parts)))
#    print("xmt: " + parts[-3])
#    print("rcv: " + parts[-2])
    xmt = parts[-3].split(': ')[1]
    rcv = parts[-2].split(': ')[1]
    return xmt, rcv


def process(input, output):
    with open(output, "wb") as fd_out:
        with open(input, "rb") as fd_in:
            for i, l in enumerate(fd_in):
                if -1 != l.find("-113019: "):
                    xmt, rcv = get_evt_bytes(l)
                    if len(xmt) >= 12 or len(rcv) >= 12:
                        continue
                fd_out.write(l)


def main():
    # dirty way of taking 2nd to the end
    input, output = sys.argv[1:]
    process(input, output)
    return 0

if __name__ == '__main__':
    sys.exit(main())
