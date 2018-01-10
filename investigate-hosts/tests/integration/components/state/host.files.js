export default {
  files: [
    {
      machineOsType: 'linux',
      machineName: 'HarpServer',
      machineAgentId: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      agentVersion: '1.0.0.0',
      scanStartTime: '2017-07-11T05:42:54.000Z',
      checksumSha256: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
      path: '/usr/lib/systemd/system',
      pathH8: '1778501902604433296',
      fileName: 'systemd-journald.service',
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
    {
      machineOsType: 'linux',
      machineName: 'HarpServer',
      machineAgentId: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      agentVersion: '1.0.0.0',
      scanStartTime: '2017-07-11T05:42:54.000Z',
      checksumSha256: 'a565da7cb9d42a77f300b3eaeeddf73fab4293e442737fe453373ddc2fedf2be',
      path: '/usr/lib/modules/3.10.0-229.20.1.el7.x86_64/kernel/drivers/ata',
      pathH8: '8325233813913331491',
      fileName: 'ata_generic.ko',
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
    },
    {
      machineOsType: 'linux',
      machineName: 'HarpServer',
      machineAgentId: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      agentVersion: '1.0.0.0',
      scanStartTime: '2017-07-11T05:42:54.000Z',
      checksumSha256: '29772d95b2488a5a7715a96270f827fbf2b2e1e0a627ae041a54a6faddd2686c',
      path: '/usr/lib/systemd/system',
      pathH8: '1778501902604433296',
      fileName: 'rhel-readonly.service',
      owner: {
        username: 'root',
        groupname: 'root',
        uid: 0,
        gid: 0
      },
      timeModified: '2015-01-15T08:57:03.000Z',
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
            name: 'rhel-readonly.service',
            description: 'Configure read-only root support',
            state: 'loaded-active-exited',
            pid: 1697473911
          },
          {
            systemdPathH8: 0,
            name: 'rhel-readonly.service',
            description: 'Configure read-only root support',
            state: 'loaded-active-exited',
            pid: 1697473911
          }
        ]
      },
      fileProperties: {
        id: '29772d95b2488a5a7715a96270f827fbf2b2e1e0a627ae041a54a6faddd2686c',
        firstFileName: 'rhel-readonly.service',
        machineOsType: 'linux',
        size: 366,
        checksumMd5: 'c0ddd7db5cd26fbacc986119659cb34c',
        checksumSha1: 'bb74d291a2f40d6d9f2effb3c618d52267f4cfbb',
        checksumSha256: '29772d95b2488a5a7715a96270f827fbf2b2e1e0a627ae041a54a6faddd2686c',
        entropy: 4.682791635812802,
        format: 'script'
      }
    },
    {
      machineOsType: 'linux',
      machineName: 'HarpServer',
      machineAgentId: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      agentVersion: '1.0.0.0',
      scanStartTime: '2017-07-11T05:42:54.000Z',
      checksumSha256: 'b11c3d093d413fe7c9afd7252668e91f0b8e64065613e50ce20fc42ea2398b0a',
      path: '/usr/lib/modules/3.10.0-229.20.1.el7.x86_64/kernel/drivers/i2c/busses',
      pathH8: '4840059083571355663',
      fileName: 'i2c-piix4.ko',
      owner: {
        username: 'root',
        groupname: 'root',
        uid: 0,
        gid: 0
      },
      timeModified: '2015-11-03T19:49:25.000Z',
      timeAccessed: '2017-07-11T05:42:55.000Z',
      mode: 33188,
      sameDirectoryFileCounts: {
        nonExe: 0,
        exe: 21,
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
            imageSize: 22106,
            numberOfInstances: 0,
            loadState: 'Live',
            dependencies: [
              '-'
            ],
            author: 'Frodo Looijaard <frodol@dds.nl> and Philip Edelbrock <phil@netroedge.com>',
            description: 'PIIX4 SMBus driver',
            sourceVersion: '4D5C0600215ABD5B7EB1F7A',
            versionMagic: '3.10.0-229.20.1.el7.x86_64 SMP mod_unload modversions'
          }
        ]
      },
      fileProperties: {
        id: 'b11c3d093d413fe7c9afd7252668e91f0b8e64065613e50ce20fc42ea2398b0a',
        firstFileName: 'i2c-piix4.ko',
        machineOsType: 'linux',
        size: 27685,
        checksumMd5: 'ccd82fc4fe95d2ca1b804e850d75e2e4',
        checksumSha1: 'f035d3e5445af8c4a643e93edf4bca62888a1bc5',
        checksumSha256: 'b11c3d093d413fe7c9afd7252668e91f0b8e64065613e50ce20fc42ea2398b0a',
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
        entropy: 3.8188263802458438,
        format: 'elf'
      }
    },
    {
      machineOsType: 'linux',
      machineName: 'HarpServer',
      machineAgentId: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      agentVersion: '1.0.0.0',
      scanStartTime: '2017-07-11T05:42:54.000Z',
      checksumSha256: '1217fa0aca70a546e80497a00f4aabaee9f282740b2f0dc823b1d69a5392e2ca',
      path: '/usr/lib/systemd/system',
      pathH8: '1778501902604433296',
      fileName: 'lvm2-monitor.service',
      owner: {
        username: 'root',
        groupname: 'root',
        uid: 0,
        gid: 0
      },
      timeModified: '2015-06-23T22:10:15.000Z',
      timeAccessed: '2017-07-11T03:40:40.000Z',
      mode: 33060,
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
            name: 'lvm2-monitor.service',
            description: 'Monitoring of LVM2 mirrors, snapshots etc. using dmeventd or progress polling',
            state: 'loaded-active-exited',
            pid: 1697473911
          },
          {
            systemdPathH8: 0,
            name: 'lvm2-monitor.service',
            description: 'Monitoring of LVM2 mirrors, snapshots etc. using dmeventd or progress polling',
            state: 'loaded-active-exited',
            pid: 1697473911
          }
        ]
      },
      fileProperties: {
        id: '1217fa0aca70a546e80497a00f4aabaee9f282740b2f0dc823b1d69a5392e2ca',
        firstFileName: 'lvm2-monitor.service',
        machineOsType: 'linux',
        size: 645,
        checksumMd5: '8d2b29b08fddb14fa95833b0a2b1fe1b',
        checksumSha1: '08883946c3e0de4e879d4598c206f4ff9acd024a',
        checksumSha256: '1217fa0aca70a546e80497a00f4aabaee9f282740b2f0dc823b1d69a5392e2ca',
        entropy: 5.108615774151603,
        format: 'script'
      }
    },
    {
      machineOsType: 'linux',
      machineName: 'HarpServer',
      machineAgentId: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      agentVersion: '1.0.0.0',
      scanStartTime: '2017-07-11T05:42:54.000Z',
      checksumSha256: '3621db9f17a28b4d921b75dab20a970a6b312da246b74aa77452194c09c50f48',
      path: '/usr/lib/modules/3.10.0-229.20.1.el7.x86_64/kernel/drivers/gpu/drm',
      pathH8: '-3673784228052374458',
      fileName: 'drm_kms_helper.ko',
      owner: {
        username: 'root',
        groupname: 'root',
        uid: 0,
        gid: 0
      },
      timeModified: '2015-11-03T19:49:13.000Z',
      timeAccessed: '2017-07-11T05:42:55.000Z',
      mode: 33188,
      sameDirectoryFileCounts: {
        nonExe: 0,
        exe: 4,
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
            imageSize: 98226,
            numberOfInstances: 1,
            loadState: 'Live',
            dependencies: [
              'vmwgfx',
              ''
            ],
            author: 'David Airlie, Jesse Barnes',
            description: 'DRM KMS helper',
            sourceVersion: '5C7B7263DE3E45EF6BFA0FA',
            versionMagic: '3.10.0-229.20.1.el7.x86_64 SMP mod_unload modversions'
          }
        ]
      },
      fileProperties: {
        id: '3621db9f17a28b4d921b75dab20a970a6b312da246b74aa77452194c09c50f48',
        firstFileName: 'drm_kms_helper.ko',
        machineOsType: 'linux',
        size: 147381,
        checksumMd5: '78f19471bcdcc755bad328a6c0918fb1',
        checksumSha1: '7ba9e236e1dcbe9cc889f5d62d6f647847c95d9f',
        checksumSha256: '3621db9f17a28b4d921b75dab20a970a6b312da246b74aa77452194c09c50f48',
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
        entropy: 4.817198900068834,
        format: 'elf'
      }
    }
  ],
  selectedFileHash: 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
  pageNumber: 10,
  totalItems: 500,
  sortField: 'fileName',
  isDescOrder: false,
  filesLoadingStatus: 'completed',
  filesLoadMoreStatus: 'stopped'
};