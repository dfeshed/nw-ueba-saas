import { driverSchema } from './driver-schemas';
import { librarySchema } from './libraries-schemas';
import { autorunSchema, serviceSchema, taskSchema } from './autorun-schemas';
import { imageHookSchema, kernelHookSchema, threadSchema } from './anomaly-schema';
import { fileListSchema } from './file-schema';

const getSchema = (type) => {
  switch (type) {
    case 'DRIVER':
      return driverSchema;
    case 'LIBRARY':
      return librarySchema;
    case 'AUTORUN':
      return autorunSchema;
    case 'SERVICE':
      return serviceSchema;
    case 'TASK':
      return taskSchema;
    case 'IMAGEHOOK':
      return imageHookSchema;
    case 'THREAD':
      return threadSchema;
    case 'KERNELHOOK':
      return kernelHookSchema;
    case 'FILE':
      return fileListSchema;
  }
};

export {
  getSchema
};
