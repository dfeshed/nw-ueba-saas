const SECURITY_CONFIG = {
  windows: [
    {
      keyword: 'cookiesCleanup',
      value: 'Cookies Cleanup',
      label: {
        red: 'Cannot delete browsing history as Delete Browsing History in IE is disabled.',
        green: 'Can delete browsing history in IE by default.'
      }
    },
    {
      keyword: 'crossSiteScriptFilter',
      value: 'Cross Site Script Filter',
      label: {
        red: 'Potentially malicious script will not be blocked as XSS filter of IE is disabled. ',
        green: 'XSS filter of IE is enabled by default.'
      }
    },
    {
      keyword: 'smartScreenFilter',
      value: 'Smart Screen Filter',
      label: {
        red: 'Personal and Financial information may be at risk as SmartScreen filter for IE is disabled.',
        green: 'SmartScreen filter for IE is enabled by default.'
      }
    },
    {
      keyword: 'allowAccessDataSourceDomain',
      value: 'Allow Access Datasource Domain',
      label: {
        red: 'Access to data sources across domains in IE is enabled.',
        green: 'Access to data sources across domains in IE is disabled.'
      }
    },
    {
      keyword: 'allowDisplayMixedContent',
      value: 'Allow Display Mixed Content',
      label: {
        red: 'Secure websites containing unsecured content is allowed in IE. ',
        green: 'Only secure content is displayed when browsing sites with mixed content (HTTP andHTTPS) by default.'
      }
    },
    {
      keyword: 'badCertificateWarning',
      value: 'Bad Certificate Warning',
      label: {
        red: 'No notification while visiting malicious websites as Check for server certificate revocation option in IE is disabled.',
        green: 'Notifies while visiting websites with invalid certificates by default.'
      }
    },
    {
      keyword: 'intranetZoneNotification',
      value: 'Intranet Zone Notification',
      label: {
        red: 'Protected mode disabled for Intranet zone',
        green: 'Protected mode enabled for Intranet zone and mapping rules are applied.'
      }
    },
    {
      keyword: 'warningPostRedirection',
      value: 'Warning Post Redirection',
      label: {
        red: 'No notification if POST requests is redirected to a zone that does not permit posts in IE.',
        green: 'Notifies if POST requests is redirected to a zone that does not permit posts in IE by default.'
      }
    },
    {
      keyword: 'warningOnZoneCrossing',
      value: 'Warning On Zone Crossing',
      label: {
        red: 'No notification about unsecure requests on secured websites in IE.',
        green: 'Notifies about unsecure requests on secured websites in IE.'
      }
    },
    {
      keyword: 'ieEnhancedSecurity',
      value: 'IE Enhanced Security',
      label: {
        red: 'Agent machine is more vulnerable as IE Enhanced Security Configuration is disabled.',
        green: 'IE Enhanced Security Configuration is enabled.'
      }
    },
    {
      keyword: 'registryTools',
      value: 'Registry Tools',
      label: {
        red: 'Access to Registry Editor disabled.',
        green: 'Access to Registry Editor enabled.'
      }
    },
    {
      keyword: 'taskManager',
      value: 'Task Manager',
      label: {
        red: 'Cannot view processes as Task Manager is disabled.',
        green: 'Can view processes using Task Manager.'
      }
    },
    {
      keyword: 'windowsUpdate',
      value: 'Windows Update',
      label: {
        red: 'Can connect to Windows update website.',
        green: 'Cannot connect to Windows update website.'
      }
    },
    {
      keyword: 'antiVirus',
      value: 'Anti-virus',
      label: {
        red: 'Antivirus software disabled.',
        green: 'Antivirus software enabled by default.'
      }
    },
    {
      keyword: 'firewall',
      value: 'Firewall',
      label: {
        red: 'Firewall software disabled.',
        green: 'Firewall software enabled by default.'
      }
    },
    {
      keyword: 'uac',
      value: 'UAC',
      label: {
        red: 'User Access Control settings disabled.',
        green: 'User Access Control settings enabled by default.'
      }
    },
    {
      keyword: 'lua',
      value: 'LUA',
      label: {
        red: 'Administrator in Admin Approval Mode user type is disabled.',
        green: 'Administrator in Admin Approval Mode user type and Limited User Account polices are enabled by default.'
      }
    },
    {
      keyword: 'systemRestore',
      value: 'System Restore',
      label: {
        red: 'Cannot create restore points as System Restore is disabled.',
        green: 'System Restore enabled by default.'
      }
    },
    {
      keyword: 'ieDep',
      value: 'IE Dep',
      label: {
        red: 'Harmful programs can execute from reserved locations as Data Execution Prevention is disabled.',
        green: 'Data Execution Prevention is enabled by default.'
      }
    }
  ],
  mac: [
    {
      keyword: 'safariFraudWebsiteWarning',
      value: 'Safari Fraud Website Warning',
      label: {
        red: 'Warn when visiting a fraudulent website security setting in Safari is disabled.',
        green: 'Warn when visiting a fraudulent website security setting in Safari is enabled.'
      }
    },
    {
      keyword: 'kextSigningDisabled',
      value: 'Kext Signing Disabled',
      label: {
        red: 'Unsigned kernel extensions are allowed to be loaded on this agent.',
        green: 'Unsigned kernel extensions are not allowed to be loaded on this agent.'
      }
    },
    {
      keyword: 'fileVault',
      value: 'File Vault',
      label: {
        red: 'FileVault encryption disabled.',
        green: 'FileVault encryption enabled.'
      }
    },
    {
      keyword: 'systemIntegrityProtection',
      value: 'System Integrity Protection',
      label: {
        red: 'File system and processes are not protected by System Integrity Protection.',
        green: 'System Integrity Protection settings are enabled by default.'
      }
    },
    {
      keyword: 'firewall',
      value: 'Firewall',
      label: {
        red: 'Firewall disabled.',
        green: 'Firewall enabled'
      }
    },
    {
      keyword: 'gatekeeper',
      value: 'Gate Keeper',
      label: {
        red: 'Can download any application as Gatekeeper option is disabled.',
        green: 'Cannot download application as Gatekeeper option is enabled.'
      }
    },
    {
      keyword: 'sudoersNoPasswordPrompt',
      value: 'Sudoers Password Prompt',
      label: {
        red: 'Can run sudo command without a password.',
        green: 'Password is required to run sudo command.'
      }
    }
  ],
  linux: []
};

export default SECURITY_CONFIG;