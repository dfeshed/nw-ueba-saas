import { moduleForComponent, test } from 'ember-qunit';
import Ember from 'ember';
import hbs from 'htmlbars-inline-precompile';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-respond/incident-detail/detail-index', 'Integration | Component | rsa respond/incident detail/detail index', {
  integration: true
});

test('it renders', function(assert) {
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

  let categoryTags = [EmberObject.create({ 'parent': 'Environmental', 'name': 'Deterioration', 'id': '562aae59e4b03ae1affcc4ff' }),
      EmberObject.create({ 'parent': 'Error', 'name': 'Capacity shortage', 'id': '562aae59e4b03ae1affcc522' }),
      EmberObject.create({ 'parent': 'Hacking', 'name': 'Session replay', 'id': '562aae59e4b03ae1affcc545' })];

  this.set('incident', incident);
  this.set('categoryTags', categoryTags);

  this.render(hbs`{{rsa-respond/incident-detail/detail-index model=incident categoryTags=categoryTags}}`);

  assert.equal(this.$('.rsa-respond-incident__top-panel').length, 1, 'Testing detail header element exists');
  assert.equal(this.$('.rsa-application-layout-manager').length, 1, 'Layout element exists');

});
