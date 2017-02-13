import Ember from 'ember';
const { Helper: { helper } } = Ember;
const {
  set
} = Ember;
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
