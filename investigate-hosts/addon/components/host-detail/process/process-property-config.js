import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';

const fileProperty = [
  {
    sectionName: 'Process',
    fields: [
      {
        field: 'process.createUtcTime',
        format: 'DATE'
      },
      {
        field: 'process.eprocess'
      },
      {
        field: 'process.integrityLevel'
      },
      {
        field: 'process.parentPath'
      },
      {
        field: 'process.threadCount'
      },
      {
        field: 'process.sessionId'
      }
    ]
  },
  {
    sectionName: 'Image',
    fields: [
      {
        field: 'process.imageBase',
        format: 'HEX'
      },
      {
        field: 'process.imageSize',
        format: 'SIZE'
      }
    ]
  }
];

const config = [...defaultPropertyConfig, ...fileProperty];

export default config;
