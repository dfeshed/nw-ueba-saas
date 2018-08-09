export default (paramString) => {
  // Example string:
  // " [depth:<uint32>] [options:<string>] [exclude:<string>]"
  paramString = paramString.trim();
  if (paramString.length === 0) {
    return [];
  }
  let params = paramString.split(' ');
  // Remove starting and ending square brackets from each parameter
  // Check if they exist first, some don't include them
  // If they're there, the parameter is optional
  params = params.map((param) => {
    let optional = false;
    if (param[0] === '[' && param[param.length - 1] === ']') {
      optional = true;
      param = param.substring(1, param.length - 1);
    }
    const split = param.split(':');
    return {
      name: split[0],
      displayName: split[0],
      type: _parseType(split[1]),
      optional
    };
  });
  return params;
};

const _parseType = (typeString) => {
  switch (typeString) {
    case '<uint32>':
    case '<uint64>':
      return 'number';
    case '<string>':
      return 'text';
    case '<bool>':
      return 'boolean';
    case '<date-time>':
      return 'date-time';
    default:
      return typeString;
  }
};