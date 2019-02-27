import { createSelector } from 'reselect';
import FILE_LIST_COLUMNS_CONFIG from './file-list-columns-config';

/**
 * Files table column width
 * @public
 */
const COLUMN_WIDTH = {
  'firstFileName': '10vw',
  'firstSeenTime': '7vw',
  'size': '3vw',
  'format': '10vw',
  'signature.features': '8vw',
  'entropy': '5vw',
  'pe.resources.company': '6vw',
  'checksumMd5': '15vw',
  'checksumSha1': '15vw',
  'checksumSha256': '18vw',
  'machineOsType': '6vw',
  'downloadInfo.status': '10vw'
};

const SUPPORTED_SORT_TYPES = [
  'firstFileName',
  'reputationStatus',
  'score',
  'format',
  'pe.resources.company',
  'fileStatus',
  'remediationAction',
  'downloadInfo.status',
  'size',
  'signature.features',
  'firstSeenTime',
  'machineOsType'
];


const schema = () => FILE_LIST_COLUMNS_CONFIG;
const _preferences = (state) => state.preferences.preferences;

const _visibleColumns = createSelector(
  _preferences,
  (preferences) => {
    if (preferences.filePreference) {
      return preferences.filePreference.visibleColumns;
    }
    return [];
  }
);


export const columns = createSelector(
  [schema, _visibleColumns],
  (schema, _visibleColumns) => {
    return schema.map((item) => {
      const { dataType, name: field, searchable, values } = item;
      const disableSort = !SUPPORTED_SORT_TYPES.includes(field);

      return {
        visible: _visibleColumns.includes(field),
        dataType,
        field,
        searchable,
        values,
        title: `investigateFiles.fields.${field}`,
        width: COLUMN_WIDTH[field] || '4vw',
        disableSort
      };

    });
    // Data-table component is expecting simple array/ember array
  }
);

export const isSchemaLoaded = createSelector(
  columns,
  (columns) => {
    return !!columns.length;
  }
);

