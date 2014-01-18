from collections import defaultdict
import fileinput
import sys
import re

comment = re.compile(r'#.*')

def parseLDIFRecord(ldif_record):
    ldif_data = defaultdict(list)
    # Fix stupid line wrapping in LDIF
    fixed_record = re.sub(r'\n ', '', ldif_record)

    for l in fixed_record.split('\n'):
        if not l.strip(): continue
        if comment.match(l): continue
        (k, v) = tuple(re.split(": |:: ", l, 1))
        ldif_data[k].append(v)
        
    return ldif_data

def parseLDIFFile(file_content, schema):
    for ldif_record in ''.join(file_content).split("\n\n"):
        csv = ""
        ldif_dict = parseLDIFRecord(ldif_record)
        for col in schema:
            csv += ';'.join(ldif_dict[col])
            csv +=  "|"
        sys.stdout.write(csv.rstrip("|").rstrip())
        print "\n",

def main():
    if len(sys.argv) < 2:
        print "Usage: cat ldif.txt | {0} <attributes> [...]".format(sys.argv[0])
        print "Where <attributes> contains a list of space-separated attributes to include in the CSV. LDIF data is read from stdin."
        sys.exit(-1)
        
    ldif_lines = sys.stdin.readlines()    
    csv_schema = sys.argv[1:]
    
    parseLDIFFile(ldif_lines, csv_schema)
    
if __name__ == '__main__':
    main()
