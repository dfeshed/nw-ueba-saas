import { module, test } from 'qunit';
import {
  processDetailsData,
  processTreeData,
  processListData,
  dllListData
} from '../../../state/state';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | process');

import {
  isNavigatedFromExplore,
  getProcessData,
  processTree,
  noProcessData,
  enrichedDllData,
  processList,
  isProcessLoading,
  isJazzAgent,
  imageHooksData
} from 'investigate-hosts/reducers/details/process/selectors';

test('getProcessData', function(assert) {
  const result = getProcessData(Immutable.from({
    endpoint: {
      process: {
        processDetails: processDetailsData
      }
    }
  }));
  assert.equal(result.process.signature, 'valid,signed,Apple');
});

test('processTree', function(assert) {
  const result = processTree(Immutable.from({
    endpoint: {
      process: {
        processList: processListData,
        processTree: processTreeData
      },
      explore: {
        selectedTab: 'process'
      }
    }
  }));
  assert.equal(result.length, 3);
});

test('isNavigatedFromExplore', function(assert) {
  const result = isNavigatedFromExplore(Immutable.from({
    endpoint: {
      process: {
        processList: processListData,
        processTree: processTreeData
      },
      explore: {
        selectedTab: {
          tabName: 'PROCESS'
        }
      }
    }
  }));
  assert.equal(result, true);
});

test('noProcessData returns true when processTree is empty', function(assert) {
  const result = noProcessData(Immutable.from({
    endpoint: {
      process: {
        processTree: [],
        processList: []
      },
      explore: {
        selectedTab: {
          tabName: 'PROCESS'
        }
      }
    }
  }));
  assert.deepEqual(result, true);
});

test('noProcessData returns false when processTree is not empty', function(assert) {
  const result = noProcessData(Immutable.from({
    endpoint: {
      process: {
        processTree: processTreeData,
        processList: processListData
      },
      explore: {
        selectedTab: {
          tabName: 'PROCESS'
        }
      }
    }
  }));
  assert.deepEqual(result, false);
});

test('enrichedDllData without signature', function(assert) {
  const result = enrichedDllData(Immutable.from({
    endpoint: {
      process: {
        dllList: [
          {
            fileProperties: {
              fileName: 'test.exe'
            }
          }
        ]
      }
    }
  }));
  assert.equal(result[0].signature, undefined);
});
test('enrichedDllData with signature', function(assert) {

  let result = enrichedDllData(Immutable.from({
    endpoint: {
      process: {
        dllList: [
          {
            fileProperties: {
              fileName: 'test.exe',
              signature: {
                features: 'singed'
              }
            }
          }
        ]
      }
    }
  }));
  assert.equal(result[0].signature, 'singed');

  result = enrichedDllData(Immutable.from({
    endpoint: {
      process: {
        dllList: [
          {
            fileProperties: {
              fileName: 'test.exe',
              signature: {
                features: 'singed',
                signer: 'Apple'
              }
            }
          }
        ]
      }
    }
  }));
  assert.equal(result[0].signature, 'singed,Apple');
});
test('enrichedDllData empty dll list', function(assert) {

  const result = enrichedDllData(Immutable.from({
    endpoint: {
      process: {
        dllList: []
      }
    }
  }));
  assert.equal(result.length, 0);
});

test('enrichedDllData empty fileProperty list', function(assert) {

  const result = enrichedDllData(Immutable.from({
    endpoint: {
      process: {
        dllList: [
          {
            name: 'test'
          }
        ]
      }
    }
  }));
  assert.equal(result.length, 0);
});

test('processList', function(assert) {

  const result = processList(Immutable.from({
    endpoint: {
      explore: {
        selectedTab: 'process'
      },
      process: {
        processTree: [
          {
            id: 1,
            parentPid: 0,
            pid: 1,
            name: 'test',
            checksumSha256: 1,
            childProcesses: [
              {
                id: 3,
                parentPid: 1,
                pid: 3,
                name: 'test',
                checksumSha256: 111
              },
              {
                id: 4,
                parentPid: 1,
                pid: 4,
                name: 'test',
                checksumSha256: 1
              }
            ]
          }
        ],
        processList: [
          {
            id: 1,
            parentPid: 0,
            pid: 1,
            name: 'test',
            checksumSha256: 1
          },
          {
            id: 3,
            parentPid: 2,
            pid: 3,
            name: 'test',
            checksumSha256: 111
          },
          {
            id: 4,
            parentPid: 2,
            pid: 4,
            name: 'test',
            checksumSha256: 1
          }
        ]

      }
    }
  }));
  assert.equal(result.length, 3);
});


