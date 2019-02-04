import { module, test } from 'qunit';
import { resetRiskScoringWhenDisabled, denormalizeRiskScoringSettings, normalizeRiskScoringSettings } from 'configure/reducers/respond/risk-scoring/normalize';
import { getRiskScoringSettings } from '../../../integration/component/respond/risk-scoring/data';

module('Unit | Utility | Respond Risk Scoring Normalize');

test('normalize will not explode when incoming settings are empty or undefined', function(assert) {
  assert.expect(3);

  let result = normalizeRiskScoringSettings([]);
  assert.deepEqual(result, {});

  result = normalizeRiskScoringSettings(undefined);
  assert.deepEqual(result, {});

  result = normalizeRiskScoringSettings(null);
  assert.deepEqual(result, {});
});

test('normalize will not explode when incoming settings are incomplete', function(assert) {
  assert.expect(1);

  const result = normalizeRiskScoringSettings([
    {
      type: 'x'
    },
    {
      type: 'file',
      threshold: '2',
      timeWindow: 9
    }
  ]);

  assert.deepEqual(result, {
    x: {
      threshold: '',
      timeWindow: '',
      timeWindowUnit: '',
      enabled: true
    },
    file: {
      threshold: '2',
      timeWindow: '',
      timeWindowUnit: '',
      enabled: true
    }
  });
});

test('normalize will not explode when incoming settings are incorrect', function(assert) {
  assert.expect(1);

  const result = normalizeRiskScoringSettings([
    {
      foo: '99'
    },
    {
    },
    {
      type: 'file',
      bar: '2',
      timeWindow: [{
        name: 'x'
      }],
      enabled: false
    }
  ]);

  assert.deepEqual(result, {
    undefined: {
      threshold: '',
      timeWindow: '',
      timeWindowUnit: '',
      enabled: true
    },
    file: {
      threshold: '',
      timeWindow: '',
      timeWindowUnit: '',
      enabled: false
    }
  });
});

test('denormalize will transform settings to array of key values', function(assert) {
  assert.expect(1);

  const result = denormalizeRiskScoringSettings({
    host: {
      threshold: '75',
      timeWindow: '1',
      timeWindowUnit: 'd',
      enabled: true
    },
    file: {
      threshold: '80',
      timeWindow: '24',
      timeWindowUnit: 'h',
      enabled: false
    }
  });

  const expected = [
    {
      type: 'HOST',
      threshold: 75,
      timeWindow: '1d',
      enabled: true
    },
    {
      type: 'FILE',
      threshold: 80,
      timeWindow: '24h',
      enabled: false
    }
  ];

  assert.deepEqual(result, expected);
});

test('resetRiskScoringWhenDisabled will reset any file threshold and time related values when file disabled', function(assert) {
  assert.expect(1);

  const settings = getRiskScoringSettings();

  const result = resetRiskScoringWhenDisabled({
    host: {
      threshold: '75',
      timeWindow: '1',
      timeWindowUnit: 'd',
      enabled: true
    },
    file: {
      threshold: '',
      timeWindow: '9',
      timeWindowUnit: 'd',
      enabled: false
    }
  }, settings);

  const expected = {
    host: {
      threshold: '75',
      timeWindow: '1',
      timeWindowUnit: 'd',
      enabled: true
    },
    file: {
      threshold: '80',
      timeWindow: '24',
      timeWindowUnit: 'h',
      enabled: false
    }
  };

  assert.deepEqual(result, expected);
});

test('resetRiskScoringWhenDisabled will reset any host threshold and time related values when host disabled', function(assert) {
  assert.expect(1);

  const settings = getRiskScoringSettings();

  const result = resetRiskScoringWhenDisabled({
    host: {
      threshold: '64',
      timeWindow: '3',
      timeWindowUnit: 'h',
      enabled: false
    },
    file: {
      threshold: '80',
      timeWindow: '24',
      timeWindowUnit: 'h',
      enabled: true
    }
  }, settings);

  const expected = {
    host: {
      threshold: '75',
      timeWindow: '1',
      timeWindowUnit: 'd',
      enabled: false
    },
    file: {
      threshold: '80',
      timeWindow: '24',
      timeWindowUnit: 'h',
      enabled: true
    }
  };

  assert.deepEqual(result, expected);
});

