import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';

const fileProperty = [
  {
    sectionName: 'Process',
    fields: [
      {
        field: 'process.createTime',
        format: 'DATE'
      },
      {
        field: 'process.eprocess',
        format: 'HEX'
      },
      {
        field: 'process.integrityLevel'
      },
      {
        field: 'process.parentPath'
      },
      {
        field: 'process.floatingThreadCount'
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
