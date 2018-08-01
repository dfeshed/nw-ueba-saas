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
  const {
    hooks = [],
    threads = []
  } = context;

  addId(hooks, id, 'hooks_');
  addId(threads, id, 'threads_');

  return {
    ...input,
    hooks,
    threads
  };
};


const hook = new schema.Entity('hooks', {}, { processStrategy: commonNormalizerStrategy });
const thread = new schema.Entity('threads', {}, { processStrategy: commonNormalizerStrategy });

const fileContextHooks = new schema.Entity('fileContext',
  {
    hooks: [hook]
  },
  { idAttribute: 'checksumSha256', processStrategy: fileContextStrategy });

const fileContextThreads = new schema.Entity('fileContext',
  {
    threads: [thread]
  },
  { idAttribute: 'checksumSha256', processStrategy: fileContextStrategy });

// List of file context
const fileContextHooksSchema = [fileContextHooks];
const fileContextThreadsSchema = [fileContextThreads];

export {
  fileContextHooksSchema,
  fileContextThreadsSchema
};
