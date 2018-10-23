import { createSelector } from 'reselect';

const _totalItems = (state) => state.certificate.list.totalItems;

const certificatesList = (state) => state.certificate.list.certificatesList || [];

const _certificatesLoadingStatus = (state) => state.certificate.list.certificatesLoadingStatus;

const _selectedCertificateList = (state) => state.certificate.list.selectedCertificateList || [];

export const certificatesColumns = [
  {
    field: 'radio',
    dataType: 'radio',
    width: 50
  },
  {
    field: 'friendlyName',
    title: 'configure.endpoint.certificates.columns.friendlyName',
    label: 'Friendly Name',
    width: 350,
    disableSort: false
  },
  {
    field: 'subject',
    title: 'configure.endpoint.certificates.columns.subject',
    label: 'Subject',
    width: 300,
    disableSort: false
  },
  {
    field: 'certificateStatus',
    title: 'configure.endpoint.certificates.columns.certificateStatus',
    label: 'Status',
    width: 300,
    disableSort: true
  },
  {
    field: 'subjectKey',
    title: 'configure.endpoint.certificates.columns.subjectKey',
    label: 'Subject Key',
    width: 280,
    disableSort: false
  },
  {
    field: 'serial',
    title: 'configure.endpoint.certificates.columns.serial',
    label: 'Serial',
    width: 260,
    disableSort: false
  },
  {
    field: 'issuer',
    title: 'configure.endpoint.certificates.columns.issuer',
    label: 'Issuer',
    width: 300,
    disableSort: false
  },
  {
    field: 'authorityKey',
    title: 'configure.endpoint.certificates.columns.authorityKey',
    label: 'Authority Key',
    width: 260,
    disableSort: false
  },
  {
    field: 'thumbprint',
    title: 'configure.endpoint.certificates.columns.thumbprint',
    label: 'Thumb Print',
    width: 300,
    disableSort: false
  },
  {
    field: 'notValidBeforeUtcDate',
    title: 'configure.endpoint.certificates.columns.notValidBeforeUtcDate',
    label: 'notValidBeforeUtcDate',
    width: 200,
    disableSort: false
  },
  {
    field: 'notValidAfterUtcDate',
    title: 'configure.endpoint.certificates.columns.notValidAfterUtcDate',
    label: 'notValidAfterUtcDate',
    width: 200,
    disableSort: false
  }
];

export const certificatesCount = createSelector(
  certificatesList,
  (certificatesList) => {
    return certificatesList.length;
  }
);

export const certificatesCountForDisplay = createSelector(
  [ _totalItems],
  (totalItems) => {
    // For performance reasons api returns 1000 as totalItems when filter is applied, even if result is more than 1000
    // Make sure we append '+' to indicate user more files are present
    if (totalItems >= 1000) {
      return `${totalItems}+`;
    }
    return `${totalItems}`;
  }
);

export const certificatesLoading = createSelector(
  [_certificatesLoadingStatus],
  (certificatesLoadingStatus) => {
    return certificatesLoadingStatus === 'wait';
  }
);

/**
 * selector to know all rows selected
 * @public
 */
export const isAllSelected = createSelector(
  [certificatesList, _selectedCertificateList],
  (certificatesList, selectedCertificateList) => {
    if (certificatesList && selectedCertificateList.length) {
      return certificatesList.length === selectedCertificateList.length;
    }
    return false;
  }
);

