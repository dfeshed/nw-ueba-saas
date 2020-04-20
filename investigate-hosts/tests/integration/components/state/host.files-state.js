export default {
  fileContext: {
    files_1: {
      id: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
      machineOsType: 'linux',
      machineName: 'HarpServer',
      machineAgentId: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      agentVersion: '1.0.0.0',
      scanStartTime: '2017-07-11T05:42:54.000Z',
      checksumSha256: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
      path: '/usr/lib/systemd/system',
      pathH8: '1778501902604433296',
      fileName: 'systemd-journald.service',
      deleted: true,
      owner: {
        username: 'root',
        groupname: 'root',
        uid: 0,
        gid: 0
      },
      timeModified: '2015-09-15T13:21:10.000Z',
      timeAccessed: '2017-07-11T03:40:40.000Z',
      mode: 33188,
      sameDirectoryFileCounts: {
        nonExe: 193,
        exe: 1,
        subFolder: 0,
        exeSameCompany: 0,
        hiddenFiles: 0
      },
      directoryFeatures: [
        'usr'
      ],
      linux: {
        systemds: [
          {
            systemdPathH8: 0,
            name: 'systemd-journald.service',
            description: 'Journal Service',
            state: 'loaded-active-running',
            pid: 1697473911,
            triggeredBy: [
              'systemd-journald.socket'
            ],
            triggerStrings: [
              'Stream=/run/systemd/journal/stdout',
              'Datagram=/run/systemd/journal/socket',
              'Datagram=/dev/log'
            ]
          },
          {
            systemdPathH8: 0,
            name: 'systemd-journald.service',
            description: 'Journal Service',
            state: 'loaded-active-running',
            pid: 1697473911,
            triggeredBy: [
              'systemd-journald.socket'
            ],
            triggerStrings: [
              'Stream=/run/systemd/journal/stdout',
              'Datagram=/run/systemd/journal/socket',
              'Datagram=/dev/log'
            ]
          }
        ]
      },
      fileProperties: {
        id: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
        firstFileName: 'systemd-journald.service',
        machineOsType: 'linux',
        size: 940,
        checksumMd5: '11bb579c7fe34ccfb65de2026f6ba71f',
        checksumSha1: 'b91de355f748ea4eb7328ed47d0f4d17d25c6b8a',
        checksumSha256: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
        entropy: 5.197017123035716,
        format: 'script'
      }
    },
    files_2: {
      id: 'a565da7cb9d42a77f300b3eaeeddf73fab4293e442737fe453373ddc2fedf2be',
      machineOsType: 'linux',
      machineName: 'HarpServer',
      machineAgentId: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      agentVersion: '1.0.0.0',
      scanStartTime: '2017-07-11T05:42:54.000Z',
      checksumSha256: 'a565da7cb9d42a77f300b3eaeeddf73fab4293e442737fe453373ddc2fedf2be',
      path: '/usr/lib/modules/3.10.0-229.20.1.el7.x86_64/kernel/drivers/ata',
      pathH8: '8325233813913331491',
      fileName: 'ata_generic.ko',
      deleted: false,
      owner: {
        username: 'root',
        groupname: 'root',
        uid: 0,
        gid: 0
      },
      timeModified: '2015-11-03T19:49:05.000Z',
      timeAccessed: '2017-07-11T05:42:55.000Z',
      mode: 33188,
      sameDirectoryFileCounts: {
        nonExe: 0,
        exe: 50,
        subFolder: 0,
        exeSameCompany: 0,
        hiddenFiles: 0
      },
      fileFeatures: [
        'partOfRPM'
      ],
      directoryFeatures: [
        'usr'
      ],
      rpm: {
        category: 'System Environment/Kernel'
      },
      linux: {
        drivers: [
          {
            imageBase: 9223372036854776000,
            imageSize: 12910,
            numberOfInstances: 0,
            loadState: 'Live',
            dependencies: [
              '-'
            ],
            author: 'Alan Cox',
            description: 'low-level driver for generic ATA',
            sourceVersion: '51672935B1BE74E18674B67',
            versionMagic: '3.10.0-229.20.1.el7.x86_64 SMP mod_unload modversions'
          }
        ]
      },
      fileProperties: {
        id: 'a565da7cb9d42a77f300b3eaeeddf73fab4293e442737fe453373ddc2fedf2be',
        firstFileName: 'ata_generic.ko',
        machineOsType: 'linux',
        size: 13757,
        checksumMd5: 'b48e2ef833bab15a62f128e1293de1bb',
        checksumSha1: '3ce5a7f5bcea2bb3971edfe417f41904f5e84837',
        checksumSha256: 'a565da7cb9d42a77f300b3eaeeddf73fab4293e442737fe453373ddc2fedf2be',
        elf: {
          classType: 0,
          data: 0,
          entryPoint: 0,
          features: [
            'arch64',
            'lkm'
          ],
          type: 1
        },
        entropy: 3.5172264322543323,
        format: 'elf'
      }
    }
  },
  contextLoadingStatus: 'completed',
  contextLoadMoreStatus: null,
  fileContextSelections: [
    {
      id: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
      firstFileName: 'systemd-journald.service',
      machineOsType: 'linux',
      size: 940,
      checksumMd5: '11bb579c7fe34ccfb65de2026f6ba71f',
      checksumSha1: 'b91de355f748ea4eb7328ed47d0f4d17d25c6b8a',
      checksumSha256: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
      entropy: 5.197017123035716,
      format: 'script'
    }
  ],
  selectedRowId: null,
  pageNumber: 10,
  totalItems: 500,
  sortField: 'fileName',
  isDescOrder: false,
  filesLoadingStatus: 'completed',
  filesLoadMoreStatus: 'stopped'
};