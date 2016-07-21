import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-incident-tile', 'Integration | Component | rsa incident tile', {
  integration: true
});

test('The tile component is rendered properly.', function(assert) {

  let testInc = EmberObject.create({
      'id': 'INC-490',
      'name': 'Suspected command and control communication with www.media.gwu.edu',
      'riskScore': 96,'prioritySort': 0,
      'statusSort': 1,
      'created': 1452485774539,
      'assignee': {
        'id': '1'
      },
      'createdBy': 'Suspected Command & Control Communication By Domain',
      'alertCount': 1,
      'categories': [],'sources': ['Event Stream Analysis'],'lastUpdated': 1452485774539,
      'ruleId': '5681b379e4b0947bc54e6c9d',
      'summary': 'SA detected communications with www.media.gwu.edu that may be malware command and control.\n\n1. Evaluate if the domain is legitimate (online radio, news feed, partner, automated testing, etc.).\n2. Review domain registration for suspect information (Registrant country, registrar, no registration data found, etc).\n3. If the domain is suspect, go to the Investigations module to locate other activity to/from it.',
      'alerts': [
        {
          'id': '56932c61e4b0e179f1af6556',
          'incidentCreated': 1452485774539,
          'incidentId': 'INC-490',
          'status': 'GROUPED_IN_INCIDENT',
          'receivedTime': { '$date': 1452485729741 },
          'alert': {
            'name': 'Suspected C&C',
            'risk_score': 20,
            'severity': 50,
            'events': [{
              'related_links': [{
                'type': 'investigate_original_event','url': '/investigation/host/10.101.217.121: 56005/navigate/event/AUTO/21053778' },
                { 'type': 'investigate_destination_domain','url': '/investigation/10.101.217.121: 56005/navigate/query/alias.host%3D%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A14%3A22.000Z' }
              ],
              'data': [{ 'filename': 'cotlow_awards.cfm','size': 23704 }],
              'destination': {
                'device': {
                  'compliance_rating': '',
                  'netbios_name': '',
                  'port': 80,
                  'mac_address': '00: 13: c3: 3b: be: 00',
                  'criticality': '',
                  'asset_type': '',
                  'ip_address': '161.253.149.52',
                  'facility': '',
                  'business_unit': '',
                  'geolocation': { 'country': 'United States','city': 'Washington','latitude': 38.93759918212891,'organization': 'The George Washington University','domain': 'gwu.edu','longitude': -77.0927963256836 }
                },
                'user': {
                  'email_address': '',
                  'ad_username': '',
                  'ad_domain': '',
                  'username': '' }
              },
              'description': '',
              'source': {
                'device': {
                  'compliance_rating': '',
                  'netbios_name': '',
                  'port': 35444,
                  'mac_address': '00: 13: c3: 3b: c7: 00',
                  'criticality': '','asset_type': '','ip_address': '66.249.67.67',
                  'facility': '','business_unit': '',
                  'geolocation': { 'country': 'United States','city': 'Mountain View','latitude': 37.4192008972168,'organization': 'Googlebot','domain': 'googlebot.com','longitude': -122.0574035644531 }
                },
                'user': { 'email_address': '','ad_username': '','ad_domain': '','username': '' }
              },
              'type': 'Network',

              'enrichment': {
                'command_control': {
                  'weighted_domain_ua_ratio_score': 4,
                  'weighted_domain_referer_ratio_score': 4,
                  'weighted_domain_referer_score': 2,
                  'confidence': 40,
                  'weighted_whois_age_score': 0,
                  'weighted_whois_validity_score': 0,
                  'aggregate': 100
                },
                'ctxhub': { 'domain_is_whitelisted': false },
                'whois': {
                  'estimated_domain_validity_days': 2601,
                  'expires_date': '19-jan-2023',
                  'registrant_country': 'US',
                  'registrar_name': 'MARKMONITOR INC.',
                  'is_cached': true,
                  'registrant_email': 'domainadmin@yahoo-inc.com',
                  'source': 'DATABASE',
                  'age_score': 0,
                  'scaled_validity': 10,
                  'domain_name': 'yahoo.com',
                  'scaled_age': 10,
                  'registrant_state': 'CA',
                  'estimated_domain_age_days': 4741,
                  'registrant_name': 'Domain Administrator',
                  'registrant_organization': 'Yahoo! Inc.',
                  'registrant_postal_code': '94089',
                  'registrant_street1': '701 First Avenue',
                  'registrant_telephone': '1.4083493300',
                  'created_date': '18-jan-1995',
                  'updated_date': '06-sep-2013',
                  'registrant_city': 'Sunnyvale',
                  'validity_score': 0
                },
                'normalized': {
                  'full_domain': 'www.media.gwu.edu',
                  'domain': 'gwu.edu',
                  'srcip_domain': '66.249.67.67_gwu.edu',
                  'user_agent': 'Mozilla/5.0',
                  'timestamp': 1452485662000
                },
                'domain': {
                  'ua_ratio_score': 100,
                  'referer_num_events': 191,
                  'ua_cardinality': 1,
                  'referer_ratio': 100,
                  'referer_cardinality': 1,
                  'referer_conditional_cardinality': 1,
                  'ua_num_events': 191,
                  'ua_score': 100,
                  'referer_ratio_score': 100,
                  'referer_score': 100,
                  'ua_conditional_cardinality': 1,
                  'ua_ratio': 100
                },
                'beaconing': {
                  'beaconing_score': 89.35304630354096,
                  'beaconing_period': 60950
                },
                'new_domain': {
                  'age_num_events': 28,
                  'age_age': 528000,
                  'age_score': 99.39075237491708
                },
                'httpEventEnrichedRule': { 'flow_name': 'C2' },
                'user_agent': { 'rare_num_events': 30,'rare_score': 27.25317930340126,'rare_cardinality': 27 },
                'smooth': { 'smooth_beaconing_score': 97.42054727927368 }
              },
              'file': 'cotlow_awards.cfm',
              'size': 23704,
              'detected_by': '',
              'domain': 'www.media.gwu.edu',
              'from': '66.249.67.67: 35444',
              'to': '161.253.149.52: 80',
              'detector': {
                'device_class': '',
                'ip_address': '',
                'product_name': ''
              },
              'user': '',
              'timestamp': '2016-01-11T04: 14: 22.000Z'
            }],
            'destination_country': ['United States'],
            'source_country': ['United States'],
            'source': 'Event Stream Analysis',
            'signature_id': 'Suspected C&C',
            'timestamp': 1452485729000,
            'type': ['Network'],
            'numEvents': 1,
            'related_links': [
              { 'type': 'investigate_session','url': '/investigation/10.101.217.121: 56005/navigate/query/sessionid%3D21053778' },
              { 'type': 'investigate_src_ip','url': '/investigation/10.101.217.121: 56005/navigate/query/ip.src%3D66.249.67.67%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A24%3A22.000Z' },
              { 'type': 'investigate_dst_ip','url': '/investigation/10.101.217.121: 56005/navigate/query/ip.dst%3D161.253.149.52%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A24%3A22.000Z' },
              { 'type': 'investigate_destination_domain','url': '/investigation/10.101.217.121: 56005/navigate/query/alias.host%3D%2Fdate%2F2016-01-11T04%3A04%3A22.000Z%2F2016-01-11T04%3A24%3A22.000Z' }
            ],
            'groupby_domain': 'www.media.gwu.edu',
            'groupby_destination_ip': '161.253.149.52',
            'groupby_source_username': '',
            'groupby_source_ip': '66.249.67.67',
            'groupby_filename': 'cotlow_awards.cfm' } }]
    }),
    users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', friendlyName: 'user1',  email: 'user1@rsa.com' }),
      EmberObject.create({ id: 2, firstName: 'User 2', lastName: 'LastName 2', friendlyName: 'user2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: 3, firstName: 'User 3', lastName: 'LastName 3', friendlyName: 'user3', email: 'user3@rsa.com' }) ];

  this.set('testInc', testInc);
  this.set('users', users);

  this.render(hbs`{{rsa-incident-tile model=testInc users=users}}`);

  assert.equal(this.$('.rsa-update-indicator__dot').length, 1, 'Testing to see if the update indicator element exists.');
  assert.equal(this.$('.rsa-incident-tile').length, 1, 'Testing to see if a rsa-incident-tile element exists.');
  assert.equal(this.$('.rsa-incident-tile-section').length, 1, 'Testing to see if a rsa-incident-tile-section element exists.');
  assert.equal(this.$('.rsa-incident-tile-id').length, 1, 'Testing to see if a rsa-incident-id elements exist.');
  assert.ok(this.$('.rsa-incident-tile-header').length, 'Incident tile header not found in DOM');
  assert.ok((this.$('.rsa-incident-tile-score').text().indexOf(testInc.riskScore) >= 0), 'Unexpected incident risk score');
  assert.equal(this.$('.rsa-incident-tile-id').text().trim(), testInc.id, 'Unexpected incident id in the tile');
  assert.ok(this.$('.rsa-incident-tile-name').length, 'Incident tile name not found in DOM');
  assert.ok(this.$('.rsa-incident-tile-name').text().indexOf('Suspected command and control communication with www.media.gwu.edu') >= 0, 'Unexpected name value');
  assert.ok(this.$('.rsa-incident-tile-created-date').length, 'Incident tile created date not found in DOM');
  assert.ok(this.$('.rsa-incident-tile-status-selector').length, 'Incident tile status not found in DOM');
  assert.ok(this.$('.rsa-incident-tile-status').text().indexOf('Assigned') >= 0, 'Unexpected assigned value');
  assert.ok(this.$('.rsa-incident-tile-priority-selector').length, 'Incident tile priority not found in DOM');
  assert.ok((this.$('.rsa-incident-tile-priority-selector').text().indexOf('Low') >= 0), 'Unexpected incident severity');
  assert.ok(this.$('.rsa-incident-tile-assignee-selector').length, 'Incident tile assignee not found in DOM');
  assert.ok((this.$('.rsa-incident-tile-assignee-selector').text().indexOf('User 1') >= 0), 'Unexpected Assignee value');
  assert.equal(this.$('.rsa-incident-tile-alert-count').length, 1, 'Incident tile alert count not found in DOM');
  assert.equal(this.$('.rsa-incident-tile-alert-count').text().trim(), '1', 'Unexpected alert count value');
  assert.equal(this.$('.rsa-incident-tile-event-count').length, 1, 'Incident tile Event count not found in DOM');
  assert.equal(this.$('.rsa-incident-tile-event-count').text().trim(), '1', 'Unexpected Event count value');
  assert.equal(this.$('.rsa-incident-tile-sources').length, 1, 'Incident tile sources not found in DOM');
  assert.equal(this.$('.rsa-incident-tile-sources').text().trim(), ['ESA'], 'Unexpected alert count value');
  assert.equal(this.$('.rsa-incident-tile-score .label').text().trim(), 'Risk Score', 'Incident tile does include risk score label in DOM');

  this.render(hbs`{{rsa-incident-tile model=testInc users=users size='small'}}`);
  assert.equal(this.$('.rsa-update-indicator__dot').length, 1, 'Testing to see if the update indicator element exists.');
  assert.equal(this.$('.rsa-incident-tile-status-selector').length, 0, 'Small incident tile does not include status selector in DOM');
  assert.equal(this.$('.rsa-incident-tile-priority-selector').length, 0, 'Small incident tile does not include priority selector in DOM');
  assert.equal(this.$('.rsa-incident-tile-assignee-selector').length, 0, 'Small incident tile does not include assignee selector in DOM');
  assert.equal(this.$('.rsa-incident-tile-score .score').text().trim(), testInc.riskScore, 'Small incident tile does include risk score label in DOM');
  assert.equal(this.$('.rsa-incident-tile-id').text().trim(), testInc.id, 'Small incident tile contains id in the tile');
  assert.equal(this.$('.rsa-incident-tile-name').length, 1, 'Small incident tile name not found in DOM');
  assert.equal(this.$('.rsa-incident-tile-score .label').text().trim().length, 0, 'Small incident tile does not include risk score label in DOM');
  assert.equal(this.$('.rsa-incident-tile-alert-count').length, 1, 'Small Incident tile alert count is present in DOM');
  assert.equal(this.$('.rsa-incident-tile-event-count').length, 1, 'Small incident tile Event count is present in DOM');
  assert.equal(this.$('.rsa-incident-tile-sources').length, 1, 'Small incident tile sources not found in DOM');
});

test('The tile component renders the proper contextual timestamp.', function(assert) {
  /*
   * The dates used below should match the following:
   * | STATUS  | DATE                | MILLISECONDS  |
   * | New     | 05/01/2016 10:25:15 | 1462123515000 |
   * | Updated | 05/20/2016 11:23:15 | 1463768595000 |
   */
  let mockIncident = EmberObject.create({
    'id': 'INC-490',
    'name': 'Suspected command and control communication with www.media.gwu.edu',
    'riskScore': 96,
    'prioritySort': 0,
    'statusSort': 0,
    'created': 1462123515000,
    'assignee': {
      'id': '1'
    },
    'createdBy': 'Suspected Command & Control Communication By Domain',
    'alertCount': 1,
    'categories': [],
    'sources': ['Event Stream Analysis'],
    'ruleId': '5681b379e4b0947bc54e6c9d',
    'summary': 'SA detected communications with www.media.gwu.edu that may be malware command and control.\n\n1. Evaluate if the domain is legitimate (online radio, news feed, partner, automated testing, etc.).\n2. Review domain registration for suspect information (Registrant country, registrar, no registration data found, etc).\n3. If the domain is suspect, go to the Investigations module to locate other activity to/from it.'
  });

  this.set('mockIncident', mockIncident);
  this.render(hbs`{{rsa-incident-tile model=mockIncident}}`);
  assert.equal(this.$('.rsa-incident-tile-created-date').text().indexOf('created') !== -1, true, 'Testing whether or not a created date is shown.');

  this.set('mockIncident.lastUpdated', 1463768595000);
  this.set('mockIncident.statusSort', 2);
  assert.equal(this.$('.rsa-incident-tile-created-date').text().indexOf('updated') !== -1, true, 'Testing whether or not an updated date is shown.');

  this.set('mockIncident.statusSort', 4);
  assert.equal(this.$('.rsa-incident-tile-created-date').text().indexOf('updated') !== -1, true, 'Testing whether or not an updated date is shown for other statuses.');
  assert.equal(this.$('.rsa-incident-tile-status-selector option[selected="selected"]').text() !== 'Updated', true, 'Testing to ensure that the status is something other than Updated.');
});

test('Edit button stays visible after click and the mouse leaves the component', function(assert) {
  let incident = EmberObject.create({
      riskScore: 1,
      id: 'INC-491',
      createdBy: 'User X',
      created: '2015-10-10',
      statusSort: 0,
      prioritySort: 0,
      alertCount: 10,
      sources: ['Event Stream Analysis'],
      assignee: {
        id: '1'
      }
    }),
    users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: 2, firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: 3, firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-incident-tile model=incident users=users}}
  `);

  let container = this.$('.rsa-incident-tile');
  container.trigger('mouseenter');
  this.$('.rsa-edit-tool').trigger('click');
  container.trigger('mouseleave');

  assert.equal(this.$('.rsa-edit-tool.hide').length, 0, 'Edit button is present after click it');

});

test('Edit mode is disabled if starting to edit another tile', function(assert) {
  let incident = EmberObject.create({
      riskScore: 1,
      id: 'INC-491',
      createdBy: 'User X',
      created: '2015-10-10',
      statusSort: 0,
      prioritySort: 0,
      alertCount: 10,
      sources: ['Event Stream Analysis'],
      assignee: {
        id: '1'
      }
    }),
    users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: 2, firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: 3, firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-incident-tile id='tile1' model=incident users=users}}{{rsa-incident-tile id='tile2' model=incident users=users}}
  `);

  let tile1 = this.$('#tile1'),
    tile2 = this.$('#tile2');

  tile1.trigger('mouseenter');
  tile1.find('.rsa-edit-tool').trigger('click');
  tile1.trigger('mouseleave');

  tile2.trigger('mouseenter');
  tile2.find('.rsa-edit-tool').trigger('click');

  tile1.trigger('focusout');

  assert.equal(tile1.find('.rsa-edit-tool').css('visibility'), 'hidden', 'Tile 1 hides the edit button');
  assert.equal(tile2.find('.rsa-edit-tool').css('visibility'), 'visible', 'Tile 2 shows the edit button');

});

