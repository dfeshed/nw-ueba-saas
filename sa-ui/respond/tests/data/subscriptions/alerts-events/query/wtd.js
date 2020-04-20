export default [].concat(
  [
    {
      'description': 'Retail Wire Over 3000',
      'details': 'Retail wire amount is 150,000',
      'from': '10.1.1.198',
      'id': 198,
      'name': 'Retail Wire Over 3000',
      'related_links': [
        {
          'type': 'View Original Event (in WTD)',
          'url': 'https://test-bhasker.silvertailsystems.com/#incidentDetails?incident=198'
        }
      ],
      'rule': 'retail_wire_over_3000',
      'rulecomment': 'Triggered when retail wire exceeds $3000',
      'score': 0.0,
      'source': {
        'device': {
          'ip_address': '10.1.1.198'
        },
        'user': {
          'username': 'John Doe1'
        }
      },
      'tenant': 'tenant1',
      'timestamp': 1434571084000,
      'type': 'Web Threat Detection Incident',
      'user': 'John Doe1'
    }
  ]
);
