import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-respond/incident-detail/detail-storyline/ecat-indicators', 'Integration | Component | rsa respond/incident detail/detail storyline/ecat indicators', {
  integration: true
});

test('it renders ECAT indicators', function(assert) {
  const indicator = {
    'indicator': {
      'catalyst': false,
      'modelName': 'ModuleIOC',
      'sourceTypes': ['ENDPOINT'],
      'alert': {
        'agentid': '26C5C21F-4DA8-3A00-437C-AB7444987430',
        'shost': 'INENDEBS1L2C',
        'src': '192.168.1.1',
        'smac': '11-11-11-11-11-11-11-11',
        'fname': 'filename.exe',
        'fsize': '23562',
        'fileHash': 'de9f2c7f d25e1b3a fad3e85a 0bd17d9b 100db4b3',
        'instantIOCName': 'TestIOC',
        'instantIOCLevel': '3',
        'OPSWATResult': 'OPSWAT result here',
        'YARAResult': 'N YARA rules matched',
        'Bit9Status': 'bad',
        'moduleScore': '1-2-3-4',
        'moduleSignature': 'ABC Inc.',
        'os': 'Windows 7',
        'md5sum': '0x00000000000000000000000000000000',
        'machineScore': 1024,
        'relationships': [[
          '',
          'INENDEBS1L2C',
          '',
          '192.168.1.0',
          '',
          'de9f2c7f d25e1b3a fad3e85a 0bd17d9b 100db4b3'
        ]],
        'risk_score': 90,
        'severity': 90,
        'signature_id': 'ModuleIOC',
        'source': 'ECAT',
        'timestamp': 1448000000000,
        'type': [ 'Instant IOC' ],
        'user_summary': []
      }
    },
    'lookup': {}
  };
  this.set('indicator', indicator.indicator);

  this.render(hbs`{{rsa-respond/incident-detail/detail-storyline/ecat-indicators indicator=indicator}}`);

  assert.equal(this.$('.indicator.non-catalyst').length, 1, 'ECAT is catalyst indicator');
  assert.equal(this.$('.sub-indicator.non-catalyst').length, 0, 'with given data ECAT storyline has no sub-indicators');
  assert.equal(this.$('.indicator.non-catalyst .risk-score').length, 1, 'indicator has risk score');
  assert.equal(this.$('.indicator.non-catalyst .indicator__icon').length, 1, 'Right arrow to drill down is present');
  assert.ok(this.$('.indicator-source div').hasClass('is-neutral'), 'content label sources rendered with right style');
  assert.equal(this.$('.indicator-source div').length, 1, 'ECAT indicator has 1 sources');
  assert.equal(this.$('.match-sub-indicator').length, 0, 'ECAT indicator has no lookup items for the given data');

  this.set('indicator.catalyst', true);

  assert.equal(this.$('.indicator.non-catalyst').length, 0, 'ECAT has no non-catalyst indicator');
  assert.equal(this.$('.sub-indicator.non-catalyst').length, 0, 'ECAT has no non-catalyst sub-indicators');
});