test('Clicking off a card in edit mode exits edit mode without saving any field changes', function(assert) {
  let preStatusValue = 0,
    newStatusValue = 1,
    incident = EmberObject.create({
      riskScore: 1,
      id: 'INC-491',
      createdBy: 'User X',
      created: '2015-10-10',
      statusSort: preStatusValue,
      prioritySort: 0,
      alertCount: 10,
      sources: ['Event Stream Analysis'],
      assignee: {
        id: '1'
      }
    }),
    users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' })];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-incident-tile model=incident users=users}} <div class='.other-component'/>
  `);

  let container = this.$('.rsa-incident-tile');
  container.trigger('mouseenter');

  this.$('.rsa-edit-tool').trigger('click');

  let tileStatusVal = container.find('.rsa-incident-tile-status-selector').val();
  assert.equal(incident.statusSort, tileStatusVal, 'Tile displays the current Incident status.');

  container.find('.rsa-incident-tile-status-selector').val(newStatusValue);
  assert.equal(incident.statusSort, preStatusValue, 'After updating the Select, the incident status has its prev value before saving the model');
  this.$('.other-component').trigger('click');

  assert.equal(incident.statusSort, preStatusValue, 'After exiting tile, new Incident status must retain the old value');
  assert.notEqual(incident.statusSort, newStatusValue, 'After exiting tile, new Incident status should not be saved');

});

test('Assignee field contains at least one option', function(assert) {
  let incident = EmberObject.create({
      riskScore: 1,
      id: 'INC-491',
      createdBy: 'User X',
      created: '2015-10-10',
      statusSort: 0,
      prioritySort: 0,
      alertCount: 10,
      sources: ['Event Stream Analysis'],
      assignee: {
        id: 1
      }
    }),
    users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' })];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-incident-tile model=incident users=users}}
  `);

  let container = this.$('.rsa-incident-tile');
  container.trigger('mouseenter');

  this.$('.rsa-edit-tool').trigger('click');

  let selectorOptionCount = container.find('.rsa-incident-tile-assignee-selector option').length;
  assert.notEqual(0, selectorOptionCount, 'Tile displays the current Incident status.');

});

