import { module, test } from 'qunit';
import { libraries } from '../../../state/state';
import { fileContextListSchema } from 'investigate-hosts/reducers/details/libraries/schemas';
import Immutable from 'seamless-immutable';
import { normalize } from 'normalizr';

module('Unit | Selectors | overview');

import {
  getLibraries
} from 'investigate-hosts/reducers/details/libraries/selectors';

test('getLibraries', function(assert) {
  const normalizedData = normalize(libraries, fileContextListSchema);
  const result = getLibraries(Immutable.from({
    endpoint: {
      processList: [{ pid: 683, name: 'test' }],
      libraries: { library: normalizedData.entities.library },
      explore: {}
    }
  }));
  assert.equal(result.length, 8);
});
