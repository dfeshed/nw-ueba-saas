/**
 * This utils returns columns for given schema (Ex. Authentication, File, AD)
 * IF none is matching then returns base columns.
 * @private
 */
const baseColumnConfigForEvents = [{
  field: 'eventDate.epochSecond',
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
  width: '4.5vw',
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
}, {
  field: 'operationTypeCategories',
  width: '10vw',
  title: 'Operation Type Category',
  visible: true,
  disableSort: true
}];

const activeDirectoryColumns = [{
  field: 'objectId',
  width: '7.5vw',
  title: 'Object Name',
  linkField: 'user_link',
  additionalFilter: 'obj.name',
  visible: true,
  disableSort: true
}, {
  field: 'scores.operationType',
  width: '10vw',
  title: 'Action Score',
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
  visible: true,
  linkField: 'user_link',
  additionalFilter: 'srcMachineId',
  disableSort: true
}, {
  field: 'additionalInfo.Logon_Type',
  width: '3.5vw',
  title: 'Logon Type',
  visible: false,
  disableSort: true
}, {
  field: 'dstMachineId',
  width: '3.5vw',
  linkField: 'user_link',
  additionalFilter: 'dstMachineId',
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
  width: '3.5vw',
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
  linkField: 'user_link',
  additionalFilter: 'absoluteSrcFolderFilePath',
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
  additionalFilter: 'absoluteSrcFilePath',
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
  width: '3.5vw',
  title: 'Machine Name',
  linkField: 'machine_name_link',
  visible: true,
  disableSort: true
}, {
  field: 'srcProcessFileName',
  width: '3.5vw',
  title: 'Source Process',
  visible: true,
  linkField: 'src_process_link',
  disableSort: true
}, {
  field: 'dstProcessFileName',
  width: '3.5vw',
  title: 'Destination Process',
  visible: true,
  linkField: 'dst_process_link',
  disableSort: true
}];

const registryColumns = [{
  field: 'machineId',
  width: '3.5vw',
  title: 'Machine Id',
  visible: true,
  disableSort: true
}, {
  field: 'machineName',
  width: '3.5vw',
  title: 'Machine Name',
  linkField: 'machine_name_link',
  visible: true,
  disableSort: true
}, {
  field: 'machineOwner',
  width: '3.5vw',
  title: 'Machine Owner',
  visible: true,
  disableSort: true
}, {
  field: 'processDirectory',
  width: '3.5vw',
  title: 'Process Directory',
  visible: true,
  disableSort: true
}, {
  field: 'processFileName',
  width: '3.5vw',
  title: 'Process File Name',
  visible: true,
  linkField: 'process_name_link',
  disableSort: true
}, {
  field: 'processDirectoryGroups',
  width: '3.5vw',
  title: 'Process Directory Groups',
  visible: true,
  disableSort: true
}, {
  field: 'processCategories',
  width: '3.5vw',
  title: 'Process Categories',
  visible: true,
  disableSort: true
}, {
  field: 'processCertificateIssuer',
  width: '3.5vw',
  title: 'Process Certificate Issuer',
  visible: true,
  disableSort: true
}, {
  field: 'registryKeyGroup',
  width: '3.5vw',
  title: 'Registry Key Group',
  visible: true,
  disableSort: true
}, {
  field: 'registryKey',
  width: '3.5vw',
  title: 'Registry Key',
  visible: true,
  disableSort: true
}, {
  field: 'registryValueName',
  width: '3.5vw',
  title: 'Registry Value Name',
  visible: true,
  disableSort: true
}, {
  field: 'userId',
  width: '3.5vw',
  title: 'User Id',
  visible: true,
  disableSort: true
}, {
  field: 'operationType',
  width: '3.5vw',
  title: 'Operation Type',
  visible: true,
  disableSort: true
}, {
  field: 'userName',
  width: '3.5vw',
  title: 'User Name',
  visible: true,
  linkField: 'user_sid_link',
  disableSort: true
}, {
  field: 'userDisplayName',
  width: '3.5vw',
  title: ' User Display Name',
  linkField: 'user_sid_link',
  visible: true,
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
  return baseColumnConfigForEvents.concat(indicatorColumnMap[indicatorName] ? indicatorColumnMap[indicatorName] : []);
};