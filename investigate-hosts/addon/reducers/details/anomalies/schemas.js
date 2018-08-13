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
    imageHooks = [],
    threads = [],
    kernelHooks = []
  } = context;

  addId(imageHooks, id, 'imageHooks_');
  addId(threads, id, 'threads_');
  addId(kernelHooks, id, 'kernelHooks_');

  return {
    ...input,
    imageHooks,
    threads,
    kernelHooks
  };
};


const imageHook = new schema.Entity('imageHooks', {}, { processStrategy: commonNormalizerStrategy });
const thread = new schema.Entity('threads', {}, { processStrategy: commonNormalizerStrategy });
const kernelHook = new schema.Entity('kernelHooks', {}, { processStrategy: commonNormalizerStrategy });

const fileContextImageHooks = new schema.Entity('fileContext',
  {
    imageHooks: [imageHook]
  },
  { idAttribute: 'checksumSha256', processStrategy: fileContextStrategy });

const fileContextThreads = new schema.Entity('fileContext',
  {
    threads: [thread]
  },
  { idAttribute: 'checksumSha256', processStrategy: fileContextStrategy });

const fileContextKernelHook = new schema.Entity('fileContext',
  {
    kernelHooks: [kernelHook]
  },
  { idAttribute: 'checksumSha256', processStrategy: fileContextStrategy });

// List of file context
const fileContextImageHooksSchema = [fileContextImageHooks];
const fileContextThreadsSchema = [fileContextThreads];
const fileContextKernelHooksSchema = [fileContextKernelHook];

export {
  fileContextImageHooksSchema,
  fileContextThreadsSchema,
  fileContextKernelHooksSchema
};
