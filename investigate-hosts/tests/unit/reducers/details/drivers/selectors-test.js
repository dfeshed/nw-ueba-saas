import { module, test } from 'qunit';
import { driversData } from '../../../state/state';
import { fileContextListSchema } from 'investigate-hosts/reducers/details/drivers/schemas';
import Immutable from 'seamless-immutable';
import { normalize } from 'normalizr';

module('Unit | Selectors | overview');

import {
  drivers
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
