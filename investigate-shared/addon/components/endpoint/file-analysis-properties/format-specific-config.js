export default {
  pe: [
    {
      sectionName: 'Image Details',
      fields: [
        {
          field: 'pe.architecture'
        },
        {
          field: 'pe.characteristics'
        },
        {
          field: 'pe.compileTime',
          format: 'DATE'
        },
        {
          field: 'pe.entryPoint'
        },
        {
          field: 'pe.importedDlls',
          format: 'ACCORDIONLIST'
        },
        {
          field: 'pe.sectionNames',
          format: 'ACCORDION',
          label: 'investigateShared.endpoint.fileAnalysis.pe.sectionNames'
        },
        {
          field: 'pe.subSystem'
        }
      ]
    },
    {
      sectionName: 'Packaging Detection',
      fields: [
        {
          field: 'pe.entryPointValid'
        },
        {
          field: 'pe.uncommonSectionFound'
        },
        {
          field: 'entropy'
        },
        {
          field: 'pe.packerSectionFound'
        }
      ]
    }
  ],
  macho: [
    {
      sectionName: 'Image Details',
      fields: [
        {
          field: 'macho.architecture'
        },
        {
          field: 'macho.entryPoint'
        },
        {
          field: 'macho.importedDlls',
          format: 'ACCORDIONLIST'
        },
        {
          field: 'macho.segmentNames',
          format: 'ACCORDION',
          label: 'investigateShared.endpoint.fileAnalysis.macho.segmentNames'
        },
        {
          field: 'macho.uuid'
        },
        {
          field: 'macho.fileType'
        }
      ]
    },
    {
      sectionName: 'Packaging Detection',
      fields: [
        {
          field: 'macho.entryPointValid'
        },
        {
          field: 'macho.uncommonSectionFound'
        },
        {
          field: 'entropy'
        },
        {
          field: 'macho.packerSectionFound'
        }
      ]
    }
  ],
  elf: [
    {
      sectionName: 'Image Details',
      fields: [
        {
          field: 'elf.architecture'
        },
        {
          field: 'elf.entryPoint'
        },
        {
          field: 'elf.neededLibraries',
          format: 'ACCORDION',
          label: 'investigateShared.endpoint.fileAnalysis.elf.neededLibraries'
        },
        {
          field: 'elf.sectionNames',
          format: 'ACCORDION',
          label: 'investigateShared.endpoint.fileAnalysis.elf.sectionNames'
        },
        {
          field: 'elf.fileType'
        }
      ]
    },
    {
      sectionName: 'Packaging Detection',
      fields: [
        {
          field: 'elf.entryPointValid'
        },
        {
          field: 'elf.uncommonSectionFound'
        },
        {
          field: 'entropy'
        },
        {
          field: 'elf.packerSectionFound'
        }
      ]
    }
  ],
  default: [
    {
      sectionName: 'Packaging Detection',
      fields: [
        {
          field: 'entropy'
        }
      ]
    }
  ]
};