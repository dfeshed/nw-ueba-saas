import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-respond/incident-detail/event-overview', 'Integration | Component | rsa respond/incident detail/event overview', {
  integration: true,

  beforeEach() {
    let event = {
      'referer': 'curl/7.24.0',
      'alias_host': '4554mb.ru',
      'data': [{
        'filename': '',
        'size': 0,
        'hash': ''
      }],
      'destination': {
        'device': {
          'geolocation': {}
        },
        'user': {}
      },
      'source': {
        'device': {
          'geolocation': {}
        },
        'user': {}
      },
      'type': 'Unknown',
      'directory': '',
      'content': 'txt',
      'enrichment': {
        'command_control': {
          'weighted_domain_ua_ratio_score': 30,
          'weighted_domain_referer_ratio_score': 20,
          'weighted_domain_referer_score': 30,
          'confidence': 100,
          'weighted_whois_age_score': 10,
          'weighted_whois_validity_score': 10,
          'aggregate': 90
        },
        'whois': {
          'scaled_validity': 92.1,
          'domain_name': '4554mb.ru',
          'estimated_domain_validity_days': 3000,
          'scaled_age': 90.5,
          'expires_date': '10-dec-2015',
          'estimated_domain_age_days': 2000,
          'is_cached': false,
          'created_date': '23-sep-2015',
          'updated_date': '2015-09-26',
          'source': 'DATABASE',
          'age_score': 10.3,
          'validity_score': 9.1
        },
        'normalized': {
          'domain': '4554mb.ru',
          'url': '',
          'timestamp': 1445971625844,
          'domain_sip': '4554mb.ru2.2.2.2'
        },
        'domain': {
          'ua_ratio_score': 91.2,
          'referer_num_events': 106,
          'ua_cardinality': 10,
          'referer_ratio': 0.2,
          'referer_cardinality': 10,
          'referer_conditional_cardinality': 2,
          'ua_num_events': 106,
          'ua_score': 96.5,
          'referer_ratio_score': 91.2,
          'referer_score': 96.5,
          'ua_conditional_cardinality': 2,
          'ua_ratio': 0.2
        },
        'beaconing': {
          'beaconing_score': 99,
          'beaconing_period': 3622
        },
        'new_domain': {
          'age_age': 10000000,
          'age_score': 98.7
        },
        'user_agent': {
          'rare_ratio': 0.2,
          'rare_num_events': 106,
          'rare_conditional_cardinality': 2,
          'rare_ratio_score': 91.2,
          'rare_score': 96.5,
          'rare_cardinality': 10
        },
        'smooth': {
          'smooth_beaconing_score': 99.2
        }
      },
      'file': '',
      'detected_by': '-,',
      'client': 'Microsoft IE 10.0',
      'action': 'GET',
      'from': '',
      'timestamp': 'Tue Oct 27 11:47:05 PDT 2015',
      'event_source_id': 'DEV1-IM-Concentrator.grcrtp.local:50005:29589',
      'related_links': [{
        'type': 'investigate_original_event',
        'url': ''
      }, {
        'type': 'investigate_destination_domain',
        'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%274554mb.ru%27%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z'
      }],
      'ip_source': '192.168.1.1',
      'ip_dst': '2.2.2.2',
      'size': 0,
      'service': 80,
      'domain': '4554mb.ru',
      'to': '',
      'detector': {
        'device_class': '',
        'ip_address': '',
        'product_name': ''
      },
      'user': ''
    };
    this.setProperties({
      event
    });
  }
});

test('it renders', function(assert) {

  this.render(hbs`{{rsa-respond/incident-detail/event-overview model=event}}`);

  assert.equal(this.$('.event-overview').length, 1, 'Testing event-overview element exists');
  assert.equal(this.$('.event-overview__loader').length, 0, 'Testing event-overview__loader element doesn not exist');
  assert.equal(this.$('.event-overview__title').length, 1, 'Testing event-overview__title element exists');
  assert.equal(this.$('.event-overview__header').length, 1, 'Testing event-overview__header section exists');
  assert.equal(this.$('.event-overview__summary').length, 1, 'Testing event-overview__summary section exists');
  assert.equal(this.$('.event-overview__info').length, 1, 'Testing event-overview__info element exists');
  assert.equal(this.$('.event-overview__source').length, 1, 'Testing event-overview__source element exists');
  assert.equal(this.$('.event-overview__destination').length, 1, 'Testing event-overview__destination element exists');
  assert.equal(this.$('.event-overview__domain').length, 1, 'Testing event-overview__domain element exists');
  assert.equal(this.$('.event-overview__meta').length, 1, 'Testing event-overview__meta element exists');
});

test('When no model, the loader component is in place', function(assert) {

  this.render(hbs`{{rsa-respond/incident-detail/event-overview}}`);

  assert.equal(this.$('.event-overview').length, 1, 'Testing event-overview element exists');
  assert.equal(this.$('.event-overview__loader').length, 1, 'Testing event-overview__loader element exists');
});
