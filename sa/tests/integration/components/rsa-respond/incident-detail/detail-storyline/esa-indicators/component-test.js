import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-respond/incident-detail/detail-storyline/esa-indicators', 'Integration | Component | rsa respond/incident detail/detail storyline/esa indicators', {
  integration: true
});

test('it renders C2-Packet', function(assert) {
  const indicator = {
    'catalyst': false,
    'indicator': {
      'modelName': 'C2-Packet',
      'sourceTypes': ['UEBA', 'Packet'],
      'alert': {
        'events': [{
          'domain': '4554mb.ru',
          'enrichment': {
            'rsa_analytics_http-packet_c2_newdomain_score': 85.53810102910933,
            'rsa_analytics_http-packet_c2_referer_score': 64.4036421083141,
            'rsa_analytics_http-packet_c2_ua_ratio_score': 100.0,
            'rsa_analytics_http-packet_c2_normalized_domain': 'evil1-beacon45s.com',
            'rsa_analytics_http-packet_c2_whois_validity_score': 83.5030897216977,
            'rsa_analytics_http-packet_c2_command_control_aggregate': 90.7626911163496,
            'rsa_analytics_http-packet_c2_whois_age_score': 86.639009496887,
            'rsa_analytics_http-packet_c2_whois_scaled_validity': 10.0,
            'rsa_analytics_http-packet_c2_referer_ratio_score': 90.0,
            'rsa_analytics_http-packet_c2_whois_domain_not_found_by_whois': true,
            'rsa_analytics_http-packet_c2_smooth_score': 88.0,
            'rsa_analytics_http-packet_c2_normalized_full_domain': 'evil1-beacon45s.com'
          }
        }],
        'timestamp': 1471428207482,
        'user_summary': []
      }
    },
    'lookup': {}
  };
  this.setProperties({
    'groupedIps': ['10.10.10.10'],
    indicator: indicator.indicator
  });

  this.render(hbs`{{rsa-respond/incident-detail/detail-storyline/esa-indicators indicatorType='c2' groupedIps=groupedIps indicator=indicator}}`);

  assert.equal(this.$('.indicator.non-catalyst').length, 1, 'C2-Packet non-catalyst indicator is rendered');
  assert.equal(this.$('.indicator.non-catalyst .rsa-content-ip-connections').length, 1, 'C2-Packet indicator renders ip using style guide component');
  assert.equal(this.$('.indicator.non-catalyst .rsa-content-ip-connections .from-ip').length, 1, 'C2-Packet indicator renders just the from ip');
  assert.equal(this.$('.sub-indicator.non-catalyst').length, 7, 'C2-Packet storyline has sub-indicators');
  assert.equal(this.$('.indicator.non-catalyst .risk-score').length, 1, 'C2-Packet indicator has risk score');
  assert.equal(this.$('.indicator.non-catalyst .indicator__icon').length, 1, 'C2-Packet Right arrow to drill down is present');
  assert.ok(this.$('.indicator-source div').hasClass('is-neutral'), 'C2-Packet content label sources rendered with right style');
  // assert.equal(this.$('.indicator-source div').length, 2, 'C2 Packet indicator has 2 sources');
  assert.equal(this.$('.match-sub-indicator').length, 0, 'C2 Packet indicator has no lookup items for the given data');
});

test('it renders C2-Log', function(assert) {
  const indicator = {
    'catalyst': false,
    'indicator': {
      'modelName': 'C2-Log',
      'sourceTypes': ['UEBA', 'Log'],
      'alert': {
        'events': [{
          'domain': '4554mb.ru',
          'enrichment': {
            'rsa_analytics_http-log_c2_newdomain_score': 85.53810102910933,
            'rsa_analytics_http-log_c2_referer_score': 64.4036421083141,
            'rsa_analytics_http-log_c2_ua_ratio_score': 100.0,
            'rsa_analytics_http-log_c2_normalized_domain': 'evil1-beacon45s.com',
            'rsa_analytics_http-log_c2_whois_validity_score': 83.5030897216977,
            'rsa_analytics_http-log_c2_command_control_aggregate': 90.7626911163496,
            'rsa_analytics_http-log_c2_whois_age_score': 86.639009496887,
            'rsa_analytics_http-log_c2_whois_scaled_validity': 10.0,
            'rsa_analytics_http-log_c2_referer_ratio_score': 90.0,
            'rsa_analytics_http-log_c2_whois_domain_not_found_by_whois': true,
            'rsa_analytics_http-log_c2_smooth_score': 88.0,
            'rsa_analytics_http-log_c2_normalized_full_domain': 'evil1-beacon45s.com'
          }
        }],
        'timestamp': 1471428207482,
        'user_summary': []
      }
    },
    'lookup': {}
  };
  this.setProperties({
    'groupedIps': ['10.10.10.10'],
    indicator: indicator.indicator
  });

  this.render(hbs`{{rsa-respond/incident-detail/detail-storyline/esa-indicators indicatorType='c2' groupedIps=groupedIps indicator=indicator}}`);

  assert.equal(this.$('.indicator.non-catalyst').length, 1, 'C2 Log non-catalyst indicator is rendered');
  assert.equal(this.$('.indicator.non-catalyst .rsa-content-ip-connections').length, 1, 'C2 Log indicator renders ip using style guide component');
  assert.equal(this.$('.indicator.non-catalyst .rsa-content-ip-connections .from-ip').length, 1, 'C2 Log indicator renders just the from ip');
  assert.equal(this.$('.sub-indicator.non-catalyst').length, 7, 'C2 Log storyline has sub-indicators');
  assert.equal(this.$('.indicator.non-catalyst .risk-score').length, 1, 'C2 Log indicator has risk score');
  assert.equal(this.$('.indicator.non-catalyst .indicator__icon').length, 1, 'C2 Log Right arrow to drill down is present');
  assert.ok(this.$('.indicator-source div').hasClass('is-neutral'), 'C2 Log content label sources rendered with right style');
  // assert.equal(this.$('.indicator-source div').length, 2, 'C2 Log indicator has 2 sources');
  assert.equal(this.$('.match-sub-indicator').length, 0, 'C2 Log indicator has no lookup items for the given data');

  this.set('indicator.catalyst', true);

  assert.equal(this.$('.indicator.non-catalyst').length, 0, 'C2 has no non-catalyst indicator');
  assert.equal(this.$('.sub-indicator.non-catalyst').length, 0, 'C2 Log storyline has no non-catalyst sub-indicators');
  assert.equal(this.$('.indicator').length, 1, 'C2 catalyst indicator is rendered');
  assert.equal(this.$('.sub-indicator').length, 7, 'C2 Log storyline has catalyst sub-indicators');
});

