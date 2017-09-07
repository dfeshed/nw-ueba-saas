import { createSelector } from 'reselect';

/**
 * Files table column width
 * @public
 */
const COLUMN_WIDTH = {
  'firstFileName': 200,
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
  'size',
  'format',
  'signature.features',
  'entropy',
  'pe.resources.company',
  'mac.resources.company'
];

const schema = (state) => state.schema.schema || [];

export const columns = createSelector(
  schema,
  (schema) => {
    const updatedSchema = schema.map((item) => {

      const { defaultProjection: visible, dataType, name: field, searchable, values } = item;
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
    return updatedSchema;
  }
);

export const isSchemaLoaded = createSelector(
  columns,
  (columns) => {
    return !!columns.length;
  }
);
