import { schema } from 'normalizr';
import { addId, commonNormalizerStrategy } from 'investigate-hosts/reducers/details/schema-utils';

const imageHooksStrategy = (input) => {
  const { machineOsType, id } = input;
  const context = input[machineOsType];
  const {
    imageHooks = []
  } = context;
  addId(imageHooks, id, 'imageHooks_');
  const { fileName: dllFileName } = input;
  const newData = imageHooks.map((item) => {
    const {
      hookLocation: { fileName: hookedFileName, symbol },
      process: { fileName, pid }
    } = item;
    return {
      ...item,
      dllFileName,
      hookedProcess: `${fileName} : ${pid}`,
      hookedFileName,
      symbol
    };
  });
  return {
    ...input,
    imageHooks: newData
  };
};

const kernelHooksStrategy = (input) => {
  const { machineOsType, id } = input;
  const context = input[machineOsType];
  const {
    kernelHooks = []
  } = context;
  addId(kernelHooks, id, 'kernelHooks_');
  const { fileName: driverFileName } = input;
  const newData = kernelHooks.map((item) => {
    const { hookLocation: { fileName: hookedFileName, objectFunction } } = item;
    return {
      ...item,
      objectFunction,
      driverFileName,
      hookedFileName
    };
  });
  return {
    ...input,
    kernelHooks: newData
  };
};

const threadsStrategy = (input) => {
  const { machineOsType, id } = input;
  const context = input[machineOsType];
  const {
    threads = []
  } = context;
  addId(threads, id, 'threads_');
  const newData = threads.map((item) => {
    const { processName, pid } = item;
    return {
      ...item,
      process: `${processName} : ${pid}`
    };
  });
  return {
    ...input,
    threads: newData
  };
};

const imageHook = new schema.Entity('IMAGEHOOK', {}, { processStrategy: commonNormalizerStrategy });
const thread = new schema.Entity('THREAD', {}, { processStrategy: commonNormalizerStrategy });
const kernelHook = new schema.Entity('KERNELHOOK', {}, { processStrategy: commonNormalizerStrategy });

const fileContextImageHooks = new schema.Entity('fileContext',
  {
    imageHooks: [imageHook]
  },
  { idAttribute: 'checksumSha256', processStrategy: imageHooksStrategy });

const fileContextThreads = new schema.Entity('fileContext',
  {
    threads: [thread]
  },
  { idAttribute: 'checksumSha256', processStrategy: threadsStrategy });

const fileContextKernelHook = new schema.Entity('fileContext',
  {
    kernelHooks: [kernelHook]
  },
  { idAttribute: 'checksumSha256', processStrategy: kernelHooksStrategy });

// List of file context
const imageHookSchema = [fileContextImageHooks];
const threadSchema = [fileContextThreads];
const kernelHookSchema = [fileContextKernelHook];

export {
  imageHookSchema,
  threadSchema,
  kernelHookSchema
};
