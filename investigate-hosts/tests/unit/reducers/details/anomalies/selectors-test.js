import { module, test } from 'qunit';
import { anomaliesData, registryDiscrepanciesData } from '../../../state/state';
import {
  fileContextImageHooksSchema,
  fileContextKernelHooksSchema,
  fileContextThreadsSchema
} from 'investigate-hosts/reducers/details/anomalies/schemas';
import Immutable from 'seamless-immutable';
import { normalize } from 'normalizr';
import {
  imageHooks,
  isImageHooksDataLoading,
  imageHooksData,
  selectedImageHooksFileProperties,
  kernelHooks,
  isKernelHooksDataLoading,
  kernelHooksData,
  selectedKernelHooksFileProperties,
  threads,
  isThreadsDataLoading,
  suspiciousThreadsData,
  selectedThreadsFileProperties,
  registryDiscrepancies
} from 'investigate-hosts/reducers/details/anomalies/selectors';

module('Unit | Selectors | anomalies');

test('Image Hooks', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextImageHooksSchema);
  const { imageHooks: entitiesHooksData } = normalizedData.entities;
  const result = imageHooks(Immutable.from({
    endpoint: {
      anomalies: {
        imageHooks: entitiesHooksData
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
  const normalizedData = normalize(anomaliesData, fileContextImageHooksSchema);
  const { imageHooks: entitiesHooksData } = normalizedData.entities;
  const result = imageHooks(Immutable.from({
    endpoint: {
      anomalies: {
        imageHooks: entitiesHooksData
      },
      explore: {
        selectedTab: {
          tabName: 'IMAGEHOOKS',
          checksum: '85c3ac6e4c93b4231cf33c2d7ccdd9fbae348e072eda1afcaaa7609fa75583ce'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 0);
});

test('filter anomalies image hooks based on valid checksum', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextImageHooksSchema);
  const { imageHooks: entitiesHooksData } = normalizedData.entities;
  const result = imageHooks(Immutable.from({
    endpoint: {
      anomalies: {
        imageHooks: entitiesHooksData
      },
      explore: {
        selectedTab: {
          tabName: 'IMAGEHOOKS',
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
  const normalizedData = normalize(anomaliesData, fileContextImageHooksSchema);
  const { imageHooks: entitiesHooksData } = normalizedData.entities;
  const result = imageHooks(Immutable.from({
    endpoint: {
      anomalies: {
        imageHooks: entitiesHooksData
      },
      explore: {
      },
      datatable: {
        sortConfig: {
          imageHooks: { isDescending: false, field: 'fileName' }
        }
      }
    }
  }));

  assert.equal(result[0].fileName, '[FLOATING_CODE]');
});

test('isImageHooksDataLoading returns true when imageHooksLoadingStatus is wait', function(assert) {
  const result = isImageHooksDataLoading(Immutable.from({
    endpoint: {
      anomalies: {
        imageHooksLoadingStatus: 'wait'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, true);
});

test('isImageHooksDataLoading returns false when imageHooksLoadingStatus is complete', function(assert) {
  const result = isImageHooksDataLoading(Immutable.from({
    endpoint: {
      anomalies: {
        imageHooksLoadingStatus: 'complete'
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
  const normalizedData = normalize(anomaliesData, fileContextImageHooksSchema);
  const { imageHooks: entitiesHooksData } = normalizedData.entities;
  const result = imageHooksData(Immutable.from({
    endpoint: {
      anomalies: {
        imageHooks: entitiesHooksData,
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
          imageHooks: {
            isDescending: false,
            field: 'dllFileName'
          }
        }
      }
    }
  }));

  assert.equal(result.length, 7);
});

test('selectedImageHooksFileProperties returns the selected file for properties section', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextImageHooksSchema);
  const { imageHooks: entitiesHooksData } = normalizedData.entities;
  const result = selectedImageHooksFileProperties(Immutable.from({
    endpoint: {
      anomalies: {
        imageHooks: entitiesHooksData,
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
          imageHooks: {
            isDescending: false,
            field: 'dllFileName'
          }
        }
      }
    }
  }));

  assert.equal(result.fileId, '5b3f348cb249594f465125f2');
});

test('Threads', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextThreadsSchema);
  const { threads: entitiesThreadsData } = normalizedData.entities;
  const result = threads(Immutable.from({
    endpoint: {
      anomalies: {
        threads: entitiesThreadsData
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 5);
});

test('filter Threads based on invalid checksum', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextThreadsSchema);
  const { threads: entitiesThreadsData } = normalizedData.entities;
  const result = threads(Immutable.from({
    endpoint: {
      anomalies: {
        threads: entitiesThreadsData
      },
      explore: {
        selectedTab: {
          tabName: 'THREADS',
          checksum: '85c3ac6e4c93b4231cf33c2d7ccdd9fbae348e072eda1afcaaa7609fa75583ce'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 0);
});

test('filter anomalies threads based on valid checksum', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextThreadsSchema);
  const { threads: entitiesThreadsData } = normalizedData.entities;
  const result = threads(Immutable.from({
    endpoint: {
      anomalies: {
        threads: entitiesThreadsData
      },
      explore: {
        selectedTab: {
          tabName: 'THREADS',
          checksum: '5e99949211beab5f6d72e66a2f1e40e0ada5ab4d9d1b29b60e5952dddfe4551f'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 3);
});

test('threads sorting by fileName', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextThreadsSchema);
  const { threads: entitiesThreadsData } = normalizedData.entities;
  const result = threads(Immutable.from({
    endpoint: {
      anomalies: {
        threads: entitiesThreadsData
      },
      explore: {
      },
      datatable: {
        sortConfig: {
          threads: { isDescending: false, field: 'fileName' }
        }
      }
    }
  }));

  assert.equal(result[0].fileName, '[FLOATING_CODE]');
});

test('isThreadsDataLoading returns true when threadsLoadingStatus is wait', function(assert) {
  const result = isThreadsDataLoading(Immutable.from({
    endpoint: {
      anomalies: {
        threadsLoadingStatus: 'wait'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, true);
});

test('isThreadsDataLoading returns false when threadsLoadingStatus is complete', function(assert) {
  const result = isThreadsDataLoading(Immutable.from({
    endpoint: {
      anomalies: {
        threadsLoadingStatus: 'complete'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, false);
});

test('suspiciousThreadsData returns the processed threads', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextThreadsSchema);
  const { threads: entitiesThreadsData } = normalizedData.entities;

  const result = suspiciousThreadsData(Immutable.from({
    endpoint: {
      anomalies: {
        threads: entitiesThreadsData,
        threadsLoadingStatus: 'complete',
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
          imageHooks: {
            isDescending: false,
            field: 'dllFileName'
          },
          threads: {
            isDescending: false,
            field: 'dllFileName'
          }
        }
      }
    }
  }));

  assert.equal(result.length, 5);
});

test('selectedThreadsFileProperties returns the selected file for properties section', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextThreadsSchema);
  const { threads: entitiesThreadsData } = normalizedData.entities;
  const result = selectedThreadsFileProperties(Immutable.from({
    endpoint: {
      anomalies: {
        threads: entitiesThreadsData,
        threadsLoadingStatus: 'complete',
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
          imageHooks: null,
          threads: {
            isDescending: false,
            field: 'dllFileName'
          }
        }
      }
    }
  }));

  assert.equal(result.fileId, '5b3f348cb249594f465125f2');
});

test('Kernel Hooks', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextKernelHooksSchema);
  const { kernelHooks: entitiesHooksData } = normalizedData.entities;
  const result = kernelHooks(Immutable.from({
    endpoint: {
      anomalies: {
        kernelHooks: entitiesHooksData
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 6);
});

test('filter Hooks based on invalid checksum', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextKernelHooksSchema);
  const { kernelHooks: entitiesHooksData } = normalizedData.entities;
  const result = kernelHooks(Immutable.from({
    endpoint: {
      anomalies: {
        kernelHooks: entitiesHooksData
      },
      explore: {
        selectedTab: {
          tabName: 'KERNELHOOKS',
          checksum: '85c3ac6e4c93b4231cf33c2d7ccdd9fbae348e072eda1afcaaa7609fa75583ce'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 0);
});

test('filter anomalies kernel hooks based on valid checksum', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextKernelHooksSchema);
  const { kernelHooks: entitiesHooksData } = normalizedData.entities;
  const result = kernelHooks(Immutable.from({
    endpoint: {
      anomalies: {
        kernelHooks: entitiesHooksData
      },
      explore: {
        selectedTab: {
          tabName: 'KERNELHOOKS',
          checksum: '5e99949211beab5f6d72e66a2f1e40e0ada5ab4d9d1b29b60e5952dddfe4551f'
        }
      },
      datatable: {
      }
    }
  }));
  assert.equal(result.length, 3);
});

test('hooks sorting by fileName', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextKernelHooksSchema);
  const { kernelHooks: entitiesHooksData } = normalizedData.entities;
  const result = kernelHooks(Immutable.from({
    endpoint: {
      anomalies: {
        kernelHooks: entitiesHooksData
      },
      explore: {
      },
      datatable: {
        sortConfig: {
          kernelHooks: { isDescending: false, field: 'fileName' }
        }
      }
    }
  }));

  assert.equal(result[0].fileName, '[FLOATING_CODE]');
});

test('isKernelHooksDataLoading returns true when hooksLoadingStatus is wait', function(assert) {
  const result = isKernelHooksDataLoading(Immutable.from({
    endpoint: {
      anomalies: {
        kernelHooksLoadingStatus: 'wait'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, true);
});

test('isKernelHooksDataLoading returns false when hooksLoadingStatus is complete', function(assert) {
  const result = isKernelHooksDataLoading(Immutable.from({
    endpoint: {
      anomalies: {
        kernelHooksLoadingStatus: 'complete'
      },
      explore: {
      },
      datatable: {
      }
    }
  }));
  assert.deepEqual(result, false);
});

test('kernelHooksData returns the processed hooks', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextKernelHooksSchema);
  const { kernelHooks: entitiesHooksData } = normalizedData.entities;
  const result = kernelHooksData(Immutable.from({
    endpoint: {
      anomalies: {
        kernelHooks: entitiesHooksData,
        kernelHooksLoadingStatus: 'complete',
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
          kernelHooks: {
            isDescending: false,
            field: 'dllFileName'
          }
        }
      }
    }
  }));

  assert.equal(result.length, 6);
});

test('selectedKernelHooksFileProperties returns the selected file for properties section', function(assert) {
  const normalizedData = normalize(anomaliesData, fileContextKernelHooksSchema);
  const { kernelHooks: entitiesHooksData } = normalizedData.entities;
  const result = selectedKernelHooksFileProperties(Immutable.from({
    endpoint: {
      anomalies: {
        kernelHooks: entitiesHooksData,
        kernelhooksLoadingStatus: 'complete',
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
          kernelHooks: {
            isDescending: false,
            field: 'dllFileName'
          }
        }
      }
    }
  }));

  assert.equal(result.fileId, '5b3f348cb249594f465125f2');
});

test('registryDiscrepancies returns the processed registry discrepancies', function(assert) {
  const result = registryDiscrepancies(Immutable.from({
    endpoint: {
      overview: {
        hostDetails: {
          machine: { ...registryDiscrepanciesData }
        }
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
          kernelHooks: {
            isDescending: false,
            field: 'dllFileName'
          }
        }
      }
    }
  }));

  assert.equal(result.length, 5);
});