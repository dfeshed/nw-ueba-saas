export default {
  subscriptionDestination: '/user/queue/investigate/aliases',
  requestDestination: '/ws/investigate/aliases',
  message(/* frame */) {
    return {
      meta: {
        complete: true
      },
      data
    };
  }
};

const data = {
  'udp.srcport': {
    '7': 'echo',
    '9': 'discard',
    '13': 'daytime',
    '17': 'qotd',
    '19': 'chargen',
    '37': 'time',
    '39': 'rlp',
    '42': 'nameserver',
    '53': 'domain',
    '67': 'bootps',
    '68': 'bootpc',
    '69': 'tftp',
    '88': 'kerberos',
    '111': 'sunrpc',
    '123': 'ntp',
    '135': 'epmap',
    '137': 'netbios-ns',
    '138': 'netbios-dgm',
    '161': 'snmp',
    '162': 'snmptrap',
    '213': 'ipx',
    '443': 'https',
    '445': 'cifs',
    '464': 'kpasswd',
    '500': 'isakmp',
    '512': 'biff',
    '513': 'who',
    '514': 'syslog',
    '517': 'talk',
    '518': 'ntalk',
    '525': 'timed',
    '533': 'netwall',
    '550': 'new-rwho',
    '560': 'rmonitor',
    '561': 'monitor',
    '749': 'kerberos-adm',
    '1167': 'phone',
    '1433': 'ms-sql-s',
    '1434': 'ms-sql-m',
    '1512': 'wins',
    '1701': 'l2tp',
    '1812': 'radiusauth',
    '1813': 'radacct',
    '2049': 'nfsd',
    '2504': 'nlbs'
  },
  'tcp.srcport': {
    '7': 'echo',
    '9': 'discard',
    '13': 'daytime',
    '17': 'qotd',
    '19': 'chargen',
    '20': 'ftp-data',
    '21': 'ftp',
    '22': 'ssh',
    '23': 'telnet',
    '25': 'smtp',
    '37': 'time',
    '42': 'nameserver',
    '43': 'nicname',
    '53': 'domain',
    '70': 'gopher',
    '79': 'finger',
    '80': 'http',
    '88': 'kerberos',
    '101': 'hostname',
    '102': 'iso-tsap',
    '107': 'rtelnet',
    '109': 'pop2',
    '110': 'pop3',
    '111': 'sunrpc',
    '113': 'auth',
    '117': 'uucp-path',
    '119': 'nntp',
    '135': 'epmap',
    '137': 'netbios-ns',
    '139': 'netbios-ssn',
    '143': 'imap',
    '158': 'pcmail-srv',
    '170': 'print-srv',
    '179': 'bgp',
    '194': 'irc',
    '389': 'ldap',
    '443': 'https',
    '445': 'cifs',
    '464': 'kpasswd',
    '512': 'exec',
    '513': 'login',
    '514': 'cmd',
    '515': 'printer',
    '520': 'efs',
    '526': 'tempo',
    '530': 'courier',
    '531': 'conference',
    '532': 'netnews',
    '540': 'uucp',
    '543': 'klogin',
    '544': 'kshell',
    '556': 'remotefs',
    '636': 'ldaps',
    '749': 'kerberos-adm',
    '993': 'imaps',
    '995': 'pop3s',
    '1109': 'kpop',
    '1122': 'msn im',
    '1352': 'notes',
    '1433': 'ms-sql-s',
    '1434': 'ms-sql-m',
    '1512': 'wins',
    '1521': 'tns',
    '1524': 'ingreslock',
    '1533': 'sametime',
    '1718': 'h323',
    '1720': 'rtp',
    '1723': 'pptp',
    '1863': 'msn im',
    '2049': 'nfs',
    '2053': 'knetd',
    '3389': 'rdp',
    '5050': 'yahoo im',
    '5060': 'sip',
    '5190': 'aim',
    '6346': 'gnuetella',
    '6667': 'irc',
    '9001': 'tor',
    '9030': 'tor',
    '9535': 'man'
  },
  'ip.proto': {
    '0': 'HOPOPT',
    '1': 'ICMP',
    '2': 'IGMP',
    '3': 'GGP',
    '4': 'IP',
    '5': 'ST',
    '6': 'TCP',
    '7': 'CBT',
    '8': 'EGP',
    '9': 'IGP',
    '10': 'BBN-RCC-M',
    '11': 'NVP-II',
    '12': 'PUP',
    '13': 'ARGUS',
    '14': 'EMCON',
    '15': 'XNET',
    '16': 'CHAOS',
    '17': 'UDP',
    '18': 'MUX',
    '19': 'DCN-MEAS',
    '20': 'HMP',
    '21': 'PRM',
    '22': 'XNS-IDP',
    '23': 'TRUNK-1',
    '24': 'TRUNK-2',
    '25': 'LEAF-1',
    '26': 'LEAF-2',
    '27': 'RDP',
    '28': 'IRTP',
    '29': 'ISO-TP4',
    '30': 'NETBLT',
    '31': 'MFE-NSP',
    '32': 'MERIT-INP',
    '33': 'SEP',
    '34': '3PC',
    '35': 'IDPR',
    '36': 'XTP',
    '37': 'DDP',
    '38': 'IDPR-CMTP',
    '39': 'TP++',
    '40': 'IL',
    '41': 'IPv6',
    '42': 'SDRP',
    '43': 'IPv6-Rout',
    '44': 'IPv6-Frag',
    '45': 'IDRP',
    '46': 'RSVP',
    '47': 'GRE',
    '48': 'MHRP',
    '49': 'BNA',
    '50': 'ESP',
    '51': 'AH',
    '52': 'I-NLSP',
    '53': 'SWIPE',
    '54': 'NARP',
    '55': 'MOBILE',
    '56': 'TLSP',
    '57': 'SKIP',
    '58': 'IPv6-ICMP',
    '59': 'IPv6-NoNx',
    '60': 'IPv6-Opts',
    '61': 'AnyHost',
    '62': 'CFTP',
    '63': 'AnyNetwork',
    '64': 'SAT-EXPAK',
    '65': 'KRYPTOLAN',
    '66': 'RVD',
    '67': 'IPPC',
    '68': 'AnyFile',
    '69': 'SAT-MON',
    '70': 'VISA',
    '71': 'IPCV',
    '72': 'CPNX',
    '73': 'CPHB',
    '74': 'WSN',
    '75': 'PVP',
    '76': 'BR-SAT-MO',
    '77': 'SUN-ND',
    '78': 'WB-MON',
    '79': 'WB-EXPAK',
    '80': 'ISO-IP',
    '81': 'VMTP',
    '82': 'SECURE-VM',
    '83': 'VINES',
    '84': 'TTP',
    '85': 'NSFNET-IG',
    '86': 'DGP',
    '87': 'TCF',
    '88': 'EIGRP',
    '89': 'OSPFIGP',
    '90': 'Sprite-RP',
    '91': 'LARP',
    '92': 'MTP',
    '93': 'AX.25',
    '94': 'IPIP',
    '95': 'MICP',
    '96': 'SCC-SP',
    '97': 'ETHERIP',
    '98': 'ENCAP',
    '99': 'AnyPrivate',
    '100': 'GMTP',
    '101': 'IFMP',
    '102': 'PNNI',
    '103': 'PIM',
    '104': 'ARIS',
    '105': 'SCPS',
    '106': 'QNX',
    '107': 'A/N',
    '108': 'IPComp',
    '109': 'SNP',
    '110': 'Compaq-Pe',
    '111': 'IPX-in-IP',
    '112': 'VRRP',
    '113': 'PGM',
    '114': 'AnyHop',
    '115': 'L2TP',
    '116': 'DDX',
    '117': 'IATP',
    '118': 'STP',
    '119': 'SRP',
    '120': 'UTI',
    '121': 'SMP',
    '122': 'SM',
    '123': 'PTP',
    '124': 'ISIS',
    '125': 'FIRE',
    '126': 'CRTP',
    '127': 'CRUDP',
    '128': 'SSCOPMCE',
    '129': 'IPLT',
    '130': 'SPS',
    '131': 'PIPE Pr',
    '132': 'SCTP St',
    '133': 'FC Fi',
    '134': 'RSVP-E2E-',
    '255': 'Reserved'
  },
  'service': {
    '0': 'OTHER',
    '20': 'FTPD',
    '21': 'FTP',
    '22': 'SSH',
    '23': 'TELNET',
    '25': 'SMTP',
    '53': 'DNS',
    '67': 'DHCP',
    '69': 'TFTP',
    '80': 'HTTP',
    '110': 'POP3',
    '111': 'SUNRPC',
    '119': 'NNTP',
    '123': 'NTP',
    '135': 'RPC',
    '137': 'NETBIOS',
    '139': 'SMB',
    '143': 'IMAP',
    '161': 'SNMP',
    '179': 'BGP',
    '443': 'SSL',
    '502': 'MODBUS',
    '520': 'RIP',
    '1024': 'EXCHANGE',
    '1080': 'SOCKS',
    '1122': 'MSN IM',
    '1344': 'ICAP',
    '1352': 'NOTES',
    '1433': 'TDS',
    '1521': 'TNS',
    '1533': 'SAMETIME',
    '1719': 'H.323',
    '1720': 'RTP',
    '2000': 'SKINNY',
    '2040': 'SOULSEEK',
    '2049': 'NFS',
    '3270': 'TN3270',
    '3389': 'RDP',
    '3700': 'DB2',
    '5050': 'YAHOO IM',
    '5060': 'SIP',
    '5190': 'AOL IM',
    '5222': 'Google Talk',
    '5900': 'VNC',
    '6346': 'GNUTELLA',
    '6667': 'IRC',
    '6801': 'Net2Phone',
    '6881': 'BITTORRENT',
    '8000': 'QQ',
    '8002': 'YCHAT',
    '8019': 'WEBMAIL',
    '8082': 'FIX',
    '20000': 'DNP3',
    '1000000': 'KERNEL',
    '1000001': 'USER',
    '1000003': 'SYSTEM',
    '1000004': 'AUTH',
    '1000005': 'LOGGER',
    '1000006': 'LPD',
    '1000008': 'UUCP',
    '1000009': 'SCHEDULE',
    '1000010': 'SECURITY',
    '1000013': 'AUDIT',
    '1000014': 'ALERT',
    '1000015': 'CLOCK'
  },
  'udp.dstport': {
    '7': 'echo',
    '9': 'discard',
    '13': 'daytime',
    '17': 'qotd',
    '19': 'chargen',
    '37': 'time',
    '39': 'rlp',
    '42': 'nameserver',
    '53': 'domain',
    '67': 'bootps',
    '68': 'bootpc',
    '69': 'tftp',
    '88': 'kerberos',
    '111': 'sunrpc',
    '123': 'ntp',
    '135': 'epmap',
    '137': 'netbios-ns',
    '138': 'netbios-dgm',
    '161': 'snmp',
    '162': 'snmptrap',
    '213': 'ipx',
    '443': 'https',
    '445': 'cifs',
    '464': 'kpasswd',
    '500': 'isakmp',
    '512': 'biff',
    '513': 'who',
    '514': 'syslog',
    '517': 'talk',
    '518': 'ntalk',
    '525': 'timed',
    '533': 'netwall',
    '550': 'new-rwho',
    '560': 'rmonitor',
    '561': 'monitor',
    '749': 'kerberos-adm',
    '1167': 'phone',
    '1433': 'ms-sql-s',
    '1434': 'ms-sql-m',
    '1512': 'wins',
    '1701': 'l2tp',
    '1812': 'radiusauth',
    '1813': 'radacct',
    '2049': 'nfsd',
    '2504': 'nlbs'
  },
  'tcp.dstport': {
    '7': 'echo',
    '9': 'discard',
    '13': 'daytime',
    '17': 'qotd',
    '19': 'chargen',
    '20': 'ftp-data',
    '21': 'ftp',
    '22': 'ssh',
    '23': 'telnet',
    '25': 'smtp',
    '37': 'time',
    '42': 'nameserver',
    '43': 'nicname',
    '53': 'domain',
    '70': 'gopher',
    '79': 'finger',
    '80': 'http',
    '88': 'kerberos',
    '101': 'hostname',
    '102': 'iso-tsap',
    '107': 'rtelnet',
    '109': 'pop2',
    '110': 'pop3',
    '111': 'sunrpc',
    '113': 'auth',
    '117': 'uucp-path',
    '119': 'nntp',
    '135': 'epmap',
    '137': 'netbios-ns',
    '139': 'netbios-ssn',
    '143': 'imap',
    '158': 'pcmail-srv',
    '170': 'print-srv',
    '179': 'bgp',
    '194': 'irc',
    '389': 'ldap',
    '443': 'https',
    '445': 'cifs',
    '464': 'kpasswd',
    '512': 'exec',
    '513': 'login',
    '514': 'cmd',
    '515': 'printer',
    '520': 'efs',
    '526': 'tempo',
    '530': 'courier',
    '531': 'conference',
    '532': 'netnews',
    '540': 'uucp',
    '543': 'klogin',
    '544': 'kshell',
    '556': 'remotefs',
    '636': 'ldaps',
    '749': 'kerberos-adm',
    '993': 'imaps',
    '995': 'pop3s',
    '1109': 'kpop',
    '1122': 'msn im',
    '1352': 'notes',
    '1433': 'ms-sql-s',
    '1434': 'ms-sql-m',
    '1512': 'wins',
    '1521': 'tns',
    '1524': 'ingreslock',
    '1533': 'sametime',
    '1718': 'h323',
    '1720': 'rtp',
    '1723': 'pptp',
    '1863': 'msn im',
    '2049': 'nfs',
    '2053': 'knetd',
    '3389': 'rdp',
    '5050': 'yahoo im',
    '5060': 'sip',
    '5190': 'aim',
    '6346': 'gnuetella',
    '6667': 'irc',
    '9001': 'tor',
    '9030': 'tor',
    '9535': 'man'
  },
  'medium': {
    '1': 'Ethernet',
    '2': 'Tokenring',
    '3': 'FDDI',
    '4': 'HDLC',
    '5': 'NetWitness',
    '6': '802.11',
    '7': '802.11 Radio',
    '8': '802.11 AVS',
    '9': '802.11 PPI',
    '10': '802.11 PRISM',
    '11': '802.11 Management',
    '12': '802.11 Control',
    '13': 'DLT Raw',
    '32': 'Logs',
    '33': 'Correlation'
  },
  'eth.type': {
    '0': '802.3',
    '257': 'Experimental',
    '512': 'Xerox PUP',
    '513': 'Xerox PUP',
    '1024': 'Nixdorf',
    '1536': 'Xerox NS IDP',
    '1537': 'XNS Address Translation (3Mb only)',
    '2048': 'IP',
    '2049': 'X.75 Internet',
    '2050': 'NBS Internet',
    '2051': 'ECMA Internet',
    '2052': 'CHAOSnet',
    '2053': 'X.25 Level 3',
    '2054': 'ARP',
    '2055': 'XNS Compatibility',
    '2056': 'Frame Relay ARP',
    '2076': 'Symbolics Private',
    '2184': 'Xyplex',
    '2304': 'Ungermann-Bass network debugger',
    '2560': 'Xerox IEEE802.3 PUP',
    '2561': 'Xerox IEEE802.3 PUP Address Translation',
    '2989': 'Banyan Systems',
    '2991': 'Banyon VINES Echo',
    '4096': 'Berkeley Trailer negotiation',
    '4097': 'Berkeley Trailer encapsulation for IP',
    '4660': 'DCA - Multicast',
    '5632': 'VALID system protocol',
    '6537': 'Artificial Horizons',
    '6549': 'Datapoint Corporation (RCL lan protocol)',
    '15360': '3Com NBP virtual circuit datagram (like XNS SPP) not registered',
    '15361': '3Com NBP System control datagram not registered',
    '15362': '3Com NBP Connect request (virtual cct) not registered',
    '15363': '3Com NBP Connect repsonse not registered',
    '15364': '3Com NBP Connect complete not registered',
    '15365': '3Com NBP Close request (virtual cct) not registered',
    '15366': '3Com NBP Close response not registered',
    '15367': '3Com NBP Datagram (like XNS IDP) not registered',
    '15368': '3Com NBP Datagram broadcast not registered',
    '15369': '3Com NBP Claim NetBIOS name not registered',
    '15370': '3Com NBP Delete Netbios name not registered',
    '15371': '3Com NBP Remote adaptor status request not registered',
    '15372': '3Com NBP Remote adaptor response not registered',
    '15373': '3Com NBP Reset not registered',
    '16962': 'IEEE bridge spanning protocol',
    '16972': 'Information Modes Little Big LAN diagnostic',
    '17185': 'THD - Diddle',
    '19522': 'Information Modes Little Big LAN',
    '21000': 'BBN Simnet Private',
    '24576': 'DEC unassigned',
    '24577': 'DEC Maintenance Operation Protocol (MOP) Dump/Load Assistance',
    '24578': 'DEC Maintenance Operation Protocol (MOP) Remote Console',
    '24579': 'DECNET Phase IV',
    '24580': 'DEC Local Area Transport (LAT)',
    '24581': 'DEC diagnostic protocol (at interface initialization?)',
    '24582': 'DEC customer protocol',
    '24583': 'DEC Local Area VAX Cluster (LAVC)',
    '24584': 'DEC AMBER',
    '24585': 'DEC MUMPS',
    '24592': '3Com Corporation',
    '25944': 'Bridged Ethernet/802.3 packet',
    '28672': 'Ungermann-Bass download',
    '28673': 'Ungermann-Bass NIUs',
    '28674': 'Ungermann-Bass diagnostic/loopback',
    '28675': 'Ungermann-Bass ??? (NMC to/from UB Bridge)',
    '28677': 'Ungermann-Bass Bridge Spanning Tree',
    '28679': 'OS/9 Microware',
    '28681': 'OS/9 Net?',
    '28704': 'LRT (England) (now Sintrom)',
    '28720': 'Racal-Interlan',
    '28721': 'Prime NTS (Network Terminal Service)',
    '28724': 'Cabletron',
    '32771': 'Cronus VLN',
    '32772': 'Cronus Direct',
    '32773': 'HP Probe protocol',
    '32774': 'Nestar',
    '32776': 'AT&T/Stanford Univ.',
    '32784': 'Excelan',
    '32787': 'Silicon Graphics diagnostic',
    '32788': 'Silicon Graphics network games',
    '32789': 'Silicon Graphics reserved',
    '32790': 'Silicon Graphics XNS NameServer',
    '32793': 'Apollo DOMAIN',
    '32814': 'Tymshare',
    '32815': 'Tigan',
    '32821': 'Reverse Address Resolution Protocol (RARP)',
    '32822': 'Aeonic Systems',
    '32823': 'IPX (Novell Netware?)',
    '32824': 'DEC LanBridge Management',
    '32825': 'DEC DSM/DDP',
    '32826': 'DEC Argonaut Console',
    '32827': 'DEC VAXELN',
    '32828': 'DEC DNS Naming Service',
    '32829': 'DEC Ethernet CSMA/CD Encryption Protocol',
    '32830': 'DEC Distributed Time Service',
    '32831': 'DEC LAN Traffic Monitor Protocol',
    '32832': 'DEC PATHWORKS DECnet NETBIOS Emulation',
    '32833': 'DEC Local Area System Transport',
    '32834': 'DEC unassigned',
    '32836': 'Planning Research Corp.',
    '32838': 'AT&T',
    '32839': 'AT&T',
    '32840': 'DEC Availability Manager for Distributed Systems DECamds',
    '32841': 'ExperData',
    '32859': 'VMTP',
    '32860': 'Stanford V Kernel',
    '32861': 'Evans & Sutherland',
    '32864': 'Little Machines',
    '32866': 'Counterpoint Computers',
    '32869': 'University of Mass. at Amherst',
    '32870': 'University of Mass. at Amherst',
    '32871': 'Veeco Integrated Automation',
    '32872': 'General Dynamics',
    '32873': 'AT&T',
    '32874': 'Autophon',
    '32876': 'ComDesign',
    '32877': 'Compugraphic Corporation',
    '32878': 'Landmark Graphics Corporation',
    '32890': 'Matra',
    '32891': 'Dansk Data Elektronik',
    '32892': 'Merit Internodal',
    '32893': 'Vitalink Communications',
    '32896': 'Vitalink TransLAN III Management',
    '32897': 'Counterpoint Computers',
    '32904': 'Xyplex',
    '32923': 'EtherTalk - AppleTalk over Ethernet',
    '32924': 'Datability',
    '32927': 'Spider Systems Ltd.',
    '32931': 'Nixdorf Computers',
    '32932': 'Siemens Gammasonics Inc.',
    '32960': 'DCA Data Exchange Cluster',
    '32966': 'Pacer Software',
    '32967': 'Applitek Corporation',
    '32968': 'Intergraph Corporation',
    '32973': 'Harris Corporation',
    '32975': 'Taylor Instrument',
    '32979': 'Rosemount Corporation',
    '32981': 'IBM SNA Services over Ethernet',
    '32989': 'Varian Associates',
    '32990': 'TRFS (Integrated Solutions Transparent Remote File System)',
    '32992': 'Allen-Bradley',
    '32996': 'Datability',
    '33010': 'Retix',
    '33011': 'AppleTalk Address Resolution Protocol (AARP)',
    '33012': 'Kinetics',
    '33015': 'Apollo Computer',
    '33023': 'Wellfleet Communications',
    '33026': 'Wellfleet BOFL',
    '33027': 'Wellfleet Communications',
    '33031': 'Symbolics Private',
    '33067': 'Talaris',
    '33072': 'Waterloo Microsystems Inc.',
    '33073': 'VG Laboratory Systems',
    '33079': 'IPX',
    '33080': 'Novell Inc',
    '33081': 'KTI',
    '33087': 'M/MUMPS data sharing',
    '33093': 'Vrije Universiteit (NL)',
    '33094': 'Vrije Universiteit (NL)',
    '33095': 'Vrije Universiteit (NL)',
    '33100': 'SNMP',
    '33103': 'Technically Elite Concepts',
    '33149': 'XTP',
    '33169': 'PowerLAN',
    '33238': 'Artisoft Lantastic',
    '33239': 'Artisoft Lantastic',
    '33283': 'QNX Software Systems Ltd.',
    '33680': 'Accton Technologies (unregistered)',
    '34091': 'Talaris multicast',
    '34178': 'Kalpana',
    '34525': 'IPv6',
    '34617': 'Control Technology Inc.',
    '34618': 'Control Technology Inc.',
    '34619': 'Control Technology Inc.',
    '34620': 'Control Technology Inc.',
    '34848': 'Hitachi Cable (Optoelectronic Systems Laboratory)',
    '34902': 'Axis Communications AB',
    '34915': 'PPPoe',
    '34916': 'PPPoe',
    '34952': 'HP LanProbe test?',
    '36864': 'Loopback (Configuration Test Protocol)',
    '36865': '3Com XNS Systems Management',
    '36866': '3Com TCP/IP Systems Management',
    '36867': '3Com loopback detection',
    '43690': 'DECNET',
    '64245': 'Sonix Arpeggio',
    '65278': 'ISO CLNP/ISO ES-IS DSAP/SSAP',
    '65280': 'BBN VITAL-LanBridge cache wakeups'
  },
  'relation.type': {
    '0': 'Begin',
    '1': 'End'
  }
};