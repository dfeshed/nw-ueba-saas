/* eslint-env node */

module.exports = {
  'id': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
  'machine': {
    'machineAgentId': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
    'agentVersion': '1.0.0.0',
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
      'ipv6': 'fe80::250:56ff:fe01:4701',
      'gateway': ['0.0.0.0'],
      'promiscuous': false
    }, {
      'name': 'ens32',
      'macAddress': '00:50:56:01:2B:B5',
      'ipv4': '10.40.15.187',
      'ipv6': 'fe80::250:56ff:fe01:2bb5',
      'gateway': ['10.40.12.1'],
      'promiscuous': false
    }, { 'name': 'lo', 'ipv4': '127.0.0.1', 'ipv6': '::1', 'gateway': ['0.0.0.0'], 'promiscuous': false }],
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
    }]
  },
  'machineIdentity': {
    'id': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
    'group': 'default',
    'machineName': 'HarpServer',
    'agent': {
      'installTime': '2017-06-20T07:09:01.000Z',
      'serviceStartTime': '2017-06-28T04:19:45.000Z',
      'serviceEprocess': 0,
      'serviceProcessId': 0,
      'serviceErrorCode': 0,
      'driverErrorCode': 0,
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