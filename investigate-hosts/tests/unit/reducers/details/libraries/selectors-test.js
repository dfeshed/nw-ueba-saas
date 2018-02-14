import { module, test } from 'qunit';
import { libraries } from '../../../state/state';
import { fileContextListSchema } from 'investigate-hosts/reducers/details/libraries/schemas';
import Immutable from 'seamless-immutable';
import { normalize } from 'normalizr';

module('Unit | Selectors | overview');

import {
  getLibraries,
  isDataLoading,
  selectedLibraryFileProperty
} from 'investigate-hosts/reducers/details/libraries/selectors';

test('getLibraries', function(assert) {
  const normalizedData = normalize(libraries, fileContextListSchema);
  const result = getLibraries(Immutable.from({
    endpoint: {
      libraries: {
        library: normalizedData.entities.library,
        processList: [{ pid: 683, name: 'test' }]
      },
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
      libraries: {
        library: normalizedData.entities.library,
        processList: [{ pid: 683, name: 'test' }]
      },
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

test('getLibraries when checksum is valid', function(assert) {
  const normalizedData = normalize(libraries, fileContextListSchema);
  const result = getLibraries(Immutable.from({
    endpoint: {
      libraries: {
        library: normalizedData.entities.library,
        processList: [{ pid: 683, name: 'test' }]
      },
      explore: {
        selectedTab: {
          tabName: 'LIBRARIES',
          checksum: '5d4025d0970fa278588f118d64287700e1bc6ecbc1109022766fb67f75a317cf'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 4);
});

test('getLibraries when checksum is invalid', function(assert) {
  const normalizedData = normalize(libraries, fileContextListSchema);
  const result = getLibraries(Immutable.from({
    endpoint: {
      libraries: {
        library: normalizedData.entities.library,
        processList: [{ pid: 683, name: 'test' }]
      },
      explore: {
        selectedTab: {
          tabName: 'LIBRARIES',
          checksum: '5d0970fa278588f118d64287700e1bc6ecbc1109022766fb67f75a317cf'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 0);
});

test('getLibraries when processList is an empty array', function(assert) {
  const normalizedData = normalize(libraries, fileContextListSchema);
  const result = getLibraries(Immutable.from({
    endpoint: {
      libraries: {
        library: normalizedData.entities.library,
        processList: []
      },
      explore: {},
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 8);
  assert.deepEqual(result[0].processContext, undefined);
});

test('getLibraries when processList is undefined', function(assert) {
  const normalizedData = normalize(libraries, fileContextListSchema);
  const result = getLibraries(Immutable.from({
    endpoint: {
      libraries: { library: normalizedData.entities.library },
      explore: {},
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 8);
  assert.deepEqual(result[0].processContext, undefined);
});

test('getLibraries when pid is invalid', function(assert) {
  const normalizedData = normalize(libraries, fileContextListSchema);
  const result = getLibraries(Immutable.from({
    endpoint: {
      libraries: {
        library: normalizedData.entities.library,
        processList: [{ pid: '000', name: 'test1' }]
      },
      explore: {},
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 8);
  assert.deepEqual(result[0].processContext, undefined);
});

test('isDataLoading is set to true when libraryLoadingStatus is wait', function(assert) {
  const result = isDataLoading(Immutable.from({
    endpoint: {
      libraries: {
        libraryLoadingStatus: 'wait'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, true);
});

test('isDataLoading is set to true when libraryLoadingStatus is complete', function(assert) {
  const result = isDataLoading(Immutable.from({
    endpoint: {
      libraries: {
        libraryLoadingStatus: 'complete'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, false);
});

test('selectedLibraryFileProperty when selectedRowId is empty', function(assert) {
  const normalizedData = normalize(libraries, fileContextListSchema);
  const result = selectedLibraryFileProperty(Immutable.from({
    endpoint: {
      libraries: {
        library: normalizedData.entities.library,
        selectedRowId: ''
      },
      explore: {},
      datatable: {
      }
    }
  }));
  assert.equal(result.fileName, 'imuxsock.so', 'selected file name');
  assert.deepEqual(result.fileProperties.checksumSha256, '45f2c37e4f65bbff8024c8e9e9aee8eadc60cf3b0742f127e9d40f2fd789a0e6', 'selected file hash');
});