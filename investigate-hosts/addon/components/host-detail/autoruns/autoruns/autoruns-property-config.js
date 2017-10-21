import defaultPropertyConfig from 'investigate-hosts/components/host-detail/base-property-config';

const driverProperty = [
  {
    sectionName: 'Image',
    fields: [
      {
        field: 'loaded'
      }
    ]
  }
];

const config = [...defaultPropertyConfig, ...driverProperty];

export default config;
