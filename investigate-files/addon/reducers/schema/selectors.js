import { createSelector } from 'reselect';
import Immutable from 'seamless-immutable';
import CONFIG from './config';

/**
 * Files table column width
 * @public
 */
const COLUMN_WIDTH = {
  'firstFileName': 200,
  'firstSeenTime': 170,
  'size': 75,
  'format': 100,
  'signature.features': 130,
  'entropy': 150,
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

const schema = (state) => state.files.schema.schema || Immutable.from([]);

export const columns = createSelector(
  [schema],
  (schema) => {
    const updatedSchema = schema.map((item) => {
      const { dataType, name: field, searchable, values, visible } = item;
      const disableSort = !SUPPORTED_SORT_TYPES.includes(field);

      return {
        visible,
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
