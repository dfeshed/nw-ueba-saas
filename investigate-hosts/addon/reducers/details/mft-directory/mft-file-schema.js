import { schema } from 'normalizr';
import { addId } from 'investigate-hosts/reducers/details/schema-utils';

/**
 * Pluck the files from osType and set it to parent
 * @param input
 * @returns {{files}}
 * @public
 */
const fileContextStrategy = (input) => {
  const { id } = input;
  const files = [input];
  addId(files, id, 'files_');
  return { ...input, files };
};


const file = new schema.Entity('files', {}, { processStrategy: fileContextStrategy });

// List of files
const fileListSchema = [file];

export { fileListSchema };