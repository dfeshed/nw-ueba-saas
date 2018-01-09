import { createSelector } from 'reselect';
import Immutable from 'seamless-immutable';

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
  'pe.resources.company'
];

const schema = (state) => state.files.schema.schema || Immutable.from([]);
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
    const updatedSchema = schema.map((item) => {
      const { dataType, name: field, searchable, values } = item;
      const disableSort = !SUPPORTED_SORT_TYPES.includes(field);

      return {
        visible: _visibleColumns.includes(field),
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

