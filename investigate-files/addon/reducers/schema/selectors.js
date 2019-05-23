import { createSelector } from 'reselect';
import Immutable from 'seamless-immutable';
import FILE_LIST_COLUMNS_CONFIG from './file-list-columns-config';
import DEFAULT_FILE_PREFERENCES from './default-file-list-config';

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

const DEFAULT_COLUMN = Immutable.from([
  {
    dataType: 'checkbox',
    width: '1vw',
    class: 'rsa-form-row-checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true,
    headerComponentClass: 'rsa-form-checkbox',
    preferredDisplayIndex: 1
  },
  {
    dataType: 'string',
    width: '15vw',
    visible: true,
    field: 'firstFileName',
    searchable: true,
    title: 'investigateFiles.fields.firstFileName',
    preferredDisplayIndex: 2
  },
  {
    dataType: 'string',
    visible: true,
    field: 'score',
    searchable: false,
    title: 'investigateFiles.fields.score',
    preferredDisplayIndex: 3
  }
]);

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


const schema = () => FILE_LIST_COLUMNS_CONFIG || Immutable.from([]);
const _preferences = (state) => state.preferences.preferences;

const _visibleColumns = createSelector(
  _preferences,
  (preferences) => {
    if (preferences.filePreference) {
      return preferences.filePreference.columnConfig || [];
    }
    return [];
  }
);

export const savedColumns = createSelector(
  _visibleColumns,
  (columns) => {
    return columns;
  }
);

export const extractFilesColumns = createSelector(
  _visibleColumns,
  (visibleColumns) => {
    const savedColumns = visibleColumns.filter((item) => {
      return item.tableId === 'files';
    });
    if (savedColumns && savedColumns.length) {
      const [{ columns }] = savedColumns;
      return columns;
    } else {
      const [{ columns }] = DEFAULT_FILE_PREFERENCES.filePreference.columnConfig;
      return columns;
    }
  }
);

export const columns = createSelector(
  [schema, extractFilesColumns],
  (schema, columns) => {
    if (columns && columns.length) {
      let counter = columns.length + 3;
      if (schema && schema.length) {
        const updatedSchema = schema.map((item) => {
          const { dataType, name: field, searchable, values } = item;
          const currentColumn = columns.filter((column) => {
            return column.field === field;
          });
          let visible = false;
          let displayIndex, width;

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
            title: `investigateFiles.fields.${field}`,
            width: width || COLUMN_WIDTH[field] || '4vw',
            disableSort: !SUPPORTED_SORT_TYPES.includes(field)
          };
        });

        // Set the default columns, if not present in stored configuration
        DEFAULT_COLUMN.forEach((column) => {
          if (column.dataType === 'checkbox') {
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
      return [];
    }
  }
);

export const isSchemaLoaded = createSelector(
  columns,
  (columns) => {
    return !!columns.length;
  }
);

