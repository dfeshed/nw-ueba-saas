export const hostDetails = {
  'id': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
  'machine': {
    'machineAgentId': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
    'agentVersion': '4.4.0.0',
    'machineOsType': 'linux',
    'machineName': 'HarpServer',
    'scanStartTime': '2017-07-11T05:42:54.000Z',
    'scanRequestTime': '2017-07-11T05:42:54.000Z',
    'scanType': 'standard',
    'scanTrigger': 'manual',
    'securityConfigurations': [
      'uacDisabled',
      'luaDisabled'
    ],
    'networkInterfaces': [{
      'name': 'ens160',
      'macAddress': '00:50:56:01:47:01',
      'ipv4': ['10.40.15.171', '127.0.0.1'],
      'ipv6': ['fe80::250:56ff:fe01:4701', '::1'],
      'gateway': ['0.0.0.0'],
      'promiscuous': false
    },
    {
      'name': 'ens32',
      'macAddress': '00:50:56:01:2B:B5',
      'ipv4': ['10.40.15.187', '10.40.12.7'],
      'ipv6': ['fe80::250:56ff:fe01:2bb5', 'fe80::250:56ff:fe01:4701'],
      'gateway': ['10.40.12.1'],
      'promiscuous': false
    },
    {
      'name': 'lo',
      'ipv4': ['127.0.0.1'],
      'ipv6': ['::1'],
      'gateway': ['0.0.0.0'],
      'promiscuous': false
    },
    {
      'name': 'ens33',
      'macAddress': '00:50:56:01:2C:B5',
      'gateway': ['10.40.12.1'],
      'promiscuous': false
    }],
    'users': [{
      'name': 'root',
      'sessionId': 0,
      'host': 'inenbhatr9l1c.corp.emc.com',
      'deviceName': 'pts/0',
      'administrator': false
    }, {
      'name': 'root',
      'sessionId': 0,
      'host': 'inkumarp102l1c.corp.emc.com',
      'deviceName': 'pts/1',
      'administrator': false
    }],
    'hostFileEntries': [{
      'ip': '127.0.0.1',
      'hosts': ['localhost', 'localhost.localdomain', 'localhost4', 'localhost4.localdomain4']
    }, { 'ip': '10.40.15.187', 'hosts': ['71x64-001-0'] }, { 'ip': '10.40.5.88', 'hosts': ['helloworld.com'] }],
    'mountedPaths': [{ 'path': '/', 'fileSystem': 'rootfs', 'options': 'rw', 'remotePath': 'rootfs' }, {
      'path': '/proc',
      'fileSystem': 'proc',
      'options': 'rw,nosuid,nodev,noexec,relatime',
      'remotePath': 'proc'
    }, {
      'path': '/sys',
      'fileSystem': 'sysfs',
      'options': 'rw,nosuid,nodev,noexec,relatime',
      'remotePath': 'sysfs'
    }, {
      'path': '/dev',
      'fileSystem': 'devtmpfs',
      'options': 'rw,nosuid,size=8125192k,nr_inodes=2031298,mode=755',
      'remotePath': 'devtmpfs'
    }, {
      'path': '/sys/kernel/security',
      'fileSystem': 'securityfs',
      'options': 'rw,nosuid,nodev,noexec,relatime',
      'remotePath': 'securityfs'
    }, {
      'path': '/dev/shm',
      'fileSystem': 'tmpfs',
      'options': 'rw,nosuid,nodev',
      'remotePath': 'tmpfs'
    }, {
      'path': '/dev/pts',
      'fileSystem': 'devpts',
      'options': 'rw,nosuid,noexec,relatime,gid=5,mode=620,ptmxmode=000',
      'remotePath': 'devpts'
    }, {
      'path': '/run',
      'fileSystem': 'tmpfs',
      'options': 'rw,nosuid,nodev,mode=755',
      'remotePath': 'tmpfs'
    }, {
      'path': '/sys/fs/cgroup',
      'fileSystem': 'tmpfs',
      'options': 'rw,nosuid,nodev,noexec,mode=755',
      'remotePath': 'tmpfs'
    }, {
      'path': '/sys/fs/cgroup/systemd',
      'fileSystem': 'cgroup',
      'options': 'rw,nosuid,nodev,noexec,relatime,xattr,release_agent=/usr/lib/systemd/systemd-cgroups-agent,name=systemd',
      'remotePath': 'cgroup'
    }, {
      'path': '/sys/fs/pstore',
      'fileSystem': 'pstore',
      'options': 'rw,nosuid,nodev,noexec,relatime',
      'remotePath': 'pstore'
    }, {
      'path': '/sys/fs/cgroup/cpuset',
      'fileSystem': 'cgroup',
      'options': 'rw,nosuid,nodev,noexec,relatime,cpuset',
      'remotePath': 'cgroup'
    }, {
      'path': '/sys/fs/cgroup/cpu,cpuacct',
      'fileSystem': 'cgroup',
      'options': 'rw,nosuid,nodev,noexec,relatime,cpuacct,cpu',
      'remotePath': 'cgroup'
    }, {
      'path': '/sys/fs/cgroup/memory',
      'fileSystem': 'cgroup',
      'options': 'rw,nosuid,nodev,noexec,relatime,memory',
      'remotePath': 'cgroup'
    }, {
      'path': '/sys/fs/cgroup/devices',
      'fileSystem': 'cgroup',
      'options': 'rw,nosuid,nodev,noexec,relatime,devices',
      'remotePath': 'cgroup'
    }, {
      'path': '/sys/fs/cgroup/freezer',
      'fileSystem': 'cgroup',
      'options': 'rw,nosuid,nodev,noexec,relatime,freezer',
      'remotePath': 'cgroup'
    }, {
      'path': '/sys/fs/cgroup/net_cls',
      'fileSystem': 'cgroup',
      'options': 'rw,nosuid,nodev,noexec,relatime,net_cls',
      'remotePath': 'cgroup'
    }, {
      'path': '/sys/fs/cgroup/blkio',
      'fileSystem': 'cgroup',
      'options': 'rw,nosuid,nodev,noexec,relatime,blkio',
      'remotePath': 'cgroup'
    }, {
      'path': '/sys/fs/cgroup/perf_event',
      'fileSystem': 'cgroup',
      'options': 'rw,nosuid,nodev,noexec,relatime,perf_event',
      'remotePath': 'cgroup'
    }, {
      'path': '/sys/fs/cgroup/hugetlb',
      'fileSystem': 'cgroup',
      'options': 'rw,nosuid,nodev,noexec,relatime,hugetlb',
      'remotePath': 'cgroup'
    }, {
      'path': '/sys/kernel/config',
      'fileSystem': 'configfs',
      'options': 'rw,relatime',
      'remotePath': 'configfs'
    }, {
      'path': '/',
      'fileSystem': 'xfs',
      'options': 'rw,relatime,attr2,inode64,noquota',
      'remotePath': '/dev/mapper/centos-root'
    }, {
      'path': '/proc/sys/fs/binfmt_misc',
      'fileSystem': 'autofs',
      'options': 'rw,relatime,fd=33,pgrp=1,timeout=300,minproto=5,maxproto=5,direct',
      'remotePath': 'systemd-1'
    }, {
      'path': '/dev/hugepages',
      'fileSystem': 'hugetlbfs',
      'options': 'rw,relatime',
      'remotePath': 'hugetlbfs'
    }, {
      'path': '/dev/mqueue',
      'fileSystem': 'mqueue',
      'options': 'rw,relatime',
      'remotePath': 'mqueue'
    }, {
      'path': '/sys/kernel/debug',
      'fileSystem': 'debugfs',
      'options': 'rw,relatime',
      'remotePath': 'debugfs'
    }, {
      'path': '/boot',
      'fileSystem': 'xfs',
      'options': 'rw,relatime,attr2,inode64,noquota',
      'remotePath': '/dev/sda1'
    }, {
      'path': '/proc/sys/fs/binfmt_misc',
      'fileSystem': 'binfmt_misc',
      'options': 'rw,relatime',
      'remotePath': 'binfmt_misc'
    }],
    systemPatches: ['KB974405', '982861', 'KB2670838', 'KB2830477', 'KB2592687', 'KB2393802'],
    securityProducts: [{
      type: 'antiVirus',
      instance: 'D68DDC3A-831F-4FAE-9E44-DA132C1ACF46',
      displayName: 'Windows Defender',
      companyName: '',
      version: ''
    },
    {
      type: 'antiSpyware',
      instance: 'D68DDC3A-831F-4FAE-9E44-DA132C1ACF46',
      displayName: 'Windows Defender',
      companyName: '',
      version: ''
    }]
  },
  'machineIdentity': {
    'id': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
    'group': 'default',
    'machineName': 'HarpServer',
    'agent': {
      'installTime': '2017-06-20T07:09:01.000Z',
      'serviceStartTime': '2017-06-28T04:19:45.000Z',
      'serviceProcessId': 0,
      'blockingEnabled': false
    },
    'operatingSystem': {
      'description': 'CentOS Linux 7 (Core)',
      'servicePack': 0,
      'kernelName': 'Linux',
      'kernelRelease': '3.10.0-229.20.1.el7.x86_64',
      'kernelVersion': '#1 SMP Tue Nov 3 19:10:07 UTC 2015',
      'distribution': 'rpm',
      'lastBootTime': '2017-06-28T04:20:16.000Z'
    },
    'hardware': {
      'processorArchitecture': 'x64',
      'processorArchitectureBits': 64,
      'processorCount': 1,
      'processorName': ' Intel(R) Xeon(R) CPU E5-2697 v3 @ 2.60GHz',
      'totalPhysicalMemory': 16659738624,
      'manufacturer': 'VMware, Inc.',
      'model': 'VMware Virtual Platform',
      'serial': 'VMware-42 04 6b 0d 27 37 42 e2-86 fe 90 a3 df 40 44 3e',
      'bios': 'Phoenix Technologies LTD'
    },
    'lastUpdatedTime': '2017-07-11T05:42:57.021Z',
    'agentMode': 'userModeOnly'
  },
  'agentStatus': { 'lastSeenTime': 1500091621745, 'scanStatus': 'idle' }
};

