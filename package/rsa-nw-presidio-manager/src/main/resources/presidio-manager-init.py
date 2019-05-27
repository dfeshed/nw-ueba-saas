"""
Every time the system is restarted, especially after an upgrade, the Presidio Manager service could be loaded with
changed or new configurations. These configurations need to be updated outside the Configuration Server as well.
Therefore, this Python script:
1. Waits for the Presidio Manager service to finish loading.
2. Sends an empty Patch request to redistribute all configurations.
   Specifically, the request updates the 'workflows-default.json' file.

Author: Lior Govrin.
"""
import requests
import time

is_service_loaded = False
response = None

while not is_service_loaded:
    try:
        # Send a Get request to check if the Presidio Manager service finished loading.
        response = requests.get(url='http://localhost:8881/configuration')
        # If there wasn't a connection error, the service finished loading successfully.
        is_service_loaded = True
    except requests.exceptions.ConnectionError:
        # If there was a connection error, the service is still loading.
        time.sleep(5)

# If the status code is 200 (i.e. "OK"), the Presidio Manager service was loaded after a possible upgrade - Send an
# empty Patch request. Otherwise, the status code is 500 (i.e. "Internal Server Error"), because the service wasn't
# configured yet - Do nothing. This happens after the first deployment of the UEBA server, before the system is
# configured (before sending a Put request).
if response.status_code == 200:
    requests.patch(url='http://localhost:8881/configuration',
                   data='{"operations": []}',
                   headers={"Cache-Control": "no-cache", "Content-Type": "application/json"})
