/**
 * This utils returns columns for given schema (Ex. Authentication, File, AD)
 * IF none is matching then returns base columns.
 * @private
 */
const baseColumnConfigForEvents = [{
  field: 'time_detected',
  type: 'DATE_TIME',
  title: 'Time',
  visible: true,
  disableSort: true
}, {
  field: 'userName',
  title: 'User Name',
  visible: true,
  linkField: 'user_link',
  disableSort: true
}, {
  field: 'userId',
  width: '5.5vw',
  title: 'Normalized user name',
  visible: true,
  linkField: 'user_link',
  disableSort: true
}, {
  field: 'operationType',
  width: '10vw',
  title: 'Operation Type',
  visible: true,
  disableSort: true
} ];

const activeDirectoryColumns = [{
  field: 'objectId',
  width: 'auto',
  title: 'Object Name',
  linkField: 'user_link',
  additionalFilter: 'obj.name',
  visible: true,
  disableSort: true
}, {
  field: 'additionalInfo.IP_Address',
  width: '3.5vw',
  title: 'IP Address',
  visible: false,
  disableSort: true
}];

const authenticationColumns = [{
  field: 'srcMachineId',
  title: 'Source Host',
  width: '5vw',
  visible: true,
  linkField: 'user_link',
  additionalFilter: 'host.all',
  disableSort: true
}, {
  field: 'additionalInfo.Logon_Type',
  width: '3.5vw',
  title: 'Logon Type',
  visible: false,
  disableSort: true
}, {
  field: 'dstMachineId',
  width: '6vw',
  linkField: 'user_link',
  additionalFilter: 'host.all',
  visible: true,
  title: 'Destination Device',
  disableSort: true
}, {
  field: 'score.dstMachineNameRegexCluster',
  width: '3.5vw',
  title: 'Dest Computer Score',
  visible: false,
  disableSort: true
}, {
  field: 'score.srcMachineNameRegexCluster',
  width: '3.5vw',
  title: 'Computer Score',
  visible: false,
  disableSort: true
}, {
  field: 'site',
  width: '3.5vw',
  title: 'Site',
  visible: false,
  disableSort: true
}, {
  field: 'resultCode',
  width: 'auto',
  title: 'Result Code',
  visible: true,
  disableSort: true
}, {
  field: 'scores.site',
  width: '3.5vw',
  title: 'Score Site',
  visible: false,
  disableSort: true
}];


const fileColumns = [{
  field: 'additionalInfo.Folder_Path',
  width: '3.5vw',
  title: 'Folder Path',
  visible: false,
  disableSort: true
}, {
  field: 'absoluteSrcFolderFilePath',
  width: '15vw',
  title: 'Source Folder Path',
  visible: true,
  disableSort: true
}, {
  field: 'additionalInfo.absoluteDstFilePath',
  width: '3.5vw',
  title: 'Destination Path',
  visible: false,
  disableSort: true
}, {
  field: 'absoluteSrcFilePath',
  width: '10vw',
  title: 'Source File Path',
  linkField: 'user_link',
  visible: true,
  disableSort: true
}, {
  field: 'additionalInfo.File_Server',
  width: '3.5vw',
  title: 'File Server',
  visible: false,
  disableSort: true
}];

const processColumns = [{
  field: 'machineName',
  width: '6vw',
  title: 'Machine Name',
  linkField: 'machine_name_link',
  additionalFilter: 'host.all',
  visible: true,
  disableSort: true
}, {
  field: 'srcProcessFileName',
  width: '8vw',
  title: 'Source Process',
  visible: true,
  linkField: 'src_process_link',
  disableSort: true
}, {
  field: 'dstProcessFileName',
  width: 'auto',
  title: 'Destination Process',
  visible: true,
  linkField: 'dst_process_link',
  disableSort: true
}];

