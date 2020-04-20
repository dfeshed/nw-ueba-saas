import json
import os
import re
import socket
import urllib

# Read the current UEBA server configuration.
with open('/etc/netwitness/presidio/configserver/configurations/application-presidio.json') as json_file:
    application_presidio = json.load(json_file)

# Extract the user, password, host and port from the data pulling source configuration.
source = application_presidio['dataPulling']['source']
match = re.match('^nws://(.+):(.+)@(.+):(.+)$', source)
user = match.group(1)
password = match.group(2)
password = urllib.unquote(password).decode('utf8')
host = match.group(3)
port = match.group(4)

# Extract the IP address of the Broker's host (or Concentrator).
try:
    # Leave 'host' as is if it is already a valid IPv4 address.
    socket.inet_aton(host)
    print("'host' is a valid IPv4 address.")
except socket.error:
    # Otherwise, 'host' is the ID of the Broker's host (or Concentrator).
    # Use the Orchestration CLI Client and this ID to find the IP address.
    json_filter = '$[?(@.id=="{}")]["ipv4"]'.format(host)
    command = "orchestration-cli-client --list-hosts --quiet --json --filter '{}' | jq -r .[0]".format(json_filter)
    host = os.popen(command).read().strip()
    print("'host' was set to {}.".format(host))

# Map the port number to the source type.
if port in ['50003', '56003']:
    source_type = 'broker'
elif port in ['50005', '56005']:
    source_type = 'concentrator'
else:
    raise ValueError('Unknown port number {}.'.format(port))

# Extract the start time and schemas from the data pipeline configuration.
data_pipeline = application_presidio['dataPipeline']
time = data_pipeline['startTime']
schemas = ' '.join(data_pipeline['schemas'])

# Extract the forwarding enable/disable flag from the output forwarding configuration.
enable_forwarding = application_presidio['outputForwarding']['enableForwarding']

# Rerun the UEBA server configuration command.
os.system('sh /opt/rsa/saTools/bin/ueba-server-config -u {} -p \'{}\' -h {} -t {} -s "{}" {} -o {} -v'.format(
    user,
    password,
    host,
    time,
    schemas,
    '-e' if enable_forwarding else '',
    source_type
))
