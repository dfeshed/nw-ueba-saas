import { module, test } from 'qunit';
import { driversData } from '../../../state/state';
import { fileContextListSchema } from 'investigate-hosts/reducers/details/drivers/schemas';
import Immutable from 'seamless-immutable';
import { normalize } from 'normalizr';

module('Unit | Selectors | overview');

import {
  drivers,
  isDataLoading,
  selectedDriverFileProperty
} from 'investigate-hosts/reducers/details/drivers/selectors';

test('drivers', function(assert) {
  const normalizedData = normalize(driversData, fileContextListSchema);
  const result = drivers(Immutable.from({
    endpoint: {
      drivers: {
        driver: normalizedData.entities.driver
      },
      explore: {

      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 4);
});

test('filter drivers based on valid checksum', function(assert) {
  const normalizedData = normalize(driversData, fileContextListSchema);
  const result = drivers(Immutable.from({
    endpoint: {
      drivers: {
        driver: normalizedData.entities.driver
      },
      explore: {
        selectedTab: {
          tabName: 'DRIVERS',
          checksum: '9ee6f17becd84af5070def9237affd9ec21a45365048952bdf69f5a9fe798908'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 1);
});

test('filter drivers based on invalid checksum', function(assert) {
  const normalizedData = normalize(driversData, fileContextListSchema);
  const result = drivers(Immutable.from({
    endpoint: {
      drivers: {
        driver: normalizedData.entities.driver
      },
      explore: {
        selectedTab: {
          tabName: 'DRIVERS',
          checksum: 'eee6f17becd84af5070def9237affd9ec21a45365048952bdf69f5a9fe798908'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 0);
});

test('drivers sort by file name', function(assert) {
  const normalizedData = normalize(driversData, fileContextListSchema);
  const result = drivers(Immutable.from({
    endpoint: {
      drivers: {
        driver: normalizedData.entities.driver
      },
      explore: {
      },
      datatable: {
        sortConfig: {
          drivers: { isDescending: false, field: 'fileName' }
        }
      }
    }
  }));
  assert.equal(result[0].fileName, 'crc-t10dif.ko');
});

test('isDataLoading is set to true when driverLoadingStatus is wait', function(assert) {
  const result = isDataLoading(Immutable.from({
    endpoint: {
      drivers: {
        driverLoadingStatus: 'wait'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, true);
});

test('isDataLoading is set to false when driverLoadingStatus is complete', function(assert) {
  const result = isDataLoading(Immutable.from({
    endpoint: {
      drivers: {
        driverLoadingStatus: 'complete'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, false);
});

test('selectedDriverFileProperty when selectedRowId is empty', function(assert) {
  const normalizedData = normalize(driversData, fileContextListSchema);
  const result = selectedDriverFileProperty(Immutable.from({
    endpoint: {
      drivers: {
        driver: normalizedData.entities.driver,
        selectedRowId: ''
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.fileName, 'crc-t10dif.ko', 'file name ');
  assert.equal(result.fileProperties.checksumSha256, '3afe70ba0a58cb15a456d8d2e748a6a4bedcb59b2663fa711227b97fa2105a5f', 'fileproperties checksum');
});
