import { createSelector } from 'reselect';
import CERTIFICATE_PREFERENCE from './default_certificate_columns_config';
import CERTIFICATE_COLUMNS_CONFIG from './certificate_columns';
import Immutable from 'seamless-immutable';

const _totalItems = (state) => state.certificate.list.totalItems;

const certificatesList = (state) => state.certificate.list.certificatesList || [];

const _certificatesLoadingStatus = (state) => state.certificate.list.certificatesLoadingStatus;

const _selectedCertificateList = (state) => state.certificate.list.selectedCertificateList || [];

export const CERTIFICATE_DEFAULT_COLUMNS = [
  {
    field: 'friendlyName',
    title: 'configure.endpoint.certificates.columns.friendlyName',
    label: 'Friendly Name',
    width: '35vw',
    disableSort: false,
    visible: true,
    preferredDisplayIndex: 2
  },
  {
    field: 'certificateStatus',
    title: 'configure.endpoint.certificates.columns.certificateStatus',
    label: 'Status',
    width: '10vw',
    disableSort: true,
    visible: true,
    preferredDisplayIndex: 3
  },
  {
    field: 'radio',
    dataType: 'radio',
    width: '2vw',
    class: 'rsa-form-row-radio',
    componentClass: 'rsa-form-radio',
    visible: true,
    disableSort: true,
    headerComponentClass: 'rsa-form-radio',
    preferredDisplayIndex: 1
  }
];
const _preferences = (state) => state.preferences.preferences;
const schema = () => CERTIFICATE_COLUMNS_CONFIG || Immutable.from([]);

const _visibleColumns = createSelector(
  _preferences,
  (preferences) => {
    if (preferences.filePreference) {
      return preferences.filePreference.columnConfig || [];
    }
    return [];
  }
);

const extractFilesColumns = createSelector(
  _visibleColumns,
  (visibleColumns) => {
    const savedColumns = visibleColumns.filter((item) => {
      return item.tableId === 'files-certificates';
    });

    if (savedColumns && savedColumns.length) {
      const [{ columns }] = savedColumns;
      return columns;
    } else {
      const [{ columns }] = CERTIFICATE_PREFERENCE.certificatePreference.columnConfig;
      return columns;
    }
  }
);

export const columns = createSelector(
  [schema, extractFilesColumns],
  (schema, columns) => {
    if (columns && columns.length) {
      const sortedSchema = [];

      let counter = columns.length + 3;
      if (schema && schema.length) {
        const updatedSchema = schema.map((item) => {
          const { dataType, field, searchable, values } = item;
          let { width } = item;
          const currentColumn = columns.filter((column) => {
            return column.field === field;
          });
          let visible = false;
          let displayIndex;

          if (currentColumn && currentColumn.length) {
            visible = true;
            const [{ displayIndex: index, width: columnWidth }] = currentColumn;
            displayIndex = parseInt(index, 10);
            width = columnWidth;
          } else {
            displayIndex = counter;
            counter++;
          }

          return {
            visible,
            dataType,
            field,
            searchable,
            values,
            preferredDisplayIndex: displayIndex,
            title: `configure.endpoint.certificates.columns.${field}`,
            width: width || '4vw',
            disableSort: false
          };
        });

        // Set the default columns, if not present in stored configuration
        CERTIFICATE_DEFAULT_COLUMNS.forEach((column) => {
          if (column.dataType === 'radio') {
            updatedSchema.unshift(column);
          } else {
            const [item] = columns.filter((col) => {
              return column.field === col.field;
            });
            if (!item) {
              updatedSchema.unshift(column);
            }
          }
        });
        const visibleList = updatedSchema.filter((column) => column.visible);
        if (visibleList) {
          // Making it as mutable as schema is passed down to data-table component and data-table component expecting simple array/ember array
          return updatedSchema;
        }
      }
      return sortedSchema;
    }
  }
);

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
/**
 * To show the number of items available size in the next page load.
 * @public
 */
export const nextLoadCount = createSelector(
  certificatesList,
  (certificatesList) => {
    const PAGE_LOAD_SIZE = 100;
    return certificatesList.length >= PAGE_LOAD_SIZE ? PAGE_LOAD_SIZE : certificatesList.length;
  }
);
