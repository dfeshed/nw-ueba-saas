import { camelize } from '@ember/string';

const META_MAP = {
  'OS': 'operating_system',
  'device.type': 'device_type',
  'alias.host': 'hostname',
  'user.src': 'user_account',
  'filename.src': 'source_filename',
  'filename.dst': 'target_filename',
  'directory.src': 'source_path',
  'directory.dst': 'target_path',
  'param.src': 'launch_argument_src',
  'param.dst': 'launch_argument_dst',
  'checksum.src': 'source_hash',
  'checksum.dst': 'target_hash',
  'sessionid': 'id'
};

export const transform = (event) => {
  const newEvent = {};
  if (event) {
    const { metas } = event;
    if (!metas) {
      return;
    }
    const len = (metas && metas.length) || 0;
    let i;
    for (i = 0; i < len; i++) {
      const meta = metas[i];
      const newName = META_MAP[meta[0]];
      const transformedName = newName ? newName : camelize(meta[0]);
      newEvent[transformedName] = meta[1];
    }
  }
  return newEvent;
};