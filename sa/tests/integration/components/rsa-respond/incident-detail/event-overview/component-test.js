import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import setupRouter from 'sa/tests/helpers/setup-router';

moduleForComponent('rsa-respond/incident-detail/event-overview', 'Integration | Component | rsa respond/incident detail/event overview', {
  integration: true,

  beforeEach() {

    // setup router to support testing link-to helpers
    setupRouter(this);

    const event = {
      '_id': '57dcda273004ebc5ef83543d',
      'event_source_id': 'loki-concentrator',
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
        'ip_address': '1.2.3.4',
        'product_name': ''
      },
      'user': ''
    };

    const incident = {
      'id': 'INC-2',
      'name': 'Suspected command and control communication with 4554mb.ru',
      'summary': 'Incident Summary',
      'priority': 'CRITICAL',
      'prioritySort': 3,
      'riskScore': 80,
      'status': 'NEW',
      'created': 1474091565378,
      'lastUpdated': 1474585318241,
      'assignee': null
    };

    const services = [ { id: '555d9a6fe4b0d37c827d402d', displayName: 'loki-concentrator', 'name': 'CONCENTRATOR' } ];

    this.setProperties({
      event,
      incident,
      services
    });
  }
});

test('it renders', function(assert) {

  this.render(hbs`{{rsa-respond/incident-detail/event-overview event=event}}`);
  assert.equal(this.$('.event-overview').length, 1, 'Testing event-overview element exists');
  assert.equal(this.$('.event-overview__loader').length, 0, 'Testing event-overview__loader element does not exist');
  assert.equal(this.$('.event-overview__title').length, 1, 'Testing event-overview__title element exists');
  assert.equal(this.$('.event-overview__header').length, 1, 'Testing event-overview__header section exists');
  assert.equal(this.$('.event-overview__summary').length, 1, 'Testing event-overview__summary section exists');
  assert.equal(this.$('.event-overview__info').length, 1, 'Testing event-overview__info element exists');
  assert.equal(this.$('.event-overview__source').length, 1, 'Testing event-overview__source element exists');
  assert.equal(this.$('.event-overview__destination').length, 1, 'Testing event-overview__destination element exists');
  assert.equal(this.$('.event-overview__domain').length, 1, 'Testing event-overview__domain element exists');
  assert.equal(this.$('.event-overview__detector').length, 1, 'Testing event-overview__detector element exists');
  assert.equal(this.$('.event-overview__meta').length, 1, 'Testing event-overview__meta element exists');

});

test('When no model, the loader component is in place', function(assert) {

  this.render(hbs`{{rsa-respond/incident-detail/event-overview}}`);

  assert.equal(this.$('.event-overview').length, 1, 'Testing event-overview element exists');
  assert.equal(this.$('.event-overview .respond-loader').length, 1, 'Testing event-overview__loader element exists');
});

test('All Links to Investigate page', function(assert) {

  this.render(hbs`{{rsa-respond/incident-detail/event-overview event=event incident=incident services=services}}`);

  const sourceIpQueryUrl = '/do/investigate/query/555d9a6fe4b0d37c827d402d/1474090965/1474092165/ip.src=192.168.1.1';
  let templateUrl = this.$('.event-overview__source a').attr('href');
  assert.equal(templateUrl, sourceIpQueryUrl, 'Check URL for Source IP query');

  const dstIpQueryUrl = '/do/investigate/query/555d9a6fe4b0d37c827d402d/1474090965/1474092165/ip.dst=2.2.2.2';
  templateUrl = this.$('.event-overview__destination a').attr('href');
  assert.equal(templateUrl, dstIpQueryUrl, 'Check URL for Destination IP query');

  const domainQueryUrl = '/do/investigate/query/555d9a6fe4b0d37c827d402d/1474090965/1474092165/alias.host=4554mb.ru';
  templateUrl = this.$('.event-overview__domain a').attr('href');
  assert.equal(templateUrl, domainQueryUrl, 'Check URL for Domain (aka alias host) query');

  const detectorQueryUrl = '/do/investigate/query/555d9a6fe4b0d37c827d402d/1474090965/1474092165/device.ip=1.2.3.4';
  templateUrl = this.$('.event-overview__detector a').attr('href');
  assert.equal(templateUrl, detectorQueryUrl, 'Check URL for Detector (aka device ip) query');

});

test('Services data impacts links', function(assert) {

  // No services
  let tmpServices = [];
  this.set('tmpServices', tmpServices);
  this.render(hbs`{{rsa-respond/incident-detail/event-overview event=event incident=incident services=tmpServices}}`);

  assert.equal(this.$('.event-overview__source a').length, 0, 'No URL for Source IP query');
  assert.equal(this.$('.event-overview__destination a').length, 0, 'No URL for Destination IP query');
  assert.equal(this.$('.event-overview__domain a').length, 0, 'No URL for Domain IP query');
  assert.equal(this.$('.event-overview__detector a').length, 0, 'No URL for Detector IP query');

  // Service name not found
  tmpServices = [ { id: '555d9a6fe4b0d37c827d402d', displayName: 'not-a-loki-concentrator', name: 'CONCENTRATOR' } ];
  this.set('tmpServices', tmpServices);

  assert.equal(this.$('.event-overview__source a').length, 0, 'No URL for Source IP query');
  assert.equal(this.$('.event-overview__destination a').length, 0, 'No URL for Destination IP query');
  assert.equal(this.$('.event-overview__domain a').length, 0, 'No URL for Domain IP query');
  assert.equal(this.$('.event-overview__detector a').length, 0, 'No URL for Detector IP query');

  // Service name found
  tmpServices = [ { id: '555d9a6fe4b0d37c827d402d', displayName: 'loki-concentrator', name: 'CONCENTRATOR' } ];
  this.set('tmpServices', tmpServices);

  assert.equal(this.$('.event-overview__source a').length, 1, 'URL for Source IP query');
  assert.equal(this.$('.event-overview__destination a').length, 1, 'URL for Destination IP query');
  assert.equal(this.$('.event-overview__domain a').length, 1, 'URL for Domain IP query');
  assert.equal(this.$('.event-overview__detector a').length, 1, 'URL for Detector IP query');

});

