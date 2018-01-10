const linux = {
  overview: {
    hostDetails: {
      id: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      machine: {
        machineAgentId: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
        agentVersion: '1.0.0.0',
        machineOsType: 'linux',
        machineName: 'HarpServer',
        scanStartTime: '2017-07-11T05:42:54.000Z',
        scanRequestTime: '2017-07-11T05:42:54.000Z',
        scanType: 'standard',
        scanTrigger: 'manual',
        networkInterfaces: [
          {
            name: 'ens160',
            macAddress: '00:50:56:01:47:01',
            ipv6: ['fe80::250:56ff:fe01:4701'],
            gateway: [
              '0.0.0.0'
            ],
            promiscuous: false
          },
          {
            name: 'ens32',
            macAddress: '00:50:56:01:2B:B5',
            ipv4: ['10.40.15.187'],
            ipv6: ['fe80::250:56ff:fe01:2bb5'],
            gateway: [
              '10.40.12.1'
            ],
            promiscuous: false
          },
          {
            name: 'lo',
            ipv4: ['127.0.0.1'],
            ipv6: ['::1'],
            gateway: [
              '0.0.0.0'
            ],
            promiscuous: false
          }
        ],
        users: [
          {
            name: 'sharms74',
            sessionId: 0,
            host: 'inenbhatr9l1c.corp.emc.com',
            deviceName: 'pts/0',
            administrator: true
          },
          {
            name: 'root',
            sessionId: 1,
            host: 'inkumarp102l1c.corp.emc.com',
            deviceName: 'pts/1',
            administrator: false
          }
        ],
        hostFileEntries: [
          {
            ip: '127.0.0.1',
            hosts: [
              'localhost',
              'localhost.localdomain',
              'localhost4',
              'localhost4.localdomain4'
            ]
          },
          {
            ip: '10.40.15.187',
            hosts: [
              '71x64-001-0'
            ]
          },
          {
            ip: '10.40.5.88',
            hosts: [
              'helloworld.com'
            ]
          }
        ],
        bashHistory: [
          {
            commands: [
              'cat ifcfg-ens32 ',
              'clear'
            ],
            username: 'root'
          },
          {
            commands: [
              'mount /dev/cdrom /mnt/cdrom/',
              'ls /mnt/cdrom/',
              'umount /mnt/cdrom/',
              'cd/'
            ],
            username: 'testUser'
          }
        ],
        mountedPaths: [
          {
            path: '/',
            fileSystem: 'rootfs',
            options: 'rw',
            remotePath: 'rootfs'
          },
          {
            path: '/proc',
            fileSystem: 'proc',
            options: 'rw,nosuid,nodev,noexec,relatime',
            remotePath: 'proc'
          },
          {
            path: '/sys',
            fileSystem: 'sysfs',
            options: 'rw,nosuid,nodev,noexec,relatime',
            remotePath: 'sysfs'
          }
        ]
      },
      machineIdentity: {
        id: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
        group: 'default',
        machineName: 'HarpServer',
        agent: {
          installTime: '2017-06-20T07:09:01.000Z',
          serviceStartTime: '2017-06-28T04:19:45.000Z',
          serviceEprocess: 0,
          serviceProcessId: 0,
          serviceErrorCode: 0,
          driverErrorCode: 0,
          blockingEnabled: false
        },
        operatingSystem: {
          description: 'CentOS Linux 7 (Core)',
          servicePack: 0,
          kernelName: 'Linux',
          kernelRelease: '3.10.0-229.20.1.el7.x86_64',
          kernelVersion: '#1 SMP Tue Nov 3 19:10:07 UTC 2015',
          distribution: 'rpm',
          lastBootTime: '2017-06-28T04:20:16.000Z'
        },
        hardware: {
          processorArchitecture: 'x64',
          processorArchitectureBits: 64,
          processorCount: 1,
          processorName: ' Intel(R) Xeon(R) CPU E5-2697 v3 @ 2.60GHz',
          totalPhysicalMemory: 16659738624,
          manufacturer: 'VMware, Inc.',
          model: 'VMware Virtual Platform',
          serial: 'VMware-42 04 6b 0d 27 37 42 e2-86 fe 90 a3 df 40 44 3e',
          bios: 'Phoenix Technologies LTD'
        },
        lastUpdatedTime: '2017-07-11T05:42:57.021Z',
        agentMode: 'userModeOnly'
      },
      agentStatus: {
        lastSeenTime: 1500091621745,
        scanStatus: 'idle'
      }
    },
    exportJSONStatus: 'completed',
    loadingStatus: 'completed'
  }
};

