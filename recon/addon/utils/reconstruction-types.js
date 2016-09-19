const TYPES = [{
  code: 1,
  name: 'PACKET',
  label: 'Packet View',
  component: 'recon-event-detail/packets'
}, {
  code: 2,
  name: 'FILE',
  label: 'File View',
  component: 'recon-event-detail/files'
}];

const TYPES_BY_NAME = {};
TYPES.forEach((t) => TYPES_BY_NAME[t.name] = t);

export {
  TYPES,
  TYPES_BY_NAME
};