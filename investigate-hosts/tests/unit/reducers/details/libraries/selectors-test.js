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
      explore: {},
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 8);
});

test('libraries sort by file name', function(assert) {
  const normalizedData = normalize(libraries, fileContextListSchema);
  const result = getLibraries(Immutable.from({
    endpoint: {
      processList: [{ pid: 683, name: 'test' }],
      libraries: { library: normalizedData.entities.library },
      explore: {
      },
      datatable: {
        sortConfig: {
          libraries: { isDescending: false, field: 'fileName' }
        }
      }
    }
  }));
  assert.equal(result[0].fileName, 'imuxsock.so', 'first element');
  assert.equal(result[6].fileName, 'libxml2.so.2.9.1', 'seventh element');
});
