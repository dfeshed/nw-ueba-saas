/* eslint-env node */

export default function() {
  return [
    {
      id: 'c6eb6b013cc291ee055d3b7c0401a373827b0ed7607ead749bbc1b639c57eda0',
      firstFileName: 'services.exe',
      firstSeenTime: 1522731973476,
      machineOsType: 'windows',
      signature: {
        timeStamp: 1343276415360,
        thumbprint: '7de47e2554ce16104732642b874fe89c32591fbe',
        features: [
          'microsoft',
          'signed',
          'valid',
          'catalog'
        ],
        signer: 'Microsoft Windows'
      },
      size: 333312,
      checksumMd5: '575fb4211bb07db7d2179b1b05fe7efd',
      checksumSha1: 'ab3281274730d34ba320bcd91257867c56a2b2cc',
      checksumSha256: 'c6eb6b013cc291ee055d3b7c0401a373827b0ed7607ead749bbc1b639c57eda0',
      pe: {
        timeStamp: 1343270377000,
        imageSize: 344064,
        numberOfExportedFunctions: 0,
        numberOfNamesExported: 0,
        numberOfExecuteWriteSections: 0,
        features: [
          'exe',
          'pe32',
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
          originalFileName: 'services.exe',
          company: 'Microsoft Corporation',
          description: 'Services and Controller app'
        },
        sectionNames: [
          '.text',
          '.data',
          '.idata',
          '.rsrc',
          '.reloc'
        ],
        importedLibraries: [
          'api-ms-win-core-crt-l1-1-0.dll',
          'api-ms-win-core-crt-l2-1-0.dll',
          'RPCRT4.dll',
          'SspiCli.dll',
          'ntdll.dll',
          'profapi.dll',
          'api-ms-win-security-lsalookup-l1-1-1.dll',
          'api-ms-win-security-sddl-l1-1-0.dll',
          'CRYPTBASE.dll',
          'api-ms-win-core-errorhandling-l1-1-1.dll',
          'api-m…'
        ]
      },
      entropy: 6.462693785416757,
      format: 'pe'
    }
  ];
}