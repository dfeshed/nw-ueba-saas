export const isOSWindows = function(value = '') {
  return value.toLowerCase() === 'windows';
};

export const isModeAdvance = function(value = '') {
  return value.toLowerCase() === 'advanced';
};

export const isAgentVersionAdvanced = function(version = '') {
  const agentVersion = version.trim().split('.');
  return (agentVersion.length >= 2) ? (agentVersion[0] == 11 && agentVersion[1] >= 4) || (agentVersion[0] > 11) : false;
};