export const libraries = [
  {
    'id': '59d635be973e7702acf7e51f',
    'machineOsType': 'linux',
    'machineName': 'HarpServer1',
    'machineAgentId': 'C3C5F14B-CE69-4C3E-8E74-73962CE7DCBF',
    'agentVersion': '1.0.0.0',
    'scanStartTime': 1507210681000,
    'checksumSha256': '5d4025d0970fa278588f118d64287700e1bc6ecbc1109022766fb67f75a317cf',
    'path': '/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.121-0.b13.el7_3.x86_64/jre/lib/amd64',
    'pathH8': '1629575512214314084',
    'fileName': 'libverify.so',
    'owner': {
      'username': 'root',
      'groupname': 'root',
      'uid': 0,
      'gid': 0
    },
    'timeModified': 1484933853000,
    'timeAccessed': 1507159253000,
    'mode': 33261,
    'sameDirectoryFileCounts': {
      'nonExe': 1,
      'exe': 35,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'fileFeatures': [
      'partOfRPM'
    ],
    'directoryFeatures': [
      'usr'
    ],
    'rpm': {
      'category': 'Development/Languages'
    },
    'linux': {
      'loadedLibraries': [
        {
          'pid': 683,
          'imageBase': 139631315189760
        },
        {
          'pid': 684,
          'imageBase': 140002372235264
        },
        {
          'pid': 729,
          'imageBase': 140085476257792
        },
        {
          'pid': 735,
          'imageBase': 140710547460096
        }
      ]
    },
    'fileProperties': {
      'id': '5d4025d0970fa278588f118d64287700e1bc6ecbc1109022766fb67f75a317cf',
      'firstFileName': 'libverify.so',
      'machineOsType': 'linux',
      'size': 61304,
      'checksumMd5': 'fe9a146ed26fa964568c8e0569344e15',
      'checksumSha1': '76755167d85442a80fce1ee0d7a5cb68391234f8',
      'checksumSha256': '5d4025d0970fa278588f118d64287700e1bc6ecbc1109022766fb67f75a317cf',
      'elf': {
        'classType': 0,
        'data': 0,
        'entryPoint': 17664,
        'features': [
          'arch64',
          'so'
        ],
        'type': 3,
        'sectionNames': [
          '.note.gnu.build-id',
          '.hash',
          '.gnu.hash',
          '.dynsym',
          '.dynstr',
          '.gnu.version',
          '.gnu.version_d',
          '.gnu.version_r',
          '.rela.dyn',
          '.rela.plt',
          '.init',
          '.plt',
          '.text',
          '.fini',
          '.rodata',
          '.eh_frame_hdr',
          '.eh_frame',
          '.init_array',
          '.fini_array',
          '.jcr',
          '.data.rel.ro',
          '.dynamic',
          '.got',
          '.got.plt',
          '.bss',
          '.gnu_debuglink',
          '.gnu_debugdata',
          '.shstrtab'
        ],
        'importedLibraries': [
          'libjvm.so',
          'libc.so.6'
        ]
      },
      'entropy': 5.345998261948191,
      'format': 'elf'
    }
  },
  {
    'id': '59d635be973e7702acf7e520',
    'machineOsType': 'linux',
    'machineName': 'HarpServer1',
    'machineAgentId': 'C3C5F14B-CE69-4C3E-8E74-73962CE7DCBF',
    'agentVersion': '1.0.0.0',
    'scanStartTime': 1507210681000,
    'checksumSha256': '9671c052ff045b6fd96208f70d5cbc2d421d2ebbaeb8061dbc49acfb99fa3fb4',
    'path': '/usr/lib64/erlang/lib/sd_notify-0.1/priv',
    'pathH8': '3167199935097941238',
    'fileName': 'sd_notify_drv.so',
    'owner': {
      'username': 'root',
      'groupname': 'root',
      'uid': 0,
      'gid': 0
    },
    'timeModified': 1403108911000,
    'timeAccessed': 1507159283000,
    'mode': 33261,
    'sameDirectoryFileCounts': {
      'nonExe': 0,
      'exe': 2,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'fileFeatures': [
      'partOfRPM'
    ],
    'directoryFeatures': [
      'usr'
    ],
    'rpm': {
      'category': 'Development/Languages'
    },
    'linux': {
      'loadedLibraries': [
        {
          'pid': 1175,
          'imageBase': 139767557718016
        }
      ]
    },
    'fileProperties': {
      'id': '9671c052ff045b6fd96208f70d5cbc2d421d2ebbaeb8061dbc49acfb99fa3fb4',
      'firstFileName': 'sd_notify_drv.so',
      'machineOsType': 'linux',
      'size': 7184,
      'checksumMd5': 'daa5155c12e2bb5012cac1a60abd1cfc',
      'checksumSha1': '70a776954675d9fe3301c8a7972dfc39837b2ed3',
      'checksumSha256': '9671c052ff045b6fd96208f70d5cbc2d421d2ebbaeb8061dbc49acfb99fa3fb4',
      'elf': {
        'classType': 0,
        'data': 0,
        'entryPoint': 2208,
        'features': [
          'arch64',
          'so'
        ],
        'type': 3,
        'sectionNames': [
          '.note.gnu.build-id',
          '.gnu.hash',
          '.dynsym',
          '.dynstr',
          '.gnu.version',
          '.gnu.version_r',
          '.rela.dyn',
          '.rela.plt',
          '.init',
          '.plt',
          '.text',
          '.fini',
          '.rodata',
          '.eh_frame_hdr',
          '.eh_frame',
          '.init_array',
          '.fini_array',
          '.jcr',
          '.data.rel.ro',
          '.dynamic',
          '.got',
          '.got.plt',
          '.data',
          '.bss',
          '.gnu_debuglink',
          '.gnu_debugdata',
          '.shstrtab'
        ],
        'importedLibraries': [
          'libc.so.6'
        ]
      },
      'entropy': 3.701921040254031,
      'format': 'elf'
    }
  },
  {
    'id': '59d635be973e7702acf7e521',
    'machineOsType': 'linux',
    'machineName': 'HarpServer1',
    'machineAgentId': 'C3C5F14B-CE69-4C3E-8E74-73962CE7DCBF',
    'agentVersion': '1.0.0.0',
    'scanStartTime': 1507210681000,
    'checksumSha256': 'd6c18e0051dcc6c79a5339caf04399dbc9ec5f425ec262bee44c3bf837b4bc6c',
    'path': '/usr/lib64',
    'pathH8': '8206168421034894116',
    'fileName': 'libxml2.so.2.9.1',
    'owner': {
      'username': 'root',
      'groupname': 'root',
      'uid': 0,
      'gid': 0
    },
    'timeModified': 1466692579000,
    'timeAccessed': 1507159254000,
    'mode': 33261,
    'sameDirectoryFileCounts': {
      'nonExe': 17,
      'exe': 312,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'fileFeatures': [
      'partOfRPM'
    ],
    'directoryFeatures': [
      'usr'
    ],
    'rpm': {
      'category': 'Development/Libraries'
    },
    'linux': {
      'loadedLibraries': [
        {
          'pid': 685,
          'imageBase': 140377087434752
        }
      ]
    },
    'fileProperties': {
      'id': 'd6c18e0051dcc6c79a5339caf04399dbc9ec5f425ec262bee44c3bf837b4bc6c',
      'firstFileName': 'libxml2.so.2.9.1',
      'machineOsType': 'linux',
      'size': 1509376,
      'checksumMd5': '18fd94949f573337f0393c120cded9d2',
      'checksumSha1': '1430680ccc811e136b4d7bc11a2bd02d7aa6d7fb',
      'checksumSha256': 'd6c18e0051dcc6c79a5339caf04399dbc9ec5f425ec262bee44c3bf837b4bc6c',
      'elf': {
        'classType': 0,
        'data': 0,
        'entryPoint': 190752,
        'features': [
          'arch64',
          'so'
        ],
        'type': 3,
        'sectionNames': [
          '.note.gnu.build-id',
          '.gnu.hash',
          '.dynsym',
          '.dynstr',
          '.gnu.version',
          '.gnu.version_d',
          '.gnu.version_r',
          '.rela.dyn',
          '.rela.plt',
          '.init',
          '.plt',
          '.text',
          '.fini',
          '.rodata',
          '.eh_frame_hdr',
          '.eh_frame',
          '.init_array',
          '.fini_array',
          '.jcr',
          '.data.rel.ro',
          '.dynamic',
          '.got',
          '.got.plt',
          '.data',
          '.bss',
          '.gnu_debuglink',
          '.gnu_debugdata',
          '.shstrtab'
        ],
        'importedLibraries': [
          'libdl.so.2',
          'libz.so.1',
          'liblzma.so.5',
          'libm.so.6',
          'libc.so.6'
        ]
      },
      'entropy': 6.274714198343111,
      'format': 'elf'
    }
  },
  {
    'id': '59d635be973e7702acf7e523',
    'machineOsType': 'linux',
    'machineName': 'HarpServer1',
    'machineAgentId': 'C3C5F14B-CE69-4C3E-8E74-73962CE7DCBF',
    'agentVersion': '1.0.0.0',
    'scanStartTime': 1507210681000,
    'checksumSha256': '45f2c37e4f65bbff8024c8e9e9aee8eadc60cf3b0742f127e9d40f2fd789a0e6',
    'path': '/usr/lib64/rsyslog',
    'pathH8': '5240273615977419971',
    'fileName': 'imuxsock.so',
    'owner': {
      'username': 'root',
      'groupname': 'root',
      'uid': 0,
      'gid': 0
    },
    'timeModified': 1478386362000,
    'timeAccessed': 1507159323000,
    'mode': 33261,
    'sameDirectoryFileCounts': {
      'nonExe': 0,
      'exe': 34,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'fileFeatures': [
      'partOfRPM'
    ],
    'directoryFeatures': [
      'usr'
    ],
    'rpm': {
      'category': 'System Environment/Daemons'
    },
    'linux': {
      'loadedLibraries': [
        {
          'pid': 1170,
          'imageBase': 139801018249216
        }
      ]
    },
    'fileProperties': {
      'id': '45f2c37e4f65bbff8024c8e9e9aee8eadc60cf3b0742f127e9d40f2fd789a0e6',
      'firstFileName': 'imuxsock.so',
      'machineOsType': 'linux',
      'size': 420760,
      'checksumMd5': '74c40cdd7627505019f0016743cde0b9',
      'checksumSha1': '35ca462e7b3aeccbd9b3c2e52e447d15d34fc5fc',
      'checksumSha256': '45f2c37e4f65bbff8024c8e9e9aee8eadc60cf3b0742f127e9d40f2fd789a0e6',
      'elf': {
        'classType': 0,
        'data': 0,
        'entryPoint': 71200,
        'features': [
          'arch64',
          'so'
        ],
        'type': 3,
        'sectionNames': [
          '.note.gnu.build-id',
          '.gnu.hash',
          '.dynsym',
          '.dynstr',
          '.gnu.version',
          '.gnu.version_r',
          '.rela.dyn',
          '.rela.plt',
          '.init',
          '.plt',
          '.text',
          '.fini',
          '.rodata',
          '.eh_frame_hdr',
          '.eh_frame',
          '.gcc_except_table',
          '.init_array',
          '.fini_array',
          '.jcr',
          '.data.rel.ro',
          '.dynamic',
          '.got',
          '.data',
          '.bss',
          '.gnu_debuglink',
          '.gnu_debugdata',
          '.shstrtab'
        ],
        'importedLibraries': [
          'libdl.so.2',
          'librt.so.1',
          'libuuid.so.1',
          'libestr.so.0',
          'libjson-c.so.2',
          'libgcc_s.so.1',
          'libc.so.6'
        ]
      },
      'entropy': 5.978311739736323,
      'format': 'elf'
    }
  },
  {
    'id': '59d635be973e7702acf7e524',
    'machineOsType': 'linux',
    'machineName': 'HarpServer1',
    'machineAgentId': 'C3C5F14B-CE69-4C3E-8E74-73962CE7DCBF',
    'agentVersion': '1.0.0.0',
    'scanStartTime': 1507210681000,
    'checksumSha256': '44e6124bc51d8eef07f8f2ca043d16e13916276ae47af173c5cc4b782b274def',
    'path': '/usr/lib/vmware-tools/lib64/libvmtools.so',
    'pathH8': '-1382046628951331623',
    'fileName': 'libvmtools.so',
    'owner': {
      'username': 'root',
      'groupname': 'root',
      'uid': 0,
      'gid': 0
    },
    'timeModified': 1447237412000,
    'timeAccessed': 1507159279000,
    'mode': 33188,
    'sameDirectoryFileCounts': {
      'nonExe': 0,
      'exe': 2,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'directoryFeatures': [
      'usr'
    ],
    'linux': {
      'loadedLibraries': [
        {
          'pid': 940,
          'imageBase': 140018574397440
        }
      ]
    },
    'fileProperties': {
      'id': '44e6124bc51d8eef07f8f2ca043d16e13916276ae47af173c5cc4b782b274def',
      'firstFileName': 'libvmtools.so',
      'machineOsType': 'linux',
      'size': 819888,
      'checksumMd5': '2d935d61fed282ab84d6b1389e635eea',
      'checksumSha1': '5c4836d8ebaef64739203af596397c5682193f44',
      'checksumSha256': '44e6124bc51d8eef07f8f2ca043d16e13916276ae47af173c5cc4b782b274def',
      'elf': {
        'classType': 0,
        'data': 0,
        'entryPoint': 111232,
        'features': [
          'arch64',
          'so'
        ],
        'type': 3,
        'sectionNames': [
          '.hash',
          '.dynsym',
          '.dynstr',
          '.gnu.version',
          '.gnu.version_r',
          '.rela.dyn',
          '.rela.plt',
          '.init',
          '.plt',
          '.text',
          '.fini',
          '.rodata',
          '.modinfo',
          '.eh_frame_hdr',
          '.eh_frame',
          '.ctors',
          '.dtors',
          '.jcr',
          '.data.rel.ro',
          '.dynamic',
          '.got',
          '.data',
          '.bss',
          '.shstrtab'
        ],
        'importedLibraries': [
          'libdl.so.2',
          'libpthread.so.0',
          'libglib-2.0.so.0',
          'libgcc_s.so.1',
          'libc.so.6',
          'ld-linux-x86-64.so.2'
        ]
      },
      'entropy': 5.553366676047986,
      'format': 'elf'
    }
  }
];

export const driversData = [
  {
    'id': '59d635be973e7702acf7e51b',
    'machineOsType': 'linux',
    'machineName': 'HarpServer1',
    'machineAgentId': 'C3C5F14B-CE69-4C3E-8E74-73962CE7DCBF',
    'agentVersion': '1.0.0.0',
    'scanStartTime': 1507210681000,
    'checksumSha256': '9ee6f17becd84af5070def9237affd9ec21a45365048952bdf69f5a9fe798908',
    'path': '/usr/lib/modules/3.10.0-514.6.2.el7.x86_64/kernel/drivers/gpu/drm/vmwgfx',
    'pathH8': '1776055397464116788',
    'fileName': 'vmwgfx.ko',
    'owner': {
      'username': 'root',
      'groupname': 'root',
      'uid': 0,
      'gid': 0
    },
    'timeModified': 1487821851000,
    'timeAccessed': 1507159236000,
    'mode': 33188,
    'sameDirectoryFileCounts': {
      'nonExe': 0,
      'exe': 2,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'fileFeatures': [
      'partOfRPM'
    ],
    'directoryFeatures': [
      'usr'
    ],
    'rpm': {
      'category': 'System Environment/Kernel'
    },
    'linux': {
      'drivers': [
        {
          'imageBase': 9223372036854776000,
          'imageSize': 235043,
          'numberOfInstances': 1,
          'loadState': 'Live',
          'dependencies': [
            '-'
          ],
          'author': 'VMware Inc. and others',
          'description': 'Standalone drm driver for the VMware SVGA device',
          'sourceVersion': 'AE7EF7080842DC1E828A124',
          'versionMagic': '3.10.0-514.6.2.el7.x86_64 SMP mod_unload modversions'
        }
      ]
    },
    'fileProperties': {
      'id': '9ee6f17becd84af5070def9237affd9ec21a45365048952bdf69f5a9fe798908',
      'firstFileName': 'vmwgfx.ko',
      'machineOsType': 'linux',
      'size': 399989,
      'checksumMd5': 'eeb276261c3e18ddcf4ef16f16f0e23e',
      'checksumSha1': '2952214a36502b8364ca479d8b960d047235314e',
      'checksumSha256': '9ee6f17becd84af5070def9237affd9ec21a45365048952bdf69f5a9fe798908',
      'elf': {
        'classType': 0,
        'data': 0,
        'entryPoint': 0,
        'features': [
          'arch64',
          'lkm'
        ],
        'type': 1,
        'sectionNames': [
          '.note.gnu.build-id',
          '.text',
          '.rela.text',
          '.text.unlikely',
          '.rela.text.unlikely',
          '.init.text',
          '.rela.init.text',
          '.exit.text',
          '.rela.exit.text',
          '.rodata',
          '.rela.rodata',
          '.rodata.str1.8',
          '__bug_table',
          '.rela__bug_table',
          '.rodata.str1.1',
          '.smp_locks',
          '.rela.smp_locks',
          '__mcount_loc',
          '.rela__mcount_loc',
          '.modinfo',
          '__param',
          '.rela__param',
          '.parainstructions',
          '.rela.parainstructions',
          '__versions',
          '.data',
          '.rela.data',
          '.data.unlikely',
          '__jump_table',
          '.rela__jump_table',
          '.gnu.linkonce.this_module',
          '.rela.gnu.linkonce.this_module',
          '.bss',
          '.symtab',
          '.strtab',
          '.gnu_debuglink',
          '.shstrtab'
        ]
      },
      'entropy': 4.463662767013819,
      'format': 'elf'
    }
  },
  {
    'id': '59d635be973e7702acf7e51c',
    'machineOsType': 'linux',
    'machineName': 'HarpServer1',
    'machineAgentId': 'C3C5F14B-CE69-4C3E-8E74-73962CE7DCBF',
    'agentVersion': '1.0.0.0',
    'scanStartTime': 1507210681000,
    'checksumSha256': '939b00c2149ba3d7951da7ec917b299c27dbd878f1473a6d2121da2751e3c415',
    'path': '/usr/lib/modules/3.10.0-514.6.2.el7.x86_64/kernel/drivers/misc',
    'pathH8': '-1743464234473276130',
    'fileName': 'vmw_balloon.ko',
    'owner': {
      'username': 'root',
      'groupname': 'root',
      'uid': 0,
      'gid': 0
    },
    'timeModified': 1487821890000,
    'timeAccessed': 1507159252000,
    'mode': 33188,
    'sameDirectoryFileCounts': {
      'nonExe': 0,
      'exe': 14,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'fileFeatures': [
      'partOfRPM'
    ],
    'directoryFeatures': [
      'usr'
    ],
    'rpm': {
      'category': 'System Environment/Kernel'
    },
    'linux': {
      'drivers': [
        {
          'imageBase': 9223372036854776000,
          'imageSize': 18190,
          'numberOfInstances': 0,
          'loadState': 'Live',
          'dependencies': [
            '-'
          ],
          'author': 'VMware, Inc.',
          'description': 'VMware Memory Control (Balloon) Driver',
          'sourceVersion': '46EBBFAD9EDED604154B715',
          'versionMagic': '3.10.0-514.6.2.el7.x86_64 SMP mod_unload modversions'
        }
      ]
    },
    'fileProperties': {
      'id': '939b00c2149ba3d7951da7ec917b299c27dbd878f1473a6d2121da2751e3c415',
      'firstFileName': 'vmw_balloon.ko',
      'machineOsType': 'linux',
      'size': 23189,
      'checksumMd5': '0b3eb734f5ea4ee8e58d50f4ec7138c2',
      'checksumSha1': '3aae9bc0cfa5629cc826c70e3bb52b2e304c81cc',
      'checksumSha256': '939b00c2149ba3d7951da7ec917b299c27dbd878f1473a6d2121da2751e3c415',
      'elf': {
        'classType': 0,
        'data': 0,
        'entryPoint': 0,
        'features': [
          'arch64',
          'lkm'
        ],
        'type': 1,
        'sectionNames': [
          '.note.gnu.build-id',
          '.text',
          '.rela.text',
          '.init.text',
          '.rela.init.text',
          '.exit.text',
          '.rela.exit.text',
          '.rodata.str1.8',
          '.rodata.str1.1',
          '.rodata',
          '.rela.rodata',
          '.modinfo',
          '__mcount_loc',
          '.rela__mcount_loc',
          '__versions',
          '.data',
          '__verbose',
          '.rela__verbose',
          '.data.unlikely',
          '.gnu.linkonce.this_module',
          '.rela.gnu.linkonce.this_module',
          '.bss',
          '.symtab',
          '.strtab',
          '.gnu_debuglink',
          '.shstrtab'
        ]
      },
      'entropy': 4.113083744148256,
      'format': 'elf'
    }
  },
  {
    'id': '59d635be973e7702acf7e51d',
    'machineOsType': 'linux',
    'machineName': 'HarpServer1',
    'machineAgentId': 'C3C5F14B-CE69-4C3E-8E74-73962CE7DCBF',
    'agentVersion': '1.0.0.0',
    'scanStartTime': 1507210681000,
    'checksumSha256': '3afe70ba0a58cb15a456d8d2e748a6a4bedcb59b2663fa711227b97fa2105a5f',
    'path': '/usr/lib/modules/3.10.0-514.6.2.el7.x86_64/kernel/lib',
    'pathH8': '470112145389524598',
    'fileName': 'crc-t10dif.ko',
    'owner': {
      'username': 'root',
      'groupname': 'root',
      'uid': 0,
      'gid': 0
    },
    'timeModified': 1487821920000,
    'timeAccessed': 1507159252000,
    'mode': 33188,
    'sameDirectoryFileCounts': {
      'nonExe': 0,
      'exe': 10,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'fileFeatures': [
      'partOfRPM'
    ],
    'directoryFeatures': [
      'usr'
    ],
    'rpm': {
      'category': 'System Environment/Kernel'
    },
    'linux': {
      'drivers': [
        {
          'imageBase': 9223372036854776000,
          'imageSize': 12714,
          'numberOfInstances': 1,
          'loadState': 'Live',
          'dependencies': [
            'sd_mod',
            ''
          ],
          'description': 'T10 DIF CRC calculation',
          'sourceVersion': '0FF379865272D68BD276ED3',
          'versionMagic': '3.10.0-514.6.2.el7.x86_64 SMP mod_unload modversions'
        }
      ]
    },
    'fileProperties': {
      'id': '3afe70ba0a58cb15a456d8d2e748a6a4bedcb59b2663fa711227b97fa2105a5f',
      'firstFileName': 'crc-t10dif.ko',
      'machineOsType': 'linux',
      'size': 6613,
      'checksumMd5': 'acf4c9b1f25c3cdc06695d6011c3b318',
      'checksumSha1': 'd4cb2e05f1cabf4b98fd16de187985f6b5537a46',
      'checksumSha256': '3afe70ba0a58cb15a456d8d2e748a6a4bedcb59b2663fa711227b97fa2105a5f',
      'elf': {
        'classType': 0,
        'data': 0,
        'entryPoint': 0,
        'features': [
          'arch64',
          'lkm'
        ],
        'type': 1,
        'sectionNames': [
          '.note.gnu.build-id',
          '.text',
          '.rela.text',
          '.init.text',
          '.rela.init.text',
          '.exit.text',
          '.rela.exit.text',
          '__ksymtab',
          '.rela__ksymtab',
          '__kcrctab',
          '.rela__kcrctab',
          '.rodata.str1.1',
          '__bug_table',
          '.rela__bug_table',
          '.modinfo',
          '__ksymtab_strings',
          '__versions',
          '.data',
          '__jump_table',
          '.rela__jump_table',
          '.data..read_mostly',
          '.gnu.linkonce.this_module',
          '.rela.gnu.linkonce.this_module',
          '.bss',
          '.symtab',
          '.strtab',
          '.gnu_debuglink',
          '.shstrtab'
        ]
      },
      'entropy': 3.353349897793558,
      'format': 'elf'
    }
  },
  {
    'id': '59d635be973e7702acf7e51e',
    'machineOsType': 'linux',
    'machineName': 'HarpServer1',
    'machineAgentId': 'C3C5F14B-CE69-4C3E-8E74-73962CE7DCBF',
    'agentVersion': '1.0.0.0',
    'scanStartTime': 1507210681000,
    'checksumSha256': '0b25d3f38a822849dd6b79b029aa99dd5a8605bca72a64537d545eb9af2092bd',
    'path': '/usr/lib/modules/3.10.0-514.6.2.el7.x86_64/kernel/drivers/message/fusion',
    'pathH8': '3907344109810374181',
    'fileName': 'mptscsih.ko',
    'owner': {
      'username': 'root',
      'groupname': 'root',
      'uid': 0,
      'gid': 0
    },
    'timeModified': 1487821888000,
    'timeAccessed': 1507159245000,
    'mode': 33188,
    'sameDirectoryFileCounts': {
      'nonExe': 0,
      'exe': 6,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'fileFeatures': [
      'partOfRPM'
    ],
    'directoryFeatures': [
      'usr'
    ],
    'rpm': {
      'category': 'System Environment/Kernel'
    },
    'linux': {
      'drivers': [
        {
          'imageBase': 9223372036854776000,
          'imageSize': 40150,
          'numberOfInstances': 1,
          'loadState': 'Live',
          'dependencies': [
            'mptspi',
            ''
          ],
          'author': 'LSI Corporation',
          'description': 'Fusion MPT SCSI Host driver',
          'sourceVersion': 'B2E01114DB333404831C33F',
          'versionMagic': '3.10.0-514.6.2.el7.x86_64 SMP mod_unload modversions'
        }
      ]
    },
    'fileProperties': {
      'id': '0b25d3f38a822849dd6b79b029aa99dd5a8605bca72a64537d545eb9af2092bd',
      'firstFileName': 'mptscsih.ko',
      'machineOsType': 'linux',
      'size': 68229,
      'checksumMd5': 'da2331c6386253fd13e6f4d57b5dbb87',
      'checksumSha1': '42f0af6a7fe8ffe11fcf53970737e886aaf9b404',
      'checksumSha256': '0b25d3f38a822849dd6b79b029aa99dd5a8605bca72a64537d545eb9af2092bd',
      'elf': {
        'classType': 0,
        'data': 0,
        'entryPoint': 0,
        'features': [
          'arch64',
          'lkm'
        ],
        'type': 1,
        'sectionNames': [
          '.note.gnu.build-id',
          '.text',
          '.rela.text',
          '.text.unlikely',
          '.rela.text.unlikely',
          '__ksymtab',
          '.rela__ksymtab',
          '__kcrctab',
          '.rela__kcrctab',
          '.rodata.str1.1',
          '.rodata.str1.8',
          '.rodata',
          '.rela.rodata',
          '__ksymtab_strings',
          '.modinfo',
          '__mcount_loc',
          '.rela__mcount_loc',
          '__versions',
          '.data',
          '.rela.data',
          '.gnu.linkonce.this_module',
          '.bss',
          '.symtab',
          '.strtab',
          '.gnu_debuglink',
          '.shstrtab'
        ]
      },
      'entropy': 4.31812535428708,
      'format': 'elf'
    }
  }
];

export const filesData = {
  items: [
    {
      'id': 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
      'machineOsType': 'linux',
      'machineName': 'HarpServer',
      'machineAgentId': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      'agentVersion': '1.0.0.0',
      'scanStartTime': '2017-07-11T05:42:54.000Z',
      'checksumSha256': 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
      'path': '/usr/lib/systemd/system',
      'pathH8': '1778501902604433296',
      'fileName': 'systemd-journald.service',
      'owner': { 'username': 'root', 'groupname': 'root', 'uid': 0, 'gid': 0 },
      'timeModified': '2015-09-15T13:21:10.000Z',
      'timeAccessed': '2017-07-11T03:40:40.000Z',
      'mode': 33188,
      'sameDirectoryFileCounts': { 'nonExe': 193, 'exe': 1, 'subFolder': 0, 'exeSameCompany': 0, 'hiddenFiles': 0 },
      'directoryFeatures': ['usr'],
      'linux': {
        'systemds': [{
          'systemdPathH8': 0,
          'name': 'systemd-journald.service',
          'description': 'Journal Service',
          'state': 'loaded-active-running',
          'pid': 1697473911,
          'triggeredBy': ['systemd-journald.socket'],
          'triggerStrings': ['Stream=/run/systemd/journal/stdout', 'Datagram=/run/systemd/journal/socket', 'Datagram=/dev/log']
        }, {
          'systemdPathH8': 0,
          'name': 'systemd-journald.service',
          'description': 'Journal Service',
          'state': 'loaded-active-running',
          'pid': 1697473911,
          'triggeredBy': ['systemd-journald.socket'],
          'triggerStrings': ['Stream=/run/systemd/journal/stdout', 'Datagram=/run/systemd/journal/socket', 'Datagram=/dev/log']
        }]
      },
      'fileProperties': {
        'id': 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
        'firstFileName': 'systemd-journald.service',
        'machineOsType': 'linux',
        'size': 940,
        'checksumMd5': '11bb579c7fe34ccfb65de2026f6ba71f',
        'checksumSha1': 'b91de355f748ea4eb7328ed47d0f4d17d25c6b8a',
        'checksumSha256': 'b504d6ec4f75533d863a5a60af635fb5fc50fa60e1c2b9ec452bced9c0cacb33',
        'entropy': 5.197017123035716,
        'format': 'script'
      }
    },
    {
      'id': 'a565da7cb9d42a77f300b3eaeeddf73fab4293e442737fe453373ddc2fedf2be',
      'machineOsType': 'linux',
      'machineName': 'HarpServer',
      'machineAgentId': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      'agentVersion': '1.0.0.0',
      'scanStartTime': '2017-07-11T05:42:54.000Z',
      'checksumSha256': 'a565da7cb9d42a77f300b3eaeeddf73fab4293e442737fe453373ddc2fedf2be',
      'path': '/usr/lib/modules/3.10.0-229.20.1.el7.x86_64/kernel/drivers/ata',
      'pathH8': '8325233813913331491',
      'fileName': 'ata_generic.ko',
      'timeModified': '2015-11-03T19:49:05.000Z',
      'timeAccessed': '2017-07-11T05:42:55.000Z',
      'mode': 33188,
      'sameDirectoryFileCounts': { 'nonExe': 0, 'exe': 50, 'subFolder': 0, 'exeSameCompany': 0, 'hiddenFiles': 0 },
      'fileFeatures': ['partOfRPM'],
      'directoryFeatures': ['usr'],
      'rpm': { 'category': 'System Environment/Kernel' },
      'linux': {
        'drivers': [{
          'imageBase': 9223372036854775807,
          'imageSize': 12910,
          'numberOfInstances': 0,
          'loadState': 'Live',
          'dependencies': ['-'],
          'author': 'Alan Cox',
          'description': 'low-level driver for generic ATA',
          'sourceVersion': '51672935B1BE74E18674B67',
          'versionMagic': '3.10.0-229.20.1.el7.x86_64 SMP mod_unload modversions'
        }]
      },
      'fileProperties': {
        'id': 'a565da7cb9d42a77f300b3eaeeddf73fab4293e442737fe453373ddc2fedf2be',
        'firstFileName': 'ata_generic.ko',
        'machineOsType': 'linux',
        'size': 13757,
        'checksumMd5': 'b48e2ef833bab15a62f128e1293de1bb',
        'checksumSha1': '3ce5a7f5bcea2bb3971edfe417f41904f5e84837',
        'checksumSha256': 'a565da7cb9d42a77f300b3eaeeddf73fab4293e442737fe453373ddc2fedf2be',
        'elf': { 'classType': 0, 'data': 0, 'entryPoint': 0, 'features': ['arch64', 'lkm'], 'type': 1 },
        'entropy': 3.5172264322543323,
        'format': 'elf'
      }
    },
    {
      'id': '29772d95b2488a5a7715a96270f827fbf2b2e1e0a627ae041a54a6faddd2686c',
      'machineOsType': 'linux',
      'machineName': 'HarpServer',
      'machineAgentId': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      'agentVersion': '1.0.0.0',
      'scanStartTime': '2017-07-11T05:42:54.000Z',
      'checksumSha256': '29772d95b2488a5a7715a96270f827fbf2b2e1e0a627ae041a54a6faddd2686c',
      'path': '/usr/lib/systemd/system',
      'pathH8': '1778501902604433296',
      'fileName': 'rhel-readonly.service',
      'owner': { 'username': 'root', 'groupname': 'root', 'uid': 0, 'gid': 0 },
      'timeModified': '2015-01-15T08:57:03.000Z',
      'timeAccessed': '2017-07-11T03:40:40.000Z',
      'mode': 33188,
      'sameDirectoryFileCounts': { 'nonExe': 193, 'exe': 1, 'subFolder': 0, 'exeSameCompany': 0, 'hiddenFiles': 0 },
      'directoryFeatures': ['usr'],
      'linux': {
        'systemds': [{
          'systemdPathH8': 0,
          'name': 'rhel-readonly.service',
          'description': 'Configure read-only root support',
          'state': 'loaded-active-exited',
          'pid': 1697473911
        }, {
          'systemdPathH8': 0,
          'name': 'rhel-readonly.service',
          'description': 'Configure read-only root support',
          'state': 'loaded-active-exited',
          'pid': 1697473911
        }]
      },
      'fileProperties': {
        'id': '29772d95b2488a5a7715a96270f827fbf2b2e1e0a627ae041a54a6faddd2686c',
        'firstFileName': 'rhel-readonly.service',
        'machineOsType': 'linux',
        'size': 366,
        'checksumMd5': 'c0ddd7db5cd26fbacc986119659cb34c',
        'checksumSha1': 'bb74d291a2f40d6d9f2effb3c618d52267f4cfbb',
        'checksumSha256': '29772d95b2488a5a7715a96270f827fbf2b2e1e0a627ae041a54a6faddd2686c',
        'entropy': 4.682791635812802,
        'format': 'script'
      }
    }
  ],
  'pageNumber': 10,
  'pageSize': 100,
  'totalPages': 502,
  'totalItems': 50105,
  'hasNext': true,
  'hasPrevious': true
};

export const autorunsData = [
  {
    'id': '59d635be973e7702acf7e600',
    'machineOsType': 'linux',
    'machineName': 'HarpServer1',
    'machineAgentId': 'C3C5F14B-CE69-4C3E-8E74-73962CE7DCBF',
    'agentVersion': '1.0.0.0',
    'scanStartTime': 1507210681000,
    'checksumSha256': '4040cf29a55fd8eaa7ef2a40f8508988b10bacedc49e589aa1d1eb2cd7f02eed',
    'path': '/usr/lib/vmware-tools/bin64',
    'pathH8': '1384567574165710638',
    'fileName': 'vmware-user-suid-wrapper',
    'owner': {
      'username': 'root',
      'groupname': 'root',
      'uid': 0,
      'gid': 0
    },
    'timeModified': 1447237411000,
    'timeAccessed': 1507159360000,
    'mode': 35181,
    'sameDirectoryFileCounts': {
      'nonExe': 3,
      'exe': 6,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'fileFeatures': [
      'suid'
    ],
    'directoryFeatures': [
      'usr'
    ],
    'linux': {
      'autoruns': [
        {
          'label': 'VMware User Agent',
          'id': 'autoruns_13',
          'fileId': '59d635be973e7702acf7e600'
        }
      ],
      'initd': [
        {
          'description': ' Starts the Avahi Daemon',
          'fileId': '5a2a6d790d4a9d761fa473ba',
          'pid': 1857,
          'status': 'avahi-daemon (pid  1857) is running...',
          'type': 'Initds'
        }
      ],
      'crons': [
        {
          'id': 'crons_635',
          'launchArguments': '/etc/cron.hourly',
          'user': 'root'
        }
      ]
    },
    'fileProperties': {
      'id': '4040cf29a55fd8eaa7ef2a40f8508988b10bacedc49e589aa1d1eb2cd7f02eed',
      'firstFileName': 'vmware-user-suid-wrapper',
      'machineOsType': 'linux',
      'size': 10224,
      'checksumMd5': 'e2f3995d9218f16df453e0c6043f07c3',
      'checksumSha1': 'c40274b2775e775c25bd07d9dd90fc62efeb9351',
      'checksumSha256': '4040cf29a55fd8eaa7ef2a40f8508988b10bacedc49e589aa1d1eb2cd7f02eed',
      'elf': {
        'classType': 0,
        'data': 0,
        'entryPoint': 4197312,
        'features': [
          'arch64',
          'exe'
        ],
        'type': 2,
        'sectionNames': [
          '.interp',
          '.note.ABI-tag',
          '.hash',
          '.dynsym',
          '.dynstr',
          '.gnu.version',
          '.gnu.version_r',
          '.rela.dyn',
          '.rela.plt',
          '.init',
          '.plt',
          '.text',
          '.fini',
          '.rodata',
          '.modinfo',
          '.eh_frame_hdr',
          '.eh_frame',
          '.ctors',
          '.dtors',
          '.jcr',
          '.dynamic',
          '.got',
          '.data',
          '.bss',
          '.shstrtab'
        ],
        'importedLibraries': [
          'libc.so.6',
          'ld-linux-x86-64.so.2'
        ]
      },
      'entropy': 4.199051623390416,
      'format': 'elf'
    }
  },
  {
    'id': '59d635be973e7702acf7e527',
    'machineOsType': 'linux',
    'machineName': 'HarpServer1',
    'machineAgentId': 'C3C5F14B-CE69-4C3E-8E74-73962CE7DCBF',
    'agentVersion': '1.0.0.0',
    'scanStartTime': 1507210681000,
    'checksumSha256': '85c3ac6e4c93b4231cf33c2d7ccdd9fbae348e072eda1afcaaa7609fa75583ce',
    'path': '/etc/rc.d/init.d',
    'pathH8': '-4034902267557015157',
    'fileName': 'vmware-tools',
    'owner': {
      'username': 'root',
      'groupname': 'root',
      'uid': 0,
      'gid': 0
    },
    'timeModified': 1447237405000,
    'timeAccessed': 1507159226000,
    'mode': 33261,
    'sameDirectoryFileCounts': {
      'nonExe': 7,
      'exe': 1,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'directoryFeatures': [
      'etc'
    ],
    'linux': {
      'initds': [
        {
          'initdPathH8': 0,
          'pid': 0,
          'description': ' Manages the services needed to run VMware software# BEGINNING_OF_UTIL_DOT_SH#!/bin/sh## Copyright 2005-2011 VMware, Inc.',
          'status': 'vmtoolsd is running',
          'type': 'Initds',
          'id': 'initds_1',
          'fileId': '59d635be973e7702acf7e527'
        }
      ]
    },
    'fileProperties': {
      'id': '85c3ac6e4c93b4231cf33c2d7ccdd9fbae348e072eda1afcaaa7609fa75583ce',
      'firstFileName': 'vmware-tools',
      'machineOsType': 'linux',
      'size': 39849,
      'checksumMd5': '0837ef75ac44a6654bd00b59faa29c25',
      'checksumSha1': 'd4c130fea6bf56596ce7c70a6c8bd103a40cc4bc',
      'checksumSha256': '85c3ac6e4c93b4231cf33c2d7ccdd9fbae348e072eda1afcaaa7609fa75583ce',
      'entropy': 4.914375678229713,
      'format': 'script'
    }
  },
  {
    'id': '59d635be973e7702acf7e56f',
    'machineOsType': 'linux',
    'machineName': 'HarpServer1',
    'machineAgentId': 'C3C5F14B-CE69-4C3E-8E74-73962CE7DCBF',
    'agentVersion': '1.0.0.0',
    'scanStartTime': 1507210681000,
    'checksumSha256': '0f030f0a1c8124fca5e8b3a34ddc2ad206843227e753d0c10cc4ad1932564cee',
    'path': '/etc/cron.hourly',
    'pathH8': '-9012904399431781455',
    'fileName': '0anacron',
    'owner': {
      'username': 'root',
      'groupname': 'root',
      'uid': 0,
      'gid': 0
    },
    'timeModified': 1459436987000,
    'timeAccessed': 1507150891000,
    'mode': 33261,
    'sameDirectoryFileCounts': {
      'nonExe': 1,
      'exe': 1,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'directoryFeatures': [
      'etc'
    ],
    'linux': {
      'crons': [
        {
          'user': 'root',
          'triggerString': 'Runs every hour',
          'launchArguments': '',
          'id': 'crons_8',
          'fileId': '59d635be973e7702acf7e56f'
        }
      ]
    },
    'fileProperties': {
      'id': '0f030f0a1c8124fca5e8b3a34ddc2ad206843227e753d0c10cc4ad1932564cee',
      'firstFileName': '0anacron',
      'machineOsType': 'linux',
      'size': 392,
      'checksumMd5': '8675eb4a3dba8e20bd6b82c626304556',
      'checksumSha1': '69d2e72d5c15004ed6ae419e1e39abea623e3b95',
      'checksumSha256': '0f030f0a1c8124fca5e8b3a34ddc2ad206843227e753d0c10cc4ad1932564cee',
      'entropy': 4.735962722153822,
      'format': 'script'
    }
  }
];

export const hooksData = [
  {
    'id': '5b3f348cb249594f465125f2',
    'machineOsType': 'windows',
    'machineName': 'WIN-BKA6OVQGQI0',
    'machineAgentId': '3B1C9364-F6D1-6E1F-0552-B0F85F72AA70',
    'agentVersion': '11.3.0.0',
    'scanStartTime': 1530868761880,
    'checksumSha256': '0000000000000000000000000000000000000000000000000000000000000000',
    'path': '',
    'pathH8': 0,
    'fileName': '[FLOATING_CODE]',
    'mode': 0,
    'sameDirectoryFileCounts': {
      'nonExe': 0,
      'exe': 0,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'userModeFeatures': [
      'hookInline'
    ],
    'windows': {
      hooks: [
        {
          type: 'iat',
          features: [
            'userMode',
            'floating'
          ],
          jumpCount: 2,
          jumpTo: '0x7FF31C08C34',
          process: {
            pid: 2808,
            fileName: 'HookTest_x64.exe',
            createUtcTime: '2018-07-12T06:27:25.412Z'
          },
          hookLocation: {
            checksumSha256: '61c2abc56660f8cb8f26d371b6d2b769fdfe9db75d5c5f0d4308fc34888ff73b',
            path: 'C:\\Users\\kslp\\Desktop\\Windows_Simulator\\',
            fileName: 'HookTest_x64.exe',
            sectionBase: 0,
            imageBase: '0x13F700000',
            imageSize: 180224,
            address: '0x13F718000',
            symbol: 'crypt32.dll!CryptBinaryToStringA',
            symbolOffset: 0
          }
        },
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
            pid: 2808,
            fileName: 'HookTest_x64.exe',
            createUtcTime: '2018-07-12T06:27:25.412Z'
          },
          hookLocation: {
            checksumSha256: '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
            path: 'C:\\Windows\\System32\\',
            fileName: 'crypt32.dll',
            section: '.text',
            sectionBase: 8791750283264,
            imageBase: '0x7FEFD270000',
            imageSize: 1470464,
            address: '0x7FEFD27B390',
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
              'e96b3cfeff       ; JMP 0x7fefd25f000'
            ]
          }
        }
      ],
      threads: [
        {
          behaviorKey: '2916233153359241904',
          eprocess: '0xFFFFFA801B002B30',
          ethread: '0xFFFFFA801B002B30',
          fileId: '5b4f3474b249594c81984396',
          pid: 2164,
          processName: 'explorer.exe',
          processTime: 1531914845262,
          startAddress: '0xB5F1069',
          state: ['floating'],
          teb: '0x7FFFFF6E000',
          tid: 2164
        },
        {
          behaviorKey: '2916233153359241904',
          eprocess: '0xFFFFFA801B002B30',
          ethread: '0xFFFFFA801B002B30',
          fileId: '5b4f3474b249594c81984396',
          pid: 2164,
          processName: 'explorer.exe',
          processTime: 1531914845262,
          startAddress: '0x8811069',
          state: ['floating'],
          teb: '0x7FFFFF4C000',
          tid: 2164
        }
      ]
    }
  },
  {
    'id': '5b3f348cb249594f465125f3',
    'machineOsType': 'windows',
    'machineName': 'WIN-BKA6OVQGQI0',
    'machineAgentId': '3B1C9364-F6D1-6E1F-0552-B0F85F72AA70',
    'agentVersion': '11.3.0.0',
    'scanStartTime': 1530868761880,
    'checksumSha256': '0000000000000000000000000000000000000000000000000000000000000000',
    'path': '',
    'pathH8': 0,
    'fileName': '[FLOATING_CODE]',
    'mode': 0,
    'sameDirectoryFileCounts': {
      'nonExe': 0,
      'exe': 0,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'userModeFeatures': [
      'hookInline'
    ],
    'windows': {
      'hooks': [
        {
          'type': 'inline',
          'features': [
            'userMode',
            'floating',
            'onFunctionBounary'
          ],
          'jumpCount': 1,
          'jumpTo': '0x7FEFD23F000',
          'process': {
            'pid': 3844,
            'fileName': 'HookTest_x64.exe',
            'createUtcTime': '2018-07-06T09:19:08.230Z'
          },
          'hookLocation': {
            'checksumSha256': '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
            'path': 'C:\\Windows\\System32\\',
            'fileName': 'crypt32.dll',
            'section': '.text',
            'sectionBase': 8791750086656,
            'imageBase': '0x7FEFD240000',
            'imageSize': 1470464,
            'address': '0x7FEFD299478',
            'symbol': 'CertEnumSystemStore',
            'symbolOffset': 0
          },
          'inlinePatch': {
            'originalBytes': '4c8bdc49895b10',
            'originalAsm': [
              '4c8bdc           ; MOV R11, RSP',
              '49895b10         ; MOV [R11+0x10], RBX'
            ],
            'currentBytes': 'e9835bfaff',
            'currentAsm': [
              'e9835bfaff       ; JMP 0x7fefd23f000'
            ]
          }
        }
      ]
    }
  },
  {
    'id': '5b3f348cb249594f4651281b',
    'machineOsType': 'windows',
    'machineName': 'WIN-BKA6OVQGQI0',
    'machineAgentId': '3B1C9364-F6D1-6E1F-0552-B0F85F72AA70',
    'agentVersion': '11.3.0.0',
    'scanStartTime': 1530868761880,
    'checksumSha256': '5469da9747d23abd3a1ccffa2bccfe6256938a416f707ed2160d3eda3867c30d',
    'path': 'C:\\Users\\kslp\\AppData\\Local\\Temp\\',
    'pathH8': 0,
    'fileName': 'HookTest_DLL64_0f04.dll',
    'timeCreated': 1530868748292,
    'timeModified': 1469610156000,
    'timeAccessed': 1530868748292,
    'attributes': [
      'archive',
      'notContentIndexed'
    ],
    'mode': 0,
    'sameDirectoryFileCounts': {
      'nonExe': 14,
      'exe': 4,
      'subFolder': 4,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'fileFeatures': [
      'found'
    ],
    'directoryFeatures': [
      'temporary',
      'user',
      'appDataLocal'
    ],
    'userModeFeatures': [
      'loaded',
      'hookInline',
      'image'
    ],
    'windows': {
      'dlls': [
        {
          'pid': 3844,
          'imageBase': 8791599218688,
          'createTime': 1530868748230,
          'eprocess': '0xFFFFFA801A75D7D0',
          'imageSize': 172032
        }
      ],
      'hooks': [
        {
          'type': 'inline',
          'features': [
            'userMode',
            'onFunctionBounary'
          ],
          'jumpCount': 0,
          'jumpTo': '0x0',
          'process': {
            'pid': 3844,
            'fileName': 'HookTest_x64.exe',
            'createUtcTime': '2018-07-06T09:19:08.230Z'
          },
          'hookLocation': {
            'checksumSha256': '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
            'path': 'C:\\Windows\\System32\\',
            'fileName': 'crypt32.dll',
            'section': '.text',
            'sectionBase': 8791750086656,
            'imageBase': '0x7FEFD240000',
            'imageSize': 1470464,
            'address': '0x7FEFD26207C',
            'symbol': 'CertDuplicateStore',
            'symbolOffset': 0
          },
          'inlinePatch': {
            'originalBytes': 'f083410401',
            'originalAsm': [
              'f083410401       ; LOCK ADD DWORD [RCX+0x4], 0x1'
            ],
            'currentBytes': 'e97fcffbff',
            'currentAsm': [
              'e97fcffbff       ; JMP 0x7fefd21f000'
            ]
          }
        },
        {
          'type': 'inline',
          'features': [
            'userMode',
            'onFunctionBounary'
          ],
          'jumpCount': 0,
          'jumpTo': '0x0',
          'process': {
            'pid': 3844,
            'fileName': 'HookTest_x64.exe',
            'createUtcTime': '2018-07-06T09:19:08.230Z'
          },
          'hookLocation': {
            'checksumSha256': '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
            'path': 'C:\\Windows\\System32\\',
            'fileName': 'crypt32.dll',
            'section': '.text',
            'sectionBase': 8791750086656,
            'imageBase': '0x7FEFD240000',
            'imageSize': 1470464,
            'address': '0x7FEFD26207C',
            'symbol': 'CertDuplicateStore',
            'symbolOffset': 0
          },
          'inlinePatch': {
            'originalBytes': 'f083410401',
            'originalAsm': [
              'f083410401       ; LOCK ADD DWORD [RCX+0x4], 0x1'
            ],
            'currentBytes': 'e97fcffbff',
            'currentAsm': [
              'e97fcffbff       ; JMP 0x7fefd21f000'
            ]
          }
        },
        {
          'type': 'inline',
          'features': [
            'userMode',
            'onFunctionBounary'
          ],
          'jumpCount': 0,
          'jumpTo': '0x0',
          'process': {
            'pid': 3844,
            'fileName': 'HookTest_x64.exe',
            'createUtcTime': '2018-07-06T09:19:08.230Z'
          },
          'hookLocation': {
            'checksumSha256': '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
            'path': 'C:\\Windows\\System32\\',
            'fileName': 'crypt32.dll',
            'section': '.text',
            'sectionBase': 8791750086656,
            'imageBase': '0x7FEFD240000',
            'imageSize': 1470464,
            'address': '0x7FEFD26207C',
            'symbol': 'CertDuplicateStore',
            'symbolOffset': 0
          },
          'inlinePatch': {
            'originalBytes': 'f083410401',
            'originalAsm': [
              'f083410401       ; LOCK ADD DWORD [RCX+0x4], 0x1'
            ],
            'currentBytes': 'e97fcffbff',
            'currentAsm': [
              'e97fcffbff       ; JMP 0x7fefd21f000'
            ]
          }
        }
      ]
    },
    'fileProperties': {
      'id': '5469da9747d23abd3a1ccffa2bccfe6256938a416f707ed2160d3eda3867c30d',
      'firstFileName': 'HookTest_DLL64_061c.dll',
      'firstSeenTime': 1530681572797,
      'machineOsType': 'windows',
      'signature': {
        'features': [
          'unsigned'
        ]
      },
      'size': 150528,
      'checksumMd5': 'a91f3390e2fadbbcb2a347ba685cb22a',
      'checksumSha1': '5c6ff89eef54b7d5fba72889c2250ee09b04bcab',
      'checksumSha256': '5469da9747d23abd3a1ccffa2bccfe6256938a416f707ed2160d3eda3867c30d',
      'pe': {
        'timeStamp': 1450123256000,
        'imageSize': 172032,
        'numberOfExportedFunctions': 5,
        'numberOfNamesExported': 5,
        'numberOfExecuteWriteSections': 0,
        'features': [
          'dll',
          'pe64',
          'resourceDirectoryPresent',
          'relocationDirectoryPresent',
          'debugDirectoryPresent',
          'richSignaturePresent',
          'relocationDirectoryPresent'
        ],
        'resources': {
          'originalFileName': '',
          'company': '',
          'description': ''
        },
        'sectionNames': [
          '.text',
          '.rdata',
          '.data',
          '.pdata',
          '.rsrc',
          '.reloc'
        ],
        'importedLibraries': [
          'dbghelp.dll',
          'KERNEL32.dll',
          'WS2_32.dll',
          'SHLWAPI.dll',
          'imagehlp.dll',
          'WINHTTP.dll',
          'ADVAPI32.dll'
        ]
      },
      'entropy': 6.189471250232795,
      'format': 'pe'
    }
  },
  {
    'id': '5b3f348cb249594f4651281c',
    'machineOsType': 'windows',
    'machineName': 'WIN-BKA6OVQGQI0',
    'machineAgentId': '3B1C9364-F6D1-6E1F-0552-B0F85F72AA70',
    'agentVersion': '11.3.0.0',
    'scanStartTime': 1530868761880,
    'checksumSha256': '5e99949211beab5f6d72e66a2f1e40e0ada5ab4d9d1b29b60e5952dddfe4551f',
    'path': '',
    'pathH8': 0,
    'fileName': '[MEMORY_DLL_EF01C6AE31431AA02BE6DF1EA764A3D4]',
    'mode': 0,
    'sameDirectoryFileCounts': {
      'nonExe': 0,
      'exe': 0,
      'subFolder': 0,
      'exeSameCompany': 0,
      'hiddenFiles': 0
    },
    'userModeFeatures': [
      'loaded',
      'privateMemory'
    ],
    'processFeatures': [
      'hiddenDifferentialView'
    ],
    'windows': {
      'dlls': [
        {
          'pid': 3844,
          'imageBase': 8792632721408,
          'createTime': 1530868748230,
          'eprocess': '0xFFFFFA801A75D7D0',
          'imageSize': 192512
        }
      ],
      'hooks': [
        {
          'type': 'inline',
          'features': [
            'userMode',
            'floating',
            'onFunctionBounary',
            'trampoline'
          ],
          'jumpCount': 2,
          'jumpTo': '0x7FF31C08C34',
          'process': {
            'pid': 3844,
            'fileName': 'HookTest_x64.exe',
            'createUtcTime': '2018-07-06T09:19:08.230Z'
          },
          'hookLocation': {
            'checksumSha256': '276d1c9c78c529625c2ef3d77079324628686ea184767971901a1de93681c133',
            'path': 'C:\\Windows\\System32\\',
            'fileName': 'crypt32.dll',
            'section': '.text',
            'sectionBase': 8791750086656,
            'imageBase': '0x7FEFD240000',
            'imageSize': 1470464,
            'address': '0x7FEFD24B390',
            'symbol': 'CertGetCRLContextProperty',
            'symbolOffset': 0
          },
          'inlinePatch': {
            'originalBytes': '488bc133c9',
            'originalAsm': [
              '488bc1           ; MOV RAX, RCX',
              '33c9             ; XOR ECX, ECX'
            ],
            'currentBytes': 'e96b3cfeff',
            'currentAsm': [
              'e96b3cfeff       ; JMP 0x7fefd22f000'
            ]
          }
        }
      ],
      threads: [
        {
          behaviorKey: '2916233153359241904',
          eprocess: '0xFFFFFA801B002B30',
          ethread: '0xFFFFFA801B002B30',
          fileId: '5b4f3474b249594c81984396',
          pid: 3164,
          processName: 'iexplorer.exe',
          processTime: 1531914845262,
          startAddress: '0xB5F1069',
          state: ['floating'],
          teb: '0x7FFFFF6E007',
          tid: 2164
        },
        {
          behaviorKey: '2916233153359241904',
          eprocess: '0xFFFFFA801B002B30',
          ethread: '0xFFFFFA801B002B30',
          fileId: '5b4f3474b249594c81984396',
          pid: 7164,
          processName: 'xplorer.exe',
          processTime: 1531914845262,
          startAddress: '0x8811069',
          state: ['floating'],
          teb: '0x7FFFFF4C008',
          tid: 2164
        },
        {
          behaviorKey: '2916233153359241904',
          eprocess: '0xFFFFFA801B002B30',
          ethread: '0xFFFFFA801B002B30',
          fileId: '5b4f3474b249594c81984396',
          pid: 7164,
          processName: 'xprer.exe',
          processTime: 1531914845262,
          startAddress: '0x8811069',
          state: ['floating'],
          teb: '0x7FFFFF4C008',
          tid: 2164
        }
      ]
    },
    'fileProperties': {
      'id': '5e99949211beab5f6d72e66a2f1e40e0ada5ab4d9d1b29b60e5952dddfe4551f',
      'firstFileName': '[MEMORY_DLL_EF01C6AE31431AA02BE6DF1EA764A3D4]',
      'firstSeenTime': 1530681572797,
      'machineOsType': 'windows',
      'signature': {
        'features': [
          'unsigned'
        ]
      },
      'size': 0,
      'checksumMd5': 'ef01c6ae31431aa02be6df1ea764a3d4',
      'checksumSha1': '53f32db2c7221bdcf55be69387ec6ebffe1daa02',
      'checksumSha256': '5e99949211beab5f6d72e66a2f1e40e0ada5ab4d9d1b29b60e5952dddfe4551f',
      'pe': {
        'timeStamp': 1247535075000,
        'imageSize': 192512,
        'numberOfExportedFunctions': 10,
        'numberOfNamesExported': 10,
        'numberOfExecuteWriteSections': 0,
        'features': [
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
        'resources': {
          'originalFileName': 'keymgr.dll',
          'company': 'Microsoft Corporation',
          'description': 'Stored User Names and Passwords'
        },
        'sectionNames': [
          '.text',
          '.data',
          '.pdata',
          '.rsrc',
          '.reloc'
        ],
        'importedLibraries': [
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
      'entropy': 0,
      'format': 'pe'
    }
  }
];

export const processListData = [
  {
    'id': '59646561d8d4ae69695164a6',
    'pid': 612,
    'name': 'agetty',
    'parentPid': 1
  },
  {
    'id': '59646561d8d4ae69695164b9',
    'pid': 564,
    'name': 'auditd',
    'parentPid': 1
  },
  { 'id': '59646561d8d4ae69695164c6', 'pid': 596, 'name': 'bash', 'parentPid': 1 }, {
    'id': '59646561d8d4ae69695164c6',
    'pid': 597,
    'name': 'bash',
    'parentPid': 1
  }
];

export const processDetailsData = {
  'machineOsType': 'linux',
  'machineName': 'HarpServer',
  'machineAgentId': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
  'agentVersion': '1.0.0.0',
  'scanStartTime': '2017-07-11T05:42:54.000Z',
  'checksumSha256': '55cfc724e6f489bbddd145ad74846ff2a499869264a139377539523db95db082',
  'path': '/usr/lib/systemd',
  'pathH8': '6473163008475318373',
  'fileName': 'systemd-journald',
  'owner': { 'username': 'root', 'groupname': 'root', 'uid': 0, 'gid': 0 },
  'timeModified': '2015-09-15T13:21:29.000Z',
  'timeAccessed': '2017-07-11T05:42:54.000Z',
  'mode': 33261,
  'sameDirectoryFileCounts': { 'nonExe': 7, 'exe': 33, 'subFolder': 0, 'exeSameCompany': 0, 'hiddenFiles': 0 },
  'fileFeatures': ['partOfRPM'],
  'directoryFeatures': ['usr'],
  'rpm': { 'category': 'Unspecified' },
  'linux': {
    'processes': [{
      'pid': 459,
      'parentPid': 1,
      'imageBase': 0,
      'createUtcTime': '2017-06-28T04:19:43.000Z',
      'priority': -1,
      'uid': 0,
      'launchArguments': '/usr/lib/systemd/systemd-journald\u0000',
      'environment': 'PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin\u0000LANG=en_US.UTF-8\u0000NOTIFY_SOCKET=@/org/freedesktop/systemd1/notify\u0000LISTEN_PID=459\u0000LISTEN_FDS=3\u0000',
      'nice': 0,
      'threadCount': 0
    }]
  },
  'fileProperties': {
    'id': '55cfc724e6f489bbddd145ad74846ff2a499869264a139377539523db95db082',
    'firstFileName': 'systemd-journald',
    'machineOsType': 'linux',
    'size': 216160,
    'checksumMd5': '43dc990c8150fb2fe52cf4674a2c6cbd',
    'checksumSha1': '1e7e5dd27737060c8e79934e0e22813b135afdd2',
    'checksumSha256': '55cfc724e6f489bbddd145ad74846ff2a499869264a139377539523db95db082',
    'elf': { 'classType': 0, 'data': 0, 'entryPoint': 21556, 'features': ['arch64', 'so'], 'type': 3 },
    'entropy': 6.147272208934346,
    'format': 'elf',
    'signature': {
      'signer': 'Apple',
      'features': [ 'valid', 'signed' ]
    }
  }
};

export const processTreeData = [
  {
    'id': '59646561d8d4ae69695164a5',
    'pid': 1,
    'name': 'systemd',
    'parentPid': 0,
    'childProcesses': [
      {
        'id': '59646561d8d4ae696951648d',
        'pid': 898,
        'name': 'vmtoolsd',
        'parentPid': 1
      },
      {
        'id': '59646561d8d4ae6969516488',
        'pid': 836,
        'name': 'polkitd',
        'parentPid': 1
      }
    ]
  }
];

export const exploreData = [
  {
    machineAgentId: 'E1CD7550-B48B-B252-E55F-109A09E1B04A',
    scanStartTime: 1507245874393,
    files: [
      {
        id: '59d6c55a973e7702acf812c0',
        checksumSha256: '5830dbc77cb34c786b70441cdf5c407d77c054830b8b1e4a4579e10b5de043ac',
        path: 'C:\\Program Files\\Common Files\\VMware\\Drivers\\memctl',
        fileName: 'vmmemctl.sys',
        scanStartTime: 1507245874393,
        signature: 'Signed, VMware, Inc.',
        categories: [
          'DRIVERS',
          'SERVICES'
        ]
      },
      {
        id: '59d6c55a973e7702acf8158e',
        checksumSha256: 'c307d7a14ecc34e714755ebf0c4f21bcdbfec20fea737f0b8c4c9d680483b067',
        path: 'C:\\Windows\\System32',
        fileName: 'MemoryDiagnostic.dll',
        scanStartTime: 1507245874393,
        signature: 'Unsigned',
        categories: [
          'TASKS'
        ]
      }
    ]
  },
  {
    machineAgentId: 'E1CD7550-B48B-B252-E55F-109A09E1B04A',
    scanStartTime: 1507210527945,
    files: [
      {
        id: '59d63b6e973e7702acf80e46',
        checksumSha256: '5830dbc77cb34c786b70441cdf5c407d77c054830b8b1e4a4579e10b5de043ac',
        path: 'C:\\Program Files\\Common Files\\VMware\\Drivers\\memctl',
        fileName: 'vmmemctl.sys',
        scanStartTime: 1507210527945,
        signature: 'Signed, VMware, Inc.',
        categories: [
          'DRIVERS',
          'SERVICES'
        ]
      },
      {
        id: '59d63b6e973e7702acf81114',
        checksumSha256: 'c307d7a14ecc34e714755ebf0c4f21bcdbfec20fea737f0b8c4c9d680483b067',
        path: 'C:\\Windows\\System32',
        fileName: 'MemoryDiagnostic.dll',
        scanStartTime: 1507210527945,
        signature: 'Unsigned',
        categories: [
          'TASKS'
        ]
      }
    ]
  },
  {
    machineAgentId: 'E1CD7550-B48B-B252-E55F-109A09E1B04A',
    scanStartTime: 1507199477281,
    files: [
      {
        id: '59d6101a973e7702acf7c71f',
        checksumSha256: '5830dbc77cb34c786b70441cdf5c407d77c054830b8b1e4a4579e10b5de043ac',
        path: 'C:\\Program Files\\Common Files\\VMware\\Drivers\\memctl',
        fileName: 'vmmemctl.sys',
        scanStartTime: 1507199477281,
        signature: 'Signed, VMware, Inc.',
        categories: [
          'DRIVERS',
          'SERVICES'
        ]
      },
      {
        id: '59d6101a973e7702acf7c9ed',
        checksumSha256: 'c307d7a14ecc34e714755ebf0c4f21bcdbfec20fea737f0b8c4c9d680483b067',
        path: 'C:\\Windows\\System32',
        fileName: 'MemoryDiagnostic.dll',
        scanStartTime: 1507199477281,
        signature: 'Unsigned',
        categories: [
          'TASKS'
        ]
      }
    ]
  },
  {
    machineAgentId: 'E1CD7550-B48B-B252-E55F-109A09E1B04A',
    scanStartTime: 1507194589833,
    files: [
      {
        id: '59d5fd46973e7702acf7c2f1',
        checksumSha256: '5830dbc77cb34c786b70441cdf5c407d77c054830b8b1e4a4579e10b5de043ac',
        path: 'C:\\Program Files\\Common Files\\VMware\\Drivers\\memctl',
        fileName: 'vmmemctl.sys',
        scanStartTime: 1507194589833,
        signature: 'Signed, VMware, Inc.',
        categories: [
          'DRIVERS',
          'SERVICES'
        ]
      },
      {
        id: '59d5fd47973e7702acf7c5bf',
        checksumSha256: 'c307d7a14ecc34e714755ebf0c4f21bcdbfec20fea737f0b8c4c9d680483b067',
        path: 'C:\\Windows\\System32',
        fileName: 'MemoryDiagnostic.dll',
        scanStartTime: 1507194589833,
        signature: 'Unsigned',
        categories: [
          'TASKS'
        ]
      }
    ]
  },
  {
    machineAgentId: 'E1CD7550-B48B-B252-E55F-109A09E1B04A',
    scanStartTime: 1507192972346,
    files: [
      {
        id: '59d5f6f8973e7702acf7bec4',
        checksumSha256: '5830dbc77cb34c786b70441cdf5c407d77c054830b8b1e4a4579e10b5de043ac',
        path: 'C:\\Program Files\\Common Files\\VMware\\Drivers\\memctl',
        fileName: 'vmmemctl.sys',
        scanStartTime: 1507192972346,
        signature: 'Signed, VMware, Inc.',
        categories: [
          'DRIVERS',
          'SERVICES'
        ]
      },
      {
        id: '59d5f6f8973e7702acf7c192',
        checksumSha256: 'c307d7a14ecc34e714755ebf0c4f21bcdbfec20fea737f0b8c4c9d680483b067',
        path: 'C:\\Windows\\System32',
        fileName: 'MemoryDiagnostic.dll',
        scanStartTime: 1507192972346,
        signature: 'Unsigned',
        categories: [
          'TASKS'
        ]
      }
    ]
  }
];

export const dllListData = [
  {
    id: '5b335ffbb24959156220f9c4',
    machineOsType: 'windows',
    machineName: 'REMDBGDRV',
    machineAgentId: 'D4259B02-1EDB-53CE-56C0-8A03110C4D88',
    agentVersion: '11.2.0.0',
    scanStartTime: 1530093476248,
    checksumSha256: '2ab21ad49d358757b2176237a23f274d53a552cfd2266a5e43cbc25c8c10800b',
    path: 'C:\\Program Files\\VMware\\VMware Tools\\',
    pathH8: 0,
    fileName: 'gmodule-2.0.dll',
    timeCreated: 1489717596000,
    timeModified: 1489717596000,
    timeAccessed: 1496406783268,
    attributes: [
      'archive'
    ],
    mode: 0,
    sameDirectoryFileCounts: {
      nonExe: 10,
      exe: 35,
      subFolder: 7,
      exeSameCompany: 5,
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
      threads: [
        {
          processName: 'explorer.exe',
          processTime: 1531914845262,
          eprocess: '0xFFFFFA801B002B30',
          pid: 1392,
          ethread: '0xFFFFFA801B002B30',
          tid: 2164,
          teb: '0x7FFFFF6E000',
          startAddress: '0xB5F1069',
          state: [
            'floating'
          ],
          behaviorKey: '2916233153359241904'
        },
        {
          processName: 'explorer.exe',
          processTime: 1531914845262,
          eprocess: '0xFFFFFA801B002B30',
          pid: 2167,
          ethread: '0xFFFFFA801B002B30',
          tid: 2167,
          teb: '0x7FFFFF6E000',
          startAddress: '0xB5F1069',
          state: [
            'floating'
          ],
          behaviorKey: '2916233153359241904'
        },
        {
          processName: 'explorer.exe',
          processTime: 1531914845262,
          eprocess: '0xFFFFFA801B002B30',
          pid: 1392,
          ethread: '0xFFFFFA801B002B30',
          tid: 2164,
          teb: '0x7FFFFF4C000',
          startAddress: '0x8811069',
          state: [
            'floating'
          ],
          behaviorKey: '2916233153359241904'
        }
      ],
      dlls: [
        {
          pid: 1392,
          imageBase: 2097152,
          createTime: 1530092987886,
          eprocess: '0xFFFFFA801A64D4E0',
          imageSize: 36864
        },
        {
          pid: 1840,
          imageBase: 1048576,
          createTime: 1530093143515,
          eprocess: '0xFFFFFA8019123060',
          imageSize: 36864
        }
      ],
      hooks: [
        {
          type: 'inline',
          features: [
            'userMode',
            'trampoline'
          ],
          jumpCount: 2,
          jumpTo: '0x60000254',
          process: {
            pid: 1392,
            fileName: 'vmtoolsd.exe',
            createUtcTime: '2018-04-20T05:05:49.420Z'
          },
          hookLocation: {
            checksumSha256: 'cfcd25162159229be6b5f8e9759a444bdb5e6aa5f9ccd23e2a75f2f1afa2caf3',
            path: 'C:\\Winodws\\System32',
            fileName: 'gobject-2.0.dll',
            section: '.text',
            sectionBase: 268566528,
            imageBase: '0x10000000',
            imageSize: 409600,
            address: '0x10024455',
            symbol: 'GenerateObject',
            symbolOffset: 3
          },
          inlinePatch: {
            originalBytes: '8bff558bec83e4f8',
            originalAsm: [
              '8bff            mov     edi,edi',
              '55              push    ebp',
              '8bec            mov     ebp,esp',
              '83e4f8          and     esp,0FFFFFFF8h'
            ],
            currentBytes: '8bffe98986cdff',
            currentAsm: [
              '8bff            mov     edi,edi',
              'e98986cdff      jmp     7473f000'
            ]
          }
        },
        {
          type: 'inline',
          features: [
            'userMode',
            'trampoline'
          ],
          jumpCount: 2,
          jumpTo: '0x60000254',
          process: {
            pid: 1392,
            fileName: 'vmtoolsd.exe',
            createUtcTime: '2018-04-20T05:05:49.420Z'
          },
          hookLocation: {
            checksumSha256: 'cfcd25162159229be6b5f8e9759a444bdb5e6aa5f9ccd23e2a75f2f1afa2caf3',
            path: 'C:\\Winodws\\System32',
            fileName: 'gobject-2.0.dll',
            section: '.text',
            sectionBase: 268566528,
            imageBase: '0x10000000',
            imageSize: 409600,
            address: '0x10024455',
            symbol: 'DeleteObject',
            symbolOffset: 3
          },
          inlinePatch: {
            originalBytes: '8bff558bec83e4f8',
            originalAsm: [
              '8bff            mov     edi,edi',
              '55              push    ebp',
              '8bec            mov     ebp,esp',
              '83e4f8          and     esp,0FFFFFFF8h'
            ],
            currentBytes: '8bffe98986cdff',
            currentAsm: [
              '8bff            mov     edi,edi',
              'e98986cdff      jmp     7473f000'
            ]
          }
        },
        {
          type: 'inline',
          features: [
            'userMode',
            'trampoline'
          ],
          jumpCount: 2,
          jumpTo: '0x60000254',
          process: {
            pid: 1840,
            fileName: 'vmtoolsd.exe',
            createUtcTime: '2018-04-20T05:05:49.420Z'
          },
          hookLocation: {
            checksumSha256: 'cfcd25162159229be6b5f8e9759a444bdb5e6aa5f9ccd23e2a75f2f1afa2caf3',
            path: 'C:\\Winodws\\System32',
            fileName: 'gobject-2.0.dll',
            section: '.text',
            sectionBase: 268566528,
            imageBase: '0x10000000',
            imageSize: 409600,
            address: '0x10024455',
            symbol: 'GenerateObject',
            symbolOffset: 3
          },
          inlinePatch: {
            originalBytes: '8bff558bec83e4f8',
            originalAsm: [
              '8bff            mov     edi,edi',
              '55              push    ebp',
              '8bec            mov     ebp,esp',
              '83e4f8          and     esp,0FFFFFFF8h'
            ],
            currentBytes: '8bffe98986cdff',
            currentAsm: [
              '8bff            mov     edi,edi',
              'e98986cdff      jmp     7473f000'
            ]
          }
        },
        {
          type: 'inline',
          features: [
            'userMode',
            'trampoline'
          ],
          jumpCount: 2,
          jumpTo: '0x60000254',
          process: {
            pid: 1840,
            fileName: 'vmtoolsd.exe',
            createUtcTime: '2018-04-20T05:05:49.420Z'
          },
          hookLocation: {
            checksumSha256: 'cfcd25162159229be6b5f8e9759a444bdb5e6aa5f9ccd23e2a75f2f1afa2caf3',
            path: 'C:\\Winodws\\System32',
            fileName: 'gobject-2.0.dll',
            section: '.text',
            sectionBase: 268566528,
            imageBase: '0x10000000',
            imageSize: 409600,
            address: '0x10024455',
            symbol: 'DeleteObject',
            symbolOffset: 3
          },
          inlinePatch: {
            originalBytes: '8bff558bec83e4f8',
            originalAsm: [
              '8bff            mov     edi,edi',
              '55              push    ebp',
              '8bec            mov     ebp,esp',
              '83e4f8          and     esp,0FFFFFFF8h'
            ],
            currentBytes: '8bffe98986cdff',
            currentAsm: [
              '8bff            mov     edi,edi',
              'e98986cdff      jmp     7473f000'
            ]
          }
        }
      ]
    },
    fileProperties: {
      id: '2ab21ad49d358757b2176237a23f274d53a552cfd2266a5e43cbc25c8c10800b',
      firstFileName: 'gmodule-2.0.dll',
      firstSeenTime: 1530093476248,
      machineOsType: 'windows',
      signature: {
        timeStamp: 1489762597000,
        thumbprint: 'cef16150c61b0a1cee14a7c5d4cff80f645a6ab7',
        features: [
          'signed',
          'valid'
        ],
        signer: 'VMware, Inc.'
      },
      size: 33256,
      checksumMd5: 'c9b55e67cf0cda8cf350d8e96abd881d',
      checksumSha1: '716ef8d08840bbadee7f77cde95c56fcf02aba35',
      checksumSha256: '2ab21ad49d358757b2176237a23f274d53a552cfd2266a5e43cbc25c8c10800b',
      pe: {
        timeStamp: 1471639874000,
        imageSize: 36864,
        numberOfExportedFunctions: 8,
        numberOfNamesExported: 8,
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
          originalFileName: 'libgmodule-2.0-0.dll',
          company: 'The GLib developer community',
          description: 'GModule'
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
          'glib-2.0.dll',
          'KERNEL32.dll',
          'MSVCR90.dll'
        ]
      },
      entropy: 6.810589938365437,
      format: 'pe'
    }
  },
  {
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
  },
  {
    id: '5b335ffbb24959156220f9d3',
    machineOsType: 'windows',
    machineName: 'REMDBGDRV',
    machineAgentId: 'D4259B02-1EDB-53CE-56C0-8A03110C4D88',
    agentVersion: '11.2.0.0',
    scanStartTime: 1530093476248,
    checksumSha256: '60a8ddc0bdfe7ef1ea9a90622794b2208c5cd1982a1351b0d4a75e407588ecb4',
    path: 'C:\\Program Files\\VMware\\VMware Tools\\plugins\\vmsvc\\',
    pathH8: 0,
    fileName: 'deployPkgPlugin.dll',
    timeCreated: 1489717866000,
    timeModified: 1489717866000,
    timeAccessed: 1496406783393,
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
          imageBase: 8791676616704,
          createTime: 1530092987886,
          eprocess: '0xFFFFFA801A64D4E0',
          imageSize: 32768
        }
      ]
    },
    fileProperties: {
      id: '60a8ddc0bdfe7ef1ea9a90622794b2208c5cd1982a1351b0d4a75e407588ecb4',
      firstFileName: 'deployPkgPlugin.dll',
      firstSeenTime: 1530093476248,
      machineOsType: 'windows',
      signature: {
        timeStamp: 1489762866000,
        thumbprint: 'cef16150c61b0a1cee14a7c5d4cff80f645a6ab7',
        features: [
          'signed',
          'valid'
        ],
        signer: 'VMware, Inc.'
      },
      size: 29672,
      checksumMd5: '9ad5afe6f74cd447e03d897057dc977e',
      checksumSha1: 'ba373f9cf7492180c13c0484b34092cda2e1ad1c',
      checksumSha256: '60a8ddc0bdfe7ef1ea9a90622794b2208c5cd1982a1351b0d4a75e407588ecb4',
      pe: {
        timeStamp: 1489760457000,
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
          originalFileName: 'deployPkgPlugin.dll',
          company: 'VMware, Inc.',
          description: 'VMware Tools DeployPkg plugin'
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
          'deployPkg.dll',
          'KERNEL32.dll'
        ]
      },
      entropy: 6.760358063354543,
      format: 'pe'
    }
  },
  {
    id: '5b335ffbb24959156220f9d4',
    machineOsType: 'windows',
    machineName: 'REMDBGDRV',
    machineAgentId: 'D4259B02-1EDB-53CE-56C0-8A03110C4D88',
    agentVersion: '11.2.0.0',
    scanStartTime: 1530093476248,
    checksumSha256: '95807c5704972d29c56d7c6812aaabcbfd8976478b503ed462f7f2424a1d9296',
    path: 'C:\\Program Files\\VMware\\VMware Tools\\plugins\\vmsvc\\',
    pathH8: 0,
    fileName: 'bitMapper.dll',
    timeCreated: 1489717860000,
    timeModified: 1489717860000,
    timeAccessed: 1496406783393,
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
          imageBase: 8791676682240,
          createTime: 1530092987886,
          eprocess: '0xFFFFFA801A64D4E0',
          imageSize: 40960
        }
      ]
    },
    fileProperties: {
      id: '95807c5704972d29c56d7c6812aaabcbfd8976478b503ed462f7f2424a1d9296',
      firstFileName: 'bitMapper.dll',
      firstSeenTime: 1530093476248,
      machineOsType: 'windows',
      signature: {
        timeStamp: 1489762860000,
        thumbprint: 'cef16150c61b0a1cee14a7c5d4cff80f645a6ab7',
        features: [
          'signed',
          'valid'
        ],
        signer: 'VMware, Inc.'
      },
      size: 36840,
      checksumMd5: '4f8ef0363ec1d6ec5b3c932c00e4b149',
      checksumSha1: '9990c480745b3cce4eae98b6ff2f7e295d6e3ac7',
      checksumSha256: '95807c5704972d29c56d7c6812aaabcbfd8976478b503ed462f7f2424a1d9296',
      pe: {
        timeStamp: 1489760466000,
        imageSize: 40960,
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
          originalFileName: 'bitMapper.dll',
          company: 'VMware, Inc.',
          description: 'VMware Tools bitMapper plugin'
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
          'KERNEL32.dll'
        ]
      },
      entropy: 6.660540710302009,
      format: 'pe'
    }
  },
  {
    id: '5b335ffbb24959156220f9d5',
    machineOsType: 'windows',
    machineName: 'REMDBGDRV',
    machineAgentId: 'D4259B02-1EDB-53CE-56C0-8A03110C4D88',
    agentVersion: '11.2.0.0',
    scanStartTime: 1530093476248,
    checksumSha256: '0c97756baa5f37e854b189011f4222a1ba10e35b5e3ede4a705ee1d6524e27ac',
    path: 'C:\\Program Files\\VMware\\VMware Tools\\plugins\\vmsvc\\',
    pathH8: 0,
    fileName: 'autoUpgrade.dll',
    timeCreated: 1489717856000,
    timeModified: 1489717856000,
    timeAccessed: 1496406783393,
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
          imageBase: 8791676747776,
          createTime: 1530092987886,
          eprocess: '0xFFFFFA801A64D4E0',
          imageSize: 36864
        }
      ]
    },
    fileProperties: {
      id: '0c97756baa5f37e854b189011f4222a1ba10e35b5e3ede4a705ee1d6524e27ac',
      firstFileName: 'autoUpgrade.dll',
      firstSeenTime: 1530093476248,
      machineOsType: 'windows',
      signature: {
        timeStamp: 1489762855000,
        thumbprint: 'cef16150c61b0a1cee14a7c5d4cff80f645a6ab7',
        features: [
          'signed',
          'valid'
        ],
        signer: 'VMware, Inc.'
      },
      size: 32232,
      checksumMd5: 'a1677a70233aa34f26e1ad780d24f8d4',
      checksumSha1: 'e5831b704f8cbc0135bc2d6e86c6acf937712bde',
      checksumSha256: '0c97756baa5f37e854b189011f4222a1ba10e35b5e3ede4a705ee1d6524e27ac',
      pe: {
        timeStamp: 1489760455000,
        imageSize: 36864,
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
          originalFileName: 'autoUpgrade.dll',
          company: 'VMware, Inc.',
          description: 'VMware Tools AutoUpgrade plugin'
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
          'WTSAPI32.dll',
          'USER32.dll',
          'ADVAPI32.dll',
          'KERNEL32.dll'
        ]
      },
      entropy: 6.702875507953402,
      format: 'pe'
    }
  },
  {
    id: '5b335ffbb24959156220f9d6',
    machineOsType: 'windows',
    machineName: 'REMDBGDRV',
    machineAgentId: 'D4259B02-1EDB-53CE-56C0-8A03110C4D88',
    agentVersion: '11.2.0.0',
    scanStartTime: 1530093476248,
    checksumSha256: '84a5a562ed9a8427dc231ae609a4c0418634d169d30b20e9f0df9970608b862c',
    path: 'C:\\Program Files\\VMware\\VMware Tools\\plugins\\vmsvc\\',
    pathH8: 0,
    fileName: 'autoLogon.dll',
    timeCreated: 1489717850000,
    timeModified: 1489717850000,
    timeAccessed: 1496406783393,
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
          imageBase: 8791676813312,
          createTime: 1530092987886,
          eprocess: '0xFFFFFA801A64D4E0',
          imageSize: 49152
        }
      ],
      hooks: [
        {
          type: 'inline',
          features: [
            'userMode',
            'trampoline'
          ],
          jumpCount: 2,
          jumpTo: '0x60000254',
          process: {
            pid: 1392,
            fileName: 'vmtoolsd.exe',
            createUtcTime: '2018-04-20T05:05:49.420Z'
          },
          hookLocation: {
            checksumSha256: 'cfcd25162159229be6b5f8e9759a444bdb5e6aa5f9ccd23e2a75f2f1afa2caf3',
            path: 'C:\\Winodws\\System32',
            fileName: 'kernel32.dll',
            section: '.text',
            sectionBase: 268566528,
            imageBase: '0x10000000',
            imageSize: 409600,
            address: '0x10024455',
            symbol: 'CreateFileA',
            symbolOffset: 343
          },
          inlinePatch: {
            originalBytes: '8bff558bec83e4f8',
            originalAsm: [
              '8bff            mov     edi,edi',
              '55              push    ebp',
              '8bec            mov     ebp,esp',
              '83e4f8          and     esp,0FFFFFFF8h'
            ],
            currentBytes: '8bffe98986cdff',
            currentAsm: [
              '8bff            mov     edi,edi',
              'e98986cdff      jmp     7473f000'
            ]
          }
        },
        {
          type: 'inline',
          features: [
            'userMode',
            'trampoline'
          ],
          jumpCount: 2,
          jumpTo: '0x60000254',
          process: {
            pid: 1392,
            fileName: 'vmtoolsd.exe',
            createUtcTime: '2018-04-20T05:05:49.420Z'
          },
          hookLocation: {
            checksumSha256: 'cfcd25162159229be6b5f8e9759a444bdb5e6aa5f9ccd23e2a75f2f1afa2caf3',
            path: 'C:\\Winodws\\System32',
            fileName: 'Kernel32.dll',
            section: '.text',
            sectionBase: 268566528,
            imageBase: '0x10000000',
            imageSize: 409600,
            address: '0x10024455',
            symbol: 'ReadFile',
            symbolOffset: 3
          },
          inlinePatch: {
            originalBytes: '8bff558bec83e4f8',
            originalAsm: [
              '8bff            mov     edi,edi',
              '55              push    ebp',
              '8bec            mov     ebp,esp',
              '83e4f8          and     esp,0FFFFFFF8h'
            ],
            currentBytes: '8bffe98986cdff',
            currentAsm: [
              '8bff            mov     edi,edi',
              'e98986cdff      jmp     7473f000'
            ]
          }
        },
        {
          type: 'inline',
          features: [
            'userMode',
            'trampoline'
          ],
          jumpCount: 2,
          jumpTo: '0x60000254',
          process: {
            pid: 1392,
            fileName: 'vmtoolsd.exe',
            createUtcTime: '2018-04-20T05:05:49.420Z'
          },
          hookLocation: {
            checksumSha256: 'cfcd25162159229be6b5f8e9759a444bdb5e6aa5f9ccd23e2a75f2f1afa2caf3',
            path: 'C:\\Winodws\\System32',
            fileName: 'ntdll.dll',
            section: '.text',
            sectionBase: 268566528,
            imageBase: '0x10000000',
            imageSize: 409600,
            address: '0x10024455',
            symbol: 'NtConnect',
            symbolOffset: 3
          },
          inlinePatch: {
            originalBytes: '8bff558bec83e4f8',
            originalAsm: [
              '8bff            mov     edi,edi',
              '55              push    ebp',
              '8bec            mov     ebp,esp',
              '83e4f8          and     esp,0FFFFFFF8h'
            ],
            currentBytes: '8bffe98986cdff',
            currentAsm: [
              '8bff            mov     edi,edi',
              'e98986cdff      jmp     7473f000'
            ]
          }
        },
        {
          type: 'inline',
          features: [
            'userMode',
            'trampoline'
          ],
          jumpCount: 2,
          jumpTo: '0x60000254',
          process: {
            pid: 1840,
            fileName: 'vmtoolsd.exe',
            createUtcTime: '2018-04-20T05:05:49.420Z'
          },
          hookLocation: {
            checksumSha256: 'cfcd25162159229be6b5f8e9759a444bdb5e6aa5f9ccd23e2a75f2f1afa2caf3',
            path: 'C:\\Winodws\\System32',
            fileName: 'Kernel32.dll',
            section: '.text',
            sectionBase: 268566528,
            imageBase: '0x10000000',
            imageSize: 409600,
            address: '0x10024455',
            symbol: 'CreateFileA',
            symbolOffset: 3
          },
          inlinePatch: {
            originalBytes: '8bff558bec83e4f8',
            originalAsm: [
              '8bff            mov     edi,edi',
              '55              push    ebp',
              '8bec            mov     ebp,esp',
              '83e4f8          and     esp,0FFFFFFF8h'
            ],
            currentBytes: '8bffe98986cdff',
            currentAsm: [
              '8bff            mov     edi,edi',
              'e98986cdff      jmp     7473f000'
            ]
          }
        },
        {
          type: 'inline',
          features: [
            'userMode',
            'trampoline'
          ],
          jumpCount: 2,
          jumpTo: '0x60000254',
          process: {
            pid: 1840,
            fileName: 'vmtoolsd.exe',
            createUtcTime: '2018-04-20T05:05:49.420Z'
          },
          hookLocation: {
            checksumSha256: 'cfcd25162159229be6b5f8e9759a444bdb5e6aa5f9ccd23e2a75f2f1afa2caf3',
            path: 'C:\\Winodws\\System32',
            fileName: 'ntdll',
            section: '.text',
            sectionBase: 268566528,
            imageBase: '0x10000000',
            imageSize: 409600,
            address: '0x10024455',
            symbol: 'NtConnect',
            symbolOffset: 3
          },
          inlinePatch: {
            originalBytes: '8bff558bec83e4f8',
            originalAsm: [
              '8bff            mov     edi,edi',
              '55              push    ebp',
              '8bec            mov     ebp,esp',
              '83e4f8          and     esp,0FFFFFFF8h'
            ],
            currentBytes: '8bffe98986cdff',
            currentAsm: [
              '8bff            mov     edi,edi',
              'e98986cdff      jmp     7473f000'
            ]
          }
        }
      ]
    },
    fileProperties: {
      id: '84a5a562ed9a8427dc231ae609a4c0418634d169d30b20e9f0df9970608b862c',
      firstFileName: 'autoLogon.dll',
      firstSeenTime: 1530093476248,
      machineOsType: 'windows',
      signature: {
        timeStamp: 1489762850000,
        thumbprint: 'cef16150c61b0a1cee14a7c5d4cff80f645a6ab7',
        features: [
          'signed',
          'valid'
        ],
        signer: 'VMware, Inc.'
      },
      size: 49128,
      checksumMd5: 'eb6d78ad50f82286442ab1931617037e',
      checksumSha1: '7518cddbeef19e6a22ed720ca08b1f19bb6466b6',
      checksumSha256: '84a5a562ed9a8427dc231ae609a4c0418634d169d30b20e9f0df9970608b862c',
      pe: {
        timeStamp: 1489760465000,
        imageSize: 49152,
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
          originalFileName: 'autoLogon.dll',
          company: 'VMware, Inc.',
          description: 'VMware Tools Autologon plugin'
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
          'ADVAPI32.dll',
          'MSVCP90.dll',
          'KERNEL32.dll'
        ]
      },
      entropy: 6.437949207289836,
      format: 'pe'
    }
  },
  {
    id: '5b335ffbb24959156220f9dd',
    machineOsType: 'windows',
    machineName: 'REMDBGDRV',
    machineAgentId: 'D4259B02-1EDB-53CE-56C0-8A03110C4D88',
    agentVersion: '11.2.0.0',
    scanStartTime: 1530093476248,
    checksumSha256: 'ad77da2ba4a72628bdee26cf831ccf472472682dcd3c838263f6bff0186beea8',
    path: 'C:\\Program Files\\VMware\\VMware Tools\\',
    pathH8: 0,
    fileName: 'iconv.dll',
    timeCreated: 1489717598000,
    timeModified: 1489717598000,
    timeAccessed: 1496406783283,
    attributes: [
      'archive'
    ],
    mode: 0,
    sameDirectoryFileCounts: {
      nonExe: 10,
      exe: 35,
      subFolder: 7,
      exeSameCompany: 2,
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
          imageBase: 8791683760128,
          createTime: 1530092987886,
          eprocess: '0xFFFFFA801A64D4E0',
          imageSize: 958464
        },
        {
          pid: 1840,
          imageBase: 8791683760128,
          createTime: 1530093143515,
          eprocess: '0xFFFFFA8019123060',
          imageSize: 958464
        }
      ]
    },
    fileProperties: {
      id: 'ad77da2ba4a72628bdee26cf831ccf472472682dcd3c838263f6bff0186beea8',
      firstFileName: 'iconv.dll',
      firstSeenTime: 1530093476248,
      machineOsType: 'windows',
      signature: {
        timeStamp: 1489762598000,
        thumbprint: 'cef16150c61b0a1cee14a7c5d4cff80f645a6ab7',
        features: [
          'signed',
          'valid'
        ],
        signer: 'VMware, Inc.'
      },
      size: 958440,
      checksumMd5: '5d4b6d930f901c3e837aabae6df917f5',
      checksumSha1: '23c1b84c9319b04b4c1d674d9b9adc59cc99299e',
      checksumSha256: 'ad77da2ba4a72628bdee26cf831ccf472472682dcd3c838263f6bff0186beea8',
      pe: {
        timeStamp: 1467857299000,
        imageSize: 958464,
        numberOfExportedFunctions: 9,
        numberOfNamesExported: 9,
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
          originalFileName: 'iconv.dll',
          company: 'Free Software Foundation',
          description: 'LGPLed libiconv for Windows XP 64 and Vista 64'
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
          'KERNEL32.dll',
          'MSVCR90.dll'
        ]
      },
      entropy: 7.331881333893831,
      format: 'pe'
    }
  },
  {
    id: '5b335ffbb24959156220f9de',
    machineOsType: 'windows',
    machineName: 'REMDBGDRV',
    machineAgentId: 'D4259B02-1EDB-53CE-56C0-8A03110C4D88',
    agentVersion: '11.2.0.0',
    scanStartTime: 1530093476248,
    checksumSha256: '6900bab78df69487bb4aac747237ffdc09f523cd240bddf39d9979ea1700a6de',
    path: 'C:\\Program Files\\VMware\\VMware Tools\\',
    pathH8: 0,
    fileName: 'intl.dll',
    timeCreated: 1489717606000,
    timeModified: 1489717606000,
    timeAccessed: 1496406783283,
    attributes: [
      'archive'
    ],
    mode: 0,
    sameDirectoryFileCounts: {
      nonExe: 10,
      exe: 35,
      subFolder: 7,
      exeSameCompany: 2,
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
          imageBase: 8791684743168,
          createTime: 1530092987886,
          eprocess: '0xFFFFFA801A64D4E0',
          imageSize: 122880
        },
        {
          pid: 1840,
          imageBase: 8791684743168,
          createTime: 1530093143515,
          eprocess: '0xFFFFFA8019123060',
          imageSize: 122880
        }
      ]
    },
    fileProperties: {
      id: '6900bab78df69487bb4aac747237ffdc09f523cd240bddf39d9979ea1700a6de',
      firstFileName: 'intl.dll',
      firstSeenTime: 1530093476248,
      machineOsType: 'windows',
      signature: {
        timeStamp: 1489762605000,
        thumbprint: 'cef16150c61b0a1cee14a7c5d4cff80f645a6ab7',
        features: [
          'signed',
          'valid'
        ],
        signer: 'VMware, Inc.'
      },
      size: 122856,
      checksumMd5: '7f75a18a9e1280a1810e5ad1c99f4880',
      checksumSha1: '14c18bb0aab53660862192a0192a454516330e5d',
      checksumSha256: '6900bab78df69487bb4aac747237ffdc09f523cd240bddf39d9979ea1700a6de',
      pe: {
        timeStamp: 1392752301000,
        imageSize: 122880,
        numberOfExportedFunctions: 27,
        numberOfNamesExported: 27,
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
          originalFileName: 'intl.dll',
          company: 'Free Software Foundation',
          description: 'LGPLed libintl for Windows NT/2000/XP and Windows 95/98/ME'
        },
        sectionNames: [
          '.text',
          '.rdata',
          '.data',
          '.pdata',
          '.idata',
          '.rsrc',
          '.reloc'
        ],
        importedLibraries: [
          'iconv.dll',
          'KERNEL32.dll',
          'MSVCR90.dll'
        ]
      },
      entropy: 5.274486249921894,
      format: 'pe'
    }
  }
];
