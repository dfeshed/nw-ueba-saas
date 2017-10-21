const tabs = [
  {
    label: 'investigateHosts.tabs.hostFileEntries',
    name: 'HOST_ENTRIES',
    id: '1'
  },
  {
    label: 'investigateHosts.tabs.mountedPaths',
    hiddenFor: ['windows'],
    name: 'MOUNTED_PATH',
    id: '2'
  },
  {
    label: 'investigateHosts.tabs.networkShares',
    name: 'NETWORK_SHARES',
    hiddenFor: ['mac', 'linux'],
    id: '3'
  },
  {
    label: 'investigateHosts.tabs.bashHistories',
    name: 'BASH_HISTORY',
    hiddenFor: ['windows'],
    id: '4'
  }
];

export default tabs;