test('processTree extract the matching checksum', function(assert) {

  const result = processTree(Immutable.from({
    endpoint: {
      explore: {
        selectedTab: {
          tabName: 'PROCESS',
          checksum: 111
        }
      },
      process: {
        processTree: [
          {
            id: 1,
            parentPid: 0,
            pid: 1,
            name: 'test',
            checksumSha256: 1122,
            childProcesses: [
              {
                id: 3,
                parentPid: 1,
                pid: 3,
                name: 'test',
                checksumSha256: 123
              },
              {
                id: 4,
                parentPid: 1,
                pid: 4,
                name: 'test',
                checksumSha256: 1222,
                childProcesses: [
                  {
                    id: 5,
                    parentPid: 4,
                    pid: 5,
                    name: 'test',
                    checksumSha256: 111
                  }
                ]
              }
            ]
          },
          {
            id: 2,
            parentPid: 0,
            pid: 2,
            name: 'test',
            checksumSha256: 2
          }
        ],
        processList: [
          {
            id: 1,
            parentPid: 0,
            pid: 1,
            name: 'test',
            checksumSha256: 1
          },
          {
            id: 3,
            parentPid: 2,
            pid: 3,
            name: 'test',
            checksumSha256: 113
          },
          {
            id: 4,
            parentPid: 2,
            pid: 4,
            name: 'test',
            checksumSha256: 1
          }
        ]

      }
    }
  }));

  assert.equal(result.length, 3);
});


test('isProcessLoading', function(assert) {

  let result = isProcessLoading(Immutable.from({
    endpoint: {
      process: {
        processDetailsLoading: true,
        isProcessTreeLoading: false
      }
    }
  }));
  assert.equal(result, true);

  result = isProcessLoading(Immutable.from({
    endpoint: {
      process: {
        processDetailsLoading: false,
        isProcessTreeLoading: false
      }
    }
  }));
  assert.equal(result, false);


  result = isProcessLoading(Immutable.from({
    endpoint: {
      process: {
        processDetailsLoading: false,
        isProcessTreeLoading: true
      }
    }
  }));
  assert.equal(result, true);
});

test('isJazzAgent return true when the agentVersion includes 11.1', function(assert) {
  const result = isJazzAgent(Immutable.from({
    endpoint: {
      overview: {
        hostDetails: {
          machine: {
            agentVersion: '11.2.0'
          },
          machineIdentity: {
            agentMode: 'userModeOnly'
          }
        }
      }
    }
  }));
  assert.equal(result, true);
});

test('isJazzAgent return false when the agentVersion does not includes 11.1', function(assert) {
  const result = isJazzAgent(Immutable.from({
    endpoint: {
      overview: {
        hostDetails: {
          machine: {
            agentVersion: '11.2.0'
          },
          machineIdentity: {
            agentMode: 'full'
          }
        }
      }
    }
  }));
  assert.equal(result, false);
});

test('imageHooksData returns revelent processId data', function(assert) {
  const result = imageHooksData(Immutable.from({
    endpoint: {
      process: {
        selectedProcessId: 1392,
        dllList: dllListData
      }
    }
  }));

  const expectedResult = [
    {
      'dllFileName': 'gmodule-2.0.dll',
      'hookFileName': 'gobject-2.0.dll',
      'symbol': 'GenerateObject',
      'type': 'inline'
    },
    {
      'dllFileName': 'gmodule-2.0.dll',
      'hookFileName': 'gobject-2.0.dll',
      'symbol': 'DeleteObject',
      'type': 'inline'
    },
    {
      'dllFileName': 'autoLogon.dll',
      'hookFileName': 'kernel32.dll',
      'symbol': 'CreateFileA',
      'type': 'inline'
    },
    {
      'dllFileName': 'autoLogon.dll',
      'hookFileName': 'Kernel32.dll',
      'symbol': 'ReadFile',
      'type': 'inline'
    },
    {
      'dllFileName': 'autoLogon.dll',
      'hookFileName': 'ntdll.dll',
      'symbol': 'NtConnect',
      'type': 'inline'
    }
  ];

  assert.deepEqual(result, expectedResult, 'Relevent image hooks selected');
});

