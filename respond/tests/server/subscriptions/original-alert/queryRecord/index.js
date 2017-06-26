export default {
  subscriptionDestination: '/user/queue/alerts/original',
  requestDestination: '/ws/respond/alerts/original',
  message(/* frame */) {
    return {
      data: {
        'instance_id': '1ee6236da64f03d7098b7f4f42ae6ae9',
        'engineUri': 'default',
        'events': [
          {
            'ip_proto': 17,
            'ip_src': '128.164.32.205',
            'lifetime': 32,
            'domain_src': 'gwu.edu',
            'medium': 1,
            'sessionid': 950012,
            'rid': 517035,
            'packets': 5,
            'eth_src': '00:0c:29:1d:49:85',
            'GeoIpLookup': [
              {
                'areaCode': 202,
                'dmaCode': 511,
                'ipv4': '128.164.32.205',
                'city': 'Washington',
                'countryCode': 'US',
                'ipv4Obj': '/128.164.32.205',
                'latitude': 38.93760681152344,
                'metroCode': 511,
                'postalCode': '20016',
                'countryName': 'United States',
                'region': 'DC',
                'longitude': -77.0927963256836
              }
            ],
            'latdec_dst': 38.937599182128906,
            'alert': 'Reporting Engine - Source IP Exists',
            'payload': 250,
            'longdec_src': -77.0927963256836,
            'city_src': 'Washington',
            'country_dst': 'United States',
            'org_dst': 'The George Washington University',
            'direction': 'outbound',
            'event_source_id': '10.25.50.35:56005:950012',
            'esa_time': 1498490271186,
            'streams': 1,
            'domain_dst': 'gwu.edu',
            'ip_dst': '128.164.35.255',
            'longdec_dst': -77.0927963256836,
            'udp_dstport': 137,
            'eth_dst': 'ff:ff:ff:ff:ff:ff',
            'eth_type': 2048,
            'latdec_src': 38.937599182128906,
            'size': 460,
            'netname': [
              'other src',
              'other dst'
            ],
            'udp_srcport': 137,
            'service': 137,
            'country_src': 'United States',
            'city_dst': 'Washington',
            'time': 1498490205,
            'org_src': 'The George Washington University',
            'analysis_session': [
              'not top 20 dst'
            ],
            'did': 'localhost.localdomain'
          }
        ]
      }
    };
  }
};