test('Incident data impacts links', function(assert) {

  // Value for 'created' property is not set
  let tmpIncident = {};
  this.set('tmpIncident', tmpIncident);
  this.render(hbs`{{rsa-respond/incident-detail/event-overview event=event incident=tmpIncident services=services}}`);

  assert.equal(this.$('.event-overview__source a').length, 0, 'No URL for Source IP query');
  assert.equal(this.$('.event-overview__destination a').length, 0, 'No URL for Destination IP query');
  assert.equal(this.$('.event-overview__domain a').length, 0, 'No URL for Domain IP query');
  assert.equal(this.$('.event-overview__detector a').length, 0, 'No URL for Detector IP query');

  // Value for 'created' property is set to 0 (will use default)
  tmpIncident = { 'created': 0 };
  this.set('tmpIncident', tmpIncident);

  assert.equal(this.$('.event-overview__source a').length, 1, 'URL for Source IP query');
  assert.equal(this.$('.event-overview__destination a').length, 1, 'URL for Destination IP query');
  assert.equal(this.$('.event-overview__domain a').length, 1, 'URL for Domain IP query');
  assert.equal(this.$('.event-overview__detector a').length, 1, 'URL for Detector IP query');

  // Value for 'created' property is set to -1 (will use default)
  tmpIncident = { 'created': -1 };
  this.set('tmpIncident', tmpIncident);

  assert.equal(this.$('.event-overview__source a').length, 1, 'URL for Source IP query');
  assert.equal(this.$('.event-overview__destination a').length, 1, 'URL for Destination IP query');
  assert.equal(this.$('.event-overview__domain a').length, 1, 'URL for Domain IP query');
  assert.equal(this.$('.event-overview__detector a').length, 1, 'URL for Detector IP query');

  // Value for 'created' property is set
  tmpIncident = { 'created': 1474091565378 };
  this.set('tmpIncident', tmpIncident);

  assert.equal(this.$('.event-overview__source a').length, 1, 'URL for Source IP query');
  assert.equal(this.$('.event-overview__destination a').length, 1, 'URL for Destination IP query');
  assert.equal(this.$('.event-overview__domain a').length, 1, 'URL for Domain IP query');
  assert.equal(this.$('.event-overview__detector a').length, 1, 'URL for Detector IP query');

});

test('Event data impacts links', function(assert) {

  // No event source id
  let tmpEvent = {
    '_id': '57dcda273004ebc5ef83543d',
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
      'ip_address': '1.2.3.4',
      'product_name': ''
    },
    'user': ''
  };

  this.set('tmpEvent', tmpEvent);
  this.render(hbs`{{rsa-respond/incident-detail/event-overview event=tmpEvent incident=incident services=services}}`);

  assert.equal(this.$('.event-overview__source a').length, 0, 'No URL for Source IP query');
  assert.equal(this.$('.event-overview__destination a').length, 0, 'No URL for Destination IP query');
  assert.equal(this.$('.event-overview__domain a').length, 0, 'No URL for Domain IP query');
  assert.equal(this.$('.event-overview__detector a').length, 0, 'No URL for Detector IP query');

  // Required fields are not present in event (source ip, destination ip, alias host, detector)
  tmpEvent = {
    '_id': '57dcda273004ebc5ef83543d',
    'event_source_id': 'loki-concentrator',
    'referer': 'curl/7.24.0',
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
    'related_links': [{
      'type': 'investigate_original_event',
      'url': ''
    }, {
      'type': 'investigate_destination_domain',
      'url': '/investigation/DEV1-IM-Concentrator.grcrtp.local:50005/navigate/query/alias.host%3D%274554mb.ru%27%2Fdate%2F2016-09-17T05%3A42%3A39.853Z%2F2016-09-17T06%3A02%3A39.853Z'
    }],
    'size': 0,
    'service': 80,
    'domain': '4554mb.ru',
    'to': '',
    'user': ''
  };

  this.set('tmpEvent', tmpEvent);

  assert.equal(this.$('.event-overview__source a').length, 0, 'No URL for Source IP query');
  assert.equal(this.$('.event-overview__destination a').length, 0, 'No URL for Destination IP query');
  assert.equal(this.$('.event-overview__domain a').length, 0, 'No URL for Domain IP query');
  assert.equal(this.$('.event-overview__detector a').length, 0, 'No URL for Detector IP query');

});


