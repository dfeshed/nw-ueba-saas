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

      }
    }
  }));
  assert.equal(result.length, 4);
});