test('Incident status changed after press save', function(assert) {
  let preStatusValue = 0,
    newStatusValue = '1',
    incident = EmberObject.create({
      riskScore: 1,
      id: 'INC-491',
      createdBy: 'User X',
      created: '2015-10-10',
      statusSort: preStatusValue,
      prioritySort: 0,
      alertCount: 10,
      sources: ['Event Stream Analysis'],
      assignee: {
        id: '1'
      }
    }),
    users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: 2, firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: 3, firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  // this.set({ incident: incident, users: users });
  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-incident-tile model=incident users=users}}
  `);

  let container = this.$('.rsa-incident-tile');
  container.trigger('mouseenter');

  this.$('.rsa-edit-tool').trigger('click');

  let tileStatusVal = container.find('.rsa-incident-tile-status-selector select').val();
  assert.equal(incident.statusSort, tileStatusVal, 'Tile displays the current Incident status.');

  container.find('.rsa-incident-tile-status-selector .prompt').click();
  container.find('.rsa-incident-tile-status-selector select').val(newStatusValue).trigger('change');
  assert.equal(incident.statusSort, preStatusValue, 'After updating the Select, the incident status has its prev value before saving the model');

  container.find('.rsa-edit-tool').trigger('click');
  assert.equal(incident.status, 'ASSIGNED', 'After clicking Save, Incident status has changed to its new value');

});

test('Incident priority changed after press save', function(assert) {
  let prePriorityValue = 0,
    newPriorityValue = 1,
    incident = EmberObject.create({
      riskScore: 1,
      id: 'INC-491',
      createdBy: 'User X',
      created: '2015-10-10',
      statusSort: 0,
      prioritySort: prePriorityValue,
      alertCount: 10,
      sources: ['Event Stream Analysis'],
      assignee: {
        id: '1'
      }
    }),
    users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: 2, firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: 3, firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-incident-tile model=incident users=users}}
  `);

  let container = this.$('.rsa-incident-tile');
  container.trigger('mouseenter');

  this.$('.rsa-edit-tool').trigger('click'); // switching to edit mode

  let tilePriorityVal = container.find('.rsa-incident-tile-priority-selector select').val();
  assert.equal(incident.prioritySort, tilePriorityVal, 'Tile displays the current Incident priority.');

  container.find('.rsa-incident-tile-priority-selector .prompt').click();
  container.find('.rsa-incident-tile-priority-selector select').val(newPriorityValue).trigger('change');
  assert.equal(incident.prioritySort, prePriorityValue, 'After updating the Select, the incident model priority has its prev value before saving the model');

  container.find('.rsa-edit-tool').trigger('click');
  assert.equal(incident.priority, 'MEDIUM', 'After clicking Save, Incident priority has changed to its new value');

});

