import { module, test } from 'qunit';
import { autorunsData } from '../../../state/state';
import {
  fileContextAutorunsSchema,
  fileContextServicesSchema,
  fileContextTasksSchema
} from 'investigate-hosts/reducers/details/autorun/schemas';
import Immutable from 'seamless-immutable';
import { normalize } from 'normalizr';

module('Unit | Selectors | autorun');

import {
  tasks,
  autoruns,
  services,
  isAutorunDataLoading,
  isTaskDataLoading,
  isServiceDataLoading,
  autorunChecksums,
  selectedAutorunCount,
  isAllSelected,
  isAllServiceSelected,
  serviceChecksums,
  selectedServiceCount,
  isAllTaskSelected,
  selectedTaskCount,
  taskChecksums
} from 'investigate-hosts/reducers/details/autorun/selectors';

test('autoruns', function(assert) {
  const normalizedData = normalize(autorunsData, fileContextAutorunsSchema);
  const { autorun } = normalizedData.entities;
  const result = autoruns(Immutable.from({
    endpoint: {
      autoruns: {
        autorun
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 1);
});

test('filter autoruns based on invalid checksum', function(assert) {
  const normalizedData = normalize(autorunsData, fileContextAutorunsSchema);
  const { autorun } = normalizedData.entities;
  const result = autoruns(Immutable.from({
    endpoint: {
      autoruns: {
        autorun
      },
      explore: {
        selectedTab: {
          tabName: 'AUTORUNS',
          checksum: '85c3ac6e4c93b4231cf33c2d7ccdd9fbae348e072eda1afcaaa7609fa75583ce'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 0);
});

test('filter autoruns based on valid checksum', function(assert) {
  const normalizedData = normalize(autorunsData, fileContextAutorunsSchema);
  const { autorun } = normalizedData.entities;
  const result = autoruns(Immutable.from({
    endpoint: {
      autoruns: {
        autorun
      },
      explore: {
        selectedTab: {
          tabName: 'AUTORUNS',
          checksum: '4040cf29a55fd8eaa7ef2a40f8508988b10bacedc49e589aa1d1eb2cd7f02eed'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 1);
});


test('services', function(assert) {
  const normalizedData = normalize(autorunsData, fileContextServicesSchema);
  const { service } = normalizedData.entities;
  const result = services(Immutable.from({
    endpoint: {
      autoruns: {
        service
      },
      explore: {
      },
      datatable: {
      }
    }

  }));
  assert.equal(result.length, 1);
});

test('filter services based on valid checksum', function(assert) {
  const normalizedData = normalize(autorunsData, fileContextServicesSchema);
  const { service } = normalizedData.entities;
  const result = services(Immutable.from({
    endpoint: {
      autoruns: {
        service
      },
      explore: {
        selectedTab: {
          tabName: 'SERVICES',
          checksum: '85c3ac6e4c93b4231cf33c2d7ccdd9fbae348e072eda1afcaaa7609fa75583ce'
        }
      },
      datatable: {
      }
    }

  }));
  assert.equal(result.length, 1);
});

test('filter services based on invalid checksum', function(assert) {
  const normalizedData = normalize(autorunsData, fileContextServicesSchema);
  const { service } = normalizedData.entities;
  const result = services(Immutable.from({
    endpoint: {
      autoruns: {
        service
      },
      explore: {
        selectedTab: {
          tabName: 'SERVICES',
          checksum: '0f030f0a1c8124fca5e8b3a34ddc2ad206843227e753d0c10cc4ad1932564cee'
        }
      },
      datatable: {
      }
    }

  }));
  assert.equal(result.length, 0);
});

test('tasks', function(assert) {
  const normalizedData = normalize(autorunsData, fileContextTasksSchema);
  const { task } = normalizedData.entities;
  const result = tasks(Immutable.from({
    endpoint: {
      autoruns: {
        task
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 2);
});

test('fiter tasks based on valid checksum', function(assert) {
  const normalizedData = normalize(autorunsData, fileContextTasksSchema);
  const { task } = normalizedData.entities;
  const result = tasks(Immutable.from({
    endpoint: {
      autoruns: {
        task
      },
      explore: {
        selectedTab: {
          tabName: 'TASKS',
          checksum: '4040cf29a55fd8eaa7ef2a40f8508988b10bacedc49e589aa1d1eb2cd7f02eed'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 1);
});

test('filter tasks based on invalid checksum', function(assert) {
  const normalizedData = normalize(autorunsData, fileContextTasksSchema);
  const { task } = normalizedData.entities;
  const result = tasks(Immutable.from({
    endpoint: {
      autoruns: {
        task
      },
      explore: {
        selectedTab: {
          tabName: 'TASKS',
          checksum: '85c3ac6e4c93b4231cf33c2d7ccdd9fbae348e072eda1afcaaa7609fa75583ce'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 0);
});

test('autoruns sorting by fileName', function(assert) {
  const normalizedData = normalize(autorunsData, fileContextAutorunsSchema);
  const { autorun } = normalizedData.entities;
  const result = autoruns(Immutable.from({
    endpoint: {
      autoruns: {
        autorun
      },
      explore: {
      },
      datatable: {
        sortConfig: {
          autoruns: { isDescending: true, field: 'fileName' }
        }
      }
    }
  }));
  assert.equal(result[0].fileName, 'vmware-user-suid-wrapper');
});

test('isAutorunDataLoading is set to true when autorunLoadingStatus is wait', function(assert) {
  const result = isAutorunDataLoading(Immutable.from({
    endpoint: {
      autoruns: {
        autorunLoadingStatus: 'wait'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, true);
});

test('isAutorunDataLoading is set to false when autorunLoadingStatus is complete', function(assert) {
  const result = isAutorunDataLoading(Immutable.from({
    endpoint: {
      autoruns: {
        autorunLoadingStatus: 'complete'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, false);
});

test('isTaskDataLoading is set to true when taskLoadingStatus is wait', function(assert) {
  const result = isTaskDataLoading(Immutable.from({
    endpoint: {
      autoruns: {
        taskLoadingStatus: 'wait'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, true);
});

test('isTaskDataLoading is set to false when taskLoadingStatus is complete', function(assert) {
  const result = isTaskDataLoading(Immutable.from({
    endpoint: {
      autoruns: {
        taskLoadingStatus: 'complete'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, false);
});

test('isServiceDataLoading is set to false when serviceLoadingStatus is complete', function(assert) {
  const result = isServiceDataLoading(Immutable.from({
    endpoint: {
      autoruns: {
        serviceLoadingStatus: 'complete'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, false);
});

test('isServiceDataLoading is set to true when serviceLoadingStatus is wait', function(assert) {
  const result = isServiceDataLoading(Immutable.from({
    endpoint: {
      autoruns: {
        serviceLoadingStatus: 'wait'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, true);
});
test('isAllSelected if all autorun selected', function(assert) {

  const normalizedData = normalize(autorunsData, fileContextAutorunsSchema);
  const { autorun } = normalizedData.entities;

  const result1 = isAllSelected(Immutable.from({
    endpoint: {
      autoruns: {
        autorun,
        selectedRowId: '',
        selectedAutorunList: []
      },
      explore: {
      },
      datatable: {
      }
    }
  }));

  const result2 = isAllSelected(Immutable.from({
    endpoint: {
      autoruns: {
        autorun: new Array(3),
        selectedRowId: '',
        selectedAutorunList: new Array(3)
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.equal(result1, false, 'isAllSelected should false ');
  assert.equal(result2, true, 'isAllSelected should true ');
});
test('selectedAutorunCount', function(assert) {
  const result = selectedAutorunCount(Immutable.from({
    endpoint: {
      autoruns: {
        selectedRowId: '',
        selectedAutorunList: new Array(3)
      }
    }
  }));
  assert.equal(result, 3, 'selected autorun count should be 3');
});
test('autorunChecksums to get all selected autorun checksums', function(assert) {
  const result = autorunChecksums(Immutable.from({
    endpoint: {
      autoruns: {
        selectedRowId: '',
        selectedAutorunList: [ { checksumSha256: 0 }, { checksumSha256: 1 }, { checksumSha256: 3 } ]
      }
    }
  }));
  assert.equal(result.length, 3, 'Three checksumSha256 count should be 3');
});
test('isAllServiceSelected if all service selected', function(assert) {

  const normalizedData = normalize(autorunsData, fileContextAutorunsSchema);
  const { autorun } = normalizedData.entities;

  const result1 = isAllServiceSelected(Immutable.from({
    endpoint: {
      autoruns: {
        service: autorun,
        selectedRowId: '',
        selectedServiceList: []
      },
      explore: {
      },
      datatable: {
      }
    }
  }));

  const result2 = isAllServiceSelected(Immutable.from({
    endpoint: {
      autoruns: {
        service: new Array(3),
        selectedRowId: '',
        selectedServiceList: new Array(3)
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.equal(result1, false, 'isAllServiceSelected should false ');
  assert.equal(result2, true, 'isAllServiceSelected should true ');
});
test('selectedServiceCount', function(assert) {
  const result = selectedServiceCount(Immutable.from({
    endpoint: {
      autoruns: {
        selectedRowId: '',
        selectedServiceList: new Array(3)
      }
    }
  }));
  assert.equal(result, 3, 'selected service count should be 3');
});
test('serviceChecksums to get all selected service checksums', function(assert) {
  const result = serviceChecksums(Immutable.from({
    endpoint: {
      autoruns: {
        selectedRowId: '',
        selectedServiceList: [ { checksumSha256: 0 }, { checksumSha256: 1 }, { checksumSha256: 3 } ]
      }
    }
  }));
  assert.equal(result.length, 3, 'Three checksumSha256 count should be 3');
});
test('isAllTaskeSelected if all task selected', function(assert) {

  const normalizedData = normalize(autorunsData, fileContextAutorunsSchema);
  const { autorun } = normalizedData.entities;

  const result1 = isAllTaskSelected(Immutable.from({
    endpoint: {
      autoruns: {
        task: autorun,
        selectedRowId: '',
        selectedTaskList: []
      },
      explore: {
      },
      datatable: {
      }
    }
  }));

  const result2 = isAllTaskSelected(Immutable.from({
    endpoint: {
      autoruns: {
        task: new Array(3),
        selectedRowId: '',
        selectedTaskList: new Array(3)
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.equal(result1, false, 'isAllTaskSelected should false ');
  assert.equal(result2, true, 'isAllTaskSelected should true ');
});
test('selectedTaskCount', function(assert) {
  const result = selectedTaskCount(Immutable.from({
    endpoint: {
      autoruns: {
        selectedRowId: '',
        selectedTaskList: new Array(3)
      }
    }
  }));
  assert.equal(result, 3, 'selected task count should be 3');
});
test('taskChecksums to get all selected task checksums', function(assert) {
  const result = taskChecksums(Immutable.from({
    endpoint: {
      autoruns: {
        selectedRowId: '',
        selectedTaskList: [ { checksumSha256: 0 }, { checksumSha256: 1 }, { checksumSha256: 3 } ]
      }
    }
  }));
  assert.equal(result.length, 3, 'Three checksumSha256 count should be 3');
});