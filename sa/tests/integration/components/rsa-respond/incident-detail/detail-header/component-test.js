import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import { incStatus } from 'sa/incident/constants';
import selectors from 'sa/tests/selectors';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-incident-detail-header', 'Integration | Component | rsa respond/rsa respond detail/detail header', {
  integration: true
});

test('The incident detail header component is rendered properly.', function(assert) {

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    name: 'Suspected command and control communication with www.mozilla.com',
    createdBy: 'User X',
    created: '2015-10-10',
    lastUpdated: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    eventCount: 2,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    },
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
  });
  let users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: 2, firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: 3, firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`{{rsa-respond/incident-detail/detail-header incident=incident users=users}}`);

  assert.equal(this.$('.rsa-incident-detail-header').length, 1, 'Testing rsa-incident-detail-header element exists');
  assert.equal(this.$('.rsa-incident-detail-header__id').length, 1, 'Testing rsa-incident-detail-header__id element exists');
  assert.equal(this.$('.rsa-incident-detail-header__name').length, 1, 'Testing rsa-incident-detail-header__name element exists');
  assert.equal(this.$('.rsa-incident-detail-header__priority').length, 1, 'Testing rsa-incident-detail-header__priority element exists');
  assert.equal(this.$('.rsa-incident-detail-header__alerts').length, 1, 'Testing rsa-incident-detail-header__alerts element exists');
  assert.equal(this.$('.rsa-incident-detail-header__events').length, 1, 'Testing rsa-incident-detail-header__events element exists');
  assert.equal(this.$('.rsa-incident-detail-header__sources').length, 1, 'Testing rsa-incident-detail-header__sources element exists');
  assert.equal(this.$('.rsa-incident-detail-header__assignee').length, 1, 'Testing rsa-incident-detail-header__assignee element exists');
  assert.equal(this.$('.rsa-incident-detail-header__status').length, 1, 'Testing rsa-incident-detail-header__status element exists');
  assert.equal(this.$('.rsa-incident-detail-header__source-ip').length, 1, 'Testing rsa-incident-detail-header__source-ip element exists');
  assert.equal(this.$('.rsa-incident-detail-header__destination-ip').length, 1, 'Testing rsa-incident-detail-header__destination-ip element exists');
  assert.equal(this.$('.rsa-incident-detail-header__created').length, 1, 'Testing rsa-incident-detail-header__created element exists');
  assert.equal(this.$('.rsa-incident-detail-header__last-updated').length, 1, 'Testing rsa-incident-detail-header__last-updated element exists');

  assert.equal(this.$('.rsa-incident-detail-header__id').text().indexOf('INC-491') >= 0 , true, 'Testing correct incident ID is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__name input').val(), 'Suspected command and control communication with www.mozilla.com', 'Testing correct incident Name is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__priority .prompt').text().trim(), 'Low', 'Testing correct incident Priority is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__alerts label').text(), 10, 'Testing correct incident Alerts is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__events label').text(), 2, 'Testing correct incident Events is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__sources').length, 1, 'Testing correct number of incident Sources is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__sources .rsa-content-label').text().trim(), 'ESA', 'Testing correct incident Sources is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__assignee .prompt').text().trim(), 'User 1', 'Testing correct incident Assignee is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__status .prompt').text().trim(), 'New', 'Testing correct incident Status is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__source-ip label').text(), '66.249.67.67', 'Testing correct incident Source-IP is rendered');
  assert.equal(this.$('.rsa-incident-detail-header__destination-ip label').text(), '161.253.149.52', 'Testing correct incident Destination-IP is rendered');

});