const registryColumns = [{
  field: 'machineName',
  width: '3.5vw',
  title: 'Machine Name',
  linkField: 'machine_name_link',
  additionalFilter: 'host.all',
  visible: true,
  disableSort: true
}, {
  field: 'machineOwner',
  width: '3.5vw',
  title: 'Machine Owner',
  visible: false,
  disableSort: true
}, {
  field: 'processDirectory',
  width: '3.5vw',
  title: 'Process Directory',
  visible: false,
  disableSort: true
}, {
  field: 'processFileName',
  width: '5.5vw',
  title: 'Process File Name',
  visible: true,
  linkField: 'process_name_link',
  disableSort: true
}, {
  field: 'processDirectoryGroups',
  width: '3.5vw',
  title: 'Process Directory Groups',
  visible: false,
  disableSort: true
}, {
  field: 'processCategories',
  width: '3.5vw',
  title: 'Process Categories',
  visible: false,
  disableSort: true
}, {
  field: 'processCertificateIssuer',
  width: '3.5vw',
  title: 'Process Certificate Issuer',
  visible: false,
  disableSort: true
}, {
  field: 'registryKeyGroup',
  width: '6vw',
  title: 'Registry Key Group',
  visible: true,
  disableSort: true
}, {
  field: 'registryKey',
  width: '6vw',
  title: 'Registry Key',
  visible: true,
  disableSort: true
}, {
  field: 'registryValueName',
  width: '6vw',
  title: 'Registry Value Name',
  visible: true,
  disableSort: true
}];

const networkColumns = [{
  field: 'time_detected',
  type: 'DATE_TIME',
  title: 'Time',
  visible: true,
  disableSort: true
}, {
  field: 'srcIp',
  title: 'Source IP',
  width: '5vw',
  visible: true,
  disableSort: true
}, {
  field: 'dstIp',
  title: 'Destination IP',
  width: '5vw',
  visible: true,
  disableSort: true
}, {
  field: 'dstCountry.name',
  title: 'Destination Country',
  width: '5vw',
  visible: true,
  disableSort: true
}, {
  field: 'sslSubject.name',
  title: 'SSL',
  width: '5vw',
  visible: true,
  disableSort: true
}, {
  field: 'dstOrg.name',
  title: 'Destination Organization',
  width: '5vw',
  visible: true,
  disableSort: true
}, {
  field: 'dstAsn.name',
  title: 'Destination ASN',
  width: '5vw',
  visible: false,
  disableSort: true
}, {
  field: 'domain.name',
  title: 'Domain',
  width: '5vw',
  visible: true,
  disableSort: true
}, {
  field: 'ja3.name',
  width: '5vw',
  title: 'JA3',
  visible: true,
  disableSort: true
}, {
  field: 'ja3s',
  width: '5vw',
  title: 'JA3S',
  visible: false,
  disableSort: true
}, {
  field: 'dstPort.name',
  width: '5vw',
  title: 'Destination Port',
  visible: true,
  disableSort: true
}, {
  field: 'srcNetname',
  width: '5vw',
  title: 'Source Net Name',
  visible: true,
  disableSort: true
}, {
  field: 'dstNetname',
  width: '5vw',
  title: 'Destination Net Name',
  visible: false,
  disableSort: true
}, {
  field: 'numOfBytesSent',
  width: '5vw',
  title: 'Number of Byte Sent',
  visible: true,
  disableSort: true
}, {
  field: 'numOfBytesReceived',
  width: '5vw',
  title: 'Number of Byte Received',
  visible: false,
  disableSort: true
}];

const indicatorColumnMap = {
  active_directory: activeDirectoryColumns,
  authentication: authenticationColumns,
  file: fileColumns,
  process: processColumns,
  registry: registryColumns
};

export default (indicatorName) => {
  if (indicatorName === 'tls') {
    return networkColumns;
  }
  return baseColumnConfigForEvents.concat(indicatorColumnMap[indicatorName] ? indicatorColumnMap[indicatorName] : []);
};