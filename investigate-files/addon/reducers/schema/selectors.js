import { createSelector } from 'reselect';
import Immutable from 'seamless-immutable';

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
  'checksumSha256': '18vw'
};

const SUPPORTED_SORT_TYPES = [
  'firstFileName',
  'firstSeenTime',
  'reputationStatus',
  'score',
  'size',
  'format',
  'signature.features',
  'entropy',
  'pe.resources.company',
  'fileStatus'
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
        width: COLUMN_WIDTH[field] || '4vw',
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

