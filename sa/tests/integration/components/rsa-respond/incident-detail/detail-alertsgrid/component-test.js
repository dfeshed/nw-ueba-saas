import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-respond/incident-detail/detail-alertsgrid', 'Integration | Component | rsa respond/incident detail/detail alertsgrid', {
  integration: true
});

test('it renders', function(assert) {
  let model = [{
    'id': '56932c61e4b0e179f1af6556',
    'incidentCreated': 1452485774539,
    'incidentId': 'INC-490',
    'status': 'GROUPED_IN_INCIDENT',
    'receivedTime': { '$date': 1452485729741 },
    'alert': {
      'name': 'Suspected C&C',
      'source': ['Event Stream Analysis'],
      'risk_score': 20,
      'severity': 50,
      'events': [{
        'related_links': [{
          'type': 'investigate_original_event','url': '/investigation/host/10.101.217.121: 56005/navigate/event/AUTO/21053778' },
          { 'type': 'investigate_destination_domain','url': '/investigation/10.101.217.121: 56005/navigate/query/alias.host%3D%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A14%3A22.000Z' }
        ],
        'data': [{ 'filename': 'cotlow_awards.cfm','size': 23704 }],
        'description': '',
        'file': 'cotlow_awards.cfm',
        'size': 23704,
        'detected_by': '',
        'domain': 'www.media.gwu.edu',
        'from': '66.249.67.67: 35444',
        'to': '161.253.149.52: 80',
        'timestamp': '2016-01-11T04: 14: 22.000Z'
      }],

      'groupby_domain': 'www.media.gwu.edu',
      'groupby_destination_ip': '161.253.149.52',
      'groupby_source_username': '',
      'groupby_source_ip': '66.249.67.67',
      'groupby_filename': 'cotlow_awards.cfm' }
  }];

  this.set('model', model);
  this.render(hbs`{{rsa-respond/incident-detail/detail-alertsgrid model=model}}`);

  assert.equal(this.$('.rsa-respond-detail-grid').length, 1, 'Testing alerts table element exists');
  assert.equal(this.$('.rsa-data-table-header-cell').length, 7, 'Testing count of alerts header cells');
  assert.equal(this.$('.rsa-riskscore').length, 1, 'Testing risk score is rendered');
  assert.equal(this.$('.rsa-createddate').length, 1, 'Testing rsa-createddate is rendered');
  assert.equal(this.$('.rsa-alerts-events').length, 1, 'Testing alerts-events is rendered');
  assert.equal(this.$('.rsa-alerts-host').length, 1, 'Testing alerts-host is rendered');
  assert.equal(this.$('.rsa-domain').length, 1, 'Testing rsa-domain is rendered');
  assert.equal(this.$('.rsa-alert-source').length, 1, 'Testing alert-source is rendered');

});
