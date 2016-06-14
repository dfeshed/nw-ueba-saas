import os
import sys

data_source_to_score_tables = {
    'vpn': 'vpndatares',
    'vpn_session': 'vpnsessiondatares',
    'crmsf': 'crmsfscore',
    'wame': 'wamescore',
    'gwame': 'gwamescore',
    'kerberos': 'authenticationscores',
    'kerberos_tgt': 'kerberostgtscore',
    'ntlm': 'ntlmscore',
    'oracle': 'oraclescore',
    'prnlog': 'prnlogscore',
    'ssh': 'sshscores'
}

data_source_to_enriched_tables = {
    'vpn': 'vpnenriched',
    'vpn_session': 'vpnenriched',
    'crmsf': 'crmsfenriched',
    'wame': 'wameenriched',
    'gwame': 'gwameenriched',
    'kerberos': '4769enriched',
    'kerberos_tgt': 'logindata',
    'ntlm': 'ntlmenriched',
    'oracle': 'oracleenriched',
    'prnlog': 'prnlogenriched',
    'ssh': 'sshenriched'
}

if set(data_source_to_score_tables.iterkeys()) != set(data_source_to_enriched_tables.iterkeys()):
    print 'Tables should should contain the same data sources. Please update the script (' + \
          os.path.abspath(__file__) + ') and then run again.'
    sys.exit(1)
