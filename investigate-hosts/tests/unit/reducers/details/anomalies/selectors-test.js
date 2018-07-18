import { module, test } from 'qunit';
import { hooksData } from '../../../state/state';
import { fileContextHooksSchema } from 'investigate-hosts/reducers/details/anomalies/schemas';
import Immutable from 'seamless-immutable';
import { normalize } from 'normalizr';
import { hooks, isHooksDataLoading, imageHooksData, selectedHooksFileProperties } from 'investigate-hosts/reducers/details/anomalies/selectors';

module('Unit | Selectors | anomalies');

test('Hooks', function(assert) {
  const normalizedData = normalize(hooksData, fileContextHooksSchema);
  const { hooks: entitiesHooksData } = normalizedData.entities;
  const result = hooks(Immutable.from({
    endpoint: {
      anomalies: {
        hooks: entitiesHooksData
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 7);
});

test('filter Hooks based on invalid checksum', function(assert) {
  const normalizedData = normalize(hooksData, fileContextHooksSchema);
  const { hooks: entitiesHooksData } = normalizedData.entities;
  const result = hooks(Immutable.from({
    endpoint: {
      anomalies: {
        hooks: entitiesHooksData
      },
      explore: {
        selectedTab: {
          tabName: 'HOOKS',
          checksum: '85c3ac6e4c93b4231cf33c2d7ccdd9fbae348e072eda1afcaaa7609fa75583ce'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 0);
});

test('filter anomalies based on valid checksum', function(assert) {
  const normalizedData = normalize(hooksData, fileContextHooksSchema);
  const { hooks: entitiesHooksData } = normalizedData.entities;
  const result = hooks(Immutable.from({
    endpoint: {
      anomalies: {
        hooks: entitiesHooksData
      },
      explore: {
        selectedTab: {
          tabName: 'HOOKS',
          checksum: '5e99949211beab5f6d72e66a2f1e40e0ada5ab4d9d1b29b60e5952dddfe4551f'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 1);
});

test('hooks sorting by fileName', function(assert) {
  const normalizedData = normalize(hooksData, fileContextHooksSchema);
  const { hooks: entitiesHooksData } = normalizedData.entities;
  const result = hooks(Immutable.from({
    endpoint: {
      anomalies: {
        hooks: entitiesHooksData
      },
      explore: {
      },
      datatable: {
        sortConfig: {
          hooks: { isDescending: false, field: 'fileName' }
        }
      }
    }
  }));

  assert.equal(result[0].fileName, '[FLOATING_CODE]');
});

test('isHooksDataLoading returns true when hooksLoadingStatus is wait', function(assert) {
  const result = isHooksDataLoading(Immutable.from({
    endpoint: {
      anomalies: {
        hooksLoadingStatus: 'wait'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, true);
});

test('isHooksDataLoading returns false when hooksLoadingStatus is complete', function(assert) {
  const result = isHooksDataLoading(Immutable.from({
    endpoint: {
      anomalies: {
        hooksLoadingStatus: 'complete'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, false);
});

test('imageHooksData returns the processed hooks', function(assert) {
  const normalizedData = normalize(hooksData, fileContextHooksSchema);
  const { hooks: entitiesHooksData } = normalizedData.entities;
  const result = imageHooksData(Immutable.from({
    endpoint: {
      anomalies: {
        hooks: entitiesHooksData,
        hooksLoadingStatus: 'complete',
        selectedRowId: null
      },
      explore: {
        selectedTab: null
      },
      datatable: {
        sortConfig: {
          autoruns: null,
          services: null,
          tasks: null,
          libraries: null,
          drivers: null,
          hooks: {
            isDescending: false,
            field: 'dllFileName'
          }
        }
      }
    }
  }));

  assert.equal(result.length, 7);
});

test('selectedHooksFileProperties returns the selected file for properties section', function(assert) {
  const normalizedData = normalize(hooksData, fileContextHooksSchema);
  const { hooks: entitiesHooksData } = normalizedData.entities;
  const result = selectedHooksFileProperties(Immutable.from({
    endpoint: {
      anomalies: {
        hooks: entitiesHooksData,
        hooksLoadingStatus: 'complete',
        selectedRowId: null
      },
      explore: {
        selectedTab: null
      },
      datatable: {
        sortConfig: {
          autoruns: null,
          services: null,
          tasks: null,
          libraries: null,
          drivers: null,
          hooks: {
            isDescending: false,
            field: 'dllFileName'
          }
        }
      }
    }
  }));

  assert.equal(result.fileId, '5b3f348cb249594f465125f2');
});