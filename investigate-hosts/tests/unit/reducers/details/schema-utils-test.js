import { module, test } from 'qunit';
import { addId, commonNormalizerStrategy } from 'investigate-hosts/reducers/details/schema-utils';

module('Unit | Utils | schema-utils');

test('should add unique id to all the object', function(assert) {
  const data = [{ name: 'xyz' }, { name: 'abc' }];
  addId(data, '1', 'auto');
  assert.ok(data[0].id.includes('auto'));
});

test('should modify the signature property and add', function(assert) {
  const parent = {
    name: 'xyz',
    machineOsType: 'linux',
    linux: {
      'drivers': [
        {
          'numberOfInstances': 0,
          'loadState': 'Live'
        },
        {
          'numberOfInstances': 0,
          'loadState': 'Live'
        }
      ]
    },
    fileProperties: {
      signature: {
        features: 'xyz'
      }
    }
  };
  const child = {
    name: 'abc',
    path: 'c/test',
    size: '1024'
  };

  const data = commonNormalizerStrategy(child, parent);
  assert.deepEqual(data, {
    'machineOsType': 'linux',
    'fileProperties': {
      'signature': {
        'features': 'xyz'
      }
    },
    'name': 'abc',
    'path': 'c/test',
    'signature': 'xyz',
    'size': '1024'
  });

  const parent1 = {
    name: 'xyz',
    machineOsType: 'linux',
    fileProperties: {
    },
    linux: {
      'drivers': [
        {
          'numberOfInstances': 0,
          'loadState': 'Live'
        },
        {
          'numberOfInstances': 0,
          'loadState': 'Live'
        }
      ]
    }
  };
  const child1 = {
    name: 'abc',
    path: 'c/test',
    size: '1024'
  };

  const newData = commonNormalizerStrategy(child1, parent1);

  assert.deepEqual(newData, {
    'machineOsType': 'linux',
    'fileProperties': {},
    'name': 'abc',
    'path': 'c/test',
    'signature': undefined,
    'size': '1024'
  });
});
