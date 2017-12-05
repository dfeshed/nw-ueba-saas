/**
 * Converts to bytes into KB, MB and GB
 * @param bytes
 * @public
 */


export const convertFromBytes = (bytes) => {
  let val = null;
  let unit = null;
  const GB = Math.pow(1024, 3);
  const MB = Math.pow(1024, 2);
  const KB = 1024;
  const values = bytes.map((item) => {
    const { value } = item;
    if (item.value >= GB) {
      val = (value / GB).toFixed(1);
      unit = 'GB';
    } else if (value >= MB) {
      val = (value / MB).toFixed(1);
      unit = 'MB';
    } else if (value >= KB) {
      val = (value / KB).toFixed(1);
      unit = 'KB';
    } else {
      val = value;
      unit = 'bytes';
    }
    return { value: val.toString(), unit };
  });
  return values;
};

/**
 * Converts entered value and unit to bytes
 * @param bytes
 * @public
 */
export const convertToBytes = (unit, values) => {
  const convertedValue = values.map((item) => {
    const { value } = item;
    let val = value;
    switch (unit) {
      case 'KB' :
        val = value * 1024;
        break;
      case 'MB' :
        val = value * Math.pow(1024, 2);
        break;
      case 'GB' :
        val = value * Math.pow(1024, 3);
        break;
    }
    return { value: val };
  });
  return convertedValue;
};