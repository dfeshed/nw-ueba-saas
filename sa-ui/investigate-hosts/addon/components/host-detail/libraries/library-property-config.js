import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';

const libraryProperty = [
  {
    sectionName: 'Process',
    fields: [
      {
        field: 'createTime',
        format: 'DATE'
      },
      {
        field: 'eprocess',
        format: 'HEX'
      },
      {
        field: 'pid'
      },
      {
        field: 'imageBase',
        format: 'HEX'
      },
      {
        field: 'imageSize',
        format: 'SIZE'
      }
    ]
  }
];

const config = [...defaultPropertyConfig, ...libraryProperty];

export default config;
