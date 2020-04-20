import { schema } from 'normalizr';
import { addId, commonNormalizerStrategy } from 'investigate-hosts/reducers/details/schema-utils';

/**
 * Pluck the autoruns from osType and set it to parent
 * @param input
 * @returns {{autoruns}}
 * @public
 */
const _addAdditionalKeys = function(item) {
  const { pid, processName } = item;
  item.processContext = `${processName} : ${pid}`;
};

const fileContextStrategy = (input) => {
  const { machineOsType, id } = input;
  const context = input[machineOsType];
  const { dlls = [], dylibs = [], loadedLibraries = [] } = context;

  addId(dlls, id, 'dll_', _addAdditionalKeys);
  addId(dylibs, id, 'dylibs_', _addAdditionalKeys);
  addId(loadedLibraries, id, 'loadedLibraries_', _addAdditionalKeys);

  return { ...input, libraries: [...dlls, ...dylibs, ...loadedLibraries] };
};


const library = new schema.Entity('LIBRARY', {}, { processStrategy: commonNormalizerStrategy });

const fileContext = new schema.Entity('fileContext',
  {
    libraries: [library]
  },
  { idAttribute: 'checksumSha256', processStrategy: fileContextStrategy });

// List of file context
const librarySchema = [fileContext];

export { librarySchema };
