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
  const { drivers } = context;
  addId(drivers, id, 'drivers_');
  return { ...input, drivers };
};


const driver = new schema.Entity('driver', {}, { processStrategy: commonNormalizerStrategy });

const fileContext = new schema.Entity('fileContext',
  {
    drivers: [driver]
  },
  { idAttribute: 'checksumSha256', processStrategy: fileContextStrategy });

// List of file context
const fileContextListSchema = [fileContext];

export { fileContextListSchema };