const windows = {
  overview: {
    hostDetails: {
      id: 'CA527998-8E16-B1D4-5E3E-2140F6AD1DF6',
      machine: {
        machineAgentId: 'CA527998-8E16-B1D4-5E3E-2140F6AD1DF6',
        agentVersion: '11.1.0.0',
        machineOsType: 'windows',
        machineName: 'INENKUMARP10L8C',
        scanStartTime: 1515150192676,
        securityConfigurations: [
          'warningOnZoneCrossingDisabled'
        ],
        networkInterfaces: [
          {
            name: 'Cisco AnyConnect Secure Mobility Client Virtual Miniport Adapter for Windows x64',
            macAddress: '00:05:9A:3C:7A:00',
            networkIdv4: [
              '10.42.32.0'
            ],
            ipv4: [
              '10.42.35.93'
            ],
            ipv6: [
              'fe80::682d:b410:2858:da86%20',
              'fe80::b49f:77fc:71a4:308d%20',
              'fe80::d9dc:777a:518c:3499%20'
            ],
            networkIdv6: [
              'fe80::%20',
              'fe80::%20',
              'fe80::%20'
            ],
            gateway: [
              '::',
              '10.42.32.1'
            ],
            dns: [
              '10.73.241.89',
              '137.69.224.246'
            ],
            promiscuous: false
          },
          {
            name: 'Intel(R) Dual Band Wireless-AC 7260',
            macAddress: '4C:EB:42:AC:5B:98',
            networkIdv4: [
              '192.168.0.0'
            ],
            ipv4: [
              '192.168.0.104'
            ],
            ipv6: [
              'fe80::c549:7b49:adb5:17b%14'
            ],
            networkIdv6: [
              'fe80::%14'
            ],
            gateway: [
              '192.168.0.1'
            ],
            dns: [
              '192.168.0.1'
            ],
            promiscuous: false
          },
          {
            name: 'Cisco AnyConnect Secure Mobility Client Virtual Miniport Adapter for Windows x64',
            macAddress: '00:05:9A:3C:7A:00',
            networkIdv4: [
              '10.42.32.0'
            ],
            ipv4: [
              '10.42.35.93'
            ],
            ipv6: [
              'fe80::682d:b410:2858:da86%20',
              'fe80::b49f:77fc:71a4:308d%20',
              'fe80::d9dc:777a:518c:3499%20'
            ],
            networkIdv6: [
              'fe80::%20',
              'fe80::%20',
              'fe80::%20'
            ],
            gateway: [
              '::',
              '10.42.32.1'
            ],
            dns: [
              '10.73.241.89',
              '137.69.224.246'
            ],
            promiscuous: false
          },
          {
            name: 'Intel(R) Dual Band Wireless-AC 7260',
            macAddress: '4C:EB:42:AC:5B:98',
            networkIdv4: [
              '192.168.0.0'
            ],
            ipv4: [
              '192.168.0.104'
            ],
            ipv6: [
              'fe80::c549:7b49:adb5:17b%14'
            ],
            networkIdv6: [
              'fe80::%14'
            ],
            gateway: [
              '192.168.0.1'
            ],
            dns: [
              '192.168.0.1'
            ],
            promiscuous: false
          }
        ],
        systemPatches: [
          'KB3199986',
          'KB4013418',
          'KB4053577',
          'KB4053579'
        ],
        users: [
          {
            name: 'kumarp102',
            sessionId: 0,
            sessionType: 'interactivenetwork',
            administrator: true
          },
          {
            name: 'kumarp102',
            sessionId: 1,
            sessionType: 'interactivenetwork',
            domainUserQualifiedName: 'CN=Kumar\\, Prashant  Analyt Svc  1,OU=Engineering,OU=IN Bangalore,OU=India,OU=International Users,DC=corp,DC=emc,DC=com',
            domainUserId: '8AC2C6E9-0138-400E-97A0-5AB6D7BA5AAC',
            administrator: true
          }
        ],
        securityProducts: [
          {
            type: 'antiVirus',
            instance: '1006DC03-1FB1-9E52-7C81-F2FAB48962E3',
            displayName: 'McAfee VirusScan Enterprise',
            companyName: '',
            version: '',
            features: [
              'enabled',
              'updated',
              'onAccessScanEnabled'
            ]
          },
          {
            type: 'antiVirus',
            instance: 'D68DDC3A-831F-4FAE-9E44-DA132C1ACF46',
            displayName: 'Windows Defender',
            companyName: '',
            version: '',
            features: [
              'enabled',
              'updated'
            ]
          },
          {
            type: 'antiSpyware',
            instance: 'AB673DE7-398B-91DC-4631-C988CF0E285E',
            displayName: 'McAfee VirusScan Enterprise Antispyware Module',
            companyName: '',
            version: '',
            features: [
              'enabled',
              'updated',
              'onAccessScanEnabled'
            ]
          },
          {
            type: 'antiSpyware',
            instance: 'D68DDC3A-831F-4FAE-9E44-DA132C1ACF46',
            displayName: 'Windows Defender',
            companyName: '',
            version: '',
            features: [
              'enabled',
              'updated'
            ]
          },
          {
            type: 'firewall',
            instance: '283D5D26-55DE-9F0A-57DE-5BCF4A5A2598',
            displayName: 'McAfee Host Intrusion Prevention Firewall',
            companyName: '',
            version: '',
            features: [
              'enabled',
              'onAccessScanEnabled'
            ]
          }
        ],
        hostFileEntries: [
          {
            ip: '127.0.0.1',
            hosts: [
              'vmware-localhost'
            ]
          },
          {
            ip: '::1',
            hosts: [
              'vmware-localhost'
            ]
          },
          {
            ip: '10.25.52.113',
            hosts: [
              'cms.netwitness.com'
            ]
          }
        ],
        networkShares: [
          {
            path: 'C:\\windows',
            name: 'ADMIN$',
            description: 'Remote Admin',
            type: [
              'disk',
              'special'
            ],
            permissions: [
              'none'
            ],
            maxUses: 4294967295,
            currentUses: 0
          },
          {
            path: 'C:\\',
            name: 'C$',
            description: 'Default share',
            type: [
              'disk',
              'special'
            ],
            permissions: [
              'none'
            ],
            maxUses: 4294967295,
            currentUses: 0
          },
          {
            path: '',
            name: 'IPC$',
            description: 'Remote IPC',
            type: [
              'ipc',
              'special'
            ],
            permissions: [
              'none'
            ],
            maxUses: 4294967295,
            currentUses: 0
          },
          {
            path: 'C:\\Users',
            name: 'Users',
            description: '',
            type: [
              'disk'
            ],
            permissions: [
              'none'
            ],
            maxUses: 4294967295,
            currentUses: 0
          }
        ]
      },
      machineIdentity: {
        id: 'CA527998-8E16-B1D4-5E3E-2140F6AD1DF6',
        group: 'default',
        machineName: 'INENKUMARP10L8C',
        agent: {
          exeCompileTime: 1515062054000,
          packageTime: 1515149873444,
          installTime: 1515149921290,
          serviceStartTime: 1515149921923,
          serviceEprocess: 0,
          serviceProcessId: 3084,
          serviceErrorCode: 0,
          driverStatus: [
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255,
            255
          ],
          driverErrorCode: 0,
          blockingEnabled: false,
          blockingUpdateTime: ''
        },
        operatingSystem: {
          description: 'Microsoft Windows 10 Enterprise',
          buildNumber: '14393',
          servicePack: 0,
          directory: 'C:\\windows',
          kernelId: '5A1FB46D816000',
          kernelName: 'ntoskrnl.exe',
          kernelRelease: '2017-11-30T07:34:05.000Z',
          domainComputerId: '278DEC80-C0CB-47EF-9129-6E37EE89334D',
          domainComputerOu: 'corp.emc.com/International Clients/Win10/INENKUMARP10L8C',
          domainComputerCanonicalOu: 'corp.emc.com/International Clients/Win10/INENKUMARP10L8C',
          domainOrWorkgroup: 'corp.emc.com',
          domainRole: 'MemberWorkstation',
          lastBootTime: 1515125906085
        },
        hardware: {
          processorArchitecture: 'x64',
          processorArchitectureBits: 64,
          processorCount: 8,
          processorName: 'Intel(R) Core(TM) i7-4810MQ CPU @ 2.80GHz',
          totalPhysicalMemory: 17053265920,
          chassisType: 'Notebook',
          manufacturer: 'LENOVO',
          model: '20EGS03W1B',
          serial: 'R90HVVZA',
          bios: 'LENOVO - GNET75WW (2.23 ) - GNET75WW (2.23 )'
        },
        locale: {
          defaultLanguage: '1033',
          isoCountryCode: 'USA',
          timeZone: 'India Standard Time'
        },
        agentMode: 'userModeOnly',
        networkInterfaces: [
          {
            name: 'Cisco AnyConnect Secure Mobility Client Virtual Miniport Adapter for Windows x64',
            macAddress: '00:05:9A:3C:7A:00',
            networkIdv4: [
              '10.42.32.0'
            ],
            ipv4: [
              '10.42.35.93'
            ],
            ipv6: [
              'fe80::682d:b410:2858:da86%20',
              'fe80::b49f:77fc:71a4:308d%20',
              'fe80::d9dc:777a:518c:3499%20'
            ],
            networkIdv6: [
              'fe80::%20',
              'fe80::%20',
              'fe80::%20'
            ],
            gateway: [
              '::',
              '10.42.32.1'
            ],
            dns: [
              '10.73.241.89',
              '137.69.224.246'
            ],
            promiscuous: false
          },
          {
            name: 'Intel(R) Dual Band Wireless-AC 7260',
            macAddress: '4C:EB:42:AC:5B:98',
            networkIdv4: [
              '192.168.0.0'
            ],
            ipv4: [
              '192.168.0.104'
            ],
            ipv6: [
              'fe80::c549:7b49:adb5:17b%14'
            ],
            networkIdv6: [
              'fe80::%14'
            ],
            gateway: [
              '192.168.0.1'
            ],
            dns: [
              '192.168.0.1'
            ],
            promiscuous: false
          },
          {
            name: 'Cisco AnyConnect Secure Mobility Client Virtual Miniport Adapter for Windows x64',
            macAddress: '00:05:9A:3C:7A:00',
            networkIdv4: [
              '10.42.32.0'
            ],
            ipv4: [
              '10.42.35.93'
            ],
            ipv6: [
              'fe80::682d:b410:2858:da86%20',
              'fe80::b49f:77fc:71a4:308d%20',
              'fe80::d9dc:777a:518c:3499%20'
            ],
            networkIdv6: [
              'fe80::%20',
              'fe80::%20',
              'fe80::%20'
            ],
            gateway: [
              '::',
              '10.42.32.1'
            ],
            dns: [
              '10.73.241.89',
              '137.69.224.246'
            ],
            promiscuous: false
          },
          {
            name: 'Intel(R) Dual Band Wireless-AC 7260',
            macAddress: '4C:EB:42:AC:5B:98',
            networkIdv4: [
              '192.168.0.0'
            ],
            ipv4: [
              '192.168.0.104'
            ],
            ipv6: [
              'fe80::c549:7b49:adb5:17b%14'
            ],
            networkIdv6: [
              'fe80::%14'
            ],
            gateway: [
              '192.168.0.1'
            ],
            dns: [
              '192.168.0.1'
            ],
            promiscuous: false
          }
        ]
      },
      agentStatus: {
        lastSeenTime: 1515259999910,
        scanStatus: 'idle'
      }
    },
    downloadId: null,
    exportJSONStatus: 'completed',
    arrangeSecurityConfigsBy: 'alphabetical',
    loadingStatus: 'completed'
  }
};