test('Incident Assignee changed after press save', function(assert) {
  let assigneeIdOne = 1,
    assigneeIdTwo = 2,
    incident = EmberObject.create({
      riskScore: 1,
      id: 'INC-491',
      createdBy: 'User X',
      created: '2015-10-10',
      statusSort: 0,
      prioritySort: 0,
      alertCount: 10,
      sources: ['Event Stream Analysis'],
      assignee: {
        id: assigneeIdOne
      }
    }),
    users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: 2, firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: 3, firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`
    {{rsa-incident-tile model=incident users=users}}
  `);

  let container = this.$('.rsa-incident-tile');
  container.trigger('mouseenter');

  this.$('.rsa-edit-tool').trigger('click'); // switching to edit mode

  let tileAssigneeVal = container.find('.rsa-incident-tile-assignee-selector select').val();
  assert.equal(incident.assignee.id, tileAssigneeVal, 'Tile displays the current Incident assignee.');

  container.find('.rsa-incident-tile-assignee-selector .prompt').click();
  container.find('.rsa-incident-tile-assignee-selector select').val(assigneeIdTwo).trigger('change');
  assert.equal(incident.assignee.id, assigneeIdOne, 'After updating the Select, the incident model assignee has its prev value before saving the model');

  container.find('.rsa-edit-tool').trigger('click');
  assert.equal(incident.assignee.id, assigneeIdTwo, 'After clicking Save, Incident assignee has changed to its new value');

});

