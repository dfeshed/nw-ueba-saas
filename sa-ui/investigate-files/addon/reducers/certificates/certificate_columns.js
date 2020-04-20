const CERTIFICATE_COLUMNS_CONFIG = [
  {
    field: 'issuer',
    title: 'configure.endpoint.certificates.columns.issuer',
    label: 'Issuer',
    width: '35vw',
    disableSort: true,
    visible: true
  },
  {
    field: 'thumbprint',
    title: 'configure.endpoint.certificates.columns.thumbprint',
    label: 'Thumb Print',
    width: '35vw',
    disableSort: true,
    visible: true
  },
  {
    field: 'notValidBeforeUtcDate',
    title: 'configure.endpoint.certificates.columns.notValidBeforeUtcDate',
    label: 'notValidBeforeUtcDate',
    width: '20vw',
    disableSort: false,
    visible: true
  },
  {
    field: 'notValidAfterUtcDate',
    title: 'configure.endpoint.certificates.columns.notValidAfterUtcDate',
    label: 'notValidAfterUtcDate',
    width: '20vw',
    disableSort: false,
    visible: true
  },
  {
    field: 'subject',
    title: 'configure.endpoint.certificates.columns.subject',
    label: 'Subject',
    width: '35vw',
    disableSort: true,
    visible: true
  },
  {
    field: 'subjectKey',
    title: 'configure.endpoint.certificates.columns.subjectKey',
    label: 'Subject Key',
    width: '35vw',
    disableSort: true,
    visible: true
  },
  {
    field: 'serial',
    title: 'configure.endpoint.certificates.columns.serial',
    label: 'Serial',
    width: '35vw',
    disableSort: true,
    visible: true
  },
  {
    field: 'authorityKey',
    title: 'configure.endpoint.certificates.columns.authorityKey',
    label: 'Authority Key',
    width: '35vw',
    disableSort: true,
    visible: true
  }
];
export default CERTIFICATE_COLUMNS_CONFIG;