test('reset settings and denormalize are api compatible', function(assert) {
  assert.expect(1);

  const settings = getRiskScoringSettings();

  const resetSettings = resetRiskScoringWhenDisabled({
    host: {
      threshold: '75',
      timeWindow: '1',
      timeWindowUnit: 'd',
      enabled: true
    },
    file: {
      threshold: '',
      timeWindow: '24',
      timeWindowUnit: 'h',
      enabled: false
    }
  }, settings);

  const result = denormalizeRiskScoringSettings(resetSettings);

  const expected = [
    {
      type: 'HOST',
      threshold: 75,
      timeWindow: '1d',
      enabled: true
    },
    {
      type: 'FILE',
      threshold: 80,
      timeWindow: '24h',
      enabled: false
    }
  ];

  assert.deepEqual(result, expected);
});

test('denormalize will not explode when incoming settings are empty or undefined', function(assert) {
  assert.expect(3);

  let result = denormalizeRiskScoringSettings([]);
  assert.deepEqual(result, []);

  result = denormalizeRiskScoringSettings(undefined);
  assert.deepEqual(result, []);

  result = denormalizeRiskScoringSettings(null);
  assert.deepEqual(result, []);
});

test('denormalize will not explode when incoming settings are incomplete', function(assert) {
  assert.expect(1);

  const result = denormalizeRiskScoringSettings({
    undefined: {
      foo: undefined
    },
    file: {
      threshold: null,
      timeWindow: null,
      timeWindowUnit: null,
      enabled: false
    }
  });

  assert.deepEqual(result, [
    {
      type: 'UNDEFINED',
      threshold: 0,
      timeWindow: '',
      enabled: true
    },
    {
      type: 'FILE',
      threshold: 0,
      timeWindow: '',
      enabled: false
    }
  ]);
});

test('normalize will lowercase incoming configuration type and default enabled to true', function(assert) {
  assert.expect(1);

  const result = normalizeRiskScoringSettings([
    {
      type: 'HOST',
      threshold: '2'
    },
    {
      type: 'FILE',
      threshold: '3',
      enabled: false
    }
  ]);

  assert.deepEqual(result, {
    host: {
      threshold: '2',
      timeWindow: '',
      timeWindowUnit: '',
      enabled: true
    },
    file: {
      threshold: '3',
      timeWindow: '',
      timeWindowUnit: '',
      enabled: false
    }
  });
});

test('normalize will accept both true and false for enabled', function(assert) {
  assert.expect(1);

  const result = normalizeRiskScoringSettings([
    {
      type: 'host',
      threshold: 88,
      timeWindow: '1d',
      enabled: false
    },
    {
      type: 'file',
      threshold: 90,
      timeWindow: '24h',
      enabled: true
    }
  ]);

  assert.deepEqual(result, {
    host: {
      threshold: '88',
      timeWindow: '1',
      timeWindowUnit: 'd',
      enabled: false
    },
    file: {
      threshold: '90',
      timeWindow: '24',
      timeWindowUnit: 'h',
      enabled: true
    }
  });
});

test('normalize will correctly accept 0 for threshold value', function(assert) {
  assert.expect(1);

  const result = normalizeRiskScoringSettings([
    {
      type: 'host',
      threshold: 88,
      timeWindow: '1d',
      enabled: false
    },
    {
      type: 'file',
      threshold: 0,
      timeWindow: '24h',
      enabled: true
    }
  ]);

  assert.deepEqual(result, {
    host: {
      threshold: '88',
      timeWindow: '1',
      timeWindowUnit: 'd',
      enabled: false
    },
    file: {
      threshold: '0',
      timeWindow: '24',
      timeWindowUnit: 'h',
      enabled: true
    }
  });
});