const mac = {
  overview: {
    hostDetails: {
      id: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      machine: {
        machineAgentId: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
        agentVersion: '1.0.0.0',
        machineOsType: 'linux',
        machineName: 'HarpServer',
        scanStartTime: '2017-07-11T05:42:54.000Z',
        scanRequestTime: '2017-07-11T05:42:54.000Z',
        scanType: 'standard',
        scanTrigger: 'manual',
        networkInterfaces: [
          {
            name: 'ens160',
            macAddress: '00:50:56:01:47:01',
            ipv6: ['fe80::250:56ff:fe01:4701'],
            gateway: [
              '0.0.0.0'
            ],
            promiscuous: false
          },
          {
            name: 'ens32',
            macAddress: '00:50:56:01:2B:B5',
            ipv4: ['10.40.15.187'],
            ipv6: ['fe80::250:56ff:fe01:2bb5'],
            gateway: [
              '10.40.12.1'
            ],
            promiscuous: false
          },
          {
            name: 'lo',
            ipv4: ['127.0.0.1'],
            ipv6: ['::1'],
            gateway: [
              '0.0.0.0'
            ],
            promiscuous: false
          }
        ],
        users: [
          {
            name: 'root',
            sessionId: 0,
            host: 'inenbhatr9l1c.corp.emc.com',
            deviceName: 'pts/0',
            administrator: false
          },
          {
            name: 'root',
            sessionId: 0,
            host: 'inkumarp102l1c.corp.emc.com',
            deviceName: 'pts/1',
            administrator: false
          }
        ],
        hostFileEntries: [
          {
            ip: '127.0.0.1',
            hosts: [
              'localhost',
              'localhost.localdomain',
              'localhost4',
              'localhost4.localdomain4'
            ]
          },
          {
            ip: '10.40.15.187',
            hosts: [
              '71x64-001-0'
            ]
          },
          {
            ip: '10.40.5.88',
            hosts: [
              'helloworld.com'
            ]
          }
        ],
        bashHistory: [
          {
            commands: [
              'cd /etc/sysconfig/network-scripts/',
              'ls',
              'cat ifcfg-ens32 ',
              'clear'
            ],
            username: 'root'
          },
          {
            commands: [
              'mount /dev/cdrom /mnt/cdrom/',
              'ls /mnt/cdrom/',
              'umount /mnt/cdrom/',
              'cd/'
            ],
            username: 'testUser'
          }
        ],
        mountedPaths: []
      },
      machineIdentity: {
        id: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
        group: 'default',
        machineName: 'HarpServer',
        agent: {
          installTime: '2017-06-20T07:09:01.000Z',
          serviceStartTime: '2017-06-28T04:19:45.000Z',
          serviceEprocess: 0,
          serviceProcessId: 0,
          serviceErrorCode: 0,
          driverErrorCode: 0,
          blockingEnabled: false
        },
        operatingSystem: {
          description: 'CentOS Linux 7 (Core)',
          servicePack: 0,
          kernelName: 'Linux',
          kernelRelease: '3.10.0-229.20.1.el7.x86_64',
          kernelVersion: '#1 SMP Tue Nov 3 19:10:07 UTC 2015',
          distribution: 'rpm',
          lastBootTime: '2017-06-28T04:20:16.000Z'
        },
        hardware: {
          processorArchitecture: 'x64',
          processorArchitectureBits: 64,
          processorCount: 1,
          processorName: ' Intel(R) Xeon(R) CPU E5-2697 v3 @ 2.60GHz',
          totalPhysicalMemory: 16659738624,
          manufacturer: 'VMware, Inc.',
          model: 'VMware Virtual Platform',
          serial: 'VMware-42 04 6b 0d 27 37 42 e2-86 fe 90 a3 df 40 44 3e',
          bios: 'Phoenix Technologies LTD'
        },
        lastUpdatedTime: '2017-07-11T05:42:57.021Z',
        agentMode: 'userModeOnly'
      },
      agentStatus: {
        lastSeenTime: 1500091621745,
        scanStatus: 'idle'
      }
    },
    exportJSONStatus: 'completed',
    loadingStatus: 'completed'
  }
};

export {
  linux,
  windows,
  mac
};