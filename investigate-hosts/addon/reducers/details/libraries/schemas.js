import { schema } from 'normalizr';
import { addId, commonNormalizerStrategy } from 'investigate-hosts/reducers/details/schema-utils';
/**
 * Pluck the autoruns from osType and set it to parent
 * @param input
 * @returns {{autoruns}}
 * @public
 */
const fileContextStrategy = (input) => {
  const { machineOsType, id } = input;
  const context = input[machineOsType];
  const { dlls = [], dylibs = [], loadedLibraries = [] } = context;

  addId(dlls, id, 'dll_');
  addId(dylibs, id, 'dylibs_');
  addId(loadedLibraries, id, 'loadedLibraries_');

  return { ...input, libraries: [...dlls, ...dylibs, ...loadedLibraries] };
};


const library = new schema.Entity('library', {}, { processStrategy: commonNormalizerStrategy });

const fileContext = new schema.Entity('fileContext',
  {
    libraries: [library]
  },
  { idAttribute: 'checksumSha256', processStrategy: fileContextStrategy });

// List of file context
const fileContextListSchema = [fileContext];

export { fileContextListSchema };
