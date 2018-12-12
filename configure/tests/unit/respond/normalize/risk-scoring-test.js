import { module, test } from 'qunit';
import { denormalizeRiskScoringSettings, normalizeRiskScoringSettings } from 'configure/reducers/respond/risk-scoring/normalize';

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
      timeWindowUnit: ''
    },
    file: {
      threshold: '2',
      timeWindow: '',
      timeWindowUnit: ''
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
      }]
    }
  ]);

  assert.deepEqual(result, {
    undefined: {
      threshold: '',
      timeWindow: '',
      timeWindowUnit: ''
    },
    file: {
      threshold: '',
      timeWindow: '',
      timeWindowUnit: ''
    }
  });
});

test('denormalize will transform settings to array of key values', function(assert) {
  assert.expect(1);

  const result = denormalizeRiskScoringSettings({
    host: {
      threshold: '75',
      timeWindow: '1',
      timeWindowUnit: 'd'
    },
    file: {
      threshold: '80',
      timeWindow: '24',
      timeWindowUnit: 'h'
    }
  });

  const expected = [
    {
      type: 'HOST',
      threshold: 75,
      timeWindow: '1d'
    },
    {
      type: 'FILE',
      threshold: 80,
      timeWindow: '24h'
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
      timeWindowUnit: null
    }
  });

  assert.deepEqual(result, [
    {
      type: 'UNDEFINED',
      threshold: 0,
      timeWindow: ''
    },
    {
      type: 'FILE',
      threshold: 0,
      timeWindow: ''
    }
  ]);
});

test('normalize will lowercase incoming configuration type', function(assert) {
  assert.expect(1);

  const result = normalizeRiskScoringSettings([
    {
      type: 'HOST',
      threshold: '2'
    },
    {
      type: 'FILE',
      threshold: '3'
    }
  ]);

  assert.deepEqual(result, {
    host: {
      threshold: '2',
      timeWindow: '',
      timeWindowUnit: ''
    },
    file: {
      threshold: '3',
      timeWindow: '',
      timeWindowUnit: ''
    }
  });
});
