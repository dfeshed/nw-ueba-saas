export default [
  {
    '_id': '5ab290bbe4b06cf1a6a1de85',
    'name': 'Destination Domain',
    'literals': [
      {
        'value': 'ad.domain.dst '
      },
      {
        'value': 'destinationDnsDomain '
      },
      {
        'value': 'domain.dst '
      },
      {
        'value': 'dst_domainname '
      },
      {
        'value': 'dstdomain '
      },
      {
        'value': 'ad.domain.dst:'
      },
      {
        'value': 'destinationDnsDomain:'
      },
      {
        'value': 'domain.dst:'
      },
      {
        'value': 'dst_domainname:'
      },
      {
        'value': 'dstdomain:'
      },
      {
        'value': 'ad.domain.dst='
      },
      {
        'value': 'destinationDnsDomain='
      },
      {
        'value': 'domain.dst='
      },
      {
        'value': 'dst_domainname='
      },
      {
        'value': 'dstdomain='
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'domain.dst',
          'index': '0',
          'format': 'Text'
        }
      ],
      'format': 'Hostname'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': false,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de86',
    'name': 'Source Domain',
    'literals': [
      {
        'value': 'ad.domain.src:'
      },
      {
        'value': 'src_domainname:'
      },
      {
        'value': 'ad.domain.src='
      },
      {
        'value': 'src_domainname='
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'domain.src',
          'index': '0',
          'format': 'Text'
        }
      ],
      'format': 'Hostname'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de87',
    'name': 'Any Domain',
    'literals': [
      {
        'value': ' domain '
      },
      {
        'value': ' adom '
      },
      {
        'value': ' domain address '
      },
      {
        'value': ' domain invalid '
      },
      {
        'value': ' domain_name '
      },
      {
        'value': ' domainname '
      },
      {
        'value': ' deviceDnsDomain '
      },
      {
        'value': ' domain:'
      },
      {
        'value': ' adom:'
      },
      {
        'value': ' domain address:'
      },
      {
        'value': ' domain invalid:'
      },
      {
        'value': ' domain_name:'
      },
      {
        'value': ' domainname:'
      },
      {
        'value': ' deviceDnsDomain:'
      },
      {
        'value': ' domain address:'
      },
      {
        'value': ' domain invalid:'
      },
      {
        'value': ' domain name:'
      },
      {
        'value': ' domain ID:'
      },
      {
        'value': ' domain='
      },
      {
        'value': ' adom='
      },
      {
        'value': ' domain address='
      },
      {
        'value': ' domain invalid='
      },
      {
        'value': ' domain_name='
      },
      {
        'value': ' domainname='
      },
      {
        'value': ' deviceDnsDomain='
      },
      {
        'value': ' domain address='
      },
      {
        'value': ' domain invalid='
      },
      {
        'value': ' domain name='
      },
      {
        'value': ' domain ID='
      },
      {
        'value': ',domain '
      },
      {
        'value': ',adom '
      },
      {
        'value': ',domain address '
      },
      {
        'value': ',domain invalid '
      },
      {
        'value': ',domain_name '
      },
      {
        'value': ',domainname '
      },
      {
        'value': ',deviceDnsDomain '
      },
      {
        'value': ',domain:'
      },
      {
        'value': ',adom:'
      },
      {
        'value': ',domain address:'
      },
      {
        'value': ',domain invalid:'
      },
      {
        'value': ',domain_name:'
      },
      {
        'value': ',domainname:'
      },
      {
        'value': ',deviceDnsDomain:'
      },
      {
        'value': ',domain address:'
      },
      {
        'value': ',domain invalid:'
      },
      {
        'value': ',domain name:'
      },
      {
        'value': ',domain ID:'
      },
      {
        'value': ',domain='
      },
      {
        'value': ',adom='
      },
      {
        'value': ',domain address='
      },
      {
        'value': ',domain invalid='
      },
      {
        'value': ',domain_name='
      },
      {
        'value': ',domainname='
      },
      {
        'value': ',deviceDnsDomain='
      },
      {
        'value': ',domain address='
      },
      {
        'value': ',domain invalid='
      },
      {
        'value': ',domain name='
      },
      {
        'value': ',domain ID='
      },
      {
        'value': '^domain='
      },
      {
        'value': '^domain_name='
      },
      {
        'value': '^domainname='
      },
      {
        'value': 'Domain '
      },
      {
        'value': 'Domain Name '
      },
      {
        'value': 'ADMIN_DOMAIN '
      },
      {
        'value': 'ADDomain '
      },
      {
        'value': 'AUDIT_DOMAIN '
      },
      {
        'value': 'ADMIN_DOMAIN:'
      },
      {
        'value': 'Domain-'
      },
      {
        'value': 'Domain Name-'
      },
      {
        'value': 'Domain:'
      },
      {
        'value': 'Domain Name:'
      },
      {
        'value': 'AUDIT_DOMAIN:'
      },
      {
        'value': 'DNS:'
      },
      {
        'value': 'ADDomain:'
      },
      {
        'value': 'DDNS server:'
      },
      {
        'value': 'Domain set to:'
      },
      {
        'value': 'FQDN:'
      },
      {
        'value': 'LogonDomain:'
      },
      {
        'value': 'Domain='
      },
      {
        'value': 'Domain Name='
      },
      {
        'value': 'ADMIN_DOMAIN='
      },
      {
        'value': 'AUDIT_DOMAIN='
      },
      {
        'value': 'DNS='
      },
      {
        'value': 'ADDomain='
      },
      {
        'value': 'DDNS server='
      },
      {
        'value': 'FQDN='
      },
      {
        'value': 'LogonDomain='
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'domain',
          'index': '0',
          'format': 'Text'
        }
      ],
      'format': 'Hostname'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de88',
    'name': 'Client Username',
    'literals': [
      {
        'value': ' ruser '
      },
      {
        'value': ' ruser:'
      },
      {
        'value': ' ruser='
      },
      {
        'value': ' ruser-'
      },
      {
        'value': ',ruser '
      },
      {
        'value': ',ruser:'
      },
      {
        'value': ',ruser='
      },
      {
        'value': ',ruser-'
      },
      {
        'value': ';ruser '
      },
      {
        'value': ';ruser:'
      },
      {
        'value': ';ruser='
      },
      {
        'value': ';ruser-'
      },
      {
        'value': '^ruser='
      },
      {
        'value': 'Client User Name '
      },
      {
        'value': 'Client User Name:'
      },
      {
        'value': 'Client User Name='
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'user.src',
          'index': '1',
          'format': 'Text'
        }
      ],
      'regex': '\\s*([\\w_.@-]*)'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de89',
    'name': 'Username',
    'literals': [
      {
        'value': ' username '
      },
      {
        'value': ' username:'
      },
      {
        'value': ' suser:'
      },
      {
        'value': ' user:'
      },
      {
        'value': ' duser:'
      },
      {
        'value': ' name='
      },
      {
        'value': ' username='
      },
      {
        'value': ' suser='
      },
      {
        'value': ' user='
      },
      {
        'value': ' duser='
      },
      {
        'value': ',username '
      },
      {
        'value': ',user '
      },
      {
        'value': ',username:'
      },
      {
        'value': ',user:'
      },
      {
        'value': ',username='
      },
      {
        'value': ',suser='
      },
      {
        'value': ',user='
      },
      {
        'value': ',duser='
      },
      {
        'value': ';username '
      },
      {
        'value': ';username:'
      },
      {
        'value': ';username='
      },
      {
        'value': '^username='
      },
      {
        'value': '^user='
      },
      {
        'value': 'UserName '
      },
      {
        'value': 'User '
      },
      {
        'value': 'AddedUser '
      },
      {
        'value': 'DeletedUser '
      },
      {
        'value': 'OriginalUserName '
      },
      {
        'value': 'OS_User '
      },
      {
        'value': 'CN:'
      },
      {
        'value': 'Login:'
      },
      {
        'value': 'UserName:'
      },
      {
        'value': 'User:'
      },
      {
        'value': 'New user added:'
      },
      {
        'value': 'User has been deleted:'
      },
      {
        'value': 'AddedUser:'
      },
      {
        'value': 'DeletedUser:'
      },
      {
        'value': 'OriginalUserName:'
      },
      {
        'value': 'OS_User:'
      },
      {
        'value': 'CN='
      },
      {
        'value': 'Login='
      },
      {
        'value': 'Name='
      },
      {
        'value': 'User='
      },
      {
        'value': 'New user added='
      },
      {
        'value': 'User has been deleted='
      },
      {
        'value': 'AddedUser='
      },
      {
        'value': 'DeletedUser='
      },
      {
        'value': 'OriginalUserName='
      },
      {
        'value': 'OS_User='
      },
      {
        'value': 'OSUSERID='
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'user.dst',
          'index': '1',
          'format': 'Text'
        }
      ],
      'regex': '\\s*([\\w_.@-]*)'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de8a',
    'name': 'Destination Port',
    'literals': [
      {
        'value': ' destinationPort '
      },
      {
        'value': ' dport '
      },
      {
        'value': ' dst_port '
      },
      {
        'value': ' dstPort '
      },
      {
        'value': ' destinationPort:'
      },
      {
        'value': ' dport:'
      },
      {
        'value': ' dst_port:'
      },
      {
        'value': ' dstPort:'
      },
      {
        'value': ' destinationPort='
      },
      {
        'value': ' dport='
      },
      {
        'value': ' dst_port='
      },
      {
        'value': ' dstPort='
      },
      {
        'value': ',destinationPort '
      },
      {
        'value': ',dport '
      },
      {
        'value': ',dst_port '
      },
      {
        'value': ',dstPort '
      },
      {
        'value': ',destinationPort:'
      },
      {
        'value': ',dport:'
      },
      {
        'value': ',dst_port:'
      },
      {
        'value': ',dstPort:'
      },
      {
        'value': ',destinationPort='
      },
      {
        'value': ',dport='
      },
      {
        'value': ',dst_port='
      },
      {
        'value': ',dstPort='
      },
      {
        'value': '^destinationPort='
      },
      {
        'value': '^dport='
      },
      {
        'value': '^dst_port='
      },
      {
        'value': '^dstPort='
      },
      {
        'value': 'Destination Port '
      },
      {
        'value': 'Remote Port '
      },
      {
        'value': 'Destination Port:'
      },
      {
        'value': 'Dst Port:'
      },
      {
        'value': 'Remote Port:'
      },
      {
        'value': 'Destination Port='
      },
      {
        'value': 'Dst Port='
      },
      {
        'value': 'Remote Port='
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'port.dst',
          'index': '0',
          'format': 'UInt16'
        }
      ],
      'format': 'UInt16'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de8b',
    'name': 'Source Port',
    'literals': [
      {
        'value': ' sport '
      },
      {
        'value': ' srcport '
      },
      {
        'value': ' src_port '
      },
      {
        'value': ' src port '
      },
      {
        'value': ' sourceport '
      },
      {
        'value': ' on port:'
      },
      {
        'value': ' sport:'
      },
      {
        'value': ' spt:'
      },
      {
        'value': ' srcport:'
      },
      {
        'value': ' src_port:'
      },
      {
        'value': ' src port:'
      },
      {
        'value': ' sourceport:'
      },
      {
        'value': ' sport='
      },
      {
        'value': ' spt='
      },
      {
        'value': ' srcport='
      },
      {
        'value': ' src_port='
      },
      {
        'value': ' src port='
      },
      {
        'value': ' sourceport='
      },
      {
        'value': ',sport:'
      },
      {
        'value': ',spt:'
      },
      {
        'value': ',srcport:'
      },
      {
        'value': ',src_port:'
      },
      {
        'value': ',src port:'
      },
      {
        'value': ',sourceport:'
      },
      {
        'value': ',sport='
      },
      {
        'value': ',spt='
      },
      {
        'value': ',srcport='
      },
      {
        'value': ',src_port='
      },
      {
        'value': ',sourceport='
      },
      {
        'value': ';sourceport:'
      },
      {
        'value': ';sourceport='
      },
      {
        'value': 'Source Port '
      },
      {
        'value': 'Source Port:'
      },
      {
        'value': 'Source Port='
      },
      {
        'value': 'Source Port-'
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'port.src',
          'index': '0',
          'format': 'UInt16'
        }
      ],
      'format': 'UInt16'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de8c',
    'name': 'Any Port',
    'literals': [
      {
        'value': ' port '
      },
      {
        'value': ' port:'
      },
      {
        'value': ' port='
      },
      {
        'value': ' port-'
      },
      {
        'value': ',port '
      },
      {
        'value': ',port:'
      },
      {
        'value': ',port='
      },
      {
        'value': ';port '
      },
      {
        'value': ';port:'
      },
      {
        'value': ';port='
      },
      {
        'value': '^port='
      },
      {
        'value': 'Port '
      },
      {
        'value': 'Port:'
      },
      {
        'value': 'Port='
      },
      {
        'value': 'Port-'
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'port',
          'index': '0',
          'format': 'UInt16'
        }
      ],
      'format': 'UInt16'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de8d',
    'name': 'Destination MacAddress',
    'literals': [
      {
        'value': ' dmac '
      },
      {
        'value': ' dstMAC '
      },
      {
        'value': ' eth.dst '
      },
      {
        'value': ' dmac:'
      },
      {
        'value': ' dstMAC:'
      },
      {
        'value': ' eth.dst:'
      },
      {
        'value': ' dmac='
      },
      {
        'value': ' dstMAC='
      },
      {
        'value': ' eth.dst='
      },
      {
        'value': ',dmac:'
      },
      {
        'value': ',dstMAC:'
      },
      {
        'value': ',eth.dst:'
      },
      {
        'value': ',dmac='
      },
      {
        'value': ',dstMAC='
      },
      {
        'value': ';dmac='
      },
      {
        'value': ';dstMAC='
      },
      {
        'value': '^dmac='
      },
      {
        'value': '^dstMAC='
      },
      {
        'value': 'EndPointMACAddress '
      },
      {
        'value': 'Destination MAC '
      },
      {
        'value': 'MAC dest '
      },
      {
        'value': 'EndPointMACAddress:'
      },
      {
        'value': 'Destination MAC:'
      },
      {
        'value': 'MAC dest:'
      },
      {
        'value': 'EndPointMACAddress='
      },
      {
        'value': 'Destination MAC='
      },
      {
        'value': 'MAC dest='
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'eth.dst',
          'index': '0',
          'format': 'MAC'
        }
      ],
      'format': 'MAC'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de8e',
    'name': 'Source MacAddress',
    'literals': [
      {
        'value': ' smac '
      },
      {
        'value': ' client_mac '
      },
      {
        'value': ' eth addr '
      },
      {
        'value': ' eth.src '
      },
      {
        'value': ' smac:'
      },
      {
        'value': ' client_mac:'
      },
      {
        'value': ' eth addr:'
      },
      {
        'value': ' eth.src:'
      },
      {
        'value': ' smac='
      },
      {
        'value': ' client_mac='
      },
      {
        'value': ' eth.src='
      },
      {
        'value': ',smac:'
      },
      {
        'value': ',client_mac:'
      },
      {
        'value': ',smac='
      },
      {
        'value': ',client_mac='
      },
      {
        'value': ';smac:'
      },
      {
        'value': ';client_mac:'
      },
      {
        'value': ';smac='
      },
      {
        'value': ';client_mac='
      },
      {
        'value': '^smac='
      },
      {
        'value': '^client_mac='
      },
      {
        'value': 'Source MAC '
      },
      {
        'value': 'The attackers mac address is '
      },
      {
        'value': 'Source MAC:'
      },
      {
        'value': 'MAC source:'
      },
      {
        'value': 'Source MAC='
      },
      {
        'value': 'The attackers mac address is='
      },
      {
        'value': 'MAC source='
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'eth.src',
          'index': '0',
          'format': 'MAC'
        }
      ],
      'format': 'MAC'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de8f',
    'name': 'Any MacAddress',
    'literals': [
      {
        'value': ' mac '
      },
      {
        'value': ' mac:'
      },
      {
        'value': ' mac='
      },
      {
        'value': ',mac '
      },
      {
        'value': ',mac:'
      },
      {
        'value': ',mac='
      },
      {
        'value': '^mac='
      },
      {
        'value': 'MAC '
      },
      {
        'value': 'MAC address '
      },
      {
        'value': 'MAC:'
      },
      {
        'value': 'MAC address:'
      },
      {
        'value': 'MAC='
      },
      {
        'value': 'MAC address='
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'alias.mac',
          'index': '0',
          'format': 'MAC'
        }
      ],
      'format': 'MAC'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de90',
    'name': 'Source IP or IP:Port',
    'literals': [
      {
        'value': ' from '
      },
      {
        'value': ' src '
      },
      {
        'value': ' host '
      },
      {
        'value': ' local '
      },
      {
        'value': ' srcip '
      },
      {
        'value': ' client_ip '
      },
      {
        'value': ' from:'
      },
      {
        'value': ' src:'
      },
      {
        'value': ' host:'
      },
      {
        'value': ' local:'
      },
      {
        'value': ' srcip:'
      },
      {
        'value': ' client_ip:'
      },
      {
        'value': ' from='
      },
      {
        'value': ' src='
      },
      {
        'value': ' host='
      },
      {
        'value': ' local='
      },
      {
        'value': ' srcip='
      },
      {
        'value': ' client_ip='
      },
      {
        'value': ',from '
      },
      {
        'value': ',src '
      },
      {
        'value': ',host '
      },
      {
        'value': ',local '
      },
      {
        'value': ',srcip '
      },
      {
        'value': ',client_ip '
      },
      {
        'value': ',from:'
      },
      {
        'value': ',src:'
      },
      {
        'value': ',host:'
      },
      {
        'value': ',local:'
      },
      {
        'value': ',srcip:'
      },
      {
        'value': ',client_ip:'
      },
      {
        'value': ',from='
      },
      {
        'value': ',src='
      },
      {
        'value': ',host='
      },
      {
        'value': ',local='
      },
      {
        'value': ',srcip='
      },
      {
        'value': ',client_ip='
      },
      {
        'value': '^from='
      },
      {
        'value': '^src='
      },
      {
        'value': '^host='
      },
      {
        'value': '^local='
      },
      {
        'value': '^srcip='
      },
      {
        'value': '^client_ip='
      },
      {
        'value': 'Source '
      },
      {
        'value': 'Source Address '
      },
      {
        'value': 'Source IP '
      },
      {
        'value': 'Source Network Address '
      },
      {
        'value': 'Client Address '
      },
      {
        'value': 'Client IP '
      },
      {
        'value': 'Client IP Address '
      },
      {
        'value': 'Source:'
      },
      {
        'value': 'Source Address:'
      },
      {
        'value': 'Source IP:'
      },
      {
        'value': 'Source Network Address:'
      },
      {
        'value': 'ClientIP:'
      },
      {
        'value': 'Client:'
      },
      {
        'value': 'Client Address:'
      },
      {
        'value': 'Client IP:'
      },
      {
        'value': 'Client IP Address:'
      },
      {
        'value': 'Source='
      },
      {
        'value': 'Source Address='
      },
      {
        'value': 'Source IP='
      },
      {
        'value': 'Source Network Address='
      },
      {
        'value': 'ClientIP='
      },
      {
        'value': 'Client='
      },
      {
        'value': 'Client Address='
      },
      {
        'value': 'Client IP='
      },
      {
        'value': 'Client IP Address='
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'ip.src',
          'index': '1',
          'format': 'IPv4'
        },
        {
          'key': 'port.src',
          'index': '2',
          'format': 'UInt16'
        }
      ],
      'regex': '\\s*(\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b):?(\\d*)'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de91',
    'name': 'Destination IP or IP:Port',
    'literals': [
      {
        'value': ' dst '
      },
      {
        'value': ' destination IP '
      },
      {
        'value': ' dstIP '
      },
      {
        'value': ' dstAddr '
      },
      {
        'value': ' dst:'
      },
      {
        'value': ' destination IP:'
      },
      {
        'value': ' dstIP:'
      },
      {
        'value': ' dstAddr:'
      },
      {
        'value': ' dst='
      },
      {
        'value': ' destination IP='
      },
      {
        'value': ' dstIP='
      },
      {
        'value': ' dstAddr='
      },
      {
        'value': ',destination IP '
      },
      {
        'value': ',dst:'
      },
      {
        'value': ',destination IP:'
      },
      {
        'value': ',dstIP:'
      },
      {
        'value': ',dstAddr:'
      },
      {
        'value': ',dst='
      },
      {
        'value': ',destination IP='
      },
      {
        'value': ',dstIP='
      },
      {
        'value': ',dstAddr='
      },
      {
        'value': ';destination IP:'
      },
      {
        'value': ';dstIP:'
      },
      {
        'value': ';dstAddr:'
      },
      {
        'value': ';dst='
      },
      {
        'value': ';destination IP='
      },
      {
        'value': ';dstIP='
      },
      {
        'value': ';dstAddr='
      },
      {
        'value': '^dst='
      },
      {
        'value': '^destination IP='
      },
      {
        'value': '^dstIP='
      },
      {
        'value': '^dstAddr='
      },
      {
        'value': 'DEST '
      },
      {
        'value': 'Destination '
      },
      {
        'value': 'Destination Address '
      },
      {
        'value': 'DST '
      },
      {
        'value': 'Dst IP '
      },
      {
        'value': 'DST_IP '
      },
      {
        'value': 'ServerIP '
      },
      {
        'value': 'DEST:'
      },
      {
        'value': 'Destination:'
      },
      {
        'value': 'Destination Address:'
      },
      {
        'value': 'DST:'
      },
      {
        'value': 'Dst IP:'
      },
      {
        'value': 'DST_IP:'
      },
      {
        'value': 'ServerIP:'
      },
      {
        'value': 'DEST='
      },
      {
        'value': 'Destination='
      },
      {
        'value': 'Destination Address='
      },
      {
        'value': 'DST='
      },
      {
        'value': 'Dst IP='
      },
      {
        'value': 'DST_IP='
      },
      {
        'value': 'ServerIP='
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'ip.dst',
          'index': '1',
          'format': 'IPv4'
        },
        {
          'key': 'port.dst',
          'index': '2',
          'format': 'UInt16'
        }
      ],
      'regex': '\\s*(\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b):?(\\d*)'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de92',
    'name': 'Any IP or IP:Port',
    'literals': [
      {
        'value': ' addr '
      },
      {
        'value': ' address '
      },
      {
        'value': ' addr='
      },
      {
        'value': ' address='
      },
      {
        'value': ';addr:'
      },
      {
        'value': ';address:'
      },
      {
        'value': ';addr='
      },
      {
        'value': ';address='
      },
      {
        'value': ',addr '
      },
      {
        'value': ',address '
      },
      {
        'value': ',addr:'
      },
      {
        'value': ',address:'
      },
      {
        'value': ',addr='
      },
      {
        'value': ',address='
      },
      {
        'value': '^addr='
      },
      {
        'value': '^address='
      },
      {
        'value': 'IP '
      },
      {
        'value': 'Address '
      },
      {
        'value': 'IP:'
      },
      {
        'value': 'Address:'
      },
      {
        'value': 'IP='
      },
      {
        'value': 'Address='
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'alias.ip',
          'index': '1',
          'format': 'IPv4'
        },
        {
          'key': 'port',
          'index': '2',
          'format': 'UInt16'
        }
      ],
      'regex': '\\s*(\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b):?(\\d*)'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de93',
    'name': 'Source Email Address',
    'literals': [
      {
        'value': ' from:'
      },
      {
        'value': ' sender:'
      },
      {
        'value': ' from='
      },
      {
        'value': ' sender='
      },
      {
        'value': ' from <'
      },
      {
        'value': ' sender <'
      },
      {
        'value': ',from:'
      },
      {
        'value': ',sender:'
      },
      {
        'value': ',from='
      },
      {
        'value': ',sender='
      },
      {
        'value': ',from <'
      },
      {
        'value': ',sender <'
      },
      {
        'value': ';from:'
      },
      {
        'value': ';sender:'
      },
      {
        'value': ';from='
      },
      {
        'value': ';sender='
      },
      {
        'value': ';from <'
      },
      {
        'value': ';sender <'
      },
      {
        'value': '^from='
      },
      {
        'value': '^sender='
      },
      {
        'value': 'From:'
      },
      {
        'value': 'Sender:'
      },
      {
        'value': 'From='
      },
      {
        'value': 'Sender='
      },
      {
        'value': 'From <'
      },
      {
        'value': 'Sender <'
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'email.src',
          'index': '0',
          'format': 'Text'
        }
      ],
      'format': 'EMail'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de94',
    'name': 'Destination Email Address',
    'literals': [
      {
        'value': ' to '
      },
      {
        'value': ' to='
      },
      {
        'value': ' to:'
      },
      {
        'value': ' to <'
      },
      {
        'value': ',to '
      },
      {
        'value': ',to='
      },
      {
        'value': ',to:'
      },
      {
        'value': ',to <'
      },
      {
        'value': ';to '
      },
      {
        'value': ';to='
      },
      {
        'value': ';to:'
      },
      {
        'value': ';to <'
      },
      {
        'value': '^to='
      },
      {
        'value': 'To '
      },
      {
        'value': 'To='
      },
      {
        'value': 'To:'
      },
      {
        'value': 'To <'
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'email.dst',
          'index': '0',
          'format': 'Text'
        }
      ],
      'format': 'EMail'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  },
  {
    '_id': '5ab290bbe4b06cf1a6a1de95',
    'name': 'URL',
    'literals': [
      {
        'value': ' url '
      },
      {
        'value': ' uri '
      },
      {
        'value': ' url:'
      },
      {
        'value': ' uri:'
      },
      {
        'value': ' url='
      },
      {
        'value': ' uri='
      },
      {
        'value': ',url '
      },
      {
        'value': ',uri '
      },
      {
        'value': ',url:'
      },
      {
        'value': ',uri:'
      },
      {
        'value': ',url='
      },
      {
        'value': ',uri='
      },
      {
        'value': ';url '
      },
      {
        'value': ';uri '
      },
      {
        'value': ';uri:'
      },
      {
        'value': ';url:'
      },
      {
        'value': ';url='
      },
      {
        'value': ';uri='
      },
      {
        'value': '^url='
      },
      {
        'value': '^uri='
      },
      {
        'value': 'URL '
      },
      {
        'value': 'URL:'
      },
      {
        'value': 'URL='
      }
    ],
    'pattern': {
      'captures': [
        {
          'key': 'url',
          'index': '0',
          'format': 'Text'
        }
      ],
      'format': 'URI'
    },
    'ruleMetas': [],
    'dirty': false,
    'outOfBox': true,
    'override': false
  }
];