test('it renders UEBA WinAuth', function(assert) {
  const indicator = {
    'indicator': {
      'catalyst': true,
      'modelName': 'UBA-WinAuth',
      'sourceTypes': ['UEBA'],
      'alert': {
        'events': [{
          'domain': '4554mb.ru',
          'enrichment': {
            'rsa_analytics_uba_winauth_device_exists': false,
            'rsa_analytics_uba_winauth_failedservers_average_cardinality': 1.00000115740674,
            'rsa_analytics_uba_winauth_failedserversscore_score': 66.466440348745,
            'rsa_analytics_uba_winauth_normalized_regexoutput': 'USITBRUTSDM1',
            'rsa_analytics_uba_winauth_newserver_cardinality': 2.0,
            'rsa_analytics_uba_winauth_newdevice_average_cardinality': 1.00000115740674,
            'rsa_analytics_uba_winauth_newdevicescore_score': 86.466440348745,
            'rsa_analytics_uba_winauth_newdeviceservice_score': true,
            'rsa_analytics_uba_winauth_failedserversscore_ratio': 1.9999976851892,
            'rsa_analytics_uba_winauth_highserverscore_ratio': 1.9999976851892,
            'rsa_analytics_uba_winauth_logontypescore_score': 47.45555,
            'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_newdevicescore_score': 17.293288069749,
            'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_highserverscore_score': 17.293288069749,
            'rsa_analytics_uba_winauth_newserverscore_score': 76.466440348745,
            'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_newserverscore_score': 8.6466440348745,
            'rsa_analytics_uba_winauth_aggregation_weighted_rsa_analytics_uba_winauth_failedserversscore_score': 8.6466440348745,
            'rsa_analytics_uba_winauth_newdevice_cardinality': 2.0,
            'rsa_analytics_uba_winauth_normalized_timestamp': 1457801997000,
            'rsa_analytics_uba_winauth_normalized_hostname': 'USITBRUTSDM1',
            'rsa_analytics_uba_winauth_aggregation_aggregate': 186,
            'rsa_analytics_uba_winauth_aggregation_confidence': 66.6666666666667,
            'rsa_analytics_uba_winauth_highservers_cardinality': 2.0,
            'rsa_analytics_uba_winauth_newdevicescore_ratio': 1.9999976851892,
            'rsa_analytics_uba_winauth_newserverscore_ratio': 1.9999976851892,
            'rsa_analytics_uba_winauth_highservers_average_cardinality': 1.00000115740674,
            'rsa_analytics_uba_winauth_highserverscore_score': 66.466440348745,
            'rsa_analytics_uba_winauth_failedservers_cardinality': 2.0,
            'rsa_analytics_uba_winauth_newserver_average_cardinality': 1.00000115740674
          }
        }],
        'timestamp': 1471428207482,
        'user_summary': []
      }
    },
    'lookup': {}
  };
  this.set('indicator', indicator.indicator);

  this.render(hbs`{{rsa-respond/incident-detail/detail-storyline/esa-indicators indicatorType='winauth' indicator=indicator}}`);

  assert.equal(this.$('.indicator.non-catalyst').length, 0, 'UEBA is catalyst indicator');
  assert.equal(this.$('.indicator').length, 1, 'UEBA catalyst indicator is rendered');
  assert.equal(this.$('.sub-indicator').length, 3, 'UEBA storyline has sub-indicators');
  assert.equal(this.$('.indicator .risk-score').length, 1, 'UEBA indicator has risk score');
  assert.equal(this.$('.indicator .indicator__icon').length, 1, 'UEBA Right arrow to drill down is present');
  assert.ok(this.$('.indicator-source div').hasClass('is-neutral'), 'UEBA content label sources rendered with right style');
  assert.equal(this.$('.indicator-source div').length, 1, 'UEBA WinAuth indicator has 1 sources');
  assert.equal(this.$('.match-sub-indicator').length, 0, 'UEBA WinAuth indicator has no lookup items for the given data');

  this.set('indicator.catalyst', false);

  assert.equal(this.$('.indicator.non-catalyst').length, 1, 'UEBA has non-catalyst indicator');
  assert.equal(this.$('.sub-indicator.non-catalyst').length, 3, 'UEBA storyline has non-catalyst sub-indicators');
});
