import { module, test } from 'qunit';
import { getProperties, getValues } from 'investigate-hosts/reducers/details/selector-utils';

module('Unit | Utils | selector-utils');

const dataSet = {
  1: {
    fileName: 'abc',
    id: 1,
    checksumSha256: '1'
  },
  10: {
    fileName: 'xyz',
    id: 10,
    checksumSha256: '10'
  },
  3: {
    fileName: 'xyz1',
    id: 3,
    checksumSha256: '3'
  },
  5: {
    fileName: 'xy',
    id: 5,
    checksumSha256: '5'
  }
};

test('should extracts the values for selected tab and sorts the data', function(assert) {
  const tabName = 'AUTORUNS';
  let sortConfig = {
    autoruns: {
      field: 'id',
      isDescending: false
    }
  };
  let result = getValues(undefined, tabName, dataSet, sortConfig);
  assert.equal(result[0].id, 1, 'isDescending false');
  sortConfig = {
    autoruns: {
      field: 'id',
      isDescending: true
    }
  };
  result = getValues(undefined, tabName, dataSet, sortConfig);
  assert.equal(result[0].id, 10, 'isDescending true');
});

test('should filter the data for selected tab', function(assert) {
  const selectedTab = {
    tabName: 'AUTORUNS',
    checksum: '10'
  };
  const sortConfig = {
    autoruns: {
      field: 'id',
      isDescending: false
    }
  };
  const result = getValues(selectedTab, 'AUTORUNS', dataSet, sortConfig);
  assert.equal(result[0].id, 10, 'filter the data based on checksum');

});
test('should return the property', function(assert) {
  const result = getProperties(5, null, dataSet);
  assert.equal(result.id, 5);
  const sortConfig = {
    autoruns: {
      field: 'id',
      isDescending: false
    }
  };
  const list = getValues(undefined, 'AUTORUNS', dataSet, sortConfig);
  const newResult = getProperties(null, list, null);
  assert.equal(newResult.id, 1);
});
