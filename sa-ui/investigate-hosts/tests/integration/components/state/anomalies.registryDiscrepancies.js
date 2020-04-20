export default {
  machineOsType: 'windows',
  registryDiscrepancies: [
    {
      checksumSha256: '506116110c3b84b77578af5f445698048ee2e9e84aa19b8b3939ae103bcdeb10',
      hive: 'hkeyLocalMachine',
      reason: 'notFound',
      registryPath: 'SYSTEM\\ControlSet001\\Services\\NWEDriver13550\\'
    },
    {
      checksumSha256: '1936e11d68f0170b27aa61c139c7e06a00055df9620a968959e22ae53727f796',
      hive: 'hkeyLocalMachine',
      reason: 'notFound',
      registryPath: 'SYSTEM\\ControlSet001\\Services\\NWEDriver13550\\Instances\\',
      dataMismatch: {
        rawType: 'expandString',
        rawData: '\\SystemRoot\\system32\\drivers\\isapnp.sys',
        apiType: 'string',
        apiData: 'C:\\temp\\fake_reg_test.exe'
      }
    },
    {
      checksumSha256: '5945391f60f03bd804b24114ec3e3750483bf4903c7174e034ed3617ddf34ef9',
      hive: 'hkeyLocalMachine',
      reason: 'accessDenied',
      registryPath: 'SYSTEM\\ControlSet001\\Services\\NWEDriver13550\\@Type'
    },
    {
      checksumSha256: '5945391f60f03bd804b24114ec3e3750483bf4903c7174e034ed3617ddf34ef9',
      hive: 'hkeyLocalMachine',
      reason: 'accessDenied',
      registryPath: 'SYSTEM\\ControlSet001\\Services\\NWEDriver13550\\@Start',
      dataMismatch: {
        rawType: 'expandString',
        rawData: '\\SystemRoot\\system32\\drivers\\isapnp.sys',
        apiType: 'string',
        apiData: 'C:\\temp\\fake_reg_test.exe'
      }
    },
    {
      checksumSha256: '5945391f60f03bd804b24114ec3e3750483bf4903c7174e034ed3617ddf34ef9',
      hive: 'hkeyLocalMachine',
      reason: 'accessDenied',
      registryPath: 'SYSTEM\\ControlSet001\\Services\\NWEDriver13550\\@ErrorControl'
    }
  ]
};