test('The incident status, priority and assignee are saved', function(assert) {

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    name: 'Suspected command and control communication with www.mozilla.com',
    createdBy: 'User X',
    created: '2015-10-10',
    lastUpdated: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    eventCount: 2,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    },
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
  });
  let users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: 2, firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: 3, firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  incident.save = function() { };

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`{{rsa-respond/incident-detail/detail-header incident=incident users=users}}`);

  let statusVal = this.$('.rsa-incident-detail-header__status select').val();
  assert.equal(statusVal, 0, 'Tile displays the current Incident status.');

  this.$('.rsa-incident-detail-header__status .prompt').click();
  this.$('.rsa-incident-detail-header__status select').val(1).trigger('change');
  assert.equal(incident.statusSort, 1, 'After updating the Select, the incident status has the new value');

  let priorityVal = this.$('.rsa-incident-detail-header__priority select').val();
  assert.equal(priorityVal, 0, 'Tile displays the current Incident priority.');

  this.$('.rsa-incident-detail-header__priority .prompt').click();
  this.$('.rsa-incident-detail-header__priority select').val(1).trigger('change');
  assert.equal(incident.prioritySort, 1, 'After updating the Select, the incident priority has the new value');

  let assigneeVal = this.$('.rsa-incident-detail-header__assignee select').val();
  assert.equal(assigneeVal, 1, 'Tile displays the current Incident priority.');

  this.$('.rsa-incident-detail-header__assignee .prompt').click();
  this.$('.rsa-incident-detail-header__assignee select').val(2).trigger('change');
  assert.equal(incident.assignee.id, 2, 'After updating the Select, the incident assignee has the new value');

  this.$('.rsa-incident-detail-header__assignee .prompt').click();
  this.$('.rsa-incident-detail-header__assignee select').val(-1).trigger('change');
  assert.equal(incident.assignee, undefined, 'After updating the Select, the incident assignee has been removed');
});


test('Manually changing the state of an incident to Closed disables editable fields', function(assert) {

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    name: 'Suspected command and control communication with www.mozilla.com',
    createdBy: 'User X',
    created: '2015-10-10',
    lastUpdated: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    eventCount: 2,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    }
  });
  let users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: 2, firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: 3, firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  incident.save = function() { };

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`{{rsa-respond/incident-detail/detail-header incident=incident users=users}}`);

  this.$('.rsa-incident-detail-header__status .prompt').click();
  this.$('.rsa-incident-detail-header__status select').val(incStatus.CLOSED).trigger('change');

  assert.equal(this.$('.rsa-incident-detail-header__name').hasClass('is-read-only'), true, 'When Incident is in Closed state, Name input is disabled');
  assert.equal(this.$('.rsa-incident-detail-header__priority').hasClass('is-disabled'), true, 'When Incident is in Closed state, Priority dropdown is disabled');
  assert.equal(this.$('.rsa-incident-detail-header__assignee').hasClass('is-disabled'), true, 'When Incident is in Closed state, Assignee dropdown is disabled');
});


test('Incident priority order check (Critical -> Low)', function(assert) {

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    name: 'Suspected command and control communication with www.mozilla.com',
    createdBy: 'User X',
    created: '2015-10-10',
    lastUpdated: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    alertCount: 10,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    }
  });
  let users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
    EmberObject.create({ id: 2, firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
    EmberObject.create({ id: 3, firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`{{rsa-respond/incident-detail/detail-header model=incident users=users}}`);

  let container = this.$(selectors.pages.respond.details.header.detailHeader);

  let priorityOptionList = container.find(this.$(selectors.pages.respond.details.header.prioritySelectOption));

  assert.equal(priorityOptionList[0].text, 'Critical', 'First priority is Critical');
  assert.equal(priorityOptionList[1].text, 'High', 'Second priority is High');
  assert.equal(priorityOptionList[2].text, 'Medium', 'Third priority is Medium');
  assert.equal(priorityOptionList[3].text, 'Low', 'Fourth priority is Low');
});

test('Alert and event count missing test', function(assert) {

  let incident = EmberObject.create({
    riskScore: 1,
    id: 'INC-491',
    name: 'Suspected command and control communication with www.mozilla.com',
    createdBy: 'User X',
    created: '2015-10-10',
    lastUpdated: '2015-10-10',
    statusSort: 0,
    prioritySort: 0,
    sources: ['Event Stream Analysis'],
    assignee: {
      id: '1'
    }
  });
  let users = [EmberObject.create({ id: 1, firstName: 'User 1', lastName: 'LastName 1', email: 'user1@rsa.com' }),
    EmberObject.create({ id: 2, firstName: 'User 2', lastName: 'LastName 2', email: 'user2@rsa.com' }),
    EmberObject.create({ id: 3, firstName: 'User 3', lastName: 'LastName 3', email: 'user3@rsa.com' }) ];

  incident.save = function() { };

  this.set('incident', incident);
  this.set('users', users);

  this.render(hbs`{{rsa-respond/incident-detail/detail-header incident=incident users=users}}`);

  assert.equal(this.$('.rsa-incident-detail-header__alerts label').text(), '-', 'Missing alert count is shown');
  assert.equal(this.$('.rsa-incident-detail-header__events label').text(), '-', 'Missing event count is shown');

});
