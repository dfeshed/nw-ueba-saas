import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';

const driverProperty = [
  {
    sectionName: 'Image',
    fields: [
      {
        field: 'imageBase',
        format: 'HEX'
      },
      {
        field: 'imageSize',
        format: 'SIZE'
      },
      {
        field: 'loaded'
      }
    ]
  }
];

const config = [...defaultPropertyConfig, ...driverProperty];

export default config;
