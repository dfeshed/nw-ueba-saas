export default {
  hooks: {
    hooks_1: {
      id: 'hooks_1',
      machineOsType: 'windows',
      machineName: 'WIN-BKA6OVQGQI0',
      machineAgentId: '3B1C9364-F6D1-6E1F-0552-B0F85F72AA70',
      agentVersion: '11.3.0.0',
      scanStartTime: 1530868761880,
      checksumSha256: '0000000000000000000000000000000000000000000000000000000000000000',
      path: '',
      pathH8: 0,
      fileName: '[FLOATING_CODE]',
      mode: 0,
      sameDirectoryFileCounts: {
        nonExe: 0,
        exe: 0,
        subFolder: 0,
        exeSameCompany: 0,
        hiddenFiles: 0
      },
      userModeFeatures: [
        'hookInline'
      ],
      windows: {
        hooks: [
          {
            type: 'inline',
            features: [
              'userMode',
              'floating',
              'onFunctionBounary'
            ],
            jumpCount: 1,
            jumpTo: '0x7FEFD23F000',
            process: {
              pid: 3844,
              fileName: 'HookTest_x64.exe',
              createUtcTime: '2018-07-06T09:19:08.230Z'
            },
            hookLocation: {
              checksumSha256: '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
              path: 'C:\\Windows\\System32\\',
              fileName: 'crypt32.dll',
              section: '.text',
              sectionBase: 8791750086656,
              imageBase: '0x7FEFD240000',
              imageSize: 1470464,
              address: '0x7FEFD271740',
              symbol: 'CertOpenSystemStoreW',
              symbolOffset: 0
            },
            inlinePatch: {
              originalBytes: '48895c2408',
              originalAsm: [
                '48895c2408       ; MOV [RSP+0x8], RBX'
              ],
              currentBytes: 'e9bbd8fcff',
              currentAsm: [
                'e9bbd8fcff       ; JMP 0x7fefd23f000'
              ]
            },
            id: 'hooks_1',
            fileId: '5b3f348cb249594f465125f2'
          }
        ]
      },
      hooks: [
        {
          type: 'inline',
          features: [
            'userMode',
            'floating',
            'onFunctionBounary'
          ],
          jumpCount: 1,
          jumpTo: '0x7FEFD23F000',
          process: {
            pid: 3844,
            fileName: 'HookTest_x64.exe',
            createUtcTime: '2018-07-06T09:19:08.230Z'
          },
          hookLocation: {
            checksumSha256: '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
            path: 'C:\\Windows\\System32\\',
            fileName: 'crypt32.dll',
            section: '.text',
            sectionBase: 8791750086656,
            imageBase: '0x7FEFD240000',
            imageSize: 1470464,
            address: '0x7FEFD271740',
            symbol: 'CertOpenSystemStoreW',
            symbolOffset: 0
          },
          inlinePatch: {
            originalBytes: '48895c2408',
            originalAsm: [
              '48895c2408       ; MOV [RSP+0x8], RBX'
            ],
            currentBytes: 'e9bbd8fcff',
            currentAsm: [
              'e9bbd8fcff       ; JMP 0x7fefd23f000'
            ]
          },
          id: 'hooks_1',
          fileId: '5b3f348cb249594f465125f2'
        }
      ],
      type: 'inline',
      features: [
        'userMode',
        'floating',
        'onFunctionBounary'
      ],
      jumpCount: 1,
      jumpTo: '0x7FEFD23F000',
      process: {
        pid: 3844,
        fileName: 'HookTest_x64.exe',
        createUtcTime: '2018-07-06T09:19:08.230Z'
      },
      hookLocation: {
        checksumSha256: '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
        path: 'C:\\Windows\\System32\\',
        fileName: 'crypt32.dll',
        section: '.text',
        sectionBase: 8791750086656,
        imageBase: '0x7FEFD240000',
        imageSize: 1470464,
        address: '0x7FEFD271740',
        symbol: 'CertOpenSystemStoreW',
        symbolOffset: 0
      },
      inlinePatch: {
        originalBytes: '48895c2408',
        originalAsm: [
          '48895c2408       ; MOV [RSP+0x8], RBX'
        ],
        currentBytes: 'e9bbd8fcff',
        currentAsm: [
          'e9bbd8fcff       ; JMP 0x7fefd23f000'
        ]
      },
      fileId: '5b3f348cb249594f465125f2'
    },
    hooks_3: {
      id: 'hooks_3',
      machineOsType: 'windows',
      machineName: 'WIN-BKA6OVQGQI0',
      machineAgentId: '3B1C9364-F6D1-6E1F-0552-B0F85F72AA70',
      agentVersion: '11.3.0.0',
      scanStartTime: 1530868761880,
      checksumSha256: '5469da9747d23abd3a1ccffa2bccfe6256938a416f707ed2160d3eda3867c30d',
      path: 'C:\\Users\\kslp\\AppData\\Local\\Temp\\',
      pathH8: 0,
      fileName: 'HookTest_DLL64_0f04.dll',
      timeCreated: 1530868748292,
      timeModified: 1469610156000,
      timeAccessed: 1530868748292,
      attributes: [
        'archive',
        'notContentIndexed'
      ],
      mode: 0,
      sameDirectoryFileCounts: {
        nonExe: 14,
        exe: 4,
        subFolder: 4,
        exeSameCompany: 0,
        hiddenFiles: 0
      },
      fileFeatures: [
        'found'
      ],
      directoryFeatures: [
        'temporary',
        'user',
        'appDataLocal'
      ],
      userModeFeatures: [
        'loaded',
        'hookInline',
        'image'
      ],
      windows: {
        dlls: [
          {
            pid: 3844,
            imageBase: 8791599218688,
            createTime: 1530868748230,
            eprocess: '0xFFFFFA801A75D7D0',
            imageSize: 172032
          }
        ],
        hooks: [
          {
            type: 'inline',
            features: [
              'userMode',
              'onFunctionBounary'
            ],
            jumpCount: 0,
            jumpTo: '0x0',
            process: {
              pid: 3844,
              fileName: 'HookTest_x64.exe',
              createUtcTime: '2018-07-06T09:19:08.230Z'
            },
            hookLocation: {
              checksumSha256: '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
              path: 'C:\\Windows\\System32\\',
              fileName: 'crypt32.dll',
              section: '.text',
              sectionBase: 8791750086656,
              imageBase: '0x7FEFD240000',
              imageSize: 1470464,
              address: '0x7FEFD26207C',
              symbol: 'CertDuplicateStore',
              symbolOffset: 0
            },
            inlinePatch: {
              originalBytes: 'f083410401',
              originalAsm: [
                'f083410401       ; LOCK ADD DWORD [RCX+0x4], 0x1'
              ],
              currentBytes: 'e97fcffbff',
              currentAsm: [
                'e97fcffbff       ; JMP 0x7fefd21f000'
              ]
            },
            id: 'hooks_3',
            fileId: '5b3f348cb249594f4651281b'
          }
        ]
      },
      fileProperties: {
        id: '5469da9747d23abd3a1ccffa2bccfe6256938a416f707ed2160d3eda3867c30d',
        firstFileName: 'HookTest_DLL64_061c.dll',
        firstSeenTime: 1530681572797,
        machineOsType: 'windows',
        signature: {
          features: [
            'unsigned'
          ]
        },
        size: 150528,
        checksumMd5: 'a91f3390e2fadbbcb2a347ba685cb22a',
        checksumSha1: '5c6ff89eef54b7d5fba72889c2250ee09b04bcab',
        checksumSha256: '5469da9747d23abd3a1ccffa2bccfe6256938a416f707ed2160d3eda3867c30d',
        pe: {
          timeStamp: 1450123256000,
          imageSize: 172032,
          numberOfExportedFunctions: 5,
          numberOfNamesExported: 5,
          numberOfExecuteWriteSections: 0,
          features: [
            'dll',
            'pe64',
            'resourceDirectoryPresent',
            'relocationDirectoryPresent',
            'debugDirectoryPresent',
            'richSignaturePresent',
            'relocationDirectoryPresent'
          ],
          resources: {
            originalFileName: '',
            company: '',
            description: ''
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
            'dbghelp.dll',
            'KERNEL32.dll',
            'WS2_32.dll',
            'SHLWAPI.dll',
            'imagehlp.dll',
            'WINHTTP.dll',
            'ADVAPI32.dll'
          ]
        },
        entropy: 6.189471250232795,
        format: 'pe'
      },
      hooks: [
        {
          type: 'inline',
          features: [
            'userMode',
            'onFunctionBounary'
          ],
          jumpCount: 0,
          jumpTo: '0x0',
          process: {
            pid: 3844,
            fileName: 'HookTest_x64.exe',
            createUtcTime: '2018-07-06T09:19:08.230Z'
          },
          hookLocation: {
            checksumSha256: '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
            path: 'C:\\Windows\\System32\\',
            fileName: 'crypt32.dll',
            section: '.text',
            sectionBase: 8791750086656,
            imageBase: '0x7FEFD240000',
            imageSize: 1470464,
            address: '0x7FEFD26207C',
            symbol: 'CertDuplicateStore',
            symbolOffset: 0
          },
          inlinePatch: {
            originalBytes: 'f083410401',
            originalAsm: [
              'f083410401       ; LOCK ADD DWORD [RCX+0x4], 0x1'
            ],
            currentBytes: 'e97fcffbff',
            currentAsm: [
              'e97fcffbff       ; JMP 0x7fefd21f000'
            ]
          },
          id: 'hooks_3',
          fileId: '5b3f348cb249594f4651281b'
        }
      ],
      type: 'inline',
      features: [
        'userMode',
        'onFunctionBounary'
      ],
      jumpCount: 0,
      jumpTo: '0x0',
      process: {
        pid: 3844,
        fileName: 'HookTest_x64.exe',
        createUtcTime: '2018-07-06T09:19:08.230Z'
      },
      hookLocation: {
        checksumSha256: '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
        path: 'C:\\Windows\\System32\\',
        fileName: 'crypt32.dll',
        section: '.text',
        sectionBase: 8791750086656,
        imageBase: '0x7FEFD240000',
        imageSize: 1470464,
        address: '0x7FEFD26207C',
        symbol: 'CertDuplicateStore',
        symbolOffset: 0
      },
      inlinePatch: {
        originalBytes: 'f083410401',
        originalAsm: [
          'f083410401       ; LOCK ADD DWORD [RCX+0x4], 0x1'
        ],
        currentBytes: 'e97fcffbff',
        currentAsm: [
          'e97fcffbff       ; JMP 0x7fefd21f000'
        ]
      },
      fileId: '5b3f348cb249594f4651281b',
      signature: [
        'unsigned'
      ]
    },
    hooks_4: {
      id: 'hooks_4',
      machineOsType: 'windows',
      machineName: 'WIN-BKA6OVQGQI0',
      machineAgentId: '3B1C9364-F6D1-6E1F-0552-B0F85F72AA70',
      agentVersion: '11.3.0.0',
      scanStartTime: 1530868761880,
      checksumSha256: '5e99949211beab5f6d72e66a2f1e40e0ada5ab4d9d1b29b60e5952dddfe4551f',
      path: '',
      pathH8: 0,
      fileName: '[MEMORY_DLL_EF01C6AE31431AA02BE6DF1EA764A3D4]',
      mode: 0,
      sameDirectoryFileCounts: {
        nonExe: 0,
        exe: 0,
        subFolder: 0,
        exeSameCompany: 0,
        hiddenFiles: 0
      },
      userModeFeatures: [
        'loaded',
        'privateMemory'
      ],
      processFeatures: [
        'hiddenDifferentialView'
      ],
      windows: {
        dlls: [
          {
            pid: 3844,
            imageBase: 8792632721408,
            createTime: 1530868748230,
            eprocess: '0xFFFFFA801A75D7D0',
            imageSize: 192512
          }
        ],
        hooks: [
          {
            type: 'inline',
            features: [
              'userMode',
              'floating',
              'onFunctionBounary',
              'trampoline'
            ],
            jumpCount: 2,
            jumpTo: '0x7FF31C08C34',
            process: {
              pid: 3844,
              fileName: 'HookTest_x64.exe',
              createUtcTime: '2018-07-06T09:19:08.230Z'
            },
            hookLocation: {
              checksumSha256: '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
              path: 'C:\\Windows\\System32\\',
              fileName: 'crypt32.dll',
              section: '.text',
              sectionBase: 8791750086656,
              imageBase: '0x7FEFD240000',
              imageSize: 1470464,
              address: '0x7FEFD24B390',
              symbol: 'CertGetCRLContextProperty',
              symbolOffset: 0
            },
            inlinePatch: {
              originalBytes: '488bc133c9',
              originalAsm: [
                '488bc1           ; MOV RAX, RCX',
                '33c9             ; XOR ECX, ECX'
              ],
              currentBytes: 'e96b3cfeff',
              currentAsm: [
                'e96b3cfeff       ; JMP 0x7fefd22f000'
              ]
            },
            id: 'hooks_4',
            fileId: '5b3f348cb249594f4651281c'
          }
        ]
      },
      fileProperties: {
        id: '5e99949211beab5f6d72e66a2f1e40e0ada5ab4d9d1b29b60e5952dddfe4551f',
        firstFileName: '[MEMORY_DLL_EF01C6AE31431AA02BE6DF1EA764A3D4]',
        firstSeenTime: 1530681572797,
        machineOsType: 'windows',
        signature: {
          features: [
            'unsigned'
          ]
        },
        size: 0,
        checksumMd5: 'ef01c6ae31431aa02be6df1ea764a3d4',
        checksumSha1: '53f32db2c7221bdcf55be69387ec6ebffe1daa02',
        checksumSha256: '5e99949211beab5f6d72e66a2f1e40e0ada5ab4d9d1b29b60e5952dddfe4551f',
        pe: {
          timeStamp: 1247535075000,
          imageSize: 192512,
          numberOfExportedFunctions: 10,
          numberOfNamesExported: 10,
          numberOfExecuteWriteSections: 0,
          features: [
            'dll',
            'pe64',
            'memoryHash',
            'iconPresent',
            'versionInfoPresent',
            'resourceDirectoryPresent',
            'relocationDirectoryPresent',
            'debugDirectoryPresent',
            'boundImportDirectoryPresent',
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
            originalFileName: 'keymgr.dll',
            company: 'Microsoft Corporation',
            description: 'Stored User Names and Passwords'
          },
          sectionNames: [
            '.text',
            '.data',
            '.pdata',
            '.rsrc',
            '.reloc'
          ],
          importedLibraries: [
            'msvcrt.dll',
            'netutils.dll',
            'ADVAPI32.dll',
            'KERNEL32.dll',
            'ntdll.dll',
            'USER32.dll',
            'SHELL32.dll',
            'ole32.dll',
            'GDI32.dll',
            'SHLWAPI.dll',
            'CRYPT32.dll',
            'RPCRT4.dll'
          ]
        },
        entropy: 0,
        format: 'pe'
      },
      hooks: [
        {
          type: 'inline',
          features: [
            'userMode',
            'floating',
            'onFunctionBounary',
            'trampoline'
          ],
          jumpCount: 2,
          jumpTo: '0x7FF31C08C34',
          process: {
            pid: 3844,
            fileName: 'HookTest_x64.exe',
            createUtcTime: '2018-07-06T09:19:08.230Z'
          },
          hookLocation: {
            checksumSha256: '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
            path: 'C:\\Windows\\System32\\',
            fileName: 'crypt32.dll',
            section: '.text',
            sectionBase: 8791750086656,
            imageBase: '0x7FEFD240000',
            imageSize: 1470464,
            address: '0x7FEFD24B390',
            symbol: 'CertGetCRLContextProperty',
            symbolOffset: 0
          },
          inlinePatch: {
            originalBytes: '488bc133c9',
            originalAsm: [
              '488bc1           ; MOV RAX, RCX',
              '33c9             ; XOR ECX, ECX'
            ],
            currentBytes: 'e96b3cfeff',
            currentAsm: [
              'e96b3cfeff       ; JMP 0x7fefd22f000'
            ]
          },
          id: 'hooks_4',
          fileId: '5b3f348cb249594f4651281c'
        }
      ],
      type: 'inline',
      features: [
        'userMode',
        'floating',
        'onFunctionBounary',
        'trampoline'
      ],
      jumpCount: 2,
      jumpTo: '0x7FF31C08C34',
      process: {
        pid: 3844,
        fileName: 'HookTest_x64.exe',
        createUtcTime: '2018-07-06T09:19:08.230Z'
      },
      hookLocation: {
        checksumSha256: '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
        path: 'C:\\Windows\\System32\\',
        fileName: 'crypt32.dll',
        section: '.text',
        sectionBase: 8791750086656,
        imageBase: '0x7FEFD240000',
        imageSize: 1470464,
        address: '0x7FEFD24B390',
        symbol: 'CertGetCRLContextProperty',
        symbolOffset: 0
      },
      inlinePatch: {
        originalBytes: '488bc133c9',
        originalAsm: [
          '488bc1           ; MOV RAX, RCX',
          '33c9             ; XOR ECX, ECX'
        ],
        currentBytes: 'e96b3cfeff',
        currentAsm: [
          'e96b3cfeff       ; JMP 0x7fefd22f000'
        ]
      },
      fileId: '5b3f348cb249594f4651281c',
      signature: [
        'unsigned'
      ]
    }
  },
  hooksLoadingStatus: 'completed',
  selectedRowId: null
};
