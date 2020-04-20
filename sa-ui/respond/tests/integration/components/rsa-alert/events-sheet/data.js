const investigateEvent = [
  {
    'analysis_file': '',
    'analysis_service': 'ssl over non-standard port',
    'analysis_session': 'ratio medium transmitted',
    'data': [
      {
        'filename': '',
        'hash': '',
        'size': 4175
      }
    ],
    'description': '',
    'destination': {
      'device': {
        'asset_type': '',
        'business_unit': '',
        'compliance_rating': '',
        'criticality': '',
        'facility': '',
        'geolocation': {
          'city': '',
          'country': '',
          'domain': '',
          'latitude': null,
          'longitude': null,
          'organization': ''
        },
        'ip_address': '10.4.61.44',
        'mac_address': '00:50:56:33:18:15',
        'netbios_name': '',
        'port': 5671
      },
      'user': {
        'ad_domain': '',
        'ad_username': '',
        'email_address': '',
        'username': ''
      }
    },
    'detected_by': '',
    'detector': {
      'device_class': '',
      'ip_address': '',
      'product_name': ''
    },
    'domain': '',
    'domain_dst': '',
    'domain_src': '',
    'enrichment': '',
    'event_source': '10.4.61.33:56005',
    'event_source_id': '150',
    'file': '',
    'from': '10.4.61.97:36749',
    'host_dst': '',
    'host_src': '',
    'related_links': [
      {
        'type': 'investigate_original_event',
        'url': '/investigation/host/10.4.61.36:56005/navigate/event/AUTO/217948'
      }
    ],
    'size': 4175,
    'source': {
      'device': {
        'asset_type': '',
        'business_unit': '',
        'compliance_rating': '',
        'criticality': '',
        'facility': '',
        'geolocation': {
          'city': '',
          'country': '',
          'domain': '',
          'latitude': null,
          'longitude': null,
          'organization': ''
        },
        'ip_address': '10.4.61.97',
        'mac_address': '00:50:56:33:18:18',
        'netbios_name': '',
        'port': 36749
      },
      'user': {
        'ad_domain': '',
        'ad_username': '',
        'email_address': '',
        'username': ''
      }
    },
    'timestamp': 1534426889000,
    'to': '10.4.61.44:5671',
    'type': 'Network',
    'user': '',
    'user_dst': '',
    'user_src': '',
    'username': ''
  }
];

const malwareEvent = [
  {
    'communityScore': 100.0,
    'data': [
      {
        'av_aliases': {
          'AVG': 'IRC/BackDoor.Flood',
          'AegisLab': '',
          'Agnitum': '',
          'Ahnlab': '',
          'Antiy': '',
          'Avira': 'TR/Patched.Ren.Gen',
          'BitDefender': '',
          'ByteHero': '',
          'ClamWin': '',
          'Commtouch': 'IRC/Zapchast.AK',
          'ESET': '',
          'Emsisoft': '',
          'F-prot': '',
          'F-secure': '',
          'Filseclab': '',
          'Fortinet': 'W32/Inject.8A2F!tr',
          'GFI': 'Trojan.Win32.Generic!BT',
          'Hauri': '',
          'Ikarus': 'Trojan.IRC.Zapchast',
          'Jiangmin': '',
          'K7': '',
          'Kaspersky': '',
          'Kingsoft': '',
          'Lavasoft': 'Trojan.Script.39573',
          'McAfee-Gateway': 'Artemis!7D708F247CC6',
          'Microsoft': '',
          'NANO': 'Trojan.Script.Zapchast.yexdu',
          'Norman': 'winpe/Script_Gen.GR',
          'QuickHeal': '',
          'SUPERAntiSpyware': '',
          'Sophos': '',
          'Symantec': '',
          'TotalDefense': '',
          'TrendMicro': 'Mal_Zap',
          'TrendMicroHouseCall': 'Mal_Zap',
          'VirIT': '',
          'VirusBlokAda': '',
          'Zillya!': '',
          'Zoner': '',
          'nProtect': ''
        },
        'av_hit': null,
        'communityScore': 100.0,
        'extension': 'exe',
        'filename': 'card.gif.exe',
        'hash': '7d708f247cc6a7364b873c029bbdf459',
        'mime_type': 'application/x-dosexec',
        'original_path': '/var/lib/rsamalware/spectrum/repository/files/7d/7d708f247cc6a7364b873c029bbdf459/card.gif.exe',
        'sandboxScore': 18.0,
        'size': 63302,
        'staticScore': 100.0
      }
    ],
    'description': 'Malware Found in Uploaded File(Miss secondary hit)',
    'destination': {
      'device': {
        'asset_type': '',
        'business_unit': '',
        'compliance_rating': '',
        'criticality': '',
        'facility': '',
        'geolocation': {
          'city': '',
          'country': 'Unavailable',
          'domain': '',
          'latitude': null,
          'longitude': null,
          'organization': ''
        },
        'ip_address': 'qaspectrum2.netwitness.local',
        'mac_address': '',
        'netbios_name': '',
        'port': ''
      },
      'user': {
        'ad_domain': '',
        'ad_username': '',
        'email_address': '',
        'username': ''
      }
    },
    'detected_by': '10.25.51.142',
    'detector': {
      'ip_address': '10.25.51.142'
    },
    'event_source': '',
    'event_source_id': '0',
    'file': [
      'card.gif.exe'
    ],
    'networkScore': 0.0,
    'related_links': [
      {
        'type': 'investigate_malware',
        'url': '/investigation/undefined/malware/event/3328608'
      }
    ],
    'sandboxScore': 18.0,
    'size': [
      63302
    ],
    'source': {
      'device': {
        'asset_type': '',
        'business_unit': '',
        'compliance_rating': '',
        'criticality': '',
        'facility': '',
        'geolocation': {
          'city': '',
          'country': '',
          'domain': '',
          'latitude': null,
          'longitude': null,
          'organization': ''
        },
        'ip_address': '10.25.51.142',
        'mac_address': '',
        'netbios_name': '',
        'port': ''
      },
      'user': {
        'ad_domain': '',
        'ad_username': '',
        'email_address': '',
        'username': ''
      }
    },
    'staticScore': 100.0,
    'timestamp': 1407801720000,
    'type': 'Resubmit'
  }
];

export {
  investigateEvent,
  malwareEvent
};