test('imageHooksData returns empty array as no revelent hooks present', function(assert) {
  const result = imageHooksData(Immutable.from({
    endpoint: {
      process: {
        selectedProcessId: 1392,
        dllList: [{
          id: '5b335ffbb24959156220f9d1',
          machineOsType: 'windows',
          machineName: 'REMDBGDRV',
          machineAgentId: 'D4259B02-1EDB-53CE-56C0-8A03110C4D88',
          agentVersion: '11.2.0.0',
          scanStartTime: 1530093476248,
          checksumSha256: 'b4316a7e8c9139e18e079f94539bced78086c8dce1f90f52712f248cd5bdf03e',
          path: 'C:\\Program Files\\VMware\\VMware Tools\\plugins\\vmsvc\\',
          pathH8: 0,
          fileName: 'diskWiper.dll',
          timeCreated: 1489717882000,
          timeModified: 1489717882000,
          timeAccessed: 1496406783408,
          attributes: [
            'archive'
          ],
          mode: 0,
          sameDirectoryFileCounts: {
            nonExe: 0,
            exe: 13,
            subFolder: 0,
            exeSameCompany: 13,
            hiddenFiles: 0
          },
          fileFeatures: [
            'found'
          ],
          directoryFeatures: [
            'programFiles',
            'installer'
          ],
          userModeFeatures: [
            'loaded',
            'image'
          ],
          windows: {
            dlls: [
              {
                pid: 1392,
                imageBase: 8791675043840,
                createTime: 1530092987886,
                eprocess: '0xFFFFFA801A64D4E0',
                imageSize: 32768
              }
            ]
          },
          fileProperties: {
            id: 'b4316a7e8c9139e18e079f94539bced78086c8dce1f90f52712f248cd5bdf03e',
            firstFileName: 'diskWiper.dll',
            firstSeenTime: 1530093476248,
            machineOsType: 'windows',
            signature: {
              timeStamp: 1489762881000,
              thumbprint: 'cef16150c61b0a1cee14a7c5d4cff80f645a6ab7',
              features: [
                'signed',
                'valid'
              ],
              signer: 'VMware, Inc.'
            },
            size: 30184,
            checksumMd5: '19b4420f501c13fad92f619bb45539b6',
            checksumSha1: 'ebfeb4a707b80036aba6bda6003d84739c893d79',
            checksumSha256: 'b4316a7e8c9139e18e079f94539bced78086c8dce1f90f52712f248cd5bdf03e',
            pe: {
              timeStamp: 1489760469000,
              imageSize: 32768,
              numberOfExportedFunctions: 1,
              numberOfNamesExported: 1,
              numberOfExecuteWriteSections: 0,
              features: [
                'dll',
                'pe64',
                'versionInfoPresent',
                'resourceDirectoryPresent',
                'relocationDirectoryPresent',
                'debugDirectoryPresent',
                'richSignaturePresent',
                'relocationDirectoryPresent',
                'companyNameContainsText',
                'fileDescriptionContainsText',
                'fileVersionContainsText',
                'internalNameContainsText',
                'legalCopyrightContainsText',
                'originalFilenameContainsText',
                'productNameContainsText',
                'productVersionContainsText',
                'standardVersionMetaPresent'
              ],
              resources: {
                originalFileName: 'diskWiper.dll',
                company: 'VMware, Inc.',
                description: 'VMware Tools diskWiper plugin'
              },
              sectionNames: [
                '.text',
                '.rdata',
                '.data',
                '.pdata',
                '.rsrc',
                '.reloc'
              ],
              importedLibraries: [
                'MSVCR90.dll',
                'glib-2.0.dll',
                'vmtools.dll',
                'gobject-2.0.dll',
                'KERNEL32.dll'
              ]
            },
            entropy: 6.762114502528325,
            format: 'pe'
          }
        },
        {
          id: '5b335ffbb24959156220f9d2',
          machineOsType: 'windows',
          machineName: 'REMDBGDRV',
          machineAgentId: 'D4259B02-1EDB-53CE-56C0-8A03110C4D88',
          agentVersion: '11.2.0.0',
          scanStartTime: 1530093476248,
          checksumSha256: 'cd32794801d132aee751cee52c1623b86caa26c0505b1108aab83061737a6a0e',
          path: 'C:\\Program Files\\VMware\\VMware Tools\\',
          pathH8: 0,
          fileName: 'deployPkg.dll',
          timeCreated: 1489717580000,
          timeModified: 1489717580000,
          timeAccessed: 1496406783237,
          attributes: [
            'archive'
          ],
          mode: 0,
          sameDirectoryFileCounts: {
            nonExe: 10,
            exe: 35,
            subFolder: 7,
            exeSameCompany: 15,
            hiddenFiles: 0
          },
          fileFeatures: [
            'found'
          ],
          directoryFeatures: [
            'programFiles',
            'installer'
          ],
          userModeFeatures: [
            'loaded',
            'image'
          ],
          windows: {
            dlls: [
              {
                pid: 1392,
                imageBase: 8791675174912,
                createTime: 1530092987886,
                eprocess: '0xFFFFFA801A64D4E0',
                imageSize: 1429504
              }
            ]
          },
          fileProperties: {
            id: 'cd32794801d132aee751cee52c1623b86caa26c0505b1108aab83061737a6a0e',
            firstFileName: 'deployPkg.dll',
            firstSeenTime: 1530093476248,
            machineOsType: 'windows',
            signature: {
              timeStamp: 1483553746000,
              thumbprint: 'cef16150c61b0a1cee14a7c5d4cff80f645a6ab7',
              features: [
                'signed',
                'valid'
              ],
              signer: 'VMware, Inc.'
            },
            size: 1402696,
            checksumMd5: '49e18d16b51863b5a2bcd945df16ef51',
            checksumSha1: '646b13d9418dabdf98a2f4ab3efa3bfae6c66dd8',
            checksumSha256: 'cd32794801d132aee751cee52c1623b86caa26c0505b1108aab83061737a6a0e',
            pe: {
              timeStamp: 1483553399000,
              imageSize: 1429504,
              numberOfExportedFunctions: 338,
              numberOfNamesExported: 338,
              numberOfExecuteWriteSections: 0,
              features: [
                'dll',
                'pe64',
                'versionInfoPresent',
                'resourceDirectoryPresent',
                'relocationDirectoryPresent',
                'debugDirectoryPresent',
                'tlsDirectoryPresent',
                'richSignaturePresent',
                'relocationDirectoryPresent',
                'companyNameContainsText',
                'fileDescriptionContainsText',
                'fileVersionContainsText',
                'internalNameContainsText',
                'legalCopyrightContainsText',
                'originalFilenameContainsText',
                'productNameContainsText',
                'productVersionContainsText',
                'standardVersionMetaPresent'
              ],
              resources: {
                originalFileName: 'deployPkg.dll',
                company: 'VMware, Inc.',
                description: 'VMware Deployment Package Library'
              },
              sectionNames: [
                '.text',
                '.rdata',
                '.data',
                '.pdata',
                '.tls',
                '.rsrc',
                '.reloc'
              ],
              importedLibraries: [
                'ADVAPI32.dll',
                'WS2_32.dll',
                'SHELL32.dll',
                'MSVCP90.dll',
                'MSVCR90.dll',
                'KERNEL32.dll',
                'USER32.dll',
                'VERSION.dll'
              ]
            },
            entropy: 6.2075991890620985,
            format: 'pe'
          }
        }]
      }
    }
  }));

  const expectedResult = [];

  assert.deepEqual(result, expectedResult, 'No Relevent image hooks present so empty array returned');
});

test('imageHooksData returns [] if dllList is empty', function(assert) {
  const result = imageHooksData(Immutable.from({
    endpoint: {
      process: {
        selectedProcessId: 1392,
        dllList: []
      }
    }
  }));

  assert.deepEqual(result, [], 'Rreturns [] if dllList is empty');
});
