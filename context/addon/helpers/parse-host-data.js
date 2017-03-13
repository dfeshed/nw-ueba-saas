import { helper } from 'ember-helper';
import set from 'ember-metal/set';

export function parseHostData([machinesData, additionalData]) {
  if (machinesData && additionalData) {
    for (let i = 0; i < machinesData.length; i++) {
      set(machinesData[i], 'total_modules_count', additionalData.total_modules_count);
    }
    return machinesData;
  } else if (machinesData) {
    return machinesData;
  }
}

export default helper(parseHostData);
