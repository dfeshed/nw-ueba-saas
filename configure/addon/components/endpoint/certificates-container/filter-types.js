/**
 * A set of types representing the time range since a starting point time (e.g., since 7 days ago)
 * @public
 * @type {[*]}
 */
const FILTER_TYPES = [
  {
    name: 'certificateStatus',
    label: 'configure.endpoint.certificates.columns.certificateStatus',
    type: 'list',
    listOptions: [
      { name: 'Neutral', label: 'configure.endpoint.certificates.status.statusOptions.neutral' },
      { name: 'Whitelisted', label: 'configure.endpoint.certificates.status.statusOptions.whitelisted' },
      { name: 'Blacklisted', label: 'configure.endpoint.certificates.status.statusOptions.blacklisted' }
    ]
  },
  {
    name: 'features',
    label: 'configure.endpoint.certificates.filter.signature',
    type: 'list',
    listOptions: [
      { name: 'cert.rootMicrosoft', label: 'configure.endpoint.certificates.filter.isRootMicrosoft' }
    ]
  },
  {
    'name': 'friendlyName',
    'label': 'configure.endpoint.certificates.columns.friendlyName',
    'type': 'text',
    'validations': {
      length: {
        validator: (value) => {
          return value.length > 256;
        },
        message: 'configure.endpoint.certificates.filter.invalidFilterInputLength'
      }
    }
  },
  {
    'name': 'thumbprint',
    'label': 'configure.endpoint.certificates.columns.thumbprint',
    'type': 'text'
  }
];

export {
    FILTER_TYPES
};
