import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';

const driverProperty = [
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

const config = [...defaultPropertyConfig, ...driverProperty];

export default config;
