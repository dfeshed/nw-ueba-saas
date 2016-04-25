data_source_to_score_tables = {
    'vpn': 'vpndatares',
    'vpn_session': 'vpnsessiondatares',
    'crmsf': 'crmsfscore',
    'wame': 'wamescore',
    'gwame': 'gwamescore',
    'kerberos_logins': 'authenticationscores',
    'kerberos_tgt': 'kerberostgtscore',
    'ntlm': 'ntlmscore',
    'oracle': 'oraclecore',
    'prnlog': 'prnlogscore',
    'ssh': 'sshscores'
}

data_source_to_enriched_tables = {
    'vpn': 'vpnenriched',
    'vpn_session': 'vpnenriched',
    'crmsf': 'crmsfenriched',
    'wame': 'wameenriched',
    'gwame': 'gwameenriched',
    'kerberos_logins': '4769enriched',
    'kerberos_tgt': '4769enriched',
    'ntlm': 'ntlmenriched',
    'oracle': 'oracleenriched',
    'prnlog': 'prnlogenriched',
    'ssh': 'sshenriched'
}
