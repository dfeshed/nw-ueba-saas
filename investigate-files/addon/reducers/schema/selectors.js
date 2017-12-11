import { createSelector } from 'reselect';
import Immutable from 'seamless-immutable';
import CONFIG from './config';

/**
 * Files table column width
 * @public
 */
const COLUMN_WIDTH = {
  'firstFileName': 200,
  'firstSeenTime': 140,
  'size': 75,
  'format': 60,
  'signature.features': 130,
  'entropy': 55,
  'pe.resources.company': 150,
  'checksumMd5': 220,
  'checksumSha1': 275,
  'checksumSha256': 450
};

const SUPPORTED_SORT_TYPES = [
  'firstFileName',
  'firstSeenTime',
  'size',
  'format',
  'signature.features',
  'entropy',
  'pe.resources.company',
  'mac.resources.company'
];

const DEFAULT_COLUMNS = Immutable.from(['firstFileName']);

const schema = (state) => state.files.schema.schema || Immutable.from([]);
const _visibleColumns = (state) => state.files.schema.visibleColumns;


const _userSelectedColumns = createSelector(
  _visibleColumns,
  (visibleColumns) => {
    return visibleColumns && visibleColumns.length ? [...visibleColumns, ...DEFAULT_COLUMNS] : Immutable.from(DEFAULT_COLUMNS);
  }
);

export const columns = createSelector(
  [schema, _userSelectedColumns],
  (schema, visibleColumns) => {
    const updatedSchema = schema.map((item) => {
      const { dataType, name: field, searchable, values, visible } = item;

      const updatedVisibility = visible || visibleColumns.includes(field);

      const disableSort = !SUPPORTED_SORT_TYPES.includes(field);

      return {
        visible: updatedVisibility,
        dataType,
        field,
        searchable,
        values,
        title: `investigateFiles.fields.${field}`,
        width: COLUMN_WIDTH[field],
        disableSort
      };

    });
    // Making it as mutable as schema is passed down to data-table component
    // Data-table component is expecting simple array/ember array
    return updatedSchema.asMutable();
  }
);

export const isSchemaLoaded = createSelector(
  columns,
  (columns) => {
    return !!columns.length;
  }
);

export const preferenceConfig = createSelector(
  [isSchemaLoaded, columns],
  (isSchemaLoaded, columns) => {
    const fileConfig = { ...CONFIG };
    if (isSchemaLoaded) {
      // Set options of the dropdown from column schema
      const visibleColumns = fileConfig.items.find((item) => item.field === 'filePreference.visibleColumns');
      visibleColumns.options = columns.map((column) => column.field);
      return fileConfig;
    }
    return fileConfig;
  }
);
