import { schema } from 'normalizr';
import { addId, commonNormalizerStrategy } from 'investigate-hosts/reducers/details/schema-utils';

const _addType = (data, type) => {
  if (data && data.length) {
    data.forEach((d) => d.type = type);
  }
};

/**
 * Autorun split into 3 tabs, autorun, services and task. Auturns list will be inside the each fileContext.<machineOsType>
 * ex:
 * fileContext: [{
 *  fileContext property,
 *  fileProperty: {}
 *  linux: {
 *    autoruns: [],
 *    systemds: [],
 *    initds: [],
 *    tasks: [],
 *    daemons: [],
 *    services: []
 *  }
 * }]
 *
 * For each autoruns, systemds, etc we need add fileContext (parent) property and also we need to put it into proper bucket
 *
 * Expected output:
 *
 * autoruns: autoruns,
 * tasks: [..tasks, ...crons]
 * services: [ ...services, ...daemons, ...systemds, ...initds ]
 *
 * @param input
 * @returns {{autoruns}}
 * @public
 */
const fileContextStrategy = (input) => {
  const { machineOsType, id } = input;
  const context = input[machineOsType];
  const {
    autoruns = [],
    tasks = [],
    crons = [],
    services = [],
    daemons = [],
    systemds = [],
    initds = []
  } = context;

  _addType(initds, 'Initds');
  _addType(systemds, 'Systemds');

  // Add the id all category
  addId(autoruns, id, 'autoruns_');
  addId(services, id, 'service_');
  addId(daemons, id, 'daemons_');
  addId(systemds, id, 'systemds_');
  addId(initds, id, 'initds_');
  addId(tasks, id, 'task_');
  addId(crons, id, 'crons_');

  return {
    ...input,
    autoruns,
    services: [...services, ...daemons, ...systemds, ...initds],
    tasks: [...tasks, ...crons]
  };
};

const autorun = new schema.Entity('AUTORUN', {}, { processStrategy: commonNormalizerStrategy });
const service = new schema.Entity('SERVICE', {}, { processStrategy: commonNormalizerStrategy });
const task = new schema.Entity('TASK', {}, { processStrategy: commonNormalizerStrategy });

const fileContextAutoruns = new schema.Entity('fileContextAutoruns',
  {
    autoruns: [autorun]
  },
  { idAttribute: 'checksumSha256', processStrategy: fileContextStrategy });

const fileContextServices = new schema.Entity('fileContextServices',
  {
    services: [service]
  },
  { idAttribute: 'checksumSha256', processStrategy: fileContextStrategy });


const fileContextTasks = new schema.Entity('fileContextTasks',
  {
    tasks: [task]
  },
  { idAttribute: 'checksumSha256', processStrategy: fileContextStrategy });

// List of file context
const autorunSchema = [fileContextAutoruns];
const serviceSchema = [fileContextServices];
const taskSchema = [fileContextTasks];

export { autorunSchema, serviceSchema, taskSchema };
