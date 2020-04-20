import { module, test } from 'qunit';

import { filterData } from 'investigate-hosts/reducers/utils/filter-utils';

module('Unit | Utils | filter utils', function() {

  test('filter for equality', function(assert) {
    const data = [
      {
        fileProperty: {
          fileFirstName: 'test'
        }
      },
      {
        fileProperty: {
          fileFirstName: 'test1'
        }
      },
      {
        fileProperty: {
          fileFirstName: 'test2'
        }
      }
    ];
    const result = filterData(data, [{
      'propertyName': 'fileProperty.fileFirstName',
      'restrictionType': 'IN',
      'propertyValues': [
        {
          'value': 'test'
        },
        {
          'value': 'test1'
        }
      ]
    }]);
    assert.equal(result.length, 2);
  });

  test('when there no expression returns non filter data', function(assert) {
    const data = [
      {
        fileProperty: {
          fileFirstName: 'test'
        }
      },
      {
        fileProperty: {
          fileFirstName: 'test1'
        }
      },
      {
        fileProperty: {
          fileFirstName: 'test2'
        }
      }
    ];
    const result = filterData(data, []);
    assert.equal(result.length, 3);
  });

  test('it filters between data', function(assert) {
    const data = [
      {
        fileProperty: {
          score: 90
        }
      },
      {
        fileProperty: {
          score: 60
        }
      },
      {
        fileProperty: {
          score: 65
        }
      },
      {
        fileProperty: {
          score: 55
        }
      },
      {
        fileProperty: {
          score: 40
        }
      }
    ];
    const result = filterData(data, [{
      'propertyName': 'fileProperty.score',
      'restrictionType': 'BETWEEN',
      'propertyValues': [
        {
          'value': 40
        },
        {
          'value': 80
        }
      ]
    }]);
    assert.equal(result.length, 4);
  });

  test('it filters signature field correctly', function(assert) {
    const data = [
      {
        fileProperty: {
          signature: {
            features: ['unsigned']
          }
        }
      },
      {
        fileProperty: {
          signature: {
            features: ['signed', 'valid']
          }
        }
      },
      {
        fileProperty: {
          signature: {
            features: ['signed', 'microsoft', 'valid']
          }
        }
      },
      {
        fileProperty: {
          signature: {
            features: ['signed', 'apple', 'valid']
          }
        }
      },
      {
        fileProperty: {
          signature: {
            features: ['signed', 'apple', 'invalid']
          }
        }
      }
    ];
    const result = filterData(data, [{
      'propertyName': 'fileProperty.signature.features',
      'restrictionType': 'IN',
      'propertyValues': [
        {
          'value': 'apple'
        },
        {
          'value': 'invalid'
        }
      ]
    }]);
    assert.equal(result.length, 2);
  });

});