test('The update indicator component is rendered properly when an asynchronous update is available', function(assert) {

  let testInc = EmberObject.create({
      'id': 'INC-490',
      'name': 'Suspected command and control communication with www.media.gwu.edu',
      'riskScore': 96,'prioritySort': 0,
      'statusSort': 1,
      'created': 1452485774539,
      'assignee': { 'id': '1' },
      'asyncUpdate': true
    }),
    users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', friendlyName: 'user1',  email: 'user1@rsa.com' }) ];

  this.set('testInc', testInc);
  this.set('users', users);

  this.render(hbs`{{rsa-incident-tile model=testInc users=users}}`);

  assert.equal(this.$('.rsa-update-indicator.is-icon-only').length, 1, 'Testing to see if the update indicator element exists with the is-icon-only class.');
  assert.equal(this.$('.rsa-update-indicator.is-icon-only.is-hidden').length, 0, 'Testing to see if the update indicator element exists with the is-icon-only and is-hidden classes.');
});

test('The update indicator component is rendered properly when an asynchronous update is not available', function(assert) {

  let testInc = EmberObject.create({
      'id': 'INC-490',
      'name': 'Suspected command and control communication with www.media.gwu.edu',
      'riskScore': 96,'prioritySort': 0,
      'statusSort': 1,
      'created': 1452485774539,
      'assignee': { 'id': '1' }
    }),
    users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', friendlyName: 'user1',  email: 'user1@rsa.com' }) ];

  this.set('testInc', testInc);
  this.set('users', users);

  this.render(hbs`{{rsa-incident-tile model=testInc users=users}}`);

  assert.equal(this.$('.rsa-update-indicator.is-icon-only').length, 1, 'Testing to see if the update indicator element exists with the is-icon-only class.');
  assert.equal(this.$('.rsa-update-indicator.is-icon-only.is-hidden').length, 1, 'Testing to see if the update indicator element exists with the is-icon-only and is-hidden classes.');
});
