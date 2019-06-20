export default [
  {
    dataType: 'checkbox',
    width: 20,
    class: 'rsa-form-row-checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true,
    headerComponentClass: 'rsa-form-checkbox'
  },
  {
    dataType: 'string',
    width: '15vw',
    visible: true,
    field: 'fileName',
    searchable: true,
    disableSort: true,
    title: 'investigateHosts.downloads.tableHeader.filename'
  },
  {
    dataType: 'string',
    visible: true,
    field: 'fileType',
    searchable: false,
    disableSort: true,
    title: 'investigateHosts.downloads.tableHeader.fileType'
  },
  {
    dataType: 'string',
    visible: true,
    field: 'status',
    searchable: false,
    disableSort: true,
    title: 'investigateHosts.downloads.tableHeader.downloaded'
  },
  {
    dataType: 'string',
    visible: true,
    field: 'size',
    searchable: false,
    disableSort: true,
    title: 'investigateHosts.downloads.tableHeader.fileSize'
  },
  {
    dataType: 'string',
    width: 170,
    visible: true,
    field: 'downloadedTime',
    searchable: false,
    title: 'investigateHosts.downloads.tableHeader.downloadedTime'
  },
  {
    dataType: 'string',
    width: 170,
    visible: true,
    field: 'checksumSha256',
    searchable: false,
    disableSort: true,
    title: 'investigateHosts.downloads.tableHeader.hash'
  }
];