import { schema } from 'normalizr';
import { addId, commonNormalizerStrategy } from 'investigate-hosts/reducers/details/schema-utils';

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


const file = new schema.Entity('FILE', {}, { processStrategy: commonNormalizerStrategy });

const fileContext = new schema.Entity('fileContext',
  {
    files: [file]
  },
  { idAttribute: 'checksumSha256', processStrategy: fileContextStrategy });

// List of file context
const fileListSchema = [fileContext];

export { fileListSchema };
