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
  fileDownloadButtonStatus,
  focusedRowChecksum,
  selectedFileList,
  isAnyFileFloatingOrMemoryDll,
  hostNameList,
  selectedFileHostCount,
  isSelectedMachineServerId
} from 'investigate-hosts/reducers/details/file-context/selectors';

import { fileContextSelectionsData } from '../../../../integration/components/state/fileContextData';


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

  test('filter drivers based on expression list', function(assert) {
    const result = listOfFiles(Immutable.from({
      endpoint: {
        details: {
          filter: {
            expressionList: [
              {
                'propertyName': 'fileName',
                'restrictionType': 'IN',
                'propertyValues': [
                  {
                    'value': 'Test'
                  }
                ]
              }
            ]
          }
        },
        drivers: {
          fileContext: {
            1: {
              id: 1,
              checksumSha256: 1,
              fileName: 'Test'
            },
            2: {
              id: 2,
              fileName: 'Test12'
            },
            3: {
              id: 3,
              fileName: 'Test22'
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

  test('context sort by risk score', function(assert) {
    const result = listOfFiles(Immutable.from({
      endpoint: {
        drivers: {
          fileContext: {
            1: {
              id: 1,
              fileName: 'C',
              machineFileScore: 20,
              fileProperties: {
                score: 20
              }
            },
            2: {
              id: 2,
              fileName: 'Z',
              machineFileScore: 90,
              fileProperties: {
                score: 100
              }
            },
            3: {
              id: 3,
              fileName: 'A',
              machineFileScore: 45,
              fileProperties: {
                score: 45
              }
            }
          }
        },
        explore: {

        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result[0].fileName, 'Z');
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

  test('selectedFileList is set with checksum and filename', function(assert) {
    const result = selectedFileList(Immutable.from({
      endpoint: {
        drivers: {
          fileContextSelections: [...fileContextSelectionsData]
        }
      }
    }), 'drivers');
    assert.equal(result.length, 1, '1 file is selected');
    assert.deepEqual(result[0],
      { checksumSha256: '5469da9747d23abd3a1ccffa2bccfe6256938a416f707ed2160d3eda3867c30d',
        downloadInfo: {},
        fileName: 'HookTest_DLL64_0c30.dll' }, 'Verify first file checksum and filename');
  });

  test('fileContextFileProperty when selectedRowId is empty', function(assert) {
    const result = fileContextFileProperty(Immutable.from({
      endpoint: {
        drivers: {
          fileContext: {
            1: {
              id: 1,
              fileName: 'C',
              machineFileScore: 20,
              fileProperties: {
                score: 20
              }
            },
            2: {
              id: 2,
              fileName: 'Z',
              machineFileScore: 100,
              fileProperties: {
                score: 100
              }
            },
            3: {
              id: 3,
              fileName: 'A',
              machineFileScore: 45,
              fileProperties: {
                score: 45
              }
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
    assert.equal(result.fileName, 'Z', 'file name ');
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

  test('fileDownloadButtonStatus if agent mode is not Advanced', function(assert) {
    const result = fileDownloadButtonStatus(Immutable.from({
      endpoint: {
        machines: {
          selectedHostList: [{
            id: 1,
            version: '4.3.0.0',
            managed: true
          }]
        },
        overview: {
          hostDetails: {
            machineIdentity: {
              agentMode: 'full'
            }
          }
        },
        drivers: {
          fileContextSelections: [...fileContextSelectionsData]
        }
      }
    }), 'drivers');
    assert.deepEqual(result, { 'isDownloadToServerDisabled': true, 'isSaveLocalAndFileAnalysisDisabled': true }, 'fileDownloadButtonStatus ok');
  });

  test('fileDownloadButtonStatus if agent mode is Advanced', function(assert) {
    const result = fileDownloadButtonStatus(Immutable.from({
      endpoint: {
        machines: {
          selectedHostList: [{
            id: 1,
            version: '4.3.0.0',
            managed: true
          }]
        },
        overview: {
          hostDetails: {
            machineIdentity: {
              agentMode: 'Advanced'
            }
          }
        },
        drivers: {
          fileContextSelections: [...fileContextSelectionsData]
        }
      }
    }), 'drivers');
    assert.deepEqual(result, { 'isDownloadToServerDisabled': false, 'isSaveLocalAndFileAnalysisDisabled': true }, 'fileDownloadButtonStatus ok');
  });

  test('fileDownloadButtonStatus if more than 1 file is selected is disabled', function(assert) {
    const result = fileDownloadButtonStatus(Immutable.from({
      endpoint: {
        machines: {
          selectedHostList: [{
            id: 1,
            version: '4.3.0.0',
            managed: true
          }]
        },
        overview: {
          hostDetails: {
            machineIdentity: {
              agentMode: 'Advanced'
            }
          }
        },
        drivers: {
          fileContextSelections: [...fileContextSelectionsData, ...fileContextSelectionsData]
        }
      }
    }), 'drivers');
    assert.deepEqual(result, { 'isDownloadToServerDisabled': true, 'isSaveLocalAndFileAnalysisDisabled': true }, 'fileDownloadButtonStatus is ok');
  });

  test('fileDownloadButtonStatus if not all selected files are memorydlls or floating code', function(assert) {
    const result = fileDownloadButtonStatus(Immutable.from({
      endpoint: {
        machines: {
          selectedHostList: [{
            id: 1,
            version: '4.3.0.0',
            managed: true
          }]
        },
        overview: {
          hostDetails: {
            machineIdentity: {
              agentMode: 'Advanced'
            }
          }
        },
        drivers: {
          fileContextSelections: [...fileContextSelectionsData]
        }
      }
    }), 'drivers');
    assert.deepEqual(result, { 'isDownloadToServerDisabled': false, 'isSaveLocalAndFileAnalysisDisabled': true }, 'fileDownloadButtonStatus ok');
  });

  test('fileDownloadButtonStatus if all selected files are memorydlls or floating code', function(assert) {
    const result = fileDownloadButtonStatus(Immutable.from({
      endpoint: {
        machines: {
          selectedHostList: [{
            id: 1,
            version: '4.3.0.0',
            managed: true
          }]
        },
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
                'file.versionInfoPresent'
              ],
              format: 'pe'
            }
          ]
        }
      }
    }), 'drivers');
    assert.deepEqual(result, { 'isDownloadToServerDisabled': true, 'isSaveLocalAndFileAnalysisDisabled': true }, 'fileDownloadButtonStatus ok');
  });

  test('isAnyFileFloatingOrMemoryDll', function(assert) {
    const state = Immutable.from({
      endpoint: {
        machines: {
          selectedHostList: [{
            id: 1,
            version: '4.3.0.0',
            managed: true
          }]
        },
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
                'file.versionInfoPresent'
              ],
              format: 'pe'
            }
          ]
        }
      }
    });
    const result = isAnyFileFloatingOrMemoryDll(state, 'drivers');
    assert.equal(result, true);
  });

  test('isAnyFileFloatingOrMemoryDll when file has only memory dll, in one of the selections', function(assert) {
    const state = Immutable.from({
      endpoint: {
        machines: {
          selectedHostList: [{
            id: 1,
            version: '4.3.0.0',
            managed: true
          }]
        },
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
              format: 'pe'
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
                'file.versionInfoPresent'
              ],
              format: 'pe'
            }
          ]
        }
      }
    });
    const result = isAnyFileFloatingOrMemoryDll(state, 'drivers');
    assert.equal(result, true);
  });

  test('hostNameList', function(assert) {
    const result = hostNameList(Immutable.from({
      endpoint: {
        drivers: {
          hostNameList: [
            {
              agentId: '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAC',
              hostname: 'windows',
              score: 0
            },
            {
              agentId: '0C0454BB-A0D9-1B2A-73A6-5E8CCBF88DAB',
              hostname: 'linux',
              score: 0
            }
          ]
        },
        explore: {

        },
        datatable: {
        }
      }
    }), 'drivers');
    assert.equal(result.length, 2);
  });

  test('selectedFileHostCount is set', function(assert) {
    const result = selectedFileHostCount(Immutable.from({
      endpoint: {
        drivers: {
          fileContextSelections: [...fileContextSelectionsData]
        }
      }
    }), 'drivers');
    assert.deepEqual(result, 2, 'Verify first file Host Count');
  });

  test('isSelectedMachineServerId is set', function(assert) {
    const result = isSelectedMachineServerId(Immutable.from({
      endpointQuery: {
        selectedMachineServerId: null,
        serverId: '1ad8338d-68ee-44b7-bac6-3b09ce43ac4e'
      }
    }));
    assert.deepEqual(result, '1ad8338d-68ee-44b7-bac6-3b09ce43ac4e', 'verify the server Id');

    const result1 = isSelectedMachineServerId(Immutable.from({
      endpointQuery: {
        selectedMachineServerId: '1ad8338d-68ee-44b7-bac6-3b09ce43ac4e',
        serverId: null
      }
    }));
    assert.deepEqual(result1, '1ad8338d-68ee-44b7-bac6-3b09ce43ac4e', 'verify the Machine server Id');
  });

});

