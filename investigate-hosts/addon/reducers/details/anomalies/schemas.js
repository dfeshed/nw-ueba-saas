import { schema } from 'normalizr';
import { addId, commonNormalizerStrategy } from 'investigate-hosts/reducers/details/schema-utils';

/**
 * Pluck the anomalies from osType and set it to parent
 * @param input
 * @returns {{anomalies}}
 * @public
 */
const fileContextStrategy = (input) => {
  const { machineOsType, id } = input;
  const context = input[machineOsType];
  const { hooks } = context;
  addId(hooks, id, 'hooks_');
  return { ...input, hooks };
};


const hook = new schema.Entity('hooks', {}, { processStrategy: commonNormalizerStrategy });

const fileContext = new schema.Entity('fileContext',
  {
    hooks: [hook]
  },
  { idAttribute: 'checksumSha256', processStrategy: fileContextStrategy });

// List of file context
const fileContextHooksSchema = [fileContext];

export { fileContextHooksSchema };
