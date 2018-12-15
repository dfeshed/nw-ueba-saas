import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';


import {
  listOfFiles,
  isAllSelected,
  fileContextFileProperty,
  isDataLoading,
  selectedRowId,
  fileContextSelections,
  totalItems,
  contextLoadMoreStatus,
  isRemediationAllowed,
  fileStatus,
  isNotAdvanced,
  isFloatingOrMemoryDll,
  focusedRowChecksum
} from 'investigate-hosts/reducers/details/file-context/selectors';


module('Unit | Selectors | File Context', function() {

  test('totalItems is set', function(assert) {
    const result = totalItems(Immutable.from({
      endpoint: {
        drivers: {
          totalItems: 10
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result, 10);
  });

  test('fileStatus is set', function(assert) {
    const result = fileStatus(Immutable.from({
      endpoint: {
        drivers: {
          fileStatus: {
            status: 'Blacklisted'
          }
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result.status, 'Blacklisted');
  });

  test('contextLoadMoreStatus is set', function(assert) {
    const result = contextLoadMoreStatus(Immutable.from({
      endpoint: {
        drivers: {
          contextLoadMoreStatus: false
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result, false);
  });

  test('isRemediationAllowed is set', function(assert) {
    const result = isRemediationAllowed(Immutable.from({
      endpoint: {
        drivers: {
          isRemediationAllowed: false
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result, false);
  });


  test('selectedRowId is set', function(assert) {
    const result = selectedRowId(Immutable.from({
      endpoint: {
        drivers: {
          selectedRowId: '1'
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result, 1);
  });

  test('focusedRowChecksum when rowId is available', function(assert) {
    const result = focusedRowChecksum(Immutable.from({
      endpoint: {
        drivers: {
          fileContext: {
            1: {
              id: 1,
              fileName: 'C',
              checksumSha256: 'C1'
            },
            2: {
              id: 2,
              fileName: 'Z',
              checksumSha256: 'Z2'
            },
            3: {
              id: 3,
              fileName: 'A',
              checksumSha256: 'A3'
            }
          },
          selectedRowId: '3'
        }
      }
    }), 'drivers');
    assert.equal(result, 'A3', 'Checksum of third item is returned');
  });

  test('focusedRowChecksum is undefined when rowId is not available', function(assert) {
    const result = focusedRowChecksum(Immutable.from({
      endpoint: {
        drivers: {
          fileContext: {
            1: {
              id: 1,
              fileName: 'C',
              checksumSha256: 'C1'
            }
          },
          selectedRowId: null
        }
      }
    }), 'drivers');
    assert.equal(result, undefined, 'Checksum is not set');
  });

  test('fileContextSelections is set', function(assert) {
    const result = fileContextSelections(Immutable.from({
      endpoint: {
        drivers: {
          fileContextSelections: new Array(10)
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result.length, 10);
  });


  test('fileContext', function(assert) {
    const result = listOfFiles(Immutable.from({
      endpoint: {
        drivers: {
          fileContext: {
            1: {
              id: 1,
              fileName: 'Test'
            },
            2: {
              id: 2,
              fileName: 'Test'
            },
            3: {
              id: 3,
              fileName: 'Test'
            }
          }
        },
        explore: {

        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result.length, 3);
  });

  test('filter drivers based on valid checksum', function(assert) {
    const result = listOfFiles(Immutable.from({
      endpoint: {
        drivers: {
          fileContext: {
            1: {
              id: 1,
              checksumSha256: 1,
              fileName: 'Test'
            },
            2: {
              id: 2,
              fileName: 'Test'
            },
            3: {
              id: 3,
              fileName: 'Test'
            }
          }
        },
        explore: {
          selectedTab: {
            tabName: 'DRIVERS',
            checksum: 1
          }
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result.length, 1);
  });

  test('context sort by file name', function(assert) {
    const result = listOfFiles(Immutable.from({
      endpoint: {
        drivers: {
          fileContext: {
            1: {
              id: 1,
              fileName: 'C'
            },
            2: {
              id: 2,
              fileName: 'Z'
            },
            3: {
              id: 3,
              fileName: 'A'
            }
          }
        },
        explore: {

        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result[0].fileName, 'A');
  });

  test('isDataLoading is set to true when contextLoadingStatus is wait', function(assert) {
    const result = isDataLoading(Immutable.from({
      endpoint: {
        drivers: {
          contextLoadingStatus: 'wait'
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.deepEqual(result, true);
  });

  test('isDataLoading is set to false when contextLoadingStatus is complete', function(assert) {
    const result = isDataLoading(Immutable.from({
      endpoint: {
        drivers: {
          contextLoadingStatus: 'complete'
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.deepEqual(result, false);
  });

  test('fileContextFileProperty when selectedRowId is empty', function(assert) {
    const result = fileContextFileProperty(Immutable.from({
      endpoint: {
        drivers: {
          fileContext: {
            1: {
              id: 1,
              fileName: 'C'
            },
            2: {
              id: 2,
              fileName: 'Z'
            },
            3: {
              id: 3,
              fileName: 'A'
            }
          },
          selectedRowId: null
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result.fileName, 'A', 'file name ');
  });

  test('isAllSelected if all drivers selected', function(assert) {
    const result = isAllSelected(Immutable.from({
      endpoint: {
        drivers: {
          fileContextSelections: new Array(3),
          fileContext: {
            1: {
              id: 1,
              fileName: 'C'
            },
            2: {
              id: 2,
              fileName: 'Z'
            },
            3: {
              id: 3,
              fileName: 'A'
            }
          },
          selectedRowId: null
        },
        explore: {
        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result, true, 'isAllSelected should true ');
  });

  test('isNotAdvanced if agent mode is not Advanced', function(assert) {
    const result = isNotAdvanced(Immutable.from({
      endpoint: {
        overview: {
          hostDetails: {
            machineIdentity: {
              agentMode: 'full'
            }
          }
        }
      }
    }));
    assert.equal(result, true, 'isNotAdvanced should return true ');
  });

  test('isNotAdvanced if agent mode is Advanced', function(assert) {
    const result = isNotAdvanced(Immutable.from({
      endpoint: {
        overview: {
          hostDetails: {
            machineIdentity: {
              agentMode: 'Advanced'
            }
          }
        }
      }
    }));
    assert.equal(result, false, 'isNotAdvanced should return false');
  });

  test('isFloatingOrMemoryDll if not all selected files are memorydlls or floating code', function(assert) {
    const result = isFloatingOrMemoryDll(Immutable.from({
      endpoint: {
        overview: {
          hostDetails: {
            machineIdentity: {
              agentMode: 'Advanced'
            }
          }
        },
        drivers: {
          fileContextSelections: [
            {
              id: 'imageHooks_1',
              fileName: '[FLOATING_CODE_054F182DB0FD4AFBE92B311874C721C8]',
              checksumSha1: '77e2e1facd878903daacfb5a561456225c05a445',
              checksumSha256: 'd30ae1f19c6096d2bfb50dc22731209fd94d659c864d6642c64b5ae39f61876d',
              checksumMd5: '054f182db0fd4afbe92b311874c721c8',
              size: 65536,
              machineOsType: 'windows',
              path: '',
              downloadInfo: {},
              features: [],
              format: 'floating'
            },
            {
              id: 'imageHooks_12',
              fileName: 'HookTest_DLL64_0c30.dll',
              checksumSha1: '5c6ff89eef54b7d5fba72889c2250ee09b04bcab',
              checksumSha256: '5469da9747d23abd3a1ccffa2bccfe6256938a416f707ed2160d3eda3867c30d',
              checksumMd5: 'a91f3390e2fadbbcb2a347ba685cb22a',
              signature: {
                features: [
                  'unsigned'
                ]
              },
              size: 150528,
              machineOsType: 'windows',
              path: 'C:\\Users\\kslp\\AppData\\Local\\Temp\\',
              downloadInfo: {},
              features: [
                'file.dll',
                'file.arch64',
                'file.resourceDirectoryPresent',
                'file.relocationDirectoryPresent',
                'file.debugDirectoryPresent',
                'file.richSignaturePresent'
              ],
              format: 'pe'
            }
          ]
        }
      }
    }), 'drivers');
    assert.equal(result, false, 'isFloatingOrMemoryDll should return false ');
  });

  test('isFloatingOrMemoryDll if all selected files are memorydlls or floating code', function(assert) {
    const result = isFloatingOrMemoryDll(Immutable.from({
      endpoint: {
        overview: {
          hostDetails: {
            machineIdentity: {
              agentMode: 'Advanced'
            }
          }
        },
        drivers: {
          fileContextSelections: [
            {
              id: 'imageHooks_1',
              fileName: '[FLOATING_CODE_054F182DB0FD4AFBE92B311874C721C8]',
              checksumSha1: '77e2e1facd878903daacfb5a561456225c05a445',
              checksumSha256: 'd30ae1f19c6096d2bfb50dc22731209fd94d659c864d6642c64b5ae39f61876d',
              checksumMd5: '054f182db0fd4afbe92b311874c721c8',
              size: 65536,
              machineOsType: 'windows',
              path: '',
              downloadInfo: {},
              features: [],
              format: 'floating'
            },
            {
              id: 'imageHooks_13',
              fileName: '[MEMORY_DLL_EF01C6AE31431AA02BE6DF1EA764A3D4]',
              checksumSha1: '53f32db2c7221bdcf55be69387ec6ebffe1daa02',
              checksumSha256: '5e99949211beab5f6d72e66a2f1e40e0ada5ab4d9d1b29b60e5952dddfe4551f',
              checksumMd5: 'ef01c6ae31431aa02be6df1ea764a3d4',
              signature: {
                features: [
                  'unsigned'
                ]
              },
              size: 0,
              machineOsType: 'windows',
              path: '',
              downloadInfo: {},
              features: [
                'file.dll',
                'file.arch64',
                'file.memoryHash',
                'file.iconPresent',
                'file.versionInfoPresent',
                'file.resourceDirectoryPresent',
                'file.relocationDirectoryPresent',
                'file.debugDirectoryPresent',
                'file.boundImportDirectoryPresent',
                'file.richSignaturePresent',
                'file.companyNameContainsText',
                'file.descriptionContainsText',
                'file.versionContainsText',
                'file.internalNameContainsText',
                'file.legalCopyrightContainsText',
                'file.originalFilenameContainsText',
                'file.productNameContainsText',
                'file.productVersionContainsText',
                'file.standardVersionMetaPresent'
              ],
              format: 'pe'
            }
          ]
        }
      }
    }), 'drivers');
    assert.equal(result, true, 'isFloatingOrMemoryDll should return true ');
  });
});

