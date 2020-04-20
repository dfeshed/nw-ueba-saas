const CATEGORIES = {
  PROCESSES: 'investigateHosts.hosts.ranas.categories.Process',
  LOADED_LIBRARIES: 'investigateHosts.hosts.ranas.categories.Libraries',
  AUTORUNS: 'investigateHosts.hosts.ranas.categories.Autorun',
  SERVICES: 'investigateHosts.hosts.ranas.categories.Service',
  TASKS: 'investigateHosts.hosts.ranas.categories.Task',
  DRIVERS: 'investigateHosts.hosts.ranas.categories.Driver',
  THREADS: 'investigateHosts.hosts.ranas.categories.Thread',
  IMAGE_HOOKS: 'investigateHosts.hosts.ranas.categories.imageHooks',
  KERNEL_HOOKS: 'investigateHosts.hosts.ranas.categories.kernelHooks'

};
const CATEGORY_NAME = {
  process: 'PROCESS',
  library: 'LIBRARIES',
  autorun: 'AUTORUNS',
  service: 'SERVICES',
  task: 'TASKS',
  driver: 'DRIVERS',
  imagehooks: 'IMAGEHOOKS',
  kernelhooks: 'KERNELHOOKS',
  thread: 'THREADS'
};

const TAB_MAPPING = {
  task: 'AUTORUNS',
  service: 'AUTORUNS',
  autorun: 'AUTORUNS',
  imagehooks: 'ANOMALIES',
  kernelhooks: 'ANOMALIES',
  thread: 'ANOMALIES',
  driver: 'DRIVERS',
  process: 'PROCESS',
  library: 'LIBRARIES'
};


export {
  CATEGORIES,
  CATEGORY_NAME,
  TAB_MAPPING